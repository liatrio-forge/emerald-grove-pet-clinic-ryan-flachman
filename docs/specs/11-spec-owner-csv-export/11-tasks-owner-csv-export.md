# Tasks: Owner CSV Export (11)

## Task 01 — Write failing @WebMvcTest for GET /owners.csv base behaviour

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a

- In `OwnerControllerTests`, add a `@BeforeEach` stub (or reuse the existing one) that
  makes `owners.findBySearchCriteria(null, null, null, any(Pageable.class))` return a
  `PageImpl` containing the existing `george()` test fixture.
- Add test `testCsvExportReturnsOkWithCorrectHeaders`:
  - `GET /owners.csv`
  - Assert HTTP 200
  - Assert `Content-Type` contains `text/csv`
  - Assert `Content-Disposition` header equals `attachment; filename="owners.csv"`
- Add test `testCsvExportBodyStartsWithHeaderRow`:
  - `GET /owners.csv`
  - Assert response body starts with (or contains as first line)
    `First Name,Last Name,Address,City,Telephone`
- Run `./mvnw test -Dtest=OwnerControllerTests` and confirm both new tests **FAIL** (RED).

**Proof:** 11-proofs/11-task-01-proofs.md

## Task 02 — Implement GET /owners.csv handler in OwnerController

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b, AC-4.a

**May break compile until this task is complete; fixed within this task.**

- Add a new handler method to `OwnerController`:

  ```java
  @GetMapping(value = "/owners.csv", produces = "text/csv")
  public ResponseEntity<String> exportOwnersCsv(/* params added in Task 04 */) { … }
  ```

- Call `owners.findBySearchCriteria(null, null, null, Pageable.unpaged())` to fetch all
  owners (no filters yet).
- Build the CSV body using `StringBuilder`:
  - First line: `First Name,Last Name,Address,City,Telephone\n`
  - One line per owner: `firstName,lastName,address,city,telephone\n`
- Return `ResponseEntity.ok()` with:
  - `Content-Type: text/csv`
  - `Content-Disposition: attachment; filename="owners.csv"`
  - CSV body as the response body string
- Run `./mvnw test -Dtest=OwnerControllerTests` and confirm Task 01 tests **PASS** (GREEN).
- Run `./mvnw test` and confirm the full suite still passes.

**Proof:** 11-proofs/11-task-02-proofs.md

## Task 03 — Write failing @WebMvcTest for search filtering and empty result

Covers: AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-5.a

- Add test `testCsvFilterByLastName`:
  - `GET /owners.csv?lastName=Franklin`
  - Verify `owners.findBySearchCriteria(eq("Franklin"), isNull(), isNull(), any())`
    is called (Mockito `verify`).
  - Assert response body contains `Franklin` data row.
- Add test `testCsvFilterByTelephone`:
  - `GET /owners.csv?telephone=608`
  - Verify `owners.findBySearchCriteria(isNull(), eq("608"), isNull(), any())` is called.
- Add test `testCsvFilterByCity`:
  - `GET /owners.csv?city=Madison`
  - Verify `owners.findBySearchCriteria(isNull(), isNull(), eq("Madison"), any())` is
    called.
- Add test `testCsvFilterCombined`:
  - `GET /owners.csv?lastName=Franklin&city=Madison`
  - Verify `owners.findBySearchCriteria(eq("Franklin"), isNull(), eq("Madison"), any())`
    is called.
- Add test `testCsvEmptyResultReturnsHeaderOnly`:
  - Stub `findBySearchCriteria` to return an empty `PageImpl`.
  - `GET /owners.csv?lastName=NOMATCH`
  - Assert HTTP 200.
  - Assert body equals `First Name,Last Name,Address,City,Telephone\n` (header only).
- Run `./mvnw test -Dtest=OwnerControllerTests` and confirm all new tests **FAIL** (RED).

**Proof:** 11-proofs/11-task-03-proofs.md

## Task 04 — Wire search criteria to CSV handler

Covers: AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-5.a

- Update the `exportOwnersCsv` method signature to accept `@RequestParam` values:

  ```java
  @RequestParam(required = false, defaultValue = "") String lastName,
  @RequestParam(required = false, defaultValue = "") String telephone,
  @RequestParam(required = false, defaultValue = "") String city
  ```

- Apply `nullIfBlank` to each parameter (reuse the existing private helper).
- Pass the sanitised values to `owners.findBySearchCriteria(lastName, telephone, city,
  Pageable.unpaged())`.
- Run `./mvnw test -Dtest=OwnerControllerTests` and confirm all Task 03 tests **PASS**
  (GREEN).
- Run `./mvnw test` and confirm the full suite still passes.

**Proof:** 11-proofs/11-task-04-proofs.md

## Task 05 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test jacoco:report` and confirm:
  - All tests pass (0 failures).
  - `OwnerController` line coverage ≥ 90%.
- Start the application: `./mvnw spring-boot:run`
- Run and capture the following `curl` commands:

  ```bash
  curl -i "http://localhost:8080/owners.csv"
  curl -i "http://localhost:8080/owners.csv?lastName=Franklin"
  curl -i "http://localhost:8080/owners.csv?lastName=NOMATCH999"
  ```

- Paste the raw output (headers + body) into `11-proofs/11-task-05-proofs.md`.
- Confirm each AC-6 criterion is satisfied by the captured output.
- Update the coverage matrix in `11-validation-owner-csv-export.md` — set each row to
  `PASS` once verified.
- Tick all Definition-of-Done checkboxes in `11-validation-owner-csv-export.md`.

**Proof:** 11-proofs/11-task-05-proofs.md
