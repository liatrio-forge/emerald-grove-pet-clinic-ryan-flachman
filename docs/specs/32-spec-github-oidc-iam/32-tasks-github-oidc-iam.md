## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/32-spec-github-oidc-iam/32-spec-github-oidc-iam.md` | Source spec that defines the OIDC trust baseline, role separation, GitHub environment contract, and proof expectations this task plan must cover. |
| `docs/specs/32-spec-github-oidc-iam/32-tasks-github-oidc-iam.md` | This task-plan artifact records execution order, proof artifacts, assumptions, and junior-developer guidance for the IAM contract work. |
| `docs/specs/32-spec-github-oidc-iam/32-audit-github-oidc-iam.md` | Planning-audit artifact that records gate status, standards evidence, and any approved remediation decisions for this feature. |
| `infra/terraform/app/dev/main.tf` | Main Terraform entry point where the GitHub OIDC provider, IAM roles, trust policies, and any attached policies would be defined. |
| `infra/terraform/app/dev/locals.tf` | Shared naming and tagging locals that should gain reviewer-readable GitHub OIDC provider, role, and trust-subject support values. |
| `infra/terraform/app/dev/outputs.tf` | Downstream output contract for role ARNs or names that later workflows and docs will consume. |
| `infra/terraform/app/dev/README.md` | Operator-facing documentation for the dev stack, existing manual apply workflow, and the new IAM role matrix and GitHub configuration contract. |
| `infra/terraform/app/dev/backend.hcl.example` | Existing backend contract reused by local verification commands and any new repository-owned verification entry point. |
| `infra/terraform/floci/README.md` | Local AWS-resources validation guidance that should document how to verify the GitHub OIDC IAM contract before AWS use. |
| `.github/workflows/terraform-apply-dev.yml` | Existing AWS-assuming workflow that must stay aligned with the new environment-based trust and variable contract. |
| `.github/issue-drafts/14-spec-terraform-destroy-workflow.md` | Existing destroy-workflow direction that the new destroy role and `dev-destroy` environment assumptions must support without prematurely implementing the workflow. |
| `scripts/verify-ecs-runtime-foundation-contract.sh` | Existing repository-owned verification-script pattern that the IAM verification entry point should mirror if a new script is added. |
| `scripts/verify-github-oidc-iam-contract.sh` | Planned repository-owned verification entry point for validating the IAM contract reproducibly with sanitized Terraform commands. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowPlanContractTest.java` | Existing workflow-contract test pattern showing how GitHub Actions YAML assumptions are asserted in focused Java tests. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsIamBoundaryContractTest.java` | Existing Terraform IAM contract-test pattern showing how reviewer-readable role and trust assertions are expressed. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubOidcTrustPolicyContractTest.java` | Planned contract test for the shared GitHub OIDC provider, exact `aud`, exact repository-bound `sub`, and wildcard-trust exclusion. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubTerraformWorkflowRolesContractTest.java` | Planned contract test for separate apply and destroy roles, exact environment subjects, and broad-but-not-admin Terraform permissions. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubDeployRoleAndConfigurationContractTest.java` | Planned contract test for the separate deploy role, protected `dev` environment subject, and required GitHub variable names. |
| `src/test/java/org/springframework/samples/petclinic/system/GitHubOidcIamDocumentationContractTest.java` | Planned documentation-contract test for the reviewer-facing role matrix, environment names, and no-long-lived-key guidance. |

### Notes

- Follow strict TDD during implementation: each task slice begins with a failing Java contract test or failing workflow/documentation contract test before Terraform, YAML, script, or README changes.
- Use `terraform -chdir=infra/terraform/app/dev validate` and sanitized `terraform plan -no-color` output as the main infrastructure proof path, then run focused Java tests and `./mvnw test` before completion when test files change.
- Keep proof artifacts sanitized: use placeholder credentials such as `AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and `AWS_EC2_METADATA_DISABLED=true`; avoid live AWS account identifiers, tokens, or raw Terraform state output.
- Reuse the existing `infra/terraform/app/dev` naming, tagging, backend, and output conventions so later workflow specs can consume one stable IAM contract without reconstructing role names or trust subjects.
- Explicit implementation default for the spec’s open question: encode the GitHub OIDC provider and GitHub-assumable IAM roles in `infra/terraform/app/dev` for v1 rather than creating a separate CI/IAM Terraform stack.
- Keep non-goals intact: this plan defines the IAM and GitHub configuration contract, but it does not fully implement new destroy or deploy workflow YAML files, redesign the ECS task runtime role, or author exhaustive least-privilege Terraform permissions.

