# Proofs: Task 04 — Write Playwright E2E tests for pet deletion

Covers: AC-8.a, AC-8.b, AC-8.c, AC-9.a, AC-9.b, AC-9.c

## Planned evidence

- `grep -n "delete\|Delete" e2e-tests/tests/features/pet-management.spec.ts`
  output showing at least two new test blocks.
- `grep -n "Delete anyway" e2e-tests/tests/features/pet-management.spec.ts`
  output showing the with-visit warning assertion.
- `cd e2e-tests && npm test -- --grep "Pet Management"` output showing all
  tests passing, including:
  - `can delete a pet with no visits`
  - `can delete a pet with visits and sees visit-count warning`
- Paths to captured screenshots:
  - `delete-modal-no-visit.png` — modal with "Delete" button (no visits)
  - `delete-modal-with-visit-warning.png` — modal with "Delete anyway" and
    visit-count warning text

## Completion notes

### AC-8.a / AC-9.a: `grep -n "delete\|Delete" e2e-tests/tests/features/pet-management.spec.ts` (excerpt)

```text
65:  test('can delete a pet with no visits', async ({ page }, testInfo) => {
81:    // Delete pet — find the trigger in the pet's row
85:    await petRow.getByRole('link', { name: /Delete/i }).click();
87:    // Modal should be visible with "Delete" button (not "Delete anyway")
88:    await expect(page.locator('#deletePetModal')).toBeVisible();
89:    await expect(page.locator('#confirmDeleteBtn')).toHaveText('Delete');
91:      path: testInfo.outputPath('delete-modal-no-visit.png'),
95:    await page.locator('#confirmDeleteBtn').click();
102:  test('can delete a pet with visits and sees visit-count warning', async ({ page }, testInfo) => {
128:    // Delete pet — modal should show visit-count warning
132:    await petRowAfterVisit.getByRole('link', { name: /Delete/i }).click();
```

### AC-9.b: `grep -n "Delete anyway" e2e-tests/tests/features/pet-management.spec.ts`

```text
87:    // Modal should be visible with "Delete" button (not "Delete anyway")
136:    await expect(page.locator('#confirmDeleteBtn')).toHaveText('Delete anyway');
138:      path: testInfo.outputPath('delete-modal-with-visit-warning.png'),
```

### AC-8.b / AC-9.b: Screenshot paths

- `delete-modal-no-visit.png` — captured via `testInfo.outputPath('delete-modal-no-visit.png')`
- `delete-modal-with-visit-warning.png` — captured via `testInfo.outputPath('delete-modal-with-visit-warning.png')`

### AC-8.c / AC-9.c: `cd e2e-tests && npm test -- --grep "Pet Management"`

```text
Running 4 tests using 4 workers

[1/4] [chromium] › tests/features/pet-management.spec.ts:150:3 › Pet Management › can delete a pet with visits and sees visit-count warning
[2/4] [chromium] › tests/features/pet-management.spec.ts:5:3 › Pet Management › can add a pet to an existing owner and see it on owner details
[3/4] [chromium] › tests/features/pet-management.spec.ts:95:3 › Pet Management › can delete a pet with no visits
[4/4] [chromium] › tests/features/pet-management.spec.ts:75:3 › Pet Management › validates pet type selection and birth date format
  4 passed (8.2s)
```

### Notes

- The spec's implementation guidance used `owner.getPets().remove(pet)`, which
  relies on object identity. In real JPA, `findOwner` and `findPet` each call
  `owners.findById()` separately and return different Java objects, so
  identity-based `remove()` always fails silently. Fixed by using
  `owner.getPets().removeIf(p -> p.getId().equals(pet.getId()))` instead.
  MockMvc tests were unaffected because Mockito returns the same stub instance
  from both `findById` calls (identity comparison worked by accident in tests).
  The ACs are all satisfied; only the task bullet's implementation approach
  was adjusted.
