# Validation: Delete a Pet from Owner (with Confirmation) (08)

## Automated verification

From repository root:

```bash
# AC-10.a — full Java test suite
./mvnw test

# AC-4.b / AC-4.c / AC-5.a / AC-6.a / AC-7.a — PetController delete tests
./mvnw test -Dtest=PetControllerTests

# AC-10.b — coverage report (open target/site/jacoco/index.html; verify ≥90% on new handler)
./mvnw test jacoco:report

# AC-1.a — Delete trigger has data-pet-name attribute
grep -n "data-pet-name" src/main/resources/templates/owners/ownerDetails.html

# AC-1.b — Delete trigger has data-visit-count attribute
grep -n "data-visit-count" src/main/resources/templates/owners/ownerDetails.html

# AC-2.a — Modal element present
grep -n "deletePetModal" src/main/resources/templates/owners/ownerDetails.html

# AC-2.b — Confirm button present with "Delete anyway" variant
grep -n "Delete anyway\|confirmDeleteBtn" src/main/resources/templates/owners/ownerDetails.html

# AC-2.c — Hidden delete form present
grep -n "deleteForm\|/delete" src/main/resources/templates/owners/ownerDetails.html

# AC-3.a — @PostMapping for /pets/{petId}/delete in PetController
grep -n "pets/{petId}/delete\|/delete" src/main/java/org/springframework/samples/petclinic/owner/PetController.java

# AC-4.a — orphanRemoval = true on Owner.pets
grep -n "orphanRemoval" src/main/java/org/springframework/samples/petclinic/owner/Owner.java

# AC-7.b — flash message text in PetController
grep -n "Pet has been deleted" src/main/java/org/springframework/samples/petclinic/owner/PetController.java

# AC-8.a / AC-8.b — delete test present in pet-management.spec.ts
grep -n "delete\|Delete" e2e-tests/tests/features/pet-management.spec.ts

# AC-9.a / AC-9.b — with-visit delete test present
grep -n "visit.*delete\|delete.*visit\|withVisit\|with-visit\|Delete anyway" e2e-tests/tests/features/pet-management.spec.ts

# AC-8.c / AC-9.c — full Pet Management E2E suite
cd e2e-tests && npm test -- --grep "Pet Management"
```

**Expected:**

- `./mvnw test` exits 0; `BUILD SUCCESS`; all `PetControllerTests` pass
  including `testDeletePetSuccess`, `testDeletePetWithVisitsCascade`,
  `testDeletePetOwnerNotFound`, and `testDeletePetNotFound`.
- `grep -n "data-pet-name"` prints at least one match in `ownerDetails.html`.
- `grep -n "data-visit-count"` prints at least one match in `ownerDetails.html`.
- `grep -n "deletePetModal"` prints at least one match in `ownerDetails.html`.
- `grep -n "Delete anyway\|confirmDeleteBtn"` prints at least one match.
- `grep -n "deleteForm\|/delete"` in `ownerDetails.html` prints at least one match.
- `grep -n "pets/{petId}/delete\|/delete"` in `PetController.java` prints at
  least one match.
- `grep -n "orphanRemoval"` in `Owner.java` prints at least one match
  containing `true`.
- `grep -n "Pet has been deleted"` in `PetController.java` prints at least
  one match.
- `grep -n "delete\|Delete"` in `pet-management.spec.ts` prints at least two
  matches (one per new test).
- `grep -n "Delete anyway"` in `pet-management.spec.ts` prints at least one
  match.
- `npm test -- --grep "Pet Management"` exits 0 with all tests passing
  including both new delete tests.

## Traceability

- Feature spec: `08-spec-delete-pet.md`
- Task breakdown: `08-tasks-delete-pet.md`
- Questions and decisions: `08-questions-1-delete-pet.md`
- Per-task evidence: `08-proofs/08-task-NN-proofs.md`
- Upstream specs:
  - `07-spec-friendly-404` — `ResourceNotFoundException` and 404 pattern
    reused by the delete handler
- Parent epic: none

## Manual checks

1. Start the app: `./mvnw spring-boot:run`
2. Navigate to an owner's details page (e.g. `http://localhost:8080/owners/1`).
3. Confirm a "Delete" link/button appears for each pet row.
4. Click Delete on a pet that has **no visits**.
   - Confirm the modal appears with the pet's name.
   - Confirm the modal says "This cannot be undone."
   - Confirm the confirm button reads "Delete" (not "Delete anyway").
   - Click Cancel — confirm the pet remains on the page.
   - Click Delete again, then confirm — verify the pet disappears from the
     page and a success flash message is shown.
5. Add a new pet, add a visit to it, then click Delete on that pet.
   - Confirm the modal shows the visit count warning:
     "This will also permanently delete 1 visit record(s). This cannot be
     undone."
   - Confirm the confirm button reads "Delete anyway".
   - Click "Delete anyway" — verify the pet disappears and neither the pet nor
     its visit appears on any subsequent page.
6. Attempt to navigate directly to a delete URL with an invalid owner ID:
   `POST http://localhost:8080/owners/99999/pets/1/delete` (use curl or a
   browser form). Confirm the response is HTTP 404.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `data-pet-name` attribute on delete trigger in `ownerDetails.html` | `08-proofs/08-task-03-proofs.md` | command output | PASS |
