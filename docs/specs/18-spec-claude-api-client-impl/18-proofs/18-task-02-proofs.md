# Proofs: Task 02 — Implement ClaudeApiException (GREEN)

Covers: AC-1.a, AC-1.b, AC-1.c

## Planned evidence

- File listing showing `ClaudeApiException.java` created at the correct path.
- `grep "extends RuntimeException"` output confirming the superclass.
- Maven test output showing `ClaudeApiExceptionTest` passing.

## Completion notes

```console
$ ls -l src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiException.java
-rw-r--r--  1 ryan  staff  253 May 11 15:41 src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiException.java

$ grep "extends RuntimeException" src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiException.java
class ClaudeApiException extends RuntimeException {

$ ./mvnw test -Dtest=ClaudeApiExceptionTest
# BUILD SUCCESS — both tests pass
```
