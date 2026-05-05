---
status: accepted
created: 2026-05-05
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Vet Directory Specialty Filter (04)

## Goal

The vet directory (`/vets.html`) currently shows all veterinarians with no way to
narrow the list by specialty. Users who want to find, for example, all radiologists
must scan the full table manually. This spec adds a specialty filter control — rendered
as Bootstrap badge/pill anchor links — above the vet table. Selecting a specialty
navigates to a shareable URL (`?specialty=radiology`) that shows only matching vets.
An "All" option restores the full list; a "None" option shows vets with no specialty
assigned. Pagination preserves the active filter.

## Scope

### In scope

- A `<div data-testid="specialty-filter">` above the vet table in
  `src/main/resources/templates/vets/vetList.html` containing:
  - An "All" pill (no `specialty` param) — active when no filter is applied
  - One pill per specialty returned by `VetRepository.findAllSpecialties()` — active
    when that specialty's name matches `?specialty=<name>`
  - A "None" pill (`?specialty=none`) — active when `?specialty=none` and shows vets
    with no specialty assigned
- Two new `VetRepository` query methods without `@Cacheable`:
  - `findBySpecialtyName(String name, Pageable pageable)` — `DISTINCT` JPQL query
  - `findWithNoSpecialties(Pageable pageable)` — JPQL `IS EMPTY` query
  - `findAllSpecialties()` — distinct ordered specialty list for populating the pills
- `VetController.showVetList` accepting an optional `@RequestParam String specialty`,
  branching to the appropriate repository call, and adding `selectedSpecialty` and
  `allSpecialties` to the model
- Pagination links updated to carry `&specialty=<value>` through page turns
- Message keys `vets.filter.all` and `vets.filter.none` added to `messages.properties`
  and to all 7 non-English locale files
- Failing VetControllerTests (RED) written before the controller implementation (GREEN)
- Failing Playwright E2E test in `vet-directory.spec.ts` (RED) and updated `VetPage`
  page object written before the view change (GREEN)

### Out of scope

