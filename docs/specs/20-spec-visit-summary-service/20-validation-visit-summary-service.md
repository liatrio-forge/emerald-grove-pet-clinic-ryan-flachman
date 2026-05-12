# Validation: VisitSummaryService (20)

## Automated verification

From repository root:

```bash
# 1. Compile the project
./mvnw compile

# 2. Run parser unit tests only
./mvnw test -Dtest=VisitSummaryParserTests

# 3. Run service unit tests only
./mvnw test -Dtest=VisitSummaryServiceTests

# 4. Confirm VisitPromptBuilder tests still pass after key correction (AC-14.b)
./mvnw test -Dtest=VisitPromptBuilderTest

# 5. Run full test suite (covers AC-16.a)
./mvnw test

# 6. Generate JaCoCo coverage report (covers AC-16.b)
./mvnw test jacoco:report

# 7. Structural checks (AC-7.b, AC-7.c, AC-12, AC-13, AC-14)
grep -r "@Service" src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryService.java
grep -r "@Async" src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryService.java
grep -r "Optional<Visit> findById" src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java
grep -r "Visit save" src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java
grep -r "ManyToOne" src/main/java/org/springframework/samples/petclinic/owner/Visit.java
grep -r "insertable = false" src/main/java/org/springframework/samples/petclinic/owner/Visit.java
grep -r "follow_up" src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java
grep -rn "extends RuntimeException" src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryParseException.java
```

**Expected outputs:**

| Command | Expected |
|---------|----------|
| `./mvnw compile` | `BUILD SUCCESS` |
| `./mvnw test -Dtest=VisitSummaryParserTests` | `BUILD SUCCESS`, all test methods listed as passed |
| `./mvnw test -Dtest=VisitSummaryServiceTests` | `BUILD SUCCESS`, all test methods listed as passed |
| `./mvnw test -Dtest=VisitPromptBuilderTest` | `BUILD SUCCESS`, all test methods listed as passed |
| `./mvnw test` | `BUILD SUCCESS`, 0 failures, 0 errors |
| grep `@Service` | Line containing `@Service` in `VisitSummaryService.java` |
| grep `@Async` | Line containing `@Async("visitSummaryExecutor")` |
| grep `findById` | Line containing `Optional<Visit> findById(Integer id)` |
| grep `Visit save` | Line containing `Visit save(Visit visit)` |
| grep `ManyToOne` | Line containing `@ManyToOne(fetch = FetchType.LAZY)` |
| grep `insertable = false` | Line containing `insertable = false, updatable = false` |
| grep `follow_up` | Line containing `"follow_up"` in system prompt string |
| grep `RuntimeException` | Line containing `extends RuntimeException` |

## Traceability

