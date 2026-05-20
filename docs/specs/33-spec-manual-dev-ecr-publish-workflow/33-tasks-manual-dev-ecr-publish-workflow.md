## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-spec-manual-dev-ecr-publish-workflow.md` | Source spec that defines the manual workflow, dedicated publish-role contract, immutable tagging, and proof expectations this plan must implement. |
| `docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-tasks-manual-dev-ecr-publish-workflow.md` | This task-plan artifact records execution order, assumptions, proof artifacts, and junior-developer guidance for the feature. |
| `docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-audit-manual-dev-ecr-publish-workflow.md` | Planning-audit artifact that records gate status, standards evidence, and any later remediation decisions. |
| `.github/workflows/manual-dev-ecr-publish.yml` | Planned GitHub Actions workflow that will define manual dispatch, typed confirmation, `main`-only behavior, concurrency, Maven packaging, Docker build, OIDC auth, and ECR push steps. |
| `README.md` | Root operator-facing documentation that may need the workflow entry point and verification commands documented alongside existing infrastructure workflow guidance. |
| `infra/terraform/app/dev/README.md` | Existing dev-stack contract document where GitHub environment variables, ECR repository reuse, and the dedicated publish-role contract should be documented. |
| `infra/terraform/app/dev/main.tf` | Main Terraform entry point where the GitHub OIDC publish IAM policy and role are most likely to be added beside existing apply, destroy, and deploy roles. |
| `infra/terraform/app/dev/locals.tf` | Shared reviewer-readable naming and trust-subject locals that may need a new app-publish role identifier. |
| `infra/terraform/app/dev/outputs.tf` | Downstream output contract where the publish-role ARN can be exposed for GitHub environment configuration. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowDispatchContractTest.java` | Existing workflow-contract test pattern that shows how GitHub Actions YAML assumptions are asserted with focused file-based Java tests. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubDeployRoleAndConfigurationContractTest.java` | Existing IAM/configuration contract-test pattern that may be extended or mirrored for the new app-publish role and variable contract. |
| `src/test/java/org/springframework/samples/petclinic/system/ManualDevEcrPublishWorkflowDispatchContractTest.java` | Planned contract test for manual-only trigger behavior, `main` guard, typed confirmation, protected environment use, and concurrency settings. |
| `src/test/java/org/springframework/samples/petclinic/system/ManualDevEcrPublishWorkflowBuildAndAuthContractTest.java` | Planned contract test for Maven packaging, minimal GitHub permissions, OIDC auth, and dedicated publish-role usage. |
| `src/test/java/org/springframework/samples/petclinic/system/ManualDevEcrPublishWorkflowPushContractTest.java` | Planned contract test for Dockerfile reuse, SHA-only image publication, repository URI reuse, digest visibility, and exclusion of mutable tags or ECS rollout steps. |
| `src/test/java/org/springframework/samples/petclinic/system/ManualDevEcrPublishWorkflowDocumentationContractTest.java` | Planned documentation contract test for workflow naming, variable ownership, verification commands, and explicit non-goal boundaries. |

### Notes

- Follow strict TDD during implementation: each slice begins with a failing Java contract test before workflow YAML, Terraform, or documentation changes.
- Use focused Java contract tests plus `./mvnw test` as the main repository validation path, consistent with existing workflow and Terraform contract work in this repo.
- Keep proof artifacts sanitized: no live AWS credentials, no unmasked Docker login output, no raw tokens, and no unnecessary AWS account identifiers in committed screenshots or logs.
- Explicit implementation default for the spec's open question: do **not** add a new repository-owned verification script in v1. The verification path for this feature is the workflow contract tests plus a manually triggered GitHub Actions run and ECR inspection evidence.
- Explicit implementation default for GitHub configuration: store the Terraform-exported ECR repository URI in a protected `dev` environment variable named `REPOSITORY_URI`, and store the dedicated publish role ARN in `APP_PUBLISH_ROLE_ARN`.
- Keep non-goals intact: this plan does not add automatic publish-on-merge, ECS task-definition registration, ECS service updates, or mutable convenience tags.

## Tasks

### [x] 1.0 Define the manual workflow entrypoint and publish safety gates

#### 1.0 Proof Artifact(s)

