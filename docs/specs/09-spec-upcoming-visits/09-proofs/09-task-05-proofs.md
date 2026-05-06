# Proofs: Task 05 — Write Playwright E2E test

Covers: AC-8.a, AC-8.b, AC-8.c

## Planned evidence

- Output of `grep -n "upcoming\|Upcoming" e2e-tests/tests/features/upcoming-visits.spec.ts` returning at least one match.
- Output of `grep -n "screenshot" e2e-tests/tests/features/upcoming-visits.spec.ts` returning at least one match.
- Output of `cd e2e-tests && npm test -- --grep "Upcoming Visits"` exiting 0 with `"shows a visit scheduled within the next 7 days"` passing.
- Confirmation that `upcoming-visits-table.png` was written to the Playwright output path.

## Completion notes

### AC-8.a: E2E test creates visit and asserts row visible on `/visits/upcoming`

```text
$ grep -n "upcoming\|Upcoming" e2e-tests/tests/features/upcoming-visits.spec.ts
3:test.describe('Upcoming Visits', () => {
17:    const description = 'E2E upcoming visit test ' + Date.now();
26:    // Navigate to /visits/upcoming and verify the visit appears
27:    await page.goto('/visits/upcoming');
28:    await expect(page.getByRole('heading', { name: /Upcoming Visits/i })).toBeVisible();
31:    await page.screenshot({ path: testInfo.outputPath('upcoming-visits-table.png'), fullPage: true });
```

### AC-8.b: E2E test captures screenshot of populated upcoming-visits table

```text
$ grep -n "screenshot" e2e-tests/tests/features/upcoming-visits.spec.ts
31:    await page.screenshot({ path: testInfo.outputPath('upcoming-visits-table.png'), fullPage: true });
```

Screenshot confirmed written:

```text
e2e-tests/test-results/artifacts/features-upcoming-visits-U-ea7ff-uled-within-the-next-7-days-chromium/upcoming-visits-table.png
```

### AC-8.c: `npm test -- --grep "Upcoming Visits"` exits 0

```text
$ npm test -- --grep "Upcoming Visits"

Running 1 test using 1 worker

  1 passed (7.9s)
```

Screenshot shows the populated table: date 2026-05-09, owner "George Franklin" (linked),
pet "Leo", description "E2E upcoming visit test 1778092573859". Nav bar shows
"UPCOMING VISITS" link active.

### Notes

The spec's test code used `page.goto(href!)` to navigate to the visit form. The "Add Visit"
link in `ownerDetails.html` uses a Thymeleaf relative URL expression (`@{...}`), so
`getAttribute('href')` returns a relative path that does not resolve correctly with
`page.goto`. Fixed to use `addVisitLink.click()` consistent with `visit-scheduling.spec.ts`.
