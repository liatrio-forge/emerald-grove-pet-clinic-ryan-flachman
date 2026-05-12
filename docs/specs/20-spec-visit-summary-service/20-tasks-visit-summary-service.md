# Tasks: VisitSummaryService (20)

## Task 01 — Write failing `VisitSummaryParserTests` (RED)

Covers: AC-15.a

- Create
  `src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryParserTests.java`
  with the following test methods (all will fail to compile or run because neither
  `VisitSummaryParser` nor `VisitSummaryParseException` exist yet):
  - `parseHappyPath` — parses well-formed JSON with all four fields; asserts
    non-null summary, non-empty tags list, `urgency == VisitUrgency.MONITOR`,
    non-null followUp.
  - `parseMissingFollowUpReturnsNull` — JSON with `"follow_up": null`; asserts
    `followUp == null` without exception.
  - `parseUnknownUrgencyDefaultsToRoutine` — JSON with `"urgency": "CATASTROPHIC"`;
    asserts `urgency == VisitUrgency.ROUTINE` without exception.
  - `parseMalformedJsonThrowsParseException` — passes `"not valid json"`; asserts
    `VisitSummaryParseException` is thrown.
  - `parseEmptyTagsReturnsEmptyList` — JSON with `"tags": []`; asserts empty list.
  - `parseSingleTag` — JSON with `"tags": ["diabetes"]`; asserts list size 1.
  - `parseMultipleTags` — JSON with `"tags": ["a","b","c"]`; asserts list size 3.
- Run `./mvnw test -Dtest=VisitSummaryParserTests` and capture the failure output.
- Record the failing Maven output in the proof file (RED evidence for AC-15.a).

**Proof:** `20-proofs/20-task-01-proofs.md`

---

## Task 02 — Implement `VisitSummaryParser` and `VisitSummaryParseException` (GREEN)

