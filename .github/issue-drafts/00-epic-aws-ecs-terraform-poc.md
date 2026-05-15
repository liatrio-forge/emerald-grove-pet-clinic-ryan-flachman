# Epic: Build AWS ECS Fargate deployment POC with Terraform, ECR, ALB, CI/CD, and teardown

## Summary

Create a dev-only deployment POC for the Emerald Grove Spring Boot application on AWS using Terraform for infrastructure and GitHub Actions for infrastructure control and application deployment.

This epic covers:

- containerizing the application
- storing images in ECR
- deploying the app to ECS Fargate
- exposing the app through an ALB
- ensuring ECS tasks are only reachable from the ALB
- adding GitHub Actions workflows for infra apply, app deploy, and infra destroy
- documenting the operator flow and POC limitations

## Problem statement

The application currently has no defined AWS deployment path. The goal is to create a realistic but limited cloud deployment POC that demonstrates:

- Terraform-managed infrastructure
- GitHub OIDC-based AWS authentication
- CI-driven image publishing and ECS rollout
- manual infrastructure lifecycle control
- reliable cleanup after experimentation

The POC is explicitly not production-ready because it will remain on H2 and initially expose only HTTP.

## Goals

- Stand up the Spring Boot application in AWS ECS Fargate
- Store container images in ECR
- Route internet traffic through a public ALB
- Keep ECS tasks private and reachable only from the ALB
- Separate infrastructure apply/destroy from application deploy
- Use GitHub Actions and GitHub OIDC instead of long-lived AWS keys
- Make the stack easy to destroy after use

## Non-goals

- Production database migration
- HTTPS in v1
- multi-environment promotion
- autoscaling
- blue/green or canary deployments
- production-grade observability stack
- zero-downtime durable persistence guarantees

## Decisions already made

- Environment scope: single `dev` environment only
- Compute model: ECS Fargate
- Network baseline: new dedicated VPC
- Terraform backend: S3 + DynamoDB
- App deploy model: merge to `main` builds image, pushes to ECR, registers task definition revision, updates ECS service
- Infra model: manual `workflow_dispatch` workflow for Terraform apply
- Cleanup model: manual `workflow_dispatch` workflow for Terraform destroy
- Authentication: GitHub OIDC to AWS
- Runtime persistence: keep H2 for the POC
- Public endpoint: HTTP only in v1 because no domain is currently available

## Constraints and caveats

- H2 means task replacement loses data
- H2 means `desired_count = 1`
- HTTP-only means the original HTTPS requirement is deferred
- Private ECS plus low-cost AWS networking must be designed carefully to avoid unnecessary spend
- Terraform-managed resources and CI-managed task definition revisions must have clean ownership boundaries

## Child specs

- Spec 1: add a production container contract for the Spring Boot app
- Spec 2: bootstrap Terraform remote state for the dev environment
- Spec 3: provision the base VPC network for the dev POC
- Spec 4: add security groups enforcing ALB-only access to the app
- Spec 5: provision the ECR repository and image retention policy
- Spec 6: provision the ALB and target group for public HTTP access
- Spec 7: provision the ECS cluster, logging, and IAM execution role
- Spec 8: provision the baseline ECS task definition and service
- Spec 9: add the manual GitHub Actions workflow for Terraform apply
- Spec 10: add GitHub OIDC IAM trust and permissions for workflows
- Spec 11: add the GitHub Actions workflow that builds and pushes the Docker image to ECR
- Spec 12: extend the deploy workflow to register a new ECS task definition revision
- Spec 13: extend the deploy workflow to update the ECS service to the new revision
- Spec 14: add the manual GitHub Actions workflow for Terraform destroy
- Spec 15: handle destroy-time cleanup blockers such as ECR image retention and ECS revision drift
- Spec 16: document operator flow for create, deploy, and destroy

## Acceptance criteria

- All child specs exist as discrete implementation issues
- The final result supports:
  - manual infra apply
  - automatic app deploy on merge to `main`
  - manual infra destroy
- ECS is not directly reachable from the internet
- ALB publicly serves the application over HTTP
- The documented limitations of H2 and HTTP-only access are explicit

## Suggested labels

- `epic`
- `aws`
- `terraform`
- `ecs`
- `cicd`
- `poc`

## Risks and open questions

- Whether the chosen network design stays cheap enough for repeated experimentation
- Whether CI-created ECS task definition revisions complicate destroy behavior
- Whether the Terraform backend bootstrap should be managed in the same repo or treated as a separate one-time stack
