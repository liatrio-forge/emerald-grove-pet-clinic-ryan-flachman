# 27-spec-alb-only-app-access.md

## Introduction/Overview

This feature defines the security group contract that keeps the Spring Boot application reachable only through the public Application Load Balancer in the `dev` AWS proof of concept. The primary goal is to make the allowed traffic path explicit so later ECS service and ALB wiring can rely on a clear network-access model instead of assuming that private subnets alone provide sufficient protection.

## Goals

- Define separate security group responsibilities for the public ALB and private ECS tasks
- Ensure direct internet traffic is allowed to the ALB only and not to application tasks
- Ensure ECS task ingress is limited to the ALB security group on the application port
- Define the v1 egress stance for both security groups so later infrastructure work does not have to guess
- Keep the contract compatible with the existing container port and dev VPC topology specs

## User Stories

- **As a platform engineer**, I want the ECS service reachable only through the ALB so that the application is not accidentally exposed directly to the internet.
- **As a reviewer**, I want the allowed traffic flows written down explicitly so that I can verify the network design without reverse-engineering Terraform resources.
- **As a future spec author**, I want the security group model settled before ALB listeners and ECS service wiring are added so that later specs can focus on resource integration instead of reopening access-control decisions.
- **As an operator**, I want a simple first-pass egress posture for the dev proof of concept so that the application can function behind private subnets without unexpected outbound breakage.

## Demoable Units of Work

### Unit 1: ALB Security Group Contract

**Purpose:** Define the public entry-point security group that accepts user traffic and forwards only the traffic needed to the application tier.

**Functional Requirements:**

- The system shall define a dedicated security group for the public Application Load Balancer in the `dev` VPC.
- The system shall allow inbound internet traffic to the ALB security group only on the ALB listener port or ports selected by the later ALB-listener implementation.
- The system shall not allow direct inbound internet traffic to the ECS task security group.
- The system shall allow outbound traffic from the ALB security group to the ECS task security group on the Spring Boot application port defined by the production container contract.
- The system shall allow outbound traffic from the ALB security group to the ECS task security group on the target-group health check port when that port matches the application port.

**Proof Artifacts:**

- `File:` Terraform security-group definitions demonstrate the ALB security group exists as a separate resource with internet-facing ingress rules
- `CLI:` `terraform plan -no-color` output demonstrates inbound internet access is attached to the ALB security group and not to the ECS task security group
- `Documentation:` a traffic-flow table demonstrates that public users reach the ALB first and do not target ECS tasks directly

### Unit 2: ECS Task Security Group Contract

**Purpose:** Define the private application-tier security group that accepts application traffic only from the ALB.

**Functional Requirements:**

- The system shall define a dedicated security group for ECS tasks that use the existing private subnets from the `dev` VPC network spec.
- The system shall allow inbound traffic to the ECS task security group only from the ALB security group.
- The system shall restrict ECS task ingress to the Spring Boot application port defined by the production container contract.
- The system shall model the ALB security group as the ingress source by security-group reference rather than by broad CIDR ranges.
- The system shall keep the ECS task security group compatible with `awsvpc` task networking so later ECS service work can attach the group directly to tasks.

**Proof Artifacts:**

- `File:` Terraform security-group rule definitions demonstrate the ECS ingress source is the ALB security group reference
- `CLI:` `terraform plan -no-color` output demonstrates the ECS task security group exposes only the application port and only to the ALB security group
- `Documentation:` a short reviewer-oriented explanation demonstrates why private-subnet placement alone is not treated as sufficient protection

### Unit 3: Egress and Allowed Traffic Flow Contract

**Purpose:** Define the initial outbound posture and the full approved traffic paths for the dev proof of concept.

**Functional Requirements:**

- The system shall allow inbound internet traffic only to the ALB security group and not to ECS tasks.
- The system shall document the approved end-to-end traffic path as `internet client -> ALB -> ECS task on app port`.
- The system shall use default-open egress for the ECS task security group in v1 so private tasks can reach required outbound dependencies through the existing NAT-backed private-subnet design.
- The system shall use egress rules on the ALB security group that permit traffic only to the ECS task security group on the application and health-check port contract for this feature.
- The system shall record tighter ECS egress restrictions and VPC endpoint-based hardening as out-of-scope follow-on work rather than silently assuming them in v1.

**Proof Artifacts:**

