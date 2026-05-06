# Tasks: Upcoming Visits Page (09)

## Task 01 — Write failing UpcomingVisitsControllerTests (RED)

Covers: AC-1.a, AC-2.a, AC-3.a

- Create `src/test/java/org/springframework/samples/petclinic/owner/UpcomingVisitsControllerTests.java` annotated with `@WebMvcTest(UpcomingVisitsController.class)`, `@DisabledInNativeImage`, and `@DisabledInAotMode`.
- Declare a `@MockitoBean VisitRepository visits` field.
- In `@BeforeEach`, stub `visits.findUpcomingVisits(any(), any())` to return `List.of()` (empty — sufficient for the shape tests).
- Add two tests:

  **`testShowUpcomingVisitsDefaultWindow`** — verifies default 7-day window and view name:

  ```java
  @Test
  void testShowUpcomingVisitsDefaultWindow() throws Exception {
      mockMvc.perform(get("/visits/upcoming"))
          .andExpect(status().isOk())
          .andExpect(view().name("visits/upcomingVisits"))
          .andExpect(model().attributeExists("visits"));

      ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
      ArgumentCaptor<LocalDate> endCaptor   = ArgumentCaptor.forClass(LocalDate.class);
      verify(visits).findUpcomingVisits(startCaptor.capture(), endCaptor.capture());
      assertThat(ChronoUnit.DAYS.between(startCaptor.getValue(), endCaptor.getValue())).isEqualTo(7);
  }
  ```

  **`testShowUpcomingVisitsCustomDays`** — verifies `?days=3` overrides the window:

  ```java
  @Test
  void testShowUpcomingVisitsCustomDays() throws Exception {
      mockMvc.perform(get("/visits/upcoming").param("days", "3"))
          .andExpect(status().isOk())
          .andExpect(model().attributeExists("visits"));

      ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
      ArgumentCaptor<LocalDate> endCaptor   = ArgumentCaptor.forClass(LocalDate.class);
      verify(visits).findUpcomingVisits(startCaptor.capture(), endCaptor.capture());
      assertThat(ChronoUnit.DAYS.between(startCaptor.getValue(), endCaptor.getValue())).isEqualTo(3);
  }
  ```

- Run `./mvnw test -Dtest=UpcomingVisitsControllerTests` — confirm both tests fail because `UpcomingVisitsController` does not exist yet (compile error or `NoSuchBeanDefinitionException`). Record the failure output.

**Proof:** 09-proofs/09-task-01-proofs.md

---

## Task 02 — Add UpcomingVisitRow record and VisitRepository with date-range query

Covers: AC-4.a, AC-4.b

