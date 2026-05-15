# Spec 12: Extend the deploy workflow to register a new ECS task definition revision

## Summary

Define how the deploy workflow converts a new ECR image into a new ECS task definition revision.

## Problem statement

Publishing an image is not enough to deploy it. ECS rollouts should use immutable task definition revisions tied to immutable image URIs. That requires a concrete rendering/registration strategy in CI.

## In scope

- task definition template/rendering approach
- image URI injection
- ECS task definition registration
- revision capture for subsequent service update

## Out of scope

- ECS service update step
- rollback automation
- blue/green deployment strategies

## Decisions already made

- CI owns task definition revisions after the baseline service exists
- Images are identified by immutable Git SHA tags

## Deliverables

- CI task definition revision strategy
- Contract between image build output and ECS deploy input

## Acceptance criteria

- The workflow can register a new task definition revision for each new image
- The new revision preserves baseline runtime settings while updating the image reference
- The revision identifier is available to the next deployment step

## Dependencies

- Spec 8 for baseline task definition/service
- Spec 11 for image build/push workflow

## Implementation notes

- Keep the ownership boundary clean: Terraform owns the base service shape, CI owns revision churn
- The spec should define whether CI pulls the current task definition from AWS or renders from a checked-in template

## Risks and open questions

- Drift risk if the CI-rendered task definition no longer matches Terraform-owned baseline settings

## Suggested labels

- `spec`
- `github-actions`
- `ecs`
- `deployment`
