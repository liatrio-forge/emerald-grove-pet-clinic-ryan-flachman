# Proofs: Task 05 — Validate and capture proof artifacts

Covers: all

## Planned evidence

- Full output of `./mvnw test` showing `BUILD SUCCESS` and zero failures.
- `VisitUrgencyTest` and `VisitSummaryTest` both present in the test results
  with `Failures: 0, Errors: 0`.
- JaCoCo coverage excerpt showing ≥90% line coverage on `VisitUrgency.java`
  and `VisitSummary.java`.

## Completion notes

**Compile and test sources.** `./mvnw compile test-compile` (2026-05-11) ends with:

```text
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**Focused tests.** `./mvnw test -Dtest="VisitUrgencyTest,VisitSummaryTest"` (2026-05-11):

```text
[INFO] Running org.springframework.samples.petclinic.owner.VisitUrgencyTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.103 s -- in org.springframework.samples.petclinic.owner.VisitUrgencyTest
[INFO] Running org.springframework.samples.petclinic.owner.VisitSummaryTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.020 s -- in org.springframework.samples.petclinic.owner.VisitSummaryTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**Full suite.** `./mvnw test` (2026-05-11) — excerpt from Surefire:

```text
[INFO] Running org.springframework.samples.petclinic.owner.VisitUrgencyTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.011 s -- in org.springframework.samples.petclinic.owner.VisitUrgencyTest
[INFO] Running org.springframework.samples.petclinic.owner.VisitSummaryTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in org.springframework.samples.petclinic.owner.VisitSummaryTest
```

Summary line:

```text
[WARNING] Tests run: 128, Failures: 0, Errors: 0, Skipped: 5
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

Failures and errors are zero; five tests are skipped by configuration elsewhere
in the project (pre-existing).

**JaCoCo.** After `./mvnw test jacoco:report`, `target/site/jacoco/jacoco.csv`
includes (JaCoCo CSV columns: …, `LINE_MISSED`, `LINE_COVERED`, …):

| Class | LINE_MISSED | LINE_COVERED |
|------|-------------|--------------|
| `VisitUrgency` | 0 | 2 |
| `VisitSummary` | 0 | 6 |

100% of instrumented lines are covered for both classes (≥90% requirement met).
HTML drill-down: `target/site/jacoco/org.springframework.samples.petclinic.owner/VisitUrgency.html`
and `VisitSummary.html`.