- Feature spec: `20-spec-visit-summary-service.md`
- Task breakdown: `20-tasks-visit-summary-service.md`
- Questions and decisions: `20-questions-1-visit-summary-service.md`
- Per-task evidence: `20-proofs/20-task-NN-proofs.md`
- Upstream specs: spec-13 (AsyncConfig), spec-14 (Visit entity), spec-15
  (VisitSummary/VisitUrgency), spec-16 (VisitPromptBuilder), spec-17
  (ClaudeApiClient), spec-18 (stub/impl)

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `VisitSummaryParseException.java` exists | `20-proofs/20-task-02-proofs.md` | file creation | PASS |
| AC-1.b | Extends `RuntimeException` | `20-proofs/20-task-02-proofs.md` | grep output | PASS |
| AC-2.a | `parseHappyPath` passes | `20-proofs/20-task-02-proofs.md` | Maven test pass | PASS |
| AC-3.a | `parseMissingFollowUpReturnsNull` passes | `20-proofs/20-task-02-proofs.md` | Maven test pass | PASS |
| AC-4.a | `parseUnknownUrgencyDefaultsToRoutine` passes | `20-proofs/20-task-02-proofs.md` | Maven test pass | PASS |
| AC-5.a | `parseMalformedJsonThrowsParseException` passes | `20-proofs/20-task-02-proofs.md` | Maven test pass | PASS |
| AC-6.a | `parseEmptyTagsReturnsEmptyList` passes | `20-proofs/20-task-02-proofs.md` | Maven test pass | PASS |
| AC-6.b | `parseSingleTag` passes | `20-proofs/20-task-02-proofs.md` | Maven test pass | PASS |
| AC-6.c | `parseMultipleTags` passes | `20-proofs/20-task-02-proofs.md` | Maven test pass | PASS |
| AC-7.a | `VisitSummaryService.java` exists | `20-proofs/20-task-05-proofs.md` | file creation | PASS |
| AC-7.b | Class annotated `@Service` | `20-proofs/20-task-05-proofs.md` | grep output | PASS |
| AC-7.c | `generate` annotated `@Async("visitSummaryExecutor")` | `20-proofs/20-task-05-proofs.md` | grep output | PASS |
| AC-8.a | `generateHappyPathSetsProcessingThenDone` passes | `20-proofs/20-task-05-proofs.md` | Maven test pass | PASS |
| AC-8.b | `generateHappyPathWritesAiFields` passes | `20-proofs/20-task-05-proofs.md` | Maven test pass | PASS |
| AC-9.a | `generateClientExceptionSetsFailedStatus` passes | `20-proofs/20-task-05-proofs.md` | Maven test pass | PASS |
| AC-9.b | No exception propagates on client error | `20-proofs/20-task-05-proofs.md` | Maven test pass | PASS |
| AC-10.a | `generateParseExceptionSetsFailedStatus` passes | `20-proofs/20-task-05-proofs.md` | Maven test pass | PASS |
| AC-11.a | `generateVisitNotFoundLogsAndReturns` passes | `20-proofs/20-task-05-proofs.md` | Maven test pass | PASS |
| AC-11.b | No exception propagates when visit missing | `20-proofs/20-task-05-proofs.md` | Maven test pass | PASS |
| AC-12.a | `findById(Integer id)` declared | `20-proofs/20-task-03-proofs.md` | grep output | PASS |
| AC-12.b | `save(Visit visit)` declared | `20-proofs/20-task-03-proofs.md` | grep output | PASS |
| AC-13.a | `@ManyToOne(fetch = FetchType.LAZY) Pet pet` on Visit | `20-proofs/20-task-03-proofs.md` | grep output | PASS |
| AC-13.b | `insertable = false, updatable = false` on Pet FK | `20-proofs/20-task-03-proofs.md` | grep output | PASS |
| AC-14.a | `"follow_up"` in VisitPromptBuilder system prompt | `20-proofs/20-task-03-proofs.md` | grep output | PASS |
| AC-14.b | `VisitPromptBuilderTest` still passes | `20-proofs/20-task-03-proofs.md` | Maven test pass | PASS |
| AC-15.a | Parser RED proof captured | `20-proofs/20-task-01-proofs.md` | command output | PASS |
| AC-15.b | Service RED proof captured | `20-proofs/20-task-04-proofs.md` | command output | PASS |
| AC-16.a | `./mvnw test` exits 0 | `20-proofs/20-task-06-proofs.md` | Maven test pass | PASS |
| AC-16.b | ≥ 90% line coverage on new code | `20-proofs/20-task-06-proofs.md` | JaCoCo coverage | PASS |

## Definition of done

- [x] AC-1.a: `VisitSummaryParseException.java` exists.
- [x] AC-1.b: Extends `RuntimeException`.
- [x] AC-2.a: `parseHappyPath` passes.
- [x] AC-3.a: `parseMissingFollowUpReturnsNull` passes.
- [x] AC-4.a: `parseUnknownUrgencyDefaultsToRoutine` passes.
- [x] AC-5.a: `parseMalformedJsonThrowsParseException` passes.
- [x] AC-6.a: `parseEmptyTagsReturnsEmptyList` passes.
- [x] AC-6.b: `parseSingleTag` passes.
- [x] AC-6.c: `parseMultipleTags` passes.
- [x] AC-7.a: `VisitSummaryService.java` exists.
- [x] AC-7.b: Annotated `@Service`.
- [x] AC-7.c: `generate` annotated `@Async("visitSummaryExecutor")`.
- [x] AC-8.a: `generateHappyPathSetsProcessingThenDone` passes.
- [x] AC-8.b: `generateHappyPathWritesAiFields` passes.
- [x] AC-9.a: `generateClientExceptionSetsFailedStatus` passes.
- [x] AC-9.b: No exception propagates on client error.
- [x] AC-10.a: `generateParseExceptionSetsFailedStatus` passes.
- [x] AC-11.a: `generateVisitNotFoundLogsAndReturns` passes.
- [x] AC-11.b: No exception propagates when visit missing.
- [x] AC-12.a: `findById(Integer id)` declared on VisitRepository.
- [x] AC-12.b: `save(Visit visit)` declared on VisitRepository.
- [x] AC-13.a: `@ManyToOne` `Pet pet` field on Visit.
- [x] AC-13.b: `insertable = false, updatable = false` on Pet FK JoinColumn.
- [x] AC-14.a: `"follow_up"` key in VisitPromptBuilder system prompt.
- [x] AC-14.b: `VisitPromptBuilderTest` still passes.
- [x] AC-15.a: Parser RED proof captured.
- [x] AC-15.b: Service RED proof captured.
- [x] AC-16.a: `./mvnw test` exits 0.
- [x] AC-16.b: ≥ 90% line coverage on `VisitSummaryParser` and `VisitSummaryService`.
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥ 90% line coverage on new code.
