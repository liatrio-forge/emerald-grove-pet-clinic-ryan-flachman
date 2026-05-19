# 30-spec-ecs-runtime-foundation.md

## Introduction/Overview

This feature defines the shared Amazon ECS runtime foundation for the dev AWS proof of concept before any application service is deployed. The primary goal is to make the ECS cluster, CloudWatch log destination, and IAM role boundaries explicit so later ECS service and rollout work can attach to a stable runtime contract instead of re-deciding core platform assumptions.

## Goals

- Define one ECS cluster contract for the `infra/terraform/app/dev` stack that later ECS service work can consume directly
- Define one explicit CloudWatch log-group contract for application task logs with bounded dev-friendly retention
- Define separate IAM execution-role and task-role responsibilities before task definitions and services are introduced
- Publish a lean set of downstream outputs so later ECS specs can consume stable identifiers without reconstructing names
- Keep the scope intentionally narrow so service rollout, scaling, and broader observability decisions remain follow-on work

## User Stories

- **As a platform engineer**, I want one documented ECS runtime foundation so that later service specs can focus on application attachment instead of re-deciding cluster, logging, and IAM basics.
- **As a reviewer**, I want execution-role and task-role boundaries written down explicitly so that I can verify least-privilege intent without reverse-engineering Terraform.
- **As an operator**, I want application task logs sent to one named CloudWatch destination with explicit retention so that debugging behavior and log costs are predictable in the dev POC.
- **As a future spec author**, I want stable ECS runtime outputs so that task-definition, service, and deployment specs can reuse the same identifiers without guessing.

## Demoable Units of Work

### Unit 1: ECS Cluster Contract

**Purpose:** Define the shared ECS cluster that later Fargate services will run in without expanding this spec into service-level behavior.

**Functional Requirements:**

- The system shall define one Amazon ECS cluster in the existing `infra/terraform/app/dev` stack.
- The system shall keep the cluster contract compatible with the repository's existing ECS Fargate direction and shall not introduce EC2 capacity assumptions.
- The system shall apply the stack's existing environment-scoped naming and common-tag conventions to the cluster resource.
- The system shall keep the initial cluster scope minimal and shall not define service resources, capacity-provider strategy, ECS Exec, or Container Insights in v1.
- The system shall expose the ECS cluster name and ARN as downstream Terraform outputs for later ECS service and deployment specs.

**Proof Artifacts:**

- `File:` Terraform ECS cluster resource definition demonstrates one shared cluster exists with the expected naming and tagging contract
- `CLI:` sanitized `terraform plan -no-color` output demonstrates one ECS cluster is created in the `dev` app stack
- `Documentation:` a short runtime-contract summary demonstrates the cluster exists as shared platform foundation rather than service rollout logic

### Unit 2: CloudWatch Log Destination Contract

**Purpose:** Define the application log destination that ECS tasks will use so later task-definition work can attach to a stable, reviewable logging contract.

**Functional Requirements:**

- The system shall define one Amazon CloudWatch log group for application task logs in the `dev` app stack.
- The system shall use an explicit, environment-scoped log-group name that later ECS task-definition work can reference directly.
- The system shall configure the log group with `7` day retention for the dev proof of concept.
- The system shall document that CloudWatch Logs keeps data indefinitely by default and that this spec intentionally overrides that default with explicit retention.
- The system shall expose the application log-group name as a downstream Terraform output for later ECS task-definition wiring.

**Proof Artifacts:**

- `File:` Terraform CloudWatch log-group resource definition demonstrates explicit naming and `7` day retention
- `CLI:` sanitized `terraform plan -no-color` output demonstrates the log group is created as part of the ECS runtime foundation
- `Documentation:` runtime-contract documentation demonstrates the chosen log destination and retention behavior are explicit for reviewers

### Unit 3: IAM Execution and Task Role Boundary Contract

**Purpose:** Define the distinct IAM roles that later ECS task definitions will attach to so platform permissions and application permissions are not blended together.

**Functional Requirements:**

- The system shall define one ECS task execution role with an `ecs-tasks.amazonaws.com` trust relationship.
- The system shall attach only the minimal execution-role baseline required for the dev POC's stated runtime needs: pulling images from Amazon ECR and publishing logs to CloudWatch Logs.
- The system shall use the AWS-managed `AmazonECSTaskExecutionRolePolicy` as the v1 execution-role baseline instead of inventing a broader custom policy.
- The system shall define one separate ECS task role with an `ecs-tasks.amazonaws.com` trust relationship.
- The system shall create the task role without application-specific AWS permissions in v1.
- The system shall document that the task role exists to preserve clean role boundaries for future application AWS access and shall not be used to satisfy execution-role responsibilities.
- The system shall expose the execution-role ARN and task-role ARN as downstream Terraform outputs for later ECS task-definition and service specs.

**Proof Artifacts:**

- `File:` Terraform IAM role and policy-attachment definitions demonstrate separate execution and task roles with distinct responsibilities
- `CLI:` sanitized `terraform plan -no-color` output demonstrates both roles are created and the execution baseline is attached
- `Documentation:` a reviewer-facing role-boundary summary demonstrates why the task role is present even though it carries no app permissions in v1

### Unit 4: Local Verification Workflow Contract

**Purpose:** Preserve the repository's existing infrastructure-validation workflow so the ECS runtime contract can be reviewed locally before live AWS use.

**Functional Requirements:**

