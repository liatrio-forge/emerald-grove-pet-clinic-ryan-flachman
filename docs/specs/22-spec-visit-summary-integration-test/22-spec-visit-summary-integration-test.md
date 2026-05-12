---
status: delivered
created: 2026-05-12
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: VisitSummaryIntegrationTest (22)

## Goal

The AI Visit Notes Summarizer epic (TASK-13) needs a full-stack integration
test to validate that all components wired together in TASK-08 through
TASK-12 behave correctly end-to-end. This spec delivers two `@SpringBootTest`
integration test classes that exercise the complete async flow — HTTP form
POST → `VisitSummaryService` async execution → `VisitRepository` persistence
→ `GET /visits/{id}/summary` polling — using the in-memory H2 database and
the `ClaudeApiClientStub`. Both the happy path (async generation completing
with `DONE` status, including keyword-based urgency routing) and the failure
path (client exception producing `FAILED` status) are covered. This spec
is a prerequisite for TASK-18 (Playwright E2E).

## Scope

### In scope

- `VisitSummaryHappyPathIT.java` — `@SpringBootTest(webEnvironment = MOCK)` +
  `@AutoConfigureMockMvc` test class (no mocking of `ClaudeApiClient`; stub
  auto-activates when `anthropic.api.key` is absent) with:
  - `shouldGenerateSummaryAfterVisitSave` — posts a visit, polls the DB via
    Awaitility until `aiStatus == DONE`, asserts all five AI columns are
    populated, and asserts the polling endpoint returns a well-formed DONE
    JSON response.
  - `shouldMapDescriptionKeywordToUrgency` — `@ParameterizedTest` with two
    cases (`"limp" → "urgent"`, `"checkup" → "routine"`) verifying stub
    keyword routing end-to-end through the full HTTP + async stack.
- `VisitSummaryFailureIT.java` — `@SpringBootTest(webEnvironment = MOCK)` +
  `@AutoConfigureMockMvc` test class with `@MockitoBean ClaudeApiClient`
  configured to throw `ClaudeApiException`, containing:
  - `shouldMarkVisitFailedWhenClientThrows` — posts a visit, polls the DB
    until `aiStatus == FAILED`, and asserts the polling endpoint returns
    `{"status":"FAILED"}`.

### Out of scope

- No new production code — all production components are delivered in specs
  12–21.
- No Playwright E2E test (TASK-18 / spec-TBD).
- No MySQL or PostgreSQL variants — H2 in-memory database only.
- No real Anthropic API calls — stub or mock only.
- No retry mechanism or manual re-trigger for FAILED visits.
- Unit-level `ClaudeApiClientStub` keyword routing tests — those belong to
  spec-18.

## Source excerpts

- `src/main/java/…/owner/Visit.java` (spec-14, delivered) — entity with
  `aiStatus`, `aiSummary`, `aiTags`, `aiUrgency`, `aiFollowUp`.
- `src/main/java/…/owner/AiStatus.java` (spec-14, delivered) — `PENDING`,
  `PROCESSING`, `DONE`, `FAILED`.
- `src/main/java/…/owner/ClaudeApiException.java` (spec-17, delivered) —
  package-private `RuntimeException` in the `owner` package; accessible from
  test classes in the same package.
- `src/main/java/…/owner/ClaudeApiClientStub.java` (spec-18, delivered) —
  active when `anthropic.api.key` is absent; keyword routing: `"limp"` and
  `"pain"` → URGENT, `"checkup"` → ROUTINE, default → MONITOR.
- `src/main/java/…/owner/VisitSummaryController.java` (spec-21, delivered) —
  `GET /visits/{visitId}/summary`; response contract defined in spec-21.
- `src/main/java/…/owner/VisitController.java` (spec-21-trigger, delivered) —
  calls `visitSummaryService.generate(visit.getId())` after each successful
  save.
- `src/main/resources/db/h2/data.sql` — Owner 6 = Jean Coleman; Pet 7 =
  Samantha (type: cat, owner 6); Pet 8 = Max (type: cat, owner 6). Same
  fixtures used in `VisitAiFieldsIT`.
- `docs/epic-ai-visit-summary.md` TASK-13 — canonical description of this
  task.
- `22-questions-1-visit-summary-integration-test.md` — all design decisions
  resolved before this spec was written.

## Acceptance criteria

- **AC-1: Awaitility available on test classpath**
  - AC-1.a: Both new test classes compile with `import org.awaitility.Awaitility`
    — `./mvnw test-compile` exits 0.

- **AC-2: VisitSummaryHappyPathIT class structure**
  - AC-2.a: File exists at
    `src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java`.
  - AC-2.b: Class is annotated with `@SpringBootTest(webEnvironment = …MOCK)`,
    `@AutoConfigureMockMvc`, `@DisabledInNativeImage`, and `@DisabledInAotMode`
    — verified by grep.
  - AC-2.c: Class has no `@Transactional` annotation — verified by grep.

