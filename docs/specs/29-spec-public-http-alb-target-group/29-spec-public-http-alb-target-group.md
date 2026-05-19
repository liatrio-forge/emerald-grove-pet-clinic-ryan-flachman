# 29-spec-public-http-alb-target-group.md

## Introduction/Overview

This feature defines the public HTTP load-balancing contract for the dev AWS proof of concept by adding an internet-facing Application Load Balancer, one HTTP listener, and one ECS-ready target group. The primary goal is to make the public entrypoint identifier, target health model, and downstream outputs explicit so later ECS service work can attach to a stable ALB contract instead of choosing these details ad hoc. This spec defines a Terraform contract that is intended to be reviewable at source and plan time; live deployment verification is deferred to later validation work.

## Goals

- Define one internet-facing ALB in the existing dev public subnets
- Define one HTTP listener on port `80` with an explicit default forward action
- Define one ECS-compatible target group with a documented health-check model
- Publish the ALB and target-group outputs that later ECS and infrastructure specs can consume without reconstructing names
- Keep v1 intentionally limited to HTTP and make HTTPS-related work explicitly out of scope

## User Stories

- **As a platform engineer**, I want one documented public ALB contract so that ECS service work can attach the application to a stable internet-facing entrypoint.
- **As an operator**, I want the target-group health checks aligned with the deployed Spring Boot health endpoint so that unhealthy tasks do not receive traffic.
- **As a reviewer**, I want the ALB listener and forwarding behavior written down explicitly so that I can verify the public ingress and forwarding contract without reverse-engineering Terraform resources.
- **As a future spec author**, I want HTTP-only scope made explicit so that later HTTPS and DNS work can build on this contract without silently changing v1 behavior.

## Demoable Units of Work

### Unit 1: Public ALB Resource Contract

**Purpose:** Define the public load balancer resource that receives internet HTTP traffic in the existing dev VPC layout.

**Functional Requirements:**

- The system shall define one internet-facing Application Load Balancer in the `infra/terraform/app/dev` stack.
- The system shall attach the ALB to the existing public subnets exported by the dev network stack rather than creating new subnets.
- The system shall attach the existing ALB security group exported by the app stack to the load balancer.
- The system shall use the existing repository naming and tagging conventions for the ALB resource.
- The system shall keep the ALB IPv4-reachable from the public internet over port `80` in v1.

**Proof Artifacts:**

- `File:` Terraform ALB resource definition demonstrates the load balancer is internet-facing and uses the exported public subnets and ALB security group
- `CLI:` sanitized `terraform plan -no-color` output demonstrates one ALB is created in the dev app stack with public-subnet placement and the expected internet-facing contract
- `Documentation:` a short public-entrypoint summary demonstrates that the ALB is the approved public endpoint identifier for v1 infrastructure wiring

### Unit 2: HTTP Listener and Target Group Health Contract

**Purpose:** Define how public HTTP traffic reaches the application and how target health is evaluated before traffic is sent.

**Functional Requirements:**

- The system shall define one ALB HTTP listener on port `80`.
- The system shall configure the HTTP listener with a default forward action to one application target group.
- The system shall define the target group with target type `ip` so it is compatible with ECS `awsvpc` task networking.
- The system shall use the existing deployed application port contract of `8080` for target-group traffic.
- The system shall use HTTP health checks on `traffic-port` with path `/actuator/health`.
- The system shall use matcher `200-299` for target-group health-check success codes.
- The system shall set explicit target-group health-check values of interval `15` seconds, timeout `5` seconds, healthy threshold `2`, and unhealthy threshold `3` rather than relying on provider defaults.
- The system shall document the selected health-check thresholds as a target-group routing contract and not treat ECS startup grace behavior as part of this spec.

**Proof Artifacts:**

- `File:` Terraform listener and target-group definitions demonstrate explicit port, path, matcher, interval, timeout, and threshold settings
- `CLI:` sanitized `terraform plan -no-color` output demonstrates the listener forwards to the application target group and the target group health-check configuration is explicit
- `Documentation:` a health-check contract summary demonstrates why `/actuator/health` on port `8080` is the v1 traffic-readiness signal

