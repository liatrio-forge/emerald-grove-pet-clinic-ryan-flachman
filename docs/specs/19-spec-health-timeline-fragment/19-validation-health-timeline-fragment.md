# Validation: Health Timeline Fragment (19)

## Automated verification

From repository root:

```bash
# AC-7.a: run fragment tests only
./mvnw test -Dtest=HealthTimelineFragmentTest

# AC-1.a: confirm template file exists
ls src/main/resources/templates/fragments/health-timeline.html

# AC-1.b: confirm named fragment is declared
grep -c 'th:fragment="healthTimeline"' src/main/resources/templates/fragments/health-timeline.html

# AC-7.b: full suite — no regressions
./mvnw test
```

**Expected:**

- `HealthTimelineFragmentTest`: `BUILD SUCCESS`, 0 failures, 0 errors.
- `ls` exits 0 and prints the file path.
- `grep -c` prints `1`.
- Full suite: `BUILD SUCCESS`, all pre-existing tests still pass.

## Traceability

- Feature spec: `19-spec-health-timeline-fragment.md`
- Task breakdown: `19-tasks-health-timeline-fragment.md`
- Questions and decisions: `19-questions-1-health-timeline-fragment.md`
- Per-task evidence: `19-proofs/19-task-NN-proofs.md`
- Upstream specs: spec-14 (`visit-ai-fields`, delivered)
- Parent epic: `docs/epic-ai-visit-summary.md` TASK-14

## Manual checks

None — all acceptance criteria are verifiable by the automated commands above.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `health-timeline.html` exists at correct path | `19-proofs/19-task-02-proofs.md` | file creation | PASS |
| AC-1.b | File declares `th:fragment="healthTimeline"` | `19-proofs/19-task-02-proofs.md` | file creation | PASS |
| AC-2.a | 2026-03-15 appears before 2026-01-01 in rendered output | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-3.a | Each entry has `data-visit-id` matching visit ID | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-3.b | Each entry has `data-ai-status` matching aiStatus name | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-4.a | PENDING renders `ai-spinner` element | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-4.b | PENDING renders "Generating summary…" text | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-4.c | PROCESSING renders spinner and "Generating summary…" | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-4.d | PENDING/PROCESSING renders no urgency badge class | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-5.a | DONE renders visit date | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-5.b | DONE + ROUTINE renders `urgency-routine` class | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-5.c | DONE + MONITOR renders `urgency-monitor` class | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-5.d | DONE + URGENT renders `urgency-urgent` class | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-5.e | DONE + two-tag aiTags renders two `health-tag` chips | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-5.f | DONE renders aiSummary text | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-5.g | DONE + non-null aiFollowUp renders follow-up text | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-5.h | DONE + null aiFollowUp renders no follow-up section | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-6.a | FAILED renders `ai-error` with "Unable to generate summary" | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-6.b | FAILED renders no `ai-spinner` | `19-proofs/19-task-01-proofs.md` | Maven test pass | PASS |
| AC-7.a | `HealthTimelineFragmentTest` exits 0 | `19-proofs/19-task-03-proofs.md` | command output | PASS |
| AC-7.b | Full suite exits 0 — no regressions | `19-proofs/19-task-03-proofs.md` | command output | PASS |

## Definition of done

- [x] AC-1.a: `health-timeline.html` exists at the correct path.
- [x] AC-1.b: File declares `th:fragment="healthTimeline"`.
- [x] AC-2.a: 2026-03-15 appears before 2026-01-01 in rendered output for two-visit pet.
- [x] AC-3.a: Each entry has `data-visit-id` matching visit ID.
- [x] AC-3.b: Each entry has `data-ai-status` matching aiStatus name.
- [x] AC-4.a: PENDING renders `ai-spinner` element.
- [x] AC-4.b: PENDING renders "Generating summary…" text.
- [x] AC-4.c: PROCESSING renders spinner and "Generating summary…".
- [x] AC-4.d: PENDING/PROCESSING renders no urgency badge class.
- [x] AC-5.a: DONE renders visit date.
- [x] AC-5.b: DONE + ROUTINE renders `urgency-routine` class.
- [x] AC-5.c: DONE + MONITOR renders `urgency-monitor` class.
- [x] AC-5.d: DONE + URGENT renders `urgency-urgent` class.
- [x] AC-5.e: DONE + two-tag aiTags renders two `health-tag` chips.
- [x] AC-5.f: DONE renders aiSummary text.
- [x] AC-5.g: DONE + non-null aiFollowUp renders follow-up text.
- [x] AC-5.h: DONE + null aiFollowUp renders no follow-up section.
- [x] AC-6.a: FAILED renders `ai-error` with "Unable to generate summary".
- [x] AC-6.b: FAILED renders no `ai-spinner`.
- [x] AC-7.a: `./mvnw test -Dtest=HealthTimelineFragmentTest` exits 0.
- [x] AC-7.b: `./mvnw test` exits 0 with no regressions.
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
