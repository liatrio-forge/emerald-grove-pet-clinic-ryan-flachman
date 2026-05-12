---
status: accepted
created: 2026-05-12
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: VisitSummaryService (20)

## Goal

The AI Visit Notes Summarizer epic needs an async orchestrator that ties together
the independently-delivered components — `VisitPromptBuilder`, `ClaudeApiClient`,
and a JSON parser — into one coherent workflow. This spec delivers
`VisitSummaryService`, the `@Service` that loads a `Visit`, calls Claude, parses
the response, and writes the AI fields back to the `visits` table, all
asynchronously on the `visitSummaryExecutor` thread pool. It also delivers
`VisitSummaryParser` (TASK-06), which was never specced; without the parser the
service cannot be built, so both are included here. The service is the final
back-end building block before the polling endpoint (TASK-11) and the
`VisitController` trigger (TASK-12) can be added.

## Scope

### In scope

- `VisitSummaryParseException.java` — unchecked exception thrown when Claude's
  JSON response cannot be parsed.
- `VisitSummaryParser.java` — `@Component` that parses a raw JSON string returned
  by `ClaudeApiClient` into a `VisitSummary` record.
- `VisitSummaryService.java` — `@Service` with one public method
  `generate(Integer visitId)` annotated `@Async("visitSummaryExecutor")`.
- `VisitSummaryParserTests.java` — JUnit 5 unit tests for every parsing branch.
- `VisitSummaryServiceTests.java` — JUnit 5 + Mockito unit tests for every
  service code path.
- `VisitRepository` extension: add `Optional<Visit> findById(Integer id)` and
  `Visit save(Visit visit)`.
- `Visit.java` extension: add a read-only `@ManyToOne(fetch = FetchType.LAZY)`
  `Pet pet` back-reference (see Q2).
- `VisitPromptBuilder.java` correction: replace the system-prompt key `"followUp"`
  with `"follow_up"` to match the wire format already established by spec-18 (see
  Q6).

### Out of scope

- `VisitSummaryController` (polling endpoint) — TASK-11, separate spec.
- `VisitController` trigger — TASK-12, separate spec.
- `VisitSummaryIntegrationTest` — TASK-13, separate spec.
- `ClaudeApiClientImpl` — already delivered (spec-18).
- `ClaudeApiClientStub` — already delivered (spec-18).
- Any Thymeleaf template or CSS change.
- Retry logic on FAILED visits (open decision in the epic; not in scope here).

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java`
  (spec-14, delivered) — entity with `aiStatus`, `aiSummary`, `aiTags`,
  `aiUrgency`, `aiFollowUp`; `AiStatus` enum; constructor defaults
  `aiStatus = PENDING`.
- `src/main/java/org/springframework/samples/petclinic/owner/AiStatus.java`
  (spec-14) — `PENDING`, `PROCESSING`, `DONE`, `FAILED`.
- `src/main/java/org/springframework/samples/petclinic/owner/VisitSummary.java`
  (spec-15, delivered) — `record VisitSummary(String summary, List<String> tags,
  VisitUrgency urgency, String followUp)`.
- `src/main/java/org/springframework/samples/petclinic/owner/VisitUrgency.java`
  (spec-15) — `ROUTINE`, `MONITOR`, `URGENT`.
- `src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java`
  (spec-16, delivered) — `build(Visit, Pet)` returns `PromptRequest`.
- `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java`
  (spec-17, delivered) — `String complete(String systemPrompt, String userMessage)`.
- `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientStub.java`
  (spec-18, delivered) — active when API key is blank; defines the JSON wire
  format `{"summary":…,"tags":[…],"urgency":…,"follow_up":…}`.
- `src/main/java/org/springframework/samples/petclinic/system/AsyncConfig.java`
  (spec-13, accepted) — `@EnableAsync`; `visitSummaryExecutor` bean.
- `src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java` —
  current state: `Repository<Visit, Integer>` with no `findById` or `save`.
- `src/main/java/org/springframework/samples/petclinic/owner/Pet.java` —
  `@OneToMany @JoinColumn(name = "pet_id")` owns the `pet_id` FK column in
  `visits`.
- `docs/epic-ai-visit-summary.md` TASK-06, TASK-10 — canonical description of
  parser and service requirements.

## Acceptance criteria

- **AC-1: VisitSummaryParseException exists**
  - AC-1.a: `VisitSummaryParseException.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryParseException.java`.
  - AC-1.b: It extends `RuntimeException` — verified by grep.

- **AC-2: VisitSummaryParser — happy path**
  - AC-2.a: `VisitSummaryParserTests#parseHappyPath` passes: parsing a JSON with
    all four fields populated returns a `VisitSummary` with non-null `summary`,
    a non-empty `tags` list, `urgency == VisitUrgency.MONITOR`, and non-null
    `followUp`.