### Unit 3: Public Endpoint Identifier and Downstream Integration Contract

**Purpose:** Publish the public endpoint identifier and integration details that downstream ECS, validation, and later DNS work need without implying end-to-end application reachability before ECS service attachment exists.

**Functional Requirements:**

- The system shall expose the ALB DNS name as a Terraform output for the approved public endpoint identifier contract.
- The system shall expose the ALB hosted zone ID as a Terraform output for later Route 53 integration without requiring resource-name reconstruction.
- The system shall expose the ALB ARN, HTTP listener ARN, and application target-group ARN as Terraform outputs for downstream ECS and validation work.
- The system shall expose human-readable names for the ALB and application target group when Terraform makes those names available without reconstruction.
- The system shall use concise, reviewer-readable Terraform output names consistent with the repository's existing output naming style, specifically `alb_dns_name`, `alb_hosted_zone_id`, `alb_arn`, `alb_name`, `http_listener_arn`, `application_target_group_arn`, and `application_target_group_name`.
- The system shall document that the ALB DNS name is the approved public endpoint identifier for v1 infrastructure wiring and that end-to-end application reachability depends on later ECS service attachment.

**Proof Artifacts:**

- `File:` Terraform outputs demonstrate the ALB DNS name, readable names, and integration identifiers are exported explicitly
- `CLI:` sanitized `terraform plan -no-color` output demonstrates the output contract and listener-to-target-group wiring are defined at plan time
- `Documentation:` a public endpoint identifier note demonstrates that downstream consumers use the ALB DNS name for v1 infrastructure wiring while end-to-end application reachability remains out of scope for this spec

## Non-Goals (Out of Scope)

1. **HTTPS and certificate management**: This spec does not add HTTPS listeners, ACM certificates, TLS policies, or HTTP-to-HTTPS redirects.
2. **Custom DNS and routing policy**: This spec does not add Route 53 records, vanity domains, or DNS failover policy.
3. **ECS service attachment**: This spec does not create the ECS service, task definition, or target registrations beyond making the target group ready for later attachment.
4. **Advanced ALB features**: This spec does not add WAF, listener rules beyond the default forward action, authentication actions, or stickiness policy tuning.
5. **ALB access logging and edge observability hardening**: This spec does not add ALB access logs or broader edge-observability controls because the first milestone is entrypoint and health-contract clarity rather than internet-edge hardening.

## Design Considerations

- The v1 edge contract is intentionally limited to one internet-facing ALB, one HTTP listener on port `80`, and one default-forward rule to a single application target group.
- The target group shall use `ip` target type and `traffic-port` health checks so the contract stays aligned with ECS `awsvpc` networking and the existing application port contract.
- The target-group health contract is intentionally explicit in v1: `/actuator/health`, matcher `200-299`, interval `15`, timeout `5`, healthy threshold `2`, and unhealthy threshold `3`.
- The output contract shall surface both machine-usable ARNs and human-readable names when available so downstream specs and reviewers do not need to reconstruct resource identities.
- Later HTTPS work must be additive by introducing a new secure listener/domain layer or by explicitly superseding this spec with a revised contract that calls out the change.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing validation first, minimum implementation second, refactor third.
- Keep infrastructure work aligned with the existing Terraform layout under `infra/terraform/app/dev`, including the current single-stack pattern for dev network and app-adjacent AWS resources.
- Reuse existing naming and tagging conventions from [infra/terraform/app/dev/locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:1) and preserve reviewer-readable outputs in [infra/terraform/app/dev/outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:1).
- Keep local infrastructure validation centered on `floci` before any live AWS deployment, consistent with [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md) and [infra/terraform/floci/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/floci/README.md).
- Preserve the spec-driven workflow under `docs/specs/` and maintain conventional commit expectations from [AGENTS.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/AGENTS.md) and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).
- Initial RED-phase contract tests for this spec shall cover the ALB public-subnet attachment, HTTP listener default-forward behavior, target-group port and health-check settings, and the published output contract before Terraform implementation is expanded.

