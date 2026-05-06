# Validation: Upcoming Visits Page (09)

## Automated verification

From repository root:

```bash
# AC-9.a — full Java test suite
./mvnw test

# AC-1.a / AC-2.a / AC-3.a — UpcomingVisitsController tests
./mvnw test -Dtest=UpcomingVisitsControllerTests

# AC-9.b — coverage report (open target/site/jacoco/index.html; verify ≥90% on new code)
./mvnw test jacoco:report

# AC-4.a — VisitRepository file exists
find src/main/java -name "VisitRepository.java"

# AC-4.b — @Query present in VisitRepository
grep -n "@Query" src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java

# AC-5.a — owner name fields in template
grep -n "ownerFirst\|ownerLast\|firstName\|lastName" src/main/resources/templates/visits/upcomingVisits.html

# AC-5.b — pet name, date, description in template
grep -n "petName\|\.date\|\.description" src/main/resources/templates/visits/upcomingVisits.html

# AC-5.c — empty-state element in template
grep -n "isEmpty\|th:if.*visit\|th:unless" src/main/resources/templates/visits/upcomingVisits.html

# AC-6.a — nav link in layout
grep -n "visits/upcoming" src/main/resources/templates/fragments/layout.html

# AC-7.a — upcomingVisits key in all 9 language files
grep -rn "upcomingVisits" src/main/resources/messages/

# AC-8.a — upcoming visits E2E test present
grep -n "upcoming\|Upcoming" e2e-tests/tests/features/upcoming-visits.spec.ts

# AC-8.b — screenshot call in E2E test
grep -n "screenshot" e2e-tests/tests/features/upcoming-visits.spec.ts

# AC-8.c — run Upcoming Visits E2E suite
cd e2e-tests && npm test -- --grep "Upcoming Visits"
```

**Expected:**

- `./mvnw test` exits 0; `BUILD SUCCESS`; `UpcomingVisitsControllerTests` passes with both `testShowUpcomingVisitsDefaultWindow` and `testShowUpcomingVisitsCustomDays`.
- `find src/main/java -name "VisitRepository.java"` prints one path.
- `grep -n "@Query"` in `VisitRepository.java` prints at least one match containing the JPQL constructor expression.
- `grep -n "ownerFirst\|ownerLast\|firstName\|lastName"` in `upcomingVisits.html` prints at least one match.
- `grep -n "petName\|\.date\|\.description"` in `upcomingVisits.html` prints at least one match.
- `grep -n "isEmpty\|th:if.*visit\|th:unless"` in `upcomingVisits.html` prints at least one match.
- `grep -n "visits/upcoming"` in `layout.html` prints at least one match.
- `grep -rn "upcomingVisits"` in `src/main/resources/messages/` prints at least 9 matches (one per file).
- `grep -n "upcoming\|Upcoming"` in `upcoming-visits.spec.ts` prints at least one match.
- `grep -n "screenshot"` in `upcoming-visits.spec.ts` prints at least one match.
- `npm test -- --grep "Upcoming Visits"` exits 0 with `"shows a visit scheduled within the next 7 days"` passing.

## Traceability

- Feature spec: `09-spec-upcoming-visits.md`
- Task breakdown: `09-tasks-upcoming-visits.md`
- Questions and decisions: `09-questions-1-upcoming-visits.md`
- Per-task evidence: `09-proofs/09-task-NN-proofs.md`
- Upstream specs: none
- Parent epic: none

## Manual checks

1. Start the app: `./mvnw spring-boot:run`
2. Navigate to `http://localhost:8080/visits/upcoming`.
3. Confirm the page renders with an "Upcoming Visits" heading.
4. Confirm "Upcoming Visits" link appears in the main nav bar.
5. Navigate to an owner, add a visit with today's date.
   - Return to `/visits/upcoming` — confirm the visit appears in the table with the correct date, pet name, owner name, and description.
   - Confirm the owner name is a clickable link that navigates to the owner details page.
6. Navigate to `/visits/upcoming?days=1`.
   - Confirm only visits within the next 1 day are shown.
   - Add a visit dated 30 days from today; confirm it does **not** appear on the `?days=1` view but **does** appear on `/visits/upcoming?days=31`.
