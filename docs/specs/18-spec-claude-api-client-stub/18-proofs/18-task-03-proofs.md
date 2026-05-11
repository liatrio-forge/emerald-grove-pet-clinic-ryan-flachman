# Proofs: Task 03 — Validate and capture proof artifacts

Covers: all ACs (including AC-11.a)

## Full test run (AC-11.a)

```bash
./mvnw test
```

Excerpt:

```text
[INFO] Running org.springframework.samples.petclinic.owner.ClaudeApiClientStubTests
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: ... -- in org.springframework.samples.petclinic.owner.ClaudeApiClientStubTests
...
[INFO] Tests run: 143, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## JaCoCo — `ClaudeApiClientStub` (from `target/site/jacoco/jacoco.xml`)

After `./mvnw test jacoco:report`:

| Metric | Missed | Covered |
|--------|--------|---------|
| Instructions | 0 | 43 |
| Branches | 0 | 8 |
| Lines | 0 | 17 |
| Complexity | 0 | 7 |

Line coverage for the class is 100% (≥90% required). Branch coverage on keyword routing is 100% (0 missed of 8 branch counters in `complete`).

## Coverage matrix and Definition of Done

Updated in `18-validation-claude-api-client-stub.md` (all rows `PASS`, all checkboxes ticked).

## Completion notes

Spec front-matter set to `status: delivered` and `last_amended: 2026-05-11` in `18-spec-claude-api-client-stub.md`; specs index row updated to delivered.
