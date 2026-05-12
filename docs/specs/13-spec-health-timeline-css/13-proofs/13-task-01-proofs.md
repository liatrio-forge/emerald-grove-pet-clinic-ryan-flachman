# Proofs: Task 01 — Write failing Playwright CSS fixture test

Covers: AC-7.a, AC-1.c, AC-2.c, AC-3.c, AC-4.c, AC-5.d

## Planned evidence

- Contents of `e2e-tests/tests/health-timeline-css.spec.ts` showing each
  computed-style assertion.
- Output of running the test suite at the RED-phase commit (before any SCSS
  changes), showing the tests fail with assertion errors on `background-color`
  and `animation-name`.
- `git log --oneline -5` showing the Playwright test commit precedes any SCSS
  modification commit.

## Completion notes

### AC-7.a: A commit containing only the Playwright CSS fixture test exists before any commit that modifies `petclinic.scss`

This task creates `e2e-tests/tests/health-timeline-css.spec.ts` and commits it with no SCSS changes.
Task 02 commits the SCSS modifications. The git log below (captured after Task 02 completes) will show
the Playwright test commit SHA precedes the SCSS commit SHA.

```text
$ git log --oneline -5
(captured after Task 02 — see Task 04 validate proof for final git log)
```

RED-phase run confirmed test file exists and fails before any SCSS change:

```text
$ cd e2e-tests && npx playwright test --grep "health-timeline CSS"

Running 1 test using 1 worker

  1) [chromium] › tests/health-timeline-css.spec.ts:2:1 › health-timeline CSS classes have correct computed styles

    Error: expect(locator).toHaveCSS(expected) failed

    Locator:  locator('#test-urgency-routine')
    Expected: "rgb(36, 174, 29)"
    Received: "rgba(0, 0, 0, 0)"
    Timeout:  5000ms

    Call log:
      - Expect "toHaveCSS" with timeout 5000ms
      - waiting for locator('#test-urgency-routine')
        9 × locator resolved to <span class="urgency-routine" id="test-urgency-routine"></span>
          - unexpected value "rgba(0, 0, 0, 0)"

      21 |
      22 |   const routine = page.locator('#test-urgency-routine');
    > 23 |   await expect(routine).toHaveCSS('background-color', 'rgb(36, 174, 29)');
         |                         ^
      24 |
      25 |   const monitor = page.locator('#test-urgency-monitor');
      26 |   await expect(monitor).toHaveCSS('background-color', 'rgb(255, 193, 7)');

  1 failed
    [chromium] › tests/health-timeline-css.spec.ts:2:1 › health-timeline CSS classes have correct computed styles
```

Classes absent from `petclinic.css` — all five assertions would fail. Test stops at first assertion.

### AC-1.c, AC-2.c, AC-3.c, AC-4.c, AC-5.d: Playwright asserts computed styles

These ACs are satisfied in the GREEN phase (Task 03). This proof confirms the test exists and the assertions are present in the test file. Evidence of passing is in `13-task-03-proofs.md`.

**Test file:** `e2e-tests/tests/health-timeline-css.spec.ts`

```typescript
import { test, expect } from '@fixtures/base-test';

test('health-timeline CSS classes have correct computed styles', async ({ page }) => {
  await page.goto('/');

  await page.evaluate(() => {
    const classes = [
      'urgency-routine',
      'urgency-monitor',
      'urgency-urgent',
      'health-tag',
      'ai-spinner',
    ];
    for (const cls of classes) {
      const span = document.createElement('span');
      span.className = cls;
      span.id = `test-${cls}`;
      document.body.appendChild(span);
    }
  });

  const routine = page.locator('#test-urgency-routine');
  await expect(routine).toHaveCSS('background-color', 'rgb(36, 174, 29)');

  const monitor = page.locator('#test-urgency-monitor');
  await expect(monitor).toHaveCSS('background-color', 'rgb(255, 193, 7)');

  const urgent = page.locator('#test-urgency-urgent');
  await expect(urgent).toHaveCSS('background-color', 'rgb(220, 53, 69)');

  const tag = page.locator('#test-health-tag');
  const borderRadius = await tag.evaluate(
    (el) => parseFloat(getComputedStyle(el).borderRadius),
  );
  expect(borderRadius).toBeGreaterThan(50);

  const spinner = page.locator('#test-ai-spinner');
  await expect(spinner).toHaveCSS('animation-name', 'ai-spinner-rotate');
});
```
