# Tasks: Health Timeline CSS (13)

## Task 01 — Write failing Playwright CSS fixture test

Covers: AC-7.a, AC-1.c, AC-2.c, AC-3.c, AC-4.c, AC-5.d

- Create `e2e-tests/tests/health-timeline-css.spec.ts`.
- The test navigates to the running app's root page (`/`) so `petclinic.css`
  is loaded, then injects five `<span>` elements — one for each class under
  test — via `page.evaluate`.
- Assert computed `background-color` of `.urgency-routine` equals
  `rgb(36, 174, 29)`.
- Assert computed `background-color` of `.urgency-monitor` equals
  `rgb(255, 193, 7)`.
- Assert computed `background-color` of `.urgency-urgent` equals
  `rgb(220, 53, 69)`.
- Assert the computed `border-radius` value of `.health-tag` parses to a
  number greater than `50` (pixels).
- Assert the computed `animation-name` of `.ai-spinner` equals
  `ai-spinner-rotate`.
- Run the test suite and confirm all five assertions fail with the classes
  absent (`rgba(0, 0, 0, 0)` / `none`) — capture that output as proof.
- Commit only this test file (no SCSS changes in this commit).

**Proof:** 13-proofs/13-task-01-proofs.md

## Task 02 — Add health-timeline CSS classes to `petclinic.scss`

Covers: AC-1.a, AC-2.a, AC-3.a, AC-4.a, AC-5.a, AC-5.b

- Open `src/main/scss/petclinic.scss`.
- Insert the following block after the last existing custom rule and before
  the three `@import` lines at the bottom of the file:

  ```scss
  // Health timeline — urgency badges
  .urgency-routine,
  .urgency-monitor,
  .urgency-urgent {
    display: inline-block;
    padding: 0.25em 0.6em;
    font-size: 0.75em;
    font-weight: 600;
    line-height: 1;
    border-radius: 9999px;
    white-space: nowrap;
  }

  .urgency-routine {
    background-color: $spring-green;
    color: $spring-brown;
  }

  .urgency-monitor {
    background-color: #ffc107;
    color: #111111;
  }

  .urgency-urgent {
    background-color: #dc3545;
    color: #f8f9fa;
  }

  // Health timeline — tag chip
  .health-tag {
    display: inline-block;
    padding: 0.2em 0.55em;
    font-size: 0.75em;
    border: 1px solid $spring-green;
    border-radius: 9999px;
    color: $spring-green;
    background-color: transparent;
    white-space: nowrap;
  }

  // Health timeline — loading spinner
  @keyframes ai-spinner-rotate {
    from { transform: rotate(0deg); }
    to   { transform: rotate(360deg); }
  }

  .ai-spinner {
    display: inline-block;
    width: 1em;
    height: 1em;
    border: 0.15em solid currentColor;
    border-top-color: transparent;
    border-radius: 50%;
    animation: ai-spinner-rotate 0.75s linear infinite;
  }
  ```

- Run all six `grep -n` checks from the validation plan to confirm each
  class and the keyframe appear in the source.

**Proof:** 13-proofs/13-task-02-proofs.md

## Task 03 — Compile SCSS and verify classes in `petclinic.css`

Covers: AC-1.b, AC-2.b, AC-3.b, AC-4.b, AC-5.c

- Run `./mvnw package -P css` from the repository root and confirm
  BUILD SUCCESS.
- Run the `grep -c` command from the validation plan against the
  regenerated `petclinic.css` and confirm the count is ≥ 6.
- Spot-check the compiled file: grep for the literal background-color
  values (`#24ae1d` or its compiled form, `#ffc107`, `#dc3545`).
- Re-run `cd e2e-tests && npm test -- --grep "health-timeline CSS"` and
  confirm all five Playwright assertions now pass (GREEN phase).
- Capture the passing test output as proof.

**Proof:** 13-proofs/13-task-03-proofs.md

## Task 04 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` from the repository root and confirm BUILD SUCCESS with
  zero failures.
- Run all automated verification commands from `13-validation-health-timeline-css.md`
  in order and capture their output.
- Confirm every AC ID in the coverage matrix has at least one passing proof
  artifact; update each row's Status from `PENDING` to `PASS`.
- Update the Definition of Done checklist in `13-validation-health-timeline-css.md`.

**Proof:** 13-proofs/13-task-04-proofs.md
