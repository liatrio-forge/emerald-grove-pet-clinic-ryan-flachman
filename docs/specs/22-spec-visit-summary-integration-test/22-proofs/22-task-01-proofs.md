# Proofs: Task 01 — Verify Awaitility is on the test classpath

Covers: AC-1.a

## Command output

From repository root:

```bash
./mvnw dependency:tree -Dincludes=org.awaitility:awaitility
```

Relevant line (transitive via `spring-boot-starter-test`):

```text
[INFO]       \- org.awaitility:awaitility:jar:4.3.0:test
```

## Completion notes

No `pom.xml` change required — Awaitility 4.3.0 is already on the test classpath.

`./mvnw test-compile` completed successfully as part of the full verification run (Task 04).
