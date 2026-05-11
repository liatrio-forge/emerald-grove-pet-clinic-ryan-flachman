---
status: delivered
created: 2026-05-11
last_amended: 2026-05-11
supersedes: ~
superseded_by: ~
---

# Spec: ClaudeApiClientStub (18)

## Goal

`VisitSummaryService` (TASK-10) needs a working `ClaudeApiClient` bean that
requires no real Anthropic API key. This spec delivers `ClaudeApiClientStub`,
a Spring component that activates automatically when `anthropic.api.key` is
blank or absent and returns deterministic canned JSON strings — one for each
clinically distinct keyword pattern ("checkup", "limp", "pain", or no known
keyword). The stub lets the full AI visit-summary UI flow be exercised in local
development and in integration tests without any external network dependency.

## Scope

### In scope

- `ClaudeApiClientStub.java` — `@Component` in
  `org.springframework.samples.petclinic.owner`, active via
  `@ConditionalOnExpression("'${anthropic.api.key:}'.trim().isEmpty()")`.
- Keyword matching: case-insensitive substring search of `userMessage` for
  `"limp"` and `"pain"` (→ URGENT), then `"checkup"` (→ ROUTINE), then default
  MONITOR. Urgent takes precedence over checkup.
- Four canned JSON strings (one per urgency branch) returned as valid JSON
  shaped consistently with the `VisitSummary` parser contract (see **Contract**
  below).
- `ClaudeApiClientStubTests.java` — JUnit 5 unit tests covering every canned
  path, edge cases (mixed case, multiple keywords, null/blank input), and the
  conditional activation annotation.

### Out of scope

- `ClaudeApiClientImpl` — TASK-09 (a separate spec).
- `VisitSummaryParser` — TASK-06 (a separate spec); parsing is not done here.
- Any database, Spring MVC, or Thymeleaf concern.
- Integration tests that wire the stub into the full Spring context — those
  belong to TASK-13 (spec to be written later).
- `VisitUrgency` enum definition — owned by the `VisitSummary`/parser spec
  (TASK-04 / TASK-06). The stub uses the string values `"ROUTINE"`,
  `"MONITOR"`, `"URGENT"` directly in its canned JSON.

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClient.java`
  (spec-17, delivered) — the single-method interface this class implements.
- `src/main/resources/application.properties` — `anthropic.api.key` property
  definition with blank default; determines the `@ConditionalOnExpression`
  expression.
- `docs/epic-ai-visit-summary.md` TASK-08 — canonical description of keyword
  routing and the role of the stub in unblocking TASK-10 and TASK-13.

## Contract

This spec defines the JSON format produced by all `ClaudeApiClient`
implementations. The `VisitSummaryParser` spec (TASK-06) is the downstream
consumer and **must** parse strings shaped as follows:

```json
{
  "summary": "<non-blank, human-readable summary string>",
  "tags":    ["<tag1>", "<tag2>"],
  "urgency": "ROUTINE | MONITOR | URGENT",
  "follow_up": "<nullable follow-up instruction or null>"
}
```

**Canned response catalogue** (exact strings emitted by the stub):

| Trigger | `urgency` | `tags` example |
|---------|-----------|----------------|
| `userMessage` contains "limp" (case-insensitive) | `"URGENT"` | `["limping","orthopedic","urgent"]` |
| `userMessage` contains "pain" (case-insensitive) | `"URGENT"` | `["pain","discomfort","urgent"]` |
| `userMessage` contains "checkup" (case-insensitive) | `"ROUTINE"` | `["checkup","annual","routine"]` |
| No recognized keyword | `"MONITOR"` | `["general","monitor"]` |

When both an urgent keyword and "checkup" appear, the URGENT branch wins.
The `systemPrompt` argument is ignored by the stub.

## Acceptance criteria

- **AC-1: Class structure**
  - AC-1.a: `ClaudeApiClientStub.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientStub.java`.
  - AC-1.b: The class is annotated `@Component` — verified by
    `grep "@Component" src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientStub.java`
    returning a match.
  - AC-1.c: The class is annotated
    `@ConditionalOnExpression("'${anthropic.api.key:}'.trim().isEmpty()")` —
    verified by grep returning a match.
  - AC-1.d: The class declaration includes `implements ClaudeApiClient` — verified by grep.
  - AC-1.e: `./mvnw compile` exits 0.

- **AC-2: URGENT branch — "limp"**
  - AC-2.a: `ClaudeApiClientStubTests#completeWithLimpReturnsUrgentJson` passes:
    calling `complete("", "Dog is limping on left front leg")` returns a JSON
    string whose `urgency` field equals `"URGENT"`.

