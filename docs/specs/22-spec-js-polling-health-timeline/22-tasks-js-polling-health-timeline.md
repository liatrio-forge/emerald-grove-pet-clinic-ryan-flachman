# Tasks: JS Polling for Pending Summaries (22)

## Task 01 — Create failing Playwright polling tests and fixture HTML (RED)

Covers: AC-9.a

### 1a. Create the fixture HTML file

Create `e2e-tests/fixtures/health-timeline-fixture.html` — a minimal, fully
self-contained HTML document used only by the Playwright test. It must contain:

- Two PENDING visit entries:

  ```html
  <div data-visit-id="1" data-ai-status="PENDING" data-visit-date="2025-01-10">
    <span class="ai-spinner"></span>
    <span>Generating summary…</span>
  </div>
  <div data-visit-id="2" data-ai-status="PENDING" data-visit-date="2025-02-15">
    <span class="ai-spinner"></span>
    <span>Generating summary…</span>
  </div>
  ```

- One DONE entry (`data-ai-status="DONE"`, `data-visit-id="3"`) with
  `.visit-date`, `.urgency-routine`, `.ai-summary` already rendered.
- One FAILED entry (`data-ai-status="FAILED"`, `data-visit-id="4"`) with
  `div.ai-error` already rendered.
- **No `<script>` block.** The script is intentionally absent at this stage;
  all Playwright tests must fail because polling never starts.

### 1b. Create the Playwright test file

Create `e2e-tests/tests/health-timeline-polling.spec.ts`.

**Fixture loading pattern** used by every test in this file:

```typescript
import { test, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const FIXTURE_PATH = path.join(__dirname, '../fixtures/health-timeline-fixture.html');

// In each test:
// 1. page.clock.install() — must come first so fake timers cover the script
// 2. page.route('/fixture', serve FIXTURE_PATH content)
// 3. page.route('/visits/*/summary', stub JSON response)
// 4. page.goto('/fixture') — script runs with fake clock and intercepted fetch
// 5. page.clock.tick(N) — advance time
// 6. assert DOM state
```

Write the following test descriptions (all inside
`test.describe('health-timeline polling', ...)`). Each test must fail at
this stage because the fixture has no script.

| Test name (description) | What it asserts | Covers |
|---|---|---|
| `initialises intervals for all PENDING entries on load` | Two fetch calls issued within first tick (3 100 ms); DONE/FAILED entries get zero calls | AC-2.a, AC-2.b |
| `does not poll DONE or FAILED entries` | After one tick, fetch is called only for visitId 1 and 2, not 3 or 4 | AC-2.b |
| `replaces spinner with summary HTML on DONE response` | After stub returns DONE, `span.ai-spinner` is gone, `.ai-summary` is visible | AC-3.a |
| `sets data-ai-status to DONE after DONE response` | Entry div's `data-ai-status` equals `"DONE"` | AC-3.b |
| `cancels interval after DONE response` | After entry transitions to DONE, no further fetches for that visitId | AC-3.c |
| `DONE HTML includes urgency badge, tag chips, summary, and follow-up` | DOM contains `.urgency-monitor`, `.health-tag` (≥1), `.ai-summary`, `.ai-follow-up` | AC-3.d |
| `DONE HTML omits follow-up when followUp is null` | DOM has no `.ai-follow-up` element when followUp absent from JSON | AC-3.e |
| `shows error indicator on FAILED response` | Entry contains `div.ai-error` after FAILED stub | AC-4.a |
| `sets data-ai-status to FAILED after FAILED response` | Entry's `data-ai-status` equals `"FAILED"` | AC-4.b |
| `cancels interval after FAILED response` | No further fetches for that entry after FAILED | AC-4.c |
| `treats entry as FAILED after 40 polls without terminal status` | With clock-advanced 40 ticks of PENDING, entry shows `div.ai-error` and polling stops | AC-5.a |
| `pauses polling when tab becomes hidden` | After dispatching `visibilitychange` hidden, next tick has no fetch | AC-6.a |
| `resumes polling when tab becomes visible` | After hidden then visible, next tick issues fetch | AC-6.b |
| `removes visibilitychange listener once all entries are resolved` | After all entries resolve, subsequent visibility events cause no fetch | AC-7.a |

**Guidance for specific tests:**

- *AC-5.a timeout test*: stub `/visits/1/summary` to always return PENDING.
  After `page.clock.tick(3000 * 40)`, assert `page.locator('[data-visit-id="1"] div.ai-error')` is visible and that no further fetch occurs on tick 41.

- *AC-6.a visibility test*: dispatch the `visibilitychange` event and override
  `document.visibilityState` via `page.evaluate()`:

  ```typescript
  await page.evaluate(() => {
    Object.defineProperty(document, 'visibilityState', { value: 'hidden', configurable: true });
    document.dispatchEvent(new Event('visibilitychange'));
  });
  ```

  Then `page.clock.tick(3100)` and assert zero new fetches.

- *AC-6.b visibility resume test*: apply hidden as above, then restore visible:

  ```typescript
  await page.evaluate(() => {
    Object.defineProperty(document, 'visibilityState', { value: 'visible', configurable: true });
    document.dispatchEvent(new Event('visibilitychange'));
  });
  ```

  Then tick and assert fetch resumes.

