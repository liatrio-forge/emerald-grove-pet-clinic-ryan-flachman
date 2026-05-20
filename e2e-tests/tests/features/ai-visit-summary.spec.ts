import { test, expect } from '@fixtures/base-test';

import { VisitPage } from '@pages/visit-page';

const formatLocalDate = (d: Date) => {
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

test.describe('AI Visit Summary', () => {
  test('shows DONE urgency badge and tag chips after visit save', async ({ page }, testInfo) => {
    const visitPage = new VisitPage(page);

    await page.goto('/owners/6');
    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

    await page.locator('a[href*="pets/7/visits/new"]').click();
    await expect(visitPage.heading()).toBeVisible();

    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 365 + testInfo.retry + testInfo.workerIndex);
    const visitDate = formatLocalDate(futureDate);

    await visitPage.fillVisitDate(visitDate);
    await visitPage.fillDescription('Dog is limping on left front leg');

    await visitPage.submit();

    await expect(page).toHaveURL(/\/owners\/6(?:[;?]|$)/);
    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

    await page.locator('[data-bs-target="#health-timeline-7"]').click();

    const timeline = page.locator('#health-timeline-7');
    const entry = timeline.locator(`[data-visit-date="${visitDate}"][data-ai-status="DONE"]`).first();

    await expect(entry).toBeVisible({ timeout: 15_000 });

    await expect(entry.locator('.urgency-urgent')).toBeVisible();
    await expect(entry.locator('.health-tag').first()).toBeVisible();
    await expect(entry.locator('span.ai-spinner')).toHaveCount(0);
  });
});
