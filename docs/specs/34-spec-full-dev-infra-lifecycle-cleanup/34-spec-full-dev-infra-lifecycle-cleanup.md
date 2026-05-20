# 34-spec-full-dev-infra-lifecycle-cleanup.md

## Introduction/Overview

The current dev infrastructure lifecycle has a circular dependency: the main app stack creates the GitHub OIDC roles that later workflows depend on, which makes normal destroy and rebuild behavior unsafe and makes full cleanup awkward. This feature defines a layered Terraform and workflow design so the repository can support normal `app/dev` rebuilds through GitHub OIDC while still allowing a final repo-owned teardown that removes every POC AWS resource, including IAM roles, the OIDC provider, the S3 backend bucket, and the DynamoDB lock table.

## Goals

- Split the current Terraform ownership model into separate `state/dev`, `identity/dev`, and `app/dev` layers with clear lifecycle boundaries.
- Allow operators to destroy and recreate `app/dev` through GitHub OIDC workflows without reintroducing bootstrap credentials.
- Provide a repo-owned bootstrap workflow that can create the foundation layers and a repo-owned bootstrap destroy workflow that can remove them.
- Preserve a final zero-footprint teardown path for this POC, including GitHub configuration cleanup guidance.
- Keep the workflow contracts reviewable, testable, and documented for junior developers and maintainers.

## User Stories

- **As an operator maintaining the dev POC**, I want application infrastructure to be rebuildable without re-running full bootstrap so that routine testing and iteration stay fast and predictable.
- **As a maintainer responsible for cloud security**, I want normal apply, destroy, publish, and deploy workflows to use GitHub OIDC roles so that day-to-day automation does not depend on personal AWS credentials.
- **As a maintainer closing down the POC**, I want a repository-owned final teardown path so that I can remove all AWS resources and leave no cloud footprint behind.
- **As a reviewer approving infrastructure automation**, I want clear layer boundaries and proof artifacts so that I can verify what is created, what survives normal app teardown, and what is removed during final cleanup.

## Demoable Units of Work

### Unit 1: Extract Foundation Identity Layer

**Purpose:** Separate long-lived GitHub OIDC identity resources from the app stack so normal app rebuilds do not destroy the workflow roles they need.

**Functional Requirements:**

- The system shall define a dedicated `infra/terraform/identity/dev` stack that owns the GitHub OIDC provider and the GitHub-assumable IAM roles for Terraform apply, Terraform destroy, app publish, and app deploy.
- The system shall remove ownership of those GitHub OIDC identity resources from `infra/terraform/app/dev`.
- The system shall continue exporting the role ARNs needed by downstream workflows from the identity stack.
- The system shall document that `state/dev` owns backend resources, `identity/dev` owns GitHub workflow identity resources, and `app/dev` owns application runtime infrastructure.

**Proof Artifacts:**

- `Test:` contract tests pass demonstrating the identity resources are defined in `identity/dev` and no longer in `app/dev`.
- `CLI:` targeted Terraform validation output for `identity/dev` demonstrates the new stack is syntactically valid.
- `Documentation:` updated lifecycle and ownership docs demonstrate the three-stack boundary.

### Unit 2: Restore Normal OIDC-Based App Rebuilds

**Purpose:** Make `app/dev` independently destroyable and recreatable through normal GitHub OIDC workflows while `state/dev` and `identity/dev` remain in place.

**Functional Requirements:**

- The system shall keep `Terraform Apply Dev` limited to `app/dev` infrastructure changes that assume the protected `dev` environment role from the identity stack.
- The system shall provide a repository-owned `Terraform Destroy Dev` workflow for `app/dev` that assumes the protected `dev-destroy` environment role from the identity stack.
- The system shall ensure `app/dev` destroy does not delete the backend bucket, backend lock table, GitHub OIDC provider, or GitHub workflow IAM roles.
- The system shall preserve the GitHub configuration contract so the apply, destroy, publish, and deploy workflows continue consuming stable variable names.

**Proof Artifacts:**

- `Workflow file:` `terraform-destroy-dev.yml` demonstrates a repo-owned OIDC destroy path exists for `app/dev`.
- `Test:` workflow and documentation contract tests pass demonstrating app destroy is separate from foundation teardown.
- `Documentation:` lifecycle guide describes a normal destroy-and-recreate sequence for `app/dev` using OIDC only.

### Unit 3: Add Repo-Owned Bootstrap and Final Foundation Teardown Workflows

**Purpose:** Provide repository-owned workflows for first-time bootstrap and last-time teardown of the foundation layers that cannot be managed safely by the normal OIDC app workflows.

**Functional Requirements:**

- The system shall keep a protected `dev-bootstrap` environment for workflows that use admin-backed bootstrap AWS credentials.
- The system shall provide one repo-owned bootstrap workflow that creates or updates `state/dev`, `identity/dev`, and then `app/dev` in the correct order.
- The system shall provide one repo-owned bootstrap destroy workflow that destroys `app/dev` if needed, then destroys `identity/dev`, and finally destroys `state/dev` in the correct order.
- The system shall require explicit typed confirmation and `main`-branch restriction for both bootstrap create and bootstrap destroy workflows.
- The system shall keep bootstrap credentials in protected `dev-bootstrap` GitHub environment secrets rather than workflow dispatch inputs.

**Proof Artifacts:**

