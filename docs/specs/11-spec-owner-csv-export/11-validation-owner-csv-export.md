# Validation: Owner CSV Export (11)

## Automated verification

From repository root:

```bash
# 1. Compile — verifies no new syntax or import errors
./mvnw compile

# 2. Full test suite — all existing tests must continue to pass
./mvnw test

# 3. Coverage report — new code must reach ≥90% line coverage
./mvnw test jacoco:report
open target/site/jacoco/index.html   # or check target/site/jacoco/index.html manually

# 4. Structural check — confirm the new handler is registered
grep -n "owners.csv" src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java

# 5. Structural check — no new repository method added (Pageable.unpaged() used instead)
grep -c "findBySearchCriteria" src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java
# Expected: 1 (the existing method; count must not increase)
```

**Expected outcomes:**

| Command | Expected |
|---------|----------|
| `./mvnw compile` | BUILD SUCCESS, 0 errors |
| `./mvnw test` | BUILD SUCCESS, all tests pass |
| `./mvnw test jacoco:report` | ≥90% line coverage on `OwnerController` |
| `grep owners.csv …` | At least one line containing `"/owners.csv"` mapping |
| `grep -c findBySearchCriteria … OwnerRepository.java` | 1 |

## Traceability

- Feature spec: `11-spec-owner-csv-export.md`
- Task breakdown: `11-tasks-owner-csv-export.md`
- Questions and decisions: `11-questions-1-owner-csv-export.md`
- Per-task evidence: `11-proofs/11-task-NN-proofs.md`
- Upstream specs: `05-spec-owner-search-filters` (search criteria pattern)

## Manual checks

- Start the app with `./mvnw spring-boot:run` and run:

  ```bash
  curl -i "http://localhost:8080/owners.csv"
  curl -i "http://localhost:8080/owners.csv?lastName=Franklin"
  curl -i "http://localhost:8080/owners.csv?lastName=NOMATCH999"
  ```

  Verify headers and body match AC-1, AC-2, AC-5 by eye.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `GET /owners.csv` returns HTTP 200 | `11-proofs/11-task-02-proofs.md` | Maven test pass | PASS |
| AC-1.b | `Content-Type` contains `text/csv` | `11-proofs/11-task-02-proofs.md` | Maven test pass | PASS |
| AC-1.c | `Content-Disposition: attachment; filename="owners.csv"` header present | `11-proofs/11-task-02-proofs.md` | Maven test pass | PASS |
| AC-2.a | First line is exactly `First Name,Last Name,Address,City,Telephone` | `11-proofs/11-task-02-proofs.md` | Maven test pass | PASS |
| AC-2.b | Each data row has five unquoted comma-separated fields in order | `11-proofs/11-task-02-proofs.md` | Maven test pass | PASS |
| AC-3.a | `?lastName=X` filters by last name prefix (case-insensitive) | `11-proofs/11-task-04-proofs.md` | Maven test pass | PASS |
| AC-3.b | `?telephone=X` filters by telephone prefix | `11-proofs/11-task-04-proofs.md` | Maven test pass | PASS |
| AC-3.c | `?city=X` filters by city prefix (case-insensitive) | `11-proofs/11-task-04-proofs.md` | Maven test pass | PASS |
| AC-3.d | Combined parameters apply AND logic | `11-proofs/11-task-04-proofs.md` | Maven test pass | PASS |
| AC-4.a | All matching owners in one response, no pagination required | `11-proofs/11-task-02-proofs.md` | Maven test pass | PASS |
| AC-5.a | No-match query returns HTTP 200 with header row only | `11-proofs/11-task-04-proofs.md` | Maven test pass | PASS |
| AC-6.a | Proof doc shows `Content-Type: text/csv` and `Content-Disposition` headers | `11-proofs/11-task-05-proofs.md` | command output | PASS |
| AC-6.b | Proof doc shows CSV header line and at least one data row | `11-proofs/11-task-05-proofs.md` | command output | PASS |

## Definition of done

- [x] AC-1.a: `GET /owners.csv` returns HTTP 200.
- [x] AC-1.b: The response `Content-Type` contains `text/csv`.
- [x] AC-1.c: The response includes `Content-Disposition: attachment; filename="owners.csv"`.
- [x] AC-2.a: First line is exactly `First Name,Last Name,Address,City,Telephone`.
- [x] AC-2.b: Each data row has five unquoted comma-separated fields in the correct order.
- [x] AC-3.a: `?lastName=X` filters by last name prefix (case-insensitive).
- [x] AC-3.b: `?telephone=X` filters by telephone prefix.
- [x] AC-3.c: `?city=X` filters by city prefix (case-insensitive).
- [x] AC-3.d: Combined parameters apply AND logic.
- [x] AC-4.a: All matching owners returned in one response with no pagination parameters.
- [x] AC-5.a: No-match query returns HTTP 200 with header row only.
- [x] AC-6.a: Proof doc contains `curl -i` output showing correct response headers.
- [x] AC-6.b: Proof doc contains curl output showing the CSV header line and ≥1 data row.
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