- **AC-3: VisitSummaryParser — optional `followUp`**
  - AC-3.a: `VisitSummaryParserTests#parseMissingFollowUpReturnsNull` passes:
    JSON with `"follow_up": null` returns a `VisitSummary` with `followUp == null`
    without throwing an exception.

- **AC-4: VisitSummaryParser — unknown urgency defaults to ROUTINE**
  - AC-4.a: `VisitSummaryParserTests#parseUnknownUrgencyDefaultsToRoutine` passes:
    JSON with `"urgency": "CATASTROPHIC"` returns `urgency == VisitUrgency.ROUTINE`
    without throwing an exception.

- **AC-5: VisitSummaryParser — malformed JSON throws exception**
  - AC-5.a: `VisitSummaryParserTests#parseMalformedJsonThrowsParseException` passes:
    passing the string `"not valid json"` throws `VisitSummaryParseException`.

- **AC-6: VisitSummaryParser — tags variants**
  - AC-6.a: `VisitSummaryParserTests#parseEmptyTagsReturnsEmptyList` passes: JSON
    with `"tags": []` returns a `VisitSummary` with an empty tags list.
  - AC-6.b: `VisitSummaryParserTests#parseSingleTag` passes: JSON with
    `"tags": ["diabetes"]` returns a list of size 1.
  - AC-6.c: `VisitSummaryParserTests#parseMultipleTags` passes: JSON with
    `"tags": ["a","b","c"]` returns a list of size 3.

- **AC-7: VisitSummaryService — class structure**
  - AC-7.a: `VisitSummaryService.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryService.java`.
  - AC-7.b: The class is annotated `@Service` — verified by grep.
  - AC-7.c: The `generate(Integer visitId)` method is annotated
    `@Async("visitSummaryExecutor")` — verified by grep.

- **AC-8: VisitSummaryService — happy path**
  - AC-8.a: `VisitSummaryServiceTests#generateHappyPathSetsProcessingThenDone`
    passes: given a valid `visitId`, the service calls `visitRepository.save()` at
    least twice; the first call captures `aiStatus == PROCESSING` and the final
    call captures `aiStatus == DONE`.
  - AC-8.b: `VisitSummaryServiceTests#generateHappyPathWritesAiFields` passes:
    when `VisitSummaryParser.parse()` returns a `VisitSummary`, the saved Visit has
    `aiSummary`, `aiTags` (comma-joined list), `aiUrgency` (urgency name), and
    `aiFollowUp` matching the parsed values.

- **AC-9: VisitSummaryService — `ClaudeApiClient` exception → FAILED**
  - AC-9.a: `VisitSummaryServiceTests#generateClientExceptionSetsFailedStatus`
    passes: when `ClaudeApiClient.complete()` throws any exception, the final
    `visitRepository.save()` call captures `aiStatus == FAILED`.
  - AC-9.b: No exception propagates out of `generate()` in this scenario.

- **AC-10: VisitSummaryService — `VisitSummaryParseException` → FAILED**
  - AC-10.a: `VisitSummaryServiceTests#generateParseExceptionSetsFailedStatus`
    passes: when `VisitSummaryParser.parse()` throws `VisitSummaryParseException`,
    the final save captures `aiStatus == FAILED` and no exception propagates.