- `Documentation:` allowed-traffic matrix demonstrates which source, destination, protocol, and port combinations are approved
- `CLI:` `terraform plan -no-color` output demonstrates the ALB and ECS egress rules match the documented traffic paths
- `Test:` a future local infrastructure validation check against `floci` demonstrates the security-group contract can be exercised without using a live AWS account

## Non-Goals (Out of Scope)

1. **ALB resource creation**: This spec does not create the load balancer, listeners, or listener rules.
2. **ECS service creation**: This spec does not create task definitions, ECS services, or target-group attachments.
3. **Broader network controls**: This spec does not define WAF, IAM, NACL tuning, VPC endpoints, or comprehensive outbound-traffic hardening.

## Design Considerations

No specific design requirements identified.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing test or failing validation first, minimum implementation second, refactor third.
- Keep infrastructure work aligned with the repository's existing Terraform layout under `infra/terraform/`, especially the `state/dev`, `app/dev`, and `floci` structure already established by prior AWS specs.
- Preserve the existing spec-driven workflow under `docs/specs/` and maintain conventional commit requirements from [AGENTS.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/AGENTS.md) and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).
- Any implementation spawned from this spec should keep local infrastructure validation centered on `floci` before live AWS use.
- Any implementation should keep security-group names, tags, and outputs readable enough for a junior reviewer to map them to the documented traffic flows.

## Technical Considerations

- Current repository context already defines the production container contract in [24-spec-production-container-contract.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/24-spec-production-container-contract/24-spec-production-container-contract.md), including a stable application port of `8080`; this spec should reuse that port contract instead of re-deciding it.
- Current repository context also includes an implemented `dev` Terraform network stack with public subnets for ALB-facing resources, private subnets for ECS-facing resources, and NAT-backed outbound access from private subnets. This spec should build directly on that topology rather than redefining subnet strategy.
- Current AWS ALB guidance recommends separate ALB security groups with internet ingress on listener ports and outbound rules that allow communication to targets on listener and health-check ports. This spec should therefore keep the ALB security group separate from the ECS task security group and make target traffic explicit.
- Current AWS VPC guidance recommends using security-group references to limit traffic between tiers in the same VPC. This spec should therefore model ECS ingress from the ALB security group reference instead of using `0.0.0.0/0` or broad subnet CIDRs.
- Current Amazon ECS guidance recommends `awsvpc` networking and task-level security groups for services behind an ALB. This spec should therefore assume later ECS service wiring uses task-attached security groups in private subnets.
- Current Amazon ECS guidance also notes that NAT gateway access is the simplest way for private tasks to reach required AWS services. For this dev proof of concept, ECS egress should remain fully open in v1 to avoid blocking image pulls, telemetry, and future dependency access; this is a deliberate short-term compatibility choice, not the long-term least-privilege target.
- If a later spec introduces HTTPS-only listeners or separate health-check ports, the ALB security group may need additional listener-port entries, but the ECS ingress source should still remain limited to the ALB security group.

## Security Considerations

- The design shall not rely on private-subnet placement as the only protection for application tasks; the ECS task security group shall provide the explicit network boundary.
- Public internet ingress shall terminate at the ALB security group only; no ECS task security-group rule shall use `0.0.0.0/0` or `::/0` as an ingress source.
- Proof artifacts shall not include real AWS account secrets, live credential values, or screenshots that expose sensitive identifiers unnecessarily.
- If later work narrows ECS egress, the implementation shall preserve outbound access needed for container image retrieval, ECS control-plane communication, logging, and any approved application dependencies.

## Success Metrics

1. **Traffic-path clarity**: A junior developer can identify one approved inbound path from the public internet to the application and verify that it passes through the ALB first.
2. **Exposure reduction**: The documented security-group design contains no direct internet-ingress rule for ECS tasks.
3. **Implementation readiness**: A later Terraform implementation can add ALB and ECS security groups without reopening the app-port contract or the public-versus-private traffic model.
4. **Validation readiness**: A reviewer can confirm the contract using Terraform plan output and local `floci`-based infrastructure validation artifacts before AWS deployment.

## Open Questions

1. Should a later ALB-focused spec expose HTTP only for the initial dev POC, or should it introduce HTTPS listener requirements at the same time as listener creation?
2. Should a later hardening spec replace default-open ECS egress with explicitly scoped outbound rules and AWS service endpoints after the first end-to-end deployment path is proven?
