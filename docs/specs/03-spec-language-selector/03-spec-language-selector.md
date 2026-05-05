---
status: accepted
created: 2026-05-05
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Language Selector in Global Navbar (03)

## Goal

Users currently have no way to switch the application's UI language without
manually appending `?lang=xx` to a URL. The full i18n infrastructure already
exists — `SessionLocaleResolver`, `LocaleChangeInterceptor`, and complete
message files for English, Spanish, and German (plus six more). This spec
adds a Bootstrap dropdown in the global navbar so users can switch between
English, Español, and Deutsch through a visible UI control. No backend changes
are required.

## Scope

### In scope

- A Bootstrap 5 dropdown `<li>` added to the right side of the navbar in
  `src/main/resources/templates/fragments/layout.html`
- The dropdown toggle displays the current locale's language code in
  uppercase (e.g. `EN`, `ES`, `DE`) using `${#locale.language.toUpperCase()}`
- Three dropdown items: English (`?lang=en`), Español (`?lang=es`), Deutsch (`?lang=de`)
- A Playwright E2E test at
  `e2e-tests/tests/features/language-switching.spec.ts` that:
  - Verifies the selector is visible in the navbar
  - Switches to Spanish and asserts translated nav text ("Inicio")
  - Navigates to a second page and asserts the Spanish locale persists
  - Switches to German and asserts translated nav text ("Startseite")
  - Switches back to English and asserts English nav text ("Home")

### Out of scope

- Adding more languages beyond EN / ES / DE to the selector UI
- Cookie-based locale persistence (session storage is sufficient per requirements)
- Any changes to `WebConfiguration.java`, message property files, or Spring Boot configuration
- A new `navbar.languageSelector.label` accessibility message key (can be added via amendment if needed)
- Visual regression snapshots
- Mobile viewport testing

## Source excerpts

- `src/main/resources/templates/fragments/layout.html` lines 23–71 — current
  navbar structure; the language dropdown is inserted into the
  `<ul class="nav navbar-nav ms-auto">` list (after line 66)
- `src/main/java/org/springframework/samples/petclinic/system/WebConfiguration.java`
  lines 32–58 — existing `SessionLocaleResolver` and `LocaleChangeInterceptor`
  (no changes)
- `src/main/resources/messages/messages_es.properties` — `home=Inicio`,
  `findOwners=Buscar propietarios`
- `src/main/resources/messages/messages_de.properties` — `home=Startseite`,
  `findOwners=Besitzer suchen`

## Acceptance criteria

- **AC-1: Selector visibility**
  - AC-1.a: The language selector dropdown is visible in `nav.navbar` when the
    home page (`/`) is loaded.
  - AC-1.b: The language selector dropdown is visible in `nav.navbar` when the
    Find Owners page (`/owners/find`) is loaded, confirming presence on all pages.
  - AC-1.c: The dropdown toggle text matches the active locale's language code
    uppercased (e.g. shows `EN` when locale is English).

- **AC-2: Language switching updates UI text**
  - AC-2.a: After selecting "Español" from the dropdown, the "Home" nav link
    text changes to "Inicio".
  - AC-2.b: After selecting "Deutsch" from the dropdown, the "Home" nav link
    text changes to "Startseite".
  - AC-2.c: After selecting "English" from the dropdown, the "Home" nav link
    text changes back to "Home".

- **AC-3: Session persistence across navigation**
  - AC-3.a: After switching to Spanish on the home page (`/`) and then
    navigating to `/owners/find`, the page heading contains
    "Buscar propietarios" (Spanish locale is retained in the session).

- **AC-4: E2E test**
  - AC-4.a: `e2e-tests/tests/features/language-switching.spec.ts` exists.
  - AC-4.b: `cd e2e-tests && npm test -- --grep "language selector"` exits 0
    with all assertions passing.

- **AC-5: No regressions**
  - AC-5.a: `./mvnw test` exits 0, including `I18nPropertiesSyncTest`
    (no new message keys added, so the sync check must continue to pass).

## Conventions

- The navbar template is `src/main/resources/templates/fragments/layout.html`;
  this is the only template file that needs modification.
- Bootstrap 5 is already included via WebJars; `bootstrap.bundle.min.js`
  (which bundles Popper) is already loaded in the layout footer — the dropdown
  requires no additional JS.
- Thymeleaf expression `${#locale.language.toUpperCase()}` reads the current
  session locale; this is the correct way to display the active language.
- Thymeleaf URL expression `@{'?lang=en'}` produces a relative query string
  that the `LocaleChangeInterceptor` intercepts — no absolute URLs needed.
- E2E tests follow the page-object pattern established in
  `e2e-tests/tests/pages/base-page.ts`; the language-switching test may use
  `page` directly or add a helper to `BasePage` if the locator is reused.
- TDD is mandatory: the Playwright test must be written and confirmed failing
  before the HTML change is made.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
