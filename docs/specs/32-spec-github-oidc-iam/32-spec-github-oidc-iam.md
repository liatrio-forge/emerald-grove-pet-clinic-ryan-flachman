# 32-spec-GitHub-oidc-iam.md

## Introduction/Overview

This feature defines the AWS IAM trust and permission contract used by GitHub Actions workflows in the AWS ECS and Terraform proof of concept. The primary goal is to let GitHub Actions assume short-lived AWS roles through GitHub OIDC without long-lived AWS access keys, while keeping the trust boundary explicit, reviewable, and narrow enough for the existing `dev` environment workflows.

## Goals

- Define one reviewable GitHub OIDC trust model for AWS that downstream workflows can consume consistently.
- Separate AWS access by workflow responsibility so apply, destroy, and deploy do not share one broad GitHub role.
- Require explicit trust scoping by repository, audience, and exact GitHub subject rather than repo-wide wildcard trust.
- Keep Terraform workflow permissions broad enough for the POC to move forward without granting full administrator access.
- Define the GitHub environment and variable contract needed for later workflow implementations.

## User Stories

- **As a platform maintainer**, I want GitHub Actions to assume AWS roles through OIDC so that the repository does not depend on long-lived AWS credentials.
- **As a security reviewer**, I want the IAM trust policy to be explicit about which repository, branch, and environment can assume each role so that accidental trust expansion is easy to detect.
- **As an operator**, I want apply, destroy, and deploy responsibilities separated into different roles so that one workflow does not automatically inherit every AWS permission needed by another workflow.
- **As a future workflow author**, I want the role names, trust subjects, and GitHub configuration contract documented clearly so later workflow specs can reuse the same security model without re-deciding identity boundaries.

## Demoable Units of Work

### Unit 1: Shared GitHub OIDC Provider and Trust-Policy Baseline

**Purpose:** Define the common AWS trust-policy rules that every GitHub-assumable workflow role must inherit so the repository has one stable OIDC security baseline.

**Functional Requirements:**

- The system shall use GitHub OIDC with the AWS IAM identity provider `token.actions.githubusercontent.com`.
- The system shall require the OIDC audience claim `aud` to equal `sts.amazonaws.com` for every GitHub-assumable AWS role.
- The system shall require explicit `sub` claim matching for every GitHub-assumable AWS role and shall not allow repo-wide wildcard trust such as `repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:*`.
- The system shall scope every trusted subject to the repository `liatrio-forge/emerald-grove-pet-clinic-ryan-flachman`.
- The system shall use `sub` and `aud` claim constraints as the v1 trust boundary and shall not require additional claim locking such as `workflow` or `job_workflow_ref` in v1.

**Proof Artifacts:**

- `Documentation:` trust-policy examples demonstrate exact `aud` and exact `sub` constraints for each role
- `File:` IAM policy or Terraform contract tests demonstrate broad wildcard repo trust is excluded
- `CLI:` sanitized policy rendering or plan output demonstrates the GitHub OIDC provider and role trust conditions are defined explicitly

### Unit 2: Separate Apply and Destroy Terraform Roles

**Purpose:** Define distinct Terraform roles for safe infrastructure mutation so manual apply and manual destroy do not share the same GitHub trust subject or operational blast radius.

**Functional Requirements:**

- The system shall define one Terraform apply role for the `dev` environment and one separate Terraform destroy role for destructive cleanup.
- The system shall scope the Terraform apply role to environment-bound GitHub jobs using the exact GitHub subject for the protected `dev` environment.
- The system shall scope the Terraform destroy role to a separate protected GitHub environment using the exact GitHub subject for `dev-destroy`.
- The system shall require destroy to remain stricter than apply by using both a separate role and a separate protected environment.
- The system shall allow the Terraform apply and destroy roles to use broad POC-oriented permissions for the `dev` stack, but shall not grant unconstrained administrator-level access to the AWS account.
- The system shall document any IAM actions required by Terraform explicitly rather than leaving IAM mutation rights implied.

