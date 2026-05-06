# Validation: Friendly 404 Pages for Missing Resources (07)

## Automated verification

From repository root:

```bash
# AC-9.a — full Java test suite
./mvnw test

# AC-5.a — OwnerController 404 test
./mvnw test -Dtest=OwnerControllerTests

# AC-6.a — PetController 404 test
./mvnw test -Dtest=PetControllerTests

# AC-7.a — VisitController 404 test
./mvnw test -Dtest=VisitControllerTests

# AC-9.b — coverage report (open target/site/jacoco/index.html; verify ≥90% on modified controllers)
./mvnw test jacoco:report

# AC-1.a — confirm ResourceNotFoundException file exists
find src/main/java -name "ResourceNotFoundException.java"

# AC-1.b — confirm @ResponseStatus(HttpStatus.NOT_FOUND) annotation
grep -n "@ResponseStatus" src/main/java/org/springframework/samples/petclinic/system/ResourceNotFoundException.java

# AC-2.a — OwnerController uses ResourceNotFoundException
grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java

# AC-2.b — PetController uses ResourceNotFoundException
grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/PetController.java

# AC-2.c — VisitController uses ResourceNotFoundException
grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/VisitController.java

# AC-2.d — no IllegalArgumentException thrown for missing resources in owner package
grep -rn "throw new IllegalArgumentException" src/main/java/org/springframework/samples/petclinic/owner/

# AC-3.a — 404 template exists
find src/main/resources/templates/error -name "404.html"

# AC-3.b — 404 template contains Find Owners link
grep -n 'href.*\/owners\|th:href.*owners' src/main/resources/templates/error/404.html

# AC-3.c — 404 template contains human-readable not-found message
grep -in "not found\|could not be found" src/main/resources/templates/error/404.html

# AC-4.a — error.html does not expose exception/message text
grep -n "th:text.*\${message}\|th:text.*\${error}\|th:utext.*\${" src/main/resources/templates/error.html

# AC-8.a/AC-8.b — Playwright test exists
grep -n "99999\|not.found\|notFound\|404" e2e-tests/tests/features/owner-management.spec.ts

# AC-8.c — full Owner Management E2E suite
cd e2e-tests && npm test -- --grep "Owner Management"
```

**Expected:**

- `./mvnw test` exits 0; `BUILD SUCCESS`; all `OwnerControllerTests`,
  `PetControllerTests`, and `VisitControllerTests` pass including
  `testShowOwnerNotFound`, `testInitUpdatePetFormNotFound`, and
  `testInitNewVisitFormOwnerNotFound`.
- `find src/main/java -name "ResourceNotFoundException.java"` prints one path.
- All three `grep -n "ResourceNotFoundException"` commands on controller files
  print at least one match each.
- `grep -rn "throw new IllegalArgumentException" src/.../owner/` returns no
  output.
- `find ... -name "404.html"` prints one path.
- `grep` for `/owners` link in `404.html` prints at least one match.
- `grep -in "not found"` in `404.html` prints at least one match.
- `grep -n "th:text.*\${message}..."` on `error.html` returns **no output**.
- `grep -n "99999..."` on `owner-management.spec.ts` prints at least one match.
- `npm test -- --grep "Owner Management"` exits 0; all tests pass including
  `"shows friendly 404 page for non-existent owner"`.

## Traceability

- Feature spec: `07-spec-friendly-404.md`
- Task breakdown: `07-tasks-friendly-404.md`
- Questions and decisions: `07-questions-1-friendly-404.md`
- Per-task evidence: `07-proofs/07-task-NN-proofs.md`
- Upstream specs: none
- Parent epic: none

## Manual checks

1. Start the app: `./mvnw spring-boot:run`
2. Navigate to `http://localhost:8080/owners/99999`.
3. Confirm the browser shows HTTP 404 and a branded "Page Not Found" page — not
   a stack trace or whitelabel error page.
4. Confirm the page contains a visible "Find Owners" link that navigates back to
   `http://localhost:8080/owners/find`.
