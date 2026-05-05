# Proofs: Task 01 — Write failing Playwright E2E test (RED phase)

Covers: AC-4.a, AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b, AC-2.c, AC-3.a, AC-4.b

## Planned evidence

- `e2e-tests/tests/features/language-switching.spec.ts` created (AC-4.a).
- Output of `cd e2e-tests && npm test -- --grep "language selector"` showing the
  test **failing** — confirms RED phase: the selector is not yet in the DOM.

## Completion notes

### AC-4.a: `e2e-tests/tests/features/language-switching.spec.ts` exists

```text
$ ls e2e-tests/tests/features/language-switching.spec.ts
e2e-tests/tests/features/language-switching.spec.ts
```

File created with a single test titled
`"language selector switches UI language and persists across navigation"`.

### RED phase — `npx playwright test --grep "language selector"` (expected failure)

```text
Running 1 test using 1 worker

  1) [chromium] › tests/features/language-switching.spec.ts:2:1 › language selector switches UI language and persists across navigation

    Error: expect(locator).toBeVisible() failed

    Locator: locator('nav.navbar .dropdown-toggle')
    Expected: visible
    Timeout: 5000ms
    Error: element(s) not found

    Call log:
      - Expect "toBeVisible" with timeout 5000ms
      - waiting for locator('nav.navbar .dropdown-toggle')

       5 |   await page.goto('/');
       6 |   const toggle = page.locator('nav.navbar .dropdown-toggle');
    >  7 |   await expect(toggle).toBeVisible();
         |                        ^
       8 |

  1 failed
    [chromium] › tests/features/language-switching.spec.ts:2:1 › language selector switches UI language and persists across navigation
```

Test fails at line 7 because `.dropdown-toggle` does not exist in the navbar —
the dropdown HTML has not been added yet. RED phase is confirmed correct.

### Notes

RED phase complete. `layout.html` was not modified in this task.
