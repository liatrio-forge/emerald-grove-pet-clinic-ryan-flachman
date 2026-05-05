# Proofs: Task 03 — Validate and capture proof artifacts

Covers: all (AC-1.a through AC-5.a)

## Planned evidence

- Full output of `./mvnw test` showing `BUILD SUCCESS` and
  `I18nPropertiesSyncTest` passing (AC-5.a).
- Final output of `cd e2e-tests && npm test -- --grep "language selector"`
  showing `1 passed` (AC-4.b).
- Confirmation that `e2e-tests/tests/features/language-switching.spec.ts` exists
  (`test -f` output) (AC-4.a).
- Coverage matrix from `03-validation-language-selector.md` with all rows
  transitioned to `PASS`.

## Completion notes

(Filled in by `implement-sdd-spec`.)
