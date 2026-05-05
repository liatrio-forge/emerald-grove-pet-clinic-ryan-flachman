# Proofs: Task 02 — Write failing Playwright E2E test and update VetPage page object (RED)

Covers: AC-5.a, AC-5.b, AC-5.c, AC-3.a, AC-3.b, AC-3.c, AC-4.a, AC-4.b

## Planned evidence

- `specialtyFilterPills()` and `clickSpecialtyFilter(name)` helpers added to `e2e-tests/tests/pages/vet-page.ts`.
- `"can filter vets by specialty using query param"` test block added to `e2e-tests/tests/features/vet-directory.spec.ts`.
- Output of `cd e2e-tests && npm test -- --grep "Vet Directory"` showing the new filter test **failing** (RED phase confirmed — filter div not yet in DOM).

## Completion notes

(Filled in by `implement-sdd-spec`.)
