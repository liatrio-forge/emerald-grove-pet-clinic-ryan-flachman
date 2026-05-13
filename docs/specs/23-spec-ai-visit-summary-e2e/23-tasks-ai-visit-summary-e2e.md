# Tasks: AI Visit Summary E2E (23)

## Task 01 — Write `e2e-tests/tests/features/ai-visit-summary.spec.ts`

Covers: AC-1.a, AC-2.a, AC-2.b, AC-3.a, AC-3.b, AC-3.c, AC-3.d

- Create `e2e-tests/tests/features/ai-visit-summary.spec.ts` with the content
  below. All production code is already delivered (specs 12–22); this task is
  pure test authoring. The file should compile and the test should be green on
  first run (no RED/GREEN split applies to pure E2E test authoring).

  ```typescript
  import { test, expect } from '@fixtures/base-test';
  import { VisitPage } from '@pages/visit-page';

  const formatLocalDate = (d: Date): string => {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  test.describe('AI Visit Summary', () => {
    test('shows DONE urgency badge and tag chips after visit save', async ({ page }) => {
      const visitPage = new VisitPage(page);

      // 1. Navigate to Jean Coleman (owner 6, one pet: Samantha id=7)
      await page.goto('/owners/6');
      await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

      // 2. Open the Add Visit form
      await page.getByRole('link', { name: /^Add Visit$/i }).first().click();
      await expect(visitPage.heading()).toBeVisible();

      // 3. Fill date (1 year from today — satisfies spec-10 future-date constraint)
      //    and a description that the stub maps to URGENT ("limp" keyword)
      const futureDate = new Date();
      futureDate.setFullYear(futureDate.getFullYear() + 1);
      const visitDate = formatLocalDate(futureDate);

      await visitPage.fillVisitDate(visitDate);
      await visitPage.fillDescription('Dog is limping on left front leg');
      await visitPage.submit();

      // 4. Assert redirect lands on owner detail page
      await expect(page).toHaveURL(/\/owners\/6$/);
      await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

      // 5. Expand the health timeline (Bootstrap collapse, initially hidden)
      await page.getByRole('button', { name: /Health Timeline/i }).click();

      // 6. Locate the new visit entry by its data-visit-date attribute.
      //    The JS polling started on page load (elements are in the DOM even
      //    when the collapse is closed). Wait up to 10 s for DONE.
      const visitEntry = page.locator(`[data-visit-date="${visitDate}"]`);
      await expect(visitEntry).toHaveAttribute('data-ai-status', 'DONE', { timeout: 10_000 });

      // 7. Assert rendered summary content
      await expect(visitEntry.locator('.urgency-urgent')).toBeVisible();
      await expect(visitEntry.locator('.health-tag').first()).toBeVisible();
      await expect(visitEntry.locator('span.ai-spinner')).toHaveCount(0);
    });
  });
  ```

- After creating the file, run TypeScript compile check:

  ```bash
  cd e2e-tests && npx tsc --noEmit
  ```

  Expected: exits 0 with no errors.

- Record the `ls` output and compile result in the proof file.

**Proof:** `23-proofs/23-task-01-proofs.md`

---

## Task 02 — Run the E2E suite scoped to the new spec and capture proof

Covers: AC-4.a

- Start the Spring Boot application if it is not already running:

  ```bash
  ./mvnw spring-boot:run &
  # Wait for startup — or let Playwright's webServer config handle it
  ```

  (Playwright's `webServer` config in `playwright.config.ts` starts the app
  automatically and reuses an existing server if one is already listening on
  port 8080.)

- Run only the AI Visit Summary test:

  ```bash
  cd e2e-tests && npm test -- --grep "AI Visit Summary"
  ```

- Expected output (exact counts may vary in header lines, but the result line
  must show):

  ```text
  1 passed
  ```

  and the process must exit 0.

- Capture the full terminal output in the proof file.

- If the test fails, investigate before re-running:
  - **Spinner still present after 10 s**: check that `VisitSummaryService` is
    wired into `VisitController` (spec 21) and that no `ANTHROPIC_API_KEY` env
    var is set (stub activation depends on the key being absent).
  - **`urgency-urgent` not found**: confirm the description contains `"limp"` —
    the stub routes that keyword to `URGENT` (`ClaudeApiClientStub`).
  - **`[data-visit-date]` not found**: Bootstrap collapse animation may still be
    running; increase the `toHaveAttribute` timeout or add an explicit
    `waitFor` on the collapse element.

**Proof:** `23-proofs/23-task-02-proofs.md`
