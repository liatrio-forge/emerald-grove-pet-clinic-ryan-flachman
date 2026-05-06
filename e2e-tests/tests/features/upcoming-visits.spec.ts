import { test, expect } from '@fixtures/base-test';

test.describe('Upcoming Visits', () => {
  test('shows a visit scheduled within the next 7 days', async ({ page }, testInfo) => {
    // Navigate to an existing owner and add a visit with a date within the next 7 days
    await page.goto('/owners/1');
    await expect(page.getByRole('heading', { name: /Pets and Visits/i })).toBeVisible();

    // Click the "Add Visit" link for the first pet to navigate to the visit form
    const addVisitLink = page.getByRole('link', { name: /Add Visit/i }).first();
    await addVisitLink.click();

    // Fill in a date within the next 7 days
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 3);
    const yyyy = futureDate.getFullYear();
    const mm = String(futureDate.getMonth() + 1).padStart(2, '0');
    const dd = String(futureDate.getDate()).padStart(2, '0');
    const dateStr = `${yyyy}-${mm}-${dd}`; // local YYYY-MM-DD
    const description = 'E2E upcoming visit test ' + Date.now();

    await page.locator('input#date').fill(dateStr);
    await page.locator('input#description').fill(description);
    await page.getByRole('button', { name: /Add Visit/i }).click();

    // Should redirect back to owner details
    await expect(page.getByRole('heading', { name: /Pets and Visits/i })).toBeVisible();

    // Navigate to /visits/upcoming and verify the visit appears
    await page.goto('/visits/upcoming');
    await expect(page.getByRole('heading', { name: /Upcoming Visits/i })).toBeVisible();
    await expect(page.getByText(description, { exact: true })).toBeVisible();

    await page.screenshot({ path: testInfo.outputPath('upcoming-visits-table.png'), fullPage: true });
  });
});
