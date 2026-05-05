# Tasks: Prevent Duplicate Owner Creation (06)

## Task 01 — Add repository method and write failing OwnerServiceTests (RED)

Covers: AC-1.a, AC-2.a, AC-2.b

- Add the following method signature to `OwnerRepository`:

  ```java
  boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone(
      String firstName, String lastName, String telephone);
  ```

  No `@Query` annotation is needed — Spring Data JPA resolves the derived name
  automatically. Adding the signature now allows `OwnerServiceTests` to compile.
- Create `src/test/java/org/springframework/samples/petclinic/owner/OwnerServiceTests.java`
  as a `@ExtendWith(MockitoExtension.class)` unit test:
  - `@Mock OwnerRepository owners`
  - `@InjectMocks OwnerService ownerService` (will fail to compile until Task 04)
  - `testIsDuplicate_returnsTrueWhenMatchExists()`:
    - Given `given(owners.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone("George", "Franklin", "6085551023")).willReturn(true)`
    - When `boolean result = ownerService.isDuplicate("George", "Franklin", "6085551023")`
    - Then `assertThat(result).isTrue()`
  - `testIsDuplicate_returnsFalseWhenNoMatch()`:
    - Given stub returns `false`
    - Then `assertThat(result).isFalse()`
- Run `./mvnw test -Dtest=OwnerServiceTests` — confirm it fails to compile because
  `OwnerService` does not exist yet (RED). Record the compiler error output.

**Proof:** 06-proofs/06-task-01-proofs.md

---

## Task 02 — Write failing OwnerControllerTests for duplicate detection (RED)

Covers: AC-3.a, AC-3.b, AC-3.c

- In `OwnerControllerTests`, add `@MockitoBean OwnerService ownerService`.
- In `@BeforeEach`, add a default stub:

  ```java
  given(ownerService.isDuplicate(any(), any(), any())).willReturn(false);
  ```

- Add `testProcessCreationFormDuplicateRejected()`:
  - Stub `given(ownerService.isDuplicate(eq("Joe"), eq("Bloggs"), eq("1316761638"))).willReturn(true)`
  - Perform `post("/owners/new")` with params `firstName=Joe`, `lastName=Bloggs`,
    `address=123 Caramel Street`, `city=London`, `telephone=1316761638`
  - Assert `.andExpect(status().isOk())`
  - Assert `.andExpect(view().name("owners/createOrUpdateOwnerForm"))`
  - Assert the BindingResult has a global error with code `"duplicate"` using a
    custom `ResultMatcher` that extracts `BindingResult` from the model and calls
    `assertThat(bindingResult.getGlobalErrors()).hasSize(1)` and
    `assertThat(bindingResult.getGlobalErrors().get(0).getCode()).isEqualTo("duplicate")`
  - Assert `verify(owners, never()).save(any())`
- Verify existing `testProcessCreationFormSuccess` still compiles (the `@BeforeEach`
  default stub covers it).
- Run `./mvnw test -Dtest=OwnerControllerTests` — confirm the new test fails
  because `OwnerService` is not yet wired in the controller (RED). Record failure.

**Proof:** 06-proofs/06-task-02-proofs.md

---

## Task 03 — Write failing Playwright E2E test (RED)

Covers: AC-5.a, AC-5.b

- In `e2e-tests/tests/features/owner-management.spec.ts`, append a test named
  `"blocks duplicate owner creation"`:
  - Call `createOwner()` to generate a unique owner object (reuse existing helper).
  - Fill and submit the `/owners/new` form with those details; assert redirect to
    the owner detail page (confirming first creation succeeded).
  - Navigate back to `/owners/new`.
  - Fill the form with the **same** `firstName`, `lastName`, and `telephone`.
  - Submit the form.
  - Assert the page does **not** redirect (URL stays on `/owners/new` or similar
    creation path) — use `expect(page).not.toHaveURL(/\/owners\/\d+/)`.
  - Assert a visible error element containing text matching
    `/already exists/i` is present on the page.
  - Capture a screenshot:

    ```ts
    await page.screenshot({ path: testInfo.outputPath('duplicate-owner-error.png') });
    ```

- Run `cd e2e-tests && npm test -- --grep "blocks duplicate owner creation"` —
  confirm the test fails because the controller does not yet reject duplicates
  (the second submit redirects to a new owner detail page) (RED). Record failure.

**Proof:** 06-proofs/06-task-03-proofs.md

---

## Task 04 — Implement OwnerService (GREEN)

Covers: AC-2.a, AC-2.b

- Create `src/main/java/org/springframework/samples/petclinic/owner/OwnerService.java`:

  ```java
  @Service
  public class OwnerService {

      private final OwnerRepository owners;

      public OwnerService(OwnerRepository owners) {
          this.owners = owners;
      }

      public boolean isDuplicate(String firstName, String lastName, String telephone) {
          return owners.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone(
              firstName, lastName, telephone);
      }
  }
  ```

- Run `./mvnw test -Dtest=OwnerServiceTests` — confirm both service tests pass
  (GREEN). Record passing output.

**Proof:** 06-proofs/06-task-04-proofs.md

---

## Task 05 — Update OwnerController to wire OwnerService duplicate check (GREEN)

Covers: AC-3.a, AC-3.b, AC-3.c

- Add `OwnerService ownerService` as a constructor parameter in `OwnerController`
  (alongside existing `OwnerRepository owners`). Spring Boot will inject it
  automatically.
- In `processCreationForm`, insert the duplicate check **after** the
  `if (result.hasErrors())` guard and **before** `owners.save(owner)`:

  ```java
  if (ownerService.isDuplicate(owner.getFirstName(), owner.getLastName(), owner.getTelephone())) {
      result.reject("duplicate", "An owner with this name and telephone already exists.");
      return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
  }
  ```

- Run `./mvnw test -Dtest=OwnerControllerTests` — confirm all controller tests
  pass including `testProcessCreationFormDuplicateRejected` (GREEN). Record output.

**Proof:** 06-proofs/06-task-05-proofs.md

---

## Task 06 — Update createOrUpdateOwnerForm template to render global errors (GREEN)

Covers: AC-4.a, AC-5.b, AC-5.c

- In `src/main/resources/templates/owners/createOrUpdateOwnerForm.html`, add a
  global error block immediately inside the `<form>` element, before the first
  field input:

  ```html
  <div th:if="${#fields.hasGlobalErrors()}" class="alert alert-danger" role="alert">
    <p th:each="err : ${#fields.globalErrors()}" th:text="${err}">Error</p>
  </div>
  ```

- Run `./mvnw test` — all Java tests pass.
- Run `cd e2e-tests && npm test -- --grep "Owner Management"` — all E2E tests
  pass including `"blocks duplicate owner creation"` (GREEN). Record output.

**Proof:** 06-proofs/06-task-06-proofs.md

---

## Task 07 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture full output confirming `BUILD SUCCESS`.
- Run `./mvnw test jacoco:report`; open `target/site/jacoco/index.html` and
  capture line-coverage percentages for `OwnerService` and `OwnerController`
  (must be ≥90%).
- Run `cd e2e-tests && npm test -- --grep "Owner Management"` and capture
  the passing output including `"blocks duplicate owner creation"`.
- Confirm `duplicate-owner-error.png` was written under the Playwright output path.
- Build the coverage matrix in `06-validation-prevent-duplicate-owner.md` — set
  all rows to `PASS` with real evidence references.
- Tick every checkbox in the Definition of Done in the validation file.

**Proof:** 06-proofs/06-task-07-proofs.md
