# Questions: Find Owners Multi-Field Search (05) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | How should telephone and city matching work — prefix match or exact match? | **Prefix match** for all three fields (lastName, city, telephone), consistent with how lastName search works today. Entering "608" matches any telephone starting with "608"; entering "Mad" matches "Madison". |
| Q-2 | When multiple criteria are provided, should results satisfy ALL criteria or ANY? | **AND logic** — all non-empty criteria must match. Entering lastName="Davis" + city="Sun" returns Betty Davis (Sun Prairie) but not Harold Davis (Windsor). |
| Q-3 | When no owners are found, where should the error message appear? | **Global form-level error** — not tied to a specific field. Displayed at the top of the form via `th:if="${#fields.hasGlobalErrors()}"` in Thymeleaf. The existing behaviour of placing the error on the `lastName` field is removed. |
| Q-4 | What telephone format should the search form accept? | **Digits-only, any length** — allows partial searches (e.g. "608" to find all 608 area-code owners). Non-digit characters (e.g. hyphens) are rejected with an inline field error. An empty field means no telephone filter. |
| Q-5 | Should a new search DTO be introduced, or reuse `Owner` as the model attribute? | **Reuse `Owner`** — `processFindForm` carries no `@Valid` annotation so `@NotBlank` and `@Pattern` constraints on `Owner` fields do not fire on the find form. Telephone format is validated manually in the controller via `String.matches("\\d+")`. |
| Q-6 | What repository query strategy cleanly handles three optional prefix-match criteria? | **Single `@Query` JPQL method** — `findBySearchCriteria` with null-safe LIKE conditions. Passing `null` for an unused parameter makes that `WHERE` clause always-true, achieving clean AND logic with a single query. An explicit `countQuery` is required to avoid Hibernate pagination count-query issues with `DISTINCT`. |
| Q-7 | Should `findByLastNameStartingWith` be removed from the repository? | **No** — it stays on the interface for backward compatibility. The controller's `processFindForm` routes all multi-field searches through `findBySearchCriteria`; the old derived method is no longer called by the controller but remains available. |

## Open

None.
