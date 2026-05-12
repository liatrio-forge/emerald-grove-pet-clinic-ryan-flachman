import { test, expect } from '@fixtures/base-test';

test('health-timeline CSS classes have correct computed styles', async ({ page }) => {
  await page.goto('/');

  await page.evaluate(() => {
    const classes = [
      'urgency-routine',
      'urgency-monitor',
      'urgency-urgent',
      'health-tag',
      'ai-spinner',
    ];
    for (const cls of classes) {
      const span = document.createElement('span');
      span.className = cls;
      span.id = `test-${cls}`;
      document.body.appendChild(span);
    }
  });

  const routine = page.locator('#test-urgency-routine');
  await expect(routine).toHaveCSS('background-color', 'rgb(36, 174, 29)');

  const monitor = page.locator('#test-urgency-monitor');
  await expect(monitor).toHaveCSS('background-color', 'rgb(255, 193, 7)');

  const urgent = page.locator('#test-urgency-urgent');
  await expect(urgent).toHaveCSS('background-color', 'rgb(220, 53, 69)');

  const tag = page.locator('#test-health-tag');
  const borderRadius = await tag.evaluate(
    (el) => parseFloat(getComputedStyle(el).borderRadius),
  );
  expect(borderRadius).toBeGreaterThan(50);

  const spinner = page.locator('#test-ai-spinner');
  await expect(spinner).toHaveCSS('animation-name', 'ai-spinner-rotate');
});
