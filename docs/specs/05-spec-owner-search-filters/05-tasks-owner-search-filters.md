# Tasks: Find Owners Multi-Field Search (05)

## Task 01 — Write failing OwnerControllerTests (RED)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-4.a

- Add the `findBySearchCriteria` method signature (with `@Query` annotation and
  `countQuery` as specified in the spec Conventions) to `OwnerRepository`. The
  `@Query` must be present so the interface compiles and Spring Data does not
  reject the method name at startup in non-mocked test contexts.
- Update `OwnerControllerTests` `@BeforeEach` setup to stub
  `findBySearchCriteria` instead of `findByLastNameStartingWith`. Use
  `isNull()` Mockito matchers for the `null` parameter slots:
  - Franklin single-result stub:
    `findBySearchCriteria(eq("Franklin"), isNull(), isNull(), any(Pageable.class))`
  - Multiple-results stub:
    `findBySearchCriteria(any(), any(), any(), any(Pageable.class))`
- Add `testProcessFindFormByCity` — `GET /owners?city=Madison`; mock
  `findBySearchCriteria(isNull(), isNull(), eq("Madison"), any())` returning a
  page of two owners; assert HTTP 200 and view `owners/ownersList`.
- Add `testProcessFindFormByTelephone` — `GET /owners?telephone=6085551`; mock
  `findBySearchCriteria(isNull(), eq("6085551"), isNull(), any())` returning a
  page of two owners; assert HTTP 200 and view `owners/ownersList`.
- Add `testProcessFindFormByCombinedCriteria` — `GET /owners?lastName=Davis&city=Sun`;
  mock `findBySearchCriteria(eq("Davis"), isNull(), eq("Sun"), any())` returning
  a page containing only Betty Davis; assert HTTP 200 and view `owners/ownersList`.
- Update `testProcessFindFormNoOwnersFound` — replace
  `model().attributeHasFieldErrors("owner", "lastName")` /
  `model().attributeHasFieldErrorCode("owner", "lastName", "notFound")` assertions
  with a custom result handler that inspects the BindingResult and asserts
  `bindingResult.getGlobalErrorCount() == 1` and
  `bindingResult.getFieldErrorCount() == 0`.
- Add `testProcessFindFormInvalidTelephone` — `GET /owners?telephone=608-555`;
  assert HTTP 200, `model().attributeHasFieldErrors("owner", "telephone")`,
  `model().attributeHasFieldErrorCode("owner", "telephone", "invalid")`, and
  view `owners/findOwners`. No repository call should be made.
- Add `testProcessFindFormValidPartialTelephone` — `GET /owners?telephone=608`;
  mock returns a non-empty page; assert HTTP 200, view `owners/ownersList`, and
  NO field error on `"telephone"`.
- Run `./mvnw test -Dtest=OwnerControllerTests` — confirm tests compile and the
  new/updated assertions fail (RED). Record the failure output.

**Proof:** 05-proofs/05-task-01-proofs.md

---

## Task 02 — Write failing Playwright E2E tests (RED)

Covers: AC-5.a, AC-5.b, AC-1.a

- Add `searchByFilters(filters: { lastName?: string; telephone?: string; city?: string })`
  helper to `e2e-tests/tests/pages/owner-page.ts`. The helper fills whichever
  inputs are provided (using `locator('#lastName')`, `locator('#telephone')`,
  `locator('#city')`) and clicks the "Find Owner" button.
- Add test `"can find owner by telephone"` to `owner-management.spec.ts`:
  - Create a unique owner via `createOwner()` and submit the creation form.
  - Navigate to Find Owners.
  - Call `searchByFilters({ telephone: owner.telephone })`.
  - Assert `ownersTable()` is visible and contains the owner's full name.
  - Capture a screenshot via `await page.screenshot({ path: testInfo.outputPath('telephone-search.png') })`.
- Add test `"can find owner by city"` to `owner-management.spec.ts`:
  - Create a unique owner via `createOwner()` and submit the creation form.
  - Navigate to Find Owners.
  - Call `searchByFilters({ city: owner.city })`.
  - Assert `ownersTable()` is visible and contains the owner's full name.
- Run `cd e2e-tests && npm test -- --grep "Owner Management"` — confirm the new
  tests fail because the form lacks `#telephone` and `#city` inputs (RED).
  Record the failure output.

