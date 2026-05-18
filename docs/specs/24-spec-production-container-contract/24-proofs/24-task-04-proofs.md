# Task 04 Proofs - Deploy profile isolation and operator runtime contract

## Task Summary

This task isolates deployed-environment behavior behind the dedicated `deploy`
profile and documents how operators activate it at runtime. The deploy profile
now contains only deploy-time operational overrides and runtime hooks instead of
duplicating unrelated local defaults.

## What This Task Proves

- Deploy-only operational overrides are confined to
  `application-deploy.properties`.
- The deploy profile no longer duplicates unrelated shared defaults such as the
  Anthropic URL/model values from `application.properties`.
- The README now documents deploy-profile activation, optional runtime override
  variables, the default H2 proof-of-concept path, and the container commands
  operators should use.
- A live Docker startup shows the `deploy` profile activating without editing
  local profile files or baking secrets into the image.

## Evidence Summary

- `DeployProfileIsolationTest` was written to fail until the deploy profile
  dropped duplicated local-default settings and kept only deploy-specific
  overrides plus runtime hooks.
- `application-deploy.properties` now contains the explicit deployed port,
  datasource override hooks, optional `ANTHROPIC_API_KEY`, and actuator health
  settings only.
- The README now explains how to build and run the image with
  `SPRING_PROFILES_ACTIVE=deploy`, which environment variables are optional, and
  that H2 remains the default proof-of-concept datasource unless overridden.
- A live `docker run` showed the `deploy` profile in startup logs and the H2
  default datasource path in the running container.
- The repository-wide `./mvnw test` gate still fails only in existing
  random-port integration tests because of sandbox socket restrictions, not
  because of the new deploy-profile isolation rules.

## Artifact: Deploy-profile isolation regression test

**What it proves:** The deploy profile stays limited to deploy-time overrides
and does not duplicate unrelated local defaults.

**Why it matters:** This guards against slow config drift where deploy-specific
files accumulate redundant shared configuration and become harder to reason
about.

**Command:**

```bash
./mvnw -Dtest=DeployProfileIsolationTest test
```

**Result summary:** The focused system test passed and verified the deploy
profile keeps port, actuator, datasource override hooks, and optional
`ANTHROPIC_API_KEY` while excluding unrelated local defaults such as
`anthropic.api.url` and `anthropic.model`.

```text
[INFO] Running org.springframework.samples.petclinic.system.DeployProfileIsolationTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Isolated deploy profile configuration

**What it proves:** The deploy profile now contains only deployed-environment
concerns and runtime override hooks.

**Why it matters:** Operators and future infrastructure automation need a clean
boundary between local defaults and deployed runtime behavior.

**Artifact path:** `src/main/resources/application-deploy.properties`

**Result summary:** The file now limits itself to port `8080`, datasource
override hooks with an H2 default, optional runtime-injected API credentials,
and health-oriented actuator settings. It no longer duplicates the Anthropic
URL/model defaults defined in `application.properties`.

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

## Artifact: Operator-facing deploy runtime documentation

**What it proves:** Operators now have one documented activation path and a
clear description of runtime overrides for deployed use.

**Why it matters:** The spec requires downstream Terraform and CI work to reuse
one consistent deploy-profile contract without changing local profile files.

**Artifact path:** `README.md`

**Result summary:** The README now documents the repository-owned Docker build
and run commands, requires `SPRING_PROFILES_ACTIVE=deploy`, lists optional
datasource and API-key overrides, preserves the H2 proof-of-concept default, and
publishes the shared health-check guidance.

~~~markdown
### Deploy Profile Runtime Contract

For deployed environments, use the repository-owned `Dockerfile` and activate the
dedicated `deploy` profile:

```bash
docker build -t petclinic:spec24 .
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=deploy petclinic:spec24
```

Runtime environment variables:

- `SPRING_PROFILES_ACTIVE=deploy` is required to activate deployed settings.
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and
  `SPRING_DATASOURCE_PASSWORD` are optional overrides for runtime database
  connectivity.
- `ANTHROPIC_API_KEY` is an optional runtime-injected external API credential.
~~~

## Artifact: Live deploy-profile startup proof

**What it proves:** The documented runtime command activates the deploy profile
without modifying local profiles, and the running container still uses the H2
proof-of-concept datasource unless overridden.

**Why it matters:** This is the observable runtime proof that the deploy-profile
activation path works as documented and does not depend on image-baked secrets.

**Commands:**

```bash
docker run -d --rm --name petclinic-spec24 -p 8080:8080 -e SPRING_PROFILES_ACTIVE=deploy petclinic:spec24
docker logs petclinic-spec24
curl -fsS http://127.0.0.1:8080/actuator/health
```

**Result summary:** The container logs reported the `deploy` profile as active,
started Tomcat on port `8080`, and showed Hikari using the default
`jdbc:h2:mem:petclinic` datasource. The host-visible health endpoint returned
`UP`, confirming the documented startup path works end-to-end. The host `curl`
was executed outside the sandbox because sandboxed localhost access could not
reach the Docker-mapped port.

```text
2026-05-18T13:37:06.109Z  INFO 1 --- [           main] o.s.s.petclinic.PetClinicApplication     : The following 1 profile is active: "deploy"
2026-05-18T13:37:07.085Z  INFO 1 --- [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection conn0: url=jdbc:h2:mem:petclinic user=SA
2026-05-18T13:37:08.553Z  INFO 1 --- [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
{"groups":["liveness","readiness"],"status":"UP"}
```

## Artifact: Repository-wide test gate attempt

**What it proves:** The broader project test gate was rerun after the deploy
profile isolation work, and the remaining failures are environment-specific.

**Why it matters:** Reviewers should not confuse the sandbox’s socket-binding
restriction with a regression introduced by the deploy profile changes.

**Command:**

```bash
./mvnw test
```

**Result summary:** The full suite still fails only in existing random-port
integration tests because the sandbox blocks embedded Tomcat from binding test
sockets. The new deploy-profile isolation test passed before this broader run.

```text
[ERROR] Tests run: 211, Failures: 0, Errors: 4, Skipped: 5
[ERROR]   PetClinicIntegrationTests.testFindAll » IllegalState Failed to load ApplicationContext
[ERROR]   PetClinicIntegrationTests.testOwnerDetails » IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR]   CrashControllerIntegrationTests.testTriggerExceptionHtml » IllegalState Failed to load ApplicationContext
[ERROR]   CrashControllerIntegrationTests.testTriggerExceptionJson » IllegalState ApplicationContext failure threshold (1) exceeded
[ERROR] Caused by: java.net.SocketException: Operation not permitted
```

## Reviewer Conclusion

These artifacts show the deploy profile is now a clean deployed-runtime boundary:
operators activate it explicitly with `SPRING_PROFILES_ACTIVE=deploy`, runtime
overrides remain environment-driven, H2 stays the default proof-of-concept path
unless overridden, and the running container proves the deploy profile activates
without local file edits or baked-in secrets.