- File: `.github/workflows/manual-dev-ecr-publish.yml` demonstrates one `workflow_dispatch` entrypoint scoped to the dev image-publish path, `main` branch enforcement, typed confirmation input, protected `dev` environment usage, and one concurrency boundary with queued runs.
- Screenshot: GitHub Actions manual-run form for the publish workflow shows the operator-facing confirmation input required to start a dev image publication.
- Screenshot: GitHub Actions run details for the publish job show the workflow is bound to the protected `dev` environment before AWS-backed publishing occurs.
- Test: `src/test/java/org/springframework/samples/petclinic/system/ManualDevEcrPublishWorkflowDispatchContractTest.java` passes and demonstrates the workflow contract keeps manual invocation, `main`-only scope, typed confirmation, environment binding, and concurrency behavior intact.

#### 1.0 Tasks

- [x] 1.1 Add a failing workflow-dispatch contract test that asserts one `.github/workflows/manual-dev-ecr-publish.yml` workflow exists and is triggered only by `workflow_dispatch`.
- [x] 1.2 Extend the failing dispatch contract test to assert the workflow enforces `main`-only publish behavior, requires a typed confirmation input that explicitly references the dev image publication, binds the publish job to the protected `dev` environment, and defines one concurrency group with queued runs.
- [x] 1.3 Add the minimum workflow skeleton needed to satisfy the dispatch contract, including the workflow name, manual inputs, top-level permissions block, and a readable branch guard without introducing `push` or `pull_request` triggers.
- [x] 1.4 Add the minimum safety-gate logic needed to fail clearly when the workflow runs from a non-`main` ref or the confirmation input is invalid, while keeping the AWS-backed publish job behind the protected `dev` environment.
- [x] 1.5 Capture sanitized screenshots and file-based proof showing the manual run form, environment-bound job details, and concurrency-aware workflow contract are reviewer-visible.

### [x] 2.0 Define the Maven build and GitHub OIDC authentication contract

#### 2.0 Proof Artifact(s)

- File: `.github/workflows/manual-dev-ecr-publish.yml` demonstrates the Maven package step, minimal workflow permissions, `id-token: write`, and AWS credential configuration through `APP_PUBLISH_ROLE_ARN`.
- CLI: sanitized GitHub Actions job log excerpt shows `./mvnw package -DskipTests` completes before image publication and AWS credentials are configured through GitHub OIDC instead of long-lived keys.
- Documentation: operator-facing variable contract in `README.md` or `infra/terraform/app/dev/README.md` shows `APP_PUBLISH_ROLE_ARN`, `REPOSITORY_URI`, and `AWS_REGION` ownership and expected scope.
- Test: `src/test/java/org/springframework/samples/petclinic/system/ManualDevEcrPublishWorkflowBuildAndAuthContractTest.java` passes and demonstrates the workflow preserves the Maven-first build path, minimal permissions, and dedicated publish-role contract.

#### 2.0 Tasks

- [x] 2.1 Add a failing build-and-auth contract test that asserts the workflow runs `./mvnw package -DskipTests` before any Docker build or ECR push steps execute.
- [x] 2.2 Extend the failing build-and-auth contract test to assert the workflow requests only the permissions needed for checkout, OIDC token issuance, and normal workflow execution, including explicit `id-token: write`.
- [x] 2.3 Add or extend a failing IAM/configuration contract test that asserts the dev stack and documentation define a dedicated publish role and environment variable `APP_PUBLISH_ROLE_ARN`, separate from the existing `APP_DEPLOY_ROLE_ARN` contract.
- [x] 2.4 Add the minimum Terraform IAM policy, role, naming locals, and ARN output needed to create the app-publish role in `infra/terraform/app/dev` beside the existing GitHub-assumable roles.
- [x] 2.5 Add the minimum workflow AWS-authentication steps needed to assume `APP_PUBLISH_ROLE_ARN` through GitHub OIDC, consume `AWS_REGION` and `REPOSITORY_URI` from GitHub configuration, and avoid any long-lived AWS key usage.
- [x] 2.6 Update the most appropriate operator-facing documentation with the GitHub variable contract for `APP_PUBLISH_ROLE_ARN`, `REPOSITORY_URI`, and `AWS_REGION`, then capture sanitized log proof that Maven packaging and OIDC credential setup succeed before publish.

