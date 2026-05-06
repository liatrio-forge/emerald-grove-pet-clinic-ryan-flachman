# Tasks: Friendly 404 Pages for Missing Resources (07)

## Task 01 — Write failing OwnerController 404 test (RED)

Covers: AC-5.a

- In `OwnerControllerTests`, add a test `testShowOwnerNotFound`:
  - Stub `given(this.owners.findById(999)).willReturn(Optional.empty())` (or
    rely on Mockito's default of `Optional.empty()` for an unstubbed call with
    ID 999).
  - Perform `mockMvc.perform(get("/owners/{ownerId}", 999))`.
  - Assert `.andExpect(status().isNotFound())`.
- Run `./mvnw test -Dtest=OwnerControllerTests` — confirm the new test fails.
  The current code throws `IllegalArgumentException` which Spring maps to 500,
  not 404 (RED). Record the failure output.

**Proof:** 07-proofs/07-task-01-proofs.md

---

## Task 02 — Write failing PetController and VisitController 404 tests (RED)

Covers: AC-6.a, AC-7.a

**PetControllerTests:**

- The `@BeforeEach` already stubs `owners.findById(TEST_OWNER_ID)` to return
  George, whose only pet is Max with `id=1`.
- Add a test `testInitUpdatePetFormNotFound`:
  - Perform `mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit",
    TEST_OWNER_ID, 999))` where pet ID 999 is not in George's pet list.
  - Assert `.andExpect(status().isNotFound())`.
- Run `./mvnw test -Dtest=PetControllerTests` — confirm the new test fails.
  Currently `owner.getPet(999)` returns `null` with no exception thrown, so the
  handler receives a null model attribute and produces a 500 or NPE (RED).
  Record the failure output.

**VisitControllerTests:**

- Add a test `testInitNewVisitFormOwnerNotFound`:
  - Ensure `owners.findById(999)` is not stubbed (Mockito default returns
    `Optional.empty()`).
  - Perform `mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new",
    999, 1))`.
  - Assert `.andExpect(status().isNotFound())`.
- Run `./mvnw test -Dtest=VisitControllerTests` — confirm the new test fails
  (current `IllegalArgumentException` → 500, not 404) (RED). Record the failure
  output.

**Proof:** 07-proofs/07-task-02-proofs.md

---

## Task 03 — Create ResourceNotFoundException; replace throws in all three controllers (GREEN)

Covers: AC-1.a, AC-1.b, AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-5.a, AC-6.a, AC-7.a

- Create
  `src/main/java/org/springframework/samples/petclinic/system/ResourceNotFoundException.java`:

  ```java
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public class ResourceNotFoundException extends RuntimeException {

      public ResourceNotFoundException(String message) {
          super(message);
      }
  }
  ```

- In `OwnerController.findOwner`, replace:

  ```java
  .orElseThrow(() -> new IllegalArgumentException(...))
  ```

  with:

  ```java
  .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId))
  ```

  Apply the same replacement to every `orElseThrow` call in `OwnerController`
  that currently throws `IllegalArgumentException`.

- In `PetController.findOwner`, apply the same replacement.

- In `PetController.findPet`, after calling `owner.getPet(petId)`, add a null
  guard:

  ```java
  Pet pet = owner.getPet(petId);
  if (pet == null) {
      throw new ResourceNotFoundException("Pet not found with id: " + petId);
  }
  return pet;
  ```

- In `VisitController`, replace both `IllegalArgumentException` throws (missing
  owner, missing pet) with `ResourceNotFoundException`.

- Run `./mvnw test -Dtest=OwnerControllerTests` — confirm `testShowOwnerNotFound`
  now passes alongside all existing tests (GREEN). Record passing output.
- Run `./mvnw test -Dtest=PetControllerTests` — confirm `testInitUpdatePetFormNotFound`
  now passes (GREEN). Record passing output.
- Run `./mvnw test -Dtest=VisitControllerTests` — confirm `testInitNewVisitFormOwnerNotFound`
  now passes (GREEN). Record passing output.

**Proof:** 07-proofs/07-task-03-proofs.md

---

## Task 04 — Create error/404.html and strip exception message from error.html

Covers: AC-3.a, AC-3.b, AC-3.c, AC-4.a

- Create directory `src/main/resources/templates/error/` if it does not already
  exist.

- Create `src/main/resources/templates/error/404.html` using the project's
  standard layout fragment:

  ```html
  <!DOCTYPE html>
  <html xmlns:th="https://www.thymeleaf.org"
        xmlns:layout="https://www.ultraq.net.nz/thymeleaf/layout"
        layout:decorate="~{fragments/layout}">
  <head>
    <title>Page Not Found</title>
  </head>
  <body>
  <div layout:fragment="content" class="container">
    <div class="row justify-content-center mt-5">
      <div class="col-md-6 text-center liatrio-error-card p-4">
        <h2>Page Not Found</h2>
        <p class="mt-3">The requested resource could not be found.</p>
        <a th:href="@{/owners/find}" class="btn btn-primary mt-3">Find Owners</a>
      </div>
    </div>
  </div>
  </body>
  </html>
  ```

- In `src/main/resources/templates/error.html`, locate the paragraph that
  renders the raw exception message (a `<p>` element with a `th:text` or
  `th:utext` expression referencing `${message}`, `${error}`, or similar) and
  remove it. Preserve the `th:switch` status block and all other layout
  structure.

- Run `./mvnw test` — confirm all tests still pass. Record output.
- Run structural checks:

  ```bash
  find src/main/resources/templates/error -name "404.html"
  grep -n 'href.*\/owners\|th:href.*owners' src/main/resources/templates/error/404.html
  grep -in "not found\|could not be found" src/main/resources/templates/error/404.html
  grep -n "th:text.*\${message}\|th:text.*\${error}\|th:utext.*\${" src/main/resources/templates/error.html
  ```

  The first three commands must return matches; the last must return no matches.

**Proof:** 07-proofs/07-task-04-proofs.md

---

## Task 05 — Write Playwright E2E test for missing owner 404

Covers: AC-8.a, AC-8.b, AC-8.c

- In `e2e-tests/tests/features/owner-management.spec.ts`, append a test:

  ```ts
  test('shows friendly 404 page for non-existent owner', async ({ page }, testInfo) => {
    await page.goto('/owners/99999');
    await expect(page.getByText(/not found/i)).toBeVisible();
    await expect(page.getByRole('link', { name: /find owners/i })).toBeVisible();
    await page.screenshot({ path: testInfo.outputPath('owner-not-found.png') });
  });
  ```

- Run `cd e2e-tests && npm test -- --grep "Owner Management"` — confirm all
  tests pass including the new one. Record passing output.

**Proof:** 07-proofs/07-task-05-proofs.md

---

## Task 06 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture full output confirming `BUILD SUCCESS`.
- Run `./mvnw test jacoco:report`; open `target/site/jacoco/index.html` and
  capture line-coverage percentages for `ResourceNotFoundException` and the
  three modified controllers (must be ≥90% on new/changed lines).
- Run `cd e2e-tests && npm test -- --grep "Owner Management"` and capture
  the passing output including `"shows friendly 404 page for non-existent owner"`.
- Confirm `owner-not-found.png` was written to the Playwright output path.
- Build the coverage matrix in `07-validation-friendly-404.md` — set all rows
  to `PASS` with real evidence references.
- Tick every checkbox in the Definition of Done in the validation file.

**Proof:** 07-proofs/07-task-06-proofs.md
