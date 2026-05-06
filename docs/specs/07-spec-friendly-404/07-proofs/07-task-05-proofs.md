# Proofs: Task 05 — Write Playwright E2E test for missing owner 404

Covers: AC-8.a, AC-8.b, AC-8.c

## Planned evidence

- `grep -n "99999\|not.found\|notFound\|404" e2e-tests/tests/features/owner-management.spec.ts`
  output (≥1 match).
- `cd e2e-tests && npm test -- --grep "Owner Management"` output confirming all
  tests pass including `"shows friendly 404 page for non-existent owner"`.
- `owner-not-found.png` screenshot captured to the Playwright artifacts output
  path.

## Completion notes

(Filled in by `implement-sdd-spec`.)
