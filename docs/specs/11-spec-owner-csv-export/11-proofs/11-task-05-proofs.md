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

(Filled in by `implement-sdd-spec`.)
