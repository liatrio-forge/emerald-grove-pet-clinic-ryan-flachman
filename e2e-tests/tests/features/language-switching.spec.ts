import { test, expect } from '@fixtures/base-test';

test('language selector switches UI language and persists across navigation', async ({ page }) => {
  // AC-1.a: selector visible on home page
  await page.goto('/');
  const toggle = page.locator('nav.navbar .dropdown-toggle');
  await expect(toggle).toBeVisible();

  // AC-1.c: toggle shows current locale uppercased
  await expect(toggle).toHaveText('EN');

  // AC-1.b: selector visible on Find Owners page
  await page.goto('/owners/find');
  await expect(page.locator('nav.navbar .dropdown-toggle')).toBeVisible();

  // AC-2.a: switching to Spanish changes nav text to "Inicio"
  await page.goto('/');
  await page.locator('nav.navbar .dropdown-toggle').click();
  await page.locator('nav.navbar .dropdown-item', { hasText: 'Español' }).click();
  await expect(page.locator('nav.navbar').getByRole('link', { name: 'Inicio' })).toBeVisible();

  // AC-3.a: Spanish locale persists after navigation
  await page.goto('/owners/find');
  await expect(page.locator('h2')).toContainText('Buscar propietarios');

  // AC-2.b: switching to German changes nav text to "Startseite"
  await page.goto('/');
  await page.locator('nav.navbar .dropdown-toggle').click();
  await page.locator('nav.navbar .dropdown-item', { hasText: 'Deutsch' }).click();
  await expect(page.locator('nav.navbar').getByRole('link', { name: 'Startseite' })).toBeVisible();

  // AC-2.c: switching back to English restores "Home"
  await page.locator('nav.navbar .dropdown-toggle').click();
  await page.locator('nav.navbar .dropdown-item', { hasText: 'English' }).click();
  await expect(page.locator('nav.navbar').getByRole('link', { name: 'Home' })).toBeVisible();
});
