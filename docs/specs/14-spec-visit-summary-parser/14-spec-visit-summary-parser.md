---
status: accepted
created: 2026-05-11
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: VisitSummaryParser (14)

## Goal

The AI Visit Notes Summarizer epic (see `docs/epic-ai-visit-summary.md`) requires
a dedicated parser that converts the raw JSON string returned by Claude into a
structured `VisitSummary` object. This spec delivers `VisitSummaryParser`, a pure
utility class (no Spring wiring) with deterministic rules for every edge case:
missing optional fields, unrecognised urgency values, and malformed JSON. Once this
spec is delivered, `VisitSummaryService` (TASK-10) can call it without handling raw
JSON itself.

## Scope

### In scope

- `VisitSummaryParser.java` in `org.springframework.samples.petclinic.owner` — pure
  utility class with a single `parse(String json)` method that returns a `VisitSummary`.
- `VisitSummaryParseException.java` in the same package — unchecked exception thrown
  on malformed JSON.
- Unit tests in `VisitSummaryParserTests.java` covering all five scenarios from the
  epic: happy path, missing `followUp`, unknown urgency, malformed JSON, and tags
  array variants (empty / single / multiple).
- Tests written before production code (TDD RED phase) per project standards.

### Out of scope

- `VisitSummary` record and `VisitUrgency` enum — delivered by TASK-04's spec; must
  exist before this spec's implementation begins.
- `VisitPromptBuilder` (TASK-05) and any logic that produces the JSON string.
- `ClaudeApiClient` and the network layer (TASK-07, TASK-08, TASK-09).
- `VisitSummaryService` async orchestration (TASK-10).
- Retry logic, logging configuration, or production Spring wiring.
- Null / blank handling for `summary` beyond what the epic specifies.

## Source excerpts

- `docs/epic-ai-visit-summary.md` TASK-04 — defines the `VisitSummary` record shape
  and the `VisitUrgency` enum values (ROUTINE / MONITOR / URGENT) this parser maps to.
- `docs/epic-ai-visit-summary.md` TASK-06 — lists the five test scenarios that must
  be covered.
- `src/main/java/org/springframework/samples/petclinic/owner/` — package where all
  new files are placed, consistent with `Visit.java` and `Pet.java`.

## Contract

### Expected Claude JSON shape

The parser expects the following camelCase JSON structure from Claude:

```json
{
  "summary":  "string — required",
  "tags":     ["string", "…"],
  "urgency":  "routine | monitor | urgent",
  "followUp": "string — optional, may be absent"
}
```

Field mapping rules:

| JSON key   | Java field              | Notes                                                                        |
|------------|-------------------------|------------------------------------------------------------------------------|
| `summary`  | `VisitSummary.summary()`  | Required; present in all valid responses.                                    |
| `tags`     | `VisitSummary.tags()`     | Required array; may be empty; returned list is never null.                   |
| `urgency`  | `VisitSummary.urgency()`  | Case-insensitive; unknown or absent values map to `ROUTINE` + log warning.  |
| `followUp` | `VisitSummary.followUp()` | Optional; absent key yields `null`, not an exception.                       |

### Method signature

```java
// No Spring annotations. Instantiated directly: new VisitSummaryParser()
public VisitSummary parse(String json)
```

Throws `VisitSummaryParseException` (unchecked) when `json` is not valid JSON.

## Acceptance criteria

- **AC-1: VisitSummaryParseException**
  - AC-1.a: `VisitSummaryParseException.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryParseException.java`.
  - AC-1.b: The class declaration includes `extends RuntimeException`.
  - AC-1.c: A constructor `VisitSummaryParseException(String message, Throwable cause)`
    exists.

- **AC-2: VisitSummaryParser class structure**
  - AC-2.a: `VisitSummaryParser.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryParser.java`.
  - AC-2.b: The class carries no Spring stereotype annotations (`@Component`,
    `@Service`, `@Repository`).
  - AC-2.c: A `parse(String json)` method exists with return type `VisitSummary`.

- **AC-3: Happy path — all fields present**
  - AC-3.a: `VisitSummaryParserTests.shouldParseAllFieldsFromValidJson` passes: given
    `{"summary":"Annual checkup complete","tags":["wellness","checkup"],"urgency":"routine","followUp":"Return in 12 months"}`,
    `parse()` returns a `VisitSummary` with `summary()` == `"Annual checkup complete"`,
    `tags()` == `["wellness","checkup"]`, `urgency()` == `VisitUrgency.ROUTINE`, and
    `followUp()` == `"Return in 12 months"`.

- **AC-4: Optional followUp absent**
  - AC-4.a: `VisitSummaryParserTests.shouldReturnNullFollowUpWhenAbsent` passes: given
    JSON without a `followUp` key, `parse()` returns a `VisitSummary` with
    `followUp() == null` (no exception thrown).

- **AC-5: Unknown urgency → ROUTINE fallback**
  - AC-5.a: `VisitSummaryParserTests.shouldMapUnknownUrgencyToRoutine` passes: given
    `"urgency":"critical"` (not in ROUTINE / MONITOR / URGENT), `parse()` returns a
    `VisitSummary` with `urgency() == VisitUrgency.ROUTINE`.

- **AC-6: Malformed JSON → exception**
  - AC-6.a: `VisitSummaryParserTests.shouldThrowParseExceptionForMalformedJson` passes:
    given a non-JSON string (e.g. `"not json"`), `parse()` throws
    `VisitSummaryParseException`.

- **AC-7: Tags array variants**
  - AC-7.a: `VisitSummaryParserTests.shouldHandleEmptyTagsArray` passes: given
    `"tags":[]`, `parse()` returns a `VisitSummary` where `tags()` is an empty
    (non-null) `List<String>`.
  - AC-7.b: `VisitSummaryParserTests.shouldHandleSingleTag` passes: given
    `"tags":["checkup"]`, `parse()` returns a `VisitSummary` where `tags()` has
    exactly one element `"checkup"`.
  - AC-7.c: `VisitSummaryParserTests.shouldHandleMultipleTags` passes: given
    `"tags":["wellness","checkup","annual"]`, `parse()` returns a `VisitSummary`
    where `tags()` contains all three elements in order.

- **AC-8: Existing test suite remains green**
  - AC-8.a: `./mvnw test` exits 0 with no test failures after all changes are
    applied.

## Conventions

- `VisitSummaryParser` and `VisitSummaryParseException` live in
  `org.springframework.samples.petclinic.owner`, consistent with `Visit.java` and
  `Pet.java`.
- `VisitSummaryParser` carries no Spring annotations; it is a plain Java class
  instantiated directly (`new VisitSummaryParser()`).
- Jackson `ObjectMapper` is the JSON library (available transitively via
  `spring-boot-starter-web`); no new dependency is required.
- Urgency string matching is case-insensitive: `"ROUTINE"`, `"Routine"`, and
  `"routine"` all map to `VisitUrgency.ROUTINE`.
- A missing `urgency` key (absent, not just unrecognised) is treated identically to
  an unknown value: default to `VisitUrgency.ROUTINE` with a log warning.
- `VisitSummary` and `VisitUrgency` are provided by the TASK-04 spec; that spec must
  reach `delivered` status before this spec's implementation begins.
- TDD is mandatory: `VisitSummaryParserTests.java` must be written and confirmed
  failing (RED) before `VisitSummaryParser.java` is created.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
