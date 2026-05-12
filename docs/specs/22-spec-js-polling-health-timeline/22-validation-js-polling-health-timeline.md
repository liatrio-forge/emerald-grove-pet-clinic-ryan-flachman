# Validation: JS Polling for Pending Summaries (22)

## Automated verification

From the repository root:

```bash
# AC-1.a: data-visit-date attribute present in the fragment
grep -n "data-visit-date" src/main/resources/templates/fragments/health-timeline.html

# AC-8.a: setInterval inline in the fragment (not in a separate file)
grep -n "setInterval" src/main/resources/templates/fragments/health-timeline.html

# AC-8.b: no external JS file created
ls src/main/resources/static/resources/js/health-timeline-poller.js 2>&1 | grep "No such file"

# AC-10.a: full Maven test suite — no regressions
./mvnw test

# AC-10.b: Playwright polling unit tests
cd e2e-tests && npm test -- --grep "health-timeline polling"
```

**Expected:**

- `grep data-visit-date` — prints at least one line containing `data-visit-date`.
- `grep setInterval` — prints at least one line from `health-timeline.html`.
- `ls health-timeline-poller.js | grep "No such file"` — exits 0 (file absent).
- `./mvnw test` — exits 0, BUILD SUCCESS, zero test failures.
- `npm test -- --grep "health-timeline polling"` — exits 0; all 14 Playwright
  polling test descriptions pass.

## Traceability

- Feature spec: `22-spec-js-polling-health-timeline.md`
- Task breakdown: `22-tasks-js-polling-health-timeline.md`
- Questions and decisions: `22-questions-1-js-polling-health-timeline.md`
- Per-task evidence:
  - `22-proofs/22-task-01-proofs.md`
  - `22-proofs/22-task-02-proofs.md`
  - `22-proofs/22-task-03-proofs.md`
- Upstream specs: spec-21 (`VisitSummaryController`, JSON contract),
  spec-14 (`Visit` entity, `AiStatus`), spec-13 (health-timeline CSS),
  TASK-14 (fragment), TASK-15 (ownerDetails integration)
- Parent epic: `docs/epic-ai-visit-summary.md` TASK-17

## Manual checks

None — all acceptance criteria are verifiable by automated command or
Playwright assertion.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `data-visit-date` attribute present in `health-timeline.html` | `22-proofs/22-task-02-proofs.md` | command output (grep) | PASS |
| AC-2.a | Playwright test: initialises intervals for all PENDING entries | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-2.b | Playwright test: no polling for DONE or FAILED entries | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-3.a | Playwright test: replaces spinner with summary HTML on DONE | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-3.b | Playwright test: sets data-ai-status to DONE | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-3.c | Playwright test: cancels interval after DONE | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-3.d | Playwright test: DONE HTML includes urgency badge, tag chips, summary, follow-up | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-3.e | Playwright test: DONE HTML omits follow-up when null | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-4.a | Playwright test: shows error indicator on FAILED | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-4.b | Playwright test: sets data-ai-status to FAILED | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-4.c | Playwright test: cancels interval after FAILED | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-5.a | Playwright test: FAILED after 40 polls without terminal status | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-6.a | Playwright test: pauses polling when tab hidden | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-6.b | Playwright test: resumes polling when tab visible | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-7.a | Playwright test: removes visibilitychange listener when all resolved | `22-proofs/22-task-02-proofs.md` | Playwright test pass | PASS |
| AC-8.a | `setInterval` present inline in `health-timeline.html` | `22-proofs/22-task-02-proofs.md` | command output (grep) | PASS |
| AC-8.b | No `health-timeline-poller.js` in static resources | `22-proofs/22-task-02-proofs.md` | command output (ls) | PASS |
| AC-9.a | RED proof captures Playwright test failure before script added | `22-proofs/22-task-01-proofs.md` | command output | PASS |
| AC-10.a | `./mvnw test` exits 0 after all changes | `22-proofs/22-task-03-proofs.md` | Maven test pass | PASS |
| AC-10.b | `npm test -- --grep "health-timeline polling"` exits 0 | `22-proofs/22-task-03-proofs.md` | Playwright test pass | PASS |

## Definition of done

- [x] AC-1.a: `data-visit-date` attribute present in `health-timeline.html`
- [x] AC-2.a: Playwright test: initialises intervals for all PENDING entries on load
- [x] AC-2.b: Playwright test: no polling for DONE or FAILED entries
- [x] AC-3.a: Playwright test: replaces spinner with summary HTML on DONE response
- [x] AC-3.b: Playwright test: sets `data-ai-status` to `"DONE"` after DONE response
- [x] AC-3.c: Playwright test: cancels interval after DONE response
- [x] AC-3.d: Playwright test: DONE HTML includes urgency badge, tag chips, summary, and follow-up
- [x] AC-3.e: Playwright test: DONE HTML omits follow-up element when `followUp` is null
- [x] AC-4.a: Playwright test: shows `div.ai-error` on FAILED response
- [x] AC-4.b: Playwright test: sets `data-ai-status` to `"FAILED"` after FAILED response
- [x] AC-4.c: Playwright test: cancels interval after FAILED response
- [x] AC-5.a: Playwright test: shows `div.ai-error` after 40 polls without terminal status
- [x] AC-6.a: Playwright test: pauses polling when tab becomes hidden
- [x] AC-6.b: Playwright test: resumes polling when tab becomes visible
- [x] AC-7.a: Playwright test: removes `visibilitychange` listener once all entries resolved
- [x] AC-8.a: `setInterval` grep on `health-timeline.html` returns ≥ 1 match
- [x] AC-8.b: `health-timeline-poller.js` does not exist in static resources
- [x] AC-9.a: RED proof artifact captures Playwright test failure before script added
- [x] AC-10.a: `./mvnw test` exits 0 with zero failures
- [x] AC-10.b: `npm test -- --grep "health-timeline polling"` exits 0
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
