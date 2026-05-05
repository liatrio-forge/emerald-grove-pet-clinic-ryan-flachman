import type { Locator, Page } from '@playwright/test';

import { BasePage } from './base-page';

export class VetPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  heading(): Locator {
    return this.page.getByRole('heading', { name: /Veterinarians/i });
  }

  vetsTable(): Locator {
    return this.page.locator('table#vets');
  }

  specialtyFilterPills(): Locator {
    return this.page.locator('[data-testid="specialty-filter"]');
  }

  async clickSpecialtyFilter(name: string): Promise<void> {
    await this.specialtyFilterPills().locator('a', { hasText: name }).click();
  }

  async open(): Promise<void> {
    await this.goto('/vets.html');
    await this.heading().waitFor();
  }
}
