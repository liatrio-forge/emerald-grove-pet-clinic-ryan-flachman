# Questions: Visit AI Fields (14)

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | Which package does `AiStatus` live in? | `org.springframework.samples.petclinic.owner` — same package as `Visit.java` |
| Q-2 | What are the four enum values for `AiStatus`? | `PENDING`, `PROCESSING`, `DONE`, `FAILED` |
| Q-3 | What is the default value of `aiStatus` in a new `Visit`? | `PENDING`, set in the no-arg constructor |
| Q-4 | How is `ai_status` stored in the DB column — as the enum name or an ordinal? | `@Enumerated(EnumType.STRING)` — stored as the enum name (`VARCHAR(20)`) |
| Q-5 | Does `description` need a JPA `@Column(length = 2000)` annotation? | Yes — the schema column is now `VARCHAR(2000)` (spec 12); the entity must declare the matching length so JPA validation and schema export stay consistent |
| Q-6 | Are the four nullable AI string fields (`ai_summary`, `ai_tags`, `ai_urgency`, `ai_follow_up`) nullable at the Java level? | Yes — no `@NotNull`; nullable by default |
| Q-7 | Does this spec modify any controller, service, or Thymeleaf template? | No — entity and enum only; downstream layers are separate specs |
| Q-8 | Does this spec depend on TASK-01 (spec 12) being delivered? | Yes — spec 12 is `delivered`; the DB columns exist |
| Q-9 | What does `ai_urgency` represent at the DB level? | A `VARCHAR(20)` string; the higher-level `VisitUrgency` enum (TASK-04) maps it; this spec stores it as a plain `String` field |
| Q-10 | Is there an existing `VisitSchemaIT` test from spec 12 we must keep green? | Yes — `VisitsSchemaIT.java` was delivered by spec 12 and must continue to pass |

## Open

_None._