- Filtering on the REST `/vets` JSON endpoint — that contract must not change
- Multi-specialty filter (AND / OR combinations)
- Specialty management (create / edit / delete specialties)
- Server-side full-text search across vet name
- Client-side JavaScript filtering
- Visual regression snapshots / mobile viewport testing
- Translating individual specialty names (they come from the database)

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/vet/VetController.java` lines 44–67
  — current `showVetList` and `findPaginated` methods to be extended
- `src/main/java/org/springframework/samples/petclinic/vet/VetRepository.java` lines 38–58
  — existing `findAll` methods with `@Cacheable("vets")`; new methods must NOT carry this
  annotation (see Q-6 in questions file)
- `src/main/java/org/springframework/samples/petclinic/vet/Vet.java` — `specialties`
  field: `@ManyToMany`, fetch EAGER, join table `vet_specialties`
- `src/main/resources/templates/vets/vetList.html` — current table structure; filter div
  inserted before the `<table>` element; pagination links updated to include `specialty`
- `src/test/java/org/springframework/samples/petclinic/vet/VetControllerTests.java` lines
  54–99 — existing test fixtures (`james()` — no specialty, `helen()` — radiology) and
  mock setup to be extended for new query methods
- `e2e-tests/tests/pages/vet-page.ts` — existing `VetPage` page object to receive
  `specialtyFilterPills()` and `clickSpecialtyFilter(name)` helpers
- `src/test/java/org/springframework/samples/petclinic/system/I18nPropertiesSyncTest.java`
  lines 86–134 — enforces that every key in `messages.properties` exists in all
  non-English locale files; skips `messages_en.properties`

## Acceptance criteria

- **AC-1: Filter control present on page**
  - AC-1.a: `GET /vets.html` returns HTTP 200 and the rendered HTML contains an element
    matching `[data-testid="specialty-filter"]`.
  - AC-1.b: The "All" pill is rendered inside `[data-testid="specialty-filter"]` using
    `th:text="#{vets.filter.all}"` — no hard-coded literal text between tags.
  - AC-1.c: The "None" pill is rendered inside `[data-testid="specialty-filter"]` using
    `th:text="#{vets.filter.none}"` — no hard-coded literal text between tags.
  - AC-1.d: `I18nPropertiesSyncTest.checkNonInternationalizedStrings` passes after the
    template change — `./mvnw test -Dtest=I18nPropertiesSyncTest` exits 0.

- **AC-2: Specialty filtering changes displayed vets**
  - AC-2.a: `GET /vets.html?specialty=radiology` returns HTTP 200, model attribute
    `selectedSpecialty` equals `"radiology"`, and `listVets` contains only vets that
    have the "radiology" specialty (verified in `VetControllerTests` using existing
    `helen()` fixture).
  - AC-2.b: `GET /vets.html?specialty=none` returns HTTP 200, model attribute
    `selectedSpecialty` equals `"none"`, and `listVets` contains only vets with no
    specialties (verified in `VetControllerTests` using existing `james()` fixture).
  - AC-2.c: `GET /vets.html` (no `specialty` param) returns HTTP 200 with both James
    Carter and Helen Leary in `listVets` — existing behaviour unchanged.

- **AC-3: Active filter pill state**
  - AC-3.a: When `?specialty=radiology` is active, the radiology pill's rendered `<a>`
    element contains CSS class `active`.
  - AC-3.b: When `?specialty=none` is active, the "None" pill's `<a>` element contains
    CSS class `active`.
  - AC-3.c: When no `specialty` param is present, the "All" pill's `<a>` element
    contains CSS class `active`.

- **AC-4: Query param URL sharing and pagination preservation**
  - AC-4.a: Pagination links rendered when `?specialty=radiology` is active include
    `specialty=radiology` in each page href so the filter is preserved across pages.
  - AC-4.b: Direct navigation to `GET /vets.html?specialty=radiology` (without first
    visiting the unfiltered page) returns the same filtered results as clicking the pill.

- **AC-5: E2E test**
  - AC-5.a: `e2e-tests/tests/features/vet-directory.spec.ts` contains a test named
    `"can filter vets by specialty using query param"`.
  - AC-5.b: `cd e2e-tests && npm test -- --grep "Vet Directory"` exits 0 with all
    assertions passing.
  - AC-5.c: A Playwright screenshot is captured via `testInfo.outputPath` showing the
    filtered vet list (proof artifact for the issue's "Demo" requirement).

- **AC-6: No regressions**
  - AC-6.a: `./mvnw test` exits 0 — all existing and new Java tests pass, including
    `I18nPropertiesSyncTest`.
  - AC-6.b: Keys `vets.filter.all` and `vets.filter.none` are present in
    `messages.properties` and in each of `messages_de.properties`,
    `messages_es.properties`, `messages_fa.properties`, `messages_ko.properties`,
    `messages_pt.properties`, `messages_ru.properties`, `messages_tr.properties`.
  - AC-6.c: `./mvnw test jacoco:report` produces a JaCoCo report showing ≥90% line
    coverage on `VetController` and `VetRepository`.

## Conventions

- New `VetRepository` methods must NOT carry `@Cacheable("vets")` — see Q-6.
- The `findBySpecialtyName` JPQL query must use `SELECT DISTINCT v` and declare an
  explicit `countQuery` to avoid Hibernate count-query pagination issues.
- Filter pill text must use `#{vets.filter.all}` / `#{vets.filter.none}` message keys;
  individual specialty names use `th:text="${s.name}"` (database value, not a template
  literal).
- Thymeleaf URL expressions omit null parameters: `@{/vets.html(page=${i},specialty=${selectedSpecialty})}`
  will omit `specialty` from the URL when `selectedSpecialty` is null — use this
  behaviour to produce clean "All" pagination links.
- TDD is mandatory: the failing VetControllerTests commit precedes the controller
  implementation commit; the failing Playwright test commit precedes the view change
  commit.
- The existing `VetControllerTests` `setup()` `@BeforeEach` mock must be extended to
  stub `findAllSpecialties()` so existing tests do not break when the controller starts
  calling that method unconditionally.
- E2E page-object pattern: `VetPage` is the correct place for `specialtyFilterPills()`
  and `clickSpecialtyFilter()` helpers (see `e2e-tests/tests/pages/vet-page.ts`).

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