**Proof:** 05-proofs/05-task-02-proofs.md

---

## Task 03 — Implement repository query and update controller (GREEN)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-4.a

- Confirm the `@Query` and `countQuery` on `OwnerRepository.findBySearchCriteria`
  are correct (added in Task 01); adjust if any JPQL syntax issues arose.
- Replace `findPaginatedForOwnersLastName` in `OwnerController` with a new
  private method `findPaginatedForOwners(int page, String lastName, String telephone,
  String city)` that calls `owners.findBySearchCriteria(lastName, telephone, city,
  pageable)`.
- Update `processFindForm`:
  - Null-normalise: `String lastName = nullIfBlank(owner.getLastName())` (and
    similarly for `telephone` and `city`). Add a private `nullIfBlank(String s)`
    helper that returns `null` when the input is null or blank.
  - Validate telephone: if `telephone != null && !telephone.matches("\\d+")`,
    call `result.rejectValue("telephone", "invalid", "Telephone must contain digits only")`
    and return `"owners/findOwners"`.
  - Call `findPaginatedForOwners(page, lastName, telephone, city)`.
  - Replace `result.rejectValue("lastName", "notFound", "not found")` with
    `result.reject("notFound", "not found")`.
- Run `./mvnw test -Dtest=OwnerControllerTests` — all controller tests pass
  (GREEN for Java). Record the passing output.

**Proof:** 05-proofs/05-task-03-proofs.md

---

## Task 04 — Update findOwners.html template (GREEN)

Covers: AC-1.a, AC-1.b, AC-4.a (template rendering)

- Add a global-error display block at the top of the `<form>` in
  `src/main/resources/templates/owners/findOwners.html`:

  ```html
  <div th:if="${#fields.hasGlobalErrors()}" class="alert alert-danger">
    <p th:each="err : ${#fields.globalErrors()}" th:text="${err}">Error</p>
  </div>
  ```

- Tighten the existing `lastName` error block from `#fields.hasAnyErrors()` to
  `#fields.hasErrors('lastName')` so it no longer inadvertently renders errors
  from other fields.
- Add a `<div class="form-group">` block for `city` after the lastName block:

  ```html
  <div class="control-group">
    <label for="city" class="col-sm-2 control-label" th:text="#{city}">City</label>
    <div class="col-sm-10">
      <input class="form-control" th:field="*{city}" id="city" size="30" maxlength="80" />
      <div class="help-inline">
        <div th:if="${#fields.hasErrors('city')}">
          <p th:each="err : ${#fields.errors('city')}" th:text="${err}">Error</p>
        </div>
      </div>
    </div>
  </div>
  ```

- Add a `<div class="form-group">` block for `telephone` after city:

  ```html
  <div class="control-group">
    <label for="telephone" class="col-sm-2 control-label" th:text="#{telephone}">Telephone</label>
    <div class="col-sm-10">
      <input class="form-control" th:field="*{telephone}" id="telephone" size="20" maxlength="20" />
      <div class="help-inline">
        <div th:if="${#fields.hasErrors('telephone')}">
          <p th:each="err : ${#fields.errors('telephone')}" th:text="${err}">Error</p>
        </div>
      </div>
    </div>
  </div>
  ```

- Run `./mvnw test` — all Java tests pass.
- Run `cd e2e-tests && npm test -- --grep "Owner Management"` — all E2E tests
  pass (GREEN). Record passing output.

**Proof:** 05-proofs/05-task-04-proofs.md

---

## Task 05 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture full output confirming `BUILD SUCCESS`.
- Run `./mvnw test jacoco:report`; open `target/site/jacoco/index.html` and
  capture the line-coverage percentages for `OwnerController` and
  `OwnerRepository` (must be ≥90%).
- Run `cd e2e-tests && npm test -- --grep "Owner Management"` and capture
  the passing output.
- Confirm `telephone-search.png` screenshot was created under
  `e2e-tests/test-results/artifacts/` (or the Playwright output path).
- Build the coverage matrix in `05-validation-owner-search-filters.md` — set
  all rows to `PASS` with real evidence references.
- Tick every checkbox in the Definition of Done in the validation file.

**Proof:** 05-proofs/05-task-05-proofs.md
