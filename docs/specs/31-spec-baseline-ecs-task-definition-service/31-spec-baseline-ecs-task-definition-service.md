# 31-spec-baseline-ecs-task-definition-service.md

## Introduction/Overview

This feature defines the baseline Amazon ECS task definition and ECS service that run the Spring Boot application behind the existing public Application Load Balancer in the dev AWS proof of concept. The primary goal is to turn the already-defined infrastructure contracts for container image source, networking, logging, IAM, and load balancing into the first live end-to-end workload slice that can actually serve the application from AWS.

## Goals

- Define one baseline Fargate task definition for the Spring Boot application using the existing ECR, logging, and IAM contracts.
- Define one baseline ECS service that runs a single application task in the existing private subnets and registers it with the existing ALB target group.
- Make the bootstrap image-reference strategy explicit so the first live service can be created without inventing ad hoc manual steps.
- Define explicit startup-health and single-task deployment behavior so reviewers can understand the expected downtime and warm-up tradeoffs for the dev H2 proof of concept.
- Publish the minimum task-definition and service outputs needed for later CI-driven rollout work without letting Terraform own ongoing revision churn.

## User Stories

- **As a platform engineer**, I want one documented ECS task and service contract so that the application can run in AWS without re-deciding image, network, logging, and ALB wiring details.
- **As a reviewer**, I want the bootstrap image strategy, startup grace period, and single-task deployment behavior written down explicitly so that I can evaluate the first live workload slice without reverse-engineering Terraform.
- **As an operator**, I want the application to run only in private subnets and be reachable only through the ALB so that the dev proof of concept keeps the same ingress boundary defined by earlier specs.
- **As a future CI author**, I want the baseline service and task-definition identifiers exposed clearly so that later rollout automation can update deployments without redefining the entire service contract.

## Demoable Units of Work

### Unit 1: Baseline Fargate Task Definition Contract

**Purpose:** Define the application runtime contract that tells ECS how to run the Spring Boot container in the dev environment.

**Functional Requirements:**

- The system shall define one ECS task definition in the existing `infra/terraform/app/dev` stack.
- The system shall register the task definition for AWS Fargate and use `awsvpc` network mode.
- The system shall define one essential application container that uses the existing Spring Boot container port contract of `8080`.
- The system shall set task-level CPU and memory to `1024` CPU units (`1 vCPU`) and `2048` MiB (`2 GB`).
- The system shall configure the container to send logs to the existing CloudWatch log group by using the `awslogs` log driver with an explicit stream-prefix contract.
- The system shall attach the existing ECS task execution role and existing ECS task role from the runtime-foundation spec.
- The system shall accept one bootstrap image reference input that points to an immutable Git SHA image in the existing ECR repository.
- The system shall not use a placeholder image URI, mutable convenience tag, or console-only image override in v1.
- The system shall keep the baseline environment-variable contract limited to the minimum non-secret configuration required to run the app in the dev proof of concept.

**Proof Artifacts:**

- `File:` Terraform task-definition resource demonstrates Fargate compatibility, task sizing, port `8080`, role wiring, and CloudWatch log configuration.
- `CLI:` sanitized `terraform plan -no-color` output demonstrates one task definition is created with the expected runtime contract.
- `AWS CLI:` sanitized `aws ecs describe-task-definition` output demonstrates the registered task definition uses the expected image reference, CPU/memory, port mapping, and log-driver settings.

### Unit 2: Baseline ECS Service and ALB Attachment Contract

**Purpose:** Define the long-running ECS service that places the application task in the existing network layout and exposes it through the existing ALB target group.

**Functional Requirements:**

