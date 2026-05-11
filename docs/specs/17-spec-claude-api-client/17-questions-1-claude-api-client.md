# Questions: ClaudeApiClient Interface (17)

## Resolved

| # | Question | Decision | Rationale |
|---|----------|----------|-----------|
| Q-1 | Package location for interface and POJOs? | **`org.springframework.samples.petclinic.owner`** | Matches all existing AI domain types (`AiStatus`, `PromptRequest`, `VisitPromptBuilder`). No sub-package needed at this stage. |
| Q-2 | Should `Message` (the per-turn request object) be defined in this spec or in the implementation spec? | **Here, in spec-17** | `ClaudeRequest` holds a `List<Message>`; the type must exist in the same compilation unit as the request record. |
| Q-3 | Inner classes vs. top-level classes for the POJOs? | **Top-level records** | Consistent with the project pattern (`PromptRequest`, `AiStatus` are all top-level types). Easier to mock/reference from downstream specs. |
| Q-4 | Jackson snake_case mapping strategy: class-level `@JsonNaming` or per-field `@JsonProperty`? | **Per-field `@JsonProperty` only where the Java name differs from the JSON key** | Only two fields differ: `maxTokens → "max_tokens"` and `stopReason → "stop_reason"`. Explicit annotations are self-documenting and avoid applying a strategy that would silently rename other fields. |
| Q-5 | Should `ClaudeApiException` be defined in this spec? | **No** | The exception belongs with the concrete implementations (TASK-08 stub, TASK-09 real client). The interface contract is just the return type `String`. |
| Q-6 | Should `ClaudeResponse` include a `usage` field? | **No** | `VisitSummaryService` only needs `content[0].text`. Adding `usage` now would require defining a `Usage` POJO for no current consumer. Can be added by amendment when needed. |
| Q-7 | Does TASK-07 have a strict compile-time dependency on TASK-04 (VisitSummary DTO)? | **No** | `ClaudeApiClient.complete()` returns `String`; none of the four POJOs reference `VisitSummary` or `VisitUrgency`. The dependency listed in the epic is a logical wave ordering, not a code dependency. This spec can be implemented independently of spec-15. |
| Q-8 | What `max_tokens` default or cap should `ClaudeRequest` enforce? | **None — `maxTokens` is a plain constructor parameter** | The calling code (TASK-09 impl) chooses the value; the record is a data carrier, not a policy enforcer. |

## Open

None.
