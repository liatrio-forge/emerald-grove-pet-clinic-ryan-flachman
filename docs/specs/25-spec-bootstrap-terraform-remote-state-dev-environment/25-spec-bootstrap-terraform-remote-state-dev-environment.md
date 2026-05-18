# 25-spec-bootstrap-terraform-remote-state-dev-environment.md

## Introduction/Overview

This feature defines the Terraform remote-state foundation for the dev-only AWS proof of concept so later infrastructure work can run safely from GitHub Actions. The primary goal is to make backend ownership, locking behavior, naming, tagging, and bootstrap sequencing explicit before application infrastructure is introduced.

## Goals

- Define one unambiguous backend design for Terraform state storage and locking in the `dev` environment
- Define a bootstrap sequence that avoids self-referential backend create, update, and destroy problems
- Define backend naming and tagging conventions that later Terraform specs can reuse consistently
- Define how later Terraform stacks consume remote state without duplicating backend design decisions
- Define a local infrastructure-testing path using `floci` before AWS deployment
- Keep the scope limited to backend foundation work and exclude application infrastructure provisioning

## User Stories

- **As a platform engineer**, I want Terraform state stored remotely so that GitHub Actions runs do not depend on fragile local state.
- **As a team member applying infrastructure changes**, I want state locking defined up front so that concurrent Terraform operations do not corrupt shared state.
- **As a future spec author**, I want backend assumptions documented once so that later Terraform specs can focus on application infrastructure instead of re-deciding state management.
- **As an operator who may destroy and recreate the POC**, I want backend ownership boundaries to be explicit so that the main stack does not break its own state management during teardown.

## Demoable Units of Work

### Unit 1: Backend Ownership and Bootstrap Boundary

**Purpose:** Define who owns the remote-state resources and how they are created before any application stack uses them.

**Functional Requirements:**

- The system shall define backend resources as a dedicated bootstrap concern separate from the main application infrastructure stack.
- The system shall define a bootstrap sequence in which backend resources exist before any stack attempts `terraform init` with the remote backend enabled.
- The system shall define that destroying the main application stack does not implicitly destroy the backend resources in the same operation.
- The system shall document whether backend resources are long-lived dev-team assets or short-lived POC assets with a controlled manual teardown sequence.
- The system shall make the long-lived versus manually torn-down dev assets decision explicit so reviewers can see that the dev POC uses a controlled manual teardown sequence for backend resources.

**Proof Artifacts:**

- `Documentation:` bootstrap sequence document demonstrates backend creation happens before remote-state consumers run
- `Documentation:` ownership statement demonstrates the backend and app stack have separate lifecycle boundaries
- `CLI:` later bootstrap instructions showing an init/apply flow for backend creation demonstrate the remote backend can be established without circular dependency

### Unit 2: Remote State Resource Contract

**Purpose:** Define the AWS resource shape and conventions for storing Terraform state and coordinating state locks in `dev`.

**Functional Requirements:**

- The system shall define one Amazon S3 bucket dedicated to Terraform state storage for the `dev` environment.
- The system shall define one Amazon DynamoDB table dedicated to Terraform state locking for the `dev` environment.
- The system shall define a consistent naming convention for backend resources that includes environment context and avoids ambiguous shared names.
- The system shall define a consistent tagging convention for backend resources that later specs can reuse across AWS resources.
- The system shall require S3 bucket versioning so previous state revisions can be recovered after accidental changes.
- The system shall require server-side encryption for backend state storage at rest.
- The system shall define the DynamoDB lock table key contract required by Terraform backend locking.

**Proof Artifacts:**

- `Documentation:` backend resource specification demonstrates bucket, table, naming, and tagging rules are explicit
- `File:` later Terraform bootstrap configuration demonstrates the remote backend resource contract is implementable
- `CLI:` later evidence of created S3 bucket versioning and DynamoDB lock-table schema demonstrates the backend contract was applied correctly

### Unit 3: Remote State Consumer Guidance

**Purpose:** Define how later Terraform stacks consume the backend consistently from local development, `floci`, and GitHub Actions.

**Functional Requirements:**

- The system shall define the backend configuration approach that later Terraform stacks must use for the `dev` environment.
- The system shall define a stable state key structure for the main application stack so future infrastructure specs do not invent incompatible paths.
- The system shall prefer a backend configuration approach that keeps environment-specific values out of reusable source when practical.
- The system shall document how `floci`, GitHub Actions, and local operators supply any required backend configuration inputs.
- The system shall define that later Terraform specs assume remote state is already bootstrapped and must not recreate backend resources in the main application stack.
- The system shall define that infrastructure changes are validated locally against `floci` before AWS deployment.

**Proof Artifacts:**

