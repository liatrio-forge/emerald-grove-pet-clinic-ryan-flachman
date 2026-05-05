# Tasks: Language Selector in Global Navbar (03)

## Task 01 — Write failing Playwright E2E test (RED phase)

Covers: AC-4.a, AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b, AC-2.c, AC-3.a, AC-4.b

- Create `e2e-tests/tests/features/language-switching.spec.ts` with a single
  `test` block titled `"language selector switches UI language and persists
  across navigation"`.
- The test body (in order):
  1. `await page.goto('/')` — load home page.
  2. Assert `page.locator('nav.navbar .dropdown-toggle')` is visible — AC-1.a.
  3. Assert dropdown toggle text equals `'EN'` (locale starts as English) — AC-1.c.
  4. `await page.goto('/owners/find')` — navigate to Find Owners page.
  5. Assert `page.locator('nav.navbar .dropdown-toggle')` is visible — AC-1.b.
  6. Return to `/`.
  7. Click `.dropdown-toggle`, then click the dropdown item with text `'Español'`.
  8. Assert `page.locator('nav.navbar').getByRole('link', { name: 'Inicio' })` is
     visible — AC-2.a.
  9. Navigate to `/owners/find`; assert `page.locator('h2')` contains text
     `'Buscar propietarios'` — AC-3.a.
  10. Return to `/`; click `.dropdown-toggle`, click `'Deutsch'`.
  11. Assert `page.locator('nav.navbar').getByRole('link', { name: 'Startseite' })`
      is visible — AC-2.b.
  12. Click `.dropdown-toggle`, click `'English'`.
  13. Assert `page.locator('nav.navbar').getByRole('link', { name: 'Home' })` is
      visible — AC-2.c.
- Run `cd e2e-tests && npm test -- --grep "language selector"` and confirm the
  test **fails** (selector not yet in DOM). Capture the failing output.
- Do not modify `layout.html` in this task.

**Proof:** 03-proofs/03-task-01-proofs.md

---

## Task 02 — Add Bootstrap dropdown to navbar (GREEN phase)

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b, AC-2.c, AC-3.a

- In `src/main/resources/templates/fragments/layout.html`, inside the
  `<ul class="nav navbar-nav ms-auto">` list, append a new `<li>` after the
  Error menu item (currently the last item, ending at line ~66):

  ```html
  <li class="nav-item dropdown">
    <a class="nav-link dropdown-toggle" href="#" role="button"
       data-bs-toggle="dropdown" aria-expanded="false"
       th:text="${#locale.language.toUpperCase()}">EN</a>
    <ul class="dropdown-menu dropdown-menu-end">
      <li><a class="dropdown-item" th:href="@{'?lang=en'}">English</a></li>
      <li><a class="dropdown-item" th:href="@{'?lang=es'}">Español</a></li>
      <li><a class="dropdown-item" th:href="@{'?lang=de'}">Deutsch</a></li>
    </ul>
  </li>
  ```

- Verify no other template files need changes (the layout fragment is the single
  shared navbar).
- Run `cd e2e-tests && npm test -- --grep "language selector"` and confirm all
  assertions **pass** (GREEN). Capture the passing output.

**Proof:** 03-proofs/03-task-02-proofs.md

---

## Task 03 — Validate and capture proof artifacts

Covers: all (AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b, AC-2.c, AC-3.a, AC-4.a, AC-4.b, AC-5.a)

- Run `./mvnw test` from the repo root and capture the full output. Confirm exit
  code 0 and that `I18nPropertiesSyncTest` passes — AC-5.a.
- Run `cd e2e-tests && npm test -- --grep "language selector"` and capture the
  final passing output — AC-4.b.
- Take two screenshots via Playwright (or browser) showing the same page in
  English and Spanish — satisfies the issue proof requirement.
- Verify `e2e-tests/tests/features/language-switching.spec.ts` exists — AC-4.a.
- Fill the coverage matrix in `03-validation-language-selector.md` with `PASS`
  for each AC ID.

**Proof:** 03-proofs/03-task-03-proofs.md
