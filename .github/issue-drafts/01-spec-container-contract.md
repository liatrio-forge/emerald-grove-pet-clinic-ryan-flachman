# Spec 1: Add a production container contract for the Spring Boot app

## Summary

Define how the Spring Boot application is built and run as a container in a deployed environment.

## Problem statement

The application can run locally, but it does not yet have a concrete deployment-time container contract. ECS, ECR, ALB health checks, and CI/CD all depend on a stable answer for:

- how the image is built
- which port the app listens on
- how the process starts
- which environment variables are expected
- how health is reported
- what actuator surface is safe in a public environment

Without this spec, later infrastructure and CI work will bake in assumptions that may conflict.

## In scope

- Add a first-class `Dockerfile`
- Define the container startup command
- Define the container port
- Define runtime environment variable expectations
- Decide the application health endpoint to be used by ECS and ALB
- Restrict actuator exposure for deployed environments
- Clarify whether a dedicated deploy profile is needed

## Out of scope

- ECR repository creation
- ECS service provisioning
- ALB creation
- GitHub Actions implementation
- HTTPS or domain management

## Decisions already made

- Docker image will be built from a `Dockerfile`
- App will run on ECS Fargate
- Public entrypoint will be ALB over HTTP in v1

## Deliverables

- Container build contract
- Runtime contract for env vars and port mapping
- Health check contract
- Deployed-environment actuator exposure contract

## Acceptance criteria

- The image build approach is explicit and reproducible
- The runtime port is fixed and documented
- The health endpoint is stable and suitable for ALB/ECS checks
- Actuator exposure is reduced from the current broad default for deployed use
- The container contract is precise enough for later Terraform and workflow specs

## Dependencies

- None

## Implementation notes

- Current repo signals:
  - Spring Boot app
  - default port expected to be `8080`
  - actuator currently exposes `*`
  - profiles exist for `mysql` and `postgres`
  - H2 remains the POC database for now
- The spec should define whether to rely on default Spring Boot port behavior or set it explicitly
- The spec should avoid introducing baked-in secrets

## Risks and open questions

- Whether a deploy-specific Spring profile is required or whether env-based overrides are enough
- Whether a custom health path is needed versus plain `/actuator/health`

## Suggested labels

- `spec`
- `aws`
- `docker`
- `ecs`
- `backend`
