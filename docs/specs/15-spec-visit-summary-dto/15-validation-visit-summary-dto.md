# Validation: VisitSummary DTO (15)

## Automated verification

From repository root:

```bash
# Compile production and test code
./mvnw compile test-compile

# Run only the new unit tests
./mvnw test -Dtest="VisitUrgencyTest,VisitSummaryTest"

# Run the full test suite (must exit 0)
./mvnw test

# Generate coverage report
./mvnw test jacoco:report
# Open target/site/jacoco/index.html and verify ≥90% line coverage
# on VisitUrgency.java and VisitSummary.java
```

**Expected:**

- `./mvnw compile test-compile` — exits 0 with `BUILD SUCCESS`.
- `./mvnw test -Dtest="VisitUrgencyTest,VisitSummaryTest"` — exits 0; both test classes show `Tests run: N, Failures: 0, Errors: 0`.
- `./mvnw test` — exits 0; no pre-existing tests regressed.
- JaCoCo report — `VisitUrgency` and `VisitSummary` each show ≥90% line coverage.

## Traceability

- Feature spec: `15-spec-visit-summary-dto.md`
- Task breakdown: `15-tasks-visit-summary-dto.md`
- Questions and decisions: `15-questions-1-visit-summary-dto.md`
- Per-task evidence: `15-proofs/15-task-NN-proofs.md`
- Upstream specs: spec 14 (`visit-ai-fields`, delivered) — `AiStatus` enum and `Visit` entity
- Parent epic: `docs/epic-ai-visit-summary.md` TASK-04

## Manual checks

_None — all criteria are verifiable by automated commands._

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `VisitUrgency.java` exists at the correct path | `15-proofs/15-task-02-proofs.md` | file creation | PENDING |
| AC-1.b | Enum declares exactly `ROUTINE`, `MONITOR`, `URGENT` in that order | `15-proofs/15-task-02-proofs.md` | file creation | PENDING |
| AC-1.c | `VisitUrgencyTest` passes all assertions on enum values | `15-proofs/15-task-02-proofs.md` | Maven test pass | PENDING |
| AC-2.a | `VisitSummary.java` exists at the correct path | `15-proofs/15-task-04-proofs.md` | file creation | PENDING |
| AC-2.b | Record has four components in order: `summary`, `tags`, `urgency`, `followUp` | `15-proofs/15-task-04-proofs.md` | file creation | PENDING |
| AC-2.c | `VisitSummary` is in package `org.springframework.samples.petclinic.owner` | `15-proofs/15-task-04-proofs.md` | file creation | PENDING |
| AC-3.a | `VisitSummaryTest` asserts all-non-null construction returns correct accessors | `15-proofs/15-task-04-proofs.md` | Maven test pass | PENDING |
| AC-3.b | `VisitSummaryTest` asserts `followUp = null` is accepted and returned | `15-proofs/15-task-04-proofs.md` | Maven test pass | PENDING |
| AC-3.c | `VisitSummaryTest` asserts empty tags list is accepted | `15-proofs/15-task-04-proofs.md` | Maven test pass | PENDING |
| AC-3.d | `VisitSummaryTest` asserts `summary = null` throws `NullPointerException` | `15-proofs/15-task-04-proofs.md` | Maven test pass | PENDING |
| AC-3.e | `VisitSummaryTest` asserts `tags = null` throws `NullPointerException` | `15-proofs/15-task-04-proofs.md` | Maven test pass | PENDING |
| AC-3.f | `VisitSummaryTest` asserts `urgency = null` throws `NullPointerException` | `15-proofs/15-task-04-proofs.md` | Maven test pass | PENDING |
| AC-4.a | `VisitSummaryTest` asserts mutating the original list does not affect `tags()` | `15-proofs/15-task-04-proofs.md` | Maven test pass | PENDING |
| AC-4.b | `VisitSummaryTest` asserts `tags()` returns an unmodifiable list | `15-proofs/15-task-04-proofs.md` | Maven test pass | PENDING |
| AC-5.a | `./mvnw test` exits 0 with no failures | `15-proofs/15-task-05-proofs.md` | command output | PENDING |

## Definition of done

- [ ] AC-1.a: `VisitUrgency.java` exists at the correct path.
- [ ] AC-1.b: Enum declares exactly `ROUTINE`, `MONITOR`, `URGENT` in that order.
- [ ] AC-1.c: `VisitUrgencyTest` passes all assertions on enum values.
- [ ] AC-2.a: `VisitSummary.java` exists at the correct path.
- [ ] AC-2.b: Record has four components in order: `summary`, `tags`, `urgency`, `followUp`.
- [ ] AC-2.c: `VisitSummary` is in package `org.springframework.samples.petclinic.owner`.
- [ ] AC-3.a: `VisitSummaryTest` asserts all-non-null construction returns correct accessors.
- [ ] AC-3.b: `VisitSummaryTest` asserts `followUp = null` is accepted and returned.
- [ ] AC-3.c: `VisitSummaryTest` asserts empty tags list is accepted.
- [ ] AC-3.d: `VisitSummaryTest` asserts `summary = null` throws `NullPointerException`.
- [ ] AC-3.e: `VisitSummaryTest` asserts `tags = null` throws `NullPointerException`.
- [ ] AC-3.f: `VisitSummaryTest` asserts `urgency = null` throws `NullPointerException`.
- [ ] AC-4.a: `VisitSummaryTest` asserts mutating the original list does not affect `tags()`.
- [ ] AC-4.b: `VisitSummaryTest` asserts `tags()` returns an unmodifiable list.
- [ ] AC-5.a: `./mvnw test` exits 0 with no failures.
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on `VisitUrgency.java` and `VisitSummary.java`.
