# Task 03 Proofs - Deploy health-check contract and minimal actuator exposure

## Task Summary

This task narrows the deploy-profile actuator surface to health-only access and
documents one reusable health-check contract for both ALB and ECS. The deploy
profile now exposes `/actuator/health` on the main application port while
keeping broader actuator endpoints unavailable.

## What This Task Proves

- The `deploy` profile overrides wildcard actuator exposure with a
  health-only HTTP surface.
- The documented ALB path and ECS loopback command both target the same
  `/actuator/health` endpoint on port `8080`.
- A live `petclinic:spec24` container returns a healthy JSON response from the
  documented endpoint.
- Automated regression coverage prevents future config changes from
  re-exposing broader actuator endpoints.

## Evidence Summary

- `application-deploy.properties` now limits actuator exposure to `health`,
  keeps health details minimal, and enables Spring Boot health probes.
- `DeployActuatorHealthIntegrationTests` passed and verified `/actuator/health`
  succeeds while `/actuator/env` and `/actuator/beans` return `404` in the
  deploy profile.
- The README now documents a single ALB path and a single ECS loopback command
  based on `/actuator/health` on port `8080`.
- A live Docker run of `petclinic:spec24` reported the `deploy` profile in its
  startup logs, exposed one actuator endpoint, and returned `{"status":"UP"}`
  from the documented health URL.
- The repository-wide `./mvnw test` gate still fails only in existing
  random-port integration tests because this sandbox disallows socket binding.

## Artifact: Deploy-profile actuator configuration

**What it proves:** The deployed profile removes wildcard actuator exposure and
replaces it with a narrowly scoped health contract.

**Why it matters:** Infrastructure health checks need one stable endpoint
without exposing operational endpoints such as `env` or `beans`.

**Artifact path:** `src/main/resources/application-deploy.properties`

**Result summary:** The deploy profile now exposes only `health`, keeps health
details at `never`, and enables standard liveness/readiness group support.

```properties
# Deploy-only runtime contract for containerized environments.
server.port=8080

# Keep the proof-of-concept H2 path by default, but allow infrastructure to override
# connectivity through standard Spring datasource environment variables.
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:h2:mem:petclinic}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:sa}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}

# Optional external API credentials remain runtime-injected rather than image-baked.
anthropic.api.key=${ANTHROPIC_API_KEY:}

# Restrict deployed actuator exposure to the minimum health-check surface.
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never
management.endpoint.health.probes.enabled=true
```

## Artifact: Automated actuator exposure regression test

**What it proves:** The deploy profile keeps `/actuator/health` available while
rejecting broader actuator endpoints.

**Why it matters:** This is the main guardrail against accidentally restoring
the wildcard surface in a later configuration change.

**Command:**

```bash
./mvnw -Dtest=DeployActuatorHealthIntegrationTests test
```

**Result summary:** The focused integration test passed and verified
`/actuator/health` returned `200` while broader endpoints remained unavailable
in the deploy profile.

```text
[INFO] Running org.springframework.samples.petclinic.system.DeployActuatorHealthIntegrationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Stable health-check guidance

**What it proves:** The repository now publishes one ALB path and one ECS
loopback command that target the exact same deploy-profile endpoint.

**Why it matters:** Later Terraform and ECS specs can reuse one shared health
contract instead of inventing separate paths for different infrastructure layers.

**Artifact path:** `README.md`

**Result summary:** The operator-facing documentation now standardizes the ALB
path as `GET /actuator/health` on port `8080` and the ECS loopback command as
`curl -fsS http://127.0.0.1:8080/actuator/health`.

```markdown
Health-check guidance for downstream infrastructure:

- ALB path: `GET /actuator/health` on the main application port `8080`
- ECS loopback command: `curl -fsS http://127.0.0.1:8080/actuator/health`
```

## Artifact: Live container health-check response

**What it proves:** The documented runtime image and startup command produce a
container whose deploy-profile health endpoint is reachable and healthy.

**Why it matters:** Source-level configuration is not enough; the published
health contract must work from the running container boundary.

**Commands:**

```bash
docker build -t petclinic:spec24 .
docker run -d --rm --name petclinic-spec24 -p 8080:8080 -e SPRING_PROFILES_ACTIVE=deploy petclinic:spec24
curl -fsS http://127.0.0.1:8080/actuator/health
docker logs petclinic-spec24
```

**Result summary:** The image build completed successfully, the container
started with the `deploy` profile, Spring Boot reported one exposed actuator
endpoint, and the host-visible health URL returned `UP`. The host `curl` was
executed outside the sandbox because sandboxed localhost access could not reach
the Docker-mapped port.

```text
#15 naming to docker.io/library/petclinic:spec24 done
#15 DONE 1.3s

d94f347c54d07266d1032927156ab080016c5e4d72996be4691c7eea3bf24bbf

{"groups":["liveness","readiness"],"status":"UP"}

2026-05-18T13:37:06.109Z  INFO 1 --- [           main] o.s.s.petclinic.PetClinicApplication     : The following 1 profile is active: "deploy"
2026-05-18T13:37:08.517Z  INFO 1 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint beneath base path '/actuator'
2026-05-18T13:37:08.553Z  INFO 1 --- [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
```

## Artifact: Repository-wide test gate attempt

**What it proves:** The parent-task completion path included the repository test
suite, and the remaining failures are environment-specific rather than deploy
health-contract regressions.

**Why it matters:** Reviewers need to distinguish actual task regressions from
sandbox limitations before evaluating the proof set.

**Command:**

```bash
./mvnw test
```

**Result summary:** The full suite reached existing random-port integration
tests and failed when embedded Tomcat attempted to bind sockets in this
environment. The targeted deploy-health test passed; the remaining failure is
the sandbox limitation `java.net.SocketException: Operation not permitted`.

```text
[ERROR] Tests run: 211, Failures: 0, Errors: 4, Skipped: 5
[ERROR]   PetClinicIntegrationTests.testFindAll » IllegalState Failed to load ApplicationContext
[ERROR]   PetClinicIntegrationTests.testOwnerDetails » IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR]   CrashControllerIntegrationTests.testTriggerExceptionHtml » IllegalState Failed to load ApplicationContext
[ERROR]   CrashControllerIntegrationTests.testTriggerExceptionJson » IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR] Caused by: java.net.SocketException: Operation not permitted
```

## Reviewer Conclusion

These artifacts show the deploy profile now implements a minimal,
health-oriented actuator contract: `/actuator/health` is the only published
HTTP actuator endpoint, it works from the running container boundary, and the
repository now documents one stable health-check path and loopback command for
downstream infrastructure reuse.
