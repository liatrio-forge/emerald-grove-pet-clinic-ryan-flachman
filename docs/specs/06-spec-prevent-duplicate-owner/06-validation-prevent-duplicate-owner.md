# Validation: Prevent Duplicate Owner Creation (06)

## Automated verification

From repository root:

```bash
# AC-6.a — full Java test suite
./mvnw test

# AC-2.a, AC-2.b — OwnerService unit tests
./mvnw test -Dtest=OwnerServiceTests

# AC-3.a, AC-3.b, AC-3.c — OwnerController tests including duplicate path
./mvnw test -Dtest=OwnerControllerTests

# AC-6.b — coverage report (open target/site/jacoco/index.html; verify ≥90% on OwnerService and OwnerController)
./mvnw test jacoco:report

# AC-1.a — confirm repository method exists
grep -n "existsByFirstNameIgnoreCase" src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java

# AC-4.a — confirm template has global error block
grep -n "hasGlobalErrors" src/main/resources/templates/owners/createOrUpdateOwnerForm.html

# AC-5.a — confirm E2E test exists
grep -n '"blocks duplicate owner creation"' e2e-tests/tests/features/owner-management.spec.ts

# AC-5.c — full Owner Management E2E suite
cd e2e-tests && npm test -- --grep "Owner Management"
```

**Expected:**

- `./mvnw test` exits 0; `BUILD SUCCESS`; all `OwnerServiceTests` and
  `OwnerControllerTests` pass including `testIsDuplicate_returnsTrueWhenMatchExists`,
  `testIsDuplicate_returnsFalseWhenNoMatch`, and
  `testProcessCreationFormDuplicateRejected`.
- Both `grep` commands on source files each print at least one matching line.
- `grep` on `owner-management.spec.ts` prints at least one match.
- `npm test -- --grep "Owner Management"` exits 0; all tests pass including
  `"blocks duplicate owner creation"`.

## Traceability

- Feature spec: `06-spec-prevent-duplicate-owner.md`
- Task breakdown: `06-tasks-prevent-duplicate-owner.md`
- Questions and decisions: `06-questions-1-prevent-duplicate-owner.md`
- Per-task evidence: `06-proofs/06-task-NN-proofs.md`
- Upstream specs: none
- Parent epic: none

## Manual checks

1. Start the app: `./mvnw spring-boot:run`
2. Navigate to `http://localhost:8080/owners/new`.
3. Create a new owner (e.g. first name `Test`, last name `Owner`, telephone `5555555555`).
4. Navigate back to `http://localhost:8080/owners/new`.
5. Submit the form again with the same first name, last name, and telephone.
6. Confirm the page does **not** redirect — it stays on the creation form.
7. Confirm a visible error banner appears at the top of the form containing
   text like "An owner with this name and telephone already exists."
8. Confirm the form fields are still populated with the submitted values.
9. Navigate to Find Owners and confirm only one record exists for that name/telephone.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `OwnerRepository` declares `existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone` | `06-proofs/06-task-01-proofs.md` | file creation | PASS |
| AC-2.a | `testIsDuplicate_returnsTrueWhenMatchExists` passes | `06-proofs/06-task-04-proofs.md` | Maven test pass | PASS |
| AC-2.b | `testIsDuplicate_returnsFalseWhenNoMatch` passes | `06-proofs/06-task-04-proofs.md` | Maven test pass | PASS |
| AC-3.a | `testProcessCreationFormDuplicateRejected`: HTTP 200, view `createOrUpdateOwnerForm`, global error code `"duplicate"` | `06-proofs/06-task-05-proofs.md` | Maven test pass | PASS |
| AC-3.b | `testProcessCreationFormDuplicateRejected`: `owners.save()` never invoked | `06-proofs/06-task-05-proofs.md` | Maven test pass | PASS |
| AC-3.c | Existing `testProcessCreationFormSuccess` still returns 3xx redirect | `06-proofs/06-task-05-proofs.md` | Maven test pass | PASS |
| AC-4.a | `createOrUpdateOwnerForm.html` contains `th:if="${#fields.hasGlobalErrors()}"` | `06-proofs/06-task-06-proofs.md` | file edit | PASS |
| AC-5.a | `owner-management.spec.ts` contains `"blocks duplicate owner creation"` | `06-proofs/06-task-03-proofs.md` | file creation | PASS |
| AC-5.b | Playwright test submits duplicate and asserts visible error, no redirect | `06-proofs/06-task-06-proofs.md` | Playwright screenshot | PASS |
| AC-5.c | `npm test -- --grep "Owner Management"` exits 0 | `06-proofs/06-task-06-proofs.md` | command output | PASS |
| AC-6.a | `./mvnw test` exits 0 | `06-proofs/06-task-06-proofs.md` | Maven test pass | PASS |
| AC-6.b | JaCoCo ≥90% line coverage on `OwnerService` and `OwnerController` | `06-proofs/06-task-07-proofs.md` | JaCoCo coverage report | PASS |

## Definition of done

- [x] AC-1.a: `OwnerRepository` declares `existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone`
- [x] AC-2.a: `testIsDuplicate_returnsTrueWhenMatchExists` passes
- [x] AC-2.b: `testIsDuplicate_returnsFalseWhenNoMatch` passes
- [x] AC-3.a: `testProcessCreationFormDuplicateRejected` returns HTTP 200, correct view, global error `"duplicate"`
- [x] AC-3.b: `owners.save()` never invoked when duplicate detected
- [x] AC-3.c: Existing `testProcessCreationFormSuccess` still passes with 3xx redirect
- [x] AC-4.a: `createOrUpdateOwnerForm.html` contains `th:if="${#fields.hasGlobalErrors()}"`
- [x] AC-5.a: `owner-management.spec.ts` contains `"blocks duplicate owner creation"`
- [x] AC-5.b: Playwright test asserts visible error and no redirect on duplicate submission
- [x] AC-5.c: `cd e2e-tests && npm test -- --grep "Owner Management"` exits 0
- [x] AC-6.a: `./mvnw test` exits 0
- [x] AC-6.b: JaCoCo ≥90% line coverage on `OwnerService` and `OwnerController`
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
