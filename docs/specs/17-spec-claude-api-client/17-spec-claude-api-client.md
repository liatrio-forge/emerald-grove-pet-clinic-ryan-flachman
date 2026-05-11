---
status: accepted
created: 2026-05-11
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: ClaudeApiClient Interface (17)

## Goal

`VisitSummaryService` (TASK-10) must call the Anthropic Messages API without
being coupled to HTTP transport details. This spec introduces the
`ClaudeApiClient` interface — a single-method contract that accepts a system
prompt and a user message and returns Claude's raw text response as a `String`
— along with the four JSON transport POJOs (`ClaudeRequest`, `Message`,
`ClaudeResponse`, `ContentBlock`) that map the Anthropic Messages API wire
format. Having these types in place unblocks the stub implementation (TASK-08)
and the real HTTP implementation (TASK-09), which use `@ConditionalOnProperty`
to select between each other based on whether `anthropic.api.key` is set.

## Scope

### In scope

- `ClaudeApiClient.java` — `public interface` in
  `org.springframework.samples.petclinic.owner` with exactly one method:
  `String complete(String systemPrompt, String userMessage)`.
- `ClaudeRequest.java` — Java `record` with components:
  `String model`, `int maxTokens` (`@JsonProperty("max_tokens")`),
  `String system`, `List<Message> messages`.
- `Message.java` — Java `record` with components: `String role`, `String content`.
- `ClaudeResponse.java` — Java `record` with components:
  `String id`, `String type`, `List<ContentBlock> content`,
  `String stopReason` (`@JsonProperty("stop_reason")`).
- `ContentBlock.java` — Java `record` with components: `String type`, `String text`.
- Unit tests for all four POJOs following strict TDD (RED before GREEN).

### Out of scope

- `ClaudeApiClientStub` — TASK-08 (next spec).
- `ClaudeApiClientImpl` — TASK-09.
- `ClaudeApiException` — belongs with the concrete implementations.
- `VisitSummaryService`, `VisitSummaryParser` — downstream consumers.
- A `Usage` POJO — not needed until a consumer reads token counts.
- Any `@ConditionalOnProperty` wiring — that is the implementation's job.
- Changes to `application.properties` — delivered by spec-13 (async-config).

## Source excerpts

All referenced source is stable production code already in the repository.

- `src/main/java/org/springframework/samples/petclinic/owner/PromptRequest.java`
  — sibling record (spec-16, delivered); `ClaudeRequest` and friends follow
  the same package and `record` style conventions.
- `src/main/java/org/springframework/samples/petclinic/owner/AiStatus.java`
  — sibling enum (spec-14, delivered); confirms the package is the canonical
  home for AI-pipeline types.
- `docs/epic-ai-visit-summary.md` TASK-07 — canonical description of the
  interface contract and POJO field inventory.

## Contract

This is a contract spec. The interface and POJOs defined here are the
source of truth for TASK-08, TASK-09, and TASK-10. Downstream specs must
not redefine these shapes.

```java
// ClaudeApiClient.java
public interface ClaudeApiClient {
    String complete(String systemPrompt, String userMessage);
}

// ClaudeRequest.java
public record ClaudeRequest(
    String model,
    @JsonProperty("max_tokens") int maxTokens,
    String system,
    List<Message> messages
) {}

// Message.java
public record Message(String role, String content) {}

// ClaudeResponse.java
public record ClaudeResponse(
    String id,
    String type,
    List<ContentBlock> content,
    @JsonProperty("stop_reason") String stopReason
) {}

// ContentBlock.java
public record ContentBlock(String type, String text) {}
```

All five types live in `org.springframework.samples.petclinic.owner`.

## Acceptance criteria

- **AC-1: `ClaudeApiClient` interface**
  - AC-1.a: `ClaudeApiClient.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java`.
  - AC-1.b: The interface declares exactly one method with signature
    `String complete(String systemPrompt, String userMessage)` — verified by
    `grep "String complete(String systemPrompt, String userMessage)"
    src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java`
    returning a match.
  - AC-1.c: The interface carries no Spring annotations (`@Component`,
    `@Service`, `@Bean`, `@Repository`) — verified by `grep
    "@Component\|@Service\|@Bean\|@Repository"
    src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java`
    returning no output.
  - AC-1.d: `./mvnw compile` exits 0.

- **AC-2: `ClaudeRequest` record**
  - AC-2.a: `ClaudeRequest.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/ClaudeRequest.java`.
  - AC-2.b: Record is declared with components in order:
    `String model`, `int maxTokens`, `String system`, `List<Message> messages`.
  - AC-2.c: The `maxTokens` component is annotated
    `@JsonProperty("max_tokens")`.
  - AC-2.d: `ClaudeRequestTest` asserts that constructing a `ClaudeRequest`
    with (`"claude-haiku-4-5-20251001"`, `1024`, `"system text"`,
    `List.of(new Message("user", "hello"))`) returns the same values via
    `model()`, `maxTokens()`, `system()`, `messages()`.

- **AC-3: `Message` record**
  - AC-3.a: `Message.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/Message.java`.
  - AC-3.b: Record is declared with components in order:
    `String role`, `String content`.
  - AC-3.c: `MessageTest` asserts that constructing a `Message` with
    (`"user"`, `"hello"`) returns `"user"` from `role()` and `"hello"` from
    `content()`.

- **AC-4: `ClaudeResponse` record**
  - AC-4.a: `ClaudeResponse.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/ClaudeResponse.java`.
  - AC-4.b: Record is declared with components in order:
    `String id`, `String type`, `List<ContentBlock> content`,
    `String stopReason`.
  - AC-4.c: The `stopReason` component is annotated
    `@JsonProperty("stop_reason")`.
  - AC-4.d: `ClaudeResponseTest` asserts that constructing a `ClaudeResponse`
    with (`"msg_01"`, `"message"`,
    `List.of(new ContentBlock("text", "summary text"))`, `"end_turn"`) returns
    the same values via `id()`, `type()`, `content()`, `stopReason()`.

- **AC-5: `ContentBlock` record**
  - AC-5.a: `ContentBlock.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/ContentBlock.java`.
  - AC-5.b: Record is declared with components in order:
    `String type`, `String text`.
  - AC-5.c: `ContentBlockTest` asserts that constructing a `ContentBlock`
    with (`"text"`, `"hello"`) returns `"text"` from `type()` and `"hello"`
    from `text()`.

- **AC-6: TDD compliance**
  - AC-6.a: Proof artifact for Task 01 captures Maven output showing all
    four POJO tests failing to compile (RED phase) before any production
    code is written.

- **AC-7: Existing test suite remains green**
  - AC-7.a: `./mvnw test` exits 0 with zero failures after all changes are
    applied.

## Conventions

- All five types are top-level files in
  `org.springframework.samples.petclinic.owner`. No inner classes.
- Use Java `record` for the four POJOs; `interface` for `ClaudeApiClient`.
- `@JsonProperty` is from `com.fasterxml.jackson.annotation.JsonProperty`,
  already on the classpath via `spring-boot-starter-web`.
- No Spring, JPA, or validation annotations on any of the five types.
- `List<Message>` in `ClaudeRequest` and `List<ContentBlock>` in
  `ClaudeResponse` should use `java.util.List`.
- TDD is mandatory: all four POJO test classes must be written and confirmed
  failing (RED) before any production class is created.
- Blocks TASK-08 (`ClaudeApiClientStub`) and TASK-09 (`ClaudeApiClientImpl`)
  — those specs must not begin until this spec is `delivered`.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
