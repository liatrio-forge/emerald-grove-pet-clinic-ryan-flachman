# Task 01 Proofs - Repository-owned production container build contract

## Task Summary

This task establishes a repository-owned container build contract for the application.
The repository now contains a root `Dockerfile`, a companion `.dockerignore`, and an
automated system test that verifies the multi-stage build shape and runtime image
expectations.

## What This Task Proves

- The repository defines a first-class `Dockerfile` at the root for downstream CI/CD
  and infrastructure automation.
- The image build uses a multi-stage Maven-wrapper build and copies only the packaged
  jar into the runtime stage.
- The build context excludes local and non-runtime inputs that would make image builds
  noisier or less reproducible.
- The container build contract is covered by automated tests and the repository-wide
  test suite passes after stabilizing Mockito execution on this JDK.

## Evidence Summary

- The root `Dockerfile` builds from source with `./mvnw -DskipTests package` in a
  `build` stage and starts the packaged jar from a separate `runtime` stage.
- The root `.dockerignore` excludes repository metadata, local build output, and
  spec artifacts from the Docker build context.
- `./mvnw test -Dtest=ProductionContainerBuildContractTest` passes, proving the
  contract is enforced automatically.
- `docker build -t petclinic:spec24 .` completed successfully and produced an image
  tagged `petclinic:spec24`.
- `./mvnw test` completed successfully after switching Mockito test execution to the
  subclass mock maker for this local JDK environment.

## Artifact: Root Dockerfile contract

**What it proves:** The repository owns a multi-stage container contract that builds
the application from source and runs only the packaged artifact at runtime.

**Why it matters:** Downstream deployment automation can now rely on a single,
versioned, repository-defined image build path instead of guessing how to package or
start the application.

**Artifact path:** `Dockerfile`

**Result summary:** The file defines a `build` stage that runs the Maven wrapper and a
`runtime` stage that copies the packaged jar and starts it with a foreground
`java -jar` entrypoint.

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

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

## Artifact: Docker build context exclusions

**What it proves:** The repository excludes non-runtime inputs from the Docker build
context.

**Why it matters:** This keeps builds leaner and avoids copying local, generated, and
planning-only files into the image build context.

**Artifact path:** `.dockerignore`

**Result summary:** The ignore file excludes git metadata, local build output, and
spec/task artifacts that do not belong in the application image build context.

```text
.agents
.git
.github
.gradle
.idea
.mvn/wrapper/maven-wrapper.jar
.vscode
docs/specs
e2e-tests
target
test-results
*.iml
```

## Artifact: Automated build-contract test

**What it proves:** The repository automatically verifies the Docker build contract.

**Why it matters:** This prevents future regressions in the Dockerfile shape from
silently breaking downstream image builds.

**Command:**

```bash
./mvnw test -Dtest=ProductionContainerBuildContractTest
```

**Result summary:** The focused system test suite passed and confirmed the Dockerfile
exists, uses a multi-stage build, builds with the Maven wrapper, and keeps source
files out of the runtime stage.

```text
[INFO] Running org.springframework.samples.petclinic.system.ProductionContainerBuildContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Repository-root Docker image build

**What it proves:** The application image can be built directly from repository
sources using the repository-owned Docker contract.

**Why it matters:** This is the core runtime handoff that future CI/CD and
infrastructure work will reuse.

**Command:**

```bash
docker build -t petclinic:spec24 .
```

**Result summary:** The Docker build completed successfully, including the Maven
wrapper package step in the build stage and final image export to the local Docker
image store.

```text
#13 [build 6/6] RUN chmod +x mvnw && ./mvnw -DskipTests package
#13 [INFO] BUILD SUCCESS
#15 naming to docker.io/library/petclinic:spec24 done
#15 DONE 1.3s
```

## Artifact: Built image runtime entrypoint

**What it proves:** The produced image starts the packaged application as the
foreground process.

**Why it matters:** This confirms the runtime image contract matches the task
requirement and is suitable for container orchestrators.

**Command:**

```bash
docker image inspect petclinic:spec24 --format 'Entrypoint={{json .Config.Entrypoint}}\nCmd={{json .Config.Cmd}}'
```

**Result summary:** The built image has a single `java -jar /app/app.jar` entrypoint
and no separate `CMD`, matching the explicit runtime contract.

```text
Entrypoint=["java","-jar","/app/app.jar"]
Cmd=null
```

## Artifact: Repository-wide verification after Mockito fix

**What it proves:** The repository test harness runs successfully after fixing the
Mockito inline mock-maker failure on this local JDK.

**Why it matters:** Parent-task completion requires repository quality verification,
and this confirms the new container-contract work did not leave the suite in a broken
state.

**Command:**

```bash
./mvnw test
```

**Result summary:** The full suite completed successfully after configuring Mockito to
use the subclass mock maker under `src/test/resources/mockito-extensions/`.

```text
[INFO] Results:
[INFO] Tests run: 207, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Reviewer Conclusion

These artifacts show that the repository now owns a reproducible production container
build contract: the Dockerfile is in-repo, the build context is intentionally scoped,
the image builds successfully from source, and the contract is protected by automated
tests and a passing repository-wide Maven test run.
