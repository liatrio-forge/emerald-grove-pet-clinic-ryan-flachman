# Proofs: Task 02 — Update VisitController (inject service + guarded generate call)

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b, AC-3.a, AC-4.c, AC-4.d, AC-5.a, AC-5.b

## Planned evidence

- Diff or full listing of updated `VisitController.java` showing:
  - `private final VisitSummaryService visitSummaryService` field
  - Two-parameter constructor
  - `if (visit.getId() != null) { visitSummaryService.generate(visit.getId()); }`
    on the success path, after `owners.save(owner)`
- `./mvnw compile` output — `BUILD SUCCESS`
- `./mvnw test -Dtest=VisitControllerTests` output — all 9 tests
  **PASS** (GREEN phase, including the 2 new tests from Task 01)

## Completion notes

(Filled in by `implement-sdd-spec`.)
