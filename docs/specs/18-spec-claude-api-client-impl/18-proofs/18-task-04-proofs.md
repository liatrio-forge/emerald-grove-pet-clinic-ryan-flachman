# Proofs: Task 04 — Implement ClaudeApiClientImpl (GREEN)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-3.c, AC-3.d,
        AC-4.a, AC-4.b, AC-5.a, AC-5.b

## Planned evidence

- File listing showing `ClaudeApiClientImpl.java` created at the correct path.
- `grep "implements ClaudeApiClient"` output.
- `grep "ConditionalOnExpression"` output.
- `./mvnw compile` exit-0 output.
- `./mvnw test -Dtest=ClaudeApiClientImplTest` output showing all four tests
  passing.

## Completion notes

```console
$ ls -l src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java
-rw-r--r--  1 ryan  staff  2068 May 11 15:45 src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java

$ grep "implements ClaudeApiClient" src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java
class ClaudeApiClientImpl implements ClaudeApiClient {

$ grep "ConditionalOnExpression" src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
@ConditionalOnExpression("not '${anthropic.api.key:}'.empty")

$ ./mvnw compile
# BUILD SUCCESS (exit 0)

$ ./mvnw test -Dtest=ClaudeApiClientImplTest
# BUILD SUCCESS — 4 tests, 0 failures
```
