# Questions: Upcoming Visits Page (09) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | Does "upcoming" include today, or only strictly future visits? | **Include today** — the window is `[today, today + N days]` inclusive of today. A visit scheduled for today appears in the list. |
| Q-2 | How should visits be fetched, given no `VisitRepository` currently exists? | **New `VisitRepository`** — a Spring Data JPA repository extending `Repository<Visit, Integer>` with a JPQL `@Query` that joins through `Pet.visits` → `Pet.owner`. No modification to `Visit`, `Pet`, or `Owner` entities is permitted. |
| Q-3 | Where should the new `/visits/upcoming` endpoint live? | **New `UpcomingVisitsController`** at `@RequestMapping("/visits")` — the existing `VisitController` is scoped to `/owners/{ownerId}/pets/{petId}/visits/...` and should not be extended with cross-cutting concerns. |
| Q-4 | Should a nav link be added to the main navigation bar? | **Yes** — an "Upcoming Visits" item added alongside Find Owners and Veterinarians. |
| Q-5 | How do we access owner and pet information from Visit entities, given the relationship is unidirectional from Pet? | **JPQL constructor expression** — `FROM Pet p JOIN p.visits v JOIN p.owner o WHERE v.date BETWEEN :start AND :end`, projecting into a new `UpcomingVisitRow` record. This traverses the existing join-column relationship without modifying any entity. |
| Q-6 | What is the default for the `days` query parameter? | **7** — `@RequestParam(defaultValue = "7") int days`. |
| Q-7 | Should the results be paginated? | **No** — initial scope is minimal; pagination is explicitly out of scope. |
| Q-8 | What should the page show when there are no upcoming visits? | **Empty-state message** — the template renders a contextual message instead of an empty table body. |
| Q-9 | Should `days` be validated or capped (e.g., max 90)? | **No** — out of scope for initial delivery. Any positive integer is accepted as-is; the default applies when the param is absent. |
| Q-10 | Where should the Playwright E2E test live? | **New `upcoming-visits.spec.ts`** in `e2e-tests/tests/features/` — this is a distinct user journey from pet/visit scheduling, so a dedicated file is appropriate. |

## Open

None.