## Tasks

### [x] 1.0 Define the shared GitHub OIDC provider and trust-policy baseline

#### 1.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf` and `infra/terraform/app/dev/locals.tf` demonstrate the GitHub OIDC provider and exact-trust baseline are defined for the repository
- Test: `src/test/java/org/springframework/samples/petclinic/system/GitHubOidcTrustPolicyContractTest.java` passes and demonstrates `aud = sts.amazonaws.com`, repository-bound exact `sub` matching, and no repo-wide wildcard trust
- CLI: `terraform -chdir=infra/terraform/app/dev validate` exits `0` and demonstrates the OIDC provider and trust-policy baseline are syntactically valid
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows the GitHub OIDC provider and trust-policy resources with sanitized output

#### 1.0 Tasks

- [x] 1.1 Add a failing Terraform contract test that asserts the dev app stack defines one GitHub OIDC provider for `token.actions.githubusercontent.com`.
- [x] 1.2 Extend the failing contract test coverage to assert every trusting role uses exact `aud = sts.amazonaws.com`, exact repository-bound `sub` matching, and no repo-wide wildcard GitHub subject.
- [x] 1.3 Add the minimum Terraform OIDC provider resource, trust-policy locals, and reusable subject values needed to make the shared trust baseline reviewer-readable.
- [x] 1.4 Capture `terraform validate` and sanitized `terraform plan -no-color` proof output showing the provider and exact-trust baseline without live AWS credentials.

### [x] 2.0 Define separate Terraform apply and destroy IAM role boundaries

#### 2.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf`, `infra/terraform/app/dev/outputs.tf`, and `infra/terraform/app/dev/README.md` demonstrate separate `terraform-apply-dev` and `terraform-destroy-dev` roles with distinct trust subjects and documented permission boundaries
- Test: `src/test/java/org/springframework/samples/petclinic/system/GitHubTerraformWorkflowRolesContractTest.java` passes and demonstrates apply and destroy do not share one role, use exact environment subjects, and stay below unconstrained administrator access
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows distinct apply and destroy roles plus their reviewer-readable names or outputs
- Screenshot: GitHub environment settings for `dev` and `dev-destroy` demonstrate the intended protected-environment boundary for apply versus destroy

#### 2.0 Tasks

- [x] 2.1 Add a failing role-boundary contract test that asserts the app stack defines separate apply and destroy roles rather than one shared Terraform workflow role.
- [x] 2.2 Extend the failing test coverage to assert the apply role trusts the exact protected `dev` environment subject and the destroy role trusts the exact protected `dev-destroy` environment subject.
- [x] 2.3 Extend the failing test coverage to assert Terraform workflow permissions remain broad enough for the POC but do not grant unconstrained administrator-level access, and that IAM-sensitive actions are documented explicitly.
- [x] 2.4 Add the minimum Terraform roles, policy attachments or inline policy documents, and outputs needed to make the apply-versus-destroy boundary explicit in the existing dev stack.
- [x] 2.5 Update the dev stack README with a short role matrix explaining why destroy is intentionally stricter than apply and which environment each role expects.
- [x] 2.6 Capture sanitized `terraform plan -no-color` output and GitHub environment screenshots showing the two-role, two-environment boundary is reviewable end to end.

