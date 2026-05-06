# Proofs: Task 02 — Write failing PetController and VisitController 404 tests (RED)

Covers: AC-6.a, AC-7.a

## Planned evidence

- `./mvnw test -Dtest=PetControllerTests` output showing `testInitUpdatePetFormNotFound`
  FAILED (confirming RED state — null pet produces 500, not 404).
- `./mvnw test -Dtest=VisitControllerTests` output showing `testInitNewVisitFormOwnerNotFound`
  FAILED (confirming RED state — IllegalArgumentException produces 500, not 404).

## Completion notes

### AC-6.a: `testInitUpdatePetFormNotFound` asserts `status().isNotFound()` and passes

**RED phase — confirms failure before null guard is added.**

```text
$ ./mvnw test -Dtest="PetControllerTests,VisitControllerTests"

[ERROR]   PetControllerTests.testInitUpdatePetFormNotFound » Servlet Request processing failed:
          org.thymeleaf.exceptions.TemplateProcessingException: Exception evaluating SpringEL expression:
          "pet['new']" (template: "pets/createOrUpdatePetForm" - line 8, col 15)

[ERROR] Tests run: 15, Failures: 0, Errors: 2, Skipped: 0

[INFO] BUILD FAILURE
```

`owner.getPet(999)` returns null (no guard). Thymeleaf NPE → 500, not 404.
GREEN evidence captured in Task 03 proof.

### AC-7.a: `testInitNewVisitFormOwnerNotFound` asserts `status().isNotFound()` and passes

**RED phase — confirms failure before ResourceNotFoundException is introduced.**

```text
[ERROR]   VisitControllerTests.testInitNewVisitFormOwnerNotFound » Servlet Request processing failed:
          java.lang.IllegalArgumentException: Owner not found with id: 999. Please ensure the ID is correct

[INFO] Tests run: 15, Failures: 0, Errors: 2, Skipped: 0

[INFO] BUILD FAILURE
```

`IllegalArgumentException` (no `@ResponseStatus`) → 500, not 404.
GREEN evidence captured in Task 03 proof.
