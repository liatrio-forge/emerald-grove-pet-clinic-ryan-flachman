# Questions: VisitPromptBuilder (16)

## Resolved

| # | Question | Decision | Rationale |
|---|----------|----------|-----------|
| Q-1 | Should spec-16 lock down the exact JSON schema the system prompt instructs Claude to return? | **No — schema owned by VisitSummaryParser spec (TASK-06)** | The parser spec is the consumer; it should own the contract. Spec-16 specifies only the narrative direction ("return structured JSON with summary, tags, urgency, followUp fields"). |
| Q-2 | How should `VisitPromptBuilder` handle `Pet.getBirthDate() == null`? | **Emit the string `"unknown"` in the age position** | Keeps the prompt well-formed so Claude still processes the visit. Avoids NPE and misleading `0`. |
| Q-3 | What should `VisitPromptBuilder` do when `Visit.getDescription()` is null or blank? | **Substitute the literal string `"(no description provided)"`** | Visit already carries `@NotBlank` so this should not occur in production, but defensive handling keeps the builder safe and testable. |
| Q-4 | Package location for `PromptRequest` and `VisitPromptBuilder`? | **`org.springframework.samples.petclinic.owner`** | Matches all existing AI types (`AiStatus`, `VisitSummary`, `VisitUrgency`). No sub-package needed at this stage. |
| Q-5 | Static method vs instance method on `VisitPromptBuilder`? | **Single public static method `build(Visit, Pet)`** | Consistent with "pure utility class, no Spring annotations" in the epic. No instantiation overhead; simple to call from `VisitSummaryService`. |
| Q-6 | What if `Pet.getType()` is null? | **Emit `"unknown"` in the pet-type position** | Same defensive pattern as null birthDate. `PetType` is always set in the sample data, but the builder must not NPE. |

## Open

None.
