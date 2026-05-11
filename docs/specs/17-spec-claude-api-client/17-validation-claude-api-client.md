# Validation: ClaudeApiClient Interface (17)

## Automated verification

From repository root:

```bash
# AC-1.d, AC-7.a — compile and full test suite
./mvnw compile
./mvnw test

# AC-1.b — interface method signature present
grep "String complete(String systemPrompt, String userMessage)" \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java

# AC-1.c — no Spring annotations on the interface
grep "@Component\|@Service\|@Bean\|@Repository" \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java

# AC-2.c — @JsonProperty("max_tokens") on ClaudeRequest
grep '@JsonProperty("max_tokens")' \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeRequest.java

# AC-4.c — @JsonProperty("stop_reason") on ClaudeResponse
grep '@JsonProperty("stop_reason")' \
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeResponse.java

# AC-1.a — interface file exists
test -f src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java \
  && echo "ClaudeApiClient.java EXISTS" || echo "MISSING"

# AC-2.a — ClaudeRequest file exists
test -f src/main/java/org/springframework/samples/petclinic/owner/ClaudeRequest.java \
  && echo "ClaudeRequest.java EXISTS" || echo "MISSING"

# AC-3.a — Message file exists
test -f src/main/java/org/springframework/samples/petclinic/owner/Message.java \
  && echo "Message.java EXISTS" || echo "MISSING"

# AC-4.a — ClaudeResponse file exists
test -f src/main/java/org/springframework/samples/petclinic/owner/ClaudeResponse.java \
  && echo "ClaudeResponse.java EXISTS" || echo "MISSING"

# AC-5.a — ContentBlock file exists
test -f src/main/java/org/springframework/samples/petclinic/owner/ContentBlock.java \
  && echo "ContentBlock.java EXISTS" || echo "MISSING"
```

**Expected output:**

- `./mvnw compile` — exits 0, no compilation errors.
- `./mvnw test` — exits 0, `BUILD SUCCESS`, zero test failures.
- `grep` for interface method — prints the line containing `String complete(...)`.
- `grep` for Spring annotations on interface — **no output** (no annotations present).
- `grep` for `@JsonProperty("max_tokens")` — prints the annotated component line.
- `grep` for `@JsonProperty("stop_reason")` — prints the annotated component line.
- All five `test -f` checks — print `EXISTS`.

## Traceability

- Feature spec: `17-spec-claude-api-client.md`
- Task breakdown: `17-tasks-claude-api-client.md`
- Questions and decisions: `17-questions-1-claude-api-client.md`
- Per-task evidence: `17-proofs/17-task-NN-proofs.md`
- Upstream specs:
  - `14-spec-visit-ai-fields` — `AiStatus` enum, same package (delivered)
  - `16-spec-visit-prompt-builder` — `PromptRequest` record, same package (delivered)
- Downstream specs (blocked until delivered):
  - `ClaudeApiClientStub` (TASK-08)
  - `ClaudeApiClientImpl` (TASK-09)
  - `VisitSummaryService` (TASK-10)
- Parent epic: `docs/epic-ai-visit-summary.md` TASK-07

## Manual checks

None required — all criteria are verifiable by command.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `ClaudeApiClient.java` exists at correct path | `17-proofs/17-task-02-proofs.md` | file creation | PENDING |
| AC-1.b | Interface method signature matches contract | `17-proofs/17-task-02-proofs.md` | command output | PENDING |
| AC-1.c | No Spring annotations on interface | `17-proofs/17-task-02-proofs.md` | command output | PENDING |
| AC-1.d | `./mvnw compile` exits 0 | `17-proofs/17-task-02-proofs.md` | command output | PENDING |
| AC-2.a | `ClaudeRequest.java` exists at correct path | `17-proofs/17-task-02-proofs.md` | file creation | PENDING |
| AC-2.b | Record components in correct order and types | `17-proofs/17-task-02-proofs.md` | file creation | PENDING |
| AC-2.c | `@JsonProperty("max_tokens")` on `maxTokens` | `17-proofs/17-task-02-proofs.md` | command output | PENDING |
| AC-2.d | `ClaudeRequestTest` construction test passes | `17-proofs/17-task-02-proofs.md` | Maven test pass | PENDING |
| AC-3.a | `Message.java` exists at correct path | `17-proofs/17-task-02-proofs.md` | file creation | PENDING |
| AC-3.b | Record components in correct order and types | `17-proofs/17-task-02-proofs.md` | file creation | PENDING |
| AC-3.c | `MessageTest` construction test passes | `17-proofs/17-task-02-proofs.md` | Maven test pass | PENDING |
| AC-4.a | `ClaudeResponse.java` exists at correct path | `17-proofs/17-task-02-proofs.md` | file creation | PENDING |
| AC-4.b | Record components in correct order and types | `17-proofs/17-task-02-proofs.md` | file creation | PENDING |
| AC-4.c | `@JsonProperty("stop_reason")` on `stopReason` | `17-proofs/17-task-02-proofs.md` | command output | PENDING |
| AC-4.d | `ClaudeResponseTest` construction test passes | `17-proofs/17-task-02-proofs.md` | Maven test pass | PENDING |
| AC-5.a | `ContentBlock.java` exists at correct path | `17-proofs/17-task-02-proofs.md` | file creation | PENDING |
| AC-5.b | Record components in correct order and types | `17-proofs/17-task-02-proofs.md` | file creation | PENDING |
| AC-5.c | `ContentBlockTest` construction test passes | `17-proofs/17-task-02-proofs.md` | Maven test pass | PENDING |
| AC-6.a | RED-phase proof: tests fail before production code | `17-proofs/17-task-01-proofs.md` | command output | PENDING |
| AC-7.a | `./mvnw test` exits 0 after all changes | `17-proofs/17-task-03-proofs.md` | Maven test pass | PENDING |

## Definition of done

- [ ] AC-1.a: `ClaudeApiClient.java` exists at correct path
- [ ] AC-1.b: Interface method signature matches contract
- [ ] AC-1.c: No Spring annotations on interface
- [ ] AC-1.d: `./mvnw compile` exits 0
- [ ] AC-2.a: `ClaudeRequest.java` exists at correct path
- [ ] AC-2.b: Record components in correct order and types
- [ ] AC-2.c: `@JsonProperty("max_tokens")` on `maxTokens`
- [ ] AC-2.d: `ClaudeRequestTest` construction test passes
- [ ] AC-3.a: `Message.java` exists at correct path
- [ ] AC-3.b: Record components in correct order and types
- [ ] AC-3.c: `MessageTest` construction test passes
- [ ] AC-4.a: `ClaudeResponse.java` exists at correct path
- [ ] AC-4.b: Record components in correct order and types
- [ ] AC-4.c: `@JsonProperty("stop_reason")` on `stopReason`
- [ ] AC-4.d: `ClaudeResponseTest` construction test passes
- [ ] AC-5.a: `ContentBlock.java` exists at correct path
- [ ] AC-5.b: Record components in correct order and types
- [ ] AC-5.c: `ContentBlockTest` construction test passes
- [ ] AC-6.a: RED-phase proof captured in task-01 proof file
- [ ] AC-7.a: `./mvnw test` exits 0 with zero failures
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