### [x] 3.0 Define immutable Docker build, ECR push, and traceability behavior

#### 3.0 Proof Artifact(s)

- File: `.github/workflows/manual-dev-ecr-publish.yml` demonstrates Docker build and ECR push behavior tied to the repository-owned root `Dockerfile`, protected-environment `REPOSITORY_URI`, and a single full Git SHA tag.
- CLI: GitHub Actions logs or job summary show the fully qualified published image reference and pushed digest for the completed workflow run.
- CLI: ECR verification command output shows the expected SHA-tagged image exists in the `dev-petclinic` repository after the workflow completes.
- Test: `src/test/java/org/springframework/samples/petclinic/system/ManualDevEcrPublishWorkflowPushContractTest.java` passes and demonstrates immutable SHA-only publication, repository URI reuse, digest visibility, and exclusion of ECS rollout steps or mutable alias tags.

#### 3.0 Tasks

- [x] 3.1 Add a failing push contract test that asserts the workflow builds from the repository-owned root `Dockerfile` and publishes to the repository identified by the protected `REPOSITORY_URI` variable rather than reconstructing repository names in YAML.
- [x] 3.2 Extend the failing push contract test to assert the workflow tags the image with the full Git commit SHA, surfaces the fully qualified image reference and pushed digest in workflow-visible output, and does not publish `latest`, `main`, or any other mutable tag alias.
- [x] 3.3 Extend the failing push contract test to assert the workflow does not include ECS task-definition registration, ECS service updates, or other rollout steps that exceed the image-publication scope.
- [x] 3.4 Add the minimum Docker login, build, and ECR push workflow steps needed to satisfy the contract while preserving SHA-only publication and reviewer-readable workflow output.
- [x] 3.5 Add the minimum workflow summary or log output steps needed to make the final image reference and digest easy to inspect after a run without exposing masked credentials or raw Docker auth data.
- [x] 3.6 Capture a sanitized successful publish run and ECR inspection proof that shows the pushed SHA-tagged image exists in the correct repository and can be traced back to the originating commit.

### [x] 4.0 Document the workflow contract and operator verification path

#### 4.0 Proof Artifact(s)

- File: `README.md`, `infra/terraform/app/dev/README.md`, or a dedicated workflow-facing infrastructure document demonstrates the approved manual publish sequence, OIDC dependency, `main`-only scope, protected `dev` environment boundary, `APP_PUBLISH_ROLE_ARN` and `REPOSITORY_URI` ownership, SHA-only tag rule, and explicit non-goals.
- CLI: documented verification commands using `gh` and AWS/ECR inspection identify the exact workflow name, logs, and repository evidence a maintainer should inspect after a run.
- Diff: documentation changes demonstrate the manual image-publish contract is captured in-repository without expanding into automatic publish, ECS rollout, or mutable tag management.
- Test: `src/test/java/org/springframework/samples/petclinic/system/ManualDevEcrPublishWorkflowDocumentationContractTest.java` passes and demonstrates the repository documents the publish workflow, variable ownership, verification path, and scope boundaries consistently.

#### 4.0 Tasks

- [x] 4.1 Add a failing documentation contract test that asserts the repository documents the workflow name, manual-only trigger model, `main`-branch restriction, typed confirmation requirement, protected `dev` environment boundary, and SHA-only publication rule.
- [x] 4.2 Extend the failing documentation contract test to assert the repository documents `APP_PUBLISH_ROLE_ARN`, `REPOSITORY_URI`, and `AWS_REGION` ownership, plus the fact that long-lived AWS keys are not part of the approved workflow path.
- [x] 4.3 Update the most appropriate operator-facing documentation with the exact `gh workflow run`, `gh run view --log`, and ECR verification commands a maintainer should use to review a publish run end to end.
- [x] 4.4 Document the workflow scope boundaries explicitly so operators understand that automatic publish, ECS rollout, mutable tags, and any repo-owned verification script remain out of scope for this feature.
- [x] 4.5 Capture a sanitized documentation diff or rendered Markdown proof showing the workflow contract is documented in-repository and remains aligned with the existing ECR and GitHub OIDC guidance.
