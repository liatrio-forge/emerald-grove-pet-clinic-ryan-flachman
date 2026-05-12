# Tasks: VisitSummary DTO (15)

## Task 01 — Write failing tests for VisitUrgency enum (RED)

Covers: AC-1.c

- Create `src/test/java/org/springframework/samples/petclinic/owner/VisitUrgencyTest.java`.
- Test `shouldHaveExactlyThreeValues`: assert `VisitUrgency.values().length == 3`.
- Test `shouldResolveAllThreeValues`: assert `valueOf("ROUTINE")`, `valueOf("MONITOR")`, `valueOf("URGENT")` each return the correct constant.
- Confirm the tests fail to compile (class does not exist yet) — this is the RED state.

**Proof:** 15-proofs/15-task-01-proofs.md

## Task 02 — Implement VisitUrgency enum (GREEN)

Covers: AC-1.a, AC-1.b

- Create `src/main/java/org/springframework/samples/petclinic/owner/VisitUrgency.java`.
- Declare `public enum VisitUrgency` with exactly three values in order: `ROUTINE`, `MONITOR`, `URGENT`.
- Run `./mvnw test -Dtest=VisitUrgencyTest` and confirm all tests pass.

**Proof:** 15-proofs/15-task-02-proofs.md

## Task 03 — Write failing tests for VisitSummary record (RED)

Covers: AC-2.b, AC-2.c, AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-3.e, AC-3.f, AC-4.a, AC-4.b

- Create `src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryTest.java`.
- Test `shouldConstructWithAllFields`: build with non-null summary, tags, urgency, followUp; assert all four accessors return the supplied values.
- Test `shouldAllowNullFollowUp`: construct with `followUp = null`; assert `followUp()` returns null.
- Test `shouldAllowEmptyTags`: construct with `List.of()` as tags; assert `tags()` returns an empty list.
- Test `shouldThrowWhenSummaryIsNull`: assert `new VisitSummary(null, List.of(), VisitUrgency.ROUTINE, null)` throws `NullPointerException`.
- Test `shouldThrowWhenTagsIsNull`: assert `new VisitSummary("s", null, VisitUrgency.ROUTINE, null)` throws `NullPointerException`.
- Test `shouldThrowWhenUrgencyIsNull`: assert `new VisitSummary("s", List.of(), null, null)` throws `NullPointerException`.
- Test `shouldDefensivelyCopyTags`: pass a mutable list; mutate it after construction; assert `tags()` is unchanged.
- Test `shouldReturnUnmodifiableTags`: assert that calling `.add()` on `visitSummary.tags()` throws `UnsupportedOperationException`.
- Confirm the tests fail to compile (`VisitSummary` does not exist yet) — this is the RED state.

**Proof:** 15-proofs/15-task-03-proofs.md

## Task 04 — Implement VisitSummary record (GREEN)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-3.e, AC-3.f, AC-4.a, AC-4.b

- Create `src/main/java/org/springframework/samples/petclinic/owner/VisitSummary.java`.
- Declare `public record VisitSummary(String summary, List<String> tags, VisitUrgency urgency, String followUp)`.
- Add a compact constructor that:
  - calls `Objects.requireNonNull(summary, "summary must not be null")`.
  - calls `Objects.requireNonNull(tags, "tags must not be null")`.
  - calls `Objects.requireNonNull(urgency, "urgency must not be null")`.
  - reassigns `tags = List.copyOf(tags)`.
- Run `./mvnw test -Dtest=VisitSummaryTest` and confirm all tests pass.

**Proof:** 15-proofs/15-task-04-proofs.md

## Task 05 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture full output.
- Confirm `VisitUrgencyTest` and `VisitSummaryTest` both appear in the results as PASSED.
- Confirm no pre-existing tests have regressed.
- Run `./mvnw test jacoco:report` and verify ≥90% line coverage on new production files.
- Fill in all proof files with real command output.
- Update the coverage matrix in `15-validation-visit-summary-dto.md` to `PASS`.

**Proof:** 15-proofs/15-task-05-proofs.md
