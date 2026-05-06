# Proofs: Task 05 — Write Playwright E2E test for missing owner 404

Covers: AC-8.a, AC-8.b, AC-8.c

## Planned evidence

- `grep -n "99999\|not.found\|notFound\|404" e2e-tests/tests/features/owner-management.spec.ts`
  output (≥1 match).
- `cd e2e-tests && npm test -- --grep "Owner Management"` output confirming all
  tests pass including `"shows friendly 404 page for non-existent owner"`.
- `owner-not-found.png` screenshot captured to the Playwright artifacts output
  path.

## Completion notes

### AC-8.a: Playwright test navigates to `/owners/99999` and asserts not-found message

```text
$ grep -n "99999\|not.found\|notFound\|404" e2e-tests/tests/features/owner-management.spec.ts
125:  test('shows friendly 404 page for non-existent owner', async ({ page }, testInfo) => {
126:    await page.goto('/owners/99999');
127:    await expect(page.getByText(/not found/i)).toBeVisible();
129:    await page.screenshot({ path: testInfo.outputPath('owner-not-found.png') });
```

### AC-8.b: Playwright test asserts Find Owners link is visible on 404 page

Test line 128: `await expect(page.locator('.liatrio-error-card').getByRole('link', { name: /find owners/i })).toBeVisible()`

Scoped to `.liatrio-error-card` to avoid strict-mode multi-match with the navbar "Find Owners" link.

### AC-8.c: `npm test -- --grep "Owner Management"` exits 0

```text
$ cd e2e-tests && npm test -- --grep "Owner Management"

Running 8 tests using 8 workers

  8 passed (11.0s)
```

All 8 Owner Management tests pass, including "shows friendly 404 page for non-existent owner".
Spring log shows: `Resolved [ResourceNotFoundException: Owner not found with id: 99999]`

### Notes

`owner-not-found.png` written to Playwright artifacts path (testInfo.outputPath).
Screenshot confirms branded 404 page renders with image, "Page Not Found" heading, body text, and "Find Owners" button.
