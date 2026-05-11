# Validation: Async Config + Anthropic Properties (13)

## Automated verification

From repository root:

```bash
# Verify Anthropic properties are present in application.properties (AC-1.a, AC-1.b, AC-1.c)
grep -n "anthropic.api.key" src/main/resources/application.properties
grep -n "anthropic.api.url" src/main/resources/application.properties
grep -n "anthropic.model"   src/main/resources/application.properties

# Verify AsyncConfig.java exists with correct annotations (AC-2.a)
grep -n "@Configuration\|@EnableAsync\|visitSummaryExecutor" \
  src/main/java/org/springframework/samples/petclinic/system/AsyncConfig.java

# Run the full test suite (AC-3.a, AC-1.d, AC-2.c–g)
./mvnw test
```

**Expected:**

- All three `grep` commands return at least one matching line.
- `AsyncConfig.java` grep shows `@Configuration`, `@EnableAsync`, and `visitSummaryExecutor` on separate lines.
- `./mvnw test` exits 0; `BUILD SUCCESS` appears in output; `AsyncConfigTest` and `AsyncConfigPropertiesTest` appear in the passing test list.

## Traceability

- Feature spec: `13-spec-async-config.md`
- Task breakdown: `13-tasks-async-config.md`
- Questions and decisions: `13-questions-1-async-config.md`
- Per-task evidence: `13-proofs/13-task-NN-proofs.md`
- Downstream specs: TASK-09 (`ClaudeApiClientImpl`), TASK-10 (`VisitSummaryService`)
- Parent epic: `docs/epic-ai-visit-summary.md`

## Manual checks

None — all acceptance criteria are verifiable by automated command.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `anthropic.api.key=${ANTHROPIC_API_KEY:}` in `application.properties` | `13-proofs/13-task-04-proofs.md` | file edit | PASS |
| AC-1.b | `anthropic.api.url=https://api.anthropic.com/v1/messages` in `application.properties` | `13-proofs/13-task-04-proofs.md` | file edit | PASS |
| AC-1.c | `anthropic.model=claude-haiku-4-5-20251001` in `application.properties` | `13-proofs/13-task-04-proofs.md` | file edit | PASS |
| AC-1.d | `AsyncConfigPropertiesTest` passes; all three `containsProperty` calls return `true` | `13-proofs/13-task-04-proofs.md` | Maven test pass | PASS |
| AC-2.a | `AsyncConfig.java` exists in `system` package with `@Configuration @EnableAsync` | `13-proofs/13-task-02-proofs.md` | file creation | PASS |
| AC-2.b | Bean method `visitSummaryExecutor()` returns `ThreadPoolTaskExecutor` | `13-proofs/13-task-02-proofs.md` | file creation | PASS |
| AC-2.c | `AsyncConfigTest` asserts `corePoolSize == 2` | `13-proofs/13-task-02-proofs.md` | Maven test pass | PASS |
| AC-2.d | `AsyncConfigTest` asserts `maxPoolSize == 5` | `13-proofs/13-task-02-proofs.md` | Maven test pass | PASS |
| AC-2.e | `AsyncConfigTest` asserts `queueCapacity == 25` | `13-proofs/13-task-02-proofs.md` | Maven test pass | PASS |
| AC-2.f | `AsyncConfigTest` asserts rejection handler is `CallerRunsPolicy` | `13-proofs/13-task-02-proofs.md` | Maven test pass | PASS |
| AC-2.g | `AsyncConfigTest` asserts thread name prefix is `visitSummary-` | `13-proofs/13-task-02-proofs.md` | Maven test pass | PASS |
| AC-3.a | `./mvnw test` exits 0 with no failures | `13-proofs/13-task-05-proofs.md` | command output | PENDING |

## Definition of done

- [ ] AC-1.a: `anthropic.api.key=${ANTHROPIC_API_KEY:}` in `application.properties`
- [ ] AC-1.b: `anthropic.api.url=https://api.anthropic.com/v1/messages` in `application.properties`
- [ ] AC-1.c: `anthropic.model=claude-haiku-4-5-20251001` in `application.properties`
- [ ] AC-1.d: `AsyncConfigPropertiesTest` passes with all three `containsProperty` assertions `true`
- [ ] AC-2.a: `AsyncConfig.java` exists in `system` package with `@Configuration @EnableAsync`
- [ ] AC-2.b: Bean method `visitSummaryExecutor()` present and returns `ThreadPoolTaskExecutor`
- [ ] AC-2.c: `AsyncConfigTest` asserts `corePoolSize == 2` — passing
- [ ] AC-2.d: `AsyncConfigTest` asserts `maxPoolSize == 5` — passing
- [ ] AC-2.e: `AsyncConfigTest` asserts `queueCapacity == 25` — passing
- [ ] AC-2.f: `AsyncConfigTest` asserts rejection handler is `CallerRunsPolicy` — passing
- [ ] AC-2.g: `AsyncConfigTest` asserts thread name prefix is `visitSummary-` — passing
- [ ] AC-3.a: `./mvnw test` exits 0 with no test failures
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
- [ ] Parent epic child-registry checkbox ticked (not applicable — no epic spec file).
