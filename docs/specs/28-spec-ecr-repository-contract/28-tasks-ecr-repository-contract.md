## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/28-spec-ecr-repository-contract/28-spec-ecr-repository-contract.md` | Source spec that defines the ECR repository identity, lifecycle, CI-output, destroy, and proof requirements this plan must cover. |
| `docs/specs/28-spec-ecr-repository-contract/28-questions-1-ecr-repository-contract.md` | Records the resolved planning decisions for Git SHA-only tags, count-based retention, and force-delete destroy behavior. |
| `docs/specs/28-spec-ecr-repository-contract/28-tasks-ecr-repository-contract.md` | Task-plan artifact that captures execution order, proof artifacts, and junior-developer implementation guidance for this feature. |
| `docs/specs/28-spec-ecr-repository-contract/28-audit-ecr-repository-contract.md` | Planning-audit artifact that records gate status, repository-standards evidence, and any later remediation decisions. |
| `infra/terraform/app/dev/main.tf` | Main Terraform entry point where the ECR repository resource, lifecycle policy attachment, and force-delete behavior will be defined. |
| `infra/terraform/app/dev/locals.tf` | Planned shared naming and tagging locals for the environment-scoped repository name and any reviewer-readable ECR labels. |
| `infra/terraform/app/dev/outputs.tf` | Planned downstream outputs for the exact `repository_uri` and `repository_name` CI-consumption contract. |
| `infra/terraform/app/dev/README.md` | Operator-facing documentation for the repository contract, immutable Git SHA tagging, lifecycle preview validation, and destroy consequences. |
| `infra/terraform/app/dev/backend.hcl.example` | Existing backend contract used when local Terraform init, validate, and plan proof artifacts are exercised reproducibly. |
| `infra/terraform/floci/README.md` | Local AWS-resources validation guidance that should document how the ECR repository contract is exercised before AWS use. |
| `infra/terraform/floci/backend.hcl.example` | Existing local endpoint configuration that a repository-owned verification script can reuse for sanitized `floci`-based planning. |
| `scripts/verify-terraform-remote-state-contract.sh` | Existing repository pattern for Terraform verification workflow scripts that the new ECR-contract verification entry point should mirror where appropriate. |
| `scripts/verify-ecr-repository-contract.sh` | Planned repository-owned verification script for validating the ECR repository contract reproducibly against local tooling and `floci`. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryContractTest.java` | Planned contract test for repository identity, naming, tags, and immutable-tag behavior. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcrLifecyclePolicyContractTest.java` | Planned contract test for lifecycle-policy readability, untagged cleanup, and bounded tagged-image retention. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryOutputsAndDestroyContractTest.java` | Planned contract test for CI-facing outputs and explicit repository force-delete semantics. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryVerificationWorkflowTest.java` | Planned contract test for the local verification script, sanitized credentials, and reproducible `floci`-based planning flow. |

### Notes

