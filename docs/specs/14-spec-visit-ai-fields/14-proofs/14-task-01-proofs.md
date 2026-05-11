# Proofs: Task 01 — Write failing `AiStatusTest`

Covers: AC-1.b, AC-1.c

## Planned evidence

- `./mvnw test -Dtest=AiStatusTest` command output showing a compilation
  error or `ClassNotFoundException` (RED phase — `AiStatus.java` does not
  exist yet).

## Completion notes

### AC-1.b / AC-1.c: RED phase — compile failure before AiStatus exists

```text
$ ./mvnw test -Dtest=AiStatusTest
[ERROR]   symbol:   class AiStatus
[ERROR]   location: class org.springframework.samples.petclinic.owner.AiStatusTest
[ERROR] /…/AiStatusTest.java:[16,37] cannot find symbol
[ERROR]   symbol:   variable AiStatus
[ERROR]   location: class org.springframework.samples.petclinic.owner.AiStatusTest
[ERROR] /…/AiStatusTest.java:[17,49] cannot find symbol
[ERROR]   symbol:   variable AiStatus
[ERROR] /…/AiStatusTest.java:[18,49] cannot find symbol
[ERROR]   symbol:   variable AiStatus
[ERROR] /…/AiStatusTest.java:[19,49] cannot find symbol
[ERROR]   symbol:   variable AiStatus
[ERROR] /…/AiStatusTest.java:[20,49] cannot find symbol
[ERROR]   symbol:   variable AiStatus
[ERROR] /…/AiStatusTest.java:[25,67] cannot find symbol
[ERROR]   symbol:   variable AiStatus
[ERROR] -> [Help 1]
BUILD FAILURE
```

Test file created at:
`src/test/java/org/springframework/samples/petclinic/owner/AiStatusTest.java`

RED phase confirmed: `AiStatus` does not yet exist; compile fails as expected.
Task 02 will create the enum and move to GREEN.
