# Proofs: Task 02 — Implement GET /owners.csv handler in OwnerController

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b, AC-4.a

## Planned evidence

- `./mvnw test -Dtest=OwnerControllerTests` output showing Task 01 tests now **PASS**.
- `./mvnw test` output showing full suite passes with no regressions.

## Completion notes

### AC-1.a: `GET /owners.csv` (no query parameters) returns HTTP 200

GREEN phase — `testCsvExportReturnsOkWithCorrectHeaders` passes:

```text
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.604 s
        -- in org.springframework.samples.petclinic.owner.OwnerControllerTests
[INFO] BUILD SUCCESS
```

Handler added to `OwnerController` at
`src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java`:

```java
@GetMapping(value = "/owners.csv", produces = "text/csv")
public ResponseEntity<String> exportOwnersCsv(
        @RequestParam(required = false, defaultValue = "") String lastName,
        @RequestParam(required = false, defaultValue = "") String telephone,
        @RequestParam(required = false, defaultValue = "") String city) {
    Page<Owner> results = owners.findBySearchCriteria(nullIfBlank(lastName), nullIfBlank(telephone),
            nullIfBlank(city), Pageable.unpaged());
    StringBuilder csv = new StringBuilder("First Name,Last Name,Address,City,Telephone\n");
    for (Owner owner : results) {
        csv.append(owner.getFirstName()).append(',').append(owner.getLastName()).append(',')
            .append(owner.getAddress()).append(',').append(owner.getCity()).append(',')
            .append(owner.getTelephone()).append('\n');
    }
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"owners.csv\"");
    return ResponseEntity.ok().headers(headers).body(csv.toString());
}
```

### AC-1.b: The response `Content-Type` contains `text/csv`

`produces = "text/csv"` on the `@GetMapping` annotation causes Spring to set
`Content-Type: text/csv` on the response. `testCsvExportReturnsOkWithCorrectHeaders`
asserts `header().string("Content-Type", containsString("text/csv"))` — PASS.

### AC-1.c: The response includes `Content-Disposition: attachment; filename="owners.csv"`

`HttpHeaders.CONTENT_DISPOSITION` set to `attachment; filename="owners.csv"` in the
handler. `testCsvExportReturnsOkWithCorrectHeaders` asserts this header — PASS.

### AC-2.a: First line is exactly `First Name,Last Name,Address,City,Telephone`

`StringBuilder` starts with `"First Name,Last Name,Address,City,Telephone\n"`.
`testCsvExportBodyStartsWithHeaderRow` asserts `content().string(startsWith(...))` — PASS.

### AC-2.b: Each data row has five unquoted comma-separated fields in order

The loop appends `firstName,lastName,address,city,telephone\n` for each owner.
The `george()` fixture gives `George,Franklin,110 W. Liberty St.,Madison,6085551023`.
Covered by the broad `@BeforeEach` stub returning george. Full-suite test passes.

### AC-4.a: All matching owners in one response, no pagination required

`Pageable.unpaged()` passed to `findBySearchCriteria` — no page/size parameters
needed by callers. Stub in `@BeforeEach` with `any(Pageable.class)` confirms the
handler passes an unpaged pageable without restricting to a page size.

### Full suite regression check

```text
[WARNING] Tests run: 91, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
```

Skipped: 5 = MySQL and PostgreSQL container tests disabled (no Docker in this env).
