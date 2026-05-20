# 33-spec-manual-dev-ecr-publish-workflow.md

## Introduction/Overview

This feature defines the manual GitHub Actions workflow contract that builds and publishes the application container image to Amazon ECR for the `dev` environment. The goal is to produce a traceable, immutable image artifact without coupling image publication to ECS rollout or broad Terraform privileges, while keeping the workflow reviewable and consistent with the repository's existing GitHub OIDC and container contracts.

## Goals

- Define one manual GitHub Actions workflow that publishes the application image to the existing `dev-petclinic` ECR repository.
- Require GitHub OIDC-based AWS authentication through a dedicated publish role instead of long-lived AWS credentials.
- Publish exactly one immutable Git SHA image tag per successful workflow run.
- Preserve a clear operator-facing safety model with `main`-only execution, typed confirmation, protected `dev` environment access, and explicit concurrency behavior.
- Produce proof artifacts that let maintainers verify the workflow run, the pushed image reference, and the resulting ECR image presence.

## User Stories

- **As a maintainer**, I want to publish a dev application image manually from GitHub Actions so that image creation remains operator-controlled instead of occurring automatically on every merge.
- **As a platform engineer**, I want image publishing to use a narrow GitHub OIDC role so that ECR push authority stays separate from Terraform mutation and ECS rollout authority.
- **As a reviewer**, I want every published image to be traceable back to the exact Git commit that produced it so that later deployment work can reference immutable artifacts confidently.
- **As a future workflow author**, I want the manual image-publish contract documented clearly so that later ECS rollout automation can consume a stable image artifact contract without re-deciding workflow boundaries.

## Demoable Units of Work

### Unit 1: Manual Workflow Entry and Safety Boundary

**Purpose:** Define how a maintainer starts image publication and which controls must pass before AWS-backed publishing can occur.

**Functional Requirements:**

- The system shall define one GitHub Actions workflow triggered by `workflow_dispatch`.
- The system shall allow image publication only when the workflow runs from the `main` branch.
- The system shall require a typed confirmation input that clearly indicates intent to publish the dev application image.
- The system shall use the protected `dev` GitHub environment for the AWS-backed publish job.
- The system shall prevent overlapping dev image-publish executions through one workflow concurrency boundary with queued rather than canceled runs.

**Proof Artifacts:**

- `File:` workflow YAML demonstrates `workflow_dispatch`, `main`-only behavior, typed confirmation, environment usage, and concurrency settings.
- `Screenshot:` GitHub Actions manual run form demonstrates the operator-visible publish input required to start the workflow.
- `Screenshot:` GitHub Actions job review gate or environment usage details demonstrate the publish job is bound to the protected `dev` environment.

### Unit 2: Build and Authentication Contract

**Purpose:** Define the application build path and AWS authentication sequence used before any image can be pushed.

**Functional Requirements:**

- The system shall run the repository's Maven wrapper package path before image publication.
- The system shall use `./mvnw package -DskipTests` as the pre-push JVM build contract for this workflow.
- The system shall authenticate to AWS through GitHub OIDC rather than repository-stored long-lived AWS access keys.
- The system shall use a dedicated environment-scoped role variable named `APP_PUBLISH_ROLE_ARN`.
- The workflow shall request only the GitHub Actions permissions required for repository checkout, OIDC token issuance, and normal workflow execution.

**Proof Artifacts:**

- `File:` workflow YAML demonstrates Maven packaging, `id-token: write`, and AWS credential configuration through the publish role variable.
- `CLI:` sanitized workflow log output demonstrates the Maven package step and AWS credential configuration succeed before image push.
- `Documentation:` GitHub variable contract table demonstrates `APP_PUBLISH_ROLE_ARN` and `AWS_REGION` ownership and expected scope.

### Unit 3: Immutable ECR Publication and Traceability Contract

**Purpose:** Define exactly what image reference is published and how operators verify that the result is immutable and reviewable.

**Functional Requirements:**

- The system shall build the container image from the repository-owned root `Dockerfile`.
- The system shall push the image only to the existing `dev-petclinic` ECR repository contract consumed through the Terraform-exported repository URI.
- The system shall tag the published image with the full Git commit SHA from the workflow run.
- The system shall not publish mutable convenience tags such as `latest` or `main` in v1.
- The workflow shall surface the fully qualified published image reference and pushed digest in workflow-visible output.
- The workflow shall keep ECS task-definition registration and ECS service updates out of scope for this workflow.

**Proof Artifacts:**

- `File:` workflow YAML demonstrates Docker build and ECR push behavior tied to the immutable SHA tag only.
- `CLI:` workflow logs or job summary demonstrate the fully qualified image reference and pushed digest for the completed run.
- `CLI:` ECR verification command output demonstrates the expected SHA-tagged image exists in the target repository after the workflow completes.

## Non-Goals (Out of Scope)