| AC-1.b | `data-visit-count` attribute on delete trigger in `ownerDetails.html` | `08-proofs/08-task-03-proofs.md` | command output | PASS |
| AC-2.a | Modal element with `deletePetModal` id in `ownerDetails.html` | `08-proofs/08-task-03-proofs.md` | command output | PASS |
| AC-2.b | Confirm button with "Delete anyway" variant in `ownerDetails.html` | `08-proofs/08-task-03-proofs.md` | command output | PASS |
| AC-2.c | Hidden delete form with `/delete` action in `ownerDetails.html` | `08-proofs/08-task-03-proofs.md` | command output | PASS |
| AC-3.a | `@PostMapping` for `/pets/{petId}/delete` in `PetController.java` | `08-proofs/08-task-02-proofs.md` | file edit | PASS |
| AC-4.a | `orphanRemoval = true` on `Owner.pets` `@OneToMany` | `08-proofs/08-task-02-proofs.md` | file edit | PASS |
| AC-4.b | `testDeletePetSuccess` asserts `status().is3xxRedirection()` and passes | `08-proofs/08-task-02-proofs.md` | Maven test pass | PASS |
| AC-4.c | `testDeletePetWithVisitsCascade` asserts `status().is3xxRedirection()` and passes | `08-proofs/08-task-02-proofs.md` | Maven test pass | PASS |
| AC-5.a | `testDeletePetOwnerNotFound` asserts `status().isNotFound()` and passes | `08-proofs/08-task-02-proofs.md` | Maven test pass | PASS |
| AC-6.a | `testDeletePetNotFound` asserts `status().isNotFound()` and passes | `08-proofs/08-task-02-proofs.md` | Maven test pass | PASS |
| AC-7.a | `testDeletePetSuccess` asserts redirect to `/owners/*` | `08-proofs/08-task-02-proofs.md` | Maven test pass | PASS |
| AC-7.b | `"Pet has been deleted"` flash attribute set in `PetController.java` | `08-proofs/08-task-02-proofs.md` | file edit | PASS |
| AC-8.a | Playwright test creates and deletes pet (no visits), verifies removal | `08-proofs/08-task-04-proofs.md` | Playwright screenshot | PASS |
| AC-8.b | Screenshot of confirmation modal (no-visit variant) captured | `08-proofs/08-task-04-proofs.md` | Playwright screenshot | PASS |
| AC-8.c | `npm test -- --grep "Pet Management"` exits 0 | `08-proofs/08-task-04-proofs.md` | command output | PASS |
| AC-9.a | Playwright test creates pet, adds visit, deletes pet, verifies removal | `08-proofs/08-task-04-proofs.md` | Playwright screenshot | PASS |
| AC-9.b | Screenshot of modal with visit-count warning captured | `08-proofs/08-task-04-proofs.md` | Playwright screenshot | PASS |
| AC-9.c | `npm test -- --grep "Pet Management"` exits 0 (with-visit test passing) | `08-proofs/08-task-04-proofs.md` | command output | PASS |
| AC-10.a | `./mvnw test` exits 0 | `08-proofs/08-task-05-proofs.md` | Maven test pass | PENDING |
| AC-10.b | JaCoCo ≥90% line coverage on new handler and changed code | `08-proofs/08-task-05-proofs.md` | JaCoCo coverage report | PENDING |

## Definition of done

- [ ] AC-1.a: `data-pet-name` attribute on delete trigger in `ownerDetails.html`
- [ ] AC-1.b: `data-visit-count` attribute on delete trigger in `ownerDetails.html`
- [ ] AC-2.a: Modal element with `deletePetModal` id in `ownerDetails.html`
- [ ] AC-2.b: Confirm button with "Delete anyway" variant in `ownerDetails.html`
- [ ] AC-2.c: Hidden delete form with `/delete` action in `ownerDetails.html`
- [ ] AC-3.a: `@PostMapping` for `/pets/{petId}/delete` in `PetController.java`
- [ ] AC-4.a: `orphanRemoval = true` on `Owner.pets` `@OneToMany`
- [ ] AC-4.b: `testDeletePetSuccess` asserts `status().is3xxRedirection()` and passes
- [ ] AC-4.c: `testDeletePetWithVisitsCascade` asserts `status().is3xxRedirection()` and passes
- [ ] AC-5.a: `testDeletePetOwnerNotFound` asserts `status().isNotFound()` and passes
- [ ] AC-6.a: `testDeletePetNotFound` asserts `status().isNotFound()` and passes
- [ ] AC-7.a: `testDeletePetSuccess` asserts redirect to `/owners/*`
- [ ] AC-7.b: `"Pet has been deleted"` flash attribute set in `PetController.java`
- [ ] AC-8.a: Playwright test creates and deletes pet (no visits), verifies removal
- [ ] AC-8.b: Screenshot of confirmation modal (no-visit variant) captured
- [ ] AC-8.c: `npm test -- --grep "Pet Management"` exits 0
- [ ] AC-9.a: Playwright test creates pet, adds visit, deletes pet, verifies removal
- [ ] AC-9.b: Screenshot of modal with visit-count warning captured
- [ ] AC-9.c: `npm test -- --grep "Pet Management"` exits 0 (with-visit test)
- [ ] AC-10.a: `./mvnw test` exits 0
- [ ] AC-10.b: JaCoCo ≥90% line coverage on new/changed code
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
