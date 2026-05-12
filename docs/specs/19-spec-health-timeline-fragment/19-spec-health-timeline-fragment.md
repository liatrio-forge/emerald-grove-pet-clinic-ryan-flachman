---
status: accepted
created: 2026-05-12
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Health Timeline Fragment (19)

## Goal

The AI Visit Notes Summarizer epic surfaces Claude-generated analysis on the
owner detail page. This spec delivers the Thymeleaf fragment
(`health-timeline.html`) that renders a collapsible, per-pet visit timeline.
Each entry shows the AI-generated summary, urgency badge, tag chips, and
follow-up note when the AI job is complete; it shows a loading spinner while
the job is in progress; and it shows an error indicator when the job failed.
The fragment's `data-visit-id` / `data-ai-status` attributes are the hooks
that TASK-15 (page integration) and TASK-17 (JS polling) depend on.

## Scope

### In scope

- `src/main/resources/templates/fragments/health-timeline.html` — the
  Thymeleaf fragment file.
- The named fragment `healthTimeline` that reads a `pet` context variable.
- Reverse chronological ordering of visits within the fragment.
- Per-visit rendering for each of the four `AiStatus` lifecycle states:
  `PENDING`, `PROCESSING`, `DONE`, `FAILED`.
- `data-visit-id` and `data-ai-status` attributes on every visit entry.
- Urgency badge CSS class derivation from `visit.aiUrgency` (lowercase string
  maps to `.urgency-routine`, `.urgency-monitor`, `.urgency-urgent`).
- Tag chip rendering by splitting the comma-joined `visit.aiTags` string into
  individual elements with class `health-tag`.
- Conditional follow-up note (rendered only when `visit.aiFollowUp` is
  non-null and non-blank).
- `HealthTimelineFragmentTest` — JUnit 5 unit test class exercising every
  rendering branch described above.

### Out of scope

- Including the fragment in `ownerDetails.html` — that is TASK-15 (spec to be
  written).
