# Validation: VisitSummaryController (21)

## Automated verification

From repository root:

```bash
# Compile the whole project — catches any import or signature errors
./mvnw compile

# Run only the new controller tests
./mvnw test -Dtest=VisitSummaryControllerTests

# Full test suite — confirms no regressions
./mvnw test

# Coverage report — confirm ≥ 90% line coverage on new classes
./mvnw test jacoco:report

# Structural checks — run each grep and confirm non-empty output
grep -r "@RestController" src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryController.java
grep -r "@GetMapping.*visits.*visitId.*summary" src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryController.java
grep -r "@JsonInclude" src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryResponse.java
```

**Expected:**

- `./mvnw compile` — exits 0, no errors.
- `./mvnw test -Dtest=VisitSummaryControllerTests` — exits 0, all 6 test methods
  pass (PENDING, PROCESSING, DONE, DONE-null-followUp, DONE-empty-tags, FAILED, 404).
- `./mvnw test` — exits 0, BUILD SUCCESS, zero test failures.
- JaCoCo report — `VisitSummaryController` and `VisitSummaryResponse` each show
  ≥ 90% line coverage.
- Each grep — prints at least one matching line.

## Traceability

- Feature spec: `21-spec-visit-summary-controller.md`
- Task breakdown: `21-tasks-visit-summary-controller.md`
- Questions and decisions: `21-questions-1-visit-summary-controller.md`
- Per-task evidence:
  - `21-proofs/21-task-01-proofs.md`
  - `21-proofs/21-task-02-proofs.md`
  - `21-proofs/21-task-03-proofs.md`
- Upstream specs: spec-14 (`AiStatus`, `Visit` entity), spec-20 (`VisitRepository`
  with `findById`)
- Parent epic: `docs/epic-ai-visit-summary.md` TASK-11

## Manual checks

None — all acceptance criteria are verifiable by automated command.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `VisitSummaryResponse.java` exists at correct path | `21-proofs/21-task-02-proofs.md` | file creation | PASS |
| AC-1.b | `@JsonInclude(NON_NULL)` annotation present | `21-proofs/21-task-02-proofs.md` | command output (grep) | PASS |
| AC-2.a | `VisitSummaryController.java` exists at correct path | `21-proofs/21-task-02-proofs.md` | file creation | PASS |
| AC-2.b | `@RestController` annotation present | `21-proofs/21-task-02-proofs.md` | command output (grep) | PASS |
| AC-2.c | `@GetMapping("/visits/{visitId}/summary")` present | `21-proofs/21-task-02-proofs.md` | command output (grep) | PASS |
| AC-3.a | PENDING test passes | `21-proofs/21-task-02-proofs.md` | Maven test pass | PASS |
| AC-3.b | PROCESSING→PENDING test passes | `21-proofs/21-task-02-proofs.md` | Maven test pass | PASS |
| AC-4.a | DONE response shape test passes | `21-proofs/21-task-02-proofs.md` | Maven test pass | PASS |
| AC-4.b | urgency value is lowercase in DONE response | `21-proofs/21-task-02-proofs.md` | Maven test pass | PASS |
| AC-4.c | null followUp is omitted from JSON | `21-proofs/21-task-02-proofs.md` | Maven test pass | PASS |
| AC-5.a | FAILED response shape test passes | `21-proofs/21-task-02-proofs.md` | Maven test pass | PASS |
| AC-6.a | 404 test passes for unknown visitId | `21-proofs/21-task-02-proofs.md` | Maven test pass | PASS |
| AC-7.a | tags JSON array assertion in DONE test | `21-proofs/21-task-02-proofs.md` | Maven test pass | PASS |
| AC-7.b | empty/null aiTags returns `[]` test passes | `21-proofs/21-task-02-proofs.md` | Maven test pass | PASS |
| AC-8.a | RED proof captures failing Maven output | `21-proofs/21-task-01-proofs.md` | command output | PASS |
| AC-9.a | `./mvnw test` exits 0 after all changes | `21-proofs/21-task-03-proofs.md` | Maven test pass | PASS |
| AC-9.b | ≥ 90% line coverage on new classes | `21-proofs/21-task-03-proofs.md` | JaCoCo coverage report | PASS |

## Definition of done

- [x] AC-1.a: `VisitSummaryResponse.java` exists at correct path
- [x] AC-1.b: `@JsonInclude(NON_NULL)` annotation present on `VisitSummaryResponse`
- [x] AC-2.a: `VisitSummaryController.java` exists at correct path
- [x] AC-2.b: `@RestController` annotation present on `VisitSummaryController`
- [x] AC-2.c: `@GetMapping("/visits/{visitId}/summary")` present in `VisitSummaryController`
- [x] AC-3.a: PENDING test passes
- [x] AC-3.b: PROCESSING→PENDING test passes
- [x] AC-4.a: DONE response shape test passes
- [x] AC-4.b: urgency value is lowercase in DONE response JSON
- [x] AC-4.c: null `followUp` is omitted from the DONE JSON (not `"followUp":null`)
- [x] AC-5.a: FAILED response shape test passes
- [x] AC-6.a: 404 returned for unknown visitId
- [x] AC-7.a: `tags` is a JSON array in the DONE response
- [x] AC-7.b: null/blank `aiTags` yields `"tags":[]` in DONE response
- [x] AC-8.a: RED proof artifact captures failing Maven output before controller creation
- [x] AC-9.a: `./mvnw test` exits 0 after all changes applied
- [x] AC-9.b: ≥ 90% line coverage on `VisitSummaryController` and `VisitSummaryResponse`
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
