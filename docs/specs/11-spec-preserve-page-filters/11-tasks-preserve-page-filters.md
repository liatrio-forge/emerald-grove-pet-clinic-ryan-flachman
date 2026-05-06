---
name: 11-tasks-preserve-page-filters
description: Task breakdown for spec 11 — preserve page filters
type: project
---

# Tasks: Preserve Page Filters Across Pagination (11)

## Parent task plan (awaiting confirmation)

1. Write failing `OwnerControllerTests` for model attributes and pagination hrefs — covers AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b
2. Extend `OwnerController.addPaginationModel` to expose filter params in model — covers AC-1.a, AC-1.b, AC-1.c
3. Update `ownersList.html` pagination links to thread filter params — covers AC-2.a, AC-2.b
4. Write failing Playwright E2E test for filter preservation across pages — covers AC-3.a, AC-3.b
5. Validate all tests pass and capture proof artifacts — covers AC-4.a, AC-5.a

---

> **Confirmation gate**: Do these five parent tasks cover the work as you understand it? Reply with confirmation, edits, or a rescope request before I expand into sub-tasks.

---

## Task 01 — Write failing controller tests for filter model attributes and hrefs

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b

_RED phase — tests must fail before Task 02 is implemented._

- In `OwnerControllerTests`, add a `@BeforeEach` mock setup for a paginated result spanning 2+ pages (mock `owners.findBySearchCriteria` to return a page of `totalPages=2`, `totalElements=7`)
- Add `testProcessFindFormWithLastNameFilterExposesModelAttributes`: GET `/owners?lastName=Davis&page=1`, assert `model().attribute("filterLastName", "Davis")`, `model().attribute("filterTelephone", nullValue())`, `model().attribute("filterCity", nullValue())`
- Add `testProcessFindFormWithTelephoneFilterExposesModelAttributes`: GET `/owners?telephone=608&page=1`, assert `filterTelephone=608`, `filterLastName` is null, `filterCity` is null
- Add `testProcessFindFormWithMultipleFiltersExposesAllModelAttributes`: GET `/owners?lastName=D&telephone=6&city=M&page=1`, assert all three filter model attributes are set
- Add `testPaginationLinksIncludeActiveLastNameFilter`: GET `/owners?lastName=Davis&page=1`, assert `content().string(containsString("page=2&lastName=Davis"))` (or equivalent `xpath`/`content` check)
- Add `testPaginationLinksOmitEmptyFiltersWhenNoFilterActive`: GET `/owners?page=1` (no filter), assert response HTML does NOT contain `lastName=` or `telephone=` or `city=`

**Proof:** 11-proofs/11-task-01-proofs.md

## Task 02 — Extend `OwnerController.addPaginationModel` to pass filter params to model

Covers: AC-1.a, AC-1.b, AC-1.c

_GREEN phase for AC-1. Tests from Task 01 for model attributes must go green. AC-2 tests remain red until Task 03._

- Change `addPaginationModel(int page, Model model, Page<Owner> paginated)` signature to `addPaginationModel(int page, Model model, Page<Owner> paginated, String lastName, String telephone, String city)`
- Inside the method, add:

  ```java
  model.addAttribute("filterLastName", lastName);
  model.addAttribute("filterTelephone", telephone);
  model.addAttribute("filterCity", city);
  ```

- Update the single call site in `processFindForm` to pass the three null-normalized locals: `addPaginationModel(safePage, model, ownersResults, lastName, telephone, city)`
- Run `./mvnw test -Dtest=OwnerControllerTests` — AC-1 tests must now pass; AC-2 tests are still failing

**Proof:** 11-proofs/11-task-02-proofs.md

## Task 03 — Update `ownersList.html` pagination links to thread filter params

Covers: AC-2.a, AC-2.b

_GREEN phase for AC-2. All AC-1 and AC-2 unit tests must pass after this task._

- In `src/main/resources/templates/owners/ownersList.html`, replace all five pagination link `th:href` expressions:
  - **Numbered links** (line 35): `@{/owners(page=${i},lastName=${filterLastName},telephone=${filterTelephone},city=${filterCity})}`
  - **First** (line 40): `@{/owners(page=1,lastName=${filterLastName},telephone=${filterTelephone},city=${filterCity})}`
  - **Previous** (line 44): `@{/owners(page=${currentPage - 1},lastName=${filterLastName},telephone=${filterTelephone},city=${filterCity})}`
  - **Next** (line 49): `@{/owners(page=${currentPage + 1},lastName=${filterLastName},telephone=${filterTelephone},city=${filterCity})}`
  - **Last** (line 54): `@{/owners(page=${totalPages},lastName=${filterLastName},telephone=${filterTelephone},city=${filterCity})}`
- Run `./mvnw test -Dtest=OwnerControllerTests` — all AC-1 and AC-2 tests must pass

**Proof:** 11-proofs/11-task-03-proofs.md

## Task 04 — Write Playwright E2E test for filter preservation across pages

Covers: AC-3.a, AC-3.b

_RED then GREEN: write test first (will fail against unmodified app if running against a fresh checkout), then verify it passes against the fixed app from Tasks 02–03._

- In `e2e-tests/tests/features/owner-management.spec.ts`, add a test block `"preserves lastName filter when navigating to next page"`:
  - Use the existing `searchByFilters` helper (or equivalent page object) to search by `lastName: "F"` from the find form
  - Assert the results page shows multiple owners, and that a "next page" pagination link is visible (may require pre-seeding via `createOwner` fixture for owners whose lastName starts with "F" — check existing fixture count first)
  - Click the next-page pagination link
  - Assert `page.url()` contains `lastName=F`
  - Assert all visible owner name cells start with "F" (use `page.locator('#owners tbody tr td:first-child')`)
- Run `npm test -- --grep "preserves lastName filter"` from `e2e-tests/` and confirm pass

**Proof:** 11-proofs/11-task-04-proofs.md

## Task 05 — Validate all tests pass and capture proof artifacts

Covers: AC-4.a, AC-5.a

- Run `./mvnw test` and confirm exit 0 with all tests passing (including pre-existing `OwnerControllerTests`)
- Run `./mvnw test jacoco:report` and check `target/site/jacoco/index.html` for `OwnerController` line coverage ≥ 90%
- Run `npm test` from `e2e-tests/` and confirm E2E suite passes
- Fill all proof files with real command output
- Update the coverage matrix in `11-validation-preserve-page-filters.md`

**Proof:** 11-proofs/11-task-05-proofs.md
