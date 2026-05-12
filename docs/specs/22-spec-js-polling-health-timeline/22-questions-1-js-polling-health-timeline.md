# Questions: JS Polling for Pending Summaries (22) — Round 1

## Resolved

| # | Question | Decision |
|---|----------|----------|
| Q1 | How should the DOM be updated when a summary becomes DONE? | Build HTML client-side from the JSON response fields; no extra server endpoint or page reload needed. |
| Q2 | Where should the polling JS live? | Inline `<script>` block at the bottom of `health-timeline.html` inside the `th:fragment` container. No new static JS file. |
| Q3 | Should polling have a maximum timeout? | Yes — hard stop after 40 polls × 3 s = 2 minutes. On timeout, show the same `div.ai-error` indicator as a FAILED response. |
| Q4 | Should polling pause when the browser tab is hidden? | Yes — use the Page Visibility API (`document.visibilitychange`). Pause all active intervals when `visibilityState === "hidden"`; resume on `"visible"`. |
| Q5 | The `VisitSummaryResponse` JSON does not include `date`. How does client-built DONE HTML get the visit date? | Add a `data-visit-date` attribute to each visit entry `<div>` in `health-timeline.html` via Thymeleaf (`${#temporals.format(visit.date, 'yyyy-MM-dd')}`). JS reads it from the DOM. No change to `VisitSummaryResponse`. |
| Q6 | What framework for the JS unit tests? | Use Playwright (`page.setContent()` + `page.clock` + `page.route()`) in `e2e-tests/tests/health-timeline-polling.spec.ts`. No new test framework (Jest/Vitest) required — Playwright 1.57 already in `e2e-tests/package.json`. |
| Q7 | Should all intervals and the `visibilitychange` listener be cleaned up after all entries resolve? | Yes — remove the `visibilitychange` listener once no pending entries remain to avoid memory/event leaks. |

## Open

None.
