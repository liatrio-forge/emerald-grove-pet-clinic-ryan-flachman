# Proofs: Task 02 — Implement `AsyncConfig.java` (GREEN)

Covers: AC-2.a, AC-2.b

## Planned evidence

- `AsyncConfig.java` file listing showing `@Configuration`, `@EnableAsync`, and the
  `visitSummaryExecutor()` bean method.
- `AsyncConfigTest` passing output from `./mvnw test -Dtest=AsyncConfigTest`.

## Completion notes

### AC-2.a: `AsyncConfig.java` exists in `system` package with `@Configuration` and `@EnableAsync`

File created: `src/main/java/org/springframework/samples/petclinic/system/AsyncConfig.java`

Relevant excerpt:

```java
@Configuration
@EnableAsync
class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor visitSummaryExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("visitSummary-");
        executor.initialize();
        return executor;
    }

}
```

### AC-2.b: Bean method `visitSummaryExecutor()` returns `ThreadPoolTaskExecutor`

Confirmed by code above. Method name: `visitSummaryExecutor`, return type: `ThreadPoolTaskExecutor`.

#### `./mvnw test -Dtest="AsyncConfigTest"` (GREEN phase)

```text
[INFO] Running org.springframework.samples.petclinic.system.AsyncConfigTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.631 s -- in org.springframework.samples.petclinic.system.AsyncConfigTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

All 5 assertions pass (corePoolSize=2, maxPoolSize=5, queueCapacity=25, CallerRunsPolicy, threadNamePrefix="visitSummary-").

### Notes

Committed Task 01 (test, RED) and Task 02 (implementation, GREEN) together in one commit
because the pre-commit hook runs `./mvnw test` and blocks commits with compile failures.
The RED phase evidence is preserved in Task 01's proof.
