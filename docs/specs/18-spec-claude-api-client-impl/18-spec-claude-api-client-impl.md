---
status: delivered
created: 2026-05-11
last_amended: 2026-05-11
supersedes: ~
superseded_by: ~
---

# Spec: ClaudeApiClientImpl (18)

## Goal

`VisitSummaryService` (TASK-10) needs a real HTTP implementation of the
`ClaudeApiClient` interface that is active in production when
`ANTHROPIC_API_KEY` is set. This spec delivers `ClaudeApiClientImpl` — a
`@Component` that uses Spring `RestClient` to POST to the Anthropic Messages
API, sets the required headers, and extracts `content[0].text` from the
response — along with `ClaudeApiException`, the unchecked exception thrown on
transport or protocol errors. A `@ConditionalOnExpression` annotation
activates the impl only when `anthropic.api.key` is non-blank, ensuring the
stub (`ClaudeApiClientStub`, TASK-08) takes over in development and test
environments without a real key.

## Scope

### In scope

- `ClaudeApiException.java` — unchecked exception in
  `org.springframework.samples.petclinic.owner`:
  - `ClaudeApiException(String message)`
  - `ClaudeApiException(String message, Throwable cause)`
- `ClaudeApiClientImpl.java` — `@Component` in
  `org.springframework.samples.petclinic.owner`:
  - Activated by `@ConditionalOnExpression("not '${anthropic.api.key:}'.empty")`
  - Reads `anthropic.api.url`, `anthropic.api.key`, `anthropic.model` via
    `@Value`
  - Uses `RestClient` to POST to the configured URL
  - Sets headers: `x-api-key`, `anthropic-version: 2023-06-01`,
    `Content-Type: application/json`
  - Sends a `ClaudeRequest` body with model, `maxTokens=1024`, system prompt,
    and a single `Message("user", userMessage)`
  - Deserializes the response body as `ClaudeResponse`
  - Returns `content.get(0).text`
  - Throws `ClaudeApiException` when `content` is empty, on 4xx/5xx HTTP
    status, or on a transport exception
- `ClaudeApiExceptionTest.java` — unit test (TDD RED before GREEN)
- `ClaudeApiClientImplTest.java` — `@RestClientTest` test class using
  `MockRestServiceServer` covering five scenarios (TDD RED before GREEN)

### Out of scope

- `ClaudeApiClientStub` (TASK-08) — separate spec; uses
  `@ConditionalOnMissingBean(ClaudeApiClient.class)` as its fallback condition
- `VisitSummaryService` (TASK-10) — downstream consumer
- Timeout configuration — `RestClient` uses JDK `HttpClient` defaults;
  configurable timeouts deferred to amendment
- Retry logic — not in the epic; deferred
- Changes to `application.properties` — already delivered by spec-13

## Source excerpts

All referenced source is stable production code already in the repository.

- `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java`
  — the interface this spec implements (spec-17, delivered).
- `src/main/java/org/springframework/samples/petclinic/owner/ClaudeRequest.java`,
  `Message.java`, `ClaudeResponse.java`, `ContentBlock.java` — wire-format
  POJOs (spec-17, delivered).
- `src/main/resources/application.properties` lines 28–30 — the three
  `anthropic.*` properties this impl reads at startup.
- `src/main/java/org/springframework/samples/petclinic/system/AsyncConfig.java`
  — confirms `@Configuration` pattern used in the `system` package;
  `ClaudeApiClientImpl` lives in the `owner` package.
- `docs/epic-ai-visit-summary.md` TASK-09 — canonical feature description.

## Acceptance criteria

- **AC-1: `ClaudeApiException` exists and is well-formed**
  - AC-1.a: `ClaudeApiException.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiException.java`.
  - AC-1.b: The class extends `RuntimeException` — verified by
    `grep "extends RuntimeException" src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiException.java`
    returning a match.
  - AC-1.c: `ClaudeApiExceptionTest` asserts that the single-arg constructor
    sets the message and the two-arg constructor sets both message and cause.

