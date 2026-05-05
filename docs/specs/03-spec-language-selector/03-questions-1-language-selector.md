# Questions: Language Selector (03) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | Which languages should appear in the selector? | EN, ES, DE only (issue requirement; 9 message files exist but scope is limited to 3) |
| Q-2 | Does the `?lang=xx` mechanism already work server-side? | Yes — `SessionLocaleResolver` + `LocaleChangeInterceptor` registered in `WebConfiguration.java` handle the param on every request |
| Q-3 | Are any backend changes needed? | No — the full i18n pipeline (locale resolution, message lookup, session storage) is already in place |
| Q-4 | How are language labels displayed in the dropdown? | Hardcoded native names: "English", "Español", "Deutsch" — language names do not need translation (it is universally correct to show "Español" regardless of the current interface language) |
| Q-5 | What UI pattern should the selector use? | Bootstrap 5 dropdown, matching existing navbar patterns; toggle button shows the current locale code uppercased via `${#locale.language.toUpperCase()}` |
| Q-6 | Will new message keys be required? | No new keys for the language names. An optional aria-label key `navbar.languageSelector.label` may be added for accessibility; if so, it must be added to all 8 non-empty language files (enforced by `I18nPropertiesSyncTest`) |
| Q-7 | What is the TDD approach for a pure-template change? | The only meaningful test is a Playwright E2E test. Write it first (RED — selector not yet in DOM, test fails), then add the HTML (GREEN). Java unit/integration tests are unaffected since no Java code changes. |
| Q-8 | Where is the global layout template? | `src/main/resources/templates/fragments/layout.html` — the single shared navbar rendered on every page |
| Q-9 | What translated strings can the E2E test assert? | Spanish: `home` → "Inicio", `findOwners` → "Buscar propietarios". German: `home` → "Startseite", `findOwners` → "Besitzer suchen" |

## Open

None.
