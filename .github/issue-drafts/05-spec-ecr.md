# Spec 5: Provision the ECR repository and image retention policy

## Summary

Define the container image registry contract for the application using Amazon ECR.

## Problem statement

The deploy workflow will publish immutable Docker images and ECS will pull those images at runtime. Without a stable ECR repository contract, CI tagging, task definition updates, and teardown behavior remain ambiguous.

## In scope

- ECR repository
- repository naming
- image lifecycle/retention policy
- outputs needed by CI
- destroy-time behavior for images

## Out of scope

- Docker build workflow implementation
- ECS task definition rollout logic
- vulnerability scanning policy beyond what is necessary for the POC

## Decisions already made

- Images will be built from a Dockerfile
- Images will be tagged with immutable Git SHA values
- CI deploy will push to ECR before updating ECS

## Deliverables

- Stable ECR repository definition
- Lifecycle policy strategy
- CI consumption contract

## Acceptance criteria

- CI has a deterministic target repository URI
- Image retention behavior is documented and intentional
- Destroy semantics account for image cleanup requirements

## Dependencies

- Spec 2 for Terraform backend design

## Implementation notes

- The spec should define whether convenience tags such as `main-latest` are allowed in addition to SHA tags
- The destroy workflow will be easier if ECR deletion behavior is decided here instead of later

## Risks and open questions

- Whether ECR repository deletion will be blocked by retained images unless force-delete is designed in

## Suggested labels

- `spec`
- `aws`
- `ecr`
- `terraform`
