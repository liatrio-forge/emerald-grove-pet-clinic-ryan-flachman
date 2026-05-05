# Proofs: Task 03 — Write failing Playwright E2E test (RED)

Covers: AC-5.a, AC-5.b

## Planned evidence

- `owner-management.spec.ts` diff showing the new `"blocks duplicate owner creation"` test.
- `cd e2e-tests && npm test -- --grep "blocks duplicate owner creation"` failure output confirming the test fails because the second submission currently redirects to a new owner detail page rather than showing an error (RED phase).

## Completion notes

(Filled in by `implement-sdd-spec`.)
