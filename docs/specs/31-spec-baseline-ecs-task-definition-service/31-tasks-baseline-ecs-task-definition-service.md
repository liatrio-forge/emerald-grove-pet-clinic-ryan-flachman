## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/31-spec-baseline-ecs-task-definition-service/31-spec-baseline-ecs-task-definition-service.md` | Source spec that defines the ECS task-definition, service, bootstrap-image, and live-verification requirements this plan must cover. |
| `docs/specs/31-spec-baseline-ecs-task-definition-service/31-tasks-baseline-ecs-task-definition-service.md` | Task-plan artifact that captures execution order, proof artifacts, and implementation guidance for this feature. |
| `docs/specs/31-spec-baseline-ecs-task-definition-service/31-audit-baseline-ecs-task-definition-service.md` | Planning-audit artifact that records gate status, standards evidence, and any later remediation decisions. |
| `infra/terraform/app/dev/main.tf` | Main Terraform entry point where the ECS task definition, ECS service, and any supporting Terraform resources or data references will be defined. |
| `infra/terraform/app/dev/locals.tf` | Shared naming and tagging locals that should gain reviewer-readable ECS family, service, and log stream-prefix values consistent with the current stack pattern. |
| `infra/terraform/app/dev/outputs.tf` | Downstream output contract for the lean ECS service and task-definition identifiers later rollout work will consume. |
| `infra/terraform/app/dev/variables.tf` | Terraform input surface for the immutable bootstrap image reference and any minimal non-secret runtime configuration needed by the service. |
| `infra/terraform/app/dev/README.md` | Operator-facing documentation for the bootstrap image contract, single-task deployment tradeoff, and runtime verification workflow. |
| `infra/terraform/app/dev/backend.hcl.example` | Existing backend contract used when local Terraform init and verification proof artifacts are exercised reproducibly. |
| `infra/terraform/floci/README.md` | Local AWS-resources validation guidance that should document the ECS service verification path before live AWS use. |
| `scripts/verify-ecs-runtime-foundation-contract.sh` | Existing repository pattern for Terraform verification workflows that the new ECS task-definition and service verification script should mirror where appropriate. |
| `scripts/verify-baseline-ecs-task-definition-service-contract.sh` | Planned repository-owned verification script for validating the baseline ECS workload contract reproducibly. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsTaskDefinitionContractTest.java` | Planned contract test for the baseline Fargate task definition, sizing, port, IAM role wiring, image reference, and log-driver settings. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsServiceContractTest.java` | Planned contract test for the ECS service placement, ALB attachment, grace period, deployment percentages, and public-IP restrictions. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsServiceOutputsContractTest.java` | Planned contract test for the lean ECS service and task-definition output surface consumed by later rollout automation. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformBaselineEcsServiceVerificationWorkflowTest.java` | Planned workflow test for the verification script, bootstrap-image checks, sanitized AWS CLI proof path, and missing-file coverage. |

### Notes

- Follow strict TDD during implementation: each task slice begins with a failing Terraform contract test or failing verification-workflow test before Terraform, script, or documentation changes.
- Use `terraform -chdir=infra/terraform/app/dev validate` and sanitized `terraform plan -no-color` output as the primary local proof path, then gather sanitized AWS CLI evidence only after the infrastructure contract is applied in the target environment.
- Keep proof artifacts sanitized: use placeholder credentials for local verification and redact live account identifiers, tokens, image digests, and log contents that are not needed to prove the requirement.
- Reuse the existing `infra/terraform/app/dev` naming, tagging, ALB, target-group, IAM, log-group, subnet, and security-group outputs instead of duplicating prior-spec contracts.
- Preserve the open-question assumption from the spec: deployment circuit breaker settings remain out of scope for this baseline unless a later spec explicitly adds them.
- Adhere to repository quality gates: Markdown must pass `markdownlint`, shell scripts must pass `shellcheck`, and Java test changes must keep `./mvnw test` green.

## Tasks

### [~] 1.0 Define the baseline ECS task-definition runtime contract

