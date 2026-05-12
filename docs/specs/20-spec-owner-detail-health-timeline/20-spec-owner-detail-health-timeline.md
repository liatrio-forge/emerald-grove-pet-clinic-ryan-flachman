---
status: accepted
created: 2026-05-12
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Owner Detail — Health Timeline Integration (20)

## Goal

The AI Visit Notes Summarizer epic (see `docs/epic-ai-visit-summary.md` TASK-15)
requires that the `health-timeline` Thymeleaf fragment produced by spec-19 be
surfaced to users on the existing owner detail page. This spec integrates the
fragment into `ownerDetails.html`: it adds a Bootstrap collapse toggle labelled
`▼ Health Timeline` beneath each pet's existing visits table, with the collapsible
panel containing the `healthTimeline` fragment. Without this integration, the
fragment exists but is never displayed, and the JS polling introduced by TASK-17
has no DOM to target.

## Scope

### In scope

- `src/main/resources/templates/owners/ownerDetails.html` — add a Bootstrap
  collapse toggle and `th:insert` for the `healthTimeline` fragment inside the
  `th:each="pet : ${owner.pets}"` loop, after the existing inner visits table.
- Per-pet collapse id scoped by `pet.id` (e.g., `health-timeline-3`) to prevent
  id collisions when multiple pets appear on the same page.
- One new `@Test` method in `OwnerControllerTests` asserting the toggle and
  fragment are rendered for `GET /owners/{ownerId}`.

### Out of scope

- The `health-timeline.html` fragment itself — that is spec-19.
- CSS for urgency badges, tag chips, and spinner — that is spec-13 (health-timeline-css).
- The JS polling loop — TASK-17 (spec to be written).
- Any controller, service, or repository changes.
- Internationalisation of the `▼ Health Timeline` label — English-only.
- A "Retry" button or any UI for re-triggering the AI job.

## Source excerpts

- `docs/specs/19-spec-health-timeline-fragment/19-spec-health-timeline-fragment.md`
  — defines the `healthTimeline` fragment (named, in
  `fragments/health-timeline.html`), its `pet` context variable, and the
  `data-visit-id` / `data-ai-status` attributes it emits. This spec is the
  canonical source of what the fragment expects.
- `src/main/resources/templates/owners/ownerDetails.html` — existing template;
  the `th:each="pet : ${owner.pets}"` loop on line 48 is the insertion point.
- `docs/epic-ai-visit-summary.md` TASK-15 — canonical requirement description.

## Acceptance criteria

- **AC-1: Fragment include**
  - AC-1.a: `ownerDetails.html` contains `th:insert="~{fragments/health-timeline
    :: healthTimeline}"` inside the `th:each="pet : ${owner.pets}"` loop.
  - AC-1.b: The insert is positioned after the inner
    `<table class="table-condensed liatrio-table">` (visits + action links
    table), within the same right-hand `<td>` of the outer pet row.

- **AC-2: Bootstrap collapse toggle**
  - AC-2.a: Each pet section renders an element with `data-bs-toggle="collapse"`
    whose visible text contains `Health Timeline`.
  - AC-2.b: The collapsible `<div>` uses `th:id="'health-timeline-' + ${pet.id}"`
    so every pet on the page gets a unique collapse id.
  - AC-2.c: The toggle element's `data-bs-target` attribute equals
    `#health-timeline-{petId}` — verified structurally in the template source
    (e.g., `th:data-bs-target="'#health-timeline-' + ${pet.id}"`).

- **AC-3: `@WebMvcTest` — new test method**
  - AC-3.a: A new test method `testOwnerDetailsContainsHealthTimelineToggle()` in
    `OwnerControllerTests` calls `GET /owners/{ownerId}` and asserts the response
    body contains the string `data-bs-toggle="collapse"`.
  - AC-3.b: The same test asserts the response body contains the text
    `Health Timeline`.
  - AC-3.c: `./mvnw test -Dtest=OwnerControllerTests` exits 0 with all test
    methods passing (existing + new).

- **AC-4: Full test suite green**
  - AC-4.a: `./mvnw test` exits 0 — no regressions in any existing test class.

## Conventions

- Use `th:insert` (not `th:replace`) to preserve the outer container element.
- The collapse toggle element must be a `<button>` or `<a>` — use whichever
  matches the Bootstrap 5 collapse pattern already used in the project
  (`data-bs-toggle="collapse"`, `data-bs-target`, `aria-expanded`,
  `aria-controls`).
- `th:data-bs-target` must be used (not a plain `data-bs-target`) to let
  Thymeleaf evaluate the expression.
- The `healthTimeline` fragment receives the `pet` context variable implicitly
  via the outer `th:each` loop variable — no additional model attribute is needed.
- TDD is mandatory: the new test in `OwnerControllerTests` must be written and
  confirmed failing before any change is made to `ownerDetails.html`.
- Depends on spec-19 (`health-timeline-fragment`) in `delivered` status —
  `health-timeline.html` must exist before `ownerDetails.html` can include it
  without a Thymeleaf resolution error.
- Blocks TASK-17 (JS polling, which targets `[data-ai-status="PENDING"]` elements
  now surfaced on this page) and TASK-18 (Playwright E2E).

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