### [x] 3.0 Define the narrow app deploy role and GitHub configuration contract

#### 3.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf`, `infra/terraform/app/dev/outputs.tf`, and `infra/terraform/app/dev/README.md` demonstrate a separate deploy role, its narrower permission intent, and the required GitHub variable contract
- Test: `src/test/java/org/springframework/samples/petclinic/system/GitHubDeployRoleAndConfigurationContractTest.java` passes and demonstrates deploy remains separate from Terraform roles, trusts the protected `dev` environment subject, and documents required variable names
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowPlanContractTest.java` or a follow-on workflow contract test passes and demonstrates AWS-assuming jobs declare `id-token: write`, the expected environment, and the expected role variable names
- Screenshot: GitHub environment or variable configuration demonstrates role ARNs and deployment-sensitive values are owned by protected environments instead of long-lived AWS secrets

#### 3.0 Tasks

- [x] 3.1 Add a failing deploy-role contract test that asserts the app stack defines one deploy role separate from the Terraform apply and destroy roles.
- [x] 3.2 Extend the failing deploy-role test coverage to assert the deploy role trusts the protected `dev` environment subject and stays narrower than the Terraform roles by targeting only the ECS rollout path and related read operations.
- [x] 3.3 Add or extend a failing workflow-configuration contract test that asserts the existing apply workflow and the documented downstream contract use `id-token: write`, protected environments, and distinct role variable names instead of long-lived AWS secrets.
- [x] 3.4 Add the minimum Terraform deploy role and output contract needed so downstream build, task-definition, and service-update workflows can consume one stable deploy role ARN.
- [x] 3.5 Document the required GitHub configuration inputs, including `AWS_REGION`, `TERRAFORM_APPLY_ROLE_ARN`, `TERRAFORM_DESTROY_ROLE_ARN`, `APP_DEPLOY_ROLE_ARN`, `TF_STATE_BUCKET`, and `TF_LOCK_TABLE`, with environment-versus-repository ownership called out explicitly.
- [x] 3.6 Capture sanitized plan output and GitHub configuration screenshots showing the deploy role remains distinct and the variable contract is reviewer-readable.

### [x] 4.0 Add reviewer-facing documentation and reproducible verification for the IAM contract

#### 4.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/README.md` and `infra/terraform/floci/README.md` demonstrate the role matrix, trust subjects, GitHub environment contract, verification command, and the temporary broad-permission POC boundary
- File: `scripts/verify-github-oidc-iam-contract.sh` demonstrates a repository-owned verification entry point that initializes the dev stack, validates Terraform, and runs a sanitized local plan for the IAM contract
- Test: `src/test/java/org/springframework/samples/petclinic/system/GitHubOidcIamDocumentationContractTest.java` passes and demonstrates the operator-facing docs include exact environment names, role responsibilities, and no long-lived AWS key path
- CLI: `./scripts/verify-github-oidc-iam-contract.sh` exits `0` and demonstrates the IAM contract can be checked reproducibly before live AWS use

#### 4.0 Tasks

- [x] 4.1 Add a failing documentation or verification-workflow contract test that asserts the repository defines one reviewer-facing IAM verification path with sanitized credentials and clear missing-file failures.
- [x] 4.2 Create `scripts/verify-github-oidc-iam-contract.sh` so it mirrors the repository’s verification-script pattern, reuses `backend.hcl.example`, runs `terraform validate`, and captures a sanitized `terraform plan -no-color` for the IAM contract.
- [x] 4.3 Update `infra/terraform/floci/README.md` and the dev stack README with the exact local verification sequence, the placeholder credential expectations, and the no-long-lived-key guidance for GitHub OIDC workflows.
- [x] 4.4 Capture the `./scripts/verify-github-oidc-iam-contract.sh` proof path and documentation diff so reviewers can reproduce the IAM contract validation flow and confirm the artifacts remain observable, reproducible, scope-linked, and sanitized.
