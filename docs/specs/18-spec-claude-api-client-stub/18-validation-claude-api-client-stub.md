# Validation: ClaudeApiClientStub (18)

## Automated verification

From repository root:

```bash
# AC-1.e, AC-11.a — compile check
./mvnw compile

# AC-1.a — file exists
test -f src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientStub.java \
  && echo "PASS: file exists" || echo "FAIL: file missing"

# AC-1.b — @Component annotation present
grep "@Component" \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientStub.java

# AC-1.c — @ConditionalOnExpression annotation present
grep "ConditionalOnExpression" \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientStub.java

# AC-1.d — implements ClaudeApiClient
grep "implements ClaudeApiClient" \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientStub.java

# AC-2 through AC-9, AC-11 — run the full test suite
./mvnw test

# Line-coverage report (must show ≥90% for new files)
./mvnw test jacoco:report
# Then inspect: target/site/jacoco/index.html
```

**Expected:**

- `./mvnw compile` — exit 0, no errors.
- Each `grep` command — returns at least one matching line.
- `./mvnw test` — exit 0, zero test failures; output contains
  `ClaudeApiClientStubTests` with all test methods passing.
- JaCoCo report — `ClaudeApiClientStub` shows ≥90% line coverage and
  100% branch coverage on the keyword-routing logic.

## Traceability

- Feature spec: `18-spec-claude-api-client-stub.md`
- Task breakdown: `18-tasks-claude-api-client-stub.md`
- Questions and decisions: `18-questions-1-claude-api-client-stub.md`
- Per-task evidence: `18-proofs/18-task-NN-proofs.md`
- Upstream contract spec: `17-spec-claude-api-client/17-spec-claude-api-client.md`

## Manual checks

None required. All acceptance criteria are automatable.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `ClaudeApiClientStub.java` exists at correct path | `18-proofs/18-task-02-proofs.md` | file creation | PASS |
| AC-1.b | Class annotated `@Component` | `18-proofs/18-task-02-proofs.md` | command output | PASS |
| AC-1.c | Class annotated `@ConditionalOnExpression(...)` for blank key | `18-proofs/18-task-02-proofs.md` | command output | PASS |
| AC-1.d | Class declaration includes `implements ClaudeApiClient` | `18-proofs/18-task-02-proofs.md` | command output | PASS |
| AC-1.e | `./mvnw compile` exits 0 | `18-proofs/18-task-02-proofs.md` | command output | PASS |
| AC-2.a | `completeWithLimpReturnsUrgentJson` passes | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-3.a | `completeWithPainReturnsUrgentJson` passes | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-4.a | `completeWithCheckupReturnsRoutineJson` passes | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-5.a | `completeWithUnknownKeywordReturnsMonitorJson` passes | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-6.a | `completeWithBothLimpAndCheckupReturnsUrgent` passes | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-7.a | `completeIsCaseInsensitive` passes | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-8.a | `completeWithBlankMessageReturnsMonitorJson` passes | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-8.b | `completeWithNullMessageReturnsMonitorJson` passes | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-9.a | All tests parse result with `ObjectMapper` and assert structure | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-9.b | `tags` node is a JSON array with ≥1 element in every path | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-10.a | Task-01 proof captures RED-phase compile/test failure | `18-proofs/18-task-01-proofs.md` | command output | PASS |
| AC-11.a | `./mvnw test` exits 0 with zero failures after all changes | `18-proofs/18-task-03-proofs.md` | command output | PASS |

## Definition of done

- [x] AC-1.a: `ClaudeApiClientStub.java` exists at correct path.
- [x] AC-1.b: Class annotated `@Component`.
- [x] AC-1.c: Class annotated `@ConditionalOnExpression(...)` for blank key.
- [x] AC-1.d: Class declaration includes `implements ClaudeApiClient`.
- [x] AC-1.e: `./mvnw compile` exits 0.
- [x] AC-2.a: `completeWithLimpReturnsUrgentJson` passes.
- [x] AC-3.a: `completeWithPainReturnsUrgentJson` passes.
- [x] AC-4.a: `completeWithCheckupReturnsRoutineJson` passes.
- [x] AC-5.a: `completeWithUnknownKeywordReturnsMonitorJson` passes.
- [x] AC-6.a: `completeWithBothLimpAndCheckupReturnsUrgent` passes.
- [x] AC-7.a: `completeIsCaseInsensitive` passes.
- [x] AC-8.a: `completeWithBlankMessageReturnsMonitorJson` passes.
- [x] AC-8.b: `completeWithNullMessageReturnsMonitorJson` passes.
- [x] AC-9.a: All tests parse result with `ObjectMapper` and assert structure.
- [x] AC-9.b: `tags` node is a JSON array with ≥1 element in every path.
- [x] AC-10.a: Task-01 proof captures RED-phase compile/test failure.
- [x] AC-11.a: `./mvnw test` exits 0 with zero failures after all changes.
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
