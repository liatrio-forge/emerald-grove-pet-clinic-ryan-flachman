# 24-spec-production-container-contract.md

## Introduction/Overview

This feature defines the production container contract for the Spring Boot application so later ECS, ALB, and CI/CD work can rely on one documented runtime model. The goal is to make image build behavior, startup, port binding, health reporting, and deployed actuator exposure explicit without expanding scope into infrastructure provisioning.

## Goals

- Define a reproducible image build contract based on a repository-owned `Dockerfile`
- Define a single runtime port contract that downstream ECS and ALB work can depend on
- Define the container startup contract and expected runtime environment variables
- Define a stable health check contract for both container-local and load-balancer traffic checks
- Reduce deployed actuator exposure from the current wildcard setting to a minimal public surface

## User Stories

- **As a platform engineer**, I want a first-class container contract so that ECS task definitions and CI workflows do not guess how the application is built and started.
- **As an application developer**, I want deployment-time defaults to be explicit so that local behavior and deployed behavior stay predictable.
- **As an operator**, I want stable health endpoints so that ALB and ECS can determine whether the application should receive traffic or be restarted.
- **As a security-conscious team member**, I want actuator exposure reduced in deployed environments so that sensitive operational endpoints are not broadly exposed.

## Demoable Units of Work

### Unit 1: Container Build Contract

**Purpose:** Define how the application image is built and what artifact shape downstream deployment work can expect.

**Functional Requirements:**

- The system shall provide a repository-owned `Dockerfile` at the project root.
- The system shall use a multi-stage container build so build tooling is excluded from the final runtime image.
- The system shall build the application from repository sources using the existing Maven wrapper and project configuration.
- The system shall produce a runtime image that starts the packaged Spring Boot application without requiring source files at runtime.
- The system shall document any required companion files for image build hygiene, such as `.dockerignore`, when they materially affect reproducibility or image size.

**Proof Artifacts:**

- `File:` root `Dockerfile` demonstrates the build contract exists in-repository
- `CLI:` `docker build` succeeds against the repository root and demonstrates the image build is reproducible
- `Documentation:` spec-linked build command and expected artifact behavior demonstrate downstream tooling can reuse the contract

### Unit 2: Runtime Startup and Port Contract

**Purpose:** Define how the container process starts and which runtime values infrastructure may assume.

**Functional Requirements:**

- The system shall define one primary container process that runs the packaged Spring Boot application in the foreground.
- The system shall use port `8080` as the application listening port for deployed environments.
- The system shall keep the runtime port contract stable by setting it explicitly for the deployed environment instead of relying on undocumented assumptions.
- The system shall define environment variable expectations only for values that may vary by environment, including database connectivity and optional external API credentials.
- The system shall not require secrets to be baked into the image.
- The system shall keep H2 as the default proof-of-concept database path unless a later approved spec changes the deployed database strategy.

**Proof Artifacts:**

- `File:` deployment-oriented runtime configuration demonstrates the fixed port and environment-variable contract
- `CLI:` `docker run -p 8080:8080 ...` followed by a successful HTTP request demonstrates the container starts with the expected port mapping
- `Documentation:` listed environment variables with defaults or optionality demonstrate operators know what must be supplied at runtime

### Unit 3: Health Check and Actuator Exposure Contract

**Purpose:** Define how the deployed application reports health while limiting public operational surface area.

**Functional Requirements:**

- The system shall expose `/actuator/health` over HTTP for deployed-environment health checks.
- The system shall expose health details needed for readiness-style traffic checks without exposing the current wildcard actuator surface.
- The system shall restrict deployed actuator exposure to the minimum endpoints needed for health verification in v1.
- The system shall define one stable ALB health check path based on the main application port.
- The system shall define one stable ECS container-local health check command that targets the same application instance on loopback.
- The system shall avoid custom health paths unless a platform requirement cannot be met with Spring Boot's default actuator health path.

**Proof Artifacts:**

- `File:` deployed-environment actuator configuration demonstrates wildcard exposure is removed and health exposure is intentionally scoped
- `CLI:` HTTP `GET /actuator/health` from a running container returns a healthy response and demonstrates the health contract
- `Documentation:` explicit ALB and ECS health-check path/command guidance demonstrates later infrastructure specs can reuse the same contract

### Unit 4: Deployed Environment Configuration Boundary

**Purpose:** Define how deployed-environment settings are isolated from local defaults without introducing unnecessary profile sprawl.

**Functional Requirements:**

- The system shall use a dedicated deploy profile for deployed-environment operational settings.
- The system shall keep deploy-specific concerns limited to runtime settings such as actuator exposure, health group behavior, and explicit server port configuration.
- The system shall avoid duplicating local development properties in the deploy profile unless the deployed contract requires an override.
- The system shall allow environment variables to override deploy-profile properties where runtime infrastructure must supply values.
- The system shall document how the deploy profile is activated by the container startup contract.