Covers: AC-1.a, AC-1.b, AC-2.a, AC-3.a, AC-4.a, AC-5.a, AC-6.a, AC-6.b, AC-6.c

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryParseException.java`:
  - `public class VisitSummaryParseException extends RuntimeException`
  - Single constructor: `VisitSummaryParseException(String message, Throwable cause)`
- Create
  `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryParser.java`:
  - `@Component`
  - Private static final `ObjectMapper MAPPER = new ObjectMapper()`
  - Public method `VisitSummary parse(String rawJson)`:
    - Wrap body in `try { … } catch (Exception e) { throw new VisitSummaryParseException(…, e); }`
    - Parse with `MAPPER.readTree(rawJson)`
    - Extract `"summary"` as text
    - Extract `"tags"` as a `List<String>` (iterate `ArrayNode`)
    - Extract `"urgency"` string; convert to `VisitUrgency` via a private helper
      that defaults to `ROUTINE` with a `Logger.warn()` on unknown values
    - Extract `"follow_up"`: return `null` if node is missing or JSON null,
      otherwise `asText()`
    - Return `new VisitSummary(summary, tags, urgency, followUp)`
- Run `./mvnw test -Dtest=VisitSummaryParserTests` and capture passing output.
- Record proof: grep for `@Component` and `extends RuntimeException`, plus Maven
  test output showing all 7 tests passing.

**Proof:** `20-proofs/20-task-02-proofs.md`

---

## Task 03 — Extend `VisitRepository`, `Visit.java`, and correct `VisitPromptBuilder` key

Covers: AC-12.a, AC-12.b, AC-13.a, AC-13.b, AC-14.a, AC-14.b

- Edit `src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java`:
  - Add `Optional<Visit> findById(Integer id);` (Spring Data derives the query).
  - Add `Visit save(Visit visit);` (Spring Data provides the implementation).
  - Add `import java.util.Optional;` if not already present.
- Edit `src/main/java/org/springframework/samples/petclinic/owner/Visit.java`:
  - Add field:

    ```java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", insertable = false, updatable = false)
    private Pet pet;
    ```

  - Add getter `public Pet getPet() { return this.pet; }` (no setter — read-only).
  - Add imports: `jakarta.persistence.FetchType`, `jakarta.persistence.ManyToOne`.
- Edit `src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java`:
  - In the `SYSTEM_PROMPT` string constant, replace `"followUp"` with `"follow_up"`.
- Run `./mvnw compile` to verify no compilation errors.
- Run `./mvnw test -Dtest=VisitPromptBuilderTest` to confirm existing tests pass
  (AC-14.b).
- Capture grep outputs and Maven test output in the proof file.

**Proof:** `20-proofs/20-task-03-proofs.md`

---

## Task 04 — Write failing `VisitSummaryServiceTests` (RED)

Covers: AC-15.b

- Create
  `src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryServiceTests.java`
  with `@ExtendWith(MockitoExtension.class)` and the following mocks:
  `@Mock VisitRepository visitRepository`, `@Mock ClaudeApiClient claudeApiClient`,
  `@Mock VisitSummaryParser parser`.
- Declare `@InjectMocks VisitSummaryService service` (will fail to compile because
  `VisitSummaryService` does not exist yet).
- Write test methods (all will fail to compile):
  - `generateHappyPathSetsProcessingThenDone` — stubs `visitRepository.findById`
    to return an `Optional<Visit>` with a `Pet`; stubs `claudeApiClient.complete`
    to return valid JSON string; stubs `parser.parse` to return a `VisitSummary`;
    calls `service.generate(1)`; uses `ArgumentCaptor<Visit>` to verify two
    `save()` calls — first with `PROCESSING`, second with `DONE`.
  - `generateHappyPathWritesAiFields` — same setup; asserts the second captured
    save has `aiSummary`, `aiTags`, `aiUrgency`, `aiFollowUp` set correctly.
  - `generateClientExceptionSetsFailedStatus` — stubs `claudeApiClient.complete`
    to throw `new RuntimeException("network error")`; asserts the final save has
    `aiStatus == FAILED`; asserts no exception propagates.
  - `generateParseExceptionSetsFailedStatus` — stubs `parser.parse` to throw
    `new VisitSummaryParseException("bad json", null)`; asserts final save has
    `aiStatus == FAILED`.
  - `generateVisitNotFoundLogsAndReturns` — stubs `visitRepository.findById` to
    return `Optional.empty()`; calls `service.generate(99)`; verifies
    `claudeApiClient.complete` was never called; verifies `visitRepository.save`
    was never called; asserts no exception propagates.
- Run `./mvnw test -Dtest=VisitSummaryServiceTests` and capture the compilation /
  test failure (RED evidence for AC-15.b).

**Proof:** `20-proofs/20-task-04-proofs.md`

---

## Task 05 — Implement `VisitSummaryService` (GREEN)

Covers: AC-7.a, AC-7.b, AC-7.c, AC-8.a, AC-8.b, AC-9.a, AC-9.b, AC-10.a,
AC-11.a, AC-11.b

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryService.java`:
  - `@Service`
  - Constructor-inject `VisitRepository visitRepository`, `ClaudeApiClient claudeApiClient`,
    `VisitSummaryParser parser`
  - `@Async("visitSummaryExecutor") public void generate(Integer visitId)`:

    ```text
    1. visitRepository.findById(visitId) → if empty, log WARN and return
    2. visit.setAiStatus(PROCESSING); visitRepository.save(visit)
    3. try {
         PromptRequest prompt = VisitPromptBuilder.build(visit, visit.getPet())
         String raw = claudeApiClient.complete(prompt.systemPrompt(), prompt.userMessage())
         VisitSummary summary = parser.parse(raw)
         visit.setAiSummary(summary.summary())
         visit.setAiTags(String.join(",", summary.tags()))
         visit.setAiUrgency(summary.urgency().name())
         visit.setAiFollowUp(summary.followUp())
         visit.setAiStatus(DONE)
       } catch (Exception e) {
         log.error("AI summary generation failed for visit {}", visitId, e)
         visit.setAiStatus(FAILED)
       }
    4. visitRepository.save(visit)
    ```

- Run `./mvnw test -Dtest=VisitSummaryServiceTests` and capture passing output.
- Run grep checks for `@Service` and `@Async("visitSummaryExecutor")`.
- Capture all output in the proof file.

**Proof:** `20-proofs/20-task-05-proofs.md`

---

## Task 06 — Validate and capture proof artifacts

Covers: all (AC-16.a, AC-16.b, full regression)

- Run `./mvnw test` and capture full output — verifies AC-16.a.
- Run `./mvnw test jacoco:report` and capture coverage summary for
  `VisitSummaryParser` and `VisitSummaryService` — verifies AC-16.b (≥ 90%
  line coverage).
- Run all structural grep checks from `20-validation-visit-summary-service.md`
  and capture their output.
- Confirm every row in the coverage matrix has a `PASS` status.
- Update each proof file with the real output (no placeholders).

**Proof:** `20-proofs/20-task-06-proofs.md`
