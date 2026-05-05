# Questions: Prevent Duplicate Owner Creation (06) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | What fields define a duplicate? | **firstName + lastName + telephone** — all three must match. Address and city are excluded (two people can share an address; telephone is the strongest unique signal). |
| Q-2 | Should name matching be case-sensitive? | **Case-insensitive** for firstName and lastName. Telephone is digits-only (enforced by `@Pattern` on the creation form) so case sensitivity is moot there. |
| Q-3 | Where should duplicate detection logic live? | **New `OwnerService`** — a `@Service` class that encapsulates the duplicate check. The controller delegates to the service rather than calling the repository directly for this concern. |
| Q-4 | How should the error be surfaced in the UI? | **Global form banner** — `result.reject("duplicate", "...")` produces a global error displayed at the top of the form via `th:if="${#fields.hasGlobalErrors()}"`. Not tied to any specific field. |
| Q-5 | What happens to the submitted form data on rejection? | **Form re-shown with data preserved** — standard Spring MVC behaviour when `processCreationForm` returns the form view with a populated `BindingResult`. The user can correct or abandon the submission. |
| Q-6 | Should a database-level unique constraint be added? | **No** — application-level check only, for this spec. Adding a migration and `DataIntegrityViolationException` handling is deferred as a follow-up. |

## Open

None.