- `Documentation:` remote-state usage guidance demonstrates later specs can initialize against the same backend contract
- `File:` example backend configuration snippet demonstrates the expected bucket, key, region, locking inputs, and `floci`-specific local testing values
- `CLI:` later `terraform init` output using the documented backend approach demonstrates consumers can attach to remote state successfully

### Unit 4: Local Infrastructure Testing with Floci

**Purpose:** Define the local AWS-resource testing path that infrastructure changes must pass before AWS deployment.

**Functional Requirements:**

- The system shall define `floci` as the local AWS-resources environment for infrastructure testing.
- The system shall document that infrastructure changes are validated against `floci` before promotion to AWS.
- The system shall define how the remote-state contract is exercised locally in `floci` without requiring live AWS credentials.
- The system shall ensure proof artifacts for local infrastructure testing remain sanitized and do not expose credentials or raw state contents.

**Proof Artifacts:**

- `Documentation:` local infrastructure-testing guidance demonstrates `floci` is the required pre-AWS validation path
- `File:` local backend or verification example demonstrates how `floci` participates in the remote-state workflow
- `CLI:` later local verification output against `floci` demonstrates the infrastructure contract can be exercised without live AWS access

## Non-Goals (Out of Scope)

1. **Application infrastructure**: This spec does not create or design ECS services, ALBs, ECR repositories, networking, or application deployment resources.
2. **GitHub Actions implementation details**: This spec does not define the workflow YAML, runner permissions model, or CI orchestration beyond backend assumptions.
3. **Multi-environment support**: This spec does not define `stage`, `prod`, Terraform workspaces strategy, or cross-account promotion flows.

## Design Considerations

No specific design requirements identified.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing test first, minimum implementation second, refactor third.
- Keep new documentation in Markdown under `docs/specs/` and maintain the repository's existing spec-driven workflow.
- Preserve conventional commit expectations from [AGENTS.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/AGENTS.md) and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).
- Any later implementation should fit the repository's current greenfield infrastructure posture because the codebase currently contains application code and documentation but no checked-in Terraform modules or AWS provisioning layout.
- Any implementation spawned from this spec should include automated validation of backend configuration and safe bootstrap behavior where practical.
- Any implementation spawned from this spec should document and support `floci` as the local AWS-resource validation path before AWS deployment.

## Technical Considerations

- Current repository context shows no existing Terraform files, modules, or AWS infrastructure layout, so this spec should define the first infrastructure ownership boundary clearly instead of assuming prior conventions.
- Current HashiCorp guidance for the S3 backend recommends using partial backend configuration where practical so reusable source does not hard-code environment-specific backend values.
- Current HashiCorp guidance also recommends S3-native locking via `use_lockfile` and marks DynamoDB-based locking as deprecated for newer Terraform versions. This spec intentionally preserves the issue's explicit `S3 + DynamoDB` decision for the dev POC and should record that choice as a deliberate short-term compatibility decision rather than the long-term preferred direction.
- Current AWS guidance recommends remote state storage in S3, versioning for recovery, and strong protection against accidental loss; this spec should therefore require bucket versioning and durable state retention assumptions.
- The backend bootstrap implementation should remain separate from the main app stack to avoid circular dependency during `terraform init` and to avoid unsafe teardown ordering where the app stack destroys the resources that hold its own state.
- For this dev POC, backend resources should be treated as manually torn-down dev assets with a separate operator-controlled destroy sequence, even if the application stack is short-lived.
- The lock table contract should follow Terraform's expected schema for DynamoDB locking so later stacks can attach without custom lock semantics.
- The naming convention should be environment-scoped and human-readable so operators can distinguish backend resources from future application resources in the AWS account.
- Local infrastructure validation should run against `floci` so engineers can test remote-state assumptions before using live AWS resources.

## Security Considerations

- Terraform state may contain sensitive infrastructure values, so backend storage shall use encryption at rest and shall not rely on committed local state files for shared workflows.
- Access to the S3 state bucket and DynamoDB lock table shall follow least-privilege principles for both local operators and GitHub Actions.
- Backend configuration guidance shall avoid committing AWS credentials, access keys, or populated secret values to the repository.
- Proof artifacts shall not include raw `terraform.tfstate` contents, account secrets, or screenshots that reveal sensitive identifiers beyond what is necessary to verify the feature.

## Success Metrics

1. **Backend ownership clarity**: A junior developer can identify from the spec which resources belong to the bootstrap layer and which do not belong in the main app stack.
2. **Remote-state readiness**: A later Terraform spec can reference one documented backend contract for `dev` without reopening naming, locking, or bootstrap design questions.
3. **Operational safety**: The documented bootstrap and teardown sequence avoids circular backend initialization and avoids unsafe main-stack destroy behavior.
4. **Local validation readiness**: A team member can validate infrastructure changes against `floci` before promoting those changes to AWS.

## Open Questions

1. No open questions at this time.