#### 1.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf`, `infra/terraform/app/dev/locals.tf`, and `infra/terraform/app/dev/README.md` demonstrate one Fargate task definition with `awsvpc`, `1024` CPU, `2048` MiB memory, port `8080`, explicit `awslogs` stream prefix, existing execution/task role wiring, and only the minimum non-secret runtime configuration.
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows one ECS task definition created with the expected bootstrap image input and without placeholder or mutable image tags.
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsTaskDefinitionContractTest.java` passes and demonstrates the Terraform contract locks the baseline Fargate sizing, networking mode, image-reference rules, logging destination, and IAM role attachment.
- AWS CLI: sanitized `aws ecs describe-task-definition --task-definition <family-or-arn>` output demonstrates the registered task definition uses the expected image reference, task sizing, port mapping, and `awslogs` configuration.

#### 1.0 Tasks

- [x] 1.1 Add a failing task-definition contract test that asserts the dev app stack defines exactly one ECS task definition for Fargate with `awsvpc`, port `8080`, and task-level CPU and memory settings.
- [x] 1.2 Extend the failing contract test to assert the task definition uses the existing execution role and task role ARNs, the existing application log group, an explicit `awslogs` stream prefix, and only the minimum non-secret runtime configuration needed for the dev proof of concept.
- [x] 1.3 Extend the failing contract test to assert the task definition accepts one immutable bootstrap image-reference input and rejects placeholder, `latest`, or other mutable convenience-tag behavior in the Terraform contract.
- [x] 1.4 Add the minimum Terraform locals and variables needed to make the task family name, log stream prefix, and immutable bootstrap image-reference input explicit and reviewer-readable.
- [x] 1.5 Add the minimum Terraform ECS task-definition resource and container-definition contract needed to satisfy the test without introducing out-of-scope secrets expansion, multi-container behavior, or alternate log destinations.
- [x] 1.6 Update the app-stack README with the baseline task-definition runtime contract, including the bootstrap image requirement, Fargate sizing choice, `8080` container-port contract, and limited environment-variable surface.
- [ ] 1.7 Capture sanitized `terraform plan -no-color` proof output and a matching sanitized `aws ecs describe-task-definition` proof artifact showing the registered runtime contract.

### [~] 2.0 Define the baseline ECS service, ALB attachment, and downstream output contract

#### 2.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf`, `infra/terraform/app/dev/outputs.tf`, and `infra/terraform/app/dev/README.md` demonstrate one ECS service attached to the shared cluster, private subnets, existing ECS task security group, existing application target group, disabled public IP assignment, `desired_count = 1`, explicit health-check grace period, and explicit single-task deployment settings.
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows one ECS service plus lean outputs for the ECS service identity and baseline task-definition identity.
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsServiceContractTest.java` passes and demonstrates the service contract preserves ALB-only ingress, private-subnet placement, grace-period behavior, and documented brief downtime for single-task replacement.
- AWS CLI: sanitized `aws ecs describe-services --cluster <cluster-name> --services <service-name>` output demonstrates the deployed service keeps `desiredCount=1`, the configured grace period, and target-group attachment in the shared cluster.

#### 2.0 Tasks

- [x] 2.1 Add a failing ECS service contract test that asserts the dev app stack defines exactly one ECS service attached to the shared ECS cluster and existing application target group with `desired_count = 1`.
- [x] 2.2 Extend the failing service contract test to assert the service uses only existing private subnets, attaches the existing ECS task security group, and keeps `assign_public_ip` disabled so tasks are not directly internet-reachable.
- [x] 2.3 Extend the failing service contract test to assert `health_check_grace_period_seconds = 120` and explicit deployment percentage settings that permit single-task replacement with brief downtime rather than pretending zero-downtime behavior.
- [x] 2.4 Add a failing outputs contract test that asserts the app stack exports only the lean ECS service and baseline task-definition identifiers needed for later rollout automation, without exposing unnecessary revision-churn ownership.
- [x] 2.5 Add the minimum Terraform ECS service resource and output changes needed to satisfy the tests while reusing the existing cluster, target group, subnets, and security-group contracts from earlier specs.
- [x] 2.6 Update the app-stack README so it explicitly documents the ALB-only ingress path, the single-task H2 deployment downtime tradeoff, and the exact service/task-definition outputs downstream automation should consume.
- [ ] 2.7 Capture sanitized `terraform plan -no-color` proof output and a matching sanitized `aws ecs describe-services` proof artifact showing the live service contract.

### [~] 3.0 Verify bootstrap image readiness and live end-to-end runtime behavior

#### 3.0 Proof Artifact(s)

- File: `scripts/verify-baseline-ecs-task-definition-service-contract.sh`, `infra/terraform/floci/README.md`, and `infra/terraform/app/dev/README.md` demonstrate the reproducible verification path, the immutable Git SHA bootstrap-image requirement, and the sanitized evidence workflow for local and AWS-backed review.
- CLI: `./scripts/verify-baseline-ecs-task-definition-service-contract.sh` exits `0` and demonstrates the repository-owned verification path covers Terraform validation, sanitized planning, and the required bootstrap-image checks before live deployment.
- AWS CLI: sanitized `aws ecs describe-services`, `aws ecs list-tasks`, and `aws elbv2 describe-target-health` output demonstrate the service reaches steady state with one healthy task behind the ALB.
- URL: sanitized ALB DNS response capture demonstrates the application is reachable through the approved public entrypoint rather than a direct task-public path.
- AWS CLI: sanitized `aws logs get-log-events` output demonstrates the running container writes application logs to the expected CloudWatch log group and stream prefix.

#### 3.0 Tasks

- [x] 3.1 Add a failing verification-workflow test that asserts a repository-owned script checks for required files, validates Terraform, enforces the immutable bootstrap image prerequisite, and uses sanitized placeholder credentials where applicable.
- [x] 3.2 Extend the failing workflow test to assert the verification path covers the exact live evidence commands needed for `aws ecs describe-services`, `aws ecs list-tasks`, `aws elbv2 describe-target-health`, ALB reachability, and CloudWatch log retrieval.
- [x] 3.3 Create `scripts/verify-baseline-ecs-task-definition-service-contract.sh` by following the existing verification-script pattern and documenting any intentional distinction between local `floci` checks and AWS-backed runtime evidence collection.
- [x] 3.4 Update the most appropriate README files so operators know the required order: build and push a real immutable Git SHA image, apply Terraform, verify ECS steady state, verify target health, verify ALB reachability, and verify CloudWatch logs.
- [ ] 3.5 Capture sanitized proof artifacts for one running task, healthy ALB target registration, ALB DNS reachability, and CloudWatch log delivery, including a note that no direct task public-IP path exists because the service disables public IP assignment.
- [x] 3.6 Confirm the proof set stays within scope by excluding CI rollout automation, autoscaling, secrets-manager integration, HTTPS hardening, and database redesign from the verification workflow and documentation.
