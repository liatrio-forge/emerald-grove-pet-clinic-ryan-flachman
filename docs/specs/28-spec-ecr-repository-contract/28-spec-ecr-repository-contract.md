# 28-spec-ecr-repository-contract.md

## Introduction/Overview

This feature defines the Amazon ECR repository contract for the dev AWS proof of concept. The primary goal is to make image publishing, retention, CI consumption, and destroy behavior explicit before ECS service rollout depends on repository assumptions that are currently implicit.

## Goals

- Define one deterministic private ECR repository target for the application in `dev`
- Keep image tagging behavior aligned with immutable Git SHA deployment artifacts
- Define an intentional lifecycle policy that bounds retained images instead of allowing unbounded growth
- Define the repository outputs and naming contract that CI and later ECS specs can consume without guessing
- Define destroy-time behavior so Terraform teardown is predictable even when images remain in the repository

## User Stories

- **As a platform engineer**, I want one stable ECR repository contract so that CI can push deployment images to a deterministic destination.
- **As a reviewer**, I want repository retention and delete behavior written down explicitly so that image cleanup is intentional rather than accidental.
- **As a future spec author**, I want image source assumptions settled before ECS service and rollout specs depend on them.
- **As an operator**, I want destroy behavior to be explicit so that dev-environment teardown does not fail unexpectedly when the repository still contains images.

## Demoable Units of Work

### Unit 1: Repository Identity and Tagging Contract

**Purpose:** Define the repository resource shape, naming pattern, and tag-mutability behavior that all downstream deployment work will rely on.

**Functional Requirements:**

- The system shall define one private Amazon ECR repository for the application in the `dev` app infrastructure stack.
- The system shall use a repository name that is environment-scoped and consistent with the repository's existing Terraform naming conventions.
- The system shall configure the repository to prevent overwriting existing image tags.
- The system shall support immutable Git SHA image tags as the only approved image-tagging contract in v1.
- The system shall not define mutable convenience tags such as `latest` or `main-latest` in v1.
- The system shall apply the repository's common Terraform resource tags and extend them with any stack-specific ECR tags needed for clarity.

**Proof Artifacts:**

- `File:` Terraform ECR repository resource definition demonstrates the repository name, mutability mode, and tags are explicitly defined
- `CLI:` `terraform plan -no-color` output demonstrates one deterministic ECR repository is created in the `dev` stack
- `Documentation:` a short repository contract summary demonstrates Git SHA tags are the only approved push and deploy reference format

### Unit 2: Lifecycle Retention Contract

**Purpose:** Define how the repository automatically cleans up images so the dev POC stays bounded and predictable.

**Functional Requirements:**

- The system shall define an Amazon ECR lifecycle policy for the repository.
- The system shall expire untagged images automatically instead of relying on manual cleanup.
- The system shall retain only a bounded count of the most recent tagged Git SHA images.
- The system shall use a count-based retention strategy for tagged Git SHA images rather than a day-based retention strategy in v1.
- The system shall keep the tagged-image retention rule and untagged-image cleanup rule readable enough for a junior developer to verify.
- The system shall document that lifecycle policy preview is the required validation step before enforcing retention behavior in AWS.

**Proof Artifacts:**

- `File:` lifecycle policy JSON or Terraform policy definition demonstrates separate handling for untagged images and tagged Git SHA images
- `CLI:` `terraform plan -no-color` output demonstrates the lifecycle policy is attached to the repository
- `Documentation:` lifecycle-policy preview instructions demonstrate retention behavior can be checked before live enforcement

### Unit 3: CI Consumption and Destroy Contract

**Purpose:** Define the downstream outputs and teardown behavior so CI and Terraform destroy can operate without ambiguity.

**Functional Requirements:**

- The system shall expose the repository values needed by CI to push images, including a deterministic repository URI.
- The system shall expose exactly `repository_uri` and `repository_name` as the repository outputs that CI and later ECS or workflow specs consume without reconstructing names manually.
- The system shall document that CI pushes immutable Git SHA tags to the repository before later deployment steps update ECS.
- The system shall allow Terraform destroy to delete the repository even when it still contains images.
- The system shall make the repository force-delete behavior explicit so destroy-time image cleanup is handled intentionally by infrastructure code.
- The system shall document that deleting the repository also deletes all contained images and is acceptable for this dev-only POC lifecycle.

**Proof Artifacts:**

- `File:` Terraform outputs demonstrate CI can read the repository URI and related identifiers without reverse-engineering resource names
- `CLI:` `terraform plan -no-color` output demonstrates repository delete behavior is intentionally configured
- `Documentation:` a short CI consumption contract demonstrates how immutable Git SHA tags and the repository URI fit together in later deployment workflows

## Non-Goals (Out of Scope)

