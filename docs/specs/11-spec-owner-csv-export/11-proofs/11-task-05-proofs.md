# Proofs: Task 05 — Validate and capture proof artifacts

Covers: all

## Planned evidence

- `./mvnw test jacoco:report` output: all tests pass, `OwnerController` line coverage ≥ 90%.
- Raw `curl -i http://localhost:8080/owners.csv` output showing:
  - `HTTP/1.1 200`
  - `Content-Type: text/csv`
  - `Content-Disposition: attachment; filename="owners.csv"`
  - `First Name,Last Name,Address,City,Telephone` header row
  - At least one data row
- Raw `curl -i "http://localhost:8080/owners.csv?lastName=Franklin"` output showing
  filtered results.
- Raw `curl -i "http://localhost:8080/owners.csv?lastName=NOMATCH999"` output showing
  HTTP 200 with header row only.

## Completion notes

### Verification block

#### `./mvnw compile`

```text
[INFO] BUILD SUCCESS
[INFO] Total time:  2.621 s
```

#### `./mvnw test jacoco:report`

```text
[WARNING] Tests run: 96, Failures: 0, Errors: 0, Skipped: 5
[INFO] --- jacoco:0.8.14:report (default-cli) @ spring-petclinic ---
[INFO] Loading execution data file .../target/jacoco.exec
[INFO] BUILD SUCCESS
```

#### `OwnerController` coverage (from `target/site/jacoco/jacoco.csv`)

```text
LINE:   83/83 = 100.0%
BRANCH: 24/24 = 100.0%
METHOD: 15/16 = 93.8%
INSTR:  347/353 = 98.3%
```

All thresholds exceeded (spec requires ≥90% line coverage).

#### `grep -n "owners.csv" src/main/java/.../OwnerController.java`

```text
154:    @GetMapping(value = "/owners.csv", produces = "text/csv")
174:        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"owners.csv\"");
```

Handler is registered at the expected path.

#### `grep -c "findBySearchCriteria" src/main/java/.../OwnerRepository.java`

```text
1
```

No new repository method added; `Pageable.unpaged()` used as designed.

### AC-6.a: Proof doc shows `Content-Type: text/csv` and `Content-Disposition` headers

#### `curl -i "http://localhost:8080/owners.csv"`

```text
HTTP/1.1 200
Content-Disposition: attachment; filename="owners.csv"
Content-Type: text/csv;charset=UTF-8
Content-Length: 560
Date: Thu, 07 May 2026 02:57:35 GMT

First Name,Last Name,Address,City,Telephone
George,Franklin,110 W. Liberty St.,Madison,6085551023
Betty,Davis,638 Cardinal Ave.,Sun Prairie,6085551749
Eduardo,Rodriquez,2693 Commerce St.,McFarland,6085558763
Harold,Davis,563 Friendly St.,Windsor,6085553198
Peter,McTavish,2387 S. Fair Way,Madison,6085552765
Jean,Coleman,105 N. Lake St.,Monona,6085552654
Jeff,Black,1450 Oak Blvd.,Monona,6085555387
Maria,Escobito,345 Maple St.,Madison,6085557683
David,Schroeder,2749 Blackhawk Trail,Madison,6085559435
Carlos,Estaban,2335 Independence La.,Waunakee,6085555487
```

Both `Content-Type: text/csv` and `Content-Disposition: attachment; filename="owners.csv"` present.

### AC-6.b: Proof doc shows CSV header line and at least one data row

Output above shows the header row `First Name,Last Name,Address,City,Telephone` followed
by 10 data rows. Criteria satisfied.

### Filtering proof

#### `curl -i "http://localhost:8080/owners.csv?lastName=Franklin"`

```text
HTTP/1.1 200
Content-Disposition: attachment; filename="owners.csv"
Content-Type: text/csv;charset=UTF-8
Content-Length: 98
Date: Thu, 07 May 2026 02:57:40 GMT

First Name,Last Name,Address,City,Telephone
George,Franklin,110 W. Liberty St.,Madison,6085551023
```

Correct — only Franklin returned.

#### `curl -i "http://localhost:8080/owners.csv?lastName=NOMATCH999"`

```text
HTTP/1.1 200
Content-Disposition: attachment; filename="owners.csv"
Content-Type: text/csv;charset=UTF-8
Content-Length: 44
Date: Thu, 07 May 2026 02:57:45 GMT

First Name,Last Name,Address,City,Telephone
```

HTTP 200, header row only, no data rows — AC-5.a satisfied.

### Coverage matrix

| AC ID | Status |
|-------|--------|
| AC-1.a | PASS |
| AC-1.b | PASS |
| AC-1.c | PASS |
| AC-2.a | PASS |
| AC-2.b | PASS |
| AC-3.a | PASS |
| AC-3.b | PASS |
| AC-3.c | PASS |
| AC-3.d | PASS |
| AC-4.a | PASS |
| AC-5.a | PASS |
| AC-6.a | PASS |
| AC-6.b | PASS |
