import { test, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const FIXTURE_PATH = path.join(__dirname, '../fixtures/health-timeline-fixture.html');

async function serveFixture(route: import('@playwright/test').Route) {
  const html = fs.readFileSync(FIXTURE_PATH, 'utf-8');
  await route.fulfill({ contentType: 'text/html; charset=utf-8', body: html });
}

function visitIdFromSummaryUrl(url: string): string {
  const m = url.match(/\/visits\/(\d+)\/summary/);
  if (!m) {
    throw new Error(`Unexpected summary URL: ${url}`);
  }
  return m[1];
}

test.describe('health-timeline polling', () => {
  test('health-timeline polling | initialises intervals for all non-terminal entries on load', async ({ page }) => {
    await page.clock.install();
    let summaryFetches = 0;
    await page.route('**/visits/*/summary', async (route) => {
      summaryFetches++;
      await route.fulfill({
        contentType: 'application/json',
        body: JSON.stringify({ status: 'PENDING' })
      });
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect.poll(() => summaryFetches).toBe(3);
  });

  test('health-timeline polling | does not poll DONE or FAILED entries', async ({ page }) => {
    await page.clock.install();
    const visited = new Set<string>();
    await page.route('**/visits/*/summary', async (route) => {
      visited.add(visitIdFromSummaryUrl(route.request().url()));
      await route.fulfill({
        contentType: 'application/json',
        body: JSON.stringify({ status: 'PENDING' })
      });
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect.poll(() => visited.has('1') && visited.has('2') && visited.has('5')).toBe(true);
    expect(visited.has('3')).toBe(false);
    expect(visited.has('4')).toBe(false);
  });

  test('health-timeline polling | polls PROCESSING entries on load and resolves them to DONE', async ({ page }) => {
    await page.clock.install();
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      if (id === '5') {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({
            status: 'DONE',
            summary: 'Follow-up complete.',
            tags: ['recheck'],
            urgency: 'routine'
          })
        });
      } else {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'PENDING' })
        });
      }
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);

    const processingEntry = page.locator('[data-visit-id="5"]');
    await expect(processingEntry).toHaveAttribute('data-ai-status', 'DONE');
    await expect(processingEntry.locator('.ai-summary')).toContainText('Follow-up complete.');
    await expect(processingEntry.locator('span.ai-spinner')).toHaveCount(0);
  });

  test('health-timeline polling | replaces spinner with summary HTML on DONE response', async ({ page }) => {
    await page.clock.install();
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      if (id === '1') {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({
            status: 'DONE',
            summary: 'Recovered well.',
            tags: ['dental'],
            urgency: 'monitor',
            followUp: 'Book cleaning'
          })
        });
      } else {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'PENDING' })
        });
      }
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    const entry1 = page.locator('[data-visit-id="1"]');
    await expect(entry1.locator('span.ai-spinner')).toHaveCount(0);
    await expect(entry1.locator('.ai-summary')).toBeVisible();
  });

  test('health-timeline polling | sets data-ai-status to DONE after DONE response', async ({ page }) => {
    await page.clock.install();
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      if (id === '1') {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({
            status: 'DONE',
            summary: 'OK',
            tags: ['a'],
            urgency: 'routine'
          })
        });
      } else {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'PENDING' })
        });
      }
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect(page.locator('[data-visit-id="1"]')).toHaveAttribute('data-ai-status', 'DONE');
  });

  test('health-timeline polling | cancels interval after DONE response', async ({ page }) => {
    await page.clock.install();
    const counts = new Map<string, number>();
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      counts.set(id, (counts.get(id) ?? 0) + 1);
      if (id === '1') {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({
            status: 'DONE',
            summary: 'Done',
            tags: [],
            urgency: 'routine'
          })
        });
      } else {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'PENDING' })
        });
      }
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect.poll(() => counts.get('1')).toBe(1);
    await page.clock.runFor(9000);
    await expect.poll(() => counts.get('1')).toBe(1);
  });

  test('health-timeline polling | DONE HTML includes urgency badge, tag chips, summary, and follow-up', async ({
    page
  }) => {
    await page.clock.install();
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      if (id === '1') {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({
            status: 'DONE',
            summary: 'Stable vitals.',
            tags: ['weight', 'labs'],
            urgency: 'monitor',
            followUp: 'Return if vomiting'
          })
        });
      } else {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'PENDING' })
        });
      }
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    const entry1 = page.locator('[data-visit-id="1"]');
    await expect(entry1.locator('.urgency-monitor')).toBeVisible();
    await expect(entry1.locator('.health-tag')).toHaveCount(2);
    await expect(entry1.locator('.ai-summary')).toBeVisible();
    await expect(entry1.locator('.ai-follow-up')).toBeVisible();
  });

  test('health-timeline polling | DONE HTML omits follow-up when followUp is null', async ({ page }) => {
    await page.clock.install();
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      if (id === '1') {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({
            status: 'DONE',
            summary: 'All clear.',
            tags: ['x'],
            urgency: 'routine'
          })
        });
      } else {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'PENDING' })
        });
      }
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect(page.locator('[data-visit-id="1"]')).toHaveAttribute('data-ai-status', 'DONE');
    await expect(page.locator('[data-visit-id="1"] .ai-summary')).toBeVisible();
    await expect(page.locator('[data-visit-id="1"] .ai-follow-up')).toHaveCount(0);
  });

  test('health-timeline polling | shows error indicator on FAILED response', async ({ page }) => {
    await page.clock.install();
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      if (id === '1') {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'FAILED' })
        });
      } else {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'PENDING' })
        });
      }
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect(page.locator('[data-visit-id="1"] div.ai-error')).toBeVisible();
  });

  test('health-timeline polling | sets data-ai-status to FAILED after FAILED response', async ({ page }) => {
    await page.clock.install();
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      if (id === '1') {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'FAILED' })
        });
      } else {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'PENDING' })
        });
      }
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect(page.locator('[data-visit-id="1"]')).toHaveAttribute('data-ai-status', 'FAILED');
  });

  test('health-timeline polling | cancels interval after FAILED response', async ({ page }) => {
    await page.clock.install();
    const counts = new Map<string, number>();
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      counts.set(id, (counts.get(id) ?? 0) + 1);
      if (id === '1') {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'FAILED' })
        });
      } else {
        await route.fulfill({
          contentType: 'application/json',
          body: JSON.stringify({ status: 'PENDING' })
        });
      }
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect.poll(() => counts.get('1')).toBe(1);
    await page.clock.runFor(9000);
    await expect.poll(() => counts.get('1')).toBe(1);
  });

  test('health-timeline polling | treats entry as FAILED after 40 polls without terminal status', async ({
    page
  }) => {
    await page.clock.install();
    const visit1Polls = { n: 0 };
    await page.route('**/visits/*/summary', async (route) => {
      const id = visitIdFromSummaryUrl(route.request().url());
      if (id === '1') {
        visit1Polls.n++;
      }
      await route.fulfill({
        contentType: 'application/json',
        body: JSON.stringify({ status: 'PENDING' })
      });
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000 * 40);
    await expect(page.locator('[data-visit-id="1"] div.ai-error')).toBeVisible();
    await expect.poll(() => visit1Polls.n).toBe(40);
    const afterTimeout = visit1Polls.n;
    await page.clock.runFor(3000);
    expect(visit1Polls.n).toBe(afterTimeout);
  });

  test('health-timeline polling | pauses polling when tab becomes hidden', async ({ page }) => {
    await page.clock.install();
    let summaryFetches = 0;
    await page.route('**/visits/*/summary', async (route) => {
      summaryFetches++;
      await route.fulfill({
        contentType: 'application/json',
        body: JSON.stringify({ status: 'PENDING' })
      });
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect.poll(() => summaryFetches).toBe(3);
    const afterFirstRound = summaryFetches;
    await page.evaluate(() => {
      Object.defineProperty(document, 'visibilityState', { value: 'hidden', configurable: true });
      document.dispatchEvent(new Event('visibilitychange'));
    });
    await page.clock.runFor(3100);
    expect(summaryFetches).toBe(afterFirstRound);
  });

  test('health-timeline polling | resumes polling when tab becomes visible', async ({ page }) => {
    await page.clock.install();
    let summaryFetches = 0;
    await page.route('**/visits/*/summary', async (route) => {
      summaryFetches++;
      await route.fulfill({
        contentType: 'application/json',
        body: JSON.stringify({ status: 'PENDING' })
      });
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect.poll(() => summaryFetches).toBe(3);
    const afterFirstRound = summaryFetches;
    await page.evaluate(() => {
      Object.defineProperty(document, 'visibilityState', { value: 'hidden', configurable: true });
      document.dispatchEvent(new Event('visibilitychange'));
    });
    await page.clock.runFor(3100);
    expect(summaryFetches).toBe(afterFirstRound);
    await page.evaluate(() => {
      Object.defineProperty(document, 'visibilityState', { value: 'visible', configurable: true });
      document.dispatchEvent(new Event('visibilitychange'));
    });
    await page.clock.runFor(3000);
    await expect.poll(() => summaryFetches).toBeGreaterThan(afterFirstRound);
  });

  test('health-timeline polling | removes visibilitychange listener once all entries are resolved', async ({
    page
  }) => {
    await page.clock.install();
    let summaryFetches = 0;
    await page.route('**/visits/*/summary', async (route) => {
      summaryFetches++;
      await route.fulfill({
        contentType: 'application/json',
        body: JSON.stringify({
          status: 'DONE',
          summary: 'S',
          tags: ['t'],
          urgency: 'routine'
        })
      });
    });
    await page.route('**/fixture', serveFixture);
    await page.goto('/fixture');
    await page.clock.runFor(3000);
    await expect.poll(() => summaryFetches).toBe(3);
    const afterResolve = summaryFetches;
    await page.evaluate(() => {
      Object.defineProperty(document, 'visibilityState', { value: 'hidden', configurable: true });
      document.dispatchEvent(new Event('visibilitychange'));
    });
    await page.evaluate(() => {
      Object.defineProperty(document, 'visibilityState', { value: 'visible', configurable: true });
      document.dispatchEvent(new Event('visibilitychange'));
    });
    await page.clock.runFor(3000);
    expect(summaryFetches).toBe(afterResolve);
  });
});
