# Questions: Async Config + Anthropic Properties (13) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | Which Java package should `AsyncConfig.java` live in? | `org.springframework.samples.petclinic.system` — consistent with `CacheConfiguration.java` and `WebConfiguration.java` already in that package. |
| Q-2 | Should Anthropic properties be bound to a `@ConfigurationProperties` class in this spec? | No. The epic specifies plain `application.properties` entries for TASK-03. A typed binding class belongs in the Claude API client specs (TASK-09), not here. |
| Q-3 | Does adding `@EnableAsync` require a new Maven dependency? | No. `@EnableAsync` is in `spring-context`, which is already on the classpath via `spring-boot-starter-web`. |
| Q-4 | What thread name prefix should the executor use? | `visitSummary-` — conventional Spring naming (class-like prefix). |
| Q-5 | Is the `ANTHROPIC_API_KEY` env var guaranteed to be set in the test environment? | No. The property definition uses `${ANTHROPIC_API_KEY:}` (empty-string default), so the app must start cleanly without the env var. Tests must not require it to be set. |
| Q-6 | Should the executor bean be tested via a full `@SpringBootTest` or a focused `@TestConfiguration`? | Focused unit test: construct `AsyncConfig` directly, call `visitSummaryExecutor()`, assert the properties on the returned `ThreadPoolTaskExecutor`. Faster and more isolated than `@SpringBootTest`. |
| Q-7 | Where does the properties-load test live? | New `AsyncConfigPropertiesTest` under `src/test/java/.../system/`. Uses `@SpringBootTest` with `Environment` injection to assert the three properties are present. |

## Open

None.
