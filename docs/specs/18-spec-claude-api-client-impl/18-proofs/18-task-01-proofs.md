# Proofs: Task 01 — Write failing ClaudeApiExceptionTest (RED)

Covers: AC-6.a

## Planned evidence

- Maven output (`./mvnw test -Dtest=ClaudeApiExceptionTest`) showing a
  compile error because `ClaudeApiException` does not yet exist.

## Completion notes

With `ClaudeApiClientImplTest.java` temporarily removed from `src/test` and
`ClaudeApiException.java` absent from `src/main`, `./mvnw test -Dtest=ClaudeApiExceptionTest`
fails at `testCompile` with cannot find symbol `ClaudeApiException` in
`ClaudeApiExceptionTest` (RED). Excerpt:

```text
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.14.1:testCompile (default-testCompile) on project spring-petclinic: Compilation failure
[ERROR] /.../ClaudeApiExceptionTest.java:[15,32] cannot find symbol
[ERROR]   symbol:   class ClaudeApiException
[ERROR]   location: class org.springframework.samples.petclinic.owner.ClaudeApiExceptionTest
```
