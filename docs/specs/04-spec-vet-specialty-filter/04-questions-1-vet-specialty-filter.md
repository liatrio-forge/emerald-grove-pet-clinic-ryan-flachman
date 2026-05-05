# Questions: Vet Specialty Filter (04) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | Which filter states should exist? | Three: **All** (no `specialty` param — every vet), **specific specialty name** (`?specialty=radiology`) — only matching vets, **None** (`?specialty=none`) — vets with no specialty assigned |
| Q-2 | Should "None" (vets with no specialty) be a selectable filter? | Yes — the issue says "'None' is handled sensibly"; a dedicated "None" pill covers that case cleanly |
| Q-3 | Does the filter apply only to the HTML page or also to the REST `/vets` JSON endpoint? | HTML page (`/vets.html`) only — the JSON endpoint is used for API consumers and its contract must not change |
| Q-4 | How should pagination interact with the filter? | Pagination links must carry `&specialty=<value>` so the filter is preserved when browsing multi-page results |
| Q-5 | How are specialty options populated in the filter UI? | A new `VetRepository.findAllSpecialties()` query returns all distinct specialties ordered by name; the controller passes these as `allSpecialties` model attribute on every `/vets.html` request |
| Q-6 | Should the new repository query methods carry `@Cacheable("vets")`? | No — the existing `@Cacheable("vets")` on `findAll(Pageable)` uses only page info as the cache key; filtered queries with different parameters would collide or be served incorrect cached results. Filtered methods must NOT be annotated with `@Cacheable` |
| Q-7 | What UI pattern for the filter control? | Bootstrap badge/pill anchor links rendered in a `<div data-testid="specialty-filter">` above the table — no JavaScript required, each pill is a plain `<a>` link |
| Q-8 | How must the filter pill text be rendered (I18n constraint)? | `I18nPropertiesSyncTest.checkNonInternationalizedStrings` will fail if literal text appears between tags without a `th:text="#{...}"` attribute. "All" and "None" labels must use message keys. Specialty names come from the database (not hard-coded text in templates) and are rendered via `th:text="${s.name}"`, which is not a literal — no i18n key needed for individual specialty names |
| Q-9 | Which message keys must be added, and to which files? | `vets.filter.all` and `vets.filter.none` must be added to `messages.properties` (base) and to all non-English locale files: `messages_de.properties`, `messages_es.properties`, `messages_fa.properties`, `messages_ko.properties`, `messages_pt.properties`, `messages_ru.properties`, `messages_tr.properties`. `messages_en.properties` is excluded per `I18nPropertiesSyncTest` (uses fallback logic) |
| Q-10 | What are the seed data specialties used in E2E tests? | The existing `vet-directory.spec.ts` validates `surgery`, `dentistry`, `radiology`, and `medicine`; seed data provides Helen Leary (radiology) and others. The E2E filter test targets `radiology` as a known present specialty |
| Q-11 | Does `findDistinctBySpecialties_Name` risk returning duplicate vet rows (JPA JOIN behaviour)? | Yes — ManyToMany JOINs can produce duplicates. The method must use `DISTINCT` semantics; prefer `@Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.name = :name")` with an explicit `countQuery` to avoid Hibernate count-query issues |

## Open

None.
