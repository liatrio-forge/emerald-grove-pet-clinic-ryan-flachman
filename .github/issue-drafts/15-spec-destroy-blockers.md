# Spec 15: Handle destroy-time cleanup blockers such as ECR image retention and ECS revision drift

## Summary

Define the ownership boundaries and cleanup mechanics needed so teardown succeeds reliably instead of only in the happy path.

## Problem statement

The agreed design splits ownership between Terraform-managed infrastructure and CI-managed deployment artifacts. That split is practical, but it can create destroy-time failures if not handled intentionally. Common blockers include retained ECR images and ECS task definition drift/revision churn.

## In scope

- ECR cleanup behavior needed for successful destroy
- CI-created ECS artifact considerations that affect destroy
- ownership boundary clarification between Terraform and CI
- destroy sequencing assumptions

## Out of scope

- redesigning the overall deployment model
- adding production-grade release orchestration
- changing the backend state design unless strictly necessary

## Decisions already made

- Terraform owns long-lived infrastructure
- CI owns app image rollout and task definition revision churn
- A manual destroy workflow is required

## Deliverables

- Destroy robustness design
- Explicit handling of predictable teardown blockers

## Acceptance criteria

- Destroy workflow assumptions about ECR and ECS artifacts are explicit
- Known teardown blockers are either prevented or intentionally handled
- Ownership boundaries do not leave orphaned resources that break repeated POC cleanup

## Dependencies

- Spec 5 for ECR design
- Spec 8 for baseline ECS service design
- Spec 12 for task definition revision strategy
- Spec 14 for destroy workflow design

## Implementation notes

- This spec exists because teardown usually looks easy until image and revision churn accumulate
- The spec should define whether force-delete is acceptable for the POC and where that behavior should live

## Risks and open questions

- Whether ECS task definition revisions themselves need explicit cleanup, or only the service/ECR interactions matter

## Suggested labels

- `spec`
- `terraform`
- `cleanup`
- `ecr`
- `ecs`
