---
status: delivered
created: 2026-05-06
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Upcoming Visits Page (09)

## Goal

Clinic staff currently have no cross-cutting view of appointments scheduled in the near future; to find upcoming visits they must navigate to each owner record individually. This spec adds a read-only page at `/visits/upcoming` that lists every visit falling within the next N days (default 7, configurable via `?days=`), showing owner name, pet name, visit date, and description in a single table with a nav link for one-click access.

## Scope

### In scope

- New `UpcomingVisitRow` Java record in `org.springframework.samples.petclinic.owner` carrying `date`, `description`, `petName`, `ownerFirstName`, `ownerLastName`, and `ownerId`
- New `VisitRepository` extending `Repository<Visit, Integer>` with a single JPQL `@Query` constructor-expression method that joins through `Pet.visits` and `Pet.owner` to return a `List<UpcomingVisitRow>` for a date range
- New `UpcomingVisitsController` at `@RequestMapping("/visits")` with a single `GET /visits/upcoming` endpoint accepting an optional `?days=` parameter (default 7) and putting the result list in the model as `"visits"`
- New Thymeleaf template `src/main/resources/templates/visits/upcomingVisits.html` — Bootstrap table with columns Date, Pet Name, Owner Name, Description; owner name links to `/owners/{ownerId}`; empty-state message when the list is empty
- Nav link added to `src/main/resources/templates/fragments/layout.html` pointing to `/visits/upcoming`, using the existing `menuItem` fragment pattern, with active-menu key `"visits"`
- New i18n key `upcomingVisits` added to all 9 language property files under `src/main/resources/messages/`
- `UpcomingVisitsControllerTests` written in RED phase before any controller code: at minimum `testShowUpcomingVisitsDefaultWindow` and `testShowUpcomingVisitsCustomDays`
- Playwright E2E test in new `e2e-tests/tests/features/upcoming-visits.spec.ts`: create a visit with a date within the next 7 days, navigate to `/visits/upcoming`, verify the visit row appears

### Out of scope

