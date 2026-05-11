---
status: accepted
created: 2026-05-11
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: VisitSummary DTO (15)

## Goal

The AI Visit Notes Summarizer epic (spec 14 delivered) stores Claude-generated
analysis on each `Visit`. Before any parsing or service logic can be written,
a shared DTO is needed to carry the parsed Claude response through the system.
This spec introduces `VisitUrgency` (the three-value clinical urgency enum) and
`VisitSummary` (the immutable record holding a summary text, tag list, urgency
level, and optional follow-up note). These two types are the data contract
between `VisitSummaryParser` (TASK-06), `VisitSummaryService` (TASK-10), and
the polling controller (TASK-11); nothing downstream can be built until they exist.

## Scope

### In scope

- Create `VisitUrgency.java` enum in `org.springframework.samples.petclinic.owner`
  with values `ROUTINE`, `MONITOR`, `URGENT`.
- Create `VisitSummary.java` as a Java `record` in the same package with
  components `String summary`, `List<String> tags`, `VisitUrgency urgency`,
  `String followUp`.
- Compact constructor on `VisitSummary` that:
  - throws `NullPointerException` if `summary`, `tags`, or `urgency` is null.
  - replaces `tags` with `List.copyOf(tags)` for immutability.
- Unit tests for `VisitUrgency` (`VisitUrgencyTest`) and `VisitSummary`
  (`VisitSummaryTest`) following strict TDD (RED before GREEN).

### Out of scope

- `VisitSummaryParser`, `VisitPromptBuilder`, `ClaudeApiClient`, or any
  service/controller code — those are downstream specs.
- Modifications to `Visit.java` — `aiUrgency` stays a plain `String` on the
  entity; `VisitUrgency` is a DTO-layer type only.
- Serialization annotations (`@JsonProperty`, etc.) — the parser and controller
  specs own that concern.
- Persistence — `VisitSummary` is never stored directly; it is a transient DTO.

## Source excerpts

All source material is stable production code; no freeze required.

- `src/main/java/org/springframework/samples/petclinic/owner/AiStatus.java` —
  the existing four-value lifecycle enum; `VisitUrgency` follows the same
  package and style conventions but is a distinct type.
- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java` —
  entity whose `aiUrgency` field (plain `String`) this spec does not change.
- `docs/epic-ai-visit-summary.md` TASK-04 — canonical description of the DTO
  contract and field semantics.

## Acceptance criteria

- **AC-1: `VisitUrgency` enum exists and is correct**
  - AC-1.a: `VisitUrgency.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/VisitUrgency.java`.
  - AC-1.b: The enum declares exactly three values: `ROUTINE`, `MONITOR`,
    `URGENT` — in that order.
  - AC-1.c: `VisitUrgencyTest` passes: asserts `VisitUrgency.values().length == 3`,
    and `VisitUrgency.valueOf("ROUTINE")` returns `VisitUrgency.ROUTINE`,
    `valueOf("MONITOR")` returns `VisitUrgency.MONITOR`,
    `valueOf("URGENT")` returns `VisitUrgency.URGENT`.

- **AC-2: `VisitSummary` record exists and is correctly shaped**
  - AC-2.a: `VisitSummary.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/VisitSummary.java`.
  - AC-2.b: `VisitSummary` is declared as a `record` (not a class) with
    exactly four components in order: `String summary`, `List<String> tags`,
    `VisitUrgency urgency`, `String followUp`.
  - AC-2.c: `VisitSummary` is in package
    `org.springframework.samples.petclinic.owner`.

- **AC-3: Construction and nullability contracts**
  - AC-3.a: `VisitSummaryTest` asserts that constructing with all non-null
    arguments returns accessors equal to the supplied values:
    `summary()`, `tags()`, `urgency()`, `followUp()`.
  - AC-3.b: `VisitSummaryTest` asserts that constructing with `followUp = null`
    succeeds and `followUp()` returns `null`.
  - AC-3.c: `VisitSummaryTest` asserts that constructing with an empty
    `List.of()` as `tags` succeeds and `tags()` returns an empty list.
  - AC-3.d: `VisitSummaryTest` asserts that constructing with `summary = null`
    throws `NullPointerException`.
  - AC-3.e: `VisitSummaryTest` asserts that constructing with `tags = null`
    throws `NullPointerException`.
  - AC-3.f: `VisitSummaryTest` asserts that constructing with `urgency = null`
    throws `NullPointerException`.

- **AC-4: Tags list is immutably copied**
  - AC-4.a: `VisitSummaryTest` asserts that mutating the original `List<String>`
    passed into the constructor does not change `visitSummary.tags()` — i.e.,
    the record holds a defensive copy.
  - AC-4.b: `VisitSummaryTest` asserts that calling `.add()` on the list
    returned by `tags()` throws `UnsupportedOperationException` — the exposed
    list is unmodifiable.

- **AC-5: Existing test suite remains green**
  - AC-5.a: `./mvnw test` exits 0 with no failures after all changes are
    applied.

## Conventions

- `VisitUrgency` lives in `org.springframework.samples.petclinic.owner`
  alongside `AiStatus.java` — not in `model/`.
- `VisitSummary` is a Java `record`, not a class, to make immutability intent
  explicit.
- The compact constructor uses `Objects.requireNonNull` (or equivalent) for
  `summary`, `tags`, and `urgency`; `followUp` is explicitly allowed to be null.
- `List.copyOf(tags)` is called in the compact constructor to replace the
  incoming list with an unmodifiable snapshot. This satisfies both AC-4.a
  (defensive copy) and AC-4.b (unmodifiable accessor).
- No Spring, JPA, or validation annotations on `VisitSummary` — it is a
  plain Java record.
- TDD is mandatory: `VisitUrgencyTest` and `VisitSummaryTest` must be written
  and confirmed failing before any production code is created (RED phase first).
- Depends on spec 14 (`visit-ai-fields`, delivered) — `AiStatus` is in the
  same package; `Visit.aiUrgency` is a `String` and stays that way.
- Blocks downstream specs for `VisitPromptBuilder`, `VisitSummaryParser`, and
  `ClaudeApiClient` — they must not be started until this spec is `delivered`.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
