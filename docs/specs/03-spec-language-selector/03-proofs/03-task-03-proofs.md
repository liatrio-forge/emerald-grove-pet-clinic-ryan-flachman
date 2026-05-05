# Proofs: Task 03 — Validate and capture proof artifacts

Covers: all (AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b, AC-2.c, AC-3.a, AC-4.a, AC-4.b, AC-5.a)

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

### AC-5.a: `./mvnw test` exits 0 including `I18nPropertiesSyncTest`

```text
[INFO] Running org.springframework.samples.petclinic.system.I18nPropertiesSyncTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.016 s -- in org.springframework.samples.petclinic.system.I18nPropertiesSyncTest
[INFO] Tests run: 59, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

All 59 tests pass. `I18nPropertiesSyncTest` (2 tests: `checkNonInternationalizedStrings`
and `checkI18nPropertyFilesAreInSync`) both pass with 0 failures.

### AC-4.a: `e2e-tests/tests/features/language-switching.spec.ts` exists

```text
$ test -f e2e-tests/tests/features/language-switching.spec.ts && echo "FILE EXISTS"
FILE EXISTS
```

### AC-4.b: `npm test -- --grep "language selector"` exits 0

```text
Running 1 test using 1 worker

  1 passed (8.2s)
```

Exit code 0. All 13 assertions inside the test pass.

### Coverage matrix

| AC ID | Status |
|-------|--------|
| AC-1.a | PASS |
| AC-1.b | PASS |
| AC-1.c | PASS |
| AC-2.a | PASS |
| AC-2.b | PASS |
| AC-2.c | PASS |
| AC-3.a | PASS |
| AC-4.a | PASS |
| AC-4.b | PASS |
| AC-5.a | PASS |
