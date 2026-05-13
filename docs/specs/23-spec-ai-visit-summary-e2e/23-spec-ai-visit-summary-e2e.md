---
status: accepted
created: 2026-05-13
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: AI Visit Summary E2E (23)

## Goal

Specs 12–22 deliver the full AI Visit Notes Summarizer stack: schema, entity,
async service, stub client, REST polling endpoint, health-timeline fragment,
and JS polling. Spec 22 validates the async flow at the integration-test level.
This spec adds a browser-level end-to-end test that validates the complete user
journey: a vet submits a new visit form, is redirected to the owner detail page,
expands the health timeline, and sees the AI summary rendered with an urgency
badge and tag chips — confirming that all layers (Spring MVC, async service,
polling endpoint, Thymeleaf fragment, and client-side JS) work together in a
real browser.

## Scope

### In scope

- One Playwright test file at `e2e-tests/tests/features/ai-visit-summary.spec.ts`
  containing a single test `"AI Visit Summary | shows DONE urgency badge and
  tag chips after visit save"` that:
  1. Navigates to `/owners/6` (Jean Coleman, one pet: Samantha, id=7)
  2. Clicks the "Add Visit" link, fills in description
     `"Dog is limping on left front leg"` and a future date, and submits
  3. Asserts the redirect lands on the owner detail page
  4. Clicks the `▼ Health Timeline` toggle button to expand the timeline section
  5. Waits up to 10 s for the visit entry (matched by `data-visit-date`) to carry
     `data-ai-status="DONE"`
  6. Asserts a `.urgency-urgent` element is visible within that entry
  7. Asserts ≥ 1 `.health-tag` element is visible within that entry
  8. Asserts no `span.ai-spinner` remains within that entry

### Out of scope

- Failure path (stub → FAILED status) — fully covered by `VisitSummaryFailureIT`
  in spec 22; excluded here because toggling the stub to fail mode would require
  production code changes.
- Urgency-routing variants (e.g. "checkup" → ROUTINE badge) — covered by
  `VisitSummaryHappyPathIT` in spec 22; excluded to avoid duplicating integration
  coverage.
- New page-object classes — existing `VisitPage` is sufficient; inline locators
  are used for the health timeline toggle.
- Any change to production code — this spec is pure test authoring.

## Source excerpts

- `docs/specs/22-spec-visit-summary-integration-test/22-questions-1-visit-summary-integration-test.md`
  — Q10 confirms owner 6 / pet 7 fixture; Q4 confirms "limp" → URGENT stub routing.
- `docs/specs/22-spec-js-polling-health-timeline/` — defines the JS polling
  behavior (3 s interval, `data-ai-status` attribute update, `.urgency-*` / `.health-tag`
  DOM classes) that this test relies on.

## Acceptance criteria

- **AC-1: Test file**
  - AC-1.a: `e2e-tests/tests/features/ai-visit-summary.spec.ts` exists and
    contains a `test.describe` block named `"AI Visit Summary"` with one test
    named `"shows DONE urgency badge and tag chips after visit save"`.

- **AC-2: Form submission and redirect**
  - AC-2.a: The test submits a visit with description
    `"Dog is limping on left front leg"` for owner 6 / pet 7.
  - AC-2.b: After submit, the page URL is `/owners/6` and the `"Owner
    Information"` heading is visible.

- **AC-3: Health timeline renders DONE**
  - AC-3.a: After clicking the `▼ Health Timeline` toggle, the entry with
    `data-visit-date` matching the submitted visit date carries
    `data-ai-status="DONE"` within 10 s.
  - AC-3.b: Within that entry, an element with class `urgency-urgent` is
    visible (stub maps "limp" → URGENT via `ClaudeApiClientStub`).
  - AC-3.c: Within that entry, at least one element with class `health-tag` is
    visible.
  - AC-3.d: Within that entry, `span.ai-spinner` has count 0 (spinner is gone).

- **AC-4: Suite passes**
  - AC-4.a: `cd e2e-tests && npm test -- --grep "AI Visit Summary"` exits 0
    with 1 test passed, 0 failed.

## Conventions

- Reuse `VisitPage` from `e2e-tests/tests/pages/visit-page.ts` for date +
  description fill and form submit. Do not add a new page-object class.
- Use the same `formatLocalDate` helper pattern from `visit-scheduling.spec.ts`
  for the future date calculation (1 year from today).
- Future date is used (not today) to satisfy the "disallow past scheduling"
  validation constraint from spec 10.
- The `webServer` auto-start in `playwright.config.ts` starts the Spring Boot
  app with no API key set, which activates `ClaudeApiClientStub` automatically.
  No test-profile or env-var override is needed.
- Timeout for the `data-ai-status` wait is `10_000` ms (10 s), consistent with
  the epic's requirement (TASK-18 says "up to 10 s").
- `test.describe` name: `"AI Visit Summary"`. Test name:
  `"shows DONE urgency badge and tag chips after visit save"`.
- No screenshot capture in this test (failure screenshots are captured
  automatically by `playwright.config.ts` `screenshot: 'only-on-failure'`).

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
