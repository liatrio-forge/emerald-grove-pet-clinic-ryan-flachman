# Task 02 Proofs - Deploy-profile startup and fixed-port runtime contract

## Task Summary

This task defines the deployed runtime contract for the application. The repository
now has a dedicated `deploy` profile with an explicit `8080` port, environment-driven
runtime overrides, and a Docker startup contract that runs one foreground Spring Boot
process without baking secrets into the image.

## What This Task Proves

- The deploy profile is isolated in `application-deploy.properties` and sets an
  explicit deployed runtime port of `8080`.
- Runtime values that vary by environment are supplied through environment-variable
  overrides instead of being hard-coded into the image.
- The repository-owned Docker startup contract launches the packaged jar as one
  foreground JVM process and documents deploy-profile activation in source control.
- Running `docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=deploy petclinic:spec24`
  followed by `curl http://localhost:8080/` returns a successful HTTP response.

## Evidence Summary

- `DeployProfileRuntimeContractTest` passes and verifies both the deploy properties
  file contract and the Dockerfile deploy-profile activation contract.
- `application-deploy.properties` sets `server.port=8080` and keeps datasource and
  Anthropic credentials overrideable through standard environment variables.
- `Dockerfile` keeps the container startup path as a single foreground
  `java -jar /app/app.jar` process and declares `SPRING_PROFILES_ACTIVE=deploy`.
- A live `docker run` smoke check showed the `deploy` profile activating, Tomcat
  binding to port `8080`, and `curl http://localhost:8080/` returning `HTTP 200`.
- The repository-wide `./mvnw test` quality gate was attempted and failed in existing
  random-port integration tests because this sandbox blocks socket binding with
  `java.net.SocketException: Operation not permitted`.

## Artifact: Deploy profile runtime configuration

**What it proves:** The deploy profile owns the fixed port and environment-variable
override contract for deployed runtime behavior.

**Why it matters:** Downstream ECS and ALB work needs one explicit runtime contract
 instead of inheriting local-development defaults implicitly.

**Artifact path:** `src/main/resources/application-deploy.properties`

**Result summary:** The file explicitly sets port `8080`, leaves datasource
 connectivity overrideable with `SPRING_DATASOURCE_*`, and keeps Anthropic settings
 runtime-injected rather than image-baked.

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
anthropic.api.url=${ANTHROPIC_API_URL:https://api.anthropic.com/v1/messages}
anthropic.model=${ANTHROPIC_MODEL:claude-haiku-4-5-20251001}
```

## Artifact: Docker startup contract

**What it proves:** The repository-owned Dockerfile starts one foreground JVM process
 and documents deploy-profile activation as part of the runtime contract.

**Why it matters:** Container orchestration depends on a stable foreground process and
 a predictable activation path for deployed settings.

**Artifact path:** `Dockerfile`

**Result summary:** The runtime stage copies only the packaged jar, declares
 `SPRING_PROFILES_ACTIVE=deploy`, and starts the application with one
 `java -jar /app/app.jar` entrypoint.

```dockerfile
FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/

RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=deploy

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

## Artifact: Automated deploy runtime contract test

**What it proves:** The deploy profile and Docker startup contract are enforced by an
 automated regression test.

**Why it matters:** Future configuration changes will fail fast if they remove the
 fixed-port or deploy-profile activation behavior.

**Command:**

```bash
./mvnw -Dtest=DeployProfileRuntimeContractTest test
```

**Result summary:** The focused system test suite passed and verified both the
 dedicated deploy properties file contract and the Dockerfile requirement to
 activate the deploy profile through `SPRING_PROFILES_ACTIVE=deploy`.

```text
[INFO] Running org.springframework.samples.petclinic.system.DeployProfileRuntimeContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Container startup smoke check

**What it proves:** The documented runtime command starts the application on port
 `8080` and serves HTTP traffic successfully.

**Why it matters:** A source-level contract is not sufficient if the documented
 runtime startup path does not actually reach a working HTTP endpoint.

**Command:**

```bash
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=deploy petclinic:spec24
curl -sS -o /tmp/spec24-home.html -w 'HTTP_STATUS=%{http_code}\nCONTENT_TYPE=%{content_type}\n' http://localhost:8080/
sed -n '1,20p' /tmp/spec24-home.html
```

**Result summary:** The container logs showed the `deploy` profile activating and
 Tomcat starting on port `8080`. The HTTP smoke check returned `200` and the page
 body included the Emerald Grove application title, confirming the runtime and port
 mapping work as documented.

```text
2026-05-15T21:11:33.116Z  INFO 1 --- [           main] o.s.s.petclinic.PetClinicApplication     : The following 1 profile is active: "deploy"
2026-05-15T21:11:35.930Z  INFO 1 --- [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
HTTP_STATUS=200
CONTENT_TYPE=text/html;charset=UTF-8
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Emerald Grove Veterinary Clinic</title>
```

## Artifact: Repository-wide quality gate attempt

**What it proves:** The parent-task completion path included the repository-wide test
 command, and the remaining failure is environment-specific rather than caused by the
 new deploy-profile contract.

**Why it matters:** Reviewers need to distinguish task regressions from sandbox
 limitations before relying on the proof set.

**Command:**

```bash
./mvnw test
```

**Result summary:** The full suite reached existing random-port integration tests and
 failed when embedded Tomcat attempted to bind a socket in this environment. The task-
 specific runtime contract test passed; the repository-wide failure is the sandbox
 restriction `java.net.SocketException: Operation not permitted`, not a deploy-profile
 regression introduced by this task.

```text
[ERROR] Tests run: 209, Failures: 0, Errors: 4, Skipped: 5
[ERROR] Caused by: java.net.SocketException: Operation not permitted
[ERROR]   CrashControllerIntegrationTests.testTriggerExceptionHtml » IllegalState Failed to load ApplicationContext
[ERROR]   PetClinicIntegrationTests.testFindAll » IllegalState Failed to load ApplicationContext
```

## Reviewer Conclusion

These artifacts show that the repository now defines a deploy-specific runtime
 contract with a fixed `8080` port, environment-driven overrides, and a Docker
 startup path that runs the packaged Spring Boot application in the foreground. The
 targeted runtime contract verification and live container smoke test both passed; the
 only incomplete quality gate is an environment-specific socket-binding limitation in
 existing repository integration tests.
