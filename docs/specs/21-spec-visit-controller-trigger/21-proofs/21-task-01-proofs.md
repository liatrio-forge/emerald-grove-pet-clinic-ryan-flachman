# Proofs: Task 01 — Write failing tests in VisitControllerTests

Covers: AC-4.a, AC-4.b, AC-4.c, AC-4.d, AC-4.e

## Planned evidence

- Diff or full listing of updated `VisitControllerTests.java` showing:
  - `TEST_VISIT_ID = 42` constant
  - `@MockitoBean VisitSummaryService visitSummaryService` field
  - `willAnswer` block in `@BeforeEach init()`
  - `testGenerateCalledOnSuccessfulVisit()` method body
  - `testGenerateNotCalledWhenValidationFails()` method body
- `./mvnw test -Dtest=VisitControllerTests` output showing the 2 new
  tests **FAIL** (RED phase — compile succeeds but tests fail because
  `VisitController` doesn't yet call `generate()`).

## Completion notes

(Filled in by `implement-sdd-spec`.)