### 1c. Capture RED evidence

Run:

```bash
cd e2e-tests && npm test -- --grep "health-timeline polling"
```

All tests must fail (assertion timeouts or test-not-found). Capture the output in
`22-proofs/22-task-01-proofs.md` as RED evidence for AC-9.a.

**May break compile, fixed by:** Task 02

**Proof:** `22-proofs/22-task-01-proofs.md`

---

## Task 02 — Add data-visit-date attribute and inline polling script (GREEN)

Covers: AC-1.a, AC-2.a, AC-2.b, AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-3.e,
AC-4.a, AC-4.b, AC-4.c, AC-5.a, AC-6.a, AC-6.b, AC-7.a, AC-8.a, AC-8.b

### 2a. Add data-visit-date to health-timeline.html

In `src/main/resources/templates/fragments/health-timeline.html`, add
`data-visit-date` to the existing `th:attr` on each visit entry `<div>`:

```html
<div th:each="visit : ${pet.visitsSortedDesc}"
     th:attr="data-visit-id=${visit.id},
              data-ai-status=${visit.aiStatus},
              data-visit-date=${#temporals.format(visit.date, 'yyyy-MM-dd')}">
```

### 2b. Write the polling script

Add the following `<script>` block at the bottom of the
`<div th:fragment="healthTimeline">` container in `health-timeline.html`,
after the `th:each` loop:

The script must implement the following behaviour (exact JS is left to the
implementer; the spec constrains the observable behaviour, not the
implementation details):

```text
IIFE wrapper (no globals leaked)

Constants:
  MAX_POLLS = 40
  INTERVAL_MS = 3000

On script execution:
  1. Select all [data-ai-status="PENDING"] divs into pendingEntries.
  2. If none found, return immediately.
  3. For each entry, call startEntry(entry):
       - Create a setInterval(pollEntry, 3000) and store { timerId, pollCount: 0 }
         in a Map keyed by the entry element.
  4. Register document.addEventListener('visibilitychange', onVisibilityChange).

pollEntry(entry):
  - Read visitId from entry.dataset.visitId.
  - Read visitDate from entry.dataset.visitDate.
  - fetch('/visits/' + visitId + '/summary')
      .then(r => r.json())
      .then(data => {
          increment state.pollCount
          if data.status === 'DONE':
              entry.innerHTML = buildDoneHtml(visitDate, data)
              entry.dataset.aiStatus = 'DONE'
              stopEntry(entry)
          else if data.status === 'FAILED' OR state.pollCount >= MAX_POLLS:
              entry.innerHTML = buildErrorHtml()
              entry.dataset.aiStatus = 'FAILED'
              stopEntry(entry)
          // else PENDING and under limit: wait for next tick
      })
      .catch(() => { /* leave spinner, retry */ })

stopEntry(entry):
  - clearInterval(state.timerId)
  - activeTimers.delete(entry)
  - if activeTimers.size === 0:
      document.removeEventListener('visibilitychange', onVisibilityChange)

onVisibilityChange():
  if hidden:
    for each [entry, state] in activeTimers:
      clearInterval(state.timerId); state.timerId = null
  if visible:
    for each [entry, state] in activeTimers:
      if state.timerId is null:
        state.timerId = setInterval(pollEntry, 3000)

buildDoneHtml(date, data) → HTML string containing:
  <span class="visit-date">{date}</span>
  if data.urgency: <span class="urgency-{urgency}">{urgency}</span>
  for each tag: <span class="health-tag">{tag}</span>
  <p class="ai-summary">{summary}</p>
  if data.followUp: <p class="ai-follow-up">{followUp}</p>

buildErrorHtml() → '<div class="ai-error">Unable to generate summary.</div>'
```

### 2c. Mirror the script into the test fixture

Add **exactly the same `<script>` block** (same content) to
`e2e-tests/fixtures/health-timeline-fixture.html` so the Playwright tests
exercise the real script. Also add `data-visit-date` to the two PENDING entry
divs in the fixture.

### 2d. Run the GREEN test pass

```bash
cd e2e-tests && npm test -- --grep "health-timeline polling"
```

All 14 polling tests must pass. Capture the output.

Run structural greps:

```bash
grep -n "data-visit-date" src/main/resources/templates/fragments/health-timeline.html
grep -n "setInterval" src/main/resources/templates/fragments/health-timeline.html
ls src/main/resources/static/resources/js/health-timeline-poller.js 2>&1
```

Capture all output in `22-proofs/22-task-02-proofs.md`.

**Proof:** `22-proofs/22-task-02-proofs.md`

---

## Task 03 — Validate and capture proof artifacts

Covers: AC-10.a, AC-10.b (all)

- Run `./mvnw test` and capture full output — verifies AC-10.a (BUILD SUCCESS,
  zero test failures).
- Run `cd e2e-tests && npm test -- --grep "health-timeline polling"` and capture
  output — verifies AC-10.b (all 14 Playwright tests pass).
- Run all structural greps from `22-validation-js-polling-health-timeline.md`
  and capture their outputs.
- Confirm every row in the coverage matrix has been updated to `PASS`.
- Update each proof file with real output (no placeholders).

**Proof:** `22-proofs/22-task-03-proofs.md`