- **AC-3: URGENT branch — "pain"**
  - AC-3.a: `ClaudeApiClientStubTests#completeWithPainReturnsUrgentJson` passes:
    calling `complete("", "Cat seems to be in pain after eating")` returns a JSON
    string whose `urgency` field equals `"URGENT"`.

- **AC-4: ROUTINE branch — "checkup"**
  - AC-4.a: `ClaudeApiClientStubTests#completeWithCheckupReturnsRoutineJson` passes:
    calling `complete("", "Annual checkup for Max")` returns a JSON string whose
    `urgency` field equals `"ROUTINE"`.

- **AC-5: Default branch — MONITOR**
  - AC-5.a: `ClaudeApiClientStubTests#completeWithUnknownKeywordReturnsMonitorJson`
    passes: calling `complete("", "Seems a bit tired lately")` returns a JSON
    string whose `urgency` field equals `"MONITOR"`.

- **AC-6: Precedence — urgent beats checkup**
  - AC-6.a: `ClaudeApiClientStubTests#completeWithBothLimpAndCheckupReturnsUrgent`
    passes: calling `complete("", "Annual checkup; dog is limping")` returns
    urgency `"URGENT"`.

- **AC-7: Case-insensitive matching**
  - AC-7.a: `ClaudeApiClientStubTests#completeIsCaseInsensitive` passes: calling
    `complete("", "DOG IS LIMPING")` returns urgency `"URGENT"`.

- **AC-8: Blank / null userMessage**
  - AC-8.a: `ClaudeApiClientStubTests#completeWithBlankMessageReturnsMonitorJson`
    passes: calling `complete("", "")` returns urgency `"MONITOR"` without
    throwing an exception.
  - AC-8.b: `ClaudeApiClientStubTests#completeWithNullMessageReturnsMonitorJson`
    passes: calling `complete("", null)` returns urgency `"MONITOR"` without
    throwing a `NullPointerException`.

- **AC-9: Valid JSON structure**
  - AC-9.a: All test cases parse the returned string with
    `new ObjectMapper().readTree(result)` and assert that `summary`, `tags`,
    `urgency`, and `follow_up` nodes are present.
  - AC-9.b: The `tags` node is a JSON array with at least one element in every
    canned response.

- **AC-10: TDD compliance**
  - AC-10.a: Proof artifact for Task 01 captures Maven test output showing
    `ClaudeApiClientStubTests` failing to compile or run (RED phase) before
    `ClaudeApiClientStub.java` is created.

- **AC-11: Existing tests remain green**
  - AC-11.a: `./mvnw test` exits 0 with zero test failures after all changes
    are applied.

## Conventions

- Class lives in `org.springframework.samples.petclinic.owner`. No new package.
- Use `@ConditionalOnExpression`, not `@ConditionalOnProperty`, because
  `@ConditionalOnProperty` does not reliably match blank strings. See
  `18-questions-1-claude-api-client-stub.md` Q1.
- The `systemPrompt` parameter is accepted but not inspected; document with an
  inline comment explaining this is intentional.
- Canned JSON strings may be private static final constants or produced by a
  private helper — the implementer decides. Either approach is acceptable as long
  as all ACs pass.
- Test class uses `@ExtendWith(MockitoExtension.class)` or plain JUnit 5; no
  Spring context needed.
- Tests assert JSON content by parsing with `ObjectMapper`
  (`com.fasterxml.jackson.databind.ObjectMapper`, already on the classpath).
- Follows strict TDD: `ClaudeApiClientStubTests.java` must exist and all tests
  must fail (RED) before `ClaudeApiClientStub.java` is created.
- Blocks TASK-10 (`VisitSummaryService`) and TASK-13 (integration test); this
  spec must reach `delivered` before those are started.
- Upstream contract spec: **spec-17** (`ClaudeApiClient` interface). Do not
  redefine the interface signature.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
| 2026-05-11 | delivery | Delivered `ClaudeApiClientStub`, `ClaudeApiClientStubTests`, proofs, validation; MySQL schema/data alignment for AI visit columns | — |
