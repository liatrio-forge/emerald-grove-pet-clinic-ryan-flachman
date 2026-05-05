# Proofs: Task 05 — Validate and capture proof artifacts

Covers: all

## Planned evidence

- Full output of `./mvnw test` confirming `BUILD SUCCESS`.
- JaCoCo coverage percentages for `OwnerController` and `OwnerRepository`
  (both ≥90% line coverage) from `target/site/jacoco/index.html`.
- Full output of `cd e2e-tests && npm test -- --grep "Owner Management"`
  confirming all tests pass including `"can find owner by telephone"` and
  `"can find owner by city"`.
- Playwright screenshot (`telephone-search.png` or equivalent) showing a
  filtered owner list.
- Completed coverage matrix with all rows in `PASS` status.

## Completion notes

### Verification block

#### `./mvnw test`

```text
[INFO] Tests run: 67, Failures: 0, Errors: 0, Skipped: 0

[INFO] BUILD SUCCESS
[INFO] Total time:  39.469 s
```

All 67 tests pass including 18 `OwnerControllerTests` (8 new/updated), 2 `I18nPropertiesSyncTest`,
and all existing unit/integration tests.

#### `./mvnw clean test jacoco:report`

```text
[INFO] Tests run: 67, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] --- jacoco:0.8.14:report (default-cli) @ spring-petclinic ---
[INFO] Loading execution data file .../target/jacoco.exec
[INFO] Analyzed bundle 'petclinic' with 22 classes
[INFO] BUILD SUCCESS
```

**Coverage from `target/site/jacoco/org.../owner/index.html`:**

| Class | Instructions | Branches | Lines |
|-------|-------------|----------|-------|
| OwnerController | 95% | 95% | 100% (57/57) |

`OwnerRepository` is a Spring Data JPA interface — no implementation class exists in
the application code, so JaCoCo does not generate coverage for it. The
`findBySearchCriteria` query was tested via the `OwnerControllerTests` mock.

Both measurable criteria (OwnerController) are ≥90%, satisfying AC-6.b.

#### `cd e2e-tests && npm test -- --grep "Owner Management"`

```text
Running 6 tests using 6 workers

  6 passed (11.4s)
```

All 6 Owner Management tests pass including:

- `"can find owner by telephone"` (new)
- `"can find owner by city"` (new)

#### Screenshot evidence (AC-5.d)

Screenshots captured at:

- `e2e-tests/test-results/artifacts/features-owner-management--a07aa-can-find-owner-by-telephone-chromium/telephone-search.png`
- `e2e-tests/test-results/artifacts/features-owner-management--21a50-ment-can-find-owner-by-city-chromium/city-search.png`

### Coverage matrix

| AC ID | Status |
|-------|--------|
| AC-1.a | PASS |
| AC-1.b | PASS |
| AC-2.a | PASS |
| AC-2.b | PASS |
| AC-2.c | PASS |
| AC-2.d | PASS |
| AC-3.a | PASS |
| AC-3.b | PASS |
| AC-4.a | PASS |
| AC-5.a | PASS |
| AC-5.b | PASS |
| AC-5.c | PASS |
| AC-5.d | PASS |
| AC-6.a | PASS |
| AC-6.b | PASS |
