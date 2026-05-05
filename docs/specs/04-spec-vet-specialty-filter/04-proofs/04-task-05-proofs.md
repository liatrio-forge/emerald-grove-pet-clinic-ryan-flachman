# Proofs: Task 05 — Validate and capture proof artifacts

Covers: all (AC-1.a through AC-6.c)

## Planned evidence

- Output of `./mvnw test` — `BUILD SUCCESS`, all tests pass.
- Output of `cd e2e-tests && npm test -- --grep "Vet Directory"` — both tests pass.
- JaCoCo coverage report excerpt showing ≥90% line coverage on `VetController` and `VetRepository`.
- Playwright screenshot file from `testInfo.outputPath('vet-filter.png')` showing filtered vet list.
- Completed coverage matrix in `04-validation-vet-specialty-filter.md` with all rows in `PASS`.

## Completion notes

### Verification block

#### `./mvnw clean test jacoco:report`

```text
[INFO] Running org.springframework.samples.petclinic.vet.VetControllerTests
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.I18nPropertiesSyncTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0

[INFO] Tests run: 62, Failures: 0, Errors: 0, Skipped: 0

[INFO] BUILD SUCCESS
[INFO] Total time:  40.808 s
```

#### `cd e2e-tests && npm test -- --grep "Vet Directory"`

```text
Running 2 tests using 2 workers
  2 passed (8.2s)
```

Both `"can browse veterinarian list and view specialties"` and
`"can filter vets by specialty using query param"` pass.

#### JaCoCo coverage — `org.springframework.samples.petclinic.vet` package

```text
VetController:  100% instruction coverage, 90% branch coverage (1 of 10 branches missed)
Vet:            100% instruction coverage, 100% branch coverage
Vets:           100% instruction coverage, 100% branch coverage
Specialty:      100% instruction coverage, n/a branch coverage
Package total:  100% instruction coverage, 92% branch coverage
```

`VetController` meets the ≥90% line and branch threshold. `VetRepository` is a Spring Data JPA interface — JaCoCo does not generate coverage metrics for pure interfaces; coverage is exercised through the integration tests.

#### Playwright screenshot

Screenshot `vet-filter.png` captured via `testInfo.outputPath('vet-filter.png')` in the E2E test, saved to the Playwright artifacts directory at:
`test-results/artifacts/.../vet-filter.png`

### Coverage matrix (final state)

| AC ID | Status |
|-------|--------|
| AC-1.a | PASS |
| AC-1.b | PASS |
| AC-1.c | PASS |
| AC-1.d | PASS |
| AC-2.a | PASS |
| AC-2.b | PASS |
| AC-2.c | PASS |
| AC-3.a | PASS |
| AC-3.b | PASS |
| AC-3.c | PASS |
| AC-4.a | PASS |
| AC-4.b | PASS |
| AC-5.a | PASS |
| AC-5.b | PASS |
| AC-5.c | PASS |
| AC-6.a | PASS |
| AC-6.b | PASS |
| AC-6.c | PASS |
