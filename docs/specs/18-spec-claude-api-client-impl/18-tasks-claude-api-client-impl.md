# Tasks: ClaudeApiClientImpl (18)

## Task 01 — Write failing ClaudeApiExceptionTest (RED)

Covers: AC-6.a

- Create `src/test/java/org/springframework/samples/petclinic/owner/ClaudeApiExceptionTest.java`.
- Annotate with `@DisabledInNativeImage` and `@DisabledInAotMode` (consistent with other tests in the package).
- Write test `singleArgConstructorSetsMessage`: construct `new ClaudeApiException("boom")` and assert `getMessage()` equals `"boom"`.
- Write test `twoArgConstructorSetsMessageAndCause`: construct `new ClaudeApiException("boom", new RuntimeException("cause"))` and assert `getMessage()` equals `"boom"` and `getCause().getMessage()` equals `"cause"`.
- Run `./mvnw test -Dtest=ClaudeApiExceptionTest` and capture the compile failure output proving RED phase.

**May break compile, fixed by:** Task 02

**Proof:** 18-proofs/18-task-01-proofs.md

---

## Task 02 — Implement ClaudeApiException (GREEN)

Covers: AC-1.a, AC-1.b, AC-1.c

- Create `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiException.java`.
- Declare `class ClaudeApiException extends RuntimeException` (package-private).
- Add constructor `ClaudeApiException(String message)` delegating to `super(message)`.
- Add constructor `ClaudeApiException(String message, Throwable cause)` delegating to `super(message, cause)`.
- Run `./mvnw test -Dtest=ClaudeApiExceptionTest` and confirm both tests pass (GREEN).
- Capture file listing and grep output confirming `extends RuntimeException`.

**Proof:** 18-proofs/18-task-02-proofs.md

---

## Task 03 — Write failing ClaudeApiClientImplTest (RED)

Covers: AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-4.a, AC-4.b, AC-5.a, AC-5.b, AC-6.b

- Create `src/test/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImplTest.java`.
- Annotate the class with `@RestClientTest(ClaudeApiClientImpl.class)`, `@DisabledInNativeImage`, `@DisabledInAotMode`.
- Inject `MockRestServiceServer server` and `ClaudeApiClientImpl client` via `@Autowired`.
- Add `@TestPropertySource(properties = {"anthropic.api.key=test-key", "anthropic.api.url=http://localhost/v1/messages", "anthropic.model=claude-haiku-4-5-20251001"})` to supply the three required properties.
- Write test `completeSendsCorrectRequestAndReturnsText`:
  - Expect one POST request to `/v1/messages`.
  - Verify headers: `x-api-key: test-key`, `anthropic-version: 2023-06-01`, `Content-Type: application/json`.
  - Verify the request body deserializes to a `ClaudeRequest` with `model="claude-haiku-4-5-20251001"`, `maxTokens=1024`, `system="sys"`, and `messages=[Message("user","hello")]`.
  - Respond with HTTP 200 and a `ClaudeResponse` JSON body containing `content: [{"type":"text","text":"summary result"}]`.
  - Assert `client.complete("sys", "hello")` returns `"summary result"`.
- Write test `completeThrowsWhenContentIsEmpty`:
  - Respond with HTTP 200 and a `ClaudeResponse` JSON body where `content` is an empty array.
  - Assert `client.complete("sys", "hello")` throws `ClaudeApiException`.
- Write test `completeThrowsOnRateLimitResponse`:
  - Respond with HTTP 429.
  - Assert `client.complete("sys", "hello")` throws `ClaudeApiException`.
- Write test `completeThrowsOnServiceUnavailableResponse`:
  - Respond with HTTP 503.
  - Assert `client.complete("sys", "hello")` throws `ClaudeApiException`.
- Run `./mvnw test -Dtest=ClaudeApiClientImplTest` and capture the compile failure output proving RED phase.

**May break compile, fixed by:** Task 04

**Proof:** 18-proofs/18-task-03-proofs.md

---

## Task 04 — Implement ClaudeApiClientImpl (GREEN)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-4.a, AC-4.b, AC-5.a, AC-5.b

- Create `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientImpl.java`.
- Annotate the class with `@Component` and
  `@ConditionalOnExpression("not '${anthropic.api.key:}'.empty")`.
- Add a constructor with parameters:
  - `RestClient.Builder restClientBuilder`
  - `@Value("${anthropic.api.url}") String apiUrl`
  - `@Value("${anthropic.api.key}") String apiKey`
  - `@Value("${anthropic.model}") String model`
- In the constructor, build and store a `RestClient` instance using the builder,
  setting the base URL to `apiUrl` and default headers:
  `x-api-key: <apiKey>`, `anthropic-version: 2023-06-01`,
  `Content-Type: application/json`.
- Store `apiKey` and `model` as fields.
- Implement `String complete(String systemPrompt, String userMessage)`:
  1. Build a `ClaudeRequest` with `model`, `maxTokens=1024`, `systemPrompt`,
     and `List.of(new Message("user", userMessage))`.
  2. POST to `""` (path relative to base URL) with the request body.
  3. Deserialize the response as `ClaudeResponse`.
  4. If `response.content()` is empty, throw `new ClaudeApiException("Claude returned empty content")`.
  5. Return `response.content().get(0).text()`.
  6. Catch `RestClientResponseException` (4xx/5xx) and rethrow as
     `new ClaudeApiException("Claude API error: HTTP " + ex.getStatusCode(), ex)`.
  7. Catch `RestClientException` (transport/network) and rethrow as
     `new ClaudeApiException("Claude API transport error: " + ex.getMessage(), ex)`.
- Run `./mvnw test -Dtest=ClaudeApiClientImplTest` and confirm all four tests pass (GREEN).
- Run `./mvnw compile` and confirm exit 0.
- Capture grep outputs for `implements ClaudeApiClient` and `ConditionalOnExpression`.

**Proof:** 18-proofs/18-task-04-proofs.md

---

## Task 05 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture the full output confirming zero failures across the entire test suite.
- Run `./mvnw test jacoco:report` and capture the JaCoCo coverage excerpt for `ClaudeApiException` and `ClaudeApiClientImpl` confirming ≥90% line coverage.
- Confirm every AC ID in the coverage matrix in `18-validation-claude-api-client-impl.md` maps to a proof artifact with real (non-placeholder) output.
- Update all rows in the coverage matrix from `PENDING` to `PASS`.
- Update the spec front-matter `status` from `draft` to `proposed` (or `accepted` if reviewer approves immediately).

**Proof:** 18-proofs/18-task-05-proofs.md
