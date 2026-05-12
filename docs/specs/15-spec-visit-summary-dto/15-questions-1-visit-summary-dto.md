# Questions: VisitSummary DTO (15)

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | Which package does `VisitUrgency` live in? | `org.springframework.samples.petclinic.owner` — same package as `AiStatus.java` and `Visit.java` |
| Q-2 | Should `VisitUrgency` be a new enum separate from `AiStatus`? | Yes — `VisitUrgency` has three values (ROUTINE, MONITOR, URGENT) representing clinical urgency of a visit; `AiStatus` represents the async job lifecycle and must not be reused |
| Q-3 | Which fields on `VisitSummary` are required (non-null)? | `summary`, `tags`, and `urgency` are required; `followUp` is optional and may be null |
| Q-4 | Can the `tags` list be mutated externally after construction? | No — the compact constructor must call `List.copyOf(tags)` so the record holds an unmodifiable snapshot |
| Q-5 | Does `VisitSummary` carry any JPA, validation, or Spring annotations? | No — it is a pure Java record with no framework annotations |
| Q-6 | Does this spec modify `Visit.java` or change how `aiUrgency` is stored? | No — `Visit.aiUrgency` remains a plain `String` (spec 14 convention); `VisitUrgency` is used only in the DTO layer |
| Q-7 | What is the canonical test class name for `VisitSummary`? | `VisitSummaryTest` in `src/test/java/org/springframework/samples/petclinic/owner/` |
| Q-8 | Does `VisitSummary` need a compact constructor to enforce non-null invariants? | Yes — the compact constructor should validate that `summary`, `tags`, and `urgency` are non-null (throw `NullPointerException` or `IllegalArgumentException`) and defensively copy `tags` via `List.copyOf()` |
| Q-9 | What test class covers `VisitUrgency`? | `VisitUrgencyTest` in the same test package |
| Q-10 | Does this spec block any other spec? | Yes — spec 16 (VisitPromptBuilder), spec 17 (VisitSummaryParser), and spec 18 (ClaudeApiClient interface) all depend on `VisitSummary` and `VisitUrgency` existing |

## Open

_None._
