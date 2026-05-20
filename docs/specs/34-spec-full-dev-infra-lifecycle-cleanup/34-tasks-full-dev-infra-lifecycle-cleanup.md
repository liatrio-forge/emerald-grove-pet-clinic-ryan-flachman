## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/34-spec-full-dev-infra-lifecycle-cleanup/34-spec-full-dev-infra-lifecycle-cleanup.md` | Source spec that defines the stack split, workflow lifecycle, cleanup handoff, proof expectations, and open questions this plan must cover. |
| `docs/specs/34-spec-full-dev-infra-lifecycle-cleanup/34-tasks-full-dev-infra-lifecycle-cleanup.md` | This task-plan artifact records implementation order, assumptions, proof artifacts, and junior-developer guidance for the lifecycle cleanup feature. |
| `docs/specs/34-spec-full-dev-infra-lifecycle-cleanup/34-audit-full-dev-infra-lifecycle-cleanup.md` | Planning-audit artifact that records gate status, standards evidence, and any required remediation before implementation handoff. |
| `docs/specs/34-spec-full-dev-infra-lifecycle-cleanup/34-questions-1-full-dev-infra-lifecycle-cleanup.md` | Discovery artifact that captures recommended answers for teardown ownership, normal rebuild boundaries, and GitHub cleanup behavior. |
| `README.md` | Root operator-facing lifecycle guide already documents bootstrap/apply workflows and must be updated for the new three-layer lifecycle plus final cleanup handoff. |
| `infra/terraform/state/dev/README.md` | Existing backend-boundary guide that must remain the canonical source for backend ownership and teardown ordering. |
| `infra/terraform/identity/dev/README.md` | Planned identity-layer lifecycle guide that should explain OIDC provider ownership, GitHub role ownership, outputs, and destroy boundaries. |
| `infra/terraform/identity/dev/main.tf` | Planned Terraform entry point for the dedicated identity stack that will own the GitHub OIDC provider and workflow IAM roles. |
| `infra/terraform/identity/dev/locals.tf` | Planned naming, subject, and tag locals for the identity layer so role names and trust subjects stay reviewer-readable. |
| `infra/terraform/identity/dev/outputs.tf` | Planned output contract that will expose the apply, destroy, publish, and deploy role ARNs to downstream workflows. |
| `infra/terraform/identity/dev/variables.tf` | Planned identity-stack input contract for environment, GitHub repository identity, and any shared bootstrap parameters. |
| `infra/terraform/identity/dev/versions.tf` | Planned provider/version pinning for the new Terraform layer to keep it aligned with existing stack conventions. |
| `infra/terraform/app/dev/main.tf` | Current app stack entry point that now owns the GitHub OIDC provider and workflow IAM roles and must be reduced to runtime infrastructure only. |
| `infra/terraform/app/dev/outputs.tf` | Current output contract that likely needs to swap direct resource outputs for remote-state or input-driven foundation outputs. |
| `infra/terraform/app/dev/locals.tf` | Current naming and trust-subject locals that will need to shed identity ownership and preserve only app-runtime concerns. |
| `infra/terraform/app/dev/README.md` | Current dev app contract guide that must document the post-split app-only ownership boundary, OIDC destroy workflow, and stable variable contract. |
| `.github/workflows/terraform-apply-dev.yml` | Existing OIDC apply workflow that must remain limited to `app/dev` and may need to consume identity outputs rather than app-owned role creation. |
| `.github/workflows/terraform-destroy-dev.yml` | Planned OIDC destroy workflow for `app/dev` that should use `dev-destroy`, typed confirmation, and the stable backend-variable contract. |
| `.github/workflows/bootstrap-dev-infra.yml` | Existing bootstrap workflow that must be updated to create `state/dev`, then `identity/dev`, then `app/dev`, and to preserve bootstrap-secret policy per this spec. |
| `.github/workflows/bootstrap-destroy-dev-infra.yml` | Planned foundation teardown workflow that must destroy `app/dev` first, then `identity/dev`, then `state/dev`, with `main`-only and typed-confirmation guards. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformStateBoundaryContractTest.java` | Existing contract-test pattern for Terraform layer ownership and lifecycle documentation that can be extended or mirrored for the identity split. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubTerraformWorkflowRolesContractTest.java` | Existing contract-test pattern that currently asserts GitHub workflow roles live in `app/dev` and must be updated or superseded for identity-stack ownership. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubOidcTrustPolicyContractTest.java` | Existing trust-policy contract test that verifies subject boundaries for apply, destroy, publish, and deploy roles and will likely move to the identity layer. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubOidcIamDocumentationContractTest.java` | Existing documentation-contract test that should be updated to reflect the new identity boundary and the no-long-lived-keys workflow guidance. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformBootstrapWorkflowContractTest.java` | Existing bootstrap workflow contract test that should be extended to enforce `state/dev` -> `identity/dev` -> `app/dev` ordering and persistent bootstrap-secret guidance. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowDocumentationContractTest.java` | Existing documentation-contract test pattern for GitHub workflow docs that can be extended for destroy/rebuild lifecycle guidance. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformIdentityStackBoundaryContractTest.java` | Planned contract test that should fail first and prove identity resources moved into `infra/terraform/identity/dev`. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubIdentityStackDocumentationContractTest.java` | Planned documentation contract test for the three-stack ownership model and identity-layer responsibilities. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformDestroyDevWorkflowContractTest.java` | Planned workflow contract test for the manual OIDC `app/dev` destroy path. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformLifecycleSeparationDocumentationContractTest.java` | Planned documentation contract test for normal app rebuild versus final foundation teardown guidance. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformBootstrapDestroyWorkflowContractTest.java` | Planned workflow contract test for the repo-owned final teardown path and strict destroy ordering. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformFinalCleanupDocumentationContractTest.java` | Planned documentation contract test for blank-after-teardown GitHub variables and persistent `dev-bootstrap` secret policy. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubLifecycleConfigurationContractTest.java` | Planned contract test for the final GitHub variable/environment matrix and cleanup handoff wording. |

### Notes

- Follow strict TDD during implementation: each slice starts with failing Java contract or documentation tests before Terraform, workflow YAML, or documentation edits.
- Use the repository's existing verification style: focused Java file-content contract tests plus targeted `terraform validate` commands for the affected stack directories.
- Keep proof artifacts sanitized: no live AWS credentials, no raw tokens, no account-specific secrets, and no unsanitized GitHub environment captures committed to the repository.
- Explicit standards-precedence decision: `docs/specs/34-spec-full-dev-infra-lifecycle-cleanup/34-spec-full-dev-infra-lifecycle-cleanup.md` supersedes the current `README.md` instruction to remove `dev-bootstrap` secrets after bootstrap; implementation must update repo docs/tests to reflect the persistent `dev-bootstrap` exception required by this spec.
- Explicit implementation default for open question 1: the final teardown flow should emit explicit operator instructions for blanking GitHub variable values after AWS teardown rather than mutating GitHub configuration automatically through the GitHub API.
- Explicit implementation default for open question 2: persistent admin-backed bootstrap credentials remain scoped to the protected `dev-bootstrap` environment for this POC; moving to brokered short-lived credentials is a later hardening pass and remains out of scope.
- Keep non-goals intact: this plan does not redesign least-privilege IAM, add automatic GitHub configuration mutation, or expand ECS deployment behavior beyond the stack split and variable rewiring needed for lifecycle cleanup.

## Tasks

### [x] 1.0 Extract The Dev Identity Foundation Stack

#### 1.0 Proof Artifact(s)

- Test: `./mvnw test -Dtest=TerraformIdentityStackBoundaryContractTest,GitHubIdentityStackDocumentationContractTest` passes and demonstrates GitHub OIDC provider plus workflow IAM roles are owned by `infra/terraform/identity/dev` instead of `infra/terraform/app/dev`.
- CLI: `terraform -chdir=infra/terraform/identity/dev validate -no-color` exits successfully and demonstrates the new identity stack is syntactically valid.
- Documentation: `infra/terraform/state/dev/README.md`, `infra/terraform/identity/dev/README.md`, and `infra/terraform/app/dev/README.md` explicitly describe the `state/dev` -> `identity/dev` -> `app/dev` ownership boundary.

#### 1.0 Tasks

- [x] 1.1 Add a failing boundary contract test that asserts a dedicated `infra/terraform/identity/dev` stack exists and owns the GitHub OIDC provider plus the Terraform apply, Terraform destroy, app publish, and app deploy roles.
- [x] 1.2 Extend the failing boundary contract test to assert `infra/terraform/app/dev/main.tf` no longer declares the GitHub OIDC provider or GitHub workflow IAM roles directly.
- [x] 1.3 Add a failing documentation contract test that asserts the repository documents `state/dev` as backend owner, `identity/dev` as GitHub identity owner, and `app/dev` as runtime-infrastructure owner.
- [x] 1.4 Create the minimum `infra/terraform/identity/dev` Terraform files needed to define provider/version pins, shared locals, the OIDC provider, IAM policies, IAM roles, and outputs that mirror the existing workflow-role contract.
- [x] 1.5 Update `infra/terraform/app/dev` to consume the foundation identity contract without recreating GitHub identity resources, keeping runtime resources and any downstream references intact.
- [x] 1.6 Update lifecycle documentation so the three-stack ownership model, validation entry points, and destroy boundaries are explicit for reviewers and junior maintainers.

### [x] 2.0 Restore Independent OIDC-Based App Destroy And Rebuild

#### 2.0 Proof Artifact(s)

- Workflow file: `.github/workflows/terraform-destroy-dev.yml` shows a repo-owned `workflow_dispatch` destroy path for `app/dev` that uses the protected `dev-destroy` environment and `TERRAFORM_DESTROY_ROLE_ARN`.
- Test: `./mvnw test -Dtest=TerraformDestroyDevWorkflowContractTest,TerraformLifecycleSeparationDocumentationContractTest` passes and demonstrates `app/dev` destroy is separate from backend and identity teardown.
- Documentation: `infra/terraform/app/dev/README.md` documents the exact OIDC-only destroy and recreate sequence for `app/dev`, including the stable GitHub variable contract.

#### 2.0 Tasks

- [x] 2.1 Add a failing workflow contract test that asserts `.github/workflows/terraform-destroy-dev.yml` exists, is manual-only, requires typed confirmation, and restricts destructive execution to `main`.
- [x] 2.2 Extend the failing workflow contract test to assert the destroy workflow uses the protected `dev-destroy` environment, GitHub OIDC, `TERRAFORM_DESTROY_ROLE_ARN`, and the same backend variable names already used by steady-state workflows.
- [x] 2.3 Add a failing documentation contract test that asserts the repo distinguishes normal `app/dev` destroy/recreate from final foundation teardown and states that backend plus identity resources survive normal app destruction.
- [x] 2.4 Add the minimum destroy workflow steps needed to materialize backend configuration, initialize `infra/terraform/app/dev`, and run a reviewer-visible `terraform destroy` path without touching `state/dev` or `identity/dev`.
- [x] 2.5 Update the app-stack docs and root lifecycle guidance with the exact OIDC-only destroy-and-recreate sequence, required environments, and stable GitHub variable matrix.
- [x] 2.6 Add or update focused contract tests and documentation wording that prove `Terraform Apply Dev`, `Terraform Destroy Dev`, publish, and deploy workflows all continue using the same variable names after the stack split.

### [x] 3.0 Add Repo-Owned Bootstrap Create And Final Foundation Teardown Workflows

#### 3.0 Proof Artifact(s)

- Workflow files: `.github/workflows/bootstrap-dev-infra.yml` and `.github/workflows/bootstrap-destroy-dev-infra.yml` show protected `dev-bootstrap` usage, typed confirmation, `main`-branch restriction, secret-backed bootstrap credentials, and ordered `state/dev` -> `identity/dev` -> `app/dev` create plus `app/dev` -> `identity/dev` -> `state/dev` destroy behavior.
- Test: `./mvnw test -Dtest=TerraformBootstrapWorkflowContractTest,TerraformBootstrapDestroyWorkflowContractTest` passes and demonstrates the bootstrap workflows enforce the required ordering and keep bootstrap credentials out of dispatch inputs.
- CLI: `terraform -chdir=infra/terraform/state/dev validate -no-color` and `terraform -chdir=infra/terraform/identity/dev validate -no-color` both exit successfully and demonstrate the foundation layers are independently valid.

#### 3.0 Tasks

- [x] 3.1 Extend the existing bootstrap workflow contract test so it fails until the create path applies `state/dev`, then `identity/dev`, then `app/dev`, and summarizes the foundation outputs needed by downstream GitHub configuration.
- [x] 3.2 Add a failing bootstrap-destroy workflow contract test that asserts the repo-owned teardown workflow is manual-only, `main`-only, protected by `dev-bootstrap`, requires typed confirmation, and uses bootstrap secrets from the environment rather than dispatch inputs.
- [x] 3.3 Extend the failing bootstrap-destroy contract test to assert the destroy order is `app/dev` first when present, then `identity/dev`, then `state/dev`, with reviewer-visible summary output for the cleanup handoff.
- [x] 3.4 Update `.github/workflows/bootstrap-dev-infra.yml` with the minimum changes needed to bootstrap all three stacks in order and to summarize the stable GitHub variable values operators must maintain.
- [x] 3.5 Add `.github/workflows/bootstrap-destroy-dev-infra.yml` with the minimum ordered teardown logic needed to destroy app, identity, and state layers safely while preserving proof-oriented output and clear failure messages.
- [x] 3.6 Update repository docs so bootstrap create versus final teardown responsibilities, bootstrap-secret storage, and branch/environment protections are explicit and consistent across root and Terraform-layer readmes.

### [x] 4.0 Document Final Cleanup Handoff And Persistent GitHub Bootstrap Exception

#### 4.0 Proof Artifact(s)

- Documentation: `README.md` and the lifecycle readmes document the exact GitHub variable reset sequence, preserve stable variable names with intentionally blank values after teardown, and call out persistent `dev-bootstrap` secrets as the standing POC exception.
- Test: `./mvnw test -Dtest=TerraformFinalCleanupDocumentationContractTest,GitHubLifecycleConfigurationContractTest` passes and demonstrates the cleanup handoff, variable names, environments, and bootstrap-secret policy are explicitly documented.
- Screenshot: `docs/specs/34-spec-full-dev-infra-lifecycle-cleanup/34-proofs/34-task-04-github-config-sanitized.png` demonstrates the expected GitHub variable names and protected environments without exposing secret values.

#### 4.0 Tasks

- [x] 4.1 Add a failing documentation contract test that asserts the repository lists the exact GitHub variables and environments used by apply, destroy, publish, deploy, and bootstrap lifecycle workflows.
- [x] 4.2 Extend the failing documentation contract test to assert the final teardown handoff explicitly instructs operators to blank AWS-derived GitHub variable values while preserving the variable names for future reuse.
- [x] 4.3 Extend the failing documentation contract test to assert the repository documents persistent `dev-bootstrap` secrets as a standing POC exception rather than a one-time secret-removal step.
- [x] 4.4 Update root and stack-level docs with the final cleanup checklist, variable-reset wording, protected-environment matrix, and the policy that bootstrap secrets persist by design in `dev-bootstrap`.
- [x] 4.5 Capture or reference sanitized reviewer-facing proof that shows the expected GitHub environment names and variable names without exposing any secret values or private identifiers.
- [x] 4.6 Reconcile any older documentation that still instructs operators to remove bootstrap secrets after bootstrap so repository guidance matches the spec and workflow contract consistently.