**Proof Artifacts:**

- `Documentation:` role matrix demonstrates separate apply and destroy role names, trusted subjects, and high-level permission boundaries
- `File:` IAM policy definitions or Terraform contract tests demonstrate apply and destroy do not share one role
- `Screenshot:` GitHub environment settings for `dev` and `dev-destroy` demonstrate the intended protection boundary exists

### Unit 3: Narrow App Deploy Role for ECS Rollout

**Purpose:** Define a separate AWS role for application deployment so the automatic ECS rollout path can stay narrower than the Terraform mutation path.

**Functional Requirements:**

- The system shall define one application deploy role separate from the Terraform apply and Terraform destroy roles.
- The system shall scope the deploy role to protected `dev` environment jobs rather than unrestricted repository jobs.
- The system shall assume the deploy workflow originates from `main`, but AWS access shall still be granted only through the protected `dev` environment subject.
- The system shall keep deploy permissions limited to the ECS rollout path and related read operations needed to register task-definition revisions, update the ECS service, and observe rollout status.
- The system shall not reuse the broad Terraform role for application deploy responsibilities.

**Proof Artifacts:**

- `Documentation:` deploy-role contract demonstrates the exact trusted subject and narrower deploy permission intent
- `File:` role-policy contract test demonstrates deploy permissions remain separate from Terraform mutation permissions
- `CLI:` sanitized workflow or policy output demonstrates the deploy role ARN is a distinct GitHub configuration input

### Unit 4: GitHub Configuration and Workflow-Consumption Contract

**Purpose:** Define the GitHub-side variables, environments, and workflow assumptions that later workflow specs must consume exactly.

**Functional Requirements:**

- The system shall use protected GitHub environment configuration as the primary home for role ARNs and deployment-sensitive AWS values.
- The system shall allow only non-sensitive, repository-stable defaults to remain at repository variable scope.
- The system shall define the required environment names `dev` and `dev-destroy` as part of the public contract for downstream workflows.
- The system shall define the minimum GitHub configuration inputs needed by downstream workflows, including `AWS_REGION`, the Terraform apply role ARN, the Terraform destroy role ARN, the app deploy role ARN, and backend-state variables where relevant.
- The system shall prohibit storing long-lived AWS access keys in GitHub repository or environment secrets for these workflows.
- The system shall require any GitHub Actions job that assumes AWS credentials to declare the protected environment whose exact subject the role trust permits.

**Proof Artifacts:**

- `Documentation:` GitHub configuration table demonstrates required variables, owning scope, and consuming workflow responsibility
- `File:` workflow contract tests demonstrate AWS-assuming jobs declare `id-token: write`, the expected environment, and the expected role variable name
- `Screenshot:` GitHub Actions environment or variable configuration demonstrates the protected configuration model is reviewable by maintainers

## Non-Goals (Out of Scope)

1. [**Workflow YAML implementation**: This spec defines the IAM and trust contract consumed by workflows, but it does not fully implement the apply, destroy, or deploy workflow YAML files.]
2. [**Application runtime task role design**: This spec does not redesign the ECS task runtime role used by the application container itself.]
3. [**Fine-grained least-privilege Terraform authoring**: This spec does not require a fully minimized action-by-action Terraform policy for the POC, though it does forbid full administrator shortcuts.]

## Design Considerations

No specific design requirements identified.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing contract tests or workflow-structure tests first, minimum IAM or workflow changes second, refactor third.
- Keep infrastructure work aligned with the repository's current Terraform layout under `infra/terraform/app/dev` and the local infrastructure validation posture documented in [infra/terraform/floci/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/floci/README.md).
- Preserve the documentation-first spec workflow already used under `docs/specs/`, including concise proof artifacts and narrow scope boundaries similar to [31-spec-manual-terraform-apply.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/31-spec-manual-terraform-apply/31-spec-manual-terraform-apply.md).
- Follow the conventional commit and repository-governance expectations described in [AGENTS.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/AGENTS.md) and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).
- Keep proof artifacts sanitized so no committed evidence includes live AWS credentials, raw state contents, or unnecessary account identifiers.

