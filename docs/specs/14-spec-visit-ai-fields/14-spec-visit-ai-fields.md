---
status: in_progress
created: 2026-05-11
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Visit AI Fields (14)

## Goal

The AI Visit Notes Summarizer epic stores Claude-generated analysis on each
visit. Spec 12 added the five AI columns and extended `description` to
`VARCHAR(2000)` in all four database schemas. This spec maps those columns
onto the `Visit` JPA entity and introduces the `AiStatus` enum that records
the lifecycle of an async AI generation job (`PENDING → PROCESSING → DONE |
FAILED`). Until this spec is delivered, no Java code can read or write the
AI fields — it is the prerequisite for the VisitSummary DTO (TASK-04) and
all downstream service and UI work.

## Scope

### In scope

- Create `AiStatus.java` enum in `org.springframework.samples.petclinic.owner`
  with values `PENDING`, `PROCESSING`, `DONE`, `FAILED`.
- Add five new JPA-mapped fields to `Visit.java`:
  - `aiStatus` (`AiStatus`, `@Enumerated(EnumType.STRING)`, column `ai_status`, length 20)
  - `aiSummary` (`String`, column `ai_summary`, length 1000, nullable)
  - `aiTags` (`String`, column `ai_tags`, length 500, nullable)
  - `aiUrgency` (`String`, column `ai_urgency`, length 20, nullable)
  - `aiFollowUp` (`String`, column `ai_follow_up`, length 500, nullable)
- Add `@Column(length = 2000)` to the existing `description` field to match
  the extended schema from spec 12.
- Add getters and setters for all five new fields.
- Default `aiStatus` to `AiStatus.PENDING` in the `Visit()` constructor.
- Unit tests covering all new behavior (TDD: RED before GREEN).

### Out of scope

- `VisitSummary` record or `VisitUrgency` enum — those are TASK-04.
- Any controller, service, repository, or Thymeleaf template change.
- `ai_urgency` typed as an enum at the Java level — stored as `String`
  in this spec; the typed `VisitUrgency` enum is TASK-04's responsibility.
- Schema file changes — delivered by spec 12.

## Source excerpts

All source files are stable production assets; no freeze required.

- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java` —
  current entity with `date`, `description`, and no AI fields.
- `src/main/java/org/springframework/samples/petclinic/model/BaseEntity.java` —
  `@MappedSuperclass` providing `id`; `Visit` extends it.
- `src/main/resources/db/h2/schema.sql` — H2 `visits` table now includes
  all five AI columns (spec 12 delivered); used by the test-harness `@DataJpaTest`.
- `docs/epic-ai-visit-summary.md` TASK-02 — column names, types, and defaults
  that this spec implements.

## Acceptance criteria

- **AC-1: `AiStatus` enum**
  - AC-1.a: `AiStatus.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/AiStatus.java`.
  - AC-1.b: The enum declares exactly four values: `PENDING`, `PROCESSING`,
    `DONE`, `FAILED` — in that order.
  - AC-1.c: `AiStatusTest` (unit test) passes, asserting all four values are
    present via `AiStatus.values()` and that `AiStatus.valueOf("PENDING")`
    returns `AiStatus.PENDING`.

- **AC-2: `Visit` entity new fields**
  - AC-2.a: `Visit.java` declares a field `private AiStatus aiStatus` annotated
    with `@Column(name = "ai_status", length = 20)` and
    `@Enumerated(EnumType.STRING)`.
  - AC-2.b: `Visit.java` declares `private String aiSummary` annotated with
    `@Column(name = "ai_summary", length = 1000)`.
  - AC-2.c: `Visit.java` declares `private String aiTags` annotated with
    `@Column(name = "ai_tags", length = 500)`.
  - AC-2.d: `Visit.java` declares `private String aiUrgency` annotated with
    `@Column(name = "ai_urgency", length = 20)`.
  - AC-2.e: `Visit.java` declares `private String aiFollowUp` annotated with
    `@Column(name = "ai_follow_up", length = 500)`.

- **AC-3: `description` column length updated**
  - AC-3.a: The `description` field in `Visit.java` carries
    `@Column(length = 2000)` (matching the schema change from spec 12).

- **AC-4: `Visit` constructor default**
  - AC-4.a: `VisitAiFieldsTest` asserts that `new Visit().getAiStatus()`
    returns `AiStatus.PENDING`.
  - AC-4.b: `VisitAiFieldsTest` asserts that `new Visit().getAiSummary()`,
    `getAiTags()`, `getAiUrgency()`, and `getAiFollowUp()` all return `null`.

- **AC-5: Getters and setters**
  - AC-5.a: `VisitAiFieldsTest` asserts that calling `setAiStatus(AiStatus.DONE)`
    followed by `getAiStatus()` returns `AiStatus.DONE`.
  - AC-5.b: `VisitAiFieldsTest` asserts that calling `setAiSummary("test")`,
    `setAiTags("tag1,tag2")`, `setAiUrgency("URGENT")`, and
    `setAiFollowUp("Follow up")` followed by their respective getters returns
    the set values.

- **AC-6: JPA round-trip (H2 `@DataJpaTest`)**
  - AC-6.a: `VisitAiFieldsIT` (`@DataJpaTest`) persists a `Visit` with
    `aiStatus = DONE`, `aiSummary = "test summary"`, `aiTags = "tag1,tag2"`,
    `aiUrgency = "ROUTINE"`, `aiFollowUp = "Follow up note"`, then reloads it
    by ID and asserts all five values are preserved.
  - AC-6.b: `VisitAiFieldsIT` asserts that a freshly persisted `Visit` (no AI
    fields set explicitly) reads back with `aiStatus == PENDING` and the four
    nullable fields as `null`.

- **AC-7: Existing test suite remains green**
  - AC-7.a: `./mvnw test` exits 0 with no failures after all changes are
    applied.

## Conventions

- `AiStatus` lives in `org.springframework.samples.petclinic.owner` alongside
  `Visit.java` — not in `model/`, which holds only cross-domain base classes.
- `@Enumerated(EnumType.STRING)` is mandatory for `aiStatus` — ordinal storage
  would silently break if enum values are reordered.
- `aiUrgency` is a plain `String` in this spec. TASK-04 will introduce a typed
  `VisitUrgency` enum; this spec must not pre-empt that decision.
- TDD is mandatory: `AiStatusTest` and `VisitAiFieldsTest` must be written and
  confirmed failing before any production code is created; `VisitAiFieldsIT`
  must be written and confirmed failing before the entity fields are added.
- Depends on spec 12 (`ai-visits-schema`, delivered) — all five DB columns
  already exist.
- Blocks TASK-04 (`VisitSummary` DTO) — downstream specs must not start until
  this spec is `delivered`.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
