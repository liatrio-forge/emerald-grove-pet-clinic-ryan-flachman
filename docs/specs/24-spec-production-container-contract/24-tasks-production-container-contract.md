# 24-tasks-production-container-contract.md

## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `Dockerfile` | Defines the repository-owned multi-stage image build and foreground startup contract for deployed runtime. |
| `.dockerignore` | Keeps the Docker build context reproducible and avoids copying unnecessary local artifacts into the image build. |
| `pom.xml` | Confirms the Maven wrapper build path, Spring Boot packaging behavior, and actuator dependency used by the container contract. |
| `src/main/resources/application.properties` | Holds the current wildcard actuator exposure that the deploy-specific contract must override without changing local defaults. |
| `src/main/resources/application-deploy.properties` | New deploy profile for explicit port, actuator exposure, health behavior, and runtime override boundaries. |
| `src/test/java/org/springframework/samples/petclinic/system/ProductionContainerBuildContractTest.java` | Planned test class for repository-owned build file expectations and multi-stage container contract assertions. |
| `src/test/java/org/springframework/samples/petclinic/system/DeployProfileRuntimeContractTest.java` | Planned test class for deploy-profile startup, explicit port `8080`, and environment-variable contract behavior. |
| `src/test/java/org/springframework/samples/petclinic/system/DeployActuatorHealthIntegrationTests.java` | Planned integration test class for deployed actuator exposure and `/actuator/health` behavior. |
| `src/test/java/org/springframework/samples/petclinic/system/DeployProfileIsolationTest.java` | Planned test class for deploy-profile isolation from local defaults and profile activation rules. |
| `README.md` | Existing operator-facing documentation entry point for the deployment-oriented build, run, profile, and environment variable commands. |
| `docs/specs/24-spec-production-container-contract/24-proofs/24-task-03-proofs.md` | Planned proof artifact file for stable ALB path and ECS loopback health-check command documentation. |
| `docs/specs/24-spec-production-container-contract/24-proofs/24-task-04-proofs.md` | Planned proof artifact file for deploy-profile activation and operator-facing runtime override evidence. |

### Notes

- Follow strict TDD for implementation: add the failing test for each task slice before creating or changing the corresponding production/configuration file.
- Use the repository’s established Maven command set for verification, starting with focused test classes and then `./mvnw test` before completion.
- Keep new deploy-only behavior in `application-deploy.properties`; do not overwrite local-development defaults in `application.properties` unless the task explicitly requires a shared baseline change.
- Proof artifacts must stay sanitized: use placeholder values for credentials and avoid recording real cloud identifiers or secret material.

## Tasks

### [x] 1.0 Establish the repository-owned container build contract

#### 1.0 Proof Artifact(s)

- File: `Dockerfile` at the repository root demonstrates the in-repository multi-stage build contract
- File: `.dockerignore` at the repository root demonstrates companion build-hygiene rules for reproducible image inputs
- CLI: `docker build -t petclinic:spec24 .` exits `0` and demonstrates the image can be built from repository sources with the Maven wrapper
- Test: `src/test/java/org/springframework/samples/petclinic/system/ProductionContainerBuildContractTest.java` passes and demonstrates the build files enforce the expected container contract

#### 1.0 Tasks

- [x] 1.1 Add a failing system test that asserts the repository includes a root `Dockerfile`, uses multi-stage build steps, builds with the Maven wrapper, and expects a runtime stage that starts the packaged application without source files.
- [x] 1.2 Create the root `Dockerfile` with distinct build and runtime stages that package the application from repository sources and copy only the runnable artifact into the final image.
- [x] 1.3 Add `.dockerignore` rules that exclude local build output and other non-runtime inputs so the image build context stays reproducible and lean.
- [x] 1.4 Document and capture the `docker build -t petclinic:spec24 .` proof so downstream CI/CD and infrastructure work can reuse the same image build contract.

### [ ] 2.0 Define the runtime startup and fixed port contract

#### 2.0 Proof Artifact(s)