- **AC-3: Happy path — full DONE cycle**
  - AC-3.a: `VisitSummaryHappyPathIT#shouldGenerateSummaryAfterVisitSave`
    passes: POST to `/owners/6/pets/7/visits/new` with description
    `"Dog is limping on left front leg"` returns HTTP 3xx; Awaitility confirms
    `visitRepository.findById(visitId)` returns a visit with `aiStatus == DONE`
    within 5 seconds.
  - AC-3.b: After DONE is confirmed, the reloaded visit has non-null, non-blank
    `aiSummary`.
  - AC-3.c: After DONE is confirmed, the reloaded visit has non-null, non-blank
    `aiTags`.
  - AC-3.d: After DONE is confirmed, the reloaded visit has non-null, non-blank
    `aiUrgency`.
  - AC-3.e: GET `/visits/{visitId}/summary` returns HTTP 200 with JSON containing
    `"status":"DONE"`, a non-null `summary` string, a non-empty `tags` array,
    and a non-null `urgency` string.

- **AC-4: Urgency keyword routing (parameterized)**
  - AC-4.a: `VisitSummaryHappyPathIT#shouldMapDescriptionKeywordToUrgency[limp → urgent]`
    passes: POST a visit with `"limp"` in the description, Awaitility awaits
    `DONE`, GET `/visits/{id}/summary` returns JSON with `"urgency":"urgent"`.
  - AC-4.b: `VisitSummaryHappyPathIT#shouldMapDescriptionKeywordToUrgency[checkup → routine]`
    passes: POST a visit with `"checkup"` in the description, Awaitility awaits
    `DONE`, GET `/visits/{id}/summary` returns JSON with `"urgency":"routine"`.

- **AC-5: VisitSummaryFailureIT class structure**
  - AC-5.a: File exists at
    `src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java`.
  - AC-5.b: Class is annotated with `@SpringBootTest(webEnvironment = …MOCK)`,
    `@AutoConfigureMockMvc`, `@DisabledInNativeImage`, and `@DisabledInAotMode`
    — verified by grep.
  - AC-5.c: Class declares a field `@MockitoBean ClaudeApiClient claudeApiClient`
    — verified by grep.
  - AC-5.d: Class has no `@Transactional` annotation — verified by grep.

- **AC-6: Failure path — FAILED status**
  - AC-6.a: `VisitSummaryFailureIT#shouldMarkVisitFailedWhenClientThrows`
    passes: with mock configured via
    `given(claudeApiClient.complete(any(), any())).willThrow(new ClaudeApiException("rate limited"))`,
    POST to `/owners/6/pets/7/visits/new` results in `aiStatus == FAILED` in the
    DB within 5 seconds Awaitility timeout.
  - AC-6.b: GET `/visits/{id}/summary` returns HTTP 200 with JSON body exactly
    `{"status":"FAILED"}` after FAILED status is confirmed.

- **AC-7: All tests remain green**
  - AC-7.a: `./mvnw test` exits 0 with zero test failures and zero errors after
    both test files are added.

## Conventions

- Both test classes live in the package
  `org.springframework.samples.petclinic.owner`, granting access to
  package-private `ClaudeApiException`.
- Do NOT annotate either test class or any test method with `@Transactional`.
  The async generation runs in its own committed transactions (via
  `VisitSummaryTransactionSteps`). A `@Transactional` test would cause
  Hibernate's first-level cache to return stale data on subsequent `findById`
  calls within the same transaction.
- Awaitility poll pattern (used in all three test methods):

  ```java
  await()
      .atMost(5, TimeUnit.SECONDS)
      .pollInterval(200, TimeUnit.MILLISECONDS)
      .until(() -> visitRepository.findById(visitId)
          .map(v -> v.getAiStatus() == AiStatus.DONE)   // swap DONE→FAILED for failure test
          .orElse(false));
  ```

- Locate the new visit ID after POST by snapshotting visit IDs before the
  POST, then reloading owner 6 from `ownerRepository.findById(6)` and
  diffing:

  ```java
  Set<Integer> before = ownerRepository.findById(6).orElseThrow()
      .getPets().stream()
      .filter(p -> p.getId() == 7)
      .flatMap(p -> p.getVisits().stream())
      .map(Visit::getId)
      .collect(Collectors.toSet());
  // ... perform POST ...
  Integer visitId = ownerRepository.findById(6).orElseThrow()
      .getPets().stream()
      .filter(p -> p.getId() == 7)
      .flatMap(p -> p.getVisits().stream())
      .map(Visit::getId)
      .filter(id -> !before.contains(id))
      .findFirst()
      .orElseThrow();
  ```

- `ClaudeApiClientStub` auto-activates in `VisitSummaryHappyPathIT` because
  `anthropic.api.key` is not set in the default test environment.
- In `VisitSummaryFailureIT`, `@MockitoBean ClaudeApiClient` replaces the
  stub bean entirely for that class's Spring context. This is intentional —
  no conditional property activation is needed.
- Both classes follow the annotation convention of `OwnerControllerTests`,
  `VisitControllerTests`, and `MySqlIntegrationTests`:
  `@DisabledInNativeImage` + `@DisabledInAotMode`.
- Depends on: spec-12, spec-13, spec-14, spec-15, spec-16, spec-17, spec-18,
  spec-20, spec-21.
- Blocks: TASK-18 (Playwright E2E, separate spec).

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
