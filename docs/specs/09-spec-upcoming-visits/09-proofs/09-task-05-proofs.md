# Proofs: Task 05 — Write Playwright E2E test

Covers: AC-8.a, AC-8.b, AC-8.c

## Planned evidence

- Output of `grep -n "upcoming\|Upcoming" e2e-tests/tests/features/upcoming-visits.spec.ts` returning at least one match.
- Output of `grep -n "screenshot" e2e-tests/tests/features/upcoming-visits.spec.ts` returning at least one match.
- Output of `cd e2e-tests && npm test -- --grep "Upcoming Visits"` exiting 0 with `"shows a visit scheduled within the next 7 days"` passing.
- Confirmation that `upcoming-visits-table.png` was written to the Playwright output path.

## Completion notes

(Filled in by `implement-sdd-spec`.)
