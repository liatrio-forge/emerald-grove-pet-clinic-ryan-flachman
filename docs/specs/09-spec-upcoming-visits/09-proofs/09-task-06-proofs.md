# Proofs: Task 06 — Validate and capture proof artifacts

Covers: all

## Planned evidence

- Output of `./mvnw test` exiting 0 (`BUILD SUCCESS`).
- JaCoCo coverage screenshot or text showing ≥90% line coverage on `UpcomingVisitsController` and `VisitRepository`.
- Output of all structural `grep` checks from the validation file (all returning at least one match).
- Output of `cd e2e-tests && npm test -- --grep "Upcoming Visits"` exiting 0.
- Confirmation that `upcoming-visits-table.png` exists at the Playwright output path.
- Completed coverage matrix in `09-validation-upcoming-visits.md` with all rows `PASS`.
- All DoD checkboxes ticked in `09-validation-upcoming-visits.md`.

## Completion notes

### Verification block

#### `./mvnw test`

```text
[WARNING] Tests run: 80, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
[INFO] Total time:  16.246 s
[INFO] Finished at: 2026-05-06T13:37:46-05:00
```

5 skipped = Docker-dependent MySQL/PostgreSQL container tests.

#### `./mvnw test jacoco:report` — coverage for `UpcomingVisitsController`

```text
$ grep "UpcomingVisitsController" target/site/jacoco/jacoco.csv
petclinic,org.springframework.samples.petclinic.owner,UpcomingVisitsController,0,32,0,0,0,9,0,2,0,2
```

- LINE\_MISSED: 0, LINE\_COVERED: 9 → **100% line coverage** (threshold ≥90% met)
- BRANCH\_MISSED: 0, BRANCH\_COVERED: 0 → no branches; 100%

`VisitRepository` is a Spring Data JPA interface — JaCoCo does not emit a separate entry
for interfaces with no bytecode; the query is validated at context startup and exercised
by `ClinicServiceTests`.

#### Structural checks

```text
=== AC-4.a ===
src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java

=== AC-4.b ===
12:	@Query("""

=== AC-5.a ===
31:             th:text="${visit.ownerFirstName + ' ' + visit.ownerLastName}">

=== AC-5.b ===
28:        <td th:text="${visit.date}">2026-05-10</td>
35:        <td th:text="${visit.petName}">Pet Name</td>
36:        <td th:text="${visit.description}">Description</td>

=== AC-5.c ===
12:  <div th:if="${visits.isEmpty()}" class="alert alert-info" role="alert">
16:  <table th:unless="${visits.isEmpty()}"

=== AC-6.a ===
62:          <li th:replace="~{::menuItem ('/visits/upcoming','visits','upcoming visits','calendar',#{upcomingVisits})}">

=== AC-7.a === (count)
       9
```

#### `npm test -- --grep "Upcoming Visits"` (from `e2e-tests/`)

```text
Running 1 test using 1 worker
  1 passed (9.6s)
```

#### Screenshot artifact

```text
e2e-tests/test-results/artifacts/features-upcoming-visits-U-ea7ff-uled-within-the-next-7-days-chromium/upcoming-visits-table.png
```

File confirmed to exist.

### Coverage matrix

| AC ID | Status |
|-------|--------|
| AC-1.a | PASS |
| AC-2.a | PASS |
| AC-3.a | PASS |
| AC-4.a | PASS |
| AC-4.b | PASS |
| AC-5.a | PASS |
| AC-5.b | PASS |
| AC-5.c | PASS |
| AC-6.a | PASS |
| AC-7.a | PASS |
| AC-8.a | PASS |
| AC-8.b | PASS |
| AC-8.c | PASS |
| AC-9.a | PASS |
| AC-9.b | PASS |

All 15 active ACs in PASS at delivery.