## Technical Considerations

- Current repository context already defines the application port contract as `8080` and the v1 health endpoint as `/actuator/health` in [24-spec-production-container-contract.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/24-spec-production-container-contract/24-spec-production-container-contract.md). This spec should reuse that contract instead of redefining application readiness behavior.
- Current repository context already defines public subnets, private subnets, NAT-backed private egress, and separate ALB and ECS task security groups in the dev app stack and its related specs. This ALB spec should build directly on those exported resources rather than reopening VPC or security-group design.
- Current AWS ECS guidance states that services using `awsvpc` network mode with an Application Load Balancer must use target type `ip`. This spec should therefore make `ip` target type mandatory for the application target group.
- Current AWS ALB guidance recommends explicit HTTP health-check settings, including path, matcher, and threshold values, and notes that health checks are independent requests on the configured interval. This spec therefore fixes the target-group health contract to `/actuator/health` on `traffic-port`, matcher `200-299`, interval `15`, timeout `5`, healthy threshold `2`, and unhealthy threshold `3` rather than inheriting implicit defaults.
- Current AWS ALB guidance also notes that if all registered targets are unhealthy, the load balancer can fail open and continue routing to them. This spec therefore keeps the health-check contract explicit and predictable while accepting that fail-open mitigation belongs to the later ECS service spec rather than this ALB contract.
- Current AWS ALB security-group guidance states that load-balancer security groups must allow traffic for listener ports and any health-check traffic they send to targets. Because the repository already constrains ALB-to-ECS traffic on port `8080`, this spec should keep target traffic and health checks on `traffic-port` rather than introducing a second application port in v1.
- Startup-latency tolerance is a real risk, but current ECS guidance handles initial task warm-up through service-level health-check grace behavior. Because ECS service internals are out of scope here, this spec should define explicit target-group health settings and defer startup grace tuning to the later ECS service spec instead of blending the two concerns.
- Latest-standards research for this spec relied on current official AWS and Terraform living documentation for ALB health checks, ALB security groups, ECS ALB integration, and `aws_lb_target_group`. Those sources support the decisions to require `ip` targets for ECS `awsvpc`, keep health checks on the application traffic port, use a narrow success matcher, and set health-check values explicitly instead of depending on defaults.

## Security Considerations

- Public ingress shall terminate at the ALB only; this spec shall not introduce any direct internet path to ECS tasks.
- The ALB listener shall remain HTTP-only in v1 and the spec shall state clearly that this is a temporary proof-of-concept choice, not an implied production security posture.
- Proof artifacts shall not expose AWS credentials, real account secrets, or unnecessary sensitive infrastructure identifiers.
- The target-group health path shall reuse the minimal deployed actuator surface already defined by the container contract and shall not assume broader actuator exposure.
- Because ALB fail-open behavior can still route traffic when all registered targets are unhealthy, v1 accepts the risk of transient routing to unhealthy or warming targets until a later ECS service spec adds service-level mitigation.
- Later HTTPS, WAF, and domain work shall be treated as additive hardening steps rather than silently implied by this HTTP-only ALB spec.

## Success Metrics

1. **Public entrypoint clarity**: A junior developer can identify one approved public endpoint identifier source for v1 by reading the exported ALB DNS name output.
2. **Health-model clarity**: A reviewer can verify from Terraform plan output that the target group uses `/actuator/health` on port `8080` with explicit matcher, interval, timeout, and threshold settings.
3. **Downstream readiness**: A later ECS service spec can attach tasks to the published target-group and listener contract without reopening ALB shape, subnet placement, or port assumptions.
4. **Scope discipline**: The spec keeps HTTPS, Route 53, ECS service internals, and ALB access logging out of scope while still making the v1 HTTP ingress contract fully reviewable at source and plan time.

## Open Questions

No open questions currently block this spec.
