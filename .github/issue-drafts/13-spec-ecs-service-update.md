# Spec 13: Extend the deploy workflow to update the ECS service to the new revision

## Summary

Define the final deployment step that updates the ECS service to the newly registered task definition revision.

## Problem statement

Until the ECS service is pointed at the new task definition revision, the new image is not actually deployed. This should be isolated as its own spec because service update behavior, rollout waiting, and deployment failure reporting deserve focused review.

## In scope

- ECS service update step
- optional wait for service stability
- deployment status/failure reporting in GitHub Actions
- expected operator visibility into rollout success or failure

## Out of scope

- image build
- task definition registration
- advanced deployment strategies
- automated rollback orchestration

## Decisions already made

- Merge to `main` should automatically push to ECS
- ECS rollout uses task definition revision updates, not mutable-tag force deploys

## Deliverables

- ECS service rollout strategy in CI
- Deployment observability expectations

## Acceptance criteria

- Workflow updates the ECS service to the new task definition revision
- Workflow surfaces rollout success or failure clearly
- Deployment behavior is consistent with a single-task dev-only POC

## Dependencies

- Spec 8 for ECS service baseline
- Spec 12 for task definition revision registration

## Implementation notes

- The spec should explicitly acknowledge single-task rollout limitations caused by H2 and `desired_count = 1`
- Waiting for stability is recommended if the goal is actionable deployment feedback

## Risks and open questions

- Whether single-task deployments create brief unavailability during rollout

## Suggested labels

- `spec`
- `github-actions`
- `ecs`
- `deployment`
- `cicd`
