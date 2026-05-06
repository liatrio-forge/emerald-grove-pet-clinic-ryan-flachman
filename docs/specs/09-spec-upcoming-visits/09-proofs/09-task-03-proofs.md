# Proofs: Task 03 — Implement UpcomingVisitsController and upcomingVisits.html template (GREEN)

Covers: AC-1.a, AC-2.a, AC-3.a, AC-5.a, AC-5.b, AC-5.c

## Planned evidence

- Output of `./mvnw test -Dtest=UpcomingVisitsControllerTests` exiting 0 with both tests passing (GREEN).
- Output of `./mvnw test` exiting 0 (no regressions).
- Output of `grep -n "ownerFirst\|ownerLast\|firstName\|lastName" src/main/resources/templates/visits/upcomingVisits.html` showing at least one match.
- Output of `grep -n "petName\|\.date\|\.description" src/main/resources/templates/visits/upcomingVisits.html` showing at least one match.
- Output of `grep -n "isEmpty\|th:if.*visit\|th:unless" src/main/resources/templates/visits/upcomingVisits.html` showing at least one match.

## Completion notes

### Notes

The spec's JPQL query (`FROM Pet p JOIN p.visits v JOIN p.owner o`) references `p.owner`,
but `Pet` has no `owner` back-reference field. The actual entity structure has `Owner.pets`
as the owning side (`@OneToMany @JoinColumn(name = "owner_id")`), unidirectional. The
equivalent query starting from `Owner` (`FROM Owner o JOIN o.pets p JOIN p.visits v`)
produces identical results and was used instead. `VisitRepository` was corrected in this
task.

### AC-1.a: `testShowUpcomingVisitsDefaultWindow` asserts HTTP 200 and view name `visits/upcomingVisits`

```text
$ ./mvnw test -Dtest=UpcomingVisitsControllerTests

[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.824 s
    -- in org.springframework.samples.petclinic.owner.UpcomingVisitsControllerTests
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  5.747 s
[INFO] Finished at: 2026-05-06T13:27:06-05:00
```

### AC-2.a: `testShowUpcomingVisitsDefaultWindow` asserts 7-day window via ArgumentCaptor

Same test run as AC-1.a (both tests pass in the same run). Test verifies:

```java
assertThat(ChronoUnit.DAYS.between(startCaptor.getValue(), endCaptor.getValue())).isEqualTo(7);
```

### AC-3.a: `testShowUpcomingVisitsCustomDays` asserts 3-day window via ArgumentCaptor

Same test run as AC-1.a. Test verifies:

```java
assertThat(ChronoUnit.DAYS.between(startCaptor.getValue(), endCaptor.getValue())).isEqualTo(3);
```

### AC-5.a: Owner name fields referenced in `upcomingVisits.html`

```text
$ grep -n "ownerFirst\|ownerLast\|firstName\|lastName" src/main/resources/templates/visits/upcomingVisits.html
31:             th:text="${visit.ownerFirstName + ' ' + visit.ownerLastName}">
```

### AC-5.b: Pet name, date, description referenced in `upcomingVisits.html`

```text
$ grep -n "petName\|\.date\|\.description" src/main/resources/templates/visits/upcomingVisits.html
28:        <td th:text="${visit.date}">2026-05-10</td>
35:        <td th:text="${visit.petName}">Pet Name</td>
36:        <td th:text="${visit.description}">Description</td>
```

### AC-5.c: Empty-state element present in `upcomingVisits.html`

```text
$ grep -n "isEmpty\|th:if.*visit\|th:unless" src/main/resources/templates/visits/upcomingVisits.html
12:  <div th:if="${visits.isEmpty()}" class="alert alert-info" role="alert">
16:  <table th:unless="${visits.isEmpty()}"
```

### Full suite — no regressions

```text
$ ./mvnw test

[WARNING] Tests run: 80, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
[INFO] Total time:  13.666 s
[INFO] Finished at: 2026-05-06T13:30:06-05:00
```

5 skipped tests are Docker-dependent MySQL/PostgreSQL container tests; not regressions.
