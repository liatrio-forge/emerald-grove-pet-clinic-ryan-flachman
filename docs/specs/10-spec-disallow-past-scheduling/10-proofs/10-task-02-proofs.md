# Proofs: Task 02 — Write failing Playwright past-date test + update existing test (RED)

Covers: AC-2.d, AC-4.b

## Planned evidence

- Output of `cd e2e-tests && npm test -- --grep "rejects past date"` showing the new test **fails** (form redirected instead of showing validation error — RED phase confirmation)
- Output of `cd e2e-tests && npm test -- --grep "can schedule a visit"` showing the updated success-path test still **passes** with the dynamic future date

## Completion notes

### AC-2.d / AC-4.b: RED phase — Playwright test fails (app cannot start due to compile error)

```text
$ cd e2e-tests && npm test -- --grep "rejects past date"
[WebServer] [ERROR] COMPILATION ERROR :
[WebServer] /src/test/java/.../VisitValidatorTests.java:[35,17] cannot find symbol
  symbol:   class VisitValidator
[WebServer] /src/test/java/.../VisitValidatorTests.java:[39,33] cannot find symbol
  symbol:   class VisitValidator
[WebServer] [INFO] BUILD FAILURE
[WebServer] Total time:  3.068 s
[WebServer] Finished at: 2026-05-06T14:49:18-05:00

Error: Process from config.webServer was not able to start. Exit code: 1
```

The Playwright web server (Spring Boot via Maven) fails to start because `VisitValidatorTests.java` references `VisitValidator` which does not yet exist. This is a valid RED phase failure — no validation exists and the test cannot pass.

### Notes

The spec anticipated the RED failure would be "form redirects instead of showing error." In practice, the failure is at an earlier stage: the application cannot even compile because `VisitValidatorTests.java` (Task 01) introduced a reference to the not-yet-existing `VisitValidator`. Both failure modes confirm the same fact: past-date validation does not exist yet.

The success-path test update (dynamic future date replacing `'2024-02-02'`) is in place in `visit-scheduling.spec.ts`. GREEN phase evidence for AC-4.b captured in Task 05 after the app compiles and all tests pass.
