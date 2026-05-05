import { test, expect } from '@fixtures/base-test';

import { VetPage } from '@pages/vet-page';

test.describe('Vet Directory', () => {
  test('can browse veterinarian list and view specialties', async ({ page }, testInfo) => {
    const vetPage = new VetPage(page);

    await vetPage.open();

    await expect(vetPage.vetsTable()).toBeVisible();

    // This test relies on Petclinic's startup seed data providing vets.
    const rows = vetPage.vetsTable().locator('tbody tr');
    const rowCount = await rows.count();
    expect(rowCount, 'Expected seeded veterinarians to be present').toBeGreaterThan(0);

    await page.screenshot({ path: testInfo.outputPath('vet-directory.png'), fullPage: true });

    // Validate each row's specialty cell contains a known specialty or "none".
    for (let i = 0; i < rowCount; i++) {
      const specialtyCell = rows.nth(i).locator('td').nth(1);
      await expect(specialtyCell).toContainText(/none|surgery|dentistry|radiology|medicine/i);
    }
  });

  test('can filter vets by specialty using query param', async ({ page }, testInfo) => {
    const vetPage = new VetPage(page);
    await vetPage.open();

    // AC-1.a: filter control is visible
    await expect(vetPage.specialtyFilterPills()).toBeVisible();

    // AC-3.c: "All" pill is active when no filter is applied
    await expect(vetPage.specialtyFilterPills().locator('a.active')).toContainText(/all/i);

    // AC-2.a / AC-3.a: clicking radiology pill shows only radiology vets and marks it active
    await vetPage.clickSpecialtyFilter('radiology');
    await expect(page).toHaveURL(/specialty=radiology/);
    const filteredRows = vetPage.vetsTable().locator('tbody tr');
    const filteredCount = await filteredRows.count();
    expect(filteredCount, 'Expected at least one radiology vet').toBeGreaterThan(0);
    for (let i = 0; i < filteredCount; i++) {
      await expect(filteredRows.nth(i).locator('td').nth(1)).toContainText(/radiology/i);
    }
    await expect(vetPage.specialtyFilterPills().locator('a.active')).toContainText(/radiology/i);

    // AC-5.c: screenshot of filtered list
    await page.screenshot({ path: testInfo.outputPath('vet-filter.png'), fullPage: true });

    // AC-4.b: direct navigation to ?specialty=radiology produces same filtered results
    await page.goto('/vets.html?specialty=radiology');
    await vetPage.heading().waitFor();
    const directRows = vetPage.vetsTable().locator('tbody tr');
    const directCount = await directRows.count();
    expect(directCount).toBe(filteredCount);
    await expect(vetPage.specialtyFilterPills().locator('a.active')).toContainText(/radiology/i);

    // AC-4.a: pagination links propagate specialty state. Seed data has <5 vets per
    // specialty so pagination only renders on the unfiltered view. Verify those links
    // omit specialty= (Thymeleaf null-param omission). The filtered direction is
    // covered by VetControllerTests unit tests and template inspection.
    await page.goto('/vets.html');
    await vetPage.heading().waitFor();
    const paginationLinks = page.locator('.liatrio-pagination a[href*="page="]');
    const paginationCount = await paginationLinks.count();
    expect(paginationCount, 'Expected pagination links to be present on unfiltered vet list')
      .toBeGreaterThan(0);
    for (let i = 0; i < paginationCount; i++) {
      const href = await paginationLinks.nth(i).getAttribute('href');
      if (href) {
        expect(href).not.toContain('specialty=');
      }
    }

    // AC-3.b / AC-2.b: "None" pill shows vets with no specialty and marks it active
    await page.goto('/vets.html');
    await vetPage.heading().waitFor();
    await vetPage.clickSpecialtyFilter('None');
    await expect(page).toHaveURL(/specialty=none/);
    const noneRows = vetPage.vetsTable().locator('tbody tr');
    const noneCount = await noneRows.count();
    expect(noneCount, 'Expected at least one vet with no specialty').toBeGreaterThan(0);
    for (let i = 0; i < noneCount; i++) {
      await expect(noneRows.nth(i).locator('td').nth(1)).toContainText(/none/i);
    }
    await expect(vetPage.specialtyFilterPills().locator('a.active')).toContainText(/none/i);

    // AC-3.c / AC-2.c: "All" pill restores full list
    await vetPage.clickSpecialtyFilter('All');
    await expect(page).not.toHaveURL(/specialty=/);
    await expect(vetPage.specialtyFilterPills().locator('a.active')).toContainText(/all/i);
  });
});
