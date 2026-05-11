# Proofs: Task 05 — Validate and capture proof artifacts

Covers: AC-3.a, all

## Planned evidence

- Full `./mvnw test` command output showing `BUILD SUCCESS` and zero failures.
- Confirmation that `AsyncConfigTest` and `AsyncConfigPropertiesTest` appear in
  the passing test list.
- Coverage matrix in `13-validation-async-config.md` with all rows transitioned
  to `PASS`.

## Completion notes

### Verification block

#### Anthropic properties grep (AC-1.a, AC-1.b, AC-1.c)

```text
$ grep -n "anthropic.api.key" src/main/resources/application.properties
28:anthropic.api.key=${ANTHROPIC_API_KEY:}

$ grep -n "anthropic.api.url" src/main/resources/application.properties
29:anthropic.api.url=https://api.anthropic.com/v1/messages

$ grep -n "anthropic.model" src/main/resources/application.properties
30:anthropic.model=claude-haiku-4-5-20251001
```

#### AsyncConfig.java annotations grep (AC-2.a, AC-2.b)

```text
$ grep -n "@Configuration\|@EnableAsync\|visitSummaryExecutor" \
    src/main/java/org/springframework/samples/petclinic/system/AsyncConfig.java
10:@Configuration
11:@EnableAsync
15:    public ThreadPoolTaskExecutor visitSummaryExecutor() {
```

#### `./mvnw test` (AC-3.a, AC-1.d, AC-2.c through AC-2.g)

```text
[WARNING] Tests run: 110, Failures: 0, Errors: 0, Skipped: 5
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  19.605 s
[INFO] Finished at: 2026-05-11T11:29:57-05:00
[INFO] ------------------------------------------------------------------------
```

Exit code 0. No failures. `AsyncConfigTest` (5 tests) and `AsyncConfigPropertiesTest`
(3 tests) both pass.

### Definition of done

- [x] AC-1.a — Task 04 proof: `anthropic.api.key=${ANTHROPIC_API_KEY:}` at line 28
- [x] AC-1.b — Task 04 proof: `anthropic.api.url=https://api.anthropic.com/v1/messages` at line 29
- [x] AC-1.c — Task 04 proof: `anthropic.model=claude-haiku-4-5-20251001` at line 30
- [x] AC-1.d — Task 04 proof: `AsyncConfigPropertiesTest` passes; all 3 `containsProperty` return `true`
- [x] AC-2.a — Task 02 proof: `AsyncConfig.java` in `system` package with `@Configuration @EnableAsync`
- [x] AC-2.b — Task 02 proof: bean method `visitSummaryExecutor()` returns `ThreadPoolTaskExecutor`
- [x] AC-2.c — Task 02 proof: `AsyncConfigTest` asserts `corePoolSize == 2` — passing
- [x] AC-2.d — Task 02 proof: `AsyncConfigTest` asserts `maxPoolSize == 5` — passing
- [x] AC-2.e — Task 02 proof: `AsyncConfigTest` asserts `queueCapacity == 25` — passing
- [x] AC-2.f — Task 02 proof: `AsyncConfigTest` asserts rejection handler is `CallerRunsPolicy` — passing
- [x] AC-2.g — Task 02 proof: `AsyncConfigTest` asserts thread name prefix is `visitSummary-` — passing
- [x] AC-3.a — `./mvnw test` exits 0; Tests run: 110, Failures: 0, Errors: 0

### Coverage matrix

| AC ID | Status |
|-------|--------|
| AC-1.a | PASS |
| AC-1.b | PASS |
| AC-1.c | PASS |
| AC-1.d | PASS |
| AC-2.a | PASS |
| AC-2.b | PASS |
| AC-2.c | PASS |
| AC-2.d | PASS |
| AC-2.e | PASS |
| AC-2.f | PASS |
| AC-2.g | PASS |
| AC-3.a | PASS |