5. Confirm no exception message or internal path is visible anywhere on the page.
6. Navigate to `http://localhost:8080/oups` (CrashController).
7. Confirm the existing 500 error page still displays correctly and that the
   exception message is **not** shown (the `error.html` change affects all status
   codes).

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `ResourceNotFoundException.java` exists in `system/` package | `07-proofs/07-task-03-proofs.md` | file creation | PASS |
| AC-1.b | Class carries `@ResponseStatus(HttpStatus.NOT_FOUND)` | `07-proofs/07-task-03-proofs.md` | file creation | PASS |
| AC-2.a | `OwnerController` references `ResourceNotFoundException` | `07-proofs/07-task-03-proofs.md` | file edit | PASS |
| AC-2.b | `PetController` references `ResourceNotFoundException` | `07-proofs/07-task-03-proofs.md` | file edit | PASS |
| AC-2.c | `VisitController` references `ResourceNotFoundException` | `07-proofs/07-task-03-proofs.md` | file edit | PASS |
| AC-2.d | No `IllegalArgumentException` thrown for missing resources in `owner/` package | `07-proofs/07-task-03-proofs.md` | command output | PASS |
| AC-3.a | `error/404.html` exists | `07-proofs/07-task-04-proofs.md` | file creation | PASS |
| AC-3.b | `error/404.html` contains a link to `/owners` | `07-proofs/07-task-04-proofs.md` | file creation | PASS |
| AC-3.c | `error/404.html` contains a not-found message | `07-proofs/07-task-04-proofs.md` | file creation | PASS |
| AC-4.a | `error.html` does not render `${message}` / `${error}` / `${exception}` | `07-proofs/07-task-04-proofs.md` | file edit | PASS |
| AC-5.a | `testShowOwnerNotFound` asserts `status().isNotFound()` and passes | `07-proofs/07-task-03-proofs.md` | Maven test pass | PASS |
| AC-6.a | `testInitUpdatePetFormNotFound` asserts `status().isNotFound()` and passes | `07-proofs/07-task-03-proofs.md` | Maven test pass | PASS |
| AC-7.a | `testInitNewVisitFormOwnerNotFound` asserts `status().isNotFound()` and passes | `07-proofs/07-task-03-proofs.md` | Maven test pass | PASS |
| AC-8.a | Playwright test navigates to `/owners/99999` and asserts not-found message | `07-proofs/07-task-05-proofs.md` | Playwright screenshot | PASS |
| AC-8.b | Playwright test asserts Find Owners link is visible on 404 page | `07-proofs/07-task-05-proofs.md` | Playwright screenshot | PASS |
| AC-8.c | `npm test -- --grep "Owner Management"` exits 0 | `07-proofs/07-task-05-proofs.md` | command output | PASS |
| AC-9.a | `./mvnw test` exits 0 | `07-proofs/07-task-06-proofs.md` | Maven test pass | PASS |
| AC-9.b | JaCoCo ≥90% line coverage on new/changed code | `07-proofs/07-task-06-proofs.md` | JaCoCo coverage report | PASS |

## Definition of done

- [x] AC-1.a: `ResourceNotFoundException.java` exists in `system/` package
- [x] AC-1.b: Class carries `@ResponseStatus(HttpStatus.NOT_FOUND)`
- [x] AC-2.a: `OwnerController` references `ResourceNotFoundException`
- [x] AC-2.b: `PetController` references `ResourceNotFoundException`
- [x] AC-2.c: `VisitController` references `ResourceNotFoundException`
- [x] AC-2.d: No `IllegalArgumentException` thrown for missing resources in `owner/` package
- [x] AC-3.a: `src/main/resources/templates/error/404.html` exists
- [x] AC-3.b: `error/404.html` contains a link to `/owners`
- [x] AC-3.c: `error/404.html` contains a human-readable not-found message
- [x] AC-4.a: `error.html` does not render raw exception/message text
- [x] AC-5.a: `testShowOwnerNotFound` in `OwnerControllerTests` asserts `status().isNotFound()` and passes
- [x] AC-6.a: `testInitUpdatePetFormNotFound` in `PetControllerTests` asserts `status().isNotFound()` and passes
- [x] AC-7.a: `testInitNewVisitFormOwnerNotFound` in `VisitControllerTests` asserts `status().isNotFound()` and passes
- [x] AC-8.a: Playwright test asserts not-found message at `/owners/99999`
- [x] AC-8.b: Playwright test asserts Find Owners link visible on 404 page
- [x] AC-8.c: `cd e2e-tests && npm test -- --grep "Owner Management"` exits 0
- [x] AC-9.a: `./mvnw test` exits 0
- [x] AC-9.b: JaCoCo ≥90% line coverage on new/changed code
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
