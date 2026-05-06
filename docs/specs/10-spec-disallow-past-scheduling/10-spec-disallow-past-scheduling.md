---
status: delivered
created: 2026-05-06
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Disallow Past Visit Scheduling (10)

## Goal

Clinic staff can currently submit the visit form with any date — including dates in the past — without any validation error. This allows accidental or erroneous backdating of appointments. This spec adds a server-side rule that rejects visit dates earlier than today, surfacing a clear localized validation message while leaving today and future dates entirely unaffected.

## Scope

### In scope

- New `VisitValidator` class in `org.springframework.samples.petclinic.owner`, implementing Spring's `Validator` interface, enforcing: (a) date is not null, (b) date is not before today
- `VisitController` `@InitBinder` registration of `VisitValidator` (mirroring `PetController` → `PetValidator`)
- Message key `visit.date.pastNotAllowed` added to `messages.properties` (base) and all seven non-English locale files (`de`, `es`, `fa`, `ko`, `pt`, `ru`, `tr`)
- `VisitValidatorTests.java` — new JUnit unit test class covering: past date rejected, today accepted, future date accepted, null date rejected
- `VisitControllerTests.java` — new test cases: `testProcessNewVisitFormPastDateRejected`, `testProcessNewVisitFormTodayAccepted`, `testProcessNewVisitFormFutureDateAccepted`
- `visit-scheduling.spec.ts` — new Playwright test case for past-date validation error; update of existing success-path test to use a dynamic future date

### Out of scope

- Client-side HTML `min` attribute on the date input (browser-enforced restriction)
- Validation of existing persisted visit records
- Any edit-visit flow (no such controller exists)
- Changes to `Visit` entity annotations (`@NotBlank` on description is unchanged)
- Modifying the `inputField.html` fragment
- New Thymeleaf template changes beyond what Spring's `BindingResult` + existing `inputField` fragment already provides

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/PetValidator.java` — reference implementation for a custom Spring `Validator`; `VisitValidator` follows the same structure
- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java` — entity with `LocalDate date` field defaulting to `LocalDate.now()` in constructor; no current date constraint
- `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java` — `@InitBinder` already present for `setAllowedFields`; add a second call to register `VisitValidator`
- `src/main/resources/messages/messages.properties` — base key file; all keys here must also appear in the seven non-English locale files or `I18nPropertiesSyncTest.checkI18nPropertyFilesAreInSync` fails
- `src/main/resources/templates/fragments/inputField.html` — displays field errors via `th:errors="*{__${name}__}"`; the resolved error message text is shown directly, so the i18n key value is what the user sees
- `src/test/java/org/springframework/samples/petclinic/system/I18nPropertiesSyncTest.java` — enforces that every key in `messages.properties` is present in all non-English locale files (skips `messages_en.properties`)

## Acceptance criteria

- **AC-1: Past date rejected at the web layer**
  - AC-1.a: `VisitControllerTests.testProcessNewVisitFormPastDateRejected` POSTs a date of `LocalDate.now().minusDays(1)`, asserts `model().attributeHasFieldErrors("visit", "date")`, `status().isOk()`, and `view().name("pets/createOrUpdateVisitForm")` — verified by `./mvnw test -Dtest=VisitControllerTests` exiting 0.
  - AC-1.b: `VisitValidatorTests` contains a test `shouldRejectPastDate` that constructs a `Visit` with yesterday's date, calls `VisitValidator.validate()`, and asserts the `Errors` object has a field error on `date` with code `visit.date.pastNotAllowed` — verified by `./mvnw test -Dtest=VisitValidatorTests` exiting 0.

- **AC-2: Validation message is localized and displayed**
  - AC-2.a: `messages.properties` contains the key `visit.date.pastNotAllowed` — verified by `grep -c "visit.date.pastNotAllowed" src/main/resources/messages/messages.properties` returning 1.
  - AC-2.b: All seven non-English locale files contain `visit.date.pastNotAllowed` — verified by `grep -rn "visit.date.pastNotAllowed" src/main/resources/messages/` returning at least 8 matches (base + 7 locales).
  - AC-2.c: `./mvnw test -Dtest=I18nPropertiesSyncTest` exits 0 after the key is added.
  - AC-2.d: Playwright: submitting a past date on the visit form shows text matching the English message value on screen — verified by `cd e2e-tests && npm test -- --grep "rejects past date"` exiting 0.

- **AC-3: Today and future dates accepted**
  - AC-3.a: `VisitValidatorTests.shouldAcceptToday` constructs a `Visit` with `LocalDate.now()`, calls `validate()`, and asserts `Errors` has no field errors on `date` — verified by `./mvnw test -Dtest=VisitValidatorTests` exiting 0.
  - AC-3.b: `VisitValidatorTests.shouldAcceptFutureDate` constructs a `Visit` with `LocalDate.now().plusDays(1)`, calls `validate()`, and asserts no field errors on `date` — verified by `./mvnw test -Dtest=VisitValidatorTests` exiting 0.
  - AC-3.c: `VisitControllerTests.testProcessNewVisitFormTodayAccepted` POSTs today's date with a valid description and asserts `status().is3xxRedirection()` — verified by `./mvnw test -Dtest=VisitControllerTests` exiting 0.
  - AC-3.d: `VisitControllerTests.testProcessNewVisitFormFutureDateAccepted` POSTs a future date with a valid description and asserts `status().is3xxRedirection()` — verified by `./mvnw test -Dtest=VisitControllerTests` exiting 0.

- **AC-4: Existing tests continue to pass**
  - AC-4.a: `./mvnw test` exits 0 — all pre-existing tests (including `VisitControllerTests.testProcessNewVisitFormSuccess`) pass after the validator is added.
  - AC-4.b: The updated `visit-scheduling.spec.ts` success-path test uses a date ≥ today and passes — verified by `cd e2e-tests && npm test -- --grep "Visit Scheduling"` exiting 0.

- **AC-5: TDD compliance**
  - AC-5.a: `VisitValidatorTests.java` exists — verified by `find src/test -name "VisitValidatorTests.java"` returning one match.
  - AC-5.b: `./mvnw test jacoco:report` exits 0 with ≥90% line coverage on `VisitValidator` — verified by `grep -A2 "VisitValidator" target/site/jacoco/index.html` showing ≥90%.

## Conventions

- `VisitValidator` must implement `org.springframework.validation.Validator` — not a JSR-303/Jakarta annotation — consistent with `PetValidator`.
- The `supports` method must return `true` only for `Visit.class` (and subclasses).
- Error code for a past/null date is `visit.date.pastNotAllowed` for past dates and `required` for null, matching the string constants in `PetValidator` (`REQUIRED = "required"`).
- `VisitController` registers the validator in `@InitBinder` via `dataBinder.addValidators(new VisitValidator())` — do not replace `setAllowedFields`.
- The i18n key and validator must land in the same commit so `I18nPropertiesSyncTest` never fails on a partial state.
- TDD is mandatory: `VisitValidatorTests.java` is committed in its RED (failing) state before `VisitValidator.java` is created. Proof for Task 01 captures the failing test run.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
