---
status: delivered
created: 2026-05-12
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: JS Polling for Pending Summaries (22)

## Goal

When the owner detail page loads, any visit whose AI summary is still being
generated shows only a spinner. There is currently no browser-side mechanism
to discover when generation completes — the owner must manually reload.
This spec adds a vanilla-JS polling block, inline in the
`health-timeline.html` Thymeleaf fragment, that watches PENDING entries and
updates them live (no page reload) once each summary is ready. It consumes
the `GET /visits/{visitId}/summary` endpoint delivered in spec-21 and is the
final front-end prerequisite before the Playwright E2E smoke test (TASK-18).

## Scope

### In scope

- `src/main/resources/templates/fragments/health-timeline.html` — add a
  `data-visit-date` attribute to each visit entry `<div>`; add an inline
  `<script>` block at the bottom of the fragment containing all polling logic.
- `e2e-tests/tests/health-timeline-polling.spec.ts` — Playwright tests that
  load a static HTML fixture, stub `fetch` via `page.route()`, and control
  timers via `page.clock` to cover every polling behaviour path.
- `e2e-tests/fixtures/health-timeline-fixture.html` — minimal self-contained
  HTML fixture (no Spring server required) used by the Playwright test file.

### Out of scope

- Any change to `VisitSummaryResponse.java` or `VisitSummaryController.java` —
  already delivered (spec-21); the JSON contract is consumed as-is.
- Any change to `ownerDetails.html` — already includes the fragment (TASK-15).
- CSS changes — urgency badge, tag chip, and spinner styles already delivered
  (spec-13 health-timeline-css).
- A retry or manual re-trigger button for FAILED visits.
- Server-side rendering of DONE summaries on initial page load — handled by
  the existing Thymeleaf DONE block in the fragment; JS only updates entries
  that arrive as PENDING at load time.
- Extracting the polling JS to a separate static file under
  `src/main/resources/static/`.

## Source excerpts

- `src/main/resources/templates/fragments/health-timeline.html` — existing
  entry structure with `data-visit-id` and `data-ai-status`; spinner markup
  (`span.ai-spinner`); error markup (`div.ai-error`); CSS classes
  `urgency-{value}`, `health-tag`.
- `docs/specs/21-spec-visit-summary-controller/21-spec-visit-summary-controller.md`
  (spec-21) — canonical `GET /visits/{visitId}/summary` JSON contract. Response
  fields: `status` (`"PENDING"` | `"DONE"` | `"FAILED"`), `summary`, `tags[]`,
  `urgency` (lowercase), `followUp` (may be absent).

## Acceptance criteria

- **AC-1: data-visit-date attribute**
  - AC-1.a: Every visit entry `<div>` in `health-timeline.html` carries a
    `data-visit-date` attribute bound to the visit date formatted as
    `yyyy-MM-dd` — confirmed by:
    `grep -n "data-visit-date" src/main/resources/templates/fragments/health-timeline.html`
    returning at least one match.

- **AC-2: Polling initialisation**
  - AC-2.a: Playwright test
    `health-timeline polling | initialises intervals for all PENDING entries on load`
    passes: a fixture page with two PENDING entries issues a fetch call for
    each entry within the first clock tick (3 000 ms).
  - AC-2.b: Playwright test
    `health-timeline polling | does not poll DONE or FAILED entries`
    passes: entries with `data-ai-status="DONE"` or `"FAILED"` generate no
    fetch calls.

- **AC-3: DONE transition**
  - AC-3.a: Playwright test
    `health-timeline polling | replaces spinner with summary HTML on DONE response`
    passes: after fetch returns
    `{ status:"DONE", summary:"...", tags:[...], urgency:"monitor", followUp:"..." }`,
    the entry's spinner (`span.ai-spinner`) is gone and `.ai-summary` is visible.
  - AC-3.b: Playwright test
    `health-timeline polling | sets data-ai-status to DONE after DONE response`
    passes: the entry div's `data-ai-status` attribute equals `"DONE"`.
  - AC-3.c: Playwright test
    `health-timeline polling | cancels interval after DONE response`
    passes: no further fetch calls are issued for the resolved entry after it
    transitions to DONE.
  - AC-3.d: Playwright test
    `health-timeline polling | DONE HTML includes urgency badge, tag chips, summary, and follow-up`
    passes: the built DOM contains `.urgency-monitor`, `.health-tag` (at least
    one), `.ai-summary`, and `.ai-follow-up`.
  - AC-3.e: Playwright test
    `health-timeline polling | DONE HTML omits follow-up when followUp is null`
    passes: when `followUp` is absent from the JSON, no `.ai-follow-up` element
    is present in the entry.

