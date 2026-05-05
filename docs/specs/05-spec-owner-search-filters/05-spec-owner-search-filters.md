---
status: delivered
created: 2026-05-05
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Find Owners Multi-Field Search (05)

## Goal

The Find Owners form (`/owners/find`) currently filters by last name only. Users
who know a telephone number or city but not the exact last name must scroll the
full owner list manually. This spec adds optional **telephone** and **city**
inputs to the find form. All non-empty criteria are applied with AND logic, and
all three fields use prefix matching so partial values work. Existing last-name
search behaviour and single-result redirect are preserved.

## Scope

### In scope

- Two new optional form inputs (`id="city"`, `id="telephone"`) added to
  `src/main/resources/templates/owners/findOwners.html` with inline telephone
  validation error display and a global "no owners found" error block
- A new `OwnerRepository.findBySearchCriteria` JPQL `@Query` method accepting
  nullable `lastName`, `telephone`, and `city` parameters with prefix matching,
  AND logic, `SELECT DISTINCT`, and an explicit `countQuery`
- `OwnerController.processFindForm` updated to:
  - Null-normalise all three inputs from the bound `Owner` object
  - Reject non-digit telephone input as a field-level error (code `"invalid"`)
  - Route all searches through `findBySearchCriteria`
  - Emit a **global** form error (code `"notFound"`) when no owners match,
    replacing the existing field error on `lastName`
- Updated `OwnerControllerTests` covering city-only, telephone-only, and
  combined filter scenarios, invalid telephone, and the global no-results error
- New Playwright E2E tests `"can find owner by telephone"` and
  `"can find owner by city"` in `owner-management.spec.ts`
- `searchByFilters({lastName?, telephone?, city?})` helper added to
  `e2e-tests/tests/pages/owner-page.ts`

### Out of scope

- Filtering on the REST `/owners.json` or any API endpoint — that contract must
  not change
- Server-side full-text (contains) search — only prefix (`startsWith`) matching
- Multi-field OR logic
- Address or first-name search
- Persistent / shareable search URLs (browser GET params are naturally shareable,
  but no special URL-state management is added)
