# 31-spec-manual-terraform-apply.md

## Introduction/Overview

This feature defines the manual GitHub Actions workflow contract for applying Terraform-managed infrastructure changes to the `dev` environment. The primary goal is to let a maintainer run a safe, reviewable infrastructure apply from GitHub Actions without long-lived AWS credentials, while keeping the workflow narrow enough that it only governs the existing `dev` stack and does not absorb broader deployment concerns.

## Goals

- Define one manual GitHub Actions apply workflow for the existing `infra/terraform/app/dev` stack.
- Require GitHub OIDC-based AWS authentication instead of long-lived AWS access keys.
- Require both reviewer approval and explicit typed confirmation before Terraform apply runs.
- Ensure the applied infrastructure changes exactly match a reviewed saved Terraform plan artifact.
- Keep the workflow scoped to the `dev` environment and the `main` branch only.

## User Stories

- **As a maintainer**, I want to manually apply `dev` infrastructure from GitHub Actions so that shared infrastructure changes stay operator-controlled instead of running automatically on merge.
- **As a reviewer**, I want the workflow to show a reviewed Terraform plan before apply so that I can verify what infrastructure will change.
- **As a security-conscious platform engineer**, I want GitHub Actions to assume AWS access through OIDC so that the repository does not depend on long-lived AWS credentials.
- **As a future spec author**, I want the apply workflow contract documented clearly so later workflow and IAM work can build on stable assumptions instead of re-deciding manual deployment behavior.

## Demoable Units of Work

### Unit 1: Manual Workflow Entry and Safety Gate

**Purpose:** Define how a maintainer starts the workflow and what safety controls must pass before infrastructure changes are allowed to execute.

**Functional Requirements:**
- The system shall define one GitHub Actions workflow triggered by `workflow_dispatch`.
- The system shall scope the workflow to applying the existing `infra/terraform/app/dev` stack for the `dev` environment only.
- The system shall allow applies only when the workflow runs from the `main` branch.
- The system shall require a typed confirmation input that clearly indicates intent to apply the `dev` environment.
- The system shall require a GitHub environment approval gate before any job that can apply infrastructure gains access to protected environment configuration.
- The workflow shall use an environment configuration that supports reviewer approval and should document that self-approval prevention is preferred when repository settings permit it.

**Proof Artifacts:**
- `File:` workflow YAML demonstrates `workflow_dispatch`, `main`-only behavior, typed confirmation input, and environment gating
- `Screenshot:` GitHub Actions manual run form demonstrates the operator-facing inputs required to start the workflow
- `Screenshot:` GitHub Actions review gate demonstrates the job waits for environment approval before apply can proceed

### Unit 2: OIDC Authentication and Terraform Plan Contract

**Purpose:** Define the non-interactive AWS authentication and Terraform planning sequence that prepares an exact reviewed plan for apply.

**Functional Requirements:**
- The system shall authenticate to AWS through GitHub OIDC rather than repository-stored long-lived AWS access keys.
- The workflow shall request only the GitHub Actions permissions required for checkout, OIDC token issuance, and normal workflow execution.
- The system shall initialize Terraform against the existing remote backend contract used by `infra/terraform/app/dev`.
- The system shall run Terraform plan in automation mode and save the resulting plan to a file artifact for later apply.
- The system shall make the reviewed plan output available to maintainers through workflow logs and saved artifacts without requiring live shell access to the runner.
- The workflow shall keep backend and environment configuration externalized rather than hard-coding secret values in reusable Terraform source.

**Proof Artifacts:**
- `File:` workflow YAML demonstrates `id-token: write`, AWS credential configuration via OIDC, and Terraform `init` plus `plan -out`
- `CLI:` sanitized workflow log output demonstrates Terraform initialization and plan creation succeed for the `dev` stack
- `Artifact:` saved Terraform plan and related workflow artifact metadata demonstrate the exact reviewed plan is preserved for apply

### Unit 3: Exact Plan Apply and Run Safety Contract

**Purpose:** Define how the approved workflow applies infrastructure safely and predictably without recalculating a different plan at apply time.

**Functional Requirements:**
- The system shall apply the exact previously saved Terraform plan artifact rather than running a fresh implicit plan during apply.
- The system shall keep plan and apply execution compatible with HashiCorp automation guidance for saved plans, including preserving the required working-directory context.
- The workflow shall prevent overlapping apply executions against the same `dev` stack through a single concurrency boundary.
- The system shall surface success or failure clearly in workflow logs and job status so maintainers can determine whether the dev stack changed successfully.
- The workflow shall fail clearly when confirmation input is invalid, required configuration is missing, the reviewed plan artifact is unavailable, or Terraform apply exits non-zero.

