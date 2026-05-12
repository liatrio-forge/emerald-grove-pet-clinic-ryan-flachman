# Validation: VisitController Async Trigger (21)

## Automated verification

From repository root:

```bash
# AC-1.c, AC-5.a, AC-5.b — compile and full test suite
./mvnw compile
./mvnw test -Dtest=VisitControllerTests
./mvnw test

# AC-1.a — VisitSummaryService field declared in VisitController
grep -n "private final VisitSummaryService visitSummaryService" \
  src/main/java/org/springframework/samples/petclinic/owner/VisitController.java

# AC-1.b — constructor accepts VisitSummaryService
grep -A 5 "VisitController(" \
  src/main/java/org/springframework/samples/petclinic/owner/VisitController.java

# AC-2.a, AC-2.b — generate() call on success path with null guard
grep -n "generate\|getId\|null" \
  src/main/java/org/springframework/samples/petclinic/owner/VisitController.java

# AC-3.a — no generate() call on error path (early return before generate)
grep -n "hasErrors\|generate" \
  src/main/java/org/springframework/samples/petclinic/owner/VisitController.java

# AC-4.a — MockitoBean for VisitSummaryService in test class
grep -n "MockitoBean\|VisitSummaryService" \
  src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java

# AC-4.b — willAnswer configures owners.save() in @BeforeEach
grep -n "willAnswer\|TEST_VISIT_ID\|setId" \
  src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java

# AC-4.c — testGenerateCalledOnSuccessfulVisit exists
grep -n "testGenerateCalledOnSuccessfulVisit\|generate(TEST_VISIT_ID)\|generate(42)" \
  src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java

# AC-4.d — testGenerateNotCalledWhenValidationFails exists
grep -n "testGenerateNotCalledWhenValidationFails\|verifyNoInteractions" \
  src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java
```

**Expected:**

| Command | Expected result |
|---------|----------------|
| `./mvnw compile` | `BUILD SUCCESS` |
| `./mvnw test -Dtest=VisitControllerTests` | `BUILD SUCCESS`, 9+ tests run (7 pre-existing + 2 new), 0 failures |
| `./mvnw test` | `BUILD SUCCESS`, 0 failures |
| `grep visitSummaryService` (field) | Exactly one matching line |
| `grep VisitController(` (constructor) | Constructor signature includes `VisitSummaryService` parameter |
| `grep generate\|getId\|null` (controller) | Shows `if (visit.getId() != null)` and `generate(visit.getId())` adjacent |
| `grep hasErrors\|generate` (controller) | `hasErrors()` check appears before any `generate` call |
| `grep MockitoBean\|VisitSummaryService` (test) | Both annotations/types present in test file |
| `grep willAnswer\|TEST_VISIT_ID\|setId` (test) | `willAnswer` and `setId` present in `@BeforeEach` block |
| `grep testGenerateCalledOnSuccessfulVisit` (test) | Method name found |
| `grep testGenerateNotCalledWhenValidationFails\|verifyNoInteractions` (test) | Both found |

## Traceability

- Feature spec: `21-spec-visit-controller-trigger.md`
- Task breakdown: `21-tasks-visit-controller-trigger.md`
- Questions and decisions: `21-questions-1-visit-controller-trigger.md`
- Per-task evidence: `21-proofs/21-task-01-proofs.md`, `21-proofs/21-task-02-proofs.md`, `21-proofs/21-task-03-proofs.md`
- Upstream specs: `20-spec-visit-summary-service` (VisitSummaryService), `14-spec-visit-ai-fields` (AiStatus / Visit entity)

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `VisitController` declares `private final VisitSummaryService visitSummaryService` | `21-proofs/21-task-01-proofs.md` | file edit | PENDING |
| AC-1.b | Constructor accepts `VisitSummaryService` as second parameter | `21-proofs/21-task-01-proofs.md` | file edit | PENDING |
| AC-1.c | `./mvnw compile` exits 0 | `21-proofs/21-task-02-proofs.md` | command output | PENDING |
| AC-2.a | `generate(visit.getId())` called when `visit.getId() != null` on success path | `21-proofs/21-task-02-proofs.md` | file edit | PENDING |
| AC-2.b | Generate call appears after `owners.save()` and before redirect return | `21-proofs/21-task-02-proofs.md` | file edit | PENDING |
| AC-3.a | `hasErrors()` early-return path has no `generate()` call | `21-proofs/21-task-02-proofs.md` | file edit | PENDING |
| AC-4.a | `@MockitoBean VisitSummaryService visitSummaryService` declared in test | `21-proofs/21-task-01-proofs.md` | file edit | PENDING |
| AC-4.b | `@BeforeEach init()` configures `owners.save()` via `willAnswer` to set ID `42` | `21-proofs/21-task-01-proofs.md` | file edit | PENDING |
| AC-4.c | `testGenerateCalledOnSuccessfulVisit()` verifies `generate(42)` called exactly once | `21-proofs/21-task-01-proofs.md` | Maven test pass | PENDING |
| AC-4.d | `testGenerateNotCalledWhenValidationFails()` verifies `verifyNoInteractions(visitSummaryService)` | `21-proofs/21-task-01-proofs.md` | Maven test pass | PENDING |
| AC-4.e | All 7 pre-existing `VisitControllerTests` methods still pass | `21-proofs/21-task-03-proofs.md` | Maven test pass | PENDING |
| AC-5.a | `./mvnw test -Dtest=VisitControllerTests` exits 0 | `21-proofs/21-task-03-proofs.md` | command output | PENDING |
| AC-5.b | `./mvnw test` exits 0 for entire module | `21-proofs/21-task-03-proofs.md` | command output | PENDING |

## Definition of done

- [ ] AC-1.a: `VisitController` declares `private final VisitSummaryService visitSummaryService`.
- [ ] AC-1.b: Constructor accepts `VisitSummaryService` as its second parameter.
- [ ] AC-1.c: `./mvnw compile` exits 0.
- [ ] AC-2.a: `generate(visit.getId())` called when `visit.getId() != null` on success path.
- [ ] AC-2.b: Generate call appears after `owners.save(owner)` and before redirect return.
- [ ] AC-3.a: `hasErrors()` early-return path has no `generate()` call.
- [ ] AC-4.a: `@MockitoBean VisitSummaryService visitSummaryService` declared in test class.
- [ ] AC-4.b: `@BeforeEach` configures `owners.save()` via `willAnswer` to set ID `42`.
- [ ] AC-4.c: `testGenerateCalledOnSuccessfulVisit()` verifies `generate(42)` exactly once.
- [ ] AC-4.d: `testGenerateNotCalledWhenValidationFails()` verifies `verifyNoInteractions(visitSummaryService)`.
- [ ] AC-4.e: All 7 pre-existing `VisitControllerTests` methods still pass.
- [ ] AC-5.a: `./mvnw test -Dtest=VisitControllerTests` exits 0.
- [ ] AC-5.b: `./mvnw test` exits 0 for the entire module.
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
