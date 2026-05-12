# Questions: VisitSummaryIntegrationTest (22)

## Resolved

| # | Question | Answer |
|---|----------|--------|
| Q1 | How should the integration test wait for async completion? | Awaitility polling — call `visitRepository.findById(visitId)` every 200 ms up to a 5-second timeout using `await().atMost(5, SECONDS).pollInterval(200, MILLISECONDS).until(...)`. |
| Q2 | How should the failure path be tested? | Use `@MockitoBean ClaudeApiClient claudeApiClient` in a dedicated `VisitSummaryFailureIT` class, configured with `willThrow(new ClaudeApiException("rate limited"))`. This replaces the `ClaudeApiClientStub` bean for the entire test class's Spring context. |
| Q3 | Which database should back the integration tests? | H2 in-memory (default profile) — fast, no Docker required, consistent with `PetClinicIntegrationTests` and `VisitAiFieldsIT`. |
| Q4 | Should the integration test verify stub keyword routing (e.g. `"limp"` → URGENT)? | Yes — include `@ParameterizedTest shouldMapDescriptionKeywordToUrgency` with two cases: `"limp"` → `"urgent"` and `"checkup"` → `"routine"`. |
| Q5 | Single class or two classes for happy path vs failure path? | Two classes: `VisitSummaryHappyPathIT` (no mock, stub auto-activates) and `VisitSummaryFailureIT` (`@MockitoBean ClaudeApiClient`). A single class with `@MockitoBean` would require configuring valid JSON responses for happy-path tests, losing organic stub behavior and coupling test setup. |
| Q6 | Should tests be annotated with `@Transactional`? | No. The async generation commits its own transactions via `VisitSummaryTransactionSteps`. A `@Transactional` test would cause Hibernate's first-level cache to return stale data on subsequent `findById` calls within the same transaction, making Awaitility polling always see the original PENDING state. |
| Q7 | How to obtain the new visit ID after the form POST? | Snapshot visit IDs for pet 7 before the POST, then reload owner 6 from `ownerRepository.findById(6)` and diff — the ID not in the before-set is the new visit. |
| Q8 | Is Awaitility available on the test classpath without an explicit dependency? | Yes — `org.awaitility:awaitility:4.3.0` is in the local Maven repository as a transitive dependency of `spring-boot-starter-test` (bundled since Spring Boot 2.3). No additional `pom.xml` entry is required. |
| Q9 | Where is `ClaudeApiException` and can test classes access it? | `src/main/java/…/owner/ClaudeApiException.java` — package-private class in the `owner` package. Test classes in the matching package (`org.springframework.samples.petclinic.owner`) can access it. |
| Q10 | Which H2 sample data to use for owner and pet? | Owner ID 6 = Jean Coleman; Pet ID 7 = Samantha (type cat, owner 6). The same fixtures are used in `VisitAiFieldsIT`. |
| Q11 | `RANDOM_PORT` or `MOCK` web environment for `@SpringBootTest`? | `MOCK` + `@AutoConfigureMockMvc` — faster than `RANDOM_PORT`, enables `MockMvc` for the form POST, and avoids port allocation overhead. |

## Open

None.