- The system shall define one repository-owned verification entry point for the ECS runtime contract.
- The system shall use the existing `floci` local AWS-resources workflow before any live AWS validation.
- The system shall initialize the `infra/terraform/app/dev` stack with `backend.hcl.example`, run `terraform validate`, and run sanitized `terraform plan -no-color`.
- The system shall use placeholder local credentials such as `AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and `AWS_EC2_METADATA_DISABLED=true` throughout local verification.
- The system shall fail clearly when required Terraform, documentation, or script files are missing.

**Proof Artifacts:**

- `File:` repository-owned verification script demonstrates the reproducible local validation workflow for the ECS runtime contract
- `Test:` workflow contract test passes and demonstrates the verification script covers `floci`, backend init, sanitized Terraform checks, and missing-file failures
- `Documentation:` `infra/terraform/floci/README.md` demonstrates the exact local command for exercising the ECS runtime contract before AWS deployment

## Non-Goals (Out of Scope)

1. [**ECS service rollout**: This spec does not define ECS services, desired counts, deployment controllers, service-linked networking attachments, or startup grace behavior.]
2. [**Task definition internals**: This spec does not define container definitions, CPU or memory sizing, image tag selection, environment variables, secrets injection, or log-stream naming inside a task definition.]
3. [**Expanded observability and scaling**: This spec does not define Container Insights, alarms, dashboards, autoscaling policies, log subscription filters, or broader telemetry pipelines.]

## Design Considerations

No specific design requirements identified.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing contract test first, minimum Terraform and documentation changes second, refactor third.
- Keep infrastructure work aligned with the repository's existing Terraform layout under `infra/terraform/`, especially the established `state/dev`, `app/dev`, and `floci` structure used by specs `25`, `27`, `28`, and `29`.
- Preserve the spec-driven workflow under `docs/specs/` and maintain conventional commit expectations from [AGENTS.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/AGENTS.md) and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).
- Any implementation spawned from this spec should follow the repository's existing AWS contract-test pattern in `src/test/java/org/springframework/samples/petclinic/system/`, paired with a repo-owned verification script under `scripts/` and operator guidance in `infra/terraform/floci/README.md`.
- Any implementation should keep outputs, names, and documentation readable enough for a junior reviewer to map Terraform resources to the documented runtime contract.

## Technical Considerations

- Current repository context already defines the dev-only Terraform backend bootstrap in [25-spec-bootstrap-terraform-remote-state-dev-environment.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/25-spec-bootstrap-terraform-remote-state-dev-environment/25-spec-bootstrap-terraform-remote-state-dev-environment.md), the ECR repository contract in [28-spec-ecr-repository-contract.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/28-spec-ecr-repository-contract/28-spec-ecr-repository-contract.md), the ALB-only access contract in [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md), and the public HTTP ALB contract in [29-spec-public-http-alb-target-group.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/29-spec-public-http-alb-target-group/29-spec-public-http-alb-target-group.md). This spec should extend the existing `infra/terraform/app/dev` stack rather than creating a new stack.
- Latest official Amazon ECS guidance states that the task execution role allows the ECS and Fargate agents to make AWS API calls on the task's behalf for actions such as pulling from private ECR and sending logs to CloudWatch Logs, while those permissions are not directly exposed to the application containers. This spec should therefore keep execution permissions on the execution role rather than the task role.
- Latest official Amazon ECS guidance also states that the task role is the role used by application code inside the container when it needs to call AWS services. Because the current application does not yet require direct AWS API access, this spec should create a distinct task role with no app permissions in v1 and defer application-specific IAM policy decisions to later specs.
- Latest CloudWatch Logs guidance states that log groups retain data indefinitely by default unless retention is set explicitly. This spec should therefore define explicit `7` day retention so the dev POC has bounded debugging history and predictable log-storage cost.
- Latest CloudWatch Logs API guidance lists `7` as a valid retention value for `retentionInDays`, so the chosen retention period is natively supported and does not require a workaround.
- Latest CloudWatch and ECS documentation shows that Container Insights is an optional observability feature layered onto ECS clusters rather than a baseline cluster requirement. This spec should keep the initial cluster scope minimal and defer Container Insights to a later observability-focused spec if needed.
- Current Terraform AWS provider documentation supports first-class resources for `aws_ecs_cluster`, `aws_cloudwatch_log_group`, and IAM role attachments. The implementation should express this contract directly in Terraform resources and outputs rather than relying on implicit console-side setup.
- The downstream output surface should remain intentionally lean: `ecs_cluster_arn`, `ecs_cluster_name`, `application_log_group_name`, `ecs_task_execution_role_arn`, and `ecs_task_role_arn`.
- Local infrastructure validation should continue to use `floci` before AWS deployment so the ECS runtime contract is reviewable without requiring live AWS credentials in the first feedback loop.

## Security Considerations

- The execution role and task role shall remain separate so application containers do not inherit infrastructure-only permissions intended for image pulls and log publishing.
- The execution role shall use the minimal AWS-managed baseline required for the documented dev use case and shall not accumulate unrelated permissions such as Secrets Manager, SSM, or application-service access unless a later spec requires them.
- The task role shall begin with no application AWS permissions in v1, which keeps the initial runtime posture closer to least privilege while preserving a clean expansion point for future app integrations.
- Proof artifacts shall not expose live AWS credentials, authorization tokens, account secrets, or unnecessary infrastructure identifiers.
- Local verification artifacts shall continue using sanitized placeholder credentials only.

## Success Metrics

1. [**Runtime clarity**: A junior developer can identify one ECS cluster, one application log destination, and two distinct IAM roles by reading the spec and related Terraform outputs.]
2. [**Boundary clarity**: A reviewer can verify from Terraform source, plan output, and documentation that execution-role permissions are separated from application task-role permissions.]
3. [**Downstream readiness**: A later ECS task-definition or service spec can consume the published runtime outputs without reconstructing names or reopening the cluster, logging, or IAM-boundary decisions.]

## Open Questions

No open questions at this time.
