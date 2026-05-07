# Proofs: Task 01 — Write failing controller tests for filter model attributes and hrefs

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b

## Planned evidence

- `./mvnw test -Dtest=OwnerControllerTests` output showing the new test methods FAILING (RED phase confirmed)
- After Task 02 + 03: same command showing all tests PASSING

## Completion notes

### RED phase — new tests failing before implementation

Five new test methods were added to `OwnerControllerTests.java`:

- `testProcessFindFormWithLastNameFilterExposesModelAttributes`
- `testProcessFindFormWithTelephoneFilterExposesModelAttributes`
- `testProcessFindFormWithMultipleFiltersExposesAllModelAttributes`
- `testPaginationLinksIncludeActiveLastNameFilter`
- `testPaginationLinksOmitEmptyFiltersWhenNoFilterActive`

Run before controller / template changes to confirm RED phase:

```text
$ ./mvnw test -Dtest=OwnerControllerTests

[ERROR] Tests run: 25, Failures: 4, Errors: 0, Skipped: 0

[ERROR]   OwnerControllerTests.testProcessFindFormWithLastNameFilterExposesModelAttributes:364
          Model attribute 'filterLastName' expected:<Davis> but was:<null>
[ERROR]   OwnerControllerTests.testProcessFindFormWithMultipleFiltersExposesAllModelAttributes:392
          Model attribute 'filterLastName' expected:<D> but was:<null>
[ERROR]   OwnerControllerTests.testProcessFindFormWithTelephoneFilterExposesModelAttributes:378
          Model attribute 'filterTelephone' expected:<608> but was:<null>
[ERROR]   OwnerControllerTests.testPaginationLinksIncludeActiveLastNameFilter (href check)

[INFO] BUILD FAILURE
```

AC-1 tests fail because `addPaginationModel` does not yet expose filter attributes.
AC-2.a test fails because the old pagination links do not include `lastName=Davis`.
AC-2.b test passes in RED (old links had no filter params at all — a regression guard).

### GREEN phase — all tests pass after Tasks 02 + 03

```text
$ ./mvnw test -Dtest=OwnerControllerTests

[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.967 s
[INFO] BUILD SUCCESS
```

### Notes

New imports added to `OwnerControllerTests.java`:

- `org.springframework.data.domain.PageRequest` (for multi-page stubs)
- `static org.hamcrest.Matchers.containsString`
- `static org.hamcrest.Matchers.nullValue`

The `testPaginationLinksOmitEmptyFiltersWhenNoFilterActive` assertion checks that
`not(containsString("lastName="))` — this is a regression guard that was already
green before implementation (old links had no filter params).