7. If no visits fall within the window, confirm an empty-state message is shown rather than an empty table.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `testShowUpcomingVisitsDefaultWindow` asserts HTTP 200 and view name `visits/upcomingVisits` | `09-proofs/09-task-03-proofs.md` | Maven test pass | PASS |
| AC-2.a | `testShowUpcomingVisitsDefaultWindow` asserts 7-day window via ArgumentCaptor | `09-proofs/09-task-03-proofs.md` | Maven test pass | PASS |
| AC-3.a | `testShowUpcomingVisitsCustomDays` asserts 3-day window via ArgumentCaptor | `09-proofs/09-task-03-proofs.md` | Maven test pass | PASS |
| AC-4.a | `VisitRepository.java` exists | `09-proofs/09-task-02-proofs.md` | file creation | PASS |
| AC-4.b | `@Query` with JPQL expression in `VisitRepository.java` | `09-proofs/09-task-02-proofs.md` | command output | PASS |
| AC-5.a | Owner name fields referenced in `upcomingVisits.html` | `09-proofs/09-task-03-proofs.md` | command output | PASS |
| AC-5.b | Pet name, date, description referenced in `upcomingVisits.html` | `09-proofs/09-task-03-proofs.md` | command output | PASS |
| AC-5.c | Empty-state element in `upcomingVisits.html` | `09-proofs/09-task-03-proofs.md` | command output | PASS |
| AC-6.a | Nav link referencing `/visits/upcoming` in `layout.html` | `09-proofs/09-task-04-proofs.md` | command output | PASS |
| AC-7.a | `upcomingVisits` key present in all 9 language files | `09-proofs/09-task-04-proofs.md` | command output | PASS |
| AC-8.a | E2E test creates visit and asserts row visible on `/visits/upcoming` | `09-proofs/09-task-05-proofs.md` | Playwright screenshot | PENDING |
| AC-8.b | E2E test captures screenshot of populated table | `09-proofs/09-task-05-proofs.md` | Playwright screenshot | PENDING |
| AC-8.c | `npm test -- --grep "Upcoming Visits"` exits 0 | `09-proofs/09-task-05-proofs.md` | command output | PENDING |
| AC-9.a | `./mvnw test` exits 0 | `09-proofs/09-task-06-proofs.md` | Maven test pass | PENDING |
| AC-9.b | JaCoCo ≥90% line coverage on `UpcomingVisitsController` and `VisitRepository` | `09-proofs/09-task-06-proofs.md` | JaCoCo coverage report | PENDING |

## Definition of done

- [ ] AC-1.a: `testShowUpcomingVisitsDefaultWindow` asserts HTTP 200 and view name `visits/upcomingVisits`
- [ ] AC-2.a: `testShowUpcomingVisitsDefaultWindow` asserts 7-day window via ArgumentCaptor
- [ ] AC-3.a: `testShowUpcomingVisitsCustomDays` asserts 3-day window via ArgumentCaptor
- [ ] AC-4.a: `VisitRepository.java` exists at the expected path
- [ ] AC-4.b: `@Query` with JPQL expression present in `VisitRepository.java`
- [ ] AC-5.a: Owner name fields referenced in `upcomingVisits.html`
- [ ] AC-5.b: Pet name, date, and description referenced in `upcomingVisits.html`
- [ ] AC-5.c: Empty-state element present in `upcomingVisits.html`
- [ ] AC-6.a: Nav link referencing `/visits/upcoming` present in `layout.html`
- [ ] AC-7.a: `upcomingVisits` i18n key present in all 9 language files (≥9 grep matches)
- [ ] AC-8.a: E2E test creates visit and asserts row visible on `/visits/upcoming`
- [ ] AC-8.b: E2E test captures screenshot of populated upcoming-visits table
- [ ] AC-8.c: `npm test -- --grep "Upcoming Visits"` exits 0
- [ ] AC-9.a: `./mvnw test` exits 0
- [ ] AC-9.b: JaCoCo ≥90% line coverage on new controller and repository code
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
