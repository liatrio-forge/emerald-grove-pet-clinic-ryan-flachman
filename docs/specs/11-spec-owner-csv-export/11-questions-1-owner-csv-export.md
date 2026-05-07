# Questions: Owner CSV Export (11) — Round 1

## Resolved

| # | Question | Decision |
|---|----------|----------|
| Q-1 | The Owner entity has separate `firstName` and `lastName`. How should "name" appear in the CSV? | Two separate columns: `First Name` and `Last Name`. Matches entity model exactly. |
| Q-2 | Should `/owners.csv` respect all three search parameters (`lastName`, `telephone`, `city`) or only `lastName`? | All three, consistent with the existing HTML endpoint. The CSV is an export of whatever the user would see on screen. |
| Q-3 | Should the CSV export be paginated (like the HTML view) or return all matching results? | Return all results in one response — no pagination. CSV downloads are consumed as whole files. |
| Q-4 | Should the Playwright E2E download test be included as an acceptance criterion? | No. CLI `curl` proof only. Playwright download handling adds complexity without adding verification value for this feature. |

## Open

None. All questions resolved before authoring the spec.
