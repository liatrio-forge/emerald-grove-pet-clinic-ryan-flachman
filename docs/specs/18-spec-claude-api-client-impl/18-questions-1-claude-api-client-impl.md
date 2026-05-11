# Questions: ClaudeApiClientImpl (18)

## Resolved

| # | Question | Decision | Rationale |
|---|----------|----------|-----------|
| Q-1 | Testing framework: WireMock (as specified in epic) or `@RestClientTest` with `MockRestServiceServer`? | **`@RestClientTest` + `MockRestServiceServer`** | `spring-boot-starter-restclient-test` is already a test dependency. Adding WireMock would be a new dependency for the same test scenarios. All five error paths (200 valid, 200 empty content, 429, 503, network error) are expressible with `MockRestServiceServer`. |
| Q-2 | How to conditionally activate the impl only when `anthropic.api.key` is genuinely non-blank? | **`@ConditionalOnExpression("not '${anthropic.api.key:}'.empty")`** | `anthropic.api.key` defaults to `""` (from spec-13). `@ConditionalOnProperty` activates even for empty strings; the SpEL expression explicitly checks emptiness. The stub (TASK-08) uses `@ConditionalOnMissingBean(ClaudeApiClient.class)` as its fallback condition. |
| Q-3 | What value to use for `max_tokens` in the `ClaudeRequest`? | **1024** | Not specified in the epic. Adequate for the 4-field JSON summary (`summary`, `tags`, `urgency`, `followUp`). Can be made a property by amendment if tuning is needed. |
| Q-4 | What value for the `anthropic-version` header? | **`2023-06-01`** | Current stable value documented in the Anthropic API. Not configurable via property — baking it in is correct; a header change tracks an API migration, not a deployment setting. |
| Q-5 | Package location for `ClaudeApiException`? | **`org.springframework.samples.petclinic.owner`** | Co-located with `ClaudeApiClient` interface and the POJOs from spec-17. Consistent with all existing AI domain types. |
| Q-6 | What `ClaudeApiException` constructors are needed? | **`ClaudeApiException(String message)` and `ClaudeApiException(String message, Throwable cause)`** | The two-arg form covers wrapping `RestClientException` on network errors. No other forms needed. |
| Q-7 | What user `Message` role string to send? | **`"user"`** | The Anthropic Messages API uses `"user"` / `"assistant"` role names. The `system` prompt is sent as the top-level `system` field of `ClaudeRequest`, not as a message. |
| Q-8 | Should `ClaudeApiClientImpl` be package-private or public? | **`@Component` class; visibility follows the project pattern for service beans** | Looking at `AsyncConfig` (package-private) and other beans in the codebase; `ClaudeApiClientImpl` should be `class` (package-private) annotated `@Component` since external callers use the `ClaudeApiClient` interface. |
| Q-9 | Timeout configuration: should connect/read timeouts be configurable? | **Out of scope for this spec** | The epic does not specify timeout values. `RestClient` will use the default JDK `HttpClient` timeouts. Timeout configuration can be added by amendment if needed. |

## Open

None.
