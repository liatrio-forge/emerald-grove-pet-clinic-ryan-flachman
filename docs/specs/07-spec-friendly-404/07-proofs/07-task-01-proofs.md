# Proofs: Task 01 — Write failing OwnerController 404 test (RED)

Covers: AC-5.a

## Planned evidence

- `./mvnw test -Dtest=OwnerControllerTests` output showing `testShowOwnerNotFound`
  FAILED with expected 404 but got 500 (confirming RED state).

## Completion notes

### AC-5.a: `testShowOwnerNotFound` asserts `status().isNotFound()` and passes

**RED phase — test added, confirms failure before production fix.**

```text
$ ./mvnw test -Dtest=OwnerControllerTests

[ERROR] Errors:
[ERROR]   OwnerControllerTests.testShowOwnerNotFound:307 » Servlet Request processing failed:
          java.lang.IllegalArgumentException: Owner not found with id: 999.
          Please ensure the ID is correct and the owner exists in the database.

[INFO] Tests run: 20, Failures: 0, Errors: 1, Skipped: 0

[INFO] BUILD FAILURE
```

Test fails as expected: `IllegalArgumentException` (no `@ResponseStatus`) maps to 500, not 404.
GREEN evidence captured in Task 03 proof after `ResourceNotFoundException` is introduced.
