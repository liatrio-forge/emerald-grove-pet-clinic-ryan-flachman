# Proofs: Task 02 — Write failing Playwright E2E tests (RED)

Covers: AC-5.a, AC-5.b, AC-1.a

## Planned evidence

- `owner-page.ts` updated with `searchByFilters` helper — show the new method.
- `owner-management.spec.ts` with new test bodies for `"can find owner by telephone"`
  and `"can find owner by city"` — show both tests.
- Output of `cd e2e-tests && npm test -- --grep "Owner Management"` showing the
  new tests **failing** because `#telephone` and `#city` inputs do not yet exist
  in the form (RED phase confirmed).

## Completion notes

(Filled in by `implement-sdd-spec`.)
