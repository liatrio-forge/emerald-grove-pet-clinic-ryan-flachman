# Proofs: Task 02 — Write failing Playwright E2E tests (RED)

Covers: AC-5.a, AC-5.b, AC-1.a

## Planned evidence

- `owner-page.ts` updated with `searchByFilters` helper — show the new method.
- `owner-management.spec.ts` with new test bodies for `"can find owner by telephone"`
  and `"can find owner by city"` — show both tests.
- Output of `cd e2e-tests && npm test -- --grep "Owner Management"` showing the
  new tests **failing** because `#telephone` and `#city` inputs do not yet exist
  in the form (RED phase confirmed).

## Completion notes

### AC-5.a / AC-5.b: New E2E tests added to `owner-management.spec.ts`

`owner-page.ts` — new `searchByFilters` helper:

```typescript
async searchByFilters(filters: { lastName?: string; telephone?: string; city?: string }): Promise<void> {
  if (filters.lastName !== undefined) {
    await this.page.locator('input#lastName').fill(filters.lastName);
  }
  if (filters.telephone !== undefined) {
    await this.page.locator('input#telephone').fill(filters.telephone);
  }
  if (filters.city !== undefined) {
    await this.page.locator('input#city').fill(filters.city);
  }
  await this.page.getByRole('button', { name: /Find Owner/i }).click();
}
```

`owner-management.spec.ts` — new tests:

```typescript
test('can find owner by telephone', async ({ page }, testInfo) => {
  const ownerPage = new OwnerPage(page);
  const owner = createOwner();

  await ownerPage.openFindOwners();
  await ownerPage.clickAddOwner();
  await ownerPage.fillOwnerForm(owner);
  await ownerPage.submitOwnerForm();
  await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

  await ownerPage.openFindOwners();
  await ownerPage.searchByFilters({ telephone: owner.telephone });

  await expect(ownerPage.ownersTable()).toBeVisible();
  await expect(ownerPage.ownersTable()).toContainText(`${owner.firstName} ${owner.lastName}`);

  await page.screenshot({ path: testInfo.outputPath('telephone-search.png'), fullPage: true });
});

test('can find owner by city', async ({ page }, testInfo) => {
  const ownerPage = new OwnerPage(page);
  const uniqueCity = `City${Date.now()}`;
  const owner = createOwner({ city: uniqueCity });

  await ownerPage.openFindOwners();
  await ownerPage.clickAddOwner();
  await ownerPage.fillOwnerForm(owner);
  await ownerPage.submitOwnerForm();
  await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

  await ownerPage.openFindOwners();
  await ownerPage.searchByFilters({ city: uniqueCity });

  await expect(ownerPage.ownersTable()).toBeVisible();
  await expect(ownerPage.ownersTable()).toContainText(`${owner.firstName} ${owner.lastName}`);

  await page.screenshot({ path: testInfo.outputPath('city-search.png'), fullPage: true });
});
```

### AC-1.a: RED phase — tests fail for correct reason (form inputs absent)

`cd e2e-tests && npm test -- --grep "can find owner by"` output (RED):

```text
  2) [chromium] › tests/features/owner-management.spec.ts:91:3 › Owner Management › can find owner by telephone

    Test timeout of 30000ms exceeded.

    Error: locator.fill: Test timeout of 30000ms exceeded.
    Call log:
      - waiting for locator('input#telephone')

       at pages/owner-page.ts:41
      39 |     }
      40 |     if (filters.telephone !== undefined) {
    > 41 |       await this.page.locator('input#telephone').fill(filters.telephone);
         |                                                  ^
      42 |     }
      43 |     if (filters.city !== undefined) {
      44 |       await this.page.locator('input#city').fill(filters.city);
        at OwnerPage.searchByFilters (.../e2e-tests/tests/pages/owner-page.ts:41:50)
        at .../e2e-tests/tests/features/owner-management.spec.ts:89:21

  2 failed
    [chromium] › tests/features/owner-management.spec.ts:91:3 › Owner Management › can find owner by telephone
    [chromium] › tests/features/owner-management.spec.ts:114:3 › Owner Management › can find owner by city
```

Both tests fail because `input#telephone` and `input#city` do not yet exist in
`findOwners.html`. RED phase confirmed.