- **AC-11: VisitSummaryService — visit not found**
  - AC-11.a: `VisitSummaryServiceTests#generateVisitNotFoundLogsAndReturns` passes:
    when `visitRepository.findById()` returns empty, `generate()` returns without
    calling `ClaudeApiClient` or saving anything.
  - AC-11.b: No exception propagates in this scenario.

- **AC-12: `VisitRepository` extensions**
  - AC-12.a: `VisitRepository.java` declares
    `Optional<Visit> findById(Integer id)` — verified by grep.
  - AC-12.b: `VisitRepository.java` declares `Visit save(Visit visit)` — verified
    by grep.

- **AC-13: `Visit.java` — `Pet` back-reference**
  - AC-13.a: `Visit.java` declares a field of type `Pet` annotated
    `@ManyToOne(fetch = FetchType.LAZY)` — verified by grep.
  - AC-13.b: The `@JoinColumn` on that field carries `insertable = false,
    updatable = false` — verified by grep.

- **AC-14: `VisitPromptBuilder` key correction**
  - AC-14.a: `VisitPromptBuilder.java` system prompt contains `"follow_up"`, not
    `"followUp"` — verified by grep.
  - AC-14.b: `VisitPromptBuilderTest` (existing, spec-16) still passes after the
    correction — confirmed by `./mvnw test -Dtest=VisitPromptBuilderTest`.

- **AC-15: TDD compliance**
  - AC-15.a: The proof artifact for the parser RED task captures Maven output
    showing `VisitSummaryParserTests` failing before `VisitSummaryParser.java` is
    created.
  - AC-15.b: The proof artifact for the service RED task captures Maven output
    showing `VisitSummaryServiceTests` failing before `VisitSummaryService.java`
    is created.

- **AC-16: Existing tests remain green**
  - AC-16.a: `./mvnw test` exits 0 with zero test failures after all changes are
    applied.
  - AC-16.b: New code has ≥ 90% line coverage per JaCoCo report on
    `VisitSummaryParser` and `VisitSummaryService`.

## Conventions

- All new classes live in `org.springframework.samples.petclinic.owner`. No new
  package.
- `VisitSummaryParser` uses `com.fasterxml.jackson.databind.ObjectMapper`
  (already on classpath). Parse with `MAPPER.readTree(rawJson)` rather than
  deserializing into a POJO, to avoid a separate DTO class.
- JSON key for the follow-up field is `"follow_up"` (snake_case) — see Q6 in
  `20-questions-1-visit-summary-service.md`.
- Unknown urgency string maps to `VisitUrgency.ROUTINE` with a `Logger.warn()`.
  Log at WARN, not ERROR, because this is a graceful degradation, not a failure.
- `aiTags` is stored as a comma-joined string (no spaces after comma): e.g.
  `"diabetes,weight"`. Use `String.join(",", tags)`.
- `aiUrgency` is stored as the enum's `name()`: `"ROUTINE"`, `"MONITOR"`, or
  `"URGENT"`.
- `VisitSummaryParser` tests use no Spring context (`@ExtendWith(MockitoExtension.class)` or plain JUnit 5 is fine).
- `VisitSummaryService` tests use `@ExtendWith(MockitoExtension.class)`.
  `VisitPromptBuilder` is not mocked (it is a static utility); all other injected
  collaborators are `@Mock`.
- The `Pet` back-reference on `Visit` is read-only. Never assign to `visit.pet`
  in application code — it is populated by JPA when loading a Visit that was
  previously joined to a Pet.
- Strict TDD is mandatory: failing test must be captured in the RED proof artifact
  before the corresponding production class is created.
- Depends on: spec-14 (`Visit` entity), spec-15 (`VisitSummary`, `VisitUrgency`),
  spec-16 (`VisitPromptBuilder`), spec-17 (`ClaudeApiClient`), spec-18 (stub/impl),
  spec-13 (`AsyncConfig`).
- Blocks: TASK-11 (polling controller) and TASK-12 (`VisitController` trigger).

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
