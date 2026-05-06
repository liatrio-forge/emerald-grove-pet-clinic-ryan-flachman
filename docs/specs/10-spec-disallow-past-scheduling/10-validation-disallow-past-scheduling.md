# Validation: Disallow Past Visit Scheduling (10)

## Automated verification

From repository root:

```bash
# AC-1: Past date rejected at web layer
./mvnw test -Dtest="VisitValidatorTests,VisitControllerTests"
# Expected: BUILD SUCCESS, all VisitValidatorTests and VisitControllerTests pass

# AC-2: i18n key present in all locale files
grep -c "visit.date.pastNotAllowed" src/main/resources/messages/messages.properties
# Expected: 1

grep -rn "visit.date.pastNotAllowed" src/main/resources/messages/
# Expected: ≥8 lines (messages.properties + 7 locale files)

./mvnw test -Dtest=I18nPropertiesSyncTest
# Expected: BUILD SUCCESS

# AC-2.d / AC-4.b: Playwright E2E
cd e2e-tests && npm test -- --grep "Visit Scheduling"
# Expected: all tests pass including "rejects past date" and the updated success-path test

# AC-3: Today and future dates accepted
./mvnw test -Dtest=VisitValidatorTests
# Expected: shouldAcceptToday and shouldAcceptFutureDate pass

# AC-4 / AC-5: Full test suite + coverage
./mvnw test jacoco:report
# Expected: BUILD SUCCESS; VisitValidator line coverage ≥90%

# AC-5.a: Validator test file exists
find src/test -name "VisitValidatorTests.java"
# Expected: one match
```

## Traceability

- Feature spec: `10-spec-disallow-past-scheduling.md`
- Task breakdown: `10-tasks-disallow-past-scheduling.md`
- Questions and decisions: `10-questions-1-disallow-past-scheduling.md`
- Per-task evidence:
  - `10-proofs/10-task-01-proofs.md`
  - `10-proofs/10-task-02-proofs.md`
  - `10-proofs/10-task-03-proofs.md`
  - `10-proofs/10-task-04-proofs.md`
  - `10-proofs/10-task-05-proofs.md`
- Upstream specs: none
- Parent epic: none

## Manual checks

- Open the visit scheduling form in a browser, set the date field to yesterday, submit, and confirm the validation message "must be today or in the future" (or locale equivalent) appears inline next to the date field.
- Switch the UI language to German, repeat the above, and confirm the message reads "muss heute oder in der Zukunft liegen".

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `VisitControllerTests.testProcessNewVisitFormPastDateRejected` asserts field error, 200, and form view | `10-proofs/10-task-03-proofs.md` | Maven test pass | PASS |
| AC-1.b | `VisitValidatorTests.shouldRejectPastDate` asserts error code `visit.date.pastNotAllowed` | `10-proofs/10-task-01-proofs.md` | Maven test pass | PASS |
| AC-2.a | `messages.properties` contains `visit.date.pastNotAllowed` | `10-proofs/10-task-04-proofs.md` | command output | PASS |
| AC-2.b | All 7 non-English locale files contain `visit.date.pastNotAllowed` (≥8 grep matches) | `10-proofs/10-task-04-proofs.md` | command output | PASS |
| AC-2.c | `I18nPropertiesSyncTest` exits 0 | `10-proofs/10-task-04-proofs.md` | Maven test pass | PASS |
| AC-2.d | Playwright `rejects past date` test exits 0 | `10-proofs/10-task-05-proofs.md` | Playwright screenshot | PASS |
| AC-3.a | `VisitValidatorTests.shouldAcceptToday` asserts no date field errors | `10-proofs/10-task-03-proofs.md` | Maven test pass | PASS |
| AC-3.b | `VisitValidatorTests.shouldAcceptFutureDate` asserts no date field errors | `10-proofs/10-task-03-proofs.md` | Maven test pass | PASS |
| AC-3.c | `VisitControllerTests.testProcessNewVisitFormTodayAccepted` asserts 3xx redirect | `10-proofs/10-task-03-proofs.md` | Maven test pass | PASS |
| AC-3.d | `VisitControllerTests.testProcessNewVisitFormFutureDateAccepted` asserts 3xx redirect | `10-proofs/10-task-03-proofs.md` | Maven test pass | PASS |
| AC-4.a | `./mvnw test` exits 0 — all existing tests pass | `10-proofs/10-task-05-proofs.md` | Maven test pass | PASS |
| AC-4.b | Updated success-path Playwright test (future date) exits 0 | `10-proofs/10-task-05-proofs.md` | Playwright screenshot | PASS |
| AC-5.a | `find src/test -name "VisitValidatorTests.java"` returns one match | `10-proofs/10-task-01-proofs.md` | command output | PASS |
| AC-5.b | JaCoCo reports ≥90% line coverage on `VisitValidator` | `10-proofs/10-task-05-proofs.md` | JaCoCo coverage report | PASS |

## Definition of done

- [x] AC-1.a: `VisitControllerTests.testProcessNewVisitFormPastDateRejected` asserts field error on `date`, status 200, view `pets/createOrUpdateVisitForm`
- [x] AC-1.b: `VisitValidatorTests.shouldRejectPastDate` asserts error code `visit.date.pastNotAllowed`
- [x] AC-2.a: `messages.properties` contains key `visit.date.pastNotAllowed`
- [x] AC-2.b: All 7 non-English locale files contain `visit.date.pastNotAllowed`
- [x] AC-2.c: `I18nPropertiesSyncTest` exits 0
- [x] AC-2.d: Playwright `rejects past date` test exits 0
- [x] AC-3.a: `VisitValidatorTests.shouldAcceptToday` passes with no date errors
- [x] AC-3.b: `VisitValidatorTests.shouldAcceptFutureDate` passes with no date errors
- [x] AC-3.c: `VisitControllerTests.testProcessNewVisitFormTodayAccepted` asserts 3xx redirect
- [x] AC-3.d: `VisitControllerTests.testProcessNewVisitFormFutureDateAccepted` asserts 3xx redirect
- [x] AC-4.a: `./mvnw test` exits 0
- [x] AC-4.b: Updated Playwright success-path test (future date) exits 0
- [x] AC-5.a: `VisitValidatorTests.java` exists at `find src/test -name "VisitValidatorTests.java"`
- [x] AC-5.b: JaCoCo ≥90% line coverage on `VisitValidator`
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
