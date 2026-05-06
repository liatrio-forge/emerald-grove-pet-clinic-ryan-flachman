# Proofs: Task 05 — Validate and capture proof artifacts

Covers: all

## Planned evidence

- `./mvnw test` full output — `BUILD SUCCESS`, all tests pass.
- `./mvnw test jacoco:report` coverage summary — ≥90% line coverage on
  `PetController.deletePet` and the `Owner.pets` relationship change.
- All structural `grep` outputs from `08-validation-delete-pet.md` returning
  expected matches.
- `cd e2e-tests && npm test -- --grep "Pet Management"` full output — all
  tests pass.
- Confirmation that `delete-modal-no-visit.png` and
  `delete-modal-with-visit-warning.png` exist in the Playwright output path.
- Coverage matrix in `08-validation-delete-pet.md` with all rows set to
  `PASS`.

## Completion notes

### Verification block

#### `./mvnw test`

```text
[INFO] Tests run: 78, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
```

(5 skipped: Docker-dependent TestContainers tests — pre-existing behaviour when
Docker is unavailable; BUILD SUCCESS confirmed.)

#### `./mvnw test jacoco:report` — coverage (from `target/site/jacoco/jacoco.csv`)

```text
GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,...
petclinic,org.springframework.samples.petclinic.owner,PetController,13,246,7,25,3,58,7,24,0,15
petclinic,org.springframework.samples.petclinic.owner,Owner,0,153,4,14,0,44,4,19,0,14
```

Calculated line coverage:

- `PetController`: 58 / (58+3) = **95.1%** ≥ 90% ✅
- `Owner`: 44 / 44 = **100%** ✅

#### Structural grep checks

```text
# AC-3.a
grep -n "pets/{petId}/delete\|/delete" PetController.java
162:	@PostMapping("/pets/{petId}/delete")

# AC-4.a
grep -n "orphanRemoval" Owner.java
64:	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)

# AC-7.b
grep -n "Pet has been deleted" PetController.java
167:		redirectAttributes.addFlashAttribute("message", "Pet has been deleted");

# AC-1.a
grep -n "data-pet-name" ownerDetails.html
79:                 th:data-pet-name="${pet.name}"

# AC-1.b
grep -n "data-visit-count" ownerDetails.html
80:                 th:data-visit-count="${pet.visits.size()}"

# AC-2.a
grep -n "deletePetModal" ownerDetails.html
91: <div id="deletePetModal" ...>
100: ...style.display='none'">
134: ...style.display = 'block';

# AC-2.b
grep -n "Delete anyway\|confirmDeleteBtn" ownerDetails.html
103: <button type="submit" id="confirmDeleteBtn" ...>Delete</button>
120: var confirmBtn = document.getElementById('confirmDeleteBtn');
125: confirmBtn.textContent  = 'Delete anyway';

# AC-2.c
grep -n "deleteForm\|/delete" ownerDetails.html
98:  <form id="deleteForm" method="post" action="">
131:  document.getElementById('deleteForm').action =
132:    '/owners/' + ownerId + '/pets/' + petId + '/delete';

# AC-8.b
grep -n "screenshot.*modal\|modal.*screenshot\|confirm.*png\|no-visit" pet-management.spec.ts
91: path: testInfo.outputPath('delete-modal-no-visit.png'),

# AC-9.b
grep -n "with-visit\|visit.*warning\|Delete anyway" pet-management.spec.ts
136: await expect(page.locator('#confirmDeleteBtn')).toHaveText('Delete anyway');
138: path: testInfo.outputPath('delete-modal-with-visit-warning.png'),
```

#### `cd e2e-tests && npm test -- --grep "Pet Management"`

```text
Running 4 tests using 4 workers

[1/4] Pet Management › can delete a pet with visits and sees visit-count warning
[2/4] Pet Management › can add a pet to an existing owner and see it on owner details
[3/4] Pet Management › can delete a pet with no visits
[4/4] Pet Management › validates pet type selection and birth date format
  4 passed (8.2s)
```

### Coverage matrix

| AC ID | Status |
|-------|--------|
| AC-1.a | PASS |
| AC-1.b | PASS |
| AC-2.a | PASS |
| AC-2.b | PASS |
| AC-2.c | PASS |
| AC-3.a | PASS |
| AC-4.a | PASS |
| AC-4.b | PASS |
| AC-4.c | PASS |
| AC-5.a | PASS |
| AC-6.a | PASS |
| AC-7.a | PASS |
| AC-7.b | PASS |
| AC-8.a | PASS |
| AC-8.b | PASS |
| AC-8.c | PASS |
| AC-9.a | PASS |
| AC-9.b | PASS |
| AC-9.c | PASS |
| AC-10.a | PASS |
| AC-10.b | PASS |
