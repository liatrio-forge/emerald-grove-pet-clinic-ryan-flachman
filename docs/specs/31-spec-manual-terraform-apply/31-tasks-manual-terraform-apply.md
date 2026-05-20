## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/31-spec-manual-terraform-apply/31-spec-manual-terraform-apply.md` | Source spec that defines the manual apply workflow contract, safety gates, OIDC requirement, saved-plan rule, and `dev`-only scope this plan must cover. |
| `docs/specs/31-spec-manual-terraform-apply/31-tasks-manual-terraform-apply.md` | Task-plan artifact that captures execution order, proof artifacts, and implementation guidance for this feature. |
| `docs/specs/31-spec-manual-terraform-apply/31-audit-manual-terraform-apply.md` | Planning-audit artifact that records gate status, repository standards evidence, and any later remediation decisions. |
| `.github/workflows/terraform-apply-dev.yml` | Planned GitHub Actions workflow that will define manual dispatch, typed confirmation, OIDC authentication, reviewed plan creation, saved-plan apply, concurrency, and operator-visible failure handling. |
| `README.md` | Root infrastructure guidance already documents `floci`, remote-state verification, and repo-owned workflow entry points that this manual apply workflow documentation must stay consistent with. |
| `infra/terraform/app/dev/README.md` | Operator-facing Terraform stack contract that should document how the workflow targets the existing `infra/terraform/app/dev` stack and reuses the established backend inputs. |
| `infra/terraform/app/dev/backend.hcl.example` | Existing example backend configuration that shows the stack keeps backend details externalized rather than hard-coding environment-specific values in reusable Terraform source. |
| `infra/terraform/app/dev/versions.tf` | Current Terraform and provider version contract that the workflow must respect when running `init`, `plan`, and `apply` in automation mode. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowDispatchContractTest.java` | Planned contract test for manual dispatch, `main`-only scope, typed confirmation, and GitHub environment approval-gate behavior. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowPlanContractTest.java` | Planned contract test for minimal workflow permissions, OIDC authentication, backend-input externalization, and exact saved-plan artifact creation. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowApplyContractTest.java` | Planned contract test for exact saved-plan apply behavior, concurrency protection, and clear failure handling. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowDocumentationContractTest.java` | Planned contract test for operator-facing workflow documentation and evidence guidance. |

### Notes

- Follow strict TDD during implementation: each workflow slice begins with a failing contract test before any workflow YAML or documentation changes.
- Keep the workflow narrowly scoped to the existing `infra/terraform/app/dev` stack and `main` branch; do not fold in application image build, ECS rollout, destroy behavior, or broader CI/CD concerns.
- Reuse the repository’s existing infrastructure verification posture: `floci` remains the first local feedback loop for Terraform contracts, while this workflow adds the separate GitHub Actions apply path for reviewed live `dev` changes.
- Keep proof artifacts sanitized: use screenshots, logs, and artifact listings that avoid secrets, tokens, raw credentials, unnecessary account identifiers, or unredacted backend values.
- Adhere to repository quality gates: Markdown must pass `markdownlint`, workflow YAML must remain valid for `check-yaml`, and the Java contract tests must keep `./mvnw test` green.

## Tasks

### [x] 1.0 Define the manual workflow entrypoint and apply safety gates

#### 1.0 Proof Artifact(s)

- File: `.github/workflows/terraform-apply-dev.yml` demonstrates one `workflow_dispatch` entrypoint scoped to the `dev` apply path, `main` branch enforcement, typed confirmation input, and GitHub environment protection before apply-capable execution.
- Screenshot: GitHub Actions manual-run form for `terraform-apply-dev` shows the operator-facing confirmation input required to start a `dev` apply.
- Screenshot: GitHub environment review gate for the apply job shows the workflow pauses for reviewer approval before protected environment access is granted.
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowDispatchContractTest.java` passes and demonstrates the workflow contract keeps manual invocation, `main`-only scope, typed confirmation, and environment gating intact.

#### 1.0 Tasks

- [x] 1.1 Add a failing workflow-dispatch contract test that asserts one `.github/workflows/terraform-apply-dev.yml` workflow exists and is triggered only by `workflow_dispatch`.
- [x] 1.2 Extend the failing dispatch contract test to assert the workflow enforces `main`-only apply behavior, requires a typed confirmation input that explicitly references the `dev` environment, and binds the apply-capable job to a protected GitHub environment.
- [x] 1.3 Add the minimum workflow skeleton needed to satisfy the dispatch contract, including the workflow name, manual inputs, checkout, and explicit branch guard behavior without introducing out-of-scope automatic triggers.
- [x] 1.4 Add the minimum safety-gate logic needed to fail clearly when the confirmation input is invalid and to ensure reviewer approval is required before any job can access protected environment configuration.
- [x] 1.5 Capture the operator-facing proof artifacts for the workflow-dispatch form and environment approval gate using sanitized screenshots that show the required safety controls without exposing repository secrets.

### [x] 2.0 Define OIDC authentication and reviewed plan creation for the dev stack

#### 2.0 Proof Artifact(s)

- File: `.github/workflows/terraform-apply-dev.yml` demonstrates minimal GitHub Actions permissions, AWS OIDC credential setup, externalized backend/environment inputs, and `terraform init -input=false` plus `terraform plan -out=tfplan -input=false` against `infra/terraform/app/dev`.
- CLI: sanitized GitHub Actions job log excerpt shows Terraform initialization and saved-plan creation succeed for `infra/terraform/app/dev` without repository-stored long-lived AWS keys.
- Artifact: saved workflow artifacts for `tfplan` and the sanitized plan output demonstrate reviewers can inspect the exact plan that will later be applied.
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowPlanContractTest.java` passes and demonstrates the workflow uses OIDC, preserves the reviewed plan artifact, and keeps backend configuration out of reusable Terraform source.