- **AC-4: FAILED transition**
  - AC-4.a: Playwright test
    `health-timeline polling | shows error indicator on FAILED response`
    passes: after fetch returns `{ status:"FAILED" }`, the entry contains
    a `div.ai-error` element.
  - AC-4.b: Playwright test
    `health-timeline polling | sets data-ai-status to FAILED after FAILED response`
    passes.
  - AC-4.c: Playwright test
    `health-timeline polling | cancels interval after FAILED response`
    passes.

- **AC-5: 2-minute timeout hard stop**
  - AC-5.a: Playwright test
    `health-timeline polling | treats entry as FAILED after 40 polls without terminal status`
    passes: with `page.clock` advancing time through 40 intervals (each
    returning `{ status:"PENDING" }`), the entry shows `div.ai-error` and no
    further fetches are issued.

- **AC-6: Page Visibility API**
  - AC-6.a: Playwright test
    `health-timeline polling | pauses polling when tab becomes hidden`
    passes: after the `visibilitychange` event fires with
    `visibilityState === "hidden"`, no fetch is issued during the next clock
    tick.
  - AC-6.b: Playwright test
    `health-timeline polling | resumes polling when tab becomes visible`
    passes: after subsequently firing `visibilitychange` with
    `visibilityState === "visible"`, the next clock tick issues a fetch.

- **AC-7: Cleanup when all entries resolved**
  - AC-7.a: Playwright test
    `health-timeline polling | removes visibilitychange listener once all entries are resolved`
    passes: once all PENDING entries have reached DONE or FAILED, a subsequent
    `visibilitychange` event triggers no additional fetch.

- **AC-8: JS placement**
  - AC-8.a: `grep -n "setInterval" src/main/resources/templates/fragments/health-timeline.html`
    returns at least one match — confirms the script is inline in the fragment.
  - AC-8.b: `ls src/main/resources/static/resources/js/health-timeline-poller.js`
    exits non-zero (file does not exist) — confirms no external JS file was
    created.

- **AC-9: TDD compliance**
  - AC-9.a: RED proof artifact captures the Playwright test run failing (tests
    not found or assertion failure) before the `<script>` block is added to
    `health-timeline.html`.

- **AC-10: Full test suite passes**
  - AC-10.a: `./mvnw test` exits 0 with zero failures after all changes.
  - AC-10.b: `cd e2e-tests && npm test -- --grep "health-timeline polling"`
    exits 0 with all Playwright polling tests passing.

## Conventions

- The `<script>` block must live inside the `<div th:fragment="healthTimeline">`
  container (not in `<head>`) so it renders in context within `ownerDetails.html`.
- All DOM queries use `document.querySelectorAll('[data-ai-status="PENDING"]')`
  on `DOMContentLoaded`; no wrapping container ID assumed.
- Polling interval constant: 3 000 ms.
- Hard timeout: 40 polls per entry, tracked with a per-entry counter.
- Page Visibility: `document.addEventListener('visibilitychange', handler)`.
  On hidden: clear all active intervals (store timer IDs). On visible: restart
  intervals for entries that are still PENDING.
- Client-built DONE HTML must use the same CSS classes as the server-rendered
  block in the fragment: `urgency-{lowercase-value}`, `health-tag`,
  `ai-summary`, `ai-follow-up`.
- `data-visit-date` is bound in Thymeleaf as
  `th:attr="...,data-visit-date=${#temporals.format(visit.date, 'yyyy-MM-dd')}"`.
- Test file uses Playwright `page.setContent()` to load the fixture HTML,
  `page.clock.install()` + `page.clock.tick()` for timer control, and
  `page.route()` to stub `GET /visits/*/summary`.
- Test descriptions must be prefixed with `health-timeline polling |` so that
  `--grep "health-timeline polling"` selects them exclusively.
- Strict TDD is mandatory: the Playwright test file and fixture HTML must be
  committed and failing before the `<script>` block is added to
  `health-timeline.html`. RED proof artifact required.
- Depends on: spec-21 (`VisitSummaryController`, JSON contract), spec-13
  (health-timeline CSS), TASK-14 (fragment), TASK-15 (ownerDetails integration).
- Blocks: TASK-18 (Playwright E2E smoke test).

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
