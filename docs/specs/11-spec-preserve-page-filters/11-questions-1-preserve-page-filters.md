---
name: 11-questions-1-preserve-page-filters
description: Questions and decisions for spec 11 — preserve page filters
type: project
---

# Questions: Preserve Page Filters (11) — Round 1

## Resolved

| # | Question | Decision |
|---|----------|----------|
| Q-1 | Does the Vets list also need fixing? | No. `vetList.html` already preserves `specialty` in all pagination links using Thymeleaf ternary expressions. Only the Owners list needs work. |
| Q-2 | Are filter params available in the template already? | Partially. The `Owner` model attribute carries raw request values (possibly empty strings). The controller already null-normalizes them into local vars. To keep URLs clean (no `?lastName=`), the null-normalized values must be added to the model explicitly. |
| Q-3 | Should empty filter params appear in URLs? | No. Null values must be passed to the model so Thymeleaf's `@{...}` URL builder omits them, keeping URLs clean (e.g. `/owners?page=2` when no filter, `/owners?page=2&lastName=Davis` when filtered). |
| Q-4 | Which model attribute names for the filter values? | `filterLastName`, `filterTelephone`, `filterCity` — distinct from the `owner` model attribute to avoid shadowing the bound form object. |
| Q-5 | Does `addPaginationModel` need a signature change? | Yes. It must accept `String lastName`, `String telephone`, `String city` (all already null-normalized in `processFindForm`) and add them to the model. |
| Q-6 | How many pagination link hrefs need updating in `ownersList.html`? | Five: numbered page links, first, previous, next, last. |
| Q-7 | What Thymeleaf URL syntax handles optional params cleanly? | `@{/owners(page=${i},lastName=${filterLastName},telephone=${filterTelephone},city=${filterCity})}` — Thymeleaf omits params whose value is `null`. |
| Q-8 | Which test layer verifies the fix? | (a) `OwnerControllerTests` (MockMvc, `@WebMvcTest`) verifies model attributes and HTML content; (b) Playwright E2E test in `owner-management.spec.ts` verifies the user journey end-to-end. |
| Q-9 | Do we need a new E2E test file? | No. Add to `owner-management.spec.ts`. |
| Q-10 | Is seed data sufficient for pagination? | Yes — the H2 seed data has 10 owners; filtering by `lastName` prefix "D" returns Davises (2 records, fits one page). A multi-page filtered result can be set up by seeding with a lastName prefix that spans >5 records, or by the Playwright test creating enough owners. Review existing fixtures before deciding. |

## Open

_None — all questions resolved before implementation._
