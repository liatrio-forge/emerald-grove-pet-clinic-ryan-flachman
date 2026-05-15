# Spec 11: Add the GitHub Actions workflow that builds and pushes the Docker image to ECR

## Summary

Define the CI workflow slice that turns a merge to `main` into an immutable Docker image stored in ECR.

## Problem statement

The application deployment flow begins with producing a traceable image artifact. This should be a discrete spec because image build/push can be validated independently from ECS service rollout.

## In scope

- Trigger on merge/push to `main`
- application build step
- Docker image build
- immutable image tag strategy based on Git SHA
- ECR login and push
- optional convenience tag policy

## Out of scope

- ECS task definition registration
- ECS service update
- Terraform apply/destroy

## Decisions already made

- Deploy workflow triggers from merges to `main`
- Dockerfile is the build mechanism
- Images use immutable Git SHA tagging

## Deliverables

- CI image publishing workflow design
- Stable image naming/tagging contract

## Acceptance criteria

- Merge to `main` publishes a new Docker image to ECR
- Image can be traced back to the commit that produced it
- Workflow uses OIDC-backed AWS auth

## Dependencies

- Spec 1 for container contract
- Spec 5 for ECR contract
- Spec 10 for OIDC/IAM auth model

## Implementation notes

- The spec should clarify whether Maven or Gradle is the canonical build path for CI, since the repo contains both
- The workflow should not assume mutable tags are sufficient for deployment traceability

## Risks and open questions

- Ambiguity between Maven and Gradle in CI could create unnecessary drift if not resolved explicitly

## Suggested labels

- `spec`
- `github-actions`
- `docker`
- `ecr`
- `cicd`