**May break compile, fixed by:** Task 03 (controller references VisitRepository but doesn't exist yet until that task; Task 01 tests reference it as a `@MockitoBean` so they compile once this task lands).

- Create `src/main/java/org/springframework/samples/petclinic/owner/UpcomingVisitRow.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  import java.time.LocalDate;

  public record UpcomingVisitRow(
      int id,
      LocalDate date,
      String description,
      String petName,
      String ownerFirstName,
      String ownerLastName,
      int ownerId
  ) {}
  ```

- Create `src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  import java.time.LocalDate;
  import java.util.List;

  import org.springframework.data.jpa.repository.Query;
  import org.springframework.data.repository.Repository;
  import org.springframework.data.repository.query.Param;

  public interface VisitRepository extends Repository<Visit, Integer> {

      @Query("""
          SELECT new org.springframework.samples.petclinic.owner.UpcomingVisitRow(
              v.id, v.date, v.description,
              p.name,
              o.firstName, o.lastName, o.id
          )
          FROM Pet p JOIN p.visits v JOIN p.owner o
          WHERE v.date BETWEEN :start AND :end
          ORDER BY v.date ASC
          """)
      List<UpcomingVisitRow> findUpcomingVisits(
          @Param("start") LocalDate start,
          @Param("end") LocalDate end
      );
  }
  ```

- Run `./mvnw compile` — confirm the new files compile cleanly. Record the output.
- Run `./mvnw test -Dtest=UpcomingVisitsControllerTests` — tests should still fail (controller not yet created), but now for a `NoSuchBeanDefinitionException` rather than a compile error. Record the output.

**Proof:** 09-proofs/09-task-02-proofs.md

---

## Task 03 — Implement UpcomingVisitsController and upcomingVisits.html template (GREEN)

Covers: AC-1.a, AC-2.a, AC-3.a, AC-5.a, AC-5.b, AC-5.c

- Create `src/main/java/org/springframework/samples/petclinic/owner/UpcomingVisitsController.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  import java.time.LocalDate;
  import java.util.List;

  import org.springframework.stereotype.Controller;
  import org.springframework.ui.Model;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RequestParam;

  @Controller
  @RequestMapping("/visits")
  class UpcomingVisitsController {

      private final VisitRepository visitRepository;

      UpcomingVisitsController(VisitRepository visitRepository) {
          this.visitRepository = visitRepository;
      }

      @GetMapping("/upcoming")
      public String showUpcomingVisits(
              @RequestParam(name = "days", defaultValue = "7") int days,
              Model model) {
          LocalDate start = LocalDate.now();
          LocalDate end   = start.plusDays(days);
          List<UpcomingVisitRow> visits = visitRepository.findUpcomingVisits(start, end);
          model.addAttribute("visits", visits);
          model.addAttribute("days", days);
          return "visits/upcomingVisits";
      }
  }
  ```

- Create `src/main/resources/templates/visits/upcomingVisits.html`:

  ```html
  <!DOCTYPE html>
  <html xmlns:th="https://www.thymeleaf.org"
        th:replace="~{fragments/layout :: layout (~{::body},'visits')}">
  <body>
  <div class="container-fluid">
    <h2 th:text="#{upcomingVisits}">Upcoming Visits</h2>
    <p class="text-muted"
       th:text="'Showing visits for the next ' + ${days} + ' day(s).'">
      Showing visits for the next 7 day(s).
    </p>

    <div th:if="${visits.isEmpty()}" class="alert alert-info" role="alert">
      No upcoming visits in the next <span th:text="${days}">7</span> day(s).
    </div>

    <table th:unless="${visits.isEmpty()}"
           class="table table-striped">
      <thead>
        <tr>
          <th th:text="#{visitDate}">Visit Date</th>
          <th th:text="#{owner}">Owner</th>
          <th th:text="#{pet}">Pet</th>
          <th th:text="#{description}">Description</th>
        </tr>
      </thead>
      <tbody>
        <tr th:each="visit : ${visits}">
          <td th:text="${visit.date}">2026-05-10</td>
          <td>
            <a th:href="@{/owners/{id}(id=${visit.ownerId})}"
               th:text="${visit.ownerFirstName + ' ' + visit.ownerLastName}">
              Owner Name
            </a>
          </td>
          <td th:text="${visit.petName}">Pet Name</td>
          <td th:text="${visit.description}">Description</td>
        </tr>
      </tbody>
    </table>
  </div>
  </body>
  </html>
  ```

- Run `./mvnw test -Dtest=UpcomingVisitsControllerTests` — confirm both tests pass (GREEN). Record the passing output.
- Run `./mvnw test` — confirm no regressions across the full suite. Record the passing output.

**Proof:** 09-proofs/09-task-03-proofs.md

---

## Task 04 — Add nav link and upcomingVisits i18n key to all 9 language files

Covers: AC-6.a, AC-7.a

- In `src/main/resources/templates/fragments/layout.html`, add an "Upcoming Visits" nav item immediately after the Veterinarians item (around line 59), following the same `th:replace="~{::menuItem ...}"` pattern:

  ```html
  <li th:replace="~{::menuItem ('/visits/upcoming','visits','upcoming visits','calendar',#{upcomingVisits})}">
    <a href="/visits/upcoming">
      <span>Upcoming Visits</span>
    </a>
  </li>
  ```

  The glyph `'calendar'` uses the same Font Awesome / Bootstrap Icons convention already in use for other nav items; substitute the correct icon name if the project uses a different icon set.

- Add `upcomingVisits=Upcoming Visits` to each of the 9 language files. For non-English files, use the English string as a fallback value until translations are provided (consistent with how other keys are handled):

  | File | Value |
  |------|-------|
  | `messages.properties` | `upcomingVisits=Upcoming Visits` |
  | `messages_en.properties` | `upcomingVisits=Upcoming Visits` |
  | `messages_de.properties` | `upcomingVisits=Upcoming Visits` |
  | `messages_es.properties` | `upcomingVisits=Upcoming Visits` |
  | `messages_fa.properties` | `upcomingVisits=Upcoming Visits` |
  | `messages_ko.properties` | `upcomingVisits=Upcoming Visits` |
  | `messages_pt.properties` | `upcomingVisits=Upcoming Visits` |
  | `messages_ru.properties` | `upcomingVisits=Upcoming Visits` |
  | `messages_tr.properties` | `upcomingVisits=Upcoming Visits` |

- Run structural checks:

  ```bash
  grep -n "visits/upcoming" src/main/resources/templates/fragments/layout.html
  grep -rn "upcomingVisits" src/main/resources/messages/
  ```

  First command must return at least one match. Second must return at least 9 matches.

- Run `./mvnw test` — confirm all tests pass including `I18nPropertiesSyncTest`. Record the passing output.

**Proof:** 09-proofs/09-task-04-proofs.md

---

## Task 05 — Write Playwright E2E test

Covers: AC-8.a, AC-8.b, AC-8.c

- Create `e2e-tests/tests/features/upcoming-visits.spec.ts` with one test inside a `test.describe('Upcoming Visits', ...)` block:

  ```ts
  import { test, expect } from '@fixtures/base-test';

  test.describe('Upcoming Visits', () => {
    test('shows a visit scheduled within the next 7 days', async ({ page }, testInfo) => {
      // Navigate to an existing owner and add a visit with a date within the next 7 days
      await page.goto('/owners/1');
      await expect(page.getByRole('heading', { name: /Pets and Visits/i })).toBeVisible();

      // Find the "Add Visit" link for the first pet and extract the visit URL
      const addVisitLink = page.getByRole('link', { name: /Add Visit/i }).first();
      const href = await addVisitLink.getAttribute('href');
      await page.goto(href!);

      // Fill in a date within the next 7 days
      const futureDate = new Date();
      futureDate.setDate(futureDate.getDate() + 3);
      const dateStr = futureDate.toISOString().split('T')[0]; // YYYY-MM-DD
      const description = 'E2E upcoming visit test ' + Date.now();

      await page.locator('input#date').fill(dateStr);
      await page.locator('input#description').fill(description);
      await page.getByRole('button', { name: /Add Visit/i }).click();

      // Should redirect back to owner details
      await expect(page.getByRole('heading', { name: /Pets and Visits/i })).toBeVisible();

      // Navigate to /visits/upcoming and verify the visit appears
      await page.goto('/visits/upcoming');
      await expect(page.getByRole('heading', { name: /Upcoming Visits/i })).toBeVisible();
      await expect(page.getByText(description, { exact: true })).toBeVisible();

      await page.screenshot({ path: testInfo.outputPath('upcoming-visits-table.png'), fullPage: true });
    });
  });
  ```

- Run `cd e2e-tests && npm test -- --grep "Upcoming Visits"` — confirm the test passes. Record the passing output and confirm `upcoming-visits-table.png` is written to the Playwright output path.

**Proof:** 09-proofs/09-task-05-proofs.md

---

## Task 06 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture full output confirming `BUILD SUCCESS`.
- Run `./mvnw test jacoco:report`; open `target/site/jacoco/index.html` and capture ≥90% line coverage for `UpcomingVisitsController` and `VisitRepository`.
- Run each structural `grep` check from the validation file and record output (all must return at least one match).
- Run `cd e2e-tests && npm test -- --grep "Upcoming Visits"` and capture passing output including `"shows a visit scheduled within the next 7 days"`.
- Confirm `upcoming-visits-table.png` was written to the Playwright output path.
- Build the coverage matrix in `09-validation-upcoming-visits.md` — set all rows to `PASS` with real evidence references.
- Tick every checkbox in the Definition of Done in the validation file.

**Proof:** 09-proofs/09-task-06-proofs.md
