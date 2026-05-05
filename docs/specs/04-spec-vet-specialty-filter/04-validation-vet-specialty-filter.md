# Validation: Vet Directory Specialty Filter (04)

## Automated verification

From repository root:

```bash
# AC-6.a — full Java test suite including I18nPropertiesSyncTest
./mvnw test

# AC-1.d — i18n sync check in isolation
./mvnw test -Dtest=I18nPropertiesSyncTest

# AC-2.a, AC-2.b, AC-2.c — VetController unit tests
./mvnw test -Dtest=VetControllerTests

# AC-6.c — coverage report (open target/site/jacoco/index.html to verify ≥90% on VetController)
./mvnw test jacoco:report

# AC-6.b — confirm new keys present in base file and all required locale files
grep -l "vets.filter.all" src/main/resources/messages/*.properties

# AC-5.a — E2E spec file exists
test -f e2e-tests/tests/features/vet-directory.spec.ts && echo "EXISTS" || echo "MISSING"

# AC-5.b — E2E test suite passes
cd e2e-tests && npm test -- --grep "Vet Directory"
```

**Expected:**

- `./mvnw test` exits 0; `BUILD SUCCESS`; no `I18nPropertiesSyncTest` failures.
- `./mvnw test -Dtest=VetControllerTests` exits 0; all tests pass including new specialty filter tests.
- `grep -l "vets.filter.all" ...` lists: `messages.properties`, `messages_de.properties`, `messages_es.properties`, `messages_fa.properties`, `messages_ko.properties`, `messages_pt.properties`, `messages_ru.properties`, `messages_tr.properties` (8 files; `messages_en.properties` not listed).
- `test -f ...` prints `EXISTS`.
- `npm test -- --grep "Vet Directory"` exits 0; both `"can browse veterinarian list"` and `"can filter vets by specialty"` pass.

## Traceability

- Feature spec: `04-spec-vet-specialty-filter.md`
- Task breakdown: `04-tasks-vet-specialty-filter.md`
- Questions and decisions: `04-questions-1-vet-specialty-filter.md`
- Per-task evidence: `04-proofs/04-task-NN-proofs.md`
- Upstream specs: none
- Parent epic: none

## Manual checks

1. Start the app: `./mvnw spring-boot:run`
2. Navigate to `http://localhost:8080/vets.html` — confirm filter pills appear above the table.
3. Confirm "All" pill is active (visually distinct) with no specialty param.
4. Click "radiology" — confirm URL becomes `?specialty=radiology`, only radiology vets shown, radiology pill is active.
5. Copy that URL, open a new tab, paste it — confirm same results load directly (shareable URL).
6. Click "None" — confirm only vets with no specialty are shown.
7. Click "All" — confirm full list returns and the `specialty` param is absent from the URL.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `GET /vets.html` HTML contains `[data-testid="specialty-filter"]` | `04-proofs/04-task-04-proofs.md` | command output | PASS |
| AC-1.b | "All" pill uses `th:text="#{vets.filter.all}"` | `04-proofs/04-task-04-proofs.md` | file edit | PASS |
| AC-1.c | "None" pill uses `th:text="#{vets.filter.none}"` | `04-proofs/04-task-04-proofs.md` | file edit | PASS |
| AC-1.d | `I18nPropertiesSyncTest` passes after template change | `04-proofs/04-task-05-proofs.md` | Maven test pass | PASS |
| AC-2.a | `?specialty=radiology` → `selectedSpecialty="radiology"`, `listVets` contains only radiology vets | `04-proofs/04-task-03-proofs.md` | Maven test pass | PASS |
| AC-2.b | `?specialty=none` → `selectedSpecialty="none"`, `listVets` contains only no-specialty vets | `04-proofs/04-task-03-proofs.md` | Maven test pass | PASS |
| AC-2.c | No `specialty` param → all vets returned (existing behaviour) | `04-proofs/04-task-03-proofs.md` | Maven test pass | PASS |
| AC-3.a | Radiology pill has CSS class `active` when `?specialty=radiology` | `04-proofs/04-task-05-proofs.md` | Playwright test pass | PASS |
| AC-3.b | "None" pill has CSS class `active` when `?specialty=none` | `04-proofs/04-task-05-proofs.md` | Playwright test pass | PASS |
| AC-3.c | "All" pill has CSS class `active` when no specialty param | `04-proofs/04-task-05-proofs.md` | Playwright test pass | PASS |
| AC-4.a | Pagination links include `specialty=radiology` when filter is active | `04-proofs/04-task-05-proofs.md` | Playwright test pass | PASS |
| AC-4.b | Direct navigation to `?specialty=radiology` returns same filtered results | `04-proofs/04-task-05-proofs.md` | Playwright test pass | PASS |
| AC-5.a | `e2e-tests/tests/features/vet-directory.spec.ts` contains `"can filter vets by specialty"` test | `04-proofs/04-task-02-proofs.md` | file creation | PASS |
| AC-5.b | `npm test -- --grep "Vet Directory"` exits 0 | `04-proofs/04-task-05-proofs.md` | command output | PASS |
| AC-5.c | Playwright screenshot captured showing filtered vet list | `04-proofs/04-task-05-proofs.md` | Playwright screenshot | PASS |
| AC-6.a | `./mvnw test` exits 0 (all tests including I18nPropertiesSyncTest) | `04-proofs/04-task-05-proofs.md` | Maven test pass | PASS |
| AC-6.b | `vets.filter.all` and `vets.filter.none` present in all 8 required property files | `04-proofs/04-task-04-proofs.md` | file edit | PASS |
| AC-6.c | JaCoCo shows ≥90% line coverage on VetController and VetRepository | `04-proofs/04-task-05-proofs.md` | JaCoCo coverage report | PASS |

## Definition of done

- [x] AC-1.a: `GET /vets.html` HTML contains `[data-testid="specialty-filter"]`
- [x] AC-1.b: "All" pill uses `th:text="#{vets.filter.all}"` — no hard-coded literal
- [x] AC-1.c: "None" pill uses `th:text="#{vets.filter.none}"` — no hard-coded literal
- [x] AC-1.d: `./mvnw test -Dtest=I18nPropertiesSyncTest` exits 0
- [x] AC-2.a: `?specialty=radiology` → correct `selectedSpecialty` + filtered `listVets` in unit test
- [x] AC-2.b: `?specialty=none` → correct `selectedSpecialty` + no-specialty `listVets` in unit test
- [x] AC-2.c: No `specialty` param → all vets returned (existing test still passes)
- [x] AC-3.a: Radiology pill has CSS class `active` when `?specialty=radiology` (E2E)
- [x] AC-3.b: "None" pill has CSS class `active` when `?specialty=none` (E2E)
- [x] AC-3.c: "All" pill has CSS class `active` when no specialty param (E2E)
- [x] AC-4.a: Pagination links carry `specialty` param when filter is active (E2E)
- [x] AC-4.b: Direct navigation to `?specialty=radiology` shows same filtered results (E2E)
- [x] AC-5.a: `e2e-tests/tests/features/vet-directory.spec.ts` contains the filter test
- [x] AC-5.b: `cd e2e-tests && npm test -- --grep "Vet Directory"` exits 0
- [x] AC-5.c: Playwright screenshot captured showing filtered vet list
- [x] AC-6.a: `./mvnw test` exits 0
- [x] AC-6.b: `vets.filter.all` and `vets.filter.none` in all 8 required property files
- [x] AC-6.c: JaCoCo ≥90% line coverage on VetController and VetRepository
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