- File: `Dockerfile` `ENTRYPOINT`/`CMD` contract demonstrates the packaged Spring Boot application runs as the foreground process
- File: `src/main/resources/application-deploy.properties` demonstrates the deployed runtime uses explicit port `8080` and environment-driven configuration inputs
- CLI: `docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=deploy petclinic:spec24` followed by `curl http://localhost:8080/` returns an HTTP response and demonstrates the startup and port contract
- Test: `src/test/java/org/springframework/samples/petclinic/system/DeployProfileRuntimeContractTest.java` passes and demonstrates deploy-profile port and environment override expectations

#### 2.0 Tasks

- [ ] 2.1 Add a failing deploy-profile test that asserts deployed runtime uses explicit port `8080`, activates through `SPRING_PROFILES_ACTIVE=deploy`, and leaves secrets supplied only through environment-variable overrides.
- [ ] 2.2 Create `src/main/resources/application-deploy.properties` with deploy-only runtime settings for the fixed port and documented environment-driven values such as datasource connectivity and optional external API credentials.
- [ ] 2.3 Update the `Dockerfile` startup contract so the container runs one foreground Spring Boot process using the packaged jar and the deploy profile without requiring baked-in secrets.
- [ ] 2.4 Capture the `docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=deploy petclinic:spec24` plus HTTP smoke-check proof that confirms the startup path and port mapping work as documented.

### [ ] 3.0 Define the deployed health-check and actuator exposure contract

#### 3.0 Proof Artifact(s)

- File: `src/main/resources/application-deploy.properties` demonstrates deployed actuator exposure is reduced from wildcard to the minimum health-oriented surface
- CLI: `curl http://localhost:8080/actuator/health` against the running deploy-profile container returns `200` with a healthy response and demonstrates the public health-check contract
- Documentation: `docs/specs/24-spec-production-container-contract/24-proofs/24-task-03-proofs.md` records the stable ALB path and ECS loopback command and demonstrates downstream infrastructure can reuse one health contract
- Test: `src/test/java/org/springframework/samples/petclinic/system/DeployActuatorHealthIntegrationTests.java` passes and demonstrates `/actuator/health` is exposed while broader actuator endpoints remain unavailable in the deploy profile

#### 3.0 Tasks

- [ ] 3.1 Add a failing integration test that runs with the deploy profile and asserts `/actuator/health` returns success while broader actuator endpoints are not exposed for deployed traffic.
- [ ] 3.2 Update deploy-specific properties to remove wildcard actuator exposure, keep health on the main application port, and scope health details to the minimum needed for v1 checks.
- [ ] 3.3 Document one stable ALB health-check path and one ECS loopback health-check command that both target the same deploy-profile application instance.
- [ ] 3.4 Capture proof from a running container showing `curl http://localhost:8080/actuator/health` succeeds and that the documented health-check guidance matches the implemented contract.

### [ ] 4.0 Isolate deployed-environment settings behind a dedicated deploy profile

#### 4.0 Proof Artifact(s)

- File: `src/main/resources/application-deploy.properties` demonstrates deploy-only operational overrides are separated from local defaults
- File: `README.md` or deployment-focused documentation update demonstrates profile activation and runtime environment-variable expectations for operators
- CLI: `docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=deploy petclinic:spec24` logs the active profile and demonstrates deployed settings can be activated without changing local profiles
- Test: `src/test/java/org/springframework/samples/petclinic/system/DeployProfileIsolationTest.java` passes and demonstrates deploy-specific overrides do not leak unnecessary local-development configuration into the deployed contract

#### 4.0 Tasks

- [ ] 4.1 Add a failing profile-isolation test that asserts deploy-specific operational overrides are confined to `application-deploy.properties` and do not duplicate unrelated local-development settings.
- [ ] 4.2 Refine `application-deploy.properties` so it contains only deployed-environment concerns such as explicit port, actuator exposure, health behavior, and runtime override hooks.
- [ ] 4.3 Update operator-facing documentation to show how to activate the deploy profile, which environment variables are required or optional at runtime, and that H2 remains the default proof-of-concept database path unless overridden.
- [ ] 4.4 Capture proof that deploy-profile startup logs or observable runtime behavior confirm the profile activation path works without editing local profile files or embedding secrets in the image.
