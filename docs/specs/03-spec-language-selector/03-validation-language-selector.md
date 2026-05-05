# Validation: Language Selector in Global Navbar (03)

## Automated verification

From repository root:

```bash
# AC-5.a — no regressions in Java test suite
./mvnw test

# AC-4.a — spec file exists
test -f e2e-tests/tests/features/language-switching.spec.ts && echo "EXISTS" || echo "MISSING"

# AC-4.b — E2E test passes
cd e2e-tests && npm test -- --grep "language selector"
```

**Expected:**

- `./mvnw test` exits 0; `BUILD SUCCESS` in output; `I18nPropertiesSyncTest` reports no failures.
- `test -f ...` prints `EXISTS`.
- `npm test -- --grep "language selector"` exits 0; `1 passed` in output.

## Traceability

- Feature spec: `03-spec-language-selector.md`
- Task breakdown: `03-tasks-language-selector.md`
- Questions and decisions: `03-questions-1-language-selector.md`
- Per-task evidence: `03-proofs/03-task-0N-proofs.md`
- Upstream specs: none
- Parent epic: none

## Manual checks

- Open the app at `http://localhost:8080`. Confirm the language dropdown is
  visible in the top-right area of the navbar.
- Confirm the dropdown toggle shows `EN` in the default state.
- Select "Español"; confirm all navbar labels change to Spanish text.
- Select "Deutsch"; confirm all navbar labels change to German text.
- Select "English"; confirm labels return to English.
- Navigate between pages while in Spanish mode; confirm locale does not reset.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | Language selector dropdown is visible in `nav.navbar` on `/` | `03-proofs/03-task-02-proofs.md` | Playwright test pass | PASS |
| AC-1.b | Language selector dropdown is visible in `nav.navbar` on `/owners/find` | `03-proofs/03-task-02-proofs.md` | Playwright test pass | PASS |
| AC-1.c | Dropdown toggle text matches active locale code uppercased (`EN`) | `03-proofs/03-task-02-proofs.md` | Playwright test pass | PASS |
| AC-2.a | After selecting "Español", "Home" nav link text changes to "Inicio" | `03-proofs/03-task-02-proofs.md` | Playwright test pass | PASS |
| AC-2.b | After selecting "Deutsch", "Home" nav link text changes to "Startseite" | `03-proofs/03-task-02-proofs.md` | Playwright test pass | PASS |
| AC-2.c | After selecting "English", "Home" nav link text changes back to "Home" | `03-proofs/03-task-02-proofs.md` | Playwright test pass | PASS |
| AC-3.a | After switching to Spanish on `/`, navigating to `/owners/find` shows "Buscar propietarios" | `03-proofs/03-task-02-proofs.md` | Playwright test pass | PASS |
| AC-4.a | `e2e-tests/tests/features/language-switching.spec.ts` exists | `03-proofs/03-task-01-proofs.md` | file creation | PASS |
| AC-4.b | `npm test -- --grep "language selector"` exits 0 | `03-proofs/03-task-03-proofs.md` | command output | PENDING |
| AC-5.a | `./mvnw test` exits 0 including `I18nPropertiesSyncTest` | `03-proofs/03-task-03-proofs.md` | Maven test pass | PENDING |

## Definition of done

- [ ] AC-1.a: Language selector dropdown is visible in `nav.navbar` on `/`
- [ ] AC-1.b: Language selector dropdown is visible in `nav.navbar` on `/owners/find`
- [ ] AC-1.c: Dropdown toggle text matches active locale code uppercased (`EN`)
- [ ] AC-2.a: After selecting "Español", "Home" nav link text changes to "Inicio"
- [ ] AC-2.b: After selecting "Deutsch", "Home" nav link text changes to "Startseite"
- [ ] AC-2.c: After selecting "English", "Home" nav link text changes back to "Home"
- [ ] AC-3.a: After switching to Spanish on `/`, navigating to `/owners/find` shows "Buscar propietarios"
- [ ] AC-4.a: `e2e-tests/tests/features/language-switching.spec.ts` exists
- [ ] AC-4.b: `cd e2e-tests && npm test -- --grep "language selector"` exits 0
- [ ] AC-5.a: `./mvnw test` exits 0 including `I18nPropertiesSyncTest`
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