**Proof Artifacts:**
- `File:` workflow YAML demonstrates concurrency control, artifact handoff, and `terraform apply <saved-plan>`
- `CLI:` sanitized workflow log output demonstrates apply consumes the saved plan artifact rather than re-planning
- `Screenshot:` completed workflow run demonstrates clear pass or fail reporting for the operator

## Non-Goals (Out of Scope)

1. [**Application deployment**: This spec does not build or push container images, update ECS task definition revisions, or roll out the application service.]
2. [**Destroy workflow**: This spec does not define manual destroy behavior, destroy approval rules, or destroy IAM permissions.]
3. [**OIDC IAM implementation details**: This spec consumes the GitHub OIDC trust model but does not redefine the separate IAM trust-policy spec beyond the workflow-facing requirements it depends on.]

## Design Considerations

No specific design requirements identified.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing workflow or contract test first, minimum workflow and documentation changes second, refactor third.
- Keep infrastructure workflow behavior aligned with the existing Terraform layout under `infra/terraform/app/dev`, the remote-state expectations established in [25-spec-bootstrap-terraform-remote-state-dev-environment.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/25-spec-bootstrap-terraform-remote-state-dev-environment/25-spec-bootstrap-terraform-remote-state-dev-environment.md), and the local validation posture documented in [infra/terraform/floci/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/floci/README.md).
- Preserve the existing documentation-first spec workflow under `docs/specs/` and the conventional commit expectations described in [AGENTS.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/AGENTS.md) and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).
- Any implementation spawned from this spec should keep proof artifacts reviewer-friendly and sanitized, especially for workflow logs, Terraform output, and environment-backed configuration.
- Any implementation should stay consistent with the repository's existing pattern of narrow, contract-oriented infrastructure specs rather than bundling unrelated CI/CD behavior into one workflow.

## Technical Considerations

- Current repository context already defines the `infra/terraform/app/dev` stack, remote backend usage, and `dev`-only infrastructure direction. This workflow should target that stack directly rather than inventing a second infrastructure entrypoint.
- Current GitHub Actions guidance supports `workflow_dispatch` inputs and environment approval gates. This spec should use both because they address different risks: typed confirmation reduces accidental execution, while environment review enforces explicit human approval before protected deployment jobs proceed.
- Current GitHub OIDC guidance for AWS requires `id-token: write` and recommends narrow trust boundaries. This workflow should therefore assume a role through OIDC, avoid long-lived AWS keys, and stay limited to `main` and `dev`.
- Current AWS IAM guidance for GitHub OIDC recommends constraining `token.actions.githubusercontent.com:aud` to `sts.amazonaws.com` and tightly scoping `sub`. The workflow should be designed so the downstream IAM trust can remain narrow and reviewable.
- Current HashiCorp automation guidance recommends `terraform init -input=false`, `terraform plan -out=tfplan -input=false`, and `terraform apply -input=false tfplan` when human review of an exact plan matters. This spec should therefore require saved-plan apply behavior rather than `terraform apply -auto-approve` without a saved plan.
- HashiCorp guidance also warns that saved plans depend on matching files, plugins, platform, and working-directory context. The implementation should therefore preserve the plan artifact together with the required execution context instead of assuming plan and apply can be recomputed interchangeably.
- Current GitHub Actions guidance supports concurrency controls for deployment-style workflows. This workflow should define one concurrency boundary for the `dev` apply path so overlapping runs do not compete for shared remote state.
- This spec intentionally preserves the existing repository direction of remote Terraform state and operator-controlled AWS changes rather than shifting to HCP Terraform or automatic apply-on-merge behavior.

## Security Considerations

- The workflow shall use GitHub OIDC to obtain short-lived AWS credentials and shall not require long-lived AWS access keys in repository secrets.
- The workflow shall keep the AWS trust boundary narrow enough that unauthorized branches or repositories cannot assume the apply role.
- Environment-protected values and any backend configuration details shall remain outside committed reusable Terraform source.
- Proof artifacts shall not expose populated secrets, raw credentials, sensitive backend contents, or unnecessary account identifiers.
- Applying a reviewed saved plan is a safety control because it reduces the risk that the executed infrastructure changes differ from the reviewed plan output.

## Success Metrics

1. [**Manual apply readiness**: A maintainer can start one GitHub Actions workflow from `main`, pass the required safety gates, and apply the `dev` Terraform stack without using local AWS credentials.]
2. [**Review fidelity**: The workflow applies the exact saved Terraform plan that reviewers approved rather than a newly calculated plan.]
3. [**Security posture**: The workflow uses GitHub OIDC with narrow trust assumptions and does not depend on long-lived AWS keys stored in GitHub secrets.]
4. [**Operational safety**: Overlapping `dev` apply runs are prevented and workflow failures surface clear operator-visible diagnostics.]

## Open Questions

No open questions at this time.
