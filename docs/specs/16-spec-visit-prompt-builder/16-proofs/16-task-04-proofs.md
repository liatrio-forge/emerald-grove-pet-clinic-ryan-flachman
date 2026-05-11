# Proofs: Task 04 — Validate and capture proof artifacts

Covers: all ACs

## Planned evidence

- `./mvnw test` full output showing `BUILD SUCCESS`, zero failures, and both new
  test classes in the passing list (AC-7.a).
- Confirmation that all four per-task proof files contain real command output
  (not placeholder text).
- Coverage matrix in `16-validation-visit-prompt-builder.md` with all rows
  transitioned to `PASS`.
- Updated `docs/specs/README.md` showing spec-16 entry.

## Completion notes

**Full suite** `./mvnw test` (2026-05-11): `BUILD SUCCESS`, `Tests run: 141,
Failures: 0, Errors: 0, Skipped: 5`.

**Excerpt** — new test classes in the passing list (AC-7.a):

```text
[INFO] Running org.springframework.samples.petclinic.owner.VisitPromptBuilderTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.007 s -- in org.springframework.samples.petclinic.owner.VisitPromptBuilderTest
[INFO] Running org.springframework.samples.petclinic.owner.PromptRequestTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 s -- in org.springframework.samples.petclinic.owner.PromptRequestTest
...
[WARNING] Tests run: 141, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
```

**File checks:** `PromptRequest.java` and `VisitPromptBuilder.java` exist at the
paths in AC-1.a / AC-2.a.

**Docs:** `docs/specs/README.md` lists spec 16 as **delivered**;
`16-validation-visit-prompt-builder.md` coverage matrix set to **PASS** for all
rows; task 01–04 proof files populated with real command output above.
