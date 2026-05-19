## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/30-spec-ecs-runtime-foundation/30-spec-ecs-runtime-foundation.md` | Source spec that defines the ECS cluster, logging, IAM-boundary, output, proof, and non-goal requirements this plan must cover. |
| `docs/specs/30-spec-ecs-runtime-foundation/30-tasks-ecs-runtime-foundation.md` | Task-plan artifact that captures execution order, proof artifacts, and junior-developer implementation guidance for this feature. |
| `docs/specs/30-spec-ecs-runtime-foundation/30-audit-ecs-runtime-foundation.md` | Planning-audit artifact that records gate status, repository-standards evidence, and any later remediation decisions. |
| `infra/terraform/app/dev/main.tf` | Main Terraform entry point where the ECS cluster, CloudWatch log group, IAM roles, and managed policy attachment will be defined. |
| `infra/terraform/app/dev/locals.tf` | Shared naming and tagging locals that should gain reviewer-readable ECS runtime resource names consistent with the current stack pattern. |
| `infra/terraform/app/dev/outputs.tf` | Downstream output contract for the cluster, log group, and IAM role ARNs or names that later ECS specs will consume. |
| `infra/terraform/app/dev/README.md` | Operator-facing runtime documentation for ECS cluster purpose, log retention behavior, and execution-role versus task-role boundaries. |
| `infra/terraform/app/dev/backend.hcl.example` | Existing backend contract used when local `terraform init`, `validate`, and `plan` proof artifacts are exercised reproducibly. |
| `infra/terraform/floci/README.md` | Local AWS-resources validation guidance that should document how the ECS runtime contract is exercised before AWS use. |
| `infra/terraform/floci/docker-compose.yml` | Existing compose-managed `floci` environment that the verification workflow will rely on for local Terraform checks. |
| `scripts/verify-public-http-alb-target-group-contract.sh` | Existing repository pattern for Terraform verification workflow scripts that the new ECS runtime verification entry point should mirror where appropriate. |
| `scripts/verify-ecs-runtime-foundation-contract.sh` | Planned repository-owned verification script for validating the ECS runtime contract reproducibly against local tooling and `floci`. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformPublicAlbContractTest.java` | Existing Terraform contract-test pattern showing how infrastructure resource assertions are expressed in focused Java tests. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformPublicAlbVerificationWorkflowTest.java` | Existing verification-workflow test pattern that the ECS runtime workflow test should follow for `floci`, sanitized credentials, and missing-file coverage. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsClusterContractTest.java` | Planned contract test for the minimal ECS cluster resource, naming, tags, and non-goal exclusions such as service resources and Container Insights. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsLogGroupContractTest.java` | Planned contract test for the environment-scoped application log group and explicit `7` day retention. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsIamBoundaryContractTest.java` | Planned contract test for the separate execution and task roles, trust policy, managed execution baseline, and zero app permissions on the task role. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsRuntimeOutputsContractTest.java` | Planned contract test for the lean runtime output surface consumed by later ECS specs. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsRuntimeVerificationWorkflowTest.java` | Planned contract test for the local verification script, `floci` workflow, sanitized credentials, and reproducible proof path. |

### Notes

- Follow strict TDD during implementation: each task slice begins with a failing Terraform contract test or failing verification-workflow check before Terraform, documentation, or script changes.
- Use `terraform -chdir=infra/terraform/app/dev validate` and sanitized `terraform plan -no-color` output as the main infrastructure proof path, then run `./mvnw test` before completion when Java contract tests are added or changed.
- Keep proof artifacts sanitized: use placeholder credentials such as `AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and `AWS_EC2_METADATA_DISABLED=true`; avoid live AWS account identifiers, tokens, and raw Terraform state output.
- Reuse the existing `infra/terraform/app/dev` naming, tagging, and output conventions so later ECS task-definition and service specs can consume the runtime contract without reconstructing resource names.
- Keep non-goals intact: this plan does not add ECS services, task definition internals, autoscaling, Container Insights, ECS Exec, secrets injection, GitHub workflow IAM, or broader observability pipelines.

## Tasks

### [x] 1.0 Define the shared ECS cluster contract in the dev app stack

