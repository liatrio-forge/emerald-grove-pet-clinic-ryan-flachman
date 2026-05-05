# Proofs: Task 01 — Write failing OwnerControllerTests (RED)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-4.a

## Planned evidence

- `OwnerRepository.findBySearchCriteria` method signature (with `@Query`) added
  to the interface — show the new method.
- `OwnerControllerTests` updated `@BeforeEach` — show the new stub wiring.
- New test methods added — show `testProcessFindFormByCity`,
  `testProcessFindFormByTelephone`, `testProcessFindFormByCombinedCriteria`,
  `testProcessFindFormInvalidTelephone`, `testProcessFindFormValidPartialTelephone`,
  and the updated `testProcessFindFormNoOwnersFound`.
- Output of `./mvnw test -Dtest=OwnerControllerTests` showing the new tests
  **failing** (RED phase confirmed).

## Completion notes

### AC-2.a / AC-2.b / AC-2.c / AC-2.d / AC-3.a / AC-3.b / AC-4.a: RED phase — tests compile and fail correctly

`OwnerRepository` now declares `findBySearchCriteria` with the full `@Query` and `countQuery`:

```java
@Query(value = "SELECT DISTINCT o FROM Owner o WHERE "
        + "(:lastName IS NULL OR LOWER(o.lastName) LIKE LOWER(CONCAT(:lastName, '%'))) AND "
        + "(:telephone IS NULL OR o.telephone LIKE CONCAT(:telephone, '%')) AND "
        + "(:city IS NULL OR LOWER(o.city) LIKE LOWER(CONCAT(:city, '%')))",
        countQuery = "SELECT COUNT(DISTINCT o) FROM Owner o WHERE "
                + "(:lastName IS NULL OR LOWER(o.lastName) LIKE LOWER(CONCAT(:lastName, '%'))) AND "
                + "(:telephone IS NULL OR o.telephone LIKE CONCAT(:telephone, '%')) AND "
                + "(:city IS NULL OR LOWER(o.city) LIKE LOWER(CONCAT(:city, '%')))")
Page<Owner> findBySearchCriteria(@Param("lastName") String lastName, @Param("telephone") String telephone,
        @Param("city") String city, Pageable pageable);
```

`OwnerControllerTests` `@BeforeEach` updated to stub `findBySearchCriteria` (broad + Franklin-specific) with a `findByLastNameStartingWith(any())` safety-net stub returning an empty page:

```java
@BeforeEach
void setup() {
    Owner george = george();
    given(this.owners.findByLastNameStartingWith(any(), any(Pageable.class))).willReturn(new PageImpl<>(List.of()));
    given(this.owners.findBySearchCriteria(any(), any(), any(), any(Pageable.class)))
        .willReturn(new PageImpl<>(List.of(george, new Owner())));
    given(this.owners.findBySearchCriteria(eq("Franklin"), isNull(), isNull(), any(Pageable.class)))
        .willReturn(new PageImpl<>(List.of(george)));
    given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
    ...
}
```

New and updated tests added: `testProcessFindFormByCity`, `testProcessFindFormByTelephone`,
`testProcessFindFormByCombinedCriteria`, `testProcessFindFormInvalidTelephone`,
`testProcessFindFormValidPartialTelephone`; `testProcessFindFormNoOwnersFound` updated to
assert global error via `BindingResult` lambda; all three existing find-form tests updated to
mock `findBySearchCriteria` instead of `findByLastNameStartingWith`.

`./mvnw test -Dtest=OwnerControllerTests` output (RED):

```text
[ERROR] Failures:
[ERROR]   OwnerControllerTests.testProcessFindFormByCity:195 View name expected:<owners/ownersList> but was:<owners/findOwners>
[ERROR]   OwnerControllerTests.testProcessFindFormByCombinedCriteria:214 View name expected:<owners/ownersList> but was:<owners/findOwners>
[ERROR]   OwnerControllerTests.testProcessFindFormByLastName:167 Range for response status value 200 expected:<REDIRECTION> but was:<SUCCESSFUL>
[ERROR]   OwnerControllerTests.testProcessFindFormByTelephone:205 View name expected:<owners/ownersList> but was:<owners/findOwners>
[ERROR]   OwnerControllerTests.testProcessFindFormInvalidTelephone:221 No errors for field 'telephone' of attribute 'owner'
[ERROR]   OwnerControllerTests.testProcessFindFormNoOwnersFound:178->lambda$testProcessFindFormNoOwnersFound$0:182
expected: 1
 but was: 0
[ERROR]   OwnerControllerTests.testProcessFindFormSuccess:158 View name expected:<owners/ownersList> but was:<owners/findOwners>
[ERROR]   OwnerControllerTests.testProcessFindFormValidPartialTelephone:232 View name expected:<owners/ownersList> but was:<owners/findOwners>

Tests run: 18, Failures: 8, Errors: 0, Skipped: 0
BUILD FAILURE
```

All 8 failures are for the correct reason: controller still routes through
`findByLastNameStartingWith` (no telephone validation, field error instead of global).
10 non-find-form tests pass. RED phase confirmed.
