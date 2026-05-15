# Spec 9: Add the manual GitHub Actions workflow for Terraform apply

## Summary

Define the GitHub Actions workflow that manually applies infrastructure changes to the dev environment.

## Problem statement

Infrastructure changes should not happen automatically on every merge. The agreed model is manual operator control through GitHub Actions. That requires a dedicated workflow with predictable inputs, Terraform commands, and safeguards.

## In scope

- `workflow_dispatch` apply workflow
- GitHub OIDC authentication usage
- Terraform `init`, `plan`, and `apply` sequence
- dev environment targeting
- confirmation or safety inputs
- workflow outputs/logging expectations

## Out of scope

- app image build
- ECS application rollout
- destroy workflow
- IAM trust policy details beyond what the workflow consumes

## Decisions already made

- Terraform apply is manual
- Terraform is the source of truth for long-lived infrastructure
- Only `dev` exists right now

## Deliverables

- Manual infra apply workflow design
- Operator invocation model
- Failure/reporting expectations

## Acceptance criteria

- A maintainer can manually apply the dev infrastructure from GitHub Actions
- Workflow uses OIDC instead of long-lived AWS keys
- Workflow behavior is safe enough for repeated use

## Dependencies

- Spec 2 for backend/state assumptions
- Spec 10 for OIDC/IAM auth model

## Implementation notes

- The spec should decide whether apply happens immediately after plan in the same job or behind an explicit confirmation input
- Keep environment scope narrow to avoid accidental future overreach

## Risks and open questions

- Whether approval should rely on workflow inputs, GitHub environments, or both

## Suggested labels

- `spec`
- `github-actions`
- `terraform`
- `cicd`