- Follow strict TDD during implementation: each task slice begins with a failing Terraform contract test or failing verification-workflow check before Terraform, documentation, or script changes.
- Use `terraform -chdir=infra/terraform/app/dev validate` and sanitized `terraform plan -no-color` output as the main infrastructure proof path, then run `./mvnw test` before completion when Java contract tests are added or changed.
- Keep proof artifacts sanitized: use placeholder credentials such as `AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and `AWS_EC2_METADATA_DISABLED=true`; avoid live AWS account identifiers, tokens, and raw Terraform state output.
- Reuse the existing `infra/terraform/app/dev` naming and tagging conventions so downstream ECS and CI work can consume the repository contract without reconstructing resource names.
- Keep non-goals intact: this plan does not add Docker build automation, ECS deployment wiring, cross-account registry policy, vulnerability-scanning strategy, or broader destroy orchestration beyond repository-local semantics.

## Tasks

### [x] 1.0 Define the dev ECR repository identity and immutable tagging contract

#### 1.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf`, `infra/terraform/app/dev/locals.tf`, and `infra/terraform/app/dev/README.md` demonstrate one environment-scoped private ECR repository, explicit common tags, and immutable Git SHA tags as the only approved v1 tagging contract
- CLI: `terraform -chdir=infra/terraform/app/dev validate` exits `0` and demonstrates the ECR repository contract is syntactically valid before live AWS use
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows one deterministic repository with immutable tag mutability in the dev app stack
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryContractTest.java` passes and demonstrates the repository name, privacy, tags, and immutable-tag contract remain explicit and reviewer-readable

#### 1.0 Tasks

- [x] 1.1 Add a failing contract test that asserts the dev app stack defines exactly one private ECR repository, uses an environment-scoped name aligned with current Terraform naming conventions, and enables immutable image tags.
- [x] 1.2 Add the minimum Terraform resources and locals needed to define the ECR repository in `infra/terraform/app/dev` with explicit tag mutability and reviewer-readable naming or tag metadata.
- [x] 1.3 Update the app-stack documentation so it states that immutable Git SHA tags are the only approved push and deploy reference format in v1 and that mutable convenience tags such as `latest` are intentionally excluded.
- [x] 1.4 Capture `terraform validate` and sanitized `terraform plan -no-color` proof output showing one deterministic repository target and explicit immutable-tag behavior.

### [x] 2.0 Define the lifecycle retention contract and previewable cleanup rules

#### 2.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf` or a dedicated lifecycle-policy file demonstrates separate cleanup rules for untagged images and tagged Git SHA images with the approved retain-count of `5`
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows the lifecycle policy is attached to the repository and remains readable in planned infrastructure changes
- Documentation: `infra/terraform/app/dev/README.md` documents the lifecycle-policy preview step and explains how a reviewer verifies tagged-versus-untagged retention behavior before AWS enforcement
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcrLifecyclePolicyContractTest.java` passes and demonstrates the repository uses count-based tagged retention, automatic untagged cleanup, and no mutable convenience-tag assumptions

#### 2.0 Tasks

- [x] 2.1 Add a failing contract test that asserts the repository lifecycle policy expires untagged images automatically, retains the most recent `5` tagged Git SHA images, and uses count-based rather than age-based retention in v1.
- [~] 2.2 Add the minimum Terraform lifecycle-policy definition needed to keep tagged and untagged cleanup rules separate, readable, and attached to the repository resource contract.
- [~] 2.3 Update the repository documentation with the required lifecycle-policy preview validation step and a short reviewer-oriented explanation of how to verify retention behavior before enforcing it in AWS.
- [x] 2.4 Capture sanitized planning proof that the lifecycle policy is attached to the repository and that the tagged-image retention and untagged cleanup rules remain observable and reproducible for review.

### [x] 3.0 Define the CI consumption outputs and destroy-time repository behavior

#### 3.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/outputs.tf` demonstrates the exact downstream contract outputs `repository_uri` and `repository_name` without requiring CI or later ECS specs to reconstruct names manually
- File: `infra/terraform/app/dev/main.tf` and `infra/terraform/app/dev/README.md` demonstrate explicit `force_delete` behavior and the documented dev-only teardown consequence that deleting the repository also deletes all contained images
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows deterministic repository outputs and intentional destroy semantics in the app stack
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryOutputsAndDestroyContractTest.java` passes and demonstrates the CI-facing output names and force-delete semantics stay synchronized with documentation

#### 3.0 Tasks

- [x] 3.1 Add a failing contract test that asserts the app stack exposes exactly `repository_uri` and `repository_name` as reviewer-visible outputs for downstream CI and ECS consumption.
- [~] 3.2 Add or update the Terraform outputs and repository resource settings so the CI-facing contract is deterministic and the repository is configured to allow destroy-time force deletion even when images remain.
- [~] 3.3 Update the app-stack documentation with a short CI-consumption contract that explains how immutable Git SHA tags and `repository_uri` fit into later push and deploy workflows, plus the explicit consequence that repository destroy deletes all contained images.
- [x] 3.4 Capture sanitized `terraform plan -no-color` proof output showing the output contract and force-delete behavior are intentional, explicit, and scoped to the dev-only POC lifecycle.

### [x] 4.0 Add reproducible local verification for the ECR repository contract

#### 4.0 Proof Artifact(s)

- File: `scripts/verify-ecr-repository-contract.sh` demonstrates a repository-owned verification entry point that initializes the dev stack, validates the Terraform contract, and runs a sanitized local planning workflow against `floci`
- File: `infra/terraform/floci/README.md` or `infra/terraform/app/dev/README.md` documents the exact local verification sequence, required placeholder credentials, and the lifecycle-policy preview expectation without relying on live AWS credentials
- CLI: `./scripts/verify-ecr-repository-contract.sh` exits `0` and demonstrates the repository contract can be checked reproducibly with sanitized local credentials and clear missing-file failures
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryVerificationWorkflowTest.java` passes and demonstrates the local verification path covers `floci`, Terraform validation or planning, and reviewer-visible sanitized guidance end to end

#### 4.0 Tasks

- [x] 4.1 Add a failing contract test that asserts one repository-owned verification workflow covers `floci`, Terraform init or validate or plan ordering, sanitized local credentials, and clear missing-file failures for the ECR repository contract.
- [~] 4.2 Create `scripts/verify-ecr-repository-contract.sh` so it reuses the existing backend contract, validates the dev stack reproducibly, and runs a sanitized local planning path suitable for contract verification before AWS use.
- [~] 4.3 Update `infra/terraform/floci/README.md` or the most appropriate operator-facing doc with the exact command sequence for exercising the ECR repository contract locally, including the lifecycle-policy preview expectation and placeholder-credential requirements.
- [x] 4.4 Capture the `./scripts/verify-ecr-repository-contract.sh` proof path so reviewers can reproduce the local validation flow and confirm the artifacts remain observable, reproducible, scope-linked, and sanitized.
