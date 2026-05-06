---
status: delivered
created: 2026-05-06
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Preserve Page Filters Across Pagination (11)

## Goal

When a user searches the Owners list by last name, telephone, or city, then clicks a pagination link to move to the next page, the filter is silently dropped — the next page shows all owners, not just those matching the filter. This spec fixes that by threading the active filter parameters through every pagination link on the Owners list page. The Vets list is already correct (specialty is preserved) and is out of scope.

## Scope

### In scope

- `OwnerController.addPaginationModel` signature extended to accept `String lastName`, `String telephone`, `String city` (all null-normalized) and add them to the model as `filterLastName`, `filterTelephone`, `filterCity`
- `OwnerController.processFindForm` updated to pass the three null-normalized filter values to the updated `addPaginationModel`
- `ownersList.html` pagination links (first / previous / numbered / next / last) updated to include `filterLastName`, `filterTelephone`, `filterCity` via Thymeleaf `@{...}` URL params — null params are omitted automatically
- `OwnerControllerTests`: new `@WebMvcTest` tests verifying model attributes and generated HTML hrefs under single-filter and multi-filter scenarios
- Playwright E2E: new test in `owner-management.spec.ts` that applies a last-name filter, navigates to page 2, and asserts both the URL and the result set remain filtered

### Out of scope

- Vets list — `vetList.html` already preserves `specialty` across pagination; no changes needed
- Find Owners form (`/owners/find`) — not a list/pagination page
- Client-side state persistence (session, localStorage, URL history API)
- Any change to repository query logic or search behaviour
- Sorting controls

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java` — `processFindForm`, `addPaginationModel`, `findPaginatedForOwners`, `nullIfBlank`
- `src/main/resources/templates/owners/ownersList.html` — pagination fragment (lines 31–58)
- `src/main/resources/templates/vets/vetList.html` — reference implementation showing correct Thymeleaf multi-param pagination pattern (lines 50–83)
- `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java` — existing test class to extend
- `e2e-tests/tests/features/owner-management.spec.ts` — existing E2E file to extend

## Acceptance criteria

- **AC-1: Model attributes**
  - AC-1.a: `GET /owners?lastName=Davis&page=1` adds `filterLastName=Davis`, `filterTelephone=null`, `filterCity=null` to the Spring MVC model
  - AC-1.b: `GET /owners?telephone=608&page=1` adds `filterLastName=null`, `filterTelephone=608`, `filterCity=null` to the model
  - AC-1.c: `GET /owners?lastName=D&telephone=6&city=M&page=1` adds all three filter values to the model

- **AC-2: Pagination link hrefs (unit)**
  - AC-2.a: When `filterLastName=Davis` is in the model, the rendered HTML for the Owners list contains href values matching the pattern `/owners?page=\d+&lastName=Davis` (no telephone or city params present)
  - AC-2.b: When no filters are active (`filterLastName=null`, `filterTelephone=null`, `filterCity=null`), pagination hrefs contain only `page=N` — no empty `lastName=`, `telephone=`, or `city=` params

- **AC-3: End-to-end filter preservation**
  - AC-3.a: Playwright test: navigate to `/owners?lastName=F` (Farrell family — enough records to span >1 page when created via fixtures), click the "next" pagination link, and assert the resulting URL contains `lastName=F`
  - AC-3.b: Playwright test: on the second page with filter active, the owners table contains only rows whose name starts with "F"

- **AC-4: Regression — existing tests pass**
  - AC-4.a: `./mvnw test` exits 0 with all pre-existing `OwnerControllerTests` cases passing

- **AC-5: Coverage**
  - AC-5.a: Line coverage on `OwnerController` and `ownersList.html`-related model paths remains ≥ 90% per JaCoCo report

## Conventions

- Follow the null-param Thymeleaf URL pattern from `vetList.html` (spec-04 delivered implementation); do not use ternary chains for three-param owners case — rely on Thymeleaf's built-in null-omission behaviour
- Model attribute names `filterLastName`, `filterTelephone`, `filterCity` are distinct from the bound `owner` model attribute to prevent shadowing
- All new tests follow Arrange-Act-Assert, use `@WebMvcTest` for unit layer, follow existing mock setup in `OwnerControllerTests`
- Commit sequence: test commit (RED) → implementation commit (GREEN) → optional refactor commit

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
