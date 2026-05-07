# Proofs: Task 01 — Write failing @WebMvcTest for GET /owners.csv base behaviour

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a

## Planned evidence

- `./mvnw test -Dtest=OwnerControllerTests` output showing the two new test methods
  failing with `404 Not Found` (or `405`) because the handler does not yet exist.

## Completion notes

### AC-1.a: `GET /owners.csv` (no query parameters) returns HTTP 200

RED phase — test `testCsvExportReturnsOkWithHeaderRow` fails with 404 before the handler
exists. Output:

```text
[ERROR] Tests run: 22, Failures: 2, Errors: 0, Skipped: 0
[ERROR] org.springframework.samples.petclinic.owner.OwnerControllerTests.testCsvExportReturnsOkWithCorrectHeaders
        -- Time elapsed: 0.003 s <<< FAILURE!
java.lang.AssertionError: Status expected:<200> but was:<404>
        at ...OwnerControllerTests.testCsvExportReturnsOkWithCorrectHeaders(OwnerControllerTests.java:128)
```

### AC-1.b: The response `Content-Type` contains `text/csv`

Covered by `testCsvExportReturnsOkWithCorrectHeaders` — asserts
`header().string("Content-Type", containsString("text/csv"))`. Fails at status check
(404) in RED phase, so the header assertion has not been reached yet.

### AC-1.c: The response includes `Content-Disposition: attachment; filename="owners.csv"`

Covered by `testCsvExportReturnsOkWithCorrectHeaders` — asserts
`header().string("Content-Disposition", "attachment; filename=\"owners.csv\"")`.
Fails at status check (404) in RED phase.

### AC-2.a: First line of the response body is exactly `First Name,Last Name,Address,City,Telephone`

RED phase — test `testCsvExportBodyStartsWithHeaderRow` fails with 404:

```text
[ERROR] org.springframework.samples.petclinic.owner.OwnerControllerTests.testCsvExportBodyStartsWithHeaderRow
        -- Time elapsed: 0.009 s <<< FAILURE!
java.lang.AssertionError: Status expected:<200> but was:<404>
        at ...OwnerControllerTests.testCsvExportBodyStartsWithHeaderRow(OwnerControllerTests.java:136)
```

### Notes

Both new tests fail for the correct reason: no handler exists for `GET /owners.csv`.
The broad `findBySearchCriteria` stub in `@BeforeEach` is already in place for when
the handler is added in Task 02. Imports for `containsString` and `startsWith` were
added to the existing individual static imports block.