#### 1.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf` and `infra/terraform/app/dev/locals.tf` demonstrate one ECS cluster exists with repository-consistent naming and common tags and without service-level runtime configuration
- CLI: `terraform -chdir=infra/terraform/app/dev validate` exits `0` and demonstrates the ECS cluster contract is syntactically valid before live AWS use
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows one ECS cluster created in the `dev` app stack
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsClusterContractTest.java` passes and demonstrates the stack defines one minimal ECS Fargate-compatible cluster without capacity-provider strategy, ECS Exec, Container Insights, or service resources

#### 1.0 Tasks

- [x] 1.1 Add a failing contract test that asserts the dev app stack defines exactly one ECS cluster and keeps the cluster compatible with the repo’s existing ECS Fargate direction.
- [x] 1.2 Extend the failing contract test coverage to assert the cluster uses repository-consistent naming and common tags and does not silently add service resources, capacity-provider strategy, ECS Exec, or Container Insights in v1.
- [x] 1.3 Add the minimum Terraform ECS cluster resource and supporting locals needed to define the shared runtime cluster without reopening service-level design decisions.
- [x] 1.4 Capture `terraform validate` and sanitized `terraform plan -no-color` proof output showing one ECS cluster with the expected minimal runtime contract.

### [x] 2.0 Define the application CloudWatch log-group contract

#### 2.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf`, `infra/terraform/app/dev/locals.tf`, and `infra/terraform/app/dev/README.md` demonstrate one environment-scoped application log group with explicit `7` day retention and reviewer-readable runtime documentation
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows the application log group created with explicit retention rather than indefinite default retention
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsLogGroupContractTest.java` passes and demonstrates the app stack defines one explicit CloudWatch log group for ECS task logs with `7` day retention

#### 2.0 Tasks

- [x] 2.1 Add a failing contract test that asserts the app stack defines one application CloudWatch log group for ECS task logs with explicit `7` day retention.
- [x] 2.2 Extend the failing contract test coverage to assert the log group name is environment-scoped and reviewer-readable rather than left implicit.
- [x] 2.3 Add the minimum Terraform log-group resource and supporting locals needed to make the logging destination and retention behavior explicit.
- [x] 2.4 Update the app-stack documentation with a short runtime-logging summary that explains the explicit `7` day retention choice and states that CloudWatch indefinite retention is intentionally overridden in this dev POC.
- [x] 2.5 Capture sanitized `terraform plan -no-color` proof output showing the application log group and explicit retention behavior.

### [x] 3.0 Define IAM role boundaries and lean ECS runtime outputs

#### 3.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf`, `infra/terraform/app/dev/outputs.tf`, and `infra/terraform/app/dev/README.md` demonstrate separate ECS task execution and task roles, the managed execution-policy baseline, zero app permissions on the task role, and the exact lean output contract
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows both IAM roles plus downstream outputs `ecs_cluster_arn`, `ecs_cluster_name`, `application_log_group_name`, `ecs_task_execution_role_arn`, and `ecs_task_role_arn`
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsIamBoundaryContractTest.java` passes and demonstrates the execution role and task role stay distinct
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsRuntimeOutputsContractTest.java` passes and demonstrates the runtime output surface remains lean and reviewer-readable

#### 3.0 Tasks

- [x] 3.1 Add a failing IAM-boundary contract test that asserts the app stack defines a separate execution role and task role with `ecs-tasks.amazonaws.com` trust relationships.
- [x] 3.2 Extend the failing IAM-boundary test coverage to assert the execution role uses the AWS-managed `AmazonECSTaskExecutionRolePolicy` baseline and that the task role carries no application-specific AWS permissions in v1.
- [x] 3.3 Add a failing outputs contract test that asserts the app stack exports exactly `ecs_cluster_arn`, `ecs_cluster_name`, `application_log_group_name`, `ecs_task_execution_role_arn`, and `ecs_task_role_arn`.
- [x] 3.4 Add the minimum Terraform IAM roles, managed policy attachment, and outputs needed to make the role boundary and lean runtime output contract explicit without adding task-definition or service behavior.
- [x] 3.5 Update the app-stack documentation so it clearly explains why the task role exists even though it has no app permissions in v1 and why execution-role responsibilities must remain separate.
- [x] 3.6 Capture sanitized `terraform plan -no-color` proof output showing both roles and the exact downstream output contract.

### [x] 4.0 Add reproducible local verification for the ECS runtime contract

#### 4.0 Proof Artifact(s)

- File: `scripts/verify-ecs-runtime-foundation-contract.sh` demonstrates a repository-owned verification entry point that initializes the dev stack, validates Terraform, and runs a sanitized local planning workflow against `floci`
- File: `infra/terraform/floci/README.md` demonstrates the exact local command and placeholder-credential expectations for exercising the ECS runtime contract before AWS deployment
- CLI: `./scripts/verify-ecs-runtime-foundation-contract.sh` exits `0` and demonstrates the ECS runtime contract can be checked reproducibly with sanitized local credentials and clear missing-file failures
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsRuntimeVerificationWorkflowTest.java` passes and demonstrates the verification script covers `floci`, backend init, `terraform validate`, sanitized `terraform plan -no-color`, and missing-file failures

#### 4.0 Tasks

- [x] 4.1 Add a failing workflow contract test that asserts one repository-owned verification workflow covers `floci`, Terraform init and validate and plan ordering, sanitized placeholder credentials, and clear missing-file failures for the ECS runtime contract.
- [x] 4.2 Create `scripts/verify-ecs-runtime-foundation-contract.sh` so it mirrors the repository’s existing verification-script pattern, reuses the backend contract, and validates the ECS runtime resources reproducibly before AWS use.
- [x] 4.3 Update the most appropriate operator-facing README with the exact local verification sequence, including `floci`, `terraform validate`, sanitized `terraform plan -no-color`, and placeholder-credential expectations for the ECS runtime contract.
- [x] 4.4 Capture the `./scripts/verify-ecs-runtime-foundation-contract.sh` proof path so reviewers can reproduce the local validation flow and confirm the artifacts remain observable, reproducible, scope-linked, and sanitized.