1. **Docker build workflow implementation**: This spec does not add or redesign the container image build pipeline.
2. **ECS rollout behavior**: This spec does not define task definition revisioning, service deployment strategy, or ALB target registration.
3. **Advanced registry policy features**: This spec does not define cross-account access, replication, pull-through cache, or a vulnerability scanning strategy beyond what the repository contract needs for the dev POC.
4. **Cross-resource destroy orchestration**: This spec defines repository-local destroy semantics only; end-to-end destroy sequencing, ECS revision drift handling, and other cross-resource cleanup blockers belong to the later destroy-focused spec.
5. **Private ECS pull-path design and cost optimization**: This spec acknowledges that later ECS and network specs must provide a viable image-pull path from private tasks to ECR, but it does not define that networking approach or its cost controls.

## Design Considerations

No specific design requirements identified.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing test or failing validation first, minimum implementation second, refactor third.
- Keep infrastructure work aligned with the existing Terraform layout under `infra/terraform/`, especially the `state/dev`, `app/dev`, and `floci` structure already established by prior AWS specs.
- Reuse the current `dev` naming and tagging conventions already present in `infra/terraform/app/dev`, including environment-scoped names and shared common tags.
- Preserve the spec-driven workflow under `docs/specs/` and maintain conventional commit expectations from [AGENTS.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/AGENTS.md) and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).
- Any implementation spawned from this spec should keep proof artifacts reviewer-friendly and avoid requiring later specs to infer repository behavior from raw Terraform alone.

## Technical Considerations

- Current repository context already defines a `dev`-only Terraform app stack in `infra/terraform/app/dev` with environment-scoped naming, common tags, and downstream outputs; this spec should extend that pattern rather than inventing a separate ECR stack.
- Current repository context also defines the production container contract in [24-spec-production-container-contract.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/24-spec-production-container-contract/24-spec-production-container-contract.md), including containerized deployment assumptions that depend on a stable image source. This ECR spec should provide that image source without reopening container runtime decisions.
- Current Amazon ECR guidance recommends enabling tag immutability when images should not be overwritten. Because this project has already decided to deploy immutable Git SHA image tags, the repository contract should use immutable tagging behavior and should not add mutable convenience tags in v1.
- Current Amazon ECR guidance also recommends defining lifecycle policies and previewing them before enforcement. This spec should therefore use an explicit lifecycle policy and require preview-based validation rather than manual ad hoc cleanup.
- Current Terraform AWS provider guidance exposes explicit repository delete behavior through `force_delete`. For this dev proof of concept, the repository should intentionally allow destroy to delete the repository and any remaining images so teardown does not depend on a separate manual cleanup step.
- This repository-local destroy contract is intentionally narrow: it establishes that the repository may be force-deleted by Terraform, while later destroy-focused work remains responsible for end-to-end teardown sequencing, ECS revision drift, and other cleanup blockers outside the repository itself.
- Current Terraform AWS provider guidance defaults ECR repositories to mutable tags unless mutability is configured. This spec should make immutability explicit rather than relying on defaults.
- Current Amazon ECR guidance notes that repository-level scan-on-push settings exist, but current AWS guidance prefers managing scan configuration at the private-registry level when scan behavior matters broadly. Because vulnerability scanning is out of scope beyond what the POC requires, this spec should avoid making repository-local scanning policy the core contract unless implementation needs a minimal explicit setting for compatibility.
- This spec also depends on later ECS and network specs providing a workable private-task image-pull path to ECR. That dependency should be acknowledged here so downstream work does not assume the repository contract alone solves image retrieval, but the networking design and cost optimization for that path remain out of scope for this spec.

## Security Considerations

- The repository shall reject overwriting existing image tags so deployment artifacts remain traceable to a single Git SHA.
- CI and later deployment workflows shall use the deterministic repository URI without committing long-lived AWS credentials or secret values to the repository.
- Proof artifacts shall not expose AWS account secrets, authorization tokens, or unnecessary sensitive registry details.
- Destroy behavior shall be documented clearly because force-deleting the repository also deletes all stored images and cannot be undone.
- If later specs add repository policies or cross-account access, they shall follow least-privilege access principles rather than broad write access.

## Success Metrics

1. **Deterministic push target**: A maintainer can identify one repository URI that CI should use for image pushes in `dev`.
2. **Artifact integrity**: The repository contract allows immutable Git SHA tagging and does not rely on mutable convenience tags in v1.
3. **Bounded retention**: The documented lifecycle strategy prevents unbounded accumulation of old images while preserving a recent rollback window.
4. **Predictable teardown**: A reviewer can see from Terraform configuration and documentation that dev-stack destroy will not fail merely because images remain in the repository.

## Open Questions

1. V1 shall retain the most recent 5 tagged Git SHA images in the lifecycle policy.
