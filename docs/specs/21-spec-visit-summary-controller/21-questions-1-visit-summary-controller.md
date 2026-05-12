# Questions: VisitSummaryController (21)

## Resolved

| # | Question | Answer |
|---|----------|--------|
| Q1 | Should the controller be `@RestController` (JSON) or `@Controller` (Thymeleaf view)? | `@RestController` returning `application/json` — designed for JS polling in TASK-17. |
| Q2 | What format should the urgency field take in the JSON response? | Lowercase string: `"routine"`, `"monitor"`, `"urgent"`. The `aiUrgency` column stores uppercase (`"ROUTINE"` etc.) so lowercasing happens in the controller. |
| Q3 | Should a FAILED response include an error message field? | No — return only `{ "status": "FAILED" }`. Error details stay in server logs. |
| Q4 | How should PROCESSING status be handled? | PENDING and PROCESSING both return `{ "status": "PENDING" }`. The JS client has no meaningful distinction between the two pre-completion states. |
| Q5 | What should the `followUp` field look like when null in a DONE response? | Omit from JSON — `VisitSummaryResponse` is annotated `@JsonInclude(NON_NULL)` so null fields are silently dropped. PENDING/FAILED responses carry no `summary`, `tags`, `urgency`, or `followUp` at all. |
| Q6 | How are `tags` stored and what format does the endpoint return them in? | Stored as a comma-joined string (e.g., `"diabetes,weight"`) in `Visit.aiTags`. The controller splits on `","` and returns a `List<String>` JSON array. A null or blank `aiTags` returns `[]`. |
| Q7 | What repository does the controller use to look up a visit? | `VisitRepository.findById(Integer)` — already declared and delivered in spec-20. No new data-access code needed. |
| Q8 | Is there any authentication or authorization requirement for this endpoint? | No — the application has no authentication layer. The endpoint is public. |

## Open

None.