- **AC-2: `ClaudeApiClientImpl` class structure**
  - AC-2.a: `ClaudeApiClientImpl.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java`.
  - AC-2.b: The class implements `ClaudeApiClient` — verified by
    `grep "implements ClaudeApiClient" src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java`
    returning a match.
  - AC-2.c: The class carries `@ConditionalOnExpression("not '${anthropic.api.key:}'.empty")` —
    verified by
    `grep "ConditionalOnExpression" src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java`
    returning a match.
  - AC-2.d: `./mvnw compile` exits 0.

- **AC-3: HTTP request shape**
  - AC-3.a: Test `completeSendsCorrectRequestAndReturnsText` verifies that
    the impl POSTs to the configured URL with an `x-api-key` header equal to
    the configured key value.
  - AC-3.b: Test verifies the `anthropic-version: 2023-06-01` header is set.
  - AC-3.c: Test verifies the `Content-Type: application/json` header is set.
  - AC-3.d: Test verifies the request body deserializes to a `ClaudeRequest`
    with the correct `model`, `maxTokens=1024`, `system` prompt, and a single
    `Message("user", userMessage)`.

- **AC-4: Response extraction**
  - AC-4.a: Test `completeSendsCorrectRequestAndReturnsText` asserts that
    `complete()` returns the `text` value from `content[0]` of the response.
  - AC-4.b: Test `completeThrowsWhenContentIsEmpty` asserts that `complete()`
    throws `ClaudeApiException` when the `ClaudeResponse` has an empty
    `content` list.

- **AC-5: HTTP error handling**
  - AC-5.a: Test `completeThrowsOnRateLimitResponse` asserts that `complete()`
    throws `ClaudeApiException` when the server returns HTTP 429.
  - AC-5.b: Test `completeThrowsOnServiceUnavailableResponse` asserts that
    `complete()` throws `ClaudeApiException` when the server returns HTTP 503.

- **AC-6: TDD compliance**
  - AC-6.a: Proof artifact for Task 01 captures Maven output showing
    `ClaudeApiExceptionTest` failing to compile (RED phase) before
    `ClaudeApiException` exists.
  - AC-6.b: Proof artifact for Task 03 captures Maven output showing
    `ClaudeApiClientImplTest` failing to compile (RED phase) before
    `ClaudeApiClientImpl` exists.

- **AC-7: Existing test suite remains green**
  - AC-7.a: `./mvnw test` exits 0 with zero failures after all changes are
    applied.
  - AC-7.b: New classes have ≥90% line coverage per JaCoCo report.

## Conventions

- Both new types are top-level files in
  `org.springframework.samples.petclinic.owner`. No inner classes.
- `ClaudeApiClientImpl` is package-private (no `public` modifier); callers
  use the `ClaudeApiClient` interface.
- `RestClient` is built via `RestClient.builder()` injected through the
  constructor; the `RestClient` instance is created once and held as a field.
- `@Value("${anthropic.api.url}")`, `@Value("${anthropic.api.key}")`,
  `@Value("${anthropic.model}")` are constructor parameters.
- `ClaudeApiException` wraps `RestClientException` as the cause on transport
  errors; the status-code error case includes the HTTP status in the message.
- Tests use `@RestClientTest(ClaudeApiClientImpl.class)` with
  `MockRestServiceServer` (from `spring-boot-starter-restclient-test`, already
  in `pom.xml`). No WireMock dependency is added.
- TDD is mandatory: `ClaudeApiExceptionTest` must be written and confirmed
  failing (RED) before `ClaudeApiException` is created; `ClaudeApiClientImplTest`
  must be written and confirmed failing (RED) before `ClaudeApiClientImpl` is
  created.
- Downstream specs (`ClaudeApiClientStub` TASK-08, `VisitSummaryService`
  TASK-10) must not begin until this spec is `delivered`.
- Upstream contract (interface + POJOs from spec-17) must not be modified by
  this spec.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
