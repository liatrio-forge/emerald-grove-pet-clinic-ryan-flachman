# Validation: ClaudeApiClientImpl (18)

## Automated verification

From repository root:

```bash
# AC-1.a, AC-2.a: file existence
ls src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiException.java
ls src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java

# AC-1.b: extends RuntimeException
grep "extends RuntimeException" \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiException.java

# AC-2.b: implements ClaudeApiClient
grep "implements ClaudeApiClient" \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java

# AC-2.c: @ConditionalOnExpression present
grep "ConditionalOnExpression" \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java

# AC-2.d: compile check
./mvnw compile

# AC-7.a: full test suite green
./mvnw test

# AC-7.b: coverage report (inspect target/site/jacoco/index.html afterward)
./mvnw test jacoco:report
```

**Expected:**

- `ls` commands: exit 0, files present.
- `grep` for `extends RuntimeException`: one matching line.
- `grep` for `implements ClaudeApiClient`: one matching line.
- `grep` for `ConditionalOnExpression`: one matching line.
- `./mvnw compile`: exit 0, zero errors.
- `./mvnw test`: exit 0, zero test failures. `ClaudeApiExceptionTest` and
  `ClaudeApiClientImplTest` both appear in the passed-tests list.
- JaCoCo report: `ClaudeApiException` and `ClaudeApiClientImpl` each show
  ≥90% line coverage.

## Traceability

- Feature spec: `18-spec-claude-api-client-impl.md`
- Task breakdown: `18-tasks-claude-api-client-impl.md`
- Questions and decisions: `18-questions-1-claude-api-client-impl.md`
- Per-task evidence: `18-proofs/18-task-NN-proofs.md`
- Upstream contract spec: `17-spec-claude-api-client/17-spec-claude-api-client.md`
- Upstream config spec: `13-spec-async-config/` (Anthropic properties)
- Parent epic: `docs/epic-ai-visit-summary.md` (TASK-09)

## Manual checks

None — all acceptance criteria are automatable.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `ClaudeApiException.java` exists at the correct path | `18-proofs/18-task-02-proofs.md` | file creation | PASS |
| AC-1.b | Class extends `RuntimeException` | `18-proofs/18-task-02-proofs.md` | command output | PASS |
| AC-1.c | `ClaudeApiExceptionTest` asserts both constructors | `18-proofs/18-task-02-proofs.md` | Maven test pass | PASS |
| AC-2.a | `ClaudeApiClientImpl.java` exists at the correct path | `18-proofs/18-task-04-proofs.md` | file creation | PASS |
| AC-2.b | Class implements `ClaudeApiClient` | `18-proofs/18-task-04-proofs.md` | command output | PASS |
| AC-2.c | `@ConditionalOnExpression` annotation present | `18-proofs/18-task-04-proofs.md` | command output | PASS |
| AC-2.d | `./mvnw compile` exits 0 | `18-proofs/18-task-04-proofs.md` | command output | PASS |
| AC-3.a | Test verifies POST to URL with `x-api-key` header | `18-proofs/18-task-04-proofs.md` | Maven test pass | PASS |
| AC-3.b | Test verifies `anthropic-version: 2023-06-01` header | `18-proofs/18-task-04-proofs.md` | Maven test pass | PASS |
| AC-3.c | Test verifies `Content-Type: application/json` header | `18-proofs/18-task-04-proofs.md` | Maven test pass | PASS |
| AC-3.d | Test verifies `ClaudeRequest` body shape | `18-proofs/18-task-04-proofs.md` | Maven test pass | PASS |
| AC-4.a | Test asserts `content[0].text` is returned | `18-proofs/18-task-04-proofs.md` | Maven test pass | PASS |
| AC-4.b | Test asserts `ClaudeApiException` on empty `content` | `18-proofs/18-task-04-proofs.md` | Maven test pass | PASS |
| AC-5.a | Test asserts `ClaudeApiException` on HTTP 429 | `18-proofs/18-task-04-proofs.md` | Maven test pass | PASS |
| AC-5.b | Test asserts `ClaudeApiException` on HTTP 503 | `18-proofs/18-task-04-proofs.md` | Maven test pass | PASS |
| AC-6.a | Maven output shows `ClaudeApiExceptionTest` RED before impl | `18-proofs/18-task-01-proofs.md` | command output | PASS |
| AC-6.b | Maven output shows `ClaudeApiClientImplTest` RED before impl | `18-proofs/18-task-03-proofs.md` | command output | PASS |
| AC-7.a | `./mvnw test` exits 0 with zero failures | `18-proofs/18-task-05-proofs.md` | Maven test pass | PASS |
| AC-7.b | ≥90% line coverage on new classes per JaCoCo | `18-proofs/18-task-05-proofs.md` | JaCoCo coverage report | PASS |

## Definition of done

- [x] AC-1.a: `ClaudeApiException.java` exists at the correct path.
- [x] AC-1.b: Class extends `RuntimeException`.
- [x] AC-1.c: `ClaudeApiExceptionTest` asserts both constructors.
- [x] AC-2.a: `ClaudeApiClientImpl.java` exists at the correct path.
- [x] AC-2.b: Class implements `ClaudeApiClient`.
- [x] AC-2.c: `@ConditionalOnExpression` annotation present.
- [x] AC-2.d: `./mvnw compile` exits 0.
- [x] AC-3.a: Test verifies POST to URL with `x-api-key` header.
- [x] AC-3.b: Test verifies `anthropic-version: 2023-06-01` header.
- [x] AC-3.c: Test verifies `Content-Type: application/json` header.
- [x] AC-3.d: Test verifies `ClaudeRequest` body shape.
- [x] AC-4.a: Test asserts `content[0].text` is returned.
- [x] AC-4.b: Test asserts `ClaudeApiException` on empty `content`.
- [x] AC-5.a: Test asserts `ClaudeApiException` on HTTP 429.
- [x] AC-5.b: Test asserts `ClaudeApiException` on HTTP 503.
- [x] AC-6.a: RED-phase proof captured for `ClaudeApiExceptionTest`.
- [x] AC-6.b: RED-phase proof captured for `ClaudeApiClientImplTest`.
- [x] AC-7.a: `./mvnw test` exits 0 with zero failures.
- [x] AC-7.b: ≥90% line coverage on new classes per JaCoCo.
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