**Proof Artifacts:**

- `File:` deploy-specific application properties demonstrate deployed behavior is isolated from local defaults
- `CLI:` container startup uses the deploy profile and demonstrates deployed settings are applied without modifying local profiles
- `Documentation:` profile activation and override rules demonstrate future Terraform and CI work can configure the app consistently

## Non-Goals (Out of Scope)

1. **AWS resource provisioning**: This spec does not create or define ECR repositories, ECS services, target groups, listeners, or ALBs.
2. **CI/CD implementation**: This spec does not add GitHub Actions workflows, image publishing automation, or release orchestration.
3. **Production hardening beyond the container contract**: This spec does not define HTTPS, domains, WAF, autoscaling, secret stores, or production database migration.

## Design Considerations

No specific UI design requirements identified.

## Repository Standards

- Follow the repository's strict TDD workflow described in [docs/DEVELOPMENT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/DEVELOPMENT.md) and [docs/TESTING.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/TESTING.md): failing test first, minimum implementation second, refactor third.
- Preserve the existing Spring Boot layered structure and configuration conventions, including root `pom.xml`, Maven wrapper usage, and `src/main/resources/application*.properties`.
- Keep implementation aligned with the repository's current profiles pattern (`application.properties`, `application-mysql.properties`, `application-postgres.properties`) when introducing deployed-environment configuration.
- Keep documentation in Markdown under `docs/specs/` and maintain conventional commit expectations from `AGENTS.md` and [docs/PRECOMMIT.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/PRECOMMIT.md).
- Any implementation should add automated tests that validate configuration behavior and health endpoint expectations where practical.

## Technical Considerations

- Current repository state includes Spring Boot `4.0.0`, Maven wrapper builds, actuator dependency enabled, and `management.endpoints.web.exposure.include=*` in [src/main/resources/application.properties](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/main/resources/application.properties:17), so deployed exposure must narrow rather than expand the current surface.
- Current official Docker guidance recommends multi-stage builds to separate build tooling from the runtime image and reduce final image size and attack surface. The spec should assume a multi-stage `Dockerfile` with named stages and a minimal runtime stage.
- Current Spring Boot actuator guidance recommends exposing only the endpoints that are needed and treating HTTP-exposed actuator endpoints as security-sensitive. This spec should target `health` exposure only for deployed environments unless a later spec adds authenticated operational endpoints.
- Current Spring Boot availability guidance distinguishes liveness from readiness and warns against basing liveness on external dependencies. For this feature, the stable external contract should use `/actuator/health` for ALB traffic checks, while leaving room for later internal readiness or liveness group expansion if infrastructure needs become more specific.
- Current Amazon ECS guidance states that ECS task health is determined from health checks defined in the task definition, not only from image-embedded Docker health checks. This spec should therefore document a container-local health command for reuse by the future ECS task definition rather than treating Dockerfile `HEALTHCHECK` as sufficient by itself.
- Current ALB guidance expects a stable HTTP path on the registered target port. This spec should keep the application on a single main HTTP port and avoid management-port splitting for v1 so infrastructure checks stay simple.
- A dedicated `deploy` Spring profile is the preferred direction for this repository because it isolates deployed operational defaults from local development behavior while still allowing environment-variable overrides. This is a deliberate choice, not a forced platform requirement.

## Security Considerations

- Secrets such as database credentials and `ANTHROPIC_API_KEY` shall be injected at runtime and shall not be copied into the image, committed to source control, or hard-coded in properties files.
- The deployed configuration shall remove wildcard actuator exposure to avoid unintentionally publishing sensitive operational endpoints.
- Health responses exposed through the public load balancer shall avoid detailed sensitive internals unless a later authenticated operator-only surface is introduced.
- Proof artifacts shall not include committed secret values, cloud account identifiers that are not already public, or screenshots that reveal credentials.

## Success Metrics

1. **Build reproducibility**: A documented repository-root container build command succeeds without requiring manual undocumented setup beyond standard project prerequisites.
2. **Runtime consistency**: The container starts on port `8080` with a documented startup command and profile activation path that downstream ECS work can reuse unchanged.
3. **Operational safety**: Deployed actuator exposure is reduced from `*` to a minimal health-oriented surface while the documented health endpoint remains usable for ALB and ECS checks.

## Open Questions

1. Should a later infrastructure spec add explicit readiness and liveness group endpoints such as `/actuator/health/readiness` for ECS-internal checks, or is `/actuator/health` sufficient for the full v1 deployment path?
   - `/actuator/health` is sufficient for the full v1 deployment path.
2. Should the eventual runtime image also declare a non-root user requirement, or should that hardening step be handled in a follow-on deployment-security spec?
   - The runtime image should not declare a non-root user requirement.
