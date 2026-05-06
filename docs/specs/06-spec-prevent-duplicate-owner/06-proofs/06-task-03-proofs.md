# Proofs: Task 03 — Write failing Playwright E2E test (RED)

Covers: AC-5.a, AC-5.b

## Planned evidence

- `owner-management.spec.ts` diff showing the new `"blocks duplicate owner creation"` test.
- `cd e2e-tests && npm test -- --grep "blocks duplicate owner creation"` failure output confirming the test fails because the second submission currently redirects to a new owner detail page rather than showing an error (RED phase).

## Completion notes

### AC-5.a: `owner-management.spec.ts` contains `"blocks duplicate owner creation"`

Test appended to `e2e-tests/tests/features/owner-management.spec.ts` inside the `Owner Management` describe block:

- Creates a unique owner via `createOwner()`
- Submits the form, asserts owner detail page visible (first creation succeeds)
- Navigates to `/owners/new`, fills form with same `firstName`, `lastName`, `telephone`
- Submits again
- Asserts `expect(page).not.toHaveURL(/\/owners\/\d+/)` — must stay on creation path
- Asserts `page.getByText(/already exists/i)` is visible
- Captures screenshot to `testInfo.outputPath('duplicate-owner-error.png')`

### AC-5.b: RED phase — `cd e2e-tests && npm test -- --grep "blocks duplicate owner creation"`

```text
Expected pattern: not /\/owners\/\d+/
    Received string: "http://localhost:8080/owners/12"

      146 |     await expect(page).not.toHaveURL(/\/owners\/\d+/);
               ^
    1 failed
    [chromium] › tests/features/owner-management.spec.ts:154:3 › Owner Management › blocks duplicate owner creation
```

Test fails as expected — second submission redirects to a new owner detail page (`/owners/12`) because the controller does not yet reject duplicates. RED phase confirmed.
