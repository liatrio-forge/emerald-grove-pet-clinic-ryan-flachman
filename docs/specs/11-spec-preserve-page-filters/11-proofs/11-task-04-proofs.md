# Proofs: Task 04 — Write Playwright E2E test for filter preservation across pages

Covers: AC-3.a, AC-3.b

## Planned evidence

- `npm test -- --grep "preserves lastName filter"` output from `e2e-tests/` showing the test PASSING
- Playwright screenshot of the second filtered page showing the URL with `lastName=F` in the address bar
- Playwright screenshot showing owner table rows on page 2 all starting with "F"

## Completion notes

### AC-3.a: Playwright: click next-page with `lastName=F` active → URL contains `lastName=F`

The test uses a unique prefix `F${Date.now()}` for both firstName and lastName of 6
created owners. After searching by that prefix and clicking next:

```typescript
await expect(page).toHaveURL(new RegExp(`lastName=${uniquePrefix}`));
```

URL observed on page 2 (example): `/owners?page=2&lastName=F1746576322000`
Pattern `lastName=F1746...` contains `lastName=F`. ✓

### AC-3.b: Playwright: second page shows only owners whose name starts with "F"

Each owner's `firstName` is `${uniquePrefix}${i}` and `lastName` is `${uniquePrefix}L${i}`,
both starting with "F". The name cell renders `"firstName lastName"`, so every cell starts
with "F". Assertion:

```typescript
for (let i = 0; i < count; i++) {
    const cellText = await nameCells.nth(i).textContent();
    expect(cellText?.trim()).toMatch(/^F/);
}
```

### Playwright test run output

```text
$ cd e2e-tests && npm test -- --grep "preserves lastName filter"

Running 1 test using 1 worker

[1/1] [chromium] › tests/features/owner-management.spec.ts:166:3 › Owner Management › preserves lastName filter when navigating to next page

  1 passed (8.7s)
```

### Notes

- Test uses `{ timeout: 90_000 }` because it creates 6 owners sequentially; actual runtime ~8.7s.
- 6 owners with unique prefix → exactly 6 results in DB (no seed data overlap) → 2 pages of 5.
- `await page.goto('/owners/new')` used directly instead of openFindOwners + clickAddOwner
  to reduce navigation steps per iteration.
- Screenshot artifact saved to `testInfo.outputPath('filter-preserved-page2.png')`.
