# Proofs: Task 02 — Write failing Playwright E2E test and update VetPage page object (RED)

Covers: AC-5.a, AC-5.b, AC-5.c, AC-3.a, AC-3.b, AC-3.c, AC-4.a, AC-4.b

## Planned evidence

- `specialtyFilterPills()` and `clickSpecialtyFilter(name)` helpers added to `e2e-tests/tests/pages/vet-page.ts`.
- `"can filter vets by specialty using query param"` test block added to `e2e-tests/tests/features/vet-directory.spec.ts`.
- Output of `cd e2e-tests && npm test -- --grep "Vet Directory"` showing the new filter test **failing** (RED phase confirmed — filter div not yet in DOM).

## Completion notes

### AC-5.a: `e2e-tests/tests/features/vet-directory.spec.ts` contains a test named `"can filter vets by specialty using query param"`

Test added at line 26 of `vet-directory.spec.ts` inside the existing `test.describe('Vet Directory', ...)` block.

**File edit:**

```diff
+  test('can filter vets by specialty using query param', async ({ page }, testInfo) => {
+    const vetPage = new VetPage(page);
+    await vetPage.open();
+    // AC-1.a: filter control is visible
+    await expect(vetPage.specialtyFilterPills()).toBeVisible();
+    // AC-3.c: "All" pill is active when no filter is applied
+    await expect(vetPage.specialtyFilterPills().locator('a.active')).toContainText(/all/i);
     ...
+  });
```

`VetPage` page object updated with two new helpers:

```diff
+  specialtyFilterPills(): Locator {
+    return this.page.locator('[data-testid="specialty-filter"]');
+  }
+
+  async clickSpecialtyFilter(name: string): Promise<void> {
+    await this.specialtyFilterPills().locator('a', { hasText: name }).click();
+  }
```

TypeScript compile check: `npx tsc --noEmit` exits 0 (no type errors).

### AC-5.b, AC-3.a, AC-3.b, AC-3.c, AC-4.a, AC-4.b, AC-5.c: E2E assertions written but failing (RED)

**RED failure output:**

```text
    Expected: visible
    Timeout: 5000ms
    Error: element(s) not found

    Call log:
      - Expect "toBeVisible" with timeout 5000ms
      - waiting for locator('[data-testid="specialty-filter"]')

      31 |     // AC-1.a: filter control is visible
    > 32 |     await expect(vetPage.specialtyFilterPills()).toBeVisible();

  1 failed
    [chromium] › tests/features/vet-directory.spec.ts:26:3 › Vet Directory › can filter vets by specialty using query param
  1 passed (16.2s)
```

New filter test fails because `[data-testid="specialty-filter"]` does not yet exist in the DOM.
Existing `"can browse veterinarian list and view specialties"` test continues to pass.
RED phase confirmed.
