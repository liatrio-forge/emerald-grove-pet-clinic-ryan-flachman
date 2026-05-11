---
status: in_progress
created: 2026-05-11
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Health Timeline CSS (13)

## Goal

The AI Visit Notes Summarizer epic (see `docs/epic-ai-visit-summary.md` TASK-16)
needs five CSS classes before the health timeline fragment (TASK-14/15) and the
E2E color assertions (TASK-18) can be built. This spec defines and delivers
those classes: three urgency badge variants, a tag chip, and a loading spinner
animation — all authored in the project's SCSS source so they survive future
CSS recompiles.

## Scope

### In scope

- Add `.urgency-routine` — green filled pill badge to `src/main/scss/petclinic.scss`.
- Add `.urgency-monitor` — amber filled pill badge to `src/main/scss/petclinic.scss`.
- Add `.urgency-urgent` — red filled pill badge to `src/main/scss/petclinic.scss`.
- Add `.health-tag` — outlined pill chip to `src/main/scss/petclinic.scss`.
- Add `.ai-spinner` + `@keyframes ai-spinner-rotate` rotation animation to
  `src/main/scss/petclinic.scss`.
- Recompile `petclinic.css` from updated SCSS via `./mvnw package -P css`.
- Write a failing Playwright CSS fixture test before adding any classes (TDD RED phase).

### Out of scope

- HTML markup for the health timeline fragment — covered by TASK-14.
- Owner detail page integration — covered by TASK-15.
- JS polling — covered by TASK-17.
- Full E2E happy-path test — covered by TASK-18.
- Any Java / Spring changes.
- A separate `health-timeline.css` file — all styles go in `petclinic.scss`.

## Source excerpts

- `src/main/scss/petclinic.scss` — SCSS source; contains `$spring-green: #24AE1D`,
  Bootstrap `$warning` / `$danger` vars, `.myspinner` keyframe pattern to follow.
- `src/main/resources/static/resources/css/petclinic.css` — compiled output;
  linked by `src/main/resources/templates/fragments/layout.html` line 17.
- `docs/epic-ai-visit-summary.md` TASK-16 — authoritative class name list.

## Acceptance criteria

- **AC-1: `.urgency-routine` defined**
  - AC-1.a: `grep -n '\.urgency-routine' src/main/scss/petclinic.scss` exits 0
    and matches at least one line.
  - AC-1.b: Compiled `petclinic.css` contains `.urgency-routine` with a
    `background-color` value in the green range (`#24AE1D` / `rgb(36, 174, 29)`).
  - AC-1.c: Playwright CSS fixture test asserts the computed `background-color`
    of a `.urgency-routine` element equals `rgb(36, 174, 29)`.

- **AC-2: `.urgency-monitor` defined**
  - AC-2.a: `grep -n '\.urgency-monitor' src/main/scss/petclinic.scss` exits 0.
  - AC-2.b: Compiled `petclinic.css` contains `.urgency-monitor` with a
    `background-color` in the amber range (`#ffc107` / `rgb(255, 193, 7)`).
  - AC-2.c: Playwright CSS fixture test asserts the computed `background-color`
    of a `.urgency-monitor` element equals `rgb(255, 193, 7)`.

- **AC-3: `.urgency-urgent` defined**
  - AC-3.a: `grep -n '\.urgency-urgent' src/main/scss/petclinic.scss` exits 0.
  - AC-3.b: Compiled `petclinic.css` contains `.urgency-urgent` with a
    `background-color` in the red range (`#dc3545` / `rgb(220, 53, 69)`).
  - AC-3.c: Playwright CSS fixture test asserts the computed `background-color`
    of a `.urgency-urgent` element equals `rgb(220, 53, 69)`.

- **AC-4: `.health-tag` defined**
  - AC-4.a: `grep -n '\.health-tag' src/main/scss/petclinic.scss` exits 0.
  - AC-4.b: Compiled `petclinic.css` contains `.health-tag` with a
    `border-radius` value of `9999px` (or equivalent, e.g. `624.9375rem`).
  - AC-4.c: Playwright CSS fixture test asserts the computed `border-radius` of
    a `.health-tag` element is greater than `50px` (pill shape confirmed).

- **AC-5: `.ai-spinner` animation defined**
  - AC-5.a: `grep -n '@keyframes ai-spinner-rotate' src/main/scss/petclinic.scss`
    exits 0.
  - AC-5.b: `grep -n '\.ai-spinner' src/main/scss/petclinic.scss` exits 0.
  - AC-5.c: Compiled `petclinic.css` contains both `.ai-spinner` and
    `ai-spinner-rotate` keyframe.
  - AC-5.d: Playwright CSS fixture test asserts the computed `animation-name`
    of an `.ai-spinner` element equals `ai-spinner-rotate`.

- **AC-6: Existing test suite unaffected**
  - AC-6.a: `./mvnw test` exits 0 with no test failures after CSS changes are
    applied and `petclinic.css` is recompiled.

- **AC-7: TDD — failing test written before implementation**
  - AC-7.a: A commit containing only the Playwright CSS fixture test
    (`e2e-tests/tests/health-timeline-css.spec.ts`) exists before any commit
    that modifies `petclinic.scss`. The test must fail at that intermediate
    commit (confirmed by the proof artifact for Task 01).

## Conventions

- All new CSS rules are added to `src/main/scss/petclinic.scss`, after the
  last existing custom rule block and before the `@import` lines at the
  bottom of the file.
- Use SCSS variables already declared in `petclinic.scss` (`$spring-green`,
  `$spring-brown`) rather than re-declaring color literals.
- Urgency badge classes are self-contained and must not require the Bootstrap
  `.badge` class to be present on the same element.
- The keyframe name `ai-spinner-rotate` must not collide with the existing
  `spinner` keyframe referenced by `.myspinner`.
- After any change to `petclinic.scss`, run `./mvnw package -P css` to
  regenerate `petclinic.css` before running Playwright tests.
- TDD is mandatory: the Playwright CSS fixture test must be written and
  confirmed failing before `petclinic.scss` is modified.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
