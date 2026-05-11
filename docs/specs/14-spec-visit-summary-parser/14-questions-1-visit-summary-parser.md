# Questions: VisitSummaryParser (14) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | Should `VisitSummary.urgency` use `AiStatus`, a new enum, or a plain `String`? | **New `VisitUrgency` enum** with values ROUTINE, MONITOR, URGENT. `AiStatus` tracks pipeline lifecycle (PENDING / PROCESSING / DONE / FAILED); `VisitUrgency` carries clinical severity from the AI response. These are separate concerns. |
| Q-2 | What JSON key casing should Claude's response use? | **camelCase** (`summary`, `tags`, `urgency`, `followUp`). Matches Jackson's default `ObjectMapper` and Java field naming without custom `@JsonProperty` mappings. |
| Q-3 | Should `VisitSummaryParseException` be checked or unchecked? | **Unchecked** (`extends RuntimeException`). `VisitSummaryService` (TASK-10) catches it explicitly and sets `aiStatus = FAILED`. No checked-exception propagation through the call chain. |
| Q-4 | Where should `VisitSummaryParser` and `VisitSummaryParseException` be placed? | **`org.springframework.samples.petclinic.owner`** package, flat alongside `Visit.java` and `Pet.java`. Consistent with TASK-04's placement of `VisitSummary`. |
| Q-5 | What JSON library should be used for parsing? | **Jackson `ObjectMapper`** (available transitively via `spring-boot-starter-web`). No new dependency needed. |
| Q-6 | What happens when the `urgency` key is present but its value is unrecognised? | Map to `VisitUrgency.ROUTINE` and emit a log warning. Epic TASK-06 explicitly lists this as a required test scenario. |
| Q-7 | What happens when the `urgency` key is absent from the JSON entirely? | Same as unknown value: default to `VisitUrgency.ROUTINE` with a log warning. A missing key and an unrecognised value are treated identically. |
| Q-8 | Is `followUp` the only optional field? | Yes. `summary`, `tags`, and `urgency` are treated as required fields. Only `followUp` may be absent without throwing an exception. |

## Open

None.
