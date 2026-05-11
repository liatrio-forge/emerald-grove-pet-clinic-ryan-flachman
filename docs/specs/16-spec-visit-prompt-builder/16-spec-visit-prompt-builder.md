---
status: delivered
created: 2026-05-11
last_amended: 2026-05-11
supersedes: ~
superseded_by: ~
---

# Spec: VisitPromptBuilder (16)

## Goal

Before the async AI service (TASK-10) can call Claude, it must assemble two
strings: a scoped system prompt and a visit-specific user message. This spec
introduces `PromptRequest` (an immutable two-field record that carries those
strings) and `VisitPromptBuilder` (a stateless utility class with a single
static `build` method). Having these as a dedicated, tested component keeps
prompt assembly out of the service layer and makes prompt changes easy to test
in isolation without any Spring context. This is the last prerequisite before
`VisitSummaryService` and `VisitSummaryParser` can be written.

## Scope

### In scope

- Create `PromptRequest.java` as a Java `record` in
  `org.springframework.samples.petclinic.owner` with components
  `String systemPrompt` and `String userMessage`.
- Create `VisitPromptBuilder.java` as a pure utility class (no Spring
  annotations) in the same package. Expose one public static method:
  `public static PromptRequest build(Visit visit, Pet pet)`.
- The system prompt is a fixed string stored as a private static constant in
  `VisitPromptBuilder`. It must instruct Claude to act as a clinical
  veterinary assistant and to return structured JSON containing at least
  `summary`, `tags`, `urgency`, and `followUp` fields. The exact JSON schema
  (types, constraints) is owned by the future `VisitSummaryParser` spec.
- The user message is assembled from the user message template (see
  Conventions) using data from the supplied `Visit` and `Pet`.
- Unit tests `PromptRequestTest` and `VisitPromptBuilderTest` following strict
  TDD (RED before GREEN). Tests live in the `owner` test package.

### Out of scope

- `VisitSummaryParser`, `VisitSummaryService`, `ClaudeApiClient` — downstream
  specs that consume `PromptRequest` but are not part of this spec.
- Internationalisation of the system prompt or user message.
- Any retry or caching logic.
- Modifications to `Visit.java` or `Pet.java`.

## Source excerpts

All source material is stable production code delivered in prior specs.

- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java` —
  entity providing `getDate()` (LocalDate), `getDescription()` (String), and
  the five AI fields added in spec-14.
- `src/main/java/org/springframework/samples/petclinic/owner/Pet.java` —
  entity providing `getName()` (via `NamedEntity`), `getBirthDate()`
  (LocalDate, nullable), and `getType()` (PetType, nullable).
- `src/main/java/org/springframework/samples/petclinic/owner/VisitSummary.java`
  — sibling record (spec-15); `PromptRequest` follows the same package and
  style conventions.
- `docs/epic-ai-visit-summary.md` TASK-05 — canonical description of the
  prompt builder contract and user message template.

## Acceptance criteria

- **AC-1: `PromptRequest` record exists and is correctly shaped**
  - AC-1.a: `PromptRequest.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/PromptRequest.java`.
  - AC-1.b: `PromptRequest` is declared as a Java `record` (not a class) with
    exactly two components in order: `String systemPrompt`, `String userMessage`.
  - AC-1.c: `PromptRequest` is in package
    `org.springframework.samples.petclinic.owner`.
  - AC-1.d: `PromptRequestTest` asserts that constructing with non-null
    arguments returns accessors `systemPrompt()` and `userMessage()` equal to
    the supplied values.

- **AC-2: `VisitPromptBuilder` class exists and is correctly shaped**
  - AC-2.a: `VisitPromptBuilder.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java`.
  - AC-2.b: `VisitPromptBuilder` carries no Spring annotations (`@Component`,
    `@Service`, `@Bean`, etc.) — verified by `grep -r "@Component\|@Service\|@Bean"
    src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java`
    returning no matches.
  - AC-2.c: `VisitPromptBuilder` exposes exactly one public method:
    `public static PromptRequest build(Visit visit, Pet pet)`.
  - AC-2.d: `./mvnw compile` exits 0.

- **AC-3: System prompt content**
  - AC-3.a: `VisitPromptBuilderTest` asserts that `result.systemPrompt()` is
    non-null and non-blank.
  - AC-3.b: `VisitPromptBuilderTest` asserts that `result.systemPrompt()`
    contains (case-insensitive substring) each of `"summary"`, `"tags"`,
    `"urgency"`, `"followUp"` — confirming the JSON field names are named in
    the prompt (exact schema owned by the VisitSummaryParser spec).
  - AC-3.c: `VisitPromptBuilderTest` asserts that `result.systemPrompt()`
    contains a substring indicating JSON output (e.g. `"json"` case-insensitive).

- **AC-4: User message template — happy path**
  - AC-4.a: `VisitPromptBuilderTest` asserts the user message contains the
    pet's name (from `pet.getName()`).
  - AC-4.b: `VisitPromptBuilderTest` asserts the user message contains the
    pet type's name (from `pet.getType().getName()`).
  - AC-4.c: `VisitPromptBuilderTest` asserts the user message contains the
    correctly computed whole-year age. Given a pet born exactly 3 years before
    the visit date, the message contains `"3"`.
  - AC-4.d: `VisitPromptBuilderTest` asserts the user message contains the
    visit date formatted as `yyyy-MM-dd` (ISO local date).
  - AC-4.e: `VisitPromptBuilderTest` asserts the user message contains the
    visit description.

- **AC-5: Edge-case handling**
  - AC-5.a: `VisitPromptBuilderTest` asserts that when `pet.getBirthDate()` is
    `null`, the user message contains the string `"unknown"` in the age position
    and does not throw.
  - AC-5.b: `VisitPromptBuilderTest` asserts that when `visit.getDescription()`
    is `null`, the user message contains `"(no description provided)"` and does
    not throw.
  - AC-5.c: `VisitPromptBuilderTest` asserts that when `visit.getDescription()`
    is blank (e.g., `"   "`), the user message contains
    `"(no description provided)"` and does not throw.
  - AC-5.d: `VisitPromptBuilderTest` asserts that when `pet.getType()` is
    `null`, the user message contains `"unknown"` in the pet-type position and
    does not throw.

- **AC-6: TDD compliance**
  - AC-6.a: Proof artifact for Task 01 captures a Maven test-run output showing
    `PromptRequestTest` and `VisitPromptBuilderTest` failing before any
    production code exists (RED phase).

- **AC-7: Existing test suite remains green**
  - AC-7.a: `./mvnw test` exits 0 with no failures after all changes are
    applied.

## Conventions

- `PromptRequest` is a plain Java `record` with no Spring, JPA, or validation
  annotations. It carries no null-contract enforcement — the builder always
  supplies non-null strings.
- `VisitPromptBuilder` holds the system prompt as a `private static final
  String SYSTEM_PROMPT` constant. The user message is assembled inline inside
  `build()`.
- Age computation: `ChronoUnit.YEARS.between(pet.getBirthDate(),
  visit.getDate())`. If `pet.getBirthDate()` is null, emit `"unknown"` instead
  of computing.
- If `pet.getType()` is null or `pet.getType().getName()` is null/blank, emit
  `"unknown"` in the type position.
- If `visit.getDescription()` is null or `visit.getDescription().isBlank()`,
  substitute `"(no description provided)"`.
- User message format (exact wording must match this template):

  ```text
  Pet: {name}, {type}, age {age} years
  Visit date: {date}
  Visit notes: "{description}"
  ```

  where `{age}` is a whole number (or `"unknown"`) and `{date}` is ISO
  `yyyy-MM-dd`.
- TDD is mandatory: `PromptRequestTest` and `VisitPromptBuilderTest` must be
  written and confirmed failing (RED) before any production code is created.
- Depends on spec-15 (`visit-summary-dto`, delivered) — `VisitSummary` and
  `VisitUrgency` are already in the same package.
- Blocks the `VisitSummaryParser` (TASK-06) and `VisitSummaryService` (TASK-10)
  specs — they must not begin until this spec is `delivered`.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
