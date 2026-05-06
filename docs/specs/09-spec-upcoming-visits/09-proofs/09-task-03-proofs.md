# Proofs: Task 03 — Implement UpcomingVisitsController and upcomingVisits.html template (GREEN)

Covers: AC-1.a, AC-2.a, AC-3.a, AC-5.a, AC-5.b, AC-5.c

## Planned evidence

- Output of `./mvnw test -Dtest=UpcomingVisitsControllerTests` exiting 0 with both tests passing (GREEN).
- Output of `./mvnw test` exiting 0 (no regressions).
- Output of `grep -n "ownerFirst\|ownerLast\|firstName\|lastName" src/main/resources/templates/visits/upcomingVisits.html` showing at least one match.
- Output of `grep -n "petName\|\.date\|\.description" src/main/resources/templates/visits/upcomingVisits.html` showing at least one match.
- Output of `grep -n "isEmpty\|th:if.*visit\|th:unless" src/main/resources/templates/visits/upcomingVisits.html` showing at least one match.

## Completion notes

(Filled in by `implement-sdd-spec`.)