- Any write operation (edit, cancel, delete) from the upcoming visits view
- Pagination of the results list
- Filtering by vet, owner, pet name, or pet type
- Validation or capping of the `days` parameter beyond applying the default
- A past-visits or visit-history view
- Sorting beyond date-ascending order from the JPQL query
- Modifying the existing `Visit`, `Pet`, or `Owner` entities
- Internationalisation beyond the single `upcomingVisits` key (table-column headers reuse existing keys: `date`, `description`, `pet`, `owner`)
- Any REST/JSON endpoint

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java` — entity with `date` (LocalDate, column `visit_date`) and `description` (String, `@NotBlank`); extends `BaseEntity` for `id`; no back-reference to `Pet` — the relationship is unidirectional from `Pet.visits`
- `src/main/java/org/springframework/samples/petclinic/owner/Pet.java` — entity with `name`; holds `@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) @JoinColumn(name = "pet_id") Set<Visit> visits`; has `@ManyToOne Owner owner`
- `src/main/java/org/springframework/samples/petclinic/owner/Owner.java` — entity with `firstName`, `lastName`; source of owner-name fields surfaced in the view
- `src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java` — existing pattern for `Repository<Owner, Integer>` with JPQL queries; `VisitRepository` follows the same structure
- `src/main/resources/templates/fragments/layout.html` — navbar; existing nav items use `th:replace="~{::menuItem('/path','activeKey','title','glyph',#{key})}"` at lines ~52–59
- `src/main/resources/messages/messages.properties` — existing keys reused for table-column headers: `date`, `description`, `pet`, `owner`, `name`

## Acceptance criteria

- **AC-1: GET /visits/upcoming renders HTTP 200**
  - AC-1.a: `UpcomingVisitsControllerTests.testShowUpcomingVisitsDefaultWindow` performs `GET /visits/upcoming`, asserts `status().isOk()` and `view().name("visits/upcomingVisits")` — verified by `./mvnw test -Dtest=UpcomingVisitsControllerTests` exiting 0.

- **AC-2: Default window is 7 days**
  - AC-2.a: `UpcomingVisitsControllerTests.testShowUpcomingVisitsDefaultWindow` performs `GET /visits/upcoming` (no `days` param) and asserts the model attribute `"visits"` was populated by a repository call using a 7-day window — verified by `./mvnw test -Dtest=UpcomingVisitsControllerTests` exiting 0.

- **AC-3: ?days= param overrides the window**
  - AC-3.a: `UpcomingVisitsControllerTests.testShowUpcomingVisitsCustomDays` performs `GET /visits/upcoming?days=3` and asserts the model attribute `"visits"` was populated by a repository call using a 3-day window — verified by `./mvnw test -Dtest=UpcomingVisitsControllerTests` exiting 0.

- **AC-4: VisitRepository with date-range query exists**
  - AC-4.a: `VisitRepository.java` is present — verified by `find src/main/java -name "VisitRepository.java"` returning one match.
  - AC-4.b: `VisitRepository.java` contains a `@Query` annotation with a JPQL expression that joins `Pet.visits` and `Pet.owner` — verified by `grep -n "@Query" src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java` returning at least one match.

- **AC-5: Template renders owner, pet, date, description**
  - AC-5.a: `upcomingVisits.html` contains table-cell expressions referencing owner name fields — verified by `grep -n "ownerFirst\|ownerLast\|firstName\|lastName" src/main/resources/templates/visits/upcomingVisits.html` returning at least one match.
  - AC-5.b: `upcomingVisits.html` contains table-cell expressions for pet name, date, and description — verified by `grep -n "petName\|\.date\|\.description" src/main/resources/templates/visits/upcomingVisits.html` returning at least one match.
  - AC-5.c: `upcomingVisits.html` contains an empty-state element conditional on the list being empty — verified by `grep -n "isEmpty\|th:if.*visit\|th:unless" src/main/resources/templates/visits/upcomingVisits.html` returning at least one match.

- **AC-6: Nav link added to layout**
  - AC-6.a: `layout.html` contains a `menuItem` fragment call referencing `/visits/upcoming` — verified by `grep -n "visits/upcoming" src/main/resources/templates/fragments/layout.html` returning at least one match.

- **AC-7: upcomingVisits i18n key in all 9 language files**
  - AC-7.a: `grep -rn "upcomingVisits" src/main/resources/messages/` returns at least 9 matches — verified by the count being ≥ 9.

- **AC-8: Playwright E2E — visit in window appears on the page**
  - AC-8.a: `upcoming-visits.spec.ts` contains a test that creates a visit with a date within the next 7 days, navigates to `/visits/upcoming`, and asserts a row matching the pet name and description is visible — verified by `grep -n "upcoming\|Upcoming" e2e-tests/tests/features/upcoming-visits.spec.ts` returning at least one match.
  - AC-8.b: The E2E test captures a screenshot of the populated table to the Playwright output path — verified by `grep -n "screenshot" e2e-tests/tests/features/upcoming-visits.spec.ts` returning at least one match.
  - AC-8.c: `cd e2e-tests && npm test -- --grep "Upcoming Visits"` exits 0 with all tests passing.

- **AC-9: No regressions**
  - AC-9.a: `./mvnw test` exits 0 — all existing and new Java tests pass.
  - AC-9.b: `./mvnw test jacoco:report` produces ≥90% line coverage on `UpcomingVisitsController` and `VisitRepository` query method.

## Conventions

- `VisitRepository` must extend `Repository<Visit, Integer>` — consistent with `OwnerRepository` and `VetRepository` patterns in this codebase; do not extend `JpaRepository`.
- The JPQL query must navigate through the existing `Pet.visits` join-column relationship (`FROM Pet p JOIN p.visits v JOIN p.owner o WHERE v.date BETWEEN :start AND :end ORDER BY v.date ASC`) — no modification to `Visit`, `Pet`, or `Owner` is permitted.
- The query result type is `UpcomingVisitRow` (a Java `record` in the `owner` package). Its constructor parameter order must match the `SELECT new ...` field order in the JPQL expression.
- The `days` parameter is declared as `@RequestParam(name = "days", defaultValue = "7") int days` on the controller method.
- The model attribute name for the visit list is `"visits"` (lowercase plural).
- The view name returned is `"visits/upcomingVisits"` (camelCase suffix matching the template file name).
- The nav item in `layout.html` uses the existing `menuItem` fragment; the active-menu key is `"visits"`.
- TDD is mandatory: `UpcomingVisitsControllerTests` is committed in its RED (failing) state before the controller or repository code is committed. Proof for Task 01 captures the failing run.
- The `upcomingVisits` i18n key and the template that references it must land in the same commit so `I18nPropertiesSyncTest` never sees a partial state.
- Owner name in the table must be a link to `/owners/{ownerId}` — consistent with how owner names are linked elsewhere in the application.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