## Technical Considerations

- Scope assessment: this feature is the right size for a standalone spec because it defines one security contract that unblocks multiple later workflow specs without bundling all CI/CD implementation into one oversized change.
- Clarification status: sufficient - no questions file required.
- Current GitHub guidance for AWS OIDC is a living document and recommends using `id-token: write`, audience `sts.amazonaws.com`, and claim-based conditions so only predictable GitHub runs can receive cloud credentials. This spec should therefore require exact `aud` matching and tight `sub` scoping for every role.
- Current AWS IAM guidance for GitHub OIDC is also a living document and explicitly recommends evaluating `token.actions.githubusercontent.com:sub` in every trusting role. AWS additionally rejects trust configurations where the GitHub `sub` condition is missing or reduced to a pure wildcard. This spec should therefore forbid broad repository wildcard subjects and require reviewer-readable exact subjects.
- Current GitHub guidance notes that AWS does not support custom claims for GitHub OIDC in this integration. This spec should therefore keep v1 trust conditions limited to `aud` and `sub` instead of depending on unsupported custom-claim designs.
- The repository already commits to separate apply, destroy, and deploy workflows. This spec should preserve that workflow separation at the IAM-role layer instead of allowing one broad GitHub role to become a hidden coupling point.
- The repository already defines `main` as the reviewed source branch for manual apply and future deploy flows. This spec should preserve that direction, but the trusted GitHub subject for AWS access should be environment-based for apply and deploy so all AWS-assuming jobs can be covered by exact protected-environment subjects.
- The Terraform apply and destroy roles may remain broad at power-user scope for the POC so implementation is not blocked on exhaustive least-privilege authoring. However, the spec should require those permissions to stay below unconstrained account administrator access and should call out IAM-sensitive actions explicitly.
- The deploy role should be narrower than the Terraform roles because ECS rollout needs a smaller action surface than Terraform infrastructure mutation. The implementation should avoid reusing Terraform roles for deployment convenience.
- The GitHub-side configuration contract should prefer protected environment variables for role ARNs and other deployment-sensitive values because that aligns trust subjects, reviewer controls, and configuration ownership in one place.

## Security Considerations

- These workflows shall use short-lived AWS credentials obtained through GitHub OIDC and shall not use long-lived AWS access keys stored in GitHub secrets.
- Trust policies shall be explicit about allowed repository and subject values so unrelated branches, repositories, or environments cannot assume the roles.
- Apply, destroy, and deploy shall use separate roles so compromise or misuse of one workflow path does not automatically grant all AWS permissions needed by another path.
- Destroy shall remain stricter than apply by using a separate protected environment and separate trust subject because destroy is inherently destructive.
- Proof artifacts shall not include populated credentials, raw Terraform state, or trust-policy examples copied from live accounts without sanitization.
- Broad Terraform POC permissions are an intentional temporary compromise. The spec shall document that this is a bounded exception for delivery speed, not the long-term least-privilege target.

## Success Metrics

1. [**OIDC adoption**: Downstream workflows can assume AWS roles through GitHub OIDC with no long-lived AWS access keys required in GitHub configuration.]
2. [**Trust narrowness**: Every GitHub-assumable AWS role uses explicit `aud = sts.amazonaws.com` and exact repository-bound `sub` matching, with no repo-wide wildcard trust.]
3. [**Role separation**: Apply, destroy, and deploy each consume distinct AWS roles with reviewer-readable trust subjects and documented permission boundaries.]
4. [**Workflow readiness**: Later apply, destroy, and deploy workflow specs can reference one stable set of environment names, role variables, and trust assumptions without inventing new security decisions.]

## Open Questions

1. Whether the eventual Terraform implementation should encode these IAM roles in the existing `infra/terraform/app/dev` stack or in a separate CI/IAM Terraform stack remains open for the implementation task-planning stage.
