# Proofs: Task 06 — Validate and capture proof artifacts

Covers: all

## Planned evidence

- Output of `./mvnw test` exiting 0 (`BUILD SUCCESS`).
- JaCoCo coverage screenshot or text showing ≥90% line coverage on `UpcomingVisitsController` and `VisitRepository`.
- Output of all structural `grep` checks from the validation file (all returning at least one match).
- Output of `cd e2e-tests && npm test -- --grep "Upcoming Visits"` exiting 0.
- Confirmation that `upcoming-visits-table.png` exists at the Playwright output path.
- Completed coverage matrix in `09-validation-upcoming-visits.md` with all rows `PASS`.
- All DoD checkboxes ticked in `09-validation-upcoming-visits.md`.

## Completion notes

(Filled in by `implement-sdd-spec`.)