- The Bootstrap collapse toggle (`▼ Health Timeline`) — also TASK-15.
- CSS definitions for any of the classes used here — those are TASK-16.
- The JS polling loop — TASK-17.
- `VisitSummary` DTO or `VisitUrgency` enum — TASK-04 (not yet specced).
- Any controller or repository change.
- Internationalisation of the spinner or error text — English-only for now.

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java` (spec-14,
  delivered) — provides `aiStatus` (`AiStatus` enum), `aiSummary` (`String`),
  `aiTags` (`String`, comma-joined), `aiUrgency` (`String`: `"ROUTINE"`,
  `"MONITOR"`, or `"URGENT"`), `aiFollowUp` (`String`, nullable), `date`
  (`LocalDate`), and `id` (inherited from `BaseEntity`).
- `src/main/java/org/springframework/samples/petclinic/owner/AiStatus.java`
  (spec-14, delivered) — `PENDING`, `PROCESSING`, `DONE`, `FAILED`.
- `src/main/java/org/springframework/samples/petclinic/owner/Pet.java` —
  `getVisits()` returns `Collection<Visit>` backed by a `LinkedHashSet`.
- `docs/epic-ai-visit-summary.md` TASK-14 — canonical description of the
  fragment's required structure and data attributes.

## Acceptance criteria

- **AC-1: Fragment file and named fragment**
  - AC-1.a: `src/main/resources/templates/fragments/health-timeline.html`
    exists.
  - AC-1.b: The file contains a Thymeleaf fragment named `healthTimeline`
    (i.e., `th:fragment="healthTimeline"`).

- **AC-2: Reverse chronological ordering**
  - AC-2.a: When a `Pet` has two visits — one dated `2026-01-01` and one dated
    `2026-03-15` — the rendered HTML contains `2026-03-15` before `2026-01-01`.

- **AC-3: Data attributes**
  - AC-3.a: Each rendered visit entry carries a `data-visit-id` attribute
    whose value equals the visit's integer `id`.
  - AC-3.b: Each rendered visit entry carries a `data-ai-status` attribute
    whose value equals the visit's `aiStatus` name (e.g., `"PENDING"`).

- **AC-4: PENDING and PROCESSING state — spinner**
  - AC-4.a: When `aiStatus == PENDING`, the rendered entry contains an element
    with class `ai-spinner`.
  - AC-4.b: When `aiStatus == PENDING`, the text `Generating summary…` is
    present in the rendered entry.
  - AC-4.c: When `aiStatus == PROCESSING`, the rendered entry contains an
    element with class `ai-spinner` and the text `Generating summary…`.
  - AC-4.d: When `aiStatus == PENDING` or `PROCESSING`, no element with class
    `urgency-routine`, `urgency-monitor`, or `urgency-urgent` is rendered
    within that entry.

- **AC-5: DONE state — full content**
  - AC-5.a: When `aiStatus == DONE`, the visit's `date` value is present in
    the rendered entry.
  - AC-5.b: When `aiStatus == DONE` and `aiUrgency == "ROUTINE"`, an element
    with class `urgency-routine` is rendered.
  - AC-5.c: When `aiStatus == DONE` and `aiUrgency == "MONITOR"`, an element
    with class `urgency-monitor` is rendered.
  - AC-5.d: When `aiStatus == DONE` and `aiUrgency == "URGENT"`, an element
    with class `urgency-urgent` is rendered.
  - AC-5.e: When `aiStatus == DONE` and `aiTags == "diabetes,weight"`, exactly
    two elements with class `health-tag` are rendered, containing `diabetes`
    and `weight` respectively.
  - AC-5.f: When `aiStatus == DONE`, the `aiSummary` text is rendered in the
    entry.
  - AC-5.g: When `aiStatus == DONE` and `aiFollowUp` is non-null, the
    follow-up text is rendered.
  - AC-5.h: When `aiStatus == DONE` and `aiFollowUp` is `null`, no follow-up
    section is present.

- **AC-6: FAILED state — error indicator**
  - AC-6.a: When `aiStatus == FAILED`, an element with class `ai-error` is
    rendered and contains the text `Unable to generate summary`.
  - AC-6.b: When `aiStatus == FAILED`, no element with class `ai-spinner` is
    rendered within that entry.

- **AC-7: Test suite green**
  - AC-7.a: `./mvnw test -Dtest=HealthTimelineFragmentTest` exits 0 with all
    test methods passing.
  - AC-7.b: `./mvnw test` exits 0 — no regressions in the existing test suite.

## Conventions

- The fragment is a standalone HTML file readable by browsers (for IDE
  preview). Use `xmlns:th="https://www.thymeleaf.org"` on the root element and
  wrap content in a `<div th:fragment="healthTimeline">`.
- `AiStatus` enum comparisons in Thymeleaf must use the fully qualified type:
  `T(org.springframework.samples.petclinic.owner.AiStatus).DONE`.
- Urgency CSS class: `${'urgency-' + visit.aiUrgency.toLowerCase()}` — only
  rendered when `aiStatus == DONE` and `aiUrgency` is non-null.
- Tag splitting: `${#strings.arraySplit(visit.aiTags, ',')}` yields a
  `String[]` iterable in Thymeleaf; trim each tag with `${tag.trim()}`.
- Reverse chronological order implementation is left to the implementer
  (e.g., `#lists.sort` with a `Comparator`, a helper expression, or a
  `getVisitsSorted()` accessor added to `Pet.java`). The acceptance criterion
  tests behaviour, not mechanism.
- Test class `HealthTimelineFragmentTest` lives in
  `src/test/java/org/springframework/samples/petclinic/owner/`.
- Tests use `ClassLoaderTemplateResolver` + `SpringTemplateEngine` directly
  — no Spring context needed, no `@SpringBootTest`.
- TDD is mandatory: `HealthTimelineFragmentTest` must be written and confirmed
  failing (template not found) before `health-timeline.html` is created.
- Depends on spec-14 (`visit-ai-fields`, delivered).
- Blocks TASK-15 (owner detail page integration) and TASK-17 (JS polling).

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
