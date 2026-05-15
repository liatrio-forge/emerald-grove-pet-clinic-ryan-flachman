# Spec 14: Add the manual GitHub Actions workflow for Terraform destroy

## Summary

Define the manual GitHub Actions workflow that destroys the dev AWS application stack for cleanup.

## Problem statement

This POC must be easy to tear down after experiments. Manual console cleanup is error-prone and defeats the point of infrastructure-as-code. A dedicated destroy workflow is required, but it must also be safe because destroy is inherently destructive.

## In scope

- `workflow_dispatch` destroy workflow
- strong confirmation input
- OIDC auth
- Terraform `init` and `destroy`
- dev-only safeguards
- operator documentation expectations

## Out of scope

- backend state bootstrap destruction unless explicitly separated into its own path
- app deploy workflow
- IAM trust redesign beyond consumed permissions

## Decisions already made

- Cleanup requires a dedicated GitHub Actions workflow
- Destroy should be manual only
- Only the `dev` environment is in scope

## Deliverables

- Destroy workflow design
- Confirmation/safety model
- Dev-only protection model

## Acceptance criteria

- A maintainer can intentionally destroy the dev application stack from GitHub Actions
- Workflow has explicit safeguards against accidental execution
- The design is compatible with the remote backend strategy

## Dependencies

- Spec 2 for backend/state assumptions
- Spec 10 for OIDC/IAM auth model

## Implementation notes

- The workflow should probably require a typed confirmation token such as `destroy-dev`
- Backend resources should not be implicitly assumed destroyable by the same stack unless that has been explicitly designed

## Risks and open questions

- Whether destroy can fail if CI-managed artifacts outlive Terraform assumptions

## Suggested labels

- `spec`
- `github-actions`
- `terraform`
- `cleanup`
- `cicd`
