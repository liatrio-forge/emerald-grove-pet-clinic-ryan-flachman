# Proofs: Task 05 — Validate and capture proof artifacts

Covers: all

## Planned evidence

- Output of `./mvnw test` showing **BUILD SUCCESS** with all tests passing (AC-4.a)
- JaCoCo report excerpt for `VisitValidator` showing ≥90% line coverage (AC-5.b)
- Output of `cd e2e-tests && npm test -- --grep "Visit Scheduling"` showing all tests pass, including `rejects past date` and updated success-path test (AC-2.d, AC-4.b)
- Screenshot `past-date-validation-error.png` captured by the Playwright past-date test (AC-2.d)
- Coverage matrix from `10-validation-disallow-past-scheduling.md` with all rows updated to `PASS`

## Completion notes

### AC-4.a: ./mvnw test exits 0 — all tests pass

```text
$ ./mvnw test
[WARNING] Tests run: 89, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
[INFO] Total time:  13.688 s
[INFO] Finished at: 2026-05-06T14:55:23-05:00
```

89 tests run, 0 failures, 0 errors. 5 skipped are `@DisabledInNativeImage` / `@DisabledInAotMode` tests unrelated to this spec.

### AC-5.b: JaCoCo ≥90% line coverage on VisitValidator

```text
$ ./mvnw test jacoco:report
[INFO] BUILD SUCCESS
[INFO] Total time:  13.908 s
[INFO] Finished at: 2026-05-06T14:55:42-05:00
```

JaCoCo HTML report for `VisitValidator` (from `target/site/jacoco/org.springframework.samples.petclinic.owner/VisitValidator.html`):

```text
Total | Missed Instructions: 0 of 31 | Cov.: 100%
      | Missed Branches: 0 of 4     | Cov.: 100%
      | Missed Lines: 0 of 9        | Missed Methods: 0 of 3
```

100% line coverage, 100% branch coverage. Exceeds the ≥90% threshold.

### AC-2.d / AC-4.b: Playwright Visit Scheduling tests all pass

```text
$ cd e2e-tests && npm test -- --grep "Visit Scheduling"
Running 3 tests using 3 workers

[1/3] [chromium] › visit-scheduling.spec.ts › Visit Scheduling › can schedule a visit for an existing pet
[2/3] [chromium] › visit-scheduling.spec.ts › Visit Scheduling › rejects past date with validation message
[3/3] [chromium] › visit-scheduling.spec.ts › Visit Scheduling › validates visit description is required

  3 passed (9.0s)
```

All 3 tests pass:

- `can schedule a visit for an existing pet` — updated with dynamic future date (AC-4.b)
- `rejects past date with validation message` — new test confirming validation error message (AC-2.d)
- `validates visit description is required` — pre-existing test, still passes

### AC-2.a: grep -c returns 1

```text
$ grep -c "visit.date.pastNotAllowed" src/main/resources/messages/messages.properties
1
```

### AC-2.b: ≥8 grep matches across all locale files

```text
$ grep -rn "visit.date.pastNotAllowed" src/main/resources/messages/ | wc -l
8
```

8 matches: base + de, es, fa, ko, pt, ru, tr.

### AC-5.a: VisitValidatorTests.java found

```text
$ find src/test -name "VisitValidatorTests.java"
src/test/java/org/springframework/samples/petclinic/owner/VisitValidatorTests.java
```

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
| AC-3.c | PASS |
| AC-3.d | PASS |
| AC-4.a | PASS |
| AC-4.b | PASS |
| AC-5.a | PASS |
| AC-5.b | PASS |
