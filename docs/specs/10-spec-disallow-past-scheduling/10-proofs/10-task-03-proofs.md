# Proofs: Task 03 — Implement VisitValidator and wire into VisitController (GREEN)

Covers: AC-1.a, AC-1.b, AC-3.a, AC-3.b, AC-3.c, AC-3.d

## Planned evidence

- Output of `./mvnw test -Dtest="VisitValidatorTests,VisitControllerTests"` showing **BUILD SUCCESS** with all tests passing (GREEN phase confirmation)
- Listing of `src/main/java/org/springframework/samples/petclinic/owner/VisitValidator.java` confirming file creation

## Completion notes

### AC-1.b / AC-3.a / AC-3.b: VisitValidatorTests all pass (GREEN)

```text
$ ./mvnw test -Dtest="VisitValidatorTests,VisitControllerTests"
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  6.278 s
[INFO] Finished at: 2026-05-06T14:52:12-05:00
```

### AC-1.a: testProcessNewVisitFormPastDateRejected asserts field error, status 200, form view

Added to `VisitControllerTests.java`:

- POSTs `date = LocalDate.now().minusDays(1).toString()` and a valid description
- Asserts `model().attributeHasFieldErrors("visit", "date")`, `status().isOk()`, `view().name("pets/createOrUpdateVisitForm")`
- Included in the 8 VisitControllerTests that passed above.

### AC-3.c: testProcessNewVisitFormTodayAccepted asserts 3xx redirect

Added to `VisitControllerTests.java`:

- POSTs `date = LocalDate.now().toString()` and a valid description
- Asserts `status().is3xxRedirection()`
- Included in the 8 VisitControllerTests that passed above.

### AC-3.d: testProcessNewVisitFormFutureDateAccepted asserts 3xx redirect

Added to `VisitControllerTests.java`:

- POSTs `date = LocalDate.now().plusDays(1).toString()` and a valid description
- Asserts `status().is3xxRedirection()`
- Included in the 8 VisitControllerTests that passed above.

### AC-4.a: Pre-existing controller tests continue to pass

All 8 VisitControllerTests pass including the pre-existing: `testInitNewVisitForm`, `testProcessNewVisitFormSuccess`, `testInitNewVisitFormOwnerNotFound`, `testInitNewVisitFormPetNotFound`, `testProcessNewVisitFormHasErrors`.

### Files created/modified

- Created: `src/main/java/org/springframework/samples/petclinic/owner/VisitValidator.java`
- Modified: `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java` (added `@InitBinder("visit")`)
- Modified: `src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java` (added 3 new test methods)

### Notes

The spec bullet said to add `dataBinder.addValidators(new VisitValidator())` to the existing unscoped `@InitBinder`. However, an unscoped `@InitBinder` applies to ALL model attributes in the controller (including `Owner`), causing `IllegalStateException: Invalid target for Validator` when Spring tried to apply `VisitValidator` to an `Owner` object.

Fix: added a second `@InitBinder("visit")` method scoped to the `visit` model attribute only, mirroring the `PetController` pattern (`@InitBinder("pet")`). The existing `setDisallowedFields("id")` binder remains unchanged. This satisfies the spec intent without breaking existing behavior.
