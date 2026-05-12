# Proofs: Task 02 — Add data-visit-date attribute and inline polling script (GREEN)

Covers: AC-1.a, AC-2.a–AC-2.b, AC-3.a–AC-3.e, AC-4.a–AC-4.c, AC-5.a, AC-6.a–AC-6.b, AC-7.a, AC-8.a, AC-8.b

## Structural greps

```text
$ grep -n "data-visit-date" src/main/resources/templates/fragments/health-timeline.html
12:                data-visit-date=${#temporals.format(visit.date, 'yyyy-MM-dd')}">

$ grep -n "setInterval" src/main/resources/templates/fragments/health-timeline.html
95:          state.timerId = setInterval(function () {
145:    state.timerId = setInterval(function () {

$ ls src/main/resources/static/resources/js/health-timeline-poller.js 2>&1
ls: src/main/resources/static/resources/js/health-timeline-poller.js: No such file or directory
```

## Playwright (14 tests)

```bash
cd e2e-tests && npm test -- --grep "health-timeline polling"
```

Exit code **0**; all **14** tests whose titles start with `health-timeline polling |` passed.

## Notes

- `e2e-tests/fixtures/health-timeline-fixture.html` mirrors the production poller logic (DOM-based `buildDoneHtml` / `buildErrorHtml`; fixture uses a literal `MSG_UNABLE` string because Thymeleaf is not involved).
- The production fragment uses `th:inline="javascript"` and `/*[[#{healthTimeline.unableToGenerateSummary}]]*/` for the error copy so `I18nPropertiesSyncTest` stays green.
