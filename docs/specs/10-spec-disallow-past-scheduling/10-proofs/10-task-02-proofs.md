# Proofs: Task 02 — Write failing Playwright past-date test + update existing test (RED)

Covers: AC-2.d, AC-4.b

## Planned evidence

- Output of `cd e2e-tests && npm test -- --grep "rejects past date"` showing the new test **fails** (form redirected instead of showing validation error — RED phase confirmation)
- Output of `cd e2e-tests && npm test -- --grep "can schedule a visit"` showing the updated success-path test still **passes** with the dynamic future date

## Completion notes

(Filled in by `implement-sdd-spec`.)
