# Tasks: Async Config + Anthropic Properties (13)

## Task 01 — Write failing `AsyncConfigTest` (RED)

Covers: AC-2.c, AC-2.d, AC-2.e, AC-2.f, AC-2.g

- Create `src/test/java/org/springframework/samples/petclinic/system/AsyncConfigTest.java`.
- Annotate with `@ExtendWith(MockitoExtension.class)` (no Spring context needed).
- In the test body, instantiate `new AsyncConfig()`, call `visitSummaryExecutor()`,
  and initialize the executor via `executor.initialize()`.
- Assert `executor.getCorePoolSize() == 2`.
- Assert `executor.getMaxPoolSize() == 5`.
- Assert `executor.getQueueCapacity() == 25`.
- Assert the underlying `ThreadPoolExecutor`'s rejected-execution handler is an
  instance of `java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy` (obtain via
  `((ThreadPoolExecutor) executor.getThreadPoolExecutor()).getRejectedExecutionHandler()`).
- Assert `executor.getThreadNamePrefix().equals("visitSummary-")`.
- Confirm the test fails to compile (class not found) before Task 02 is done.

**Proof:** 13-proofs/13-task-01-proofs.md

## Task 02 — Implement `AsyncConfig.java` (GREEN)

Covers: AC-2.a, AC-2.b

- Create `src/main/java/org/springframework/samples/petclinic/system/AsyncConfig.java`.
- Annotate the class with `@Configuration` and `@EnableAsync`.
- Define a `@Bean` method `visitSummaryExecutor()` returning `ThreadPoolTaskExecutor`.
- Set `corePoolSize = 2`, `maxPoolSize = 5`, `queueCapacity = 25`.
- Set `rejectionPolicy` to a new `CallerRunsPolicy()`.
- Set `threadNamePrefix = "visitSummary-"`.
- Call `executor.initialize()` before returning.
- Run `AsyncConfigTest` — all assertions must pass (GREEN).

**Proof:** 13-proofs/13-task-02-proofs.md

## Task 03 — Write failing `AsyncConfigPropertiesTest` (RED)

Covers: AC-1.d

- Create `src/test/java/org/springframework/samples/petclinic/system/AsyncConfigPropertiesTest.java`.
- Annotate with `@SpringBootTest` and `@ActiveProfiles` (default profile is fine).
- Inject `org.springframework.core.env.Environment`.
- Assert `environment.containsProperty("anthropic.api.key")` returns `true`.
- Assert `environment.containsProperty("anthropic.api.url")` returns `true`.
- Assert `environment.containsProperty("anthropic.model")` returns `true`.
- Confirm the test fails (assertions return `false`) before Task 04 is done.

**Proof:** 13-proofs/13-task-03-proofs.md

## Task 04 — Add Anthropic properties to `application.properties` (GREEN)

Covers: AC-1.a, AC-1.b, AC-1.c

- Open `src/main/resources/application.properties`.
- Append a clearly labelled `# Anthropic / Claude API` section with exactly:

  ```properties
  anthropic.api.key=${ANTHROPIC_API_KEY:}
  anthropic.api.url=https://api.anthropic.com/v1/messages
  anthropic.model=claude-haiku-4-5-20251001
  ```

- Run `AsyncConfigPropertiesTest` — all three assertions must pass (GREEN).

**Proof:** 13-proofs/13-task-04-proofs.md

## Task 05 — Validate and capture proof artifacts

Covers: AC-3.a, all

- Run `./mvnw test` and capture full output.
- Confirm exit code 0 and no test failures.
- Confirm each AC ID has at least one passing proof artifact by reviewing the
  coverage matrix in `13-validation-async-config.md`.
- Fill all proof files with real command output (no placeholders).

**Proof:** 13-proofs/13-task-05-proofs.md
