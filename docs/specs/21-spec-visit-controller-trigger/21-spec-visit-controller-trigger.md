---
status: accepted
created: 2026-05-12
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: VisitController Async Trigger (21)

## Goal

`VisitSummaryService` (spec-20) is fully implemented but never called —
visits accumulate with `aiStatus = PENDING` indefinitely after being saved.
This spec wires the service into `VisitController` so that every
successfully persisted visit immediately fires an async AI summarization
job on the `visitSummaryExecutor` thread pool. Validation-rejected
submissions are left untouched. This closes the last back-end piece
before the full async flow can be integration-tested (TASK-13).

## Scope

### In scope

- Add `VisitSummaryService` constructor injection to `VisitController`.
- In `processNewVisitForm()`, after `owners.save(owner)`, extract
  `visit.getId()` and call `visitSummaryService.generate(visitId)`,
  guarded by `if (visitId != null)`.
- Add `@MockitoBean VisitSummaryService visitSummaryService` to
  `VisitControllerTests`.
- Extend `@BeforeEach init()` to configure the `owners.save()` mock via
  `willAnswer` to assign visit ID `42` to every visit on the pet with
  `TEST_PET_ID`, enabling deterministic ID verification.
- New test `testGenerateCalledOnSuccessfulVisit()` — verifies
  `generate(42)` is called exactly once after a valid POST.
- New test `testGenerateNotCalledWhenValidationFails()` — verifies
  `generate()` is never called when validation fails (missing
  description).

### Out of scope

- `VisitSummaryController` (polling endpoint) — TASK-11, separate spec.
- `VisitSummaryIntegrationTest` — TASK-13, separate spec.
- Any health-timeline Thymeleaf or CSS changes.
- Retry logic on `FAILED` visits (open epic decision).

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java` —
  package-private Spring MVC controller; `processNewVisitForm()` currently
  calls `owners.save(owner)` and redirects without triggering any async
  work.
- `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryService.java`
  (spec-20, accepted) — `@Service` with
  `@Async("visitSummaryExecutor") void generate(Integer visitId)`.
- `src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java` —
  `@WebMvcTest(VisitController.class)` with 7 existing test methods and
  `@MockitoBean OwnerRepository owners`.

## Acceptance criteria

- **AC-1: VisitSummaryService is injected**
  - AC-1.a: `VisitController` declares
    `private final VisitSummaryService visitSummaryService`.
  - AC-1.b: `VisitController` constructor accepts `VisitSummaryService`
    as its second parameter.
  - AC-1.c: `./mvnw compile` exits 0.

- **AC-2: Successful save triggers async generation**
  - AC-2.a: In `processNewVisitForm()`, `visitSummaryService.generate(visit.getId())`
    is called when `visit.getId() != null`, on the redirect (success)
    path only.
  - AC-2.b: The generate call appears after `owners.save(owner)` and
    before `return "redirect:/owners/{ownerId}"`.

- **AC-3: Validation failure does not trigger generation**
  - AC-3.a: When `result.hasErrors()` is true, `processNewVisitForm()`
    returns the form view without calling `visitSummaryService.generate()`.

- **AC-4: VisitControllerTests extended**
  - AC-4.a: `VisitControllerTests` declares
    `@MockitoBean VisitSummaryService visitSummaryService`.
  - AC-4.b: `@BeforeEach init()` configures `owners.save()` via
    `willAnswer` to set ID `42` on each visit belonging to the pet with
    `TEST_PET_ID`.
  - AC-4.c: `testGenerateCalledOnSuccessfulVisit()` posts a valid visit
    form and verifies `verify(visitSummaryService).generate(TEST_VISIT_ID)`
    (exactly once, `TEST_VISIT_ID = 42`).
  - AC-4.d: `testGenerateNotCalledWhenValidationFails()` posts an invalid
    form (missing description) and verifies
    `verifyNoInteractions(visitSummaryService)`.
  - AC-4.e: All 7 pre-existing `VisitControllerTests` test methods still
    pass without modification.

- **AC-5: Full test suite green**
  - AC-5.a: `./mvnw test -Dtest=VisitControllerTests` exits 0.
  - AC-5.b: `./mvnw test` exits 0 for the entire module.

## Conventions

- `VisitController` is package-private. The constructor gains a second
  parameter but does not change visibility.
- The null guard `if (visit.getId() != null)` is a deliberate defensive
  measure (see `21-questions-1-visit-controller-trigger.md`, Q4). Do not
  remove it during implementation.
- `TEST_VISIT_ID = 42` is a new test constant; add it alongside the
  existing `TEST_OWNER_ID` and `TEST_PET_ID` constants in
  `VisitControllerTests`.
- The `willAnswer` for `owners.save()` must be configured in
  `@BeforeEach` so all test methods that POST a valid form inherit the
  ID assignment without per-test repetition.
- Use `verifyNoInteractions(visitSummaryService)` (not
  `verify(visitSummaryService, never()).generate(any())`) on the failure
  path — it catches unexpected calls to any method, not just `generate`.
- Upstream service spec: `20-spec-visit-summary-service`.
- This spec blocks TASK-13 (`VisitSummaryIntegrationTest`).

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
