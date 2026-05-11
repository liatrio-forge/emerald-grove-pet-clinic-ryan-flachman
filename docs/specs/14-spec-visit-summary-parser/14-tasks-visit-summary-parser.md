# Tasks: VisitSummaryParser (14)

## Task 01 — Write failing unit tests for VisitSummaryParser (RED)

Covers: AC-3.a, AC-4.a, AC-5.a, AC-6.a, AC-7.a, AC-7.b, AC-7.c

- Create `src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryParserTests.java`
  with the following test methods (all following Arrange-Act-Assert):
  - `shouldParseAllFieldsFromValidJson` — input has summary, tags, urgency, followUp all populated
  - `shouldReturnNullFollowUpWhenAbsent` — input JSON omits the `followUp` key entirely
  - `shouldMapUnknownUrgencyToRoutine` — input has `"urgency":"critical"` (unrecognised)
  - `shouldThrowParseExceptionForMalformedJson` — input is a bare non-JSON string
  - `shouldHandleEmptyTagsArray` — input has `"tags":[]`
  - `shouldHandleSingleTag` — input has `"tags":["checkup"]`
  - `shouldHandleMultipleTags` — input has `"tags":["wellness","checkup","annual"]`
- Create minimal stub classes (enough to compile; methods throw `UnsupportedOperationException`
  or return `null`) so the test file compiles:
  - `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryParseException.java`
  - `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryParser.java`
- Confirm `./mvnw test -Dtest=VisitSummaryParserTests` exits non-zero (all 7 tests fail — RED confirmed).

**Proof:** 14-proofs/14-task-01-proofs.md

---

## Task 02 — Create VisitSummaryParseException

Covers: AC-1.a, AC-1.b, AC-1.c

- Replace the stub `VisitSummaryParseException.java` with the full implementation:
  - Class declaration: `public class VisitSummaryParseException extends RuntimeException`
  - Single constructor: `public VisitSummaryParseException(String message, Throwable cause)`
    delegating to `super(message, cause)`.
- Confirm `./mvnw compile` exits 0.

**Proof:** 14-proofs/14-task-02-proofs.md

---

## Task 03 — Implement VisitSummaryParser

Covers: AC-2.a, AC-2.b, AC-2.c, AC-3.a, AC-4.a, AC-5.a, AC-6.a, AC-7.a, AC-7.b, AC-7.c

- Replace the stub `VisitSummaryParser.java` with the full implementation:
  - Class carries no Spring annotations (`@Component`, `@Service`, `@Repository`).
  - Holds a private `ObjectMapper` instance (Jackson; no extra dependency).
  - Implement `public VisitSummary parse(String json)`:
    - Parse `json` using `ObjectMapper.readTree()`; wrap any `JsonProcessingException`
      in a `VisitSummaryParseException`.
    - Map `summary` → `VisitSummary.summary()`.
    - Map `tags` array → `List<String>`; empty array returns empty list, never null.
    - Map `urgency` → `VisitUrgency` using case-insensitive `valueOf`; on missing key
      or unrecognised value, log a warning and default to `VisitUrgency.ROUTINE`.
    - Map `followUp` → `VisitSummary.followUp()`; absent key returns `null`.
- Confirm `./mvnw test -Dtest=VisitSummaryParserTests` exits 0 (all 7 tests pass — GREEN confirmed).

**Proof:** 14-proofs/14-task-03-proofs.md

---

## Task 04 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture full output (BUILD SUCCESS, zero failures — AC-8.a).
- Run `./mvnw test jacoco:report`; confirm `VisitSummaryParser` and
  `VisitSummaryParseException` appear in `target/site/jacoco/index.html` with ≥90%
  line coverage.
- Update the coverage matrix in `14-validation-visit-summary-parser.md`: set all
  rows to `PASS`.
- Replace all placeholder text in proof files with real command output.

**Proof:** 14-proofs/14-task-04-proofs.md
