# Proofs: Task 04 — Update findOwners.html template (GREEN)

Covers: AC-1.a, AC-1.b, AC-4.a (template rendering)

## Planned evidence

- Updated `src/main/resources/templates/owners/findOwners.html` — show the
  complete form section with global-error block, city input, and telephone input.
- Output of `./mvnw test` showing `BUILD SUCCESS` (all Java tests pass).
- Output of `cd e2e-tests && npm test -- --grep "Owner Management"` showing all
  tests **passing** including the new telephone and city filter tests (GREEN).

## Completion notes

### AC-1.a: Form contains `id="city"` and `id="telephone"` inputs

Updated `findOwners.html` — complete form section:

```html
<form th:object="${owner}" th:action="@{/owners}" method="get" class="form-horizontal liatrio-form"
  id="search-owner-form">
  <div th:if="${#fields.hasGlobalErrors()}" class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <div class="help-inline">
        <p th:each="err : ${#fields.globalErrors()}" th:text="${err}">Error</p>
      </div>
    </div>
  </div>
  <div class="form-group">
    <div class="control-group" id="lastNameGroup">
      <label for="lastName" class="col-sm-2 control-label" th:text="#{lastName}">Last name </label>
      <div class="col-sm-10">
        <input class="form-control" th:field="*{lastName}" size="30" maxlength="80" />
      </div>
    </div>
  </div>
  <div class="form-group">
    <div class="control-group">
      <label for="telephone" class="col-sm-2 control-label" th:text="#{telephone}">Telephone</label>
      <div class="col-sm-10">
        <input class="form-control" th:field="*{telephone}" id="telephone" size="20" maxlength="20" />
        <div class="help-inline" th:if="${#fields.hasErrors('telephone')}">
          <p th:each="err : ${#fields.errors('telephone')}" th:text="${err}">Error</p>
        </div>
      </div>
    </div>
  </div>
  <div class="form-group">
    <div class="control-group">
      <label for="city" class="col-sm-2 control-label" th:text="#{city}">City</label>
      <div class="col-sm-10">
        <input class="form-control" th:field="*{city}" id="city" size="30" maxlength="80" />
      </div>
    </div>
  </div>
  ...
</form>
```

### AC-1.b / AC-4.a: `./mvnw test` passes (Java — all 67 tests)

```text
[INFO] Tests run: 67, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

Note: `I18nPropertiesSyncTest` initially failed because the labels used hardcoded
text. Fixed by using `th:text="#{telephone}"` and `th:text="#{city}"` which reference
existing keys in `messages.properties`.

### AC-5.c / AC-1.a: E2E tests pass (GREEN)

`cd e2e-tests && npm test -- --grep "Owner Management"`:

```text
Running 6 tests using 6 workers

  6 passed (8.6s)
```

All 6 Owner Management tests pass including the two new filter tests.

Both `"can find owner by telephone"` and `"can find owner by city"` create 2
owners with a shared unique telephone prefix / unique city, search by that
prefix/city, and assert the owners table is visible with the first owner's name.
