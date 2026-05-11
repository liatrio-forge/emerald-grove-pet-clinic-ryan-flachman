# Tasks: VisitPromptBuilder (16)

## Task 01 — Write failing tests for PromptRequest and VisitPromptBuilder (RED)

Covers: AC-1.d, AC-2.b, AC-2.c, AC-3.a, AC-3.b, AC-3.c, AC-4.a, AC-4.b,
AC-4.c, AC-4.d, AC-4.e, AC-5.a, AC-5.b, AC-5.c, AC-5.d, AC-6.a

**May break compile, fixed by:** Task 02 and Task 03

- Create
  `src/test/java/org/springframework/samples/petclinic/owner/PromptRequestTest.java`:
  - `shouldStoreSystemPromptAndUserMessage()` — construct with two non-null
    strings, assert `systemPrompt()` and `userMessage()` return the supplied
    values.
- Create
  `src/test/java/org/springframework/samples/petclinic/owner/VisitPromptBuilderTest.java`:
  - Helper: build a fully populated `Pet` (name, type, birthDate 3 years before
    a fixed visit date) and `Visit` (fixed date, non-blank description) for
    happy-path tests.
  - `shouldReturnNonBlankSystemPrompt()` — asserts `result.systemPrompt()` is
    not blank.
  - `shouldMentionJsonFieldsInSystemPrompt()` — asserts `result.systemPrompt()`
    contains each of `"summary"`, `"tags"`, `"urgency"`, `"followUp"`
    (case-insensitive).
  - `shouldMentionJsonOutputInSystemPrompt()` — asserts
    `result.systemPrompt()` contains `"json"` (case-insensitive).
  - `shouldIncludePetNameInUserMessage()` — asserts user message contains the
    pet's name.
  - `shouldIncludePetTypeInUserMessage()` — asserts user message contains the
    pet type name.
  - `shouldIncludeCorrectAgeInYearsInUserMessage()` — pet born exactly 3 years
    before visit date; asserts user message contains `"3"`.
  - `shouldIncludeVisitDateIsoInUserMessage()` — asserts user message contains
    the visit date in `yyyy-MM-dd` format.
  - `shouldIncludeDescriptionInUserMessage()` — asserts user message contains
    the visit description text.
  - `shouldUseUnknownForNullBirthDate()` — pet with null birthDate; asserts user
    message contains `"unknown"` and no exception is thrown.
  - `shouldUsePlaceholderForNullDescription()` — visit with null description;
    asserts user message contains `"(no description provided)"`.
  - `shouldUsePlaceholderForBlankDescription()` — visit with `"   "` description;
    asserts user message contains `"(no description provided)"`.
  - `shouldUseUnknownForNullPetType()` — pet with null type; asserts user message
    contains `"unknown"` and no exception is thrown.
- Run `./mvnw test -Dtest="PromptRequestTest,VisitPromptBuilderTest"` (will
  fail to compile — expected RED). Capture the failure output in the proof file.

**Proof:** 16-proofs/16-task-01-proofs.md

---

## Task 02 — Implement PromptRequest record (GREEN)

Covers: AC-1.a, AC-1.b, AC-1.c, AC-1.d

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/PromptRequest.java`:
  - `package org.springframework.samples.petclinic.owner;`
  - `public record PromptRequest(String systemPrompt, String userMessage) {}`
  - No Spring, JPA, or validation annotations.
  - No compact constructor needed — the builder always supplies non-null values.
- Run `./mvnw test -Dtest="PromptRequestTest"` — assert all `PromptRequestTest`
  cases pass (GREEN for the record).
- `VisitPromptBuilderTest` still fails to compile at this point (expected — fixed
  by Task 03).

**Proof:** 16-proofs/16-task-02-proofs.md

---

## Task 03 — Implement VisitPromptBuilder static utility (GREEN + REFACTOR)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-3.c, AC-4.a,
AC-4.b, AC-4.c, AC-4.d, AC-4.e, AC-5.a, AC-5.b, AC-5.c, AC-5.d

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java`:
  - `package org.springframework.samples.petclinic.owner;`
  - No Spring annotations.
  - `private static final String SYSTEM_PROMPT` constant holding the full system
    prompt. The prompt must:
    - Identify the assistant role as a clinical veterinary assistant.
    - Instruct Claude to return only JSON (no prose outside the JSON).
    - Name all four output fields: `summary`, `tags`, `urgency`, `followUp`.
  - `public static PromptRequest build(Visit visit, Pet pet)` method:
    - Compute `age`:
      - If `pet.getBirthDate() == null` → `age = "unknown"`.
      - Otherwise → `age = String.valueOf(ChronoUnit.YEARS.between(pet.getBirthDate(), visit.getDate()))`.
    - Compute `typeName`:
      - If `pet.getType() == null` or `pet.getType().getName()` is null/blank
        → `typeName = "unknown"`.
      - Otherwise → `typeName = pet.getType().getName()`.
    - Compute `description`:
      - If `visit.getDescription() == null` or `visit.getDescription().isBlank()`
        → `description = "(no description provided)"`.
      - Otherwise → `description = visit.getDescription()`.
    - Assemble user message using the exact template from the spec Conventions
      section.
    - Return `new PromptRequest(SYSTEM_PROMPT, userMessage)`.
- Run `./mvnw test -Dtest="PromptRequestTest,VisitPromptBuilderTest"` — all
  cases must pass (GREEN).
- REFACTOR: extract repeated null/blank guards into private static helpers if
  there is duplication; keep `build()` readable. Re-run tests to confirm still
  GREEN.
- Run `./mvnw compile` to confirm AC-2.d.
- Run `grep -r "@Component\|@Service\|@Bean" src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java`
  — confirm no output (AC-2.b).

**Proof:** 16-proofs/16-task-03-proofs.md

---

## Task 04 — Validate and capture proof artifacts

Covers: all ACs

- Run `./mvnw test` from repository root and capture full output. Assert exit
  code 0 and zero failures (AC-7.a).
- Run `./mvnw compile` and capture output (AC-2.d).
- Run Spring-annotation grep (AC-2.b) and capture empty output.
- Confirm `PromptRequest.java` exists at the expected path (AC-1.a).
- Confirm `VisitPromptBuilder.java` exists at the expected path (AC-2.a).
- Fill all four proof files with real command output (no placeholder text).
- Update the coverage matrix in `16-validation-visit-prompt-builder.md` —
  transition all `PENDING` rows to `PASS`.
- Add spec-16 row to `docs/specs/README.md`.

**Proof:** 16-proofs/16-task-04-proofs.md