- Client-side JavaScript filtering
- i18n message-key additions — no new literal text is introduced into templates

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java`
  lines 89–134 — `initFindForm`, `processFindForm`, and
  `findPaginatedForOwnersLastName` (to be replaced by `findPaginatedForOwners`)
- `src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java`
  — existing `findByLastNameStartingWith(String, Pageable)` to be supplemented
  by `findBySearchCriteria`; both methods remain on the interface
- `src/main/java/org/springframework/samples/petclinic/owner/Owner.java`
  — `city` (`@NotBlank`) and `telephone` (`@NotBlank`, `@Pattern(regexp="\\d{10}")`)
  fields; no `@Valid` is present on `processFindForm`'s `Owner` parameter, so
  these constraints do not fire on the find form
- `src/main/resources/templates/owners/findOwners.html`
  — single-field lastName form receiving two new optional inputs and a global
  error display block
- `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java`
  lines 135–167 — existing find-form tests; mocks updated to stub
  `findBySearchCriteria` and assertions updated for the global no-results error
- `e2e-tests/tests/features/owner-management.spec.ts`
  — existing owner-management E2E suite; new filter tests appended
- `e2e-tests/tests/pages/owner-page.ts`
  — `searchByLastName` helper to be supplemented with `searchByFilters`

## Acceptance criteria

- **AC-1: Form fields present and optional**
  - AC-1.a: `GET /owners/find` returns HTTP 200 and the rendered HTML contains
    an `<input>` element with `id="city"` and an `<input>` element with
    `id="telephone"`.
  - AC-1.b: `GET /owners` (no params — empty form submission) returns HTTP 200
    and the view is `owners/ownersList` — all owners are returned with no
    validation error when all three fields are blank.

- **AC-2: Filtering behavior — prefix AND matching**
  - AC-2.a: `GET /owners?city=Madison` returns HTTP 200, the view is
    `owners/ownersList`, and `listOwners` contains only owners whose city starts
    with `"Madison"` — verified in `OwnerControllerTests` by mocking
    `findBySearchCriteria(null, null, "Madison", pageable)`.
  - AC-2.b: `GET /owners?telephone=6085551` returns HTTP 200, the view is
    `owners/ownersList`, and `listOwners` contains only owners whose telephone
    starts with `"6085551"` — verified in `OwnerControllerTests` by mocking
    `findBySearchCriteria(null, "6085551", null, pageable)`.
  - AC-2.c: `GET /owners?lastName=Davis&city=Sun` returns HTTP 200, the view is
    `owners/ownersList`, and `listOwners` contains Betty Davis (Sun Prairie) but
    does NOT contain Harold Davis (Windsor) — verified by mocking
    `findBySearchCriteria("Davis", null, "Sun", pageable)`.
  - AC-2.d: `GET /owners?lastName=Franklin` (single result) returns HTTP `3xx`
    redirect to `/owners/{id}` — existing behaviour preserved; verified by the
    updated `testProcessFindFormByLastName` test mocking
    `findBySearchCriteria("Franklin", null, null, pageable)`.

- **AC-3: Telephone format validation**
  - AC-3.a: `GET /owners?telephone=608-555` (contains a hyphen) returns HTTP
    200, the `owner` model attribute has a field error on `"telephone"` with code
    `"invalid"`, and the view is `owners/findOwners` — verified in
    `OwnerControllerTests`.
  - AC-3.b: `GET /owners?telephone=608` (digits only, any length) passes
    telephone validation — no field error on `"telephone"` is emitted, and the
    controller proceeds to call the repository — verified in
    `OwnerControllerTests`.

- **AC-4: No-results global error**
  - AC-4.a: When `findBySearchCriteria` returns an empty page, the response is
    HTTP 200, the view is `owners/findOwners`, and the `owner` BindingResult
    contains a **global** error with code `"notFound"` (produced by
    `result.reject(...)`, not `result.rejectValue(...)`) with no field-level
    error on `"lastName"` — verified in `OwnerControllerTests` by asserting the
    presence of a global error on the BindingResult and the absence of a field
    error on `"lastName"`.

- **AC-5: E2E filter tests**
  - AC-5.a: `e2e-tests/tests/features/owner-management.spec.ts` contains a test
    named `"can find owner by telephone"`.
  - AC-5.b: `e2e-tests/tests/features/owner-management.spec.ts` contains a test
    named `"can find owner by city"`.
  - AC-5.c: `cd e2e-tests && npm test -- --grep "Owner Management"` exits 0 with
    all assertions passing.
  - AC-5.d: A Playwright screenshot of a filtered owner list is captured via
    `testInfo.outputPath` in at least one of the new E2E tests as a proof
    artifact.

- **AC-6: No regressions**
  - AC-6.a: `./mvnw test` exits 0 — all existing and new Java tests pass.
  - AC-6.b: `./mvnw test jacoco:report` produces a JaCoCo report showing ≥90%
    line coverage on `OwnerController` and `OwnerRepository`.

## Conventions

- The `Owner` model attribute is reused on the find form — no new search DTO.
  `processFindForm` must NOT carry `@Valid` on its `Owner` parameter.
- Null-normalise all three search inputs before calling the repository: if a
  field is blank or null, pass `null` to `findBySearchCriteria` so that
  `WHERE` clause is excluded (always-true).
- `findBySearchCriteria` must carry `SELECT DISTINCT o` in the main query and an
  explicit `countQuery` attribute on `@Query` to avoid Hibernate pagination
  count-query issues arising from the `DISTINCT`.

  ```java
  @Query(value = "SELECT DISTINCT o FROM Owner o WHERE " +
         "(:lastName IS NULL OR LOWER(o.lastName) LIKE LOWER(CONCAT(:lastName, '%'))) AND " +
         "(:telephone IS NULL OR o.telephone LIKE CONCAT(:telephone, '%')) AND " +
         "(:city IS NULL OR LOWER(o.city) LIKE LOWER(CONCAT(:city, '%')))",
         countQuery = "SELECT COUNT(DISTINCT o) FROM Owner o WHERE " +
         "(:lastName IS NULL OR LOWER(o.lastName) LIKE LOWER(CONCAT(:lastName, '%'))) AND " +
         "(:telephone IS NULL OR o.telephone LIKE CONCAT(:telephone, '%')) AND " +
         "(:city IS NULL OR LOWER(o.city) LIKE LOWER(CONCAT(:city, '%')))")
  Page<Owner> findBySearchCriteria(
      @Param("lastName") String lastName,
      @Param("telephone") String telephone,
      @Param("city") String city,
      Pageable pageable);
  ```

- Telephone validation uses `telephone.matches("\\d+")` — rejects any non-digit
  character; accepts any positive length. The existing `@Pattern(regexp="\\d{10}")`
  on `Owner.telephone` applies only to the create/edit form and must not be
  triggered here.
- The global no-results error uses `result.reject("notFound", "not found")`
  (no field name). The template must render global errors via
  `th:if="${#fields.hasGlobalErrors()}"`, distinct from any field-level error
  display.
- TDD is mandatory: the failing `OwnerControllerTests` commit precedes the
  repository and controller implementation commit; the failing Playwright commit
  precedes the template change commit.
- The `OwnerControllerTests` `@BeforeEach` mock setup must stub
  `findBySearchCriteria` (replacing the existing stubs on
  `findByLastNameStartingWith`) so no existing test breaks once the controller
  routes through the new method. Use `isNull()` Mockito matchers for the
  parameters that are expected to be `null`.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