- The system shall define one ECS service in the existing shared ECS cluster.
- The system shall use the rolling `ECS` deployment type with `desired_count = 1`.
- The system shall place service tasks only in the existing private subnets.
- The system shall attach the existing ECS task security group to the service tasks.
- The system shall set `assign_public_ip` to disabled so the application task is not directly internet-reachable.
- The system shall attach the service to the existing application target group and container port `8080`.
- The system shall set `health_check_grace_period_seconds = 120` to reduce false negatives during Spring Boot startup.
- The system shall set explicit deployment percentages that allow brief downtime for single-task replacement rather than requiring overlapping old and new tasks.
- The system shall document explicitly that the single-task H2 proof of concept accepts short deployment interruption during replacement.
- The system shall publish lean Terraform outputs for the ECS service identity and baseline task-definition identity that later rollout automation can consume directly.

**Proof Artifacts:**

- `File:` Terraform ECS service resource demonstrates cluster attachment, private-subnet placement, no public IP, target-group wiring, health grace period, and explicit single-task deployment settings.
- `CLI:` sanitized `terraform plan -no-color` output demonstrates one ECS service is created with the expected network and ALB-attachment contract.
- `AWS CLI:` sanitized `aws ecs describe-services` output demonstrates the service is running in the shared cluster with `desiredCount=1`, the configured grace period, and target-group attachment.

### Unit 3: Live End-to-End Runtime Verification Contract

**Purpose:** Prove that the first AWS-hosted application slice actually runs, becomes healthy, and is reachable through the approved ingress path.

**Functional Requirements:**

- The system shall require that a real immutable Git SHA image is pushed to the existing ECR repository before the first ECS service deployment is attempted.
- The system shall require proof that the ECS service reaches a stable state with one running task.
- The system shall require proof that the ALB target group reports the task as healthy by using the existing `/actuator/health` endpoint contract.
- The system shall require proof that the application is reachable through the ALB DNS name.
- The system shall require proof that the application is not exposed through a direct public IP path from the ECS task.
- The system shall require proof that application container logs are written to the existing CloudWatch log group.
- The system shall keep proof artifacts sanitized and shall not commit live AWS credentials, tokens, or secrets.

**Proof Artifacts:**

- `AWS CLI:` sanitized `aws ecs describe-services` and `aws ecs list-tasks` output demonstrates the service reaches steady state with one running task.
- `AWS CLI:` sanitized `aws elbv2 describe-target-health` output demonstrates the target becomes healthy behind the ALB.
- `URL:` ALB DNS name response captured from the deployed environment demonstrates the application is reachable through the approved public entrypoint.
- `AWS CLI:` sanitized CloudWatch log retrieval output demonstrates the running container writes application logs to the expected log group and stream prefix.

## Non-Goals (Out of Scope)

1. **CI-driven rollout automation**: This spec does not define GitHub Actions or other CI logic for registering later task-definition revisions or updating the ECS service automatically.
2. **Autoscaling and high availability**: This spec does not add service autoscaling, multi-task steady state, multi-environment rollout behavior, or zero-downtime deployment guarantees.
3. **Secrets-management expansion**: This spec does not add AWS Secrets Manager, SSM Parameter Store, or application-specific secret injection.
4. **Database modernization**: This spec does not migrate the application away from H2 or make the service safe for horizontal scaling with shared persistent state.
5. **Ingress hardening beyond current baseline**: This spec does not add HTTPS, custom DNS, WAF, or advanced ALB routing features.

## Design Considerations

No specific design requirements identified.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing contract or verification tests first, minimum Terraform and documentation changes second, refactor third.
- Extend the existing `infra/terraform/app/dev` stack rather than creating a new Terraform stack or duplicating outputs already published by specs `28`, `29`, and `30`.
- Preserve the repository's AWS infrastructure contract-test pattern in `src/test/java/org/springframework/samples/petclinic/system/`, paired with repo-owned verification scripts and reviewer-friendly proof artifacts.
- Reuse the existing environment-scoped naming, tagging, and output conventions already established in `infra/terraform/app/dev/locals.tf` and `infra/terraform/app/dev/outputs.tf`.
- Preserve the spec-driven workflow under `docs/specs/` and maintain conventional commit expectations from [AGENTS.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/AGENTS.md) and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).

