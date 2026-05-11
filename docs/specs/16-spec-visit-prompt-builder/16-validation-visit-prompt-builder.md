# Validation: VisitPromptBuilder (16)

## Automated verification

From repository root:

```bash
# AC-2.d, AC-7.a — compile and full test suite
./mvnw compile
./mvnw test

# AC-2.b — no Spring annotations on VisitPromptBuilder
grep -r "@Component\|@Service\|@Bean" \
  src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java

# AC-1.a — PromptRequest file exists
test -f src/main/java/org/springframework/samples/petclinic/owner/PromptRequest.java \
  && echo "FOUND" || echo "MISSING"

# AC-2.a — VisitPromptBuilder file exists
test -f src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java \
  && echo "FOUND" || echo "MISSING"

# AC-1.b — PromptRequest is a record
grep "^public record PromptRequest" \
  src/main/java/org/springframework/samples/petclinic/owner/PromptRequest.java

# AC-2.c — build method signature
grep "public static PromptRequest build" \
  src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java

# AC-6.a — proof of RED phase captured in task-01 proofs
test -f docs/specs/16-spec-visit-prompt-builder/16-proofs/16-task-01-proofs.md \
  && echo "FOUND" || echo "MISSING"
```

**Expected:**

| Command | Expected output |
|---------|----------------|
| `./mvnw compile` | `BUILD SUCCESS` |
| `./mvnw test` | `BUILD SUCCESS`, 0 failures, `PromptRequestTest` and `VisitPromptBuilderTest` in the passing test list |
| Spring annotation grep | *(no output)* |
| PromptRequest file exists | `FOUND` |
| VisitPromptBuilder file exists | `FOUND` |
| record declaration grep | `public record PromptRequest(String systemPrompt, String userMessage)` |
| build method grep | `public static PromptRequest build(Visit visit, Pet pet)` |
| task-01 proofs file exists | `FOUND` |

## Traceability

- Feature spec: `16-spec-visit-prompt-builder.md`
- Task breakdown: `16-tasks-visit-prompt-builder.md`
- Questions and decisions: `16-questions-1-visit-prompt-builder.md`
- Per-task evidence:
  - `16-proofs/16-task-01-proofs.md`
  - `16-proofs/16-task-02-proofs.md`
  - `16-proofs/16-task-03-proofs.md`
  - `16-proofs/16-task-04-proofs.md`
- Upstream specs:
  - `14-spec-visit-ai-fields` — `AiStatus`, `Visit` AI fields (delivered)
  - `15-spec-visit-summary-dto` — `VisitUrgency`, `VisitSummary` (delivered)
- Parent epic: `docs/epic-ai-visit-summary.md` TASK-05

## Manual checks

None — all acceptance criteria are verifiable via command output or file
inspection.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `PromptRequest.java` exists at expected path | `16-proofs/16-task-02-proofs.md` | file creation | PASS |
| AC-1.b | `PromptRequest` is a `record` with two components `String systemPrompt`, `String userMessage` | `16-proofs/16-task-02-proofs.md` | file edit | PASS |
| AC-1.c | `PromptRequest` is in package `org.springframework.samples.petclinic.owner` | `16-proofs/16-task-02-proofs.md` | file edit | PASS |
| AC-1.d | `PromptRequestTest` passes accessors test | `16-proofs/16-task-02-proofs.md` | Maven test pass | PASS |
| AC-2.a | `VisitPromptBuilder.java` exists at expected path | `16-proofs/16-task-03-proofs.md` | file creation | PASS |
| AC-2.b | No Spring annotations on `VisitPromptBuilder` | `16-proofs/16-task-03-proofs.md` | command output | PASS |
| AC-2.c | `public static PromptRequest build(Visit, Pet)` exists | `16-proofs/16-task-03-proofs.md` | file edit | PASS |
| AC-2.d | `./mvnw compile` exits 0 | `16-proofs/16-task-03-proofs.md` | command output | PASS |
| AC-3.a | System prompt is non-null and non-blank | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-3.b | System prompt contains "summary", "tags", "urgency", "followUp" substrings | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-3.c | System prompt contains "json" substring | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-4.a | User message contains pet name | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-4.b | User message contains pet type name | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-4.c | User message contains correct whole-year age | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-4.d | User message contains visit date as yyyy-MM-dd | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-4.e | User message contains visit description | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-5.a | Null birthDate produces "unknown" in age position | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-5.b | Null description produces "(no description provided)" | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-5.c | Blank description produces "(no description provided)" | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-5.d | Null pet type produces "unknown" in type position | `16-proofs/16-task-03-proofs.md` | Maven test pass | PASS |
| AC-6.a | Task-01 proof captures RED-phase Maven failure output | `16-proofs/16-task-01-proofs.md` | command output | PASS |
| AC-7.a | `./mvnw test` exits 0, zero failures | `16-proofs/16-task-04-proofs.md` | command output | PASS |

## Definition of done

- [x] AC-1.a: `PromptRequest.java` exists at expected path.
- [x] AC-1.b: `PromptRequest` is a `record` with components `String systemPrompt`, `String userMessage`.
- [x] AC-1.c: `PromptRequest` is in package `org.springframework.samples.petclinic.owner`.
- [x] AC-1.d: `PromptRequestTest` passes accessors test.
- [x] AC-2.a: `VisitPromptBuilder.java` exists at expected path.
- [x] AC-2.b: No Spring annotations on `VisitPromptBuilder`.
- [x] AC-2.c: `public static PromptRequest build(Visit, Pet)` exists.
- [x] AC-2.d: `./mvnw compile` exits 0.
- [x] AC-3.a: System prompt is non-null and non-blank.
- [x] AC-3.b: System prompt contains "summary", "tags", "urgency", "followUp".
- [x] AC-3.c: System prompt contains "json".
- [x] AC-4.a: User message contains pet name.
- [x] AC-4.b: User message contains pet type name.
- [x] AC-4.c: User message contains correct whole-year age.
- [x] AC-4.d: User message contains visit date as `yyyy-MM-dd`.
- [x] AC-4.e: User message contains visit description.
- [x] AC-5.a: Null birthDate → "unknown" in age position, no exception.
- [x] AC-5.b: Null description → "(no description provided)", no exception.
- [x] AC-5.c: Blank description → "(no description provided)", no exception.
- [x] AC-5.d: Null pet type → "unknown" in type position, no exception.
- [x] AC-6.a: Task-01 proof file contains RED-phase failure output.
- [x] AC-7.a: `./mvnw test` exits 0 with zero failures.
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
