# Tasks: Disallow Past Visit Scheduling (10)

## Task 01 — Write failing VisitValidatorTests.java (RED)

Covers: AC-1.b, AC-3.a, AC-3.b, AC-5.a

- Create `src/test/java/org/springframework/samples/petclinic/owner/VisitValidatorTests.java`
- Annotate with `@ExtendWith(MockitoExtension.class)` (no Spring context needed — pure unit test)
- Instantiate `VisitValidator` directly in `@BeforeEach`; create a `BeanPropertyBindingResult` for each `Visit` under test
- Write `shouldRejectPastDate`: construct `Visit` with `date = LocalDate.now().minusDays(1)`, call `validator.validate(visit, errors)`, assert `errors.hasFieldErrors("date")` is true and the first error code is `visit.date.pastNotAllowed`
- Write `shouldRejectNullDate`: construct `Visit` with `date = null`, call `validate`, assert `errors.hasFieldErrors("date")` with code `required`
- Write `shouldAcceptToday`: construct `Visit` with `date = LocalDate.now()`, call `validate`, assert `!errors.hasFieldErrors("date")`
- Write `shouldAcceptFutureDate`: construct `Visit` with `date = LocalDate.now().plusDays(1)`, call `validate`, assert `!errors.hasFieldErrors("date")`
- Run `./mvnw test -Dtest=VisitValidatorTests` and confirm it **fails** with a compilation error (class `VisitValidator` does not exist yet)
- Commit with message `test(visit): add failing VisitValidatorTests for past-date rule [RED]`

**Proof:** 10-proofs/10-task-01-proofs.md

## Task 02 — Write failing Playwright past-date test + update existing success-path test (RED)

Covers: AC-2.d, AC-4.b

- In `e2e-tests/tests/features/visit-scheduling.spec.ts`, locate the success-path test that hardcodes `visitDate = '2024-02-02'`
- Replace the hardcoded string with a dynamic future date:

  ```typescript
  const futureDate = new Date();
  futureDate.setFullYear(futureDate.getFullYear() + 1);
  const visitDate = futureDate.toISOString().split('T')[0];
  ```

- Add a new test `'rejects past date with validation message'`:
  - Navigate to `/owners/1`, click the first Add Visit link
  - Compute yesterday's date: `new Date(Date.now() - 86400000).toISOString().split('T')[0]`
  - Fill in the past date and a non-empty description
  - Click submit
  - Assert the form page is still shown (no redirect): `await expect(page).toHaveURL(/visits\/new/)`
  - Assert the validation message is visible: `await expect(page.getByText(/must be today or in the future/i)).toBeVisible()`
  - Capture screenshot to `testInfo.outputPath('past-date-validation-error.png')`
- Run `cd e2e-tests && npm test -- --grep "rejects past date"` and confirm it **fails** (no validation exists yet — form redirects instead of showing error)
- Commit with message `test(visit): add failing Playwright test for past-date rejection [RED]`

**Proof:** 10-proofs/10-task-02-proofs.md

## Task 03 — Implement VisitValidator and wire into VisitController; add controller test cases (GREEN)

Covers: AC-1.a, AC-1.b, AC-3.c, AC-3.d, AC-4.a

- Create `src/main/java/org/springframework/samples/petclinic/owner/VisitValidator.java`:

  ```java
  public class VisitValidator implements Validator {
      private static final String REQUIRED = "required";
      private static final String DATE_NOT_PAST = "visit.date.pastNotAllowed";

      @Override
      public boolean supports(Class<?> clazz) {
          return Visit.class.isAssignableFrom(clazz);
      }

      @Override
      public void validate(Object obj, Errors errors) {
          Visit visit = (Visit) obj;
          LocalDate date = visit.getDate();
          if (date == null) {
              errors.rejectValue("date", REQUIRED, REQUIRED);
          } else if (date.isBefore(LocalDate.now())) {
              errors.rejectValue("date", DATE_NOT_PAST, "must be today or in the future");
          }
      }
  }
  ```

- In `VisitController.@InitBinder`, add: `dataBinder.addValidators(new VisitValidator());` (keep the existing `setDisallowedFields("id")` call)
- In `VisitControllerTests.java`, add three new tests:
  - `testProcessNewVisitFormPastDateRejected`: POST with `date = LocalDate.now().minusDays(1).toString()` and a valid description; assert `model().attributeHasFieldErrors("visit", "date")`, `status().isOk()`, `view().name("pets/createOrUpdateVisitForm")`
  - `testProcessNewVisitFormTodayAccepted`: POST with `date = LocalDate.now().toString()` and valid description; assert `status().is3xxRedirection()`
  - `testProcessNewVisitFormFutureDateAccepted`: POST with `date = LocalDate.now().plusDays(1).toString()` and valid description; assert `status().is3xxRedirection()`
- Run `./mvnw test -Dtest="VisitValidatorTests,VisitControllerTests"` and confirm all tests **pass**
- Commit with message `feat(visit): add VisitValidator to reject past scheduling dates`

**Proof:** 10-proofs/10-task-03-proofs.md

## Task 04 — Add visit.date.pastNotAllowed to all message property files

Covers: AC-2.a, AC-2.b, AC-2.c

- Add to `src/main/resources/messages/messages.properties`:

  ```properties
  visit.date.pastNotAllowed=must be today or in the future
  ```

- Add to each of the seven non-English locale files with an appropriate translation:
  - `messages_de.properties`: `visit.date.pastNotAllowed=muss heute oder in der Zukunft liegen`
  - `messages_es.properties`: `visit.date.pastNotAllowed=debe ser hoy o en el futuro`
  - `messages_fa.properties`: `visit.date.pastNotAllowed=باید امروز یا در آینده باشد`
  - `messages_ko.properties`: `visit.date.pastNotAllowed=오늘 이후 날짜여야 합니다`
  - `messages_pt.properties`: `visit.date.pastNotAllowed=deve ser hoje ou no futuro`
  - `messages_ru.properties`: `visit.date.pastNotAllowed=должна быть сегодня или в будущем`
  - `messages_tr.properties`: `visit.date.pastNotAllowed=bugün veya gelecekte olmalıdır`
- Run `./mvnw test -Dtest=I18nPropertiesSyncTest` and confirm it exits 0
- The i18n key and the validator implementation land in a coordinated commit so `I18nPropertiesSyncTest` never sees a partial state; if committing separately, add the key first
- Commit with message `feat(visit): add i18n translations for visit.date.pastNotAllowed`

**Proof:** 10-proofs/10-task-04-proofs.md

## Task 05 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture full output; confirm exit 0
- Run `./mvnw test jacoco:report` and confirm ≥90% line coverage on `VisitValidator`
- Run `cd e2e-tests && npm test -- --grep "Visit Scheduling"` and confirm exit 0 (both the updated success-path test and the new past-date rejection test pass)
- Run `grep -c "visit.date.pastNotAllowed" src/main/resources/messages/messages.properties` and confirm output is `1`
- Run `grep -rn "visit.date.pastNotAllowed" src/main/resources/messages/` and confirm ≥ 8 matches
- Run `./mvnw test -Dtest=I18nPropertiesSyncTest` and confirm exit 0
- Run `find src/test -name "VisitValidatorTests.java"` and confirm one match
- Fill in all five proof files with real command output
- Update coverage matrix in `10-validation-disallow-past-scheduling.md` — set all rows to `PASS`

**Proof:** 10-proofs/10-task-05-proofs.md
