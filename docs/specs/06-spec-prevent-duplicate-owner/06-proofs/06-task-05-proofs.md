# Proofs: Task 05 — Update OwnerController to wire OwnerService duplicate check (GREEN)

Covers: AC-3.a, AC-3.b, AC-3.c

## Planned evidence

- `OwnerController.java` diff showing updated constructor parameter and the `isDuplicate` guard block in `processCreationForm`.
- `./mvnw test -Dtest=OwnerControllerTests` passing output confirming `testProcessCreationFormDuplicateRejected` and all pre-existing controller tests pass (GREEN phase).

## Completion notes

### AC-3.a, AC-3.b, AC-3.c: `OwnerController.java` updated

Changes applied to `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java`:

1. Added `OwnerService ownerService` constructor parameter alongside `OwnerRepository owners`.
2. Inserted duplicate check in `processCreationForm` after `@Valid`/`BindingResult` guard, before `owners.save()`:

```java
if (ownerService.isDuplicate(owner.getFirstName(), owner.getLastName(), owner.getTelephone())) {
    result.reject("duplicate", "An owner with this name and telephone already exists.");
    return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
}
```

### GREEN phase: `./mvnw test -Dtest=OwnerControllerTests`

```text
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.835 s -- in org.springframework.samples.petclinic.owner.OwnerControllerTests
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

All 19 controller tests pass including `testProcessCreationFormDuplicateRejected` (AC-3.a, AC-3.b) and the existing `testProcessCreationFormSuccess` (AC-3.c). GREEN phase confirmed.
