---
status: in_progress
created: 2026-05-11
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Async Config + Anthropic Properties (13)

## Goal

The AI Visit Notes Summarizer epic requires two configuration primitives before
any service code can be written: the Anthropic API connection properties
consumed by `ClaudeApiClientImpl` (TASK-09), and an async thread pool that
decouples visit-save latency from Claude API call time (TASK-10). This spec
delivers both as a single configuration unit with no external dependencies,
allowing TASK-09 and TASK-10 to proceed in parallel once this spec is
delivered.

## Scope

### In scope

- Add three Anthropic API properties to `src/main/resources/application.properties`:
  - `anthropic.api.key=${ANTHROPIC_API_KEY:}` (empty-string default when env var absent)
  - `anthropic.api.url=https://api.anthropic.com/v1/messages`
  - `anthropic.model=claude-haiku-4-5-20251001`
- Create `AsyncConfig.java` in the `system` package with `@Configuration @EnableAsync`
  and a `visitSummaryExecutor` `ThreadPoolTaskExecutor` bean (core=2, max=5,
  queue=25, rejection policy=`CallerRunsPolicy`, thread name prefix=`visitSummary-`).
- Unit tests for `AsyncConfig.java` verifying all five executor properties (TDD RED
  before implementation).
- A `@SpringBootTest` properties-load test verifying all three Anthropic keys are
  registered in the `Environment` (TDD RED before adding properties).

### Out of scope

- A `@ConfigurationProperties` binding class for Anthropic properties — that belongs
  in TASK-09 (`ClaudeApiClientImpl`).
- Any `ClaudeApiClient` interface, stub, or implementation.
- The `VisitSummaryService` or its `@Async` call site.
- Changes to profile-specific property files (`application-mysql.properties`,
  `application-postgres.properties`).
- Integration tests involving the Claude API network.

## Source excerpts

Source files are stable production assets; no freeze required.

- `src/main/resources/application.properties` — current property set; Anthropic
  keys are absent and must be added.
- `src/main/java/.../system/CacheConfiguration.java` — reference for `@Configuration`
  placement and package conventions in the `system` package.

## Acceptance criteria

- **AC-1: Anthropic properties present and parseable**
  - AC-1.a: The literal key `anthropic.api.key` with value `${ANTHROPIC_API_KEY:}`
    appears in `src/main/resources/application.properties`.
  - AC-1.b: The literal key `anthropic.api.url` with value
    `https://api.anthropic.com/v1/messages` appears in `application.properties`.
  - AC-1.c: The literal key `anthropic.model` with value
    `claude-haiku-4-5-20251001` appears in `application.properties`.
  - AC-1.d: `AsyncConfigPropertiesTest` (a `@SpringBootTest`) passes, confirming
    that `Environment.containsProperty("anthropic.api.key")`,
    `containsProperty("anthropic.api.url")`, and
    `containsProperty("anthropic.model")` all return `true` when the app starts
    without the `ANTHROPIC_API_KEY` env var set.

- **AC-2: AsyncConfig executor bean correctly configured**
  - AC-2.a: `AsyncConfig.java` exists at
    `src/main/java/org/springframework/samples/petclinic/system/AsyncConfig.java`
    and carries `@Configuration` and `@EnableAsync`.
  - AC-2.b: A bean method named `visitSummaryExecutor` returns a
    `ThreadPoolTaskExecutor`.
  - AC-2.c: `AsyncConfigTest` asserts `corePoolSize == 2`.
  - AC-2.d: `AsyncConfigTest` asserts `maxPoolSize == 5`.
  - AC-2.e: `AsyncConfigTest` asserts `queueCapacity == 25`.
  - AC-2.f: `AsyncConfigTest` asserts the rejection handler is an instance of
    `java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy`.
  - AC-2.g: `AsyncConfigTest` asserts thread name prefix is `visitSummary-`.

- **AC-3: Existing test suite remains green**
  - AC-3.a: `./mvnw test` exits 0 with no test failures after all changes are applied.

## Conventions

- `AsyncConfig.java` lives in `org.springframework.samples.petclinic.system` —
  consistent with `CacheConfiguration.java` and `WebConfiguration.java`.
- TDD is mandatory: `AsyncConfigTest` must be written and confirmed failing before
  `AsyncConfig.java` is created; `AsyncConfigPropertiesTest` must be written and
  confirmed failing before the properties are added.
- The `anthropic.api.key` property intentionally defaults to an empty string so the
  app starts without a real key; `ClaudeApiClientStub` (TASK-08) uses
  `@ConditionalOnProperty` to activate when the key is blank.
- Do not use `@SpringBootTest` for the executor unit test — construct `AsyncConfig`
  directly and call `visitSummaryExecutor()` to keep the test fast and isolated.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
