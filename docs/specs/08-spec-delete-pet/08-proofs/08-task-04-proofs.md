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

(Filled in by `implement-sdd-spec`.)