1. [**Automatic publish on merge**: This spec does not publish images automatically on `push` to `main`; manual dispatch is the only approved trigger for v1.]
2. [**ECS rollout automation**: This spec does not register ECS task-definition revisions, update ECS services, or wait for service rollout status.]
3. [**Mutable tag aliases**: This spec does not add `latest`, `main`, or any other mutable convenience tag policy.]

## Design Considerations

No specific design requirements identified.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing workflow or contract tests first, minimum workflow and supporting documentation changes second, refactor third.
- Preserve the repository's current Maven-first build reality for CI by using the root `pom.xml` and Maven wrapper rather than inventing a parallel Gradle workflow path.
- Keep infrastructure and deployment workflow behavior aligned with the existing GitHub Actions patterns in [.GitHub/workflows/terraform-apply-dev.yml](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/.github/workflows/terraform-apply-dev.yml), including explicit guards, environment usage, and readable operator diagnostics.
- Reuse the container and infrastructure contracts already documented in [24-spec-production-container-contract.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/24-spec-production-container-contract/24-spec-production-container-contract.md), [32-spec-GitHub-oidc-iam.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-spec-github-oidc-iam.md), and [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md) instead of redefining those contracts inside the workflow.
- Preserve documentation-first spec workflow expectations under `docs/specs/` and conventional commit expectations from [AGENTS.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/AGENTS.md) and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).

## Technical Considerations

- Scope assessment: this feature is the right size for a standalone spec because it defines one reviewable CI/CD slice that can be validated independently from ECS rollout and Terraform apply/destroy behavior.
- Clarification status: sufficient - no questions file required.
- Current repository context already defines the stable ECR repository contract as private repository `dev-petclinic`, immutable tags only, and Terraform-exported `repository_uri` / `repository_name` outputs. This workflow should consume that contract rather than inventing repository names or mutable aliases.
- Current repository context already defines a repository-owned root `Dockerfile` and Maven-based container build contract. This workflow should run the Maven wrapper package step explicitly before Docker publish so the CI signal remains consistent with the repository's established JVM build path.
- Current GitHub Actions guidance is a living document and supports `workflow_dispatch`, environment-bound jobs, configuration variables, and concurrency controls. This workflow should therefore use manual dispatch, protected `dev` environment scoping, environment-backed configuration, and one explicit concurrency group for the dev publish path.
- Current GitHub and AWS OIDC guidance is a living document and recommends `id-token: write` plus narrow role assumptions instead of long-lived AWS credentials. This workflow should therefore consume a dedicated publish role through `APP_PUBLISH_ROLE_ARN` rather than broadening Terraform roles or reusing the ECS rollout role.
- Current Docker publishing guidance from GitHub recommends purpose-built login and build/push actions for reviewable workflow behavior. This spec should assume standard GitHub Actions-based Docker build and push steps rather than custom shell-heavy Docker orchestration.
- Current Amazon ECR guidance states that immutable repositories reject attempts to overwrite an existing tag. This workflow should therefore publish each image under a unique Git SHA tag and should not attempt to maintain a mutable convenience tag in v1.
- The repository's existing `dev` environment pattern is intentionally reused here even though the trigger is manual rather than automatic. This keeps AWS configuration ownership and approval behavior aligned with the current IAM trust model and Terraform workflow posture.

## Security Considerations

- The workflow shall use short-lived AWS credentials obtained through GitHub OIDC and shall not require long-lived `AWS_ACCESS_KEY_ID` or `AWS_SECRET_ACCESS_KEY` values in repository or environment secrets.
- The publish workflow shall use a dedicated AWS role so ECR push permissions stay separate from Terraform apply/destroy permissions and from later ECS rollout permissions.
- The workflow shall keep AWS-sensitive values in GitHub environment or repository variables according to their ownership boundary and shall not hard-code role ARNs, account-specific repository URIs, or credentials in workflow source.
- Proof artifacts shall not expose credentials, unmasked Docker login data, unnecessary AWS account identifiers, or any secret values in committed docs or workflow screenshots.
- The workflow shall avoid publishing mutable tags in v1 because mutable aliases weaken artifact traceability and can blur which image was actually reviewed or deployed.

## Success Metrics

1. [**Manual publish readiness**: A maintainer can start one GitHub Actions workflow from `main`, satisfy the typed confirmation and environment boundary, and push a dev image to ECR without local AWS credentials.]
2. [**Artifact traceability**: Every successful workflow run publishes exactly one Git SHA-tagged image and exposes a workflow-visible image reference plus digest that can be traced back to the originating commit.]
3. [**Security posture**: The workflow uses GitHub OIDC with a dedicated publish role and does not depend on long-lived AWS access keys or broader Terraform role reuse.]

## Open Questions

1. Whether the eventual implementation should add a repository-owned workflow verification script similar to existing Terraform and IAM verification scripts remains open for task-planning.