- `Workflow file:` bootstrap and bootstrap-destroy workflow YAML demonstrate the protected environment, typed confirmation, branch guard, and secret usage.
- `Test:` contract tests pass demonstrating the bootstrap destroy workflow follows the required teardown order and does not use dispatch inputs for secrets.
- `Documentation:` operator guidance describes the bootstrap and final teardown sequences end to end.

### Unit 4: Define Final Cleanup Handoff and GitHub Configuration Reset

**Purpose:** Make the end of the POC auditable by specifying what happens to GitHub configuration after AWS resources are removed.

**Functional Requirements:**

- The system shall document the exact GitHub Actions variables and secrets used by the lifecycle workflows.
- The system shall define final cleanup instructions that set the AWS-derived GitHub variable values to empty strings after the final teardown completes.
- The system shall define final cleanup instructions that preserve the variable names for future reuse while making clear that the values are intentionally blank after teardown.
- The system shall document that `dev-bootstrap` secrets persist in the repository by design and represent a standing bootstrap exception for this POC.

**Proof Artifacts:**

- `Documentation:` cleanup section demonstrates the final GitHub variable reset sequence and persistent bootstrap-secret policy.
- `Screenshot:` GitHub environment or workflow summary demonstrates the named variables and environments expected by the lifecycle design.
- `Test:` documentation contract tests pass demonstrating the cleanup handoff is explicitly documented.

## Non-Goals (Out of Scope)

1. **Least-privilege IAM redesign**: This spec does not require a full least-privilege rewrite of the broad POC Terraform IAM policy.
2. **Production-grade secret rotation automation**: This spec does not add automatic rotation or external secret-manager integration for the persistent bootstrap credentials.
3. **ECS deployment feature expansion**: This spec does not redesign the application deploy workflow beyond whatever variable and role rewiring is required by the stack split.

## Design Considerations

No specific UI design requirements are identified. Workflow summaries and operator-facing documentation should present the create, rebuild, destroy, and final cleanup order clearly enough that a maintainer can follow the sequence without inferring hidden steps.

## Repository Standards

- Follow the repository's strict TDD requirement: add failing contract or documentation tests before changing Terraform, workflows, or docs.
- Follow the existing spec-driven documentation pattern in `docs/specs/` and the existing Terraform contract-test style under `src/test/java/org/springframework/samples/petclinic/system`.
- Follow the established Terraform documentation pattern in `infra/terraform/.../README.md` files, especially for lifecycle boundaries, proof-oriented wording, and explicit scope statements.
- Keep workflow contracts manual, typed-confirmation based, and protected by GitHub environments, consistent with `Terraform Apply Dev` and `Manual Dev ECR Publish`.
- Use conventional commits for any eventual implementation work.

## Technical Considerations

- Current GitHub Actions workflow syntax documents `workflow_dispatch` input types as `boolean`, `choice`, `number`, `environment`, and `string`; secrets are not a dispatch input type, so bootstrap credentials must remain GitHub environment secrets rather than manual inputs.
- Current GitHub environment guidance supports required reviewers and optional prevent-self-review rules, and environment secrets are only available after configured protection rules pass. The lifecycle workflows should continue using protected environments to gate access to OIDC roles and bootstrap credentials.
- Current Terraform S3 backend guidance recommends partial backend configuration. The split-stack design should continue materializing backend configuration files rather than hardcoding backend values in shared Terraform source.
- `state/dev` must remain the only stack allowed to create or destroy the backend S3 bucket and DynamoDB lock table.
- `identity/dev` should become the only stack allowed to create or destroy the GitHub OIDC provider and GitHub workflow IAM roles.
- `app/dev` should consume foundation outputs and must remain independently destroyable without deleting foundation resources.
- The final foundation teardown workflow should enforce destroy ordering: `app/dev` first when present, then `identity/dev`, then `state/dev`.
- The cleanup behavior of setting GitHub variable values to empty strings is an intentional deviation from the more common "delete the variable" cleanup approach because the user wants stable variable names preserved for future reuse.

## Security Considerations

- Normal apply, destroy, publish, and deploy workflows shall use GitHub OIDC roles rather than long-lived AWS access keys.
- The `dev-bootstrap` environment contains persistent admin-backed AWS credentials by explicit user choice. This is a higher-risk standing exception and shall be called out clearly in documentation and proof artifacts.
- Bootstrap and bootstrap-destroy workflows shall use protected environments, typed confirmations, and `main`-branch restrictions to reduce accidental or unreviewed execution.
- Workflow dispatch inputs shall not be used for secret material.
- Proof artifacts shall avoid exposing AWS secret values, session tokens, or any unsanitized sensitive configuration.

## Success Metrics

1. **Normal rebuild success**: a maintainer can destroy and recreate `app/dev` through repo-owned GitHub OIDC workflows without re-entering bootstrap credentials.
2. **Full teardown completeness**: a maintainer can run the documented final teardown path and remove all POC AWS resources, including backend, identity, and runtime layers.
3. **Lifecycle clarity**: repository docs and contract tests make the ownership boundaries, workflow entry points, and GitHub cleanup sequence explicit enough that another maintainer can follow the process without tribal knowledge.

## Open Questions

1. Should the final bootstrap destroy workflow actively blank GitHub variable values through the GitHub API, or should it only emit explicit operator instructions for that reset step?
2. Should the persistent `dev-bootstrap` credentials stay scoped to one environment forever, or should a later hardening pass move them to short-lived credentials issued by a broker outside GitHub?
