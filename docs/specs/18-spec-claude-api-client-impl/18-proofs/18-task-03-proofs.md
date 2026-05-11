# Proofs: Task 03 — Write failing ClaudeApiClientImplTest (RED)

Covers: AC-6.b

## Planned evidence

- Maven output (`./mvnw test -Dtest=ClaudeApiClientImplTest`) showing a
  compile error because `ClaudeApiClientImpl` does not yet exist.

## Completion notes

With `ClaudeApiClientImpl.java` removed from `src/main`, `./mvnw test -Dtest=ClaudeApiClientImplTest`
fails at `testCompile` (RED). Excerpt:

```text
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.14.1:testCompile (default-testCompile) on project spring-petclinic: Compilation failure
[ERROR] /.../ClaudeApiClientImplTest.java:[24,17] cannot find symbol
[ERROR]   symbol: class ClaudeApiClientImpl
[ERROR] /.../ClaudeApiClientImplTest.java:[35,17] cannot find symbol
[ERROR]   symbol:   class ClaudeApiClientImpl
```
