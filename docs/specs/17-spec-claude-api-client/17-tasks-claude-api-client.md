# Tasks: ClaudeApiClient Interface (17)

## Task 01 — Write failing construction tests for all four POJOs (RED)

Covers: AC-2.d, AC-3.c, AC-4.d, AC-5.c, AC-6.a

**May break compile, fixed by:** Task 02

- Create
  `src/test/java/org/springframework/samples/petclinic/owner/ClaudeRequestTest.java`:
  - `shouldStoreAllComponents()` — construct
    `new ClaudeRequest("claude-haiku-4-5-20251001", 1024, "system text", List.of(new Message("user", "hello")))`;
    assert `model()` equals `"claude-haiku-4-5-20251001"`, `maxTokens()` equals `1024`,
    `system()` equals `"system text"`, `messages()` has size 1 and the first element
    has `role()` `"user"` and `content()` `"hello"`.
- Create
  `src/test/java/org/springframework/samples/petclinic/owner/MessageTest.java`:
  - `shouldStoreRoleAndContent()` — construct `new Message("user", "hello")`;
    assert `role()` equals `"user"` and `content()` equals `"hello"`.
- Create
  `src/test/java/org/springframework/samples/petclinic/owner/ClaudeResponseTest.java`:
  - `shouldStoreAllComponents()` — construct
    `new ClaudeResponse("msg_01", "message", List.of(new ContentBlock("text", "summary text")), "end_turn")`;
    assert `id()` equals `"msg_01"`, `type()` equals `"message"`,
    `content()` has size 1 with first element having `type()` `"text"` and `text()` `"summary text"`,
    `stopReason()` equals `"end_turn"`.
- Create
  `src/test/java/org/springframework/samples/petclinic/owner/ContentBlockTest.java`:
  - `shouldStoreTypeAndText()` — construct `new ContentBlock("text", "hello")`;
    assert `type()` equals `"text"` and `text()` equals `"hello"`.
- Run `./mvnw test -Dtest="ClaudeRequestTest,MessageTest,ClaudeResponseTest,ContentBlockTest"`
  — expected to fail to compile because none of the production classes exist yet.
  Capture the compile error output in the proof file (this is the RED phase evidence).

**Proof:** 17-proofs/17-task-01-proofs.md

---

## Task 02 — Create `ClaudeApiClient` interface and all four POJO records (GREEN + REFACTOR)

Covers: AC-1.a, AC-1.b, AC-1.c, AC-1.d, AC-2.a, AC-2.b, AC-2.c, AC-2.d,
        AC-3.a, AC-3.b, AC-3.c, AC-4.a, AC-4.b, AC-4.c, AC-4.d,
        AC-5.a, AC-5.b, AC-5.c

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/Message.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  public record Message(String role, String content) {}
  ```

  No annotations needed — both field names match the JSON keys exactly.

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/ContentBlock.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  public record ContentBlock(String type, String text) {}
  ```

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/ClaudeRequest.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  import com.fasterxml.jackson.annotation.JsonProperty;
  import java.util.List;

  public record ClaudeRequest(
      String model,
      @JsonProperty("max_tokens") int maxTokens,
      String system,
      List<Message> messages
  ) {}
  ```

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/ClaudeResponse.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  import com.fasterxml.jackson.annotation.JsonProperty;
  import java.util.List;

  public record ClaudeResponse(
      String id,
      String type,
      List<ContentBlock> content,
      @JsonProperty("stop_reason") String stopReason
  ) {}
  ```

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  public interface ClaudeApiClient {
      String complete(String systemPrompt, String userMessage);
  }
  ```

  No Spring, JPA, or validation annotations.

- Run `./mvnw test -Dtest="ClaudeRequestTest,MessageTest,ClaudeResponseTest,ContentBlockTest"`
  — all four test classes must pass (GREEN).
- Run `./mvnw compile` — must exit 0 (AC-1.d).
- Run `grep "String complete(String systemPrompt, String userMessage)"
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java`
  — must print a match (AC-1.b).
- Run `grep "@Component\|@Service\|@Bean\|@Repository"
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java`
  — must return no output (AC-1.c).
- Run `grep '@JsonProperty("max_tokens")'
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeRequest.java`
  — must print a match (AC-2.c).
- Run `grep '@JsonProperty("stop_reason")'
  src/main/java/org/springframework/samples/petclinic/owner/ClaudeResponse.java`
  — must print a match (AC-4.c).
- Capture all command output in the proof file.

**Proof:** 17-proofs/17-task-02-proofs.md

---

## Task 03 — Validate and capture proof artifacts

Covers: all ACs

- Run `./mvnw test` from repository root and capture full output.
  Assert exit code 0 and zero failures (AC-7.a).
- Run all five `test -f` file-existence checks from the validation file and
  confirm each prints `EXISTS` (AC-1.a, AC-2.a, AC-3.a, AC-4.a, AC-5.a).
- Confirm proof files for Task 01 and Task 02 contain real command output
  (no placeholder text).
- Update the coverage matrix in `17-validation-claude-api-client.md` —
  transition all `PENDING` rows to `PASS`.
- Tick all Definition of Done checkboxes in `17-validation-claude-api-client.md`.
- Add spec-17 row to `docs/specs/README.md` (already done; verify status
  is updated from `draft` to reflect current state if transitioned).

**Proof:** 17-proofs/17-task-03-proofs.md
