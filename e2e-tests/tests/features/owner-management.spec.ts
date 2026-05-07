import { test, expect } from '@fixtures/base-test';

import { OwnerPage } from '@pages/owner-page';
import { createOwner } from '@utils/data-factory';
import { measureMs } from '@utils/test-helpers';

test.describe('Owner Management', () => {
  test('can search for an existing owner and view pets/visits', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.openFindOwners();

    const { durationMs } = await measureMs(async () => {
      await ownerPage.searchByLastName('Davis');
      await expect(ownerPage.ownersTable()).toBeVisible();
    });

    await page.screenshot({ path: testInfo.outputPath('owner-search-results.png'), fullPage: true });

    expect(durationMs).toBeLessThan(3_000);

    await ownerPage.openOwnerDetailsByName('Betty Davis');
    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();
    await expect(page.getByRole('heading', { name: /Pets and Visits/i })).toBeVisible();
  });

  test('can add a new owner and then edit owner info', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);
    const owner = createOwner();

    await ownerPage.openFindOwners();
    await ownerPage.clickAddOwner();

    await ownerPage.fillOwnerForm(owner);
    await page.screenshot({ path: testInfo.outputPath('new-owner-form-filled.png'), fullPage: true });

    await ownerPage.submitOwnerForm();

    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();
    await expect(page.getByRole('cell', { name: `${owner.firstName} ${owner.lastName}` })).toBeVisible();

    await ownerPage.clickEditOwner();

    const updatedCity = 'Updated City';
    await ownerPage.fillCity(updatedCity);
    await ownerPage.submitOwnerForm();

    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();
    await expect(page.getByRole('cell', { name: /Updated City/i })).toBeVisible();

    await page.screenshot({ path: testInfo.outputPath('owner-details-after-edit.png'), fullPage: true });
  });

  test('shows validation error for invalid telephone', async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    const owner = createOwner({ telephone: '123' });

    await ownerPage.openFindOwners();
    await ownerPage.clickAddOwner();

    await ownerPage.fillOwnerForm(owner);
    await ownerPage.submitOwnerForm();

    await expect(page.getByText(/Telephone must be a 10-digit number/i)).toBeVisible();
  });

  test('owner form is usable in a mobile viewport', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await page.setViewportSize({ width: 375, height: 812 });

    await ownerPage.openFindOwners();
    await ownerPage.clickAddOwner();

    await expect(page.getByRole('button', { name: /Add Owner/i })).toBeVisible();
  });

  test('can find owner by telephone', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);
    // Unique 7-digit prefix (starts with '9' to avoid seed data collision)
    const phonePrefix = '9' + String(Date.now()).slice(-6);
    const owner1 = createOwner({ telephone: phonePrefix + '001' });
    const owner2 = createOwner({ telephone: phonePrefix + '002' });

    for (const owner of [owner1, owner2]) {
      await ownerPage.openFindOwners();
      await ownerPage.clickAddOwner();
      await ownerPage.fillOwnerForm(owner);
      await ownerPage.submitOwnerForm();
      await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();
    }

    await ownerPage.openFindOwners();
    await ownerPage.searchByFilters({ telephone: phonePrefix });

    await expect(ownerPage.ownersTable()).toBeVisible();
    await expect(ownerPage.ownersTable()).toContainText(`${owner1.firstName} ${owner1.lastName}`);

    await page.screenshot({ path: testInfo.outputPath('telephone-search.png'), fullPage: true });
  });

  test('can find owner by city', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);
    const uniqueCity = `City${Date.now()}`;
    const owner1 = createOwner({ city: uniqueCity });
    const owner2 = createOwner({ city: uniqueCity });

    for (const owner of [owner1, owner2]) {
      await ownerPage.openFindOwners();
      await ownerPage.clickAddOwner();
      await ownerPage.fillOwnerForm(owner);
      await ownerPage.submitOwnerForm();
      await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();
    }

    await ownerPage.openFindOwners();
    await ownerPage.searchByFilters({ city: uniqueCity });

    await expect(ownerPage.ownersTable()).toBeVisible();
    await expect(ownerPage.ownersTable()).toContainText(`${owner1.firstName} ${owner1.lastName}`);

    await page.screenshot({ path: testInfo.outputPath('city-search.png'), fullPage: true });
  });

  test('shows friendly 404 page for non-existent owner', async ({ page }, testInfo) => {
    await page.goto('/owners/99999');
    await expect(page.getByText(/not found/i)).toBeVisible();
    await expect(page.locator('.liatrio-error-card').getByRole('link', { name: /find owners/i })).toBeVisible();
    await page.screenshot({ path: testInfo.outputPath('owner-not-found.png') });
  });

  test('preserves lastName filter when navigating to next page', { timeout: 90_000 }, async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);
    // Unique prefix starting with F — both firstName and lastName start with F
    // so every name cell in the results table starts with "F" (satisfies AC-3.b).
    const uniquePrefix = `F${Date.now()}`;

    // Create 6 owners so the filtered result spans 2 pages (page size = 5)
    for (let i = 0; i < 6; i++) {
      await page.goto('/owners/new');
      const owner = createOwner({
        firstName: `${uniquePrefix}${i}`,
        lastName: `${uniquePrefix}L${i}`,
      });
      await ownerPage.fillOwnerForm(owner);
      await ownerPage.submitOwnerForm();
      await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();
    }

    // Search by the unique prefix — returns exactly the 6 owners created above
    await ownerPage.openFindOwners();
    await ownerPage.searchByFilters({ lastName: uniquePrefix });
    await expect(ownerPage.ownersTable()).toBeVisible();

    // Navigate to page 2
    const nextLink = page.locator('a.fa-step-forward');
    await expect(nextLink).toBeVisible();
    await nextLink.click();

    // AC-3.a: URL still contains the lastName filter
    await expect(page).toHaveURL(new RegExp(`lastName=${uniquePrefix}`));

    await page.screenshot({ path: testInfo.outputPath('filter-preserved-page2.png'), fullPage: true });

    // AC-3.b: all visible owner name cells start with "F"
    const nameCells = page.locator('#owners tbody tr td:first-child');
    await expect(nameCells.first()).toBeVisible();
    const count = await nameCells.count();
    expect(count).toBeGreaterThan(0);
    for (let i = 0; i < count; i++) {
      const cellText = await nameCells.nth(i).textContent();
      expect(cellText?.trim()).toMatch(/^F/);
    }
  });

  test('blocks duplicate owner creation', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);
    const owner = createOwner();

    await ownerPage.openFindOwners();
    await ownerPage.clickAddOwner();
    await ownerPage.fillOwnerForm(owner);
    await ownerPage.submitOwnerForm();

    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

    await page.goto('/owners/new');
    await ownerPage.fillOwnerForm({
      firstName: owner.firstName,
      lastName: owner.lastName,
      address: owner.address,
      city: owner.city,
      telephone: owner.telephone,
    });
    await ownerPage.submitOwnerForm();

    await expect(page).not.toHaveURL(/\/owners\/\d+/);
    await expect(page.getByText(/already in use/i)).toBeVisible();

    await page.screenshot({ path: testInfo.outputPath('duplicate-owner-error.png') });
  });
});
