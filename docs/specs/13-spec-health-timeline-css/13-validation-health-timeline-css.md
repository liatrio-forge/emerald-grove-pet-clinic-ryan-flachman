# Validation: Health Timeline CSS (13)

## Automated verification

From repository root:

```bash
# AC-1.a — .urgency-routine exists in SCSS source
grep -n '\.urgency-routine' src/main/scss/petclinic.scss
# Expected: at least one matching line, exit 0

# AC-2.a — .urgency-monitor exists in SCSS source
grep -n '\.urgency-monitor' src/main/scss/petclinic.scss
# Expected: at least one matching line, exit 0

# AC-3.a — .urgency-urgent exists in SCSS source
grep -n '\.urgency-urgent' src/main/scss/petclinic.scss
# Expected: at least one matching line, exit 0

# AC-4.a — .health-tag exists in SCSS source
grep -n '\.health-tag' src/main/scss/petclinic.scss
# Expected: at least one matching line, exit 0

# AC-5.a — @keyframes ai-spinner-rotate exists in SCSS source
grep -n '@keyframes ai-spinner-rotate' src/main/scss/petclinic.scss
# Expected: at least one matching line, exit 0

# AC-5.b — .ai-spinner exists in SCSS source
grep -n '\.ai-spinner' src/main/scss/petclinic.scss
# Expected: at least one matching line, exit 0

# AC-1.b, AC-5.c — compiled CSS contains the class names
grep -c '\.urgency-routine\|\.urgency-monitor\|\.urgency-urgent\|\.health-tag\|\.ai-spinner\|ai-spinner-rotate' \
  src/main/resources/static/resources/css/petclinic.css
# Expected: count >= 6, exit 0

# AC-6.a — existing Java test suite passes
./mvnw test
# Expected: BUILD SUCCESS, exit 0

# AC-1.c, AC-2.c, AC-3.c, AC-4.c, AC-5.d — Playwright CSS fixture test
cd e2e-tests && npm test -- --grep "health-timeline CSS"
# Expected: all assertions pass, exit 0
```

**CSS compile gate:** Before running Playwright tests, ensure `petclinic.css`
is up to date:

```bash
./mvnw package -P css
```

## Traceability

- Feature spec: `13-spec-health-timeline-css.md`
- Task breakdown: `13-tasks-health-timeline-css.md`
- Questions and decisions: `13-questions-1-health-timeline-css.md`
- Per-task evidence: `13-proofs/13-task-NN-proofs.md`
- Parent epic: `docs/epic-ai-visit-summary.md` TASK-16

## Manual checks

- Visually confirm urgency badges render as colored pills on a dark background
  in the owner detail page (once TASK-14/15 are implemented).
- Confirm `.ai-spinner` visibly rotates in a browser (animation plays).

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `.urgency-routine` in `petclinic.scss` | `13-proofs/13-task-02-proofs.md` | command output | PASS |
| AC-1.b | Compiled CSS contains `.urgency-routine` with green background | `13-proofs/13-task-03-proofs.md` | command output | PASS |
| AC-1.c | Playwright asserts `background-color` is `rgb(36, 174, 29)` | `13-proofs/13-task-01-proofs.md` | Playwright test pass | PASS |
| AC-2.a | `.urgency-monitor` in `petclinic.scss` | `13-proofs/13-task-02-proofs.md` | command output | PASS |
| AC-2.b | Compiled CSS contains `.urgency-monitor` with amber background | `13-proofs/13-task-03-proofs.md` | command output | PASS |
| AC-2.c | Playwright asserts `background-color` is `rgb(255, 193, 7)` | `13-proofs/13-task-01-proofs.md` | Playwright test pass | PASS |
| AC-3.a | `.urgency-urgent` in `petclinic.scss` | `13-proofs/13-task-02-proofs.md` | command output | PASS |
| AC-3.b | Compiled CSS contains `.urgency-urgent` with red background | `13-proofs/13-task-03-proofs.md` | command output | PASS |
| AC-3.c | Playwright asserts `background-color` is `rgb(220, 53, 69)` | `13-proofs/13-task-01-proofs.md` | Playwright test pass | PASS |
| AC-4.a | `.health-tag` in `petclinic.scss` | `13-proofs/13-task-02-proofs.md` | command output | PASS |
| AC-4.b | Compiled CSS contains `.health-tag` with pill border-radius | `13-proofs/13-task-03-proofs.md` | command output | PASS |
| AC-4.c | Playwright asserts `border-radius` > 50px on `.health-tag` | `13-proofs/13-task-01-proofs.md` | Playwright test pass | PASS |
| AC-5.a | `@keyframes ai-spinner-rotate` in `petclinic.scss` | `13-proofs/13-task-02-proofs.md` | command output | PASS |
| AC-5.b | `.ai-spinner` in `petclinic.scss` | `13-proofs/13-task-02-proofs.md` | command output | PASS |
| AC-5.c | Compiled CSS contains `.ai-spinner` and keyframe | `13-proofs/13-task-03-proofs.md` | command output | PASS |
| AC-5.d | Playwright asserts `animation-name` is `ai-spinner-rotate` | `13-proofs/13-task-01-proofs.md` | Playwright test pass | PASS |
| AC-6.a | `./mvnw test` exits 0 | `13-proofs/13-task-04-proofs.md` | Maven test pass | PASS |
| AC-7.a | Playwright test commit precedes SCSS modification commit | `13-proofs/13-task-01-proofs.md` | behavioral evidence | PASS |

## Definition of done

- [x] AC-1.a: `.urgency-routine` exists in `src/main/scss/petclinic.scss`
- [x] AC-1.b: Compiled `petclinic.css` contains `.urgency-routine` with green background-color
- [x] AC-1.c: Playwright CSS fixture test asserts `background-color` is `rgb(36, 174, 29)`
- [x] AC-2.a: `.urgency-monitor` exists in `src/main/scss/petclinic.scss`
- [x] AC-2.b: Compiled `petclinic.css` contains `.urgency-monitor` with amber background-color
- [x] AC-2.c: Playwright CSS fixture test asserts `background-color` is `rgb(255, 193, 7)`
- [x] AC-3.a: `.urgency-urgent` exists in `src/main/scss/petclinic.scss`
- [x] AC-3.b: Compiled `petclinic.css` contains `.urgency-urgent` with red background-color
- [x] AC-3.c: Playwright CSS fixture test asserts `background-color` is `rgb(220, 53, 69)`
- [x] AC-4.a: `.health-tag` exists in `src/main/scss/petclinic.scss`
- [x] AC-4.b: Compiled `petclinic.css` contains `.health-tag` with pill border-radius
- [x] AC-4.c: Playwright CSS fixture test asserts `border-radius` > 50px on `.health-tag`
- [x] AC-5.a: `@keyframes ai-spinner-rotate` exists in `src/main/scss/petclinic.scss`
- [x] AC-5.b: `.ai-spinner` exists in `src/main/scss/petclinic.scss`
- [x] AC-5.c: Compiled `petclinic.css` contains both `.ai-spinner` and `ai-spinner-rotate`
- [x] AC-5.d: Playwright CSS fixture test asserts `animation-name` is `ai-spinner-rotate`
- [x] AC-6.a: `./mvnw test` exits 0 with no test failures
- [x] AC-7.a: Playwright test commit precedes SCSS modification commit in git history
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS` (or `RETIRED` for amended criteria).
- [x] `./mvnw test` exits 0.
- [x] Parent epic child-registry checkbox ticked (once registry is created).
