# Proofs: Task 01 — Write failing unit tests for ClaudeApiClientStub (RED phase)

Covers: AC-2.a, AC-3.a, AC-4.a, AC-5.a, AC-6.a, AC-7.a, AC-8.a, AC-8.b, AC-9.a, AC-9.b, AC-10.a

## Evidence

`ClaudeApiClientStubTests.java` was added first. With tests pointing at `ClaudeApiClientStub` and Jackson imports aligned to Spring Boot 4 (`tools.jackson.databind`), running:

```bash
./mvnw test -Dtest=ClaudeApiClientStubTests
```

failed at `testCompile` because the production class did not exist yet. Excerpt:

```text
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] .../ClaudeApiClientStubTests.java:[...] cannot find symbol
  symbol:   class ClaudeApiClientStub
  location: class org.springframework.samples.petclinic.owner.ClaudeApiClientStubTests
...
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:testCompile ... Compilation failure
...
[INFO] BUILD FAILURE
```

(Initial work used `com.fasterxml.jackson.databind` per the spec text; the project’s Spring Boot 4 stack resolves JSON via `tools.jackson`, so tests were updated to that API before this RED capture.)

## Completion notes

RED phase satisfied: tests existed and did not compile until `ClaudeApiClientStub.java` was introduced in Task 02.