## Technical Considerations

- Current repository context already defines the production container contract in spec `24`, ALB-only access in spec `27`, ECR repository behavior in spec `28`, ALB and target-group behavior in spec `29`, and ECS cluster/logging/IAM foundations in spec `30`. This spec should consume those contracts directly rather than redefining them.
- Current official Amazon ECS Fargate guidance states that Fargate tasks require task-level CPU and memory settings and always use `awsvpc` networking. This spec therefore fixes the baseline runtime contract to one valid Fargate size, `1 vCPU / 2 GB`, and private-subnet `awsvpc` placement.
- Current ECS guidance for Fargate networking states that private-subnet tasks need outbound internet routing such as a NAT gateway to pull container images when no VPC endpoint is used. Because the current dev network stack already provides NAT-backed private egress, this spec should use private subnets with no public IP rather than reopening networking design.
- Current ECS guidance states that services attached to a load balancer can use `healthCheckGracePeriodSeconds` to ignore early unhealthy Elastic Load Balancing checks during application startup. This spec therefore makes a `120` second grace period explicit for the first Spring Boot deployment.
- Current ECS rolling-deployment guidance states that `minimumHealthyPercent` and `maximumPercent` must allow the scheduler to stop or start at least one task during deployment. Because this proof of concept intentionally runs a single task backed by H2, the service deployment settings should explicitly allow replacement with brief downtime instead of pretending zero-downtime behavior is available.
- Current ECS guidance states that ECS resolves task-definition image tags to digests during service deployment. This spec should still require an immutable Git SHA image reference as the task-definition input so the initial service bootstrap remains deterministic and aligned with the repository's ECR contract.
- Current ECS Fargate logging guidance supports the `awslogs` log driver for shipping container logs to CloudWatch Logs. This spec should use the existing application log group rather than introducing a second log destination.
- Current Terraform AWS provider guidance supports first-class ECS task-definition and ECS service resources with native load-balancer, deployment, and network-configuration fields. The implementation should express this contract directly in Terraform resources and outputs rather than relying on console-side setup.
- Latest-standards research for this spec relied on current official AWS and Terraform living documentation for ECS Fargate task definitions, ECS service parameters, ECS rolling deployments, and Terraform ECS service resources.

## Security Considerations

- The ECS service shall run only in the existing private subnets and shall not assign public IP addresses to application tasks.
- Public ingress shall continue to terminate at the ALB only; this spec shall not introduce direct inbound internet access to ECS tasks.
- The task definition shall continue using the separate execution role and task role established by the runtime-foundation spec so infrastructure-only permissions are not blended into application runtime identity.
- The baseline environment contract shall avoid introducing secrets-management dependencies in v1 and shall not require secrets to be committed into Terraform source, proof artifacts, or repository documentation.
- Proof artifacts shall be sanitized and shall not expose live AWS credentials, ECR authorization tokens, CloudWatch log secrets, or unnecessary infrastructure-sensitive identifiers.
- The spec shall call out explicitly that the single-task H2 deployment model is a dev-only proof of concept and not a production-availability posture.

## Success Metrics

1. **Runnable workload**: A reviewer can verify from live AWS evidence that the ECS service reaches steady state with one running application task behind the shared cluster.
2. **Ingress boundary integrity**: A reviewer can verify that the ALB target becomes healthy and that the application is reachable through the ALB DNS name rather than a direct task-public path.
3. **Contract clarity**: A junior developer can identify the required image reference, task sizing, network placement, logging destination, and service outputs without inferring missing behavior from raw Terraform.
4. **Scope discipline**: The spec keeps CI rollout automation, autoscaling, HTTPS, and persistent-database redesign out of scope while still defining one complete end-to-end AWS workload slice.

## Open Questions

1. Future rollout hardening may decide whether deployment circuit breaker settings should become part of the service contract, but that choice does not block this baseline spec.