#### 2.0 Tasks

- [x] 2.1 Add a failing workflow-plan contract test that asserts the workflow requests only the permissions needed for checkout, OIDC token issuance, and normal workflow execution.
- [x] 2.2 Extend the failing plan contract test to assert the workflow configures AWS authentication through GitHub OIDC instead of repository-stored long-lived AWS keys and keeps the trust input surface narrow enough for the downstream IAM contract.
- [x] 2.3 Extend the failing plan contract test to assert the workflow runs `terraform -chdir=infra/terraform/app/dev init -input=false` with externalized backend inputs and then creates a reviewed saved plan with `terraform plan -out=tfplan -input=false`.
- [x] 2.4 Extend the failing plan contract test to assert the workflow uploads both the saved plan file and a reviewer-friendly sanitized plan-output artifact so maintainers can inspect the exact reviewed change set without runner shell access.
- [x] 2.5 Add the minimum workflow steps and artifact-upload behavior needed to satisfy the plan contract while preserving the existing remote-backend and `dev` stack assumptions from `infra/terraform/app/dev`.
- [x] 2.6 Capture sanitized plan-job log and artifact-list proof showing OIDC-based credential setup, Terraform initialization, saved-plan creation, and reviewer-visible artifact preservation.

### [x] 3.0 Define exact saved-plan apply execution, concurrency, and operator-visible failure handling

#### 3.0 Proof Artifact(s)

- File: `.github/workflows/terraform-apply-dev.yml` demonstrates artifact handoff into apply, `terraform apply -input=false <saved-plan>`, one concurrency boundary for the `dev` stack, and explicit failure checks for invalid confirmation, missing configuration, missing plan artifact, or non-zero Terraform apply.
- CLI: sanitized GitHub Actions apply-job log excerpt shows the workflow consumes the previously saved plan artifact rather than re-running a fresh implicit plan.
- Screenshot: completed GitHub Actions run for a success case and a failed safety case demonstrates clear operator-visible job status and diagnostics.
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowApplyContractTest.java` passes and demonstrates exact-plan apply behavior, concurrency protection, and clear failure reporting stay enforced.

#### 3.0 Tasks

- [x] 3.1 Add a failing workflow-apply contract test that asserts the apply job downloads and applies the exact previously saved `tfplan` artifact instead of running a fresh implicit plan.
- [x] 3.2 Extend the failing apply contract test to assert the workflow defines one concurrency boundary for the `dev` apply path so overlapping Terraform applies cannot run against the same stack simultaneously.
- [x] 3.3 Extend the failing apply contract test to assert the workflow fails clearly when the confirmation input is invalid, required configuration is missing, the reviewed plan artifact is unavailable, or `terraform apply` exits non-zero.
- [x] 3.4 Add the minimum apply-job workflow logic needed to satisfy the contract, including artifact handoff, working-directory consistency for saved-plan compatibility, and explicit operator-visible status messages.
- [x] 3.5 Capture sanitized success and failure proof artifacts showing exact-plan apply behavior, concurrency protection, and clear GitHub Actions job diagnostics for both pass and fail paths.

### [x] 4.0 Document the manual apply workflow contract and verification path for future operators

#### 4.0 Proof Artifact(s)

- File: `README.md`, `infra/terraform/app/dev/README.md`, or a dedicated workflow-facing infrastructure document demonstrates the approved operator sequence, OIDC dependency, reviewer-approval expectation, exact-plan apply rule, and `dev`-only scope.
- CLI: documented verification commands for reviewing workflow logs and artifacts identify the exact files, workflow name, and evidence a maintainer should inspect after a run.
- Diff: documentation changes demonstrate the workflow contract is captured in the repository without expanding into out-of-scope destroy, ECS rollout, or broader deployment automation behavior.
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowDocumentationContractTest.java` passes and demonstrates operator documentation preserves the reviewed-plan, OIDC, approval-gate, and `dev`-only constraints.

#### 4.0 Tasks

- [x] 4.1 Add a failing workflow-documentation contract test that asserts the repository documents the workflow name, `dev`-only scope, `main`-branch restriction, reviewer approval gate, typed confirmation requirement, and exact saved-plan apply rule.
- [x] 4.2 Extend the failing documentation contract test to assert the repository documents how the workflow reuses the existing `infra/terraform/app/dev` backend contract and avoids long-lived AWS keys by requiring GitHub OIDC.
- [x] 4.3 Update the most appropriate operator-facing documentation with the manual apply sequence, expected artifacts, and the exact workflow logs and files a maintainer should inspect after a run.
- [x] 4.4 Document the workflow’s scope boundaries explicitly so operators understand that image build, ECS rollout, destroy behavior, and broader deployment automation remain out of scope for this feature.
- [x] 4.5 Capture a sanitized documentation diff or rendered Markdown proof showing the workflow contract is documented in-repository and remains aligned with existing infrastructure guidance.
