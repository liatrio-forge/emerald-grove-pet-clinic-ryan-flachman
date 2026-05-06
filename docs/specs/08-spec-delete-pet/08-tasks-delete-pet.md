# Tasks: Delete a Pet from Owner (with Confirmation) (08)

## Task 01 — Write failing PetControllerTests delete tests (RED)

Covers: AC-4.b, AC-4.c, AC-5.a, AC-6.a, AC-7.a

- In `src/test/java/org/springframework/samples/petclinic/owner/PetControllerTests.java`,
  locate the existing `@BeforeEach` setup method. It already stubs
  `owners.findById(TEST_OWNER_ID)` to return a `george()` owner whose only
  pet is Max (id=1). Verify that `george()` returns Max with `id=1` so
  sub-tasks can reference that pet ID.

- Add four new tests:

  **`testDeletePetSuccess`** — pet with no visits, expects redirect:

  ```java
  @Test
  void testDeletePetSuccess() throws Exception {
      mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/delete",
              TEST_OWNER_ID, 1))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrlPattern("/owners/*"));
  }
  ```

  **`testDeletePetWithVisitsCascade`** — pet already has a visit stubbed in
  setup; confirms the handler does not reject pets-with-visits:

  ```java
  @Test
  void testDeletePetWithVisitsCascade() throws Exception {
      // Max already has a visit in george() fixture (add one if not present)
      mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/delete",
              TEST_OWNER_ID, 1))
          .andExpect(status().is3xxRedirection());
  }
  ```

  **`testDeletePetOwnerNotFound`** — unknown owner ID returns 404:

  ```java
  @Test
  void testDeletePetOwnerNotFound() throws Exception {
      mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/delete", 999, 1))
          .andExpect(status().isNotFound());
  }
  ```

  `owners.findById(999)` is not stubbed; Mockito default returns
  `Optional.empty()`, which triggers `ResourceNotFoundException` in
  `findOwner`.

  **`testDeletePetNotFound`** — valid owner but unknown pet ID returns 404:

  ```java
  @Test
  void testDeletePetNotFound() throws Exception {
      mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/delete",
              TEST_OWNER_ID, 999))
          .andExpect(status().isNotFound());
  }
  ```

  George's pet collection contains only Max (id=1); pet 999 triggers
  `ResourceNotFoundException` in `findPet`.

- Run `./mvnw test -Dtest=PetControllerTests` — confirm all four new tests
  fail (no handler exists yet → 404 or 405; not the expected 3xx for
  success tests). Record the failure output.

**Proof:** 08-proofs/08-task-01-proofs.md

---

## Task 02 — Implement delete endpoint; add orphanRemoval to Owner.pets (GREEN)

Covers: AC-3.a, AC-4.a, AC-4.b, AC-4.c, AC-5.a, AC-6.a, AC-7.a, AC-7.b

- In `src/main/java/org/springframework/samples/petclinic/owner/Owner.java`,
  change the `@OneToMany` annotation on the `pets` field from:

  ```java
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  ```

  to:

  ```java
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  ```

  This is a JPA metadata change only — no DDL migration is needed.

- In `src/main/java/org/springframework/samples/petclinic/owner/PetController.java`,
  add the delete handler method after `processUpdateForm`:

  ```java
  @PostMapping("/pets/{petId}/delete")
  public String deletePet(@ModelAttribute Owner owner, @ModelAttribute Pet pet,
          RedirectAttributes redirectAttributes) {
      owner.getPets().remove(pet);
      this.owners.save(owner);
      redirectAttributes.addFlashAttribute("message", "Pet has been deleted");
      return "redirect:/owners/{ownerId}";
  }
  ```

  The `@ModelAttribute` parameters are resolved by the existing `findOwner`
  and `findPet` `@ModelAttribute` methods — 404 handling is inherited
  automatically; no additional guard is needed inside the handler.

- Run `./mvnw test -Dtest=PetControllerTests` — confirm all four new tests
  pass alongside all pre-existing tests (GREEN). Record the passing output.

- Run `./mvnw test` — confirm no regressions across the full suite. Record
  the passing output.

**Proof:** 08-proofs/08-task-02-proofs.md

---

## Task 03 — Add Delete trigger and confirmation modal to ownerDetails.html

Covers: AC-1.a, AC-1.b, AC-2.a, AC-2.b, AC-2.c

- In `src/main/resources/templates/owners/ownerDetails.html`, inside the
  `th:each="pet : ${owner.pets}"` loop, add a Delete trigger link in the
  same table cell as the Edit Pet and Add Visit links:

  ```html
  <td>
    <a th:href="@{__${owner.id}__/pets/__${pet.id}__/edit}"
       th:text="#{editPet}">Edit Pet</a>
  </td>
  <td>
    <a th:href="@{__${owner.id}__/pets/__${pet.id}__/visits/new}"
       th:text="#{addVisit}">Add Visit</a>
  </td>
  <td>
    <a href="#"
       class="delete-pet-trigger"
       th:data-pet-id="${pet.id}"
       th:data-owner-id="${owner.id}"
       th:data-pet-name="${pet.name}"
       th:data-visit-count="${pet.visits.size()}">Delete</a>
  </td>
  ```

- After the closing `</table>` of the Pets and Visits section (before the
  `<script>` tag), add the modal markup and a hidden delete form:

  ```html
  <!-- Delete Pet Modal -->
  <div id="deletePetModal" style="display:none; position:fixed; top:0; left:0;
       width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:1000;">
    <div style="background:#fff; margin:15% auto; padding:2rem; max-width:420px;
         border-radius:4px;">
      <h4>Delete <span id="modalPetName"></span>?</h4>
      <p id="modalVisitWarning" style="display:none; color:#dc3545;"></p>
      <p>This cannot be undone.</p>
      <form id="deleteForm" method="post" action="">
        <input type="hidden"
               th:name="${_csrf.parameterName}"
               th:value="${_csrf.token}" />
        <button type="button"
                onclick="document.getElementById('deletePetModal').style.display='none'">
          Cancel
        </button>
        <button type="submit" id="confirmDeleteBtn">Delete</button>
      </form>
    </div>
  </div>
  ```

  > Note: If CSRF protection is not enabled in this application (the existing
  > forms do not include a CSRF token field), omit the hidden CSRF input.
  > Check whether `spring-boot-starter-security` is on the classpath; if not,
  > Spring Security auto-configuration is absent and CSRF is not enforced.

- Inside the existing `<script>` block (or in a new one immediately below the
  modal), add the wiring JS:

  ```js
  document.querySelectorAll('.delete-pet-trigger').forEach(function(trigger) {
    trigger.addEventListener('click', function(e) {
      e.preventDefault();
      var petName    = this.dataset.petName;
      var visitCount = parseInt(this.dataset.visitCount, 10);
      var ownerId    = this.dataset.ownerId;
      var petId      = this.dataset.petId;

      document.getElementById('modalPetName').textContent = petName;

      var warningEl = document.getElementById('modalVisitWarning');
      var confirmBtn = document.getElementById('confirmDeleteBtn');
      if (visitCount > 0) {
        warningEl.textContent =
          'This will also permanently delete ' + visitCount +
          ' visit record(s).';
        warningEl.style.display = 'block';
        confirmBtn.textContent = 'Delete anyway';
      } else {
        warningEl.style.display = 'none';
        confirmBtn.textContent = 'Delete';
      }

      document.getElementById('deleteForm').action =
        '/owners/' + ownerId + '/pets/' + petId + '/delete';

      document.getElementById('deletePetModal').style.display = 'block';
    });
  });
  ```

- Run structural checks:

  ```bash
  grep -n "data-pet-name"    src/main/resources/templates/owners/ownerDetails.html
  grep -n "data-visit-count" src/main/resources/templates/owners/ownerDetails.html
  grep -n "deletePetModal"   src/main/resources/templates/owners/ownerDetails.html
  grep -n "Delete anyway\|confirmDeleteBtn" src/main/resources/templates/owners/ownerDetails.html
  grep -n "deleteForm\|/delete" src/main/resources/templates/owners/ownerDetails.html
  ```

  All five commands must return at least one match.

- Run `./mvnw test` — confirm all tests still pass. Record passing output.

**Proof:** 08-proofs/08-task-03-proofs.md

---

## Task 04 — Write Playwright E2E tests for pet deletion

Covers: AC-8.a, AC-8.b, AC-8.c, AC-9.a, AC-9.b, AC-9.c

- In `e2e-tests/tests/features/pet-management.spec.ts`, append two new tests
  inside the existing `test.describe('Pet Management', …)` block.

  **Test 1 — create then delete (no visits):**

  ```ts
  test('can delete a pet with no visits', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);
    const pet = createPet({ type: 'cat' });

    await ownerPage.openFindOwners();
    await ownerPage.searchByLastName('Davis');
    await ownerPage.openOwnerDetailsByName('Betty Davis');

    // Create pet
    await page.getByRole('link', { name: /Add New Pet/i }).click();
    await page.locator('input#name').fill(pet.name);
    await page.locator('input#birthDate').fill(pet.birthDate);
    await page.locator('select#type').selectOption({ label: pet.type });
    await page.getByRole('button', { name: /Add Pet/i }).click();
    await expect(page.getByText(pet.name, { exact: true })).toBeVisible();

    // Delete pet — find the trigger in the pet's row
    const petRow = page.locator('tr').filter({
      has: page.locator('dd', { hasText: pet.name })
    });
    await petRow.getByRole('link', { name: /Delete/i }).click();

    // Modal should be visible with "Delete" button (not "Delete anyway")
    await expect(page.locator('#deletePetModal')).toBeVisible();
    await expect(page.locator('#confirmDeleteBtn')).toHaveText('Delete');
    await page.screenshot({
      path: testInfo.outputPath('delete-modal-no-visit.png')
    });

    // Confirm deletion
    await page.locator('#confirmDeleteBtn').click();

    // Pet must no longer appear
    await expect(page.getByRole('heading', { name: /Pets and Visits/i }))
      .toBeVisible();
    await expect(page.getByText(pet.name, { exact: true })).not.toBeVisible();
  });
  ```

  **Test 2 — create pet, add visit, then delete (with-visit warning):**

  ```ts
  test('can delete a pet with visits and sees visit-count warning', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);
    const pet = createPet({ type: 'dog' });

    await ownerPage.openFindOwners();
    await ownerPage.searchByLastName('Davis');
    await ownerPage.openOwnerDetailsByName('Betty Davis');

    // Create pet
    await page.getByRole('link', { name: /Add New Pet/i }).click();
    await page.locator('input#name').fill(pet.name);
    await page.locator('input#birthDate').fill(pet.birthDate);
    await page.locator('select#type').selectOption({ label: pet.type });
    await page.getByRole('button', { name: /Add Pet/i }).click();
    await expect(page.getByText(pet.name, { exact: true })).toBeVisible();

    // Add a visit
    const petRow = page.locator('tr').filter({
      has: page.locator('dd', { hasText: pet.name })
    });
    await petRow.getByRole('link', { name: /Add Visit/i }).first().click();
    await page.locator('input#date').fill('2025-01-01');
    await page.locator('input#description').fill('Wellness check');
    await page.getByRole('button', { name: /Add Visit/i }).click();
    await expect(page.getByRole('heading', { name: /Pets and Visits/i }))
      .toBeVisible();

    // Delete pet — modal should show visit-count warning
    const petRowAfterVisit = page.locator('tr').filter({
      has: page.locator('dd', { hasText: pet.name })
    });
    await petRowAfterVisit.getByRole('link', { name: /Delete/i }).click();

    await expect(page.locator('#deletePetModal')).toBeVisible();
    await expect(page.locator('#modalVisitWarning')).toBeVisible();
    await expect(page.locator('#confirmDeleteBtn')).toHaveText('Delete anyway');
    await page.screenshot({
      path: testInfo.outputPath('delete-modal-with-visit-warning.png')
    });

    // Confirm deletion
    await page.locator('#confirmDeleteBtn').click();

    // Pet must no longer appear
    await expect(page.getByText(pet.name, { exact: true })).not.toBeVisible();
  });
  ```

- Run `cd e2e-tests && npm test -- --grep "Pet Management"` — confirm all
  tests pass including both new delete tests. Record passing output.

**Proof:** 08-proofs/08-task-04-proofs.md

---

## Task 05 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture full output confirming `BUILD SUCCESS`.
- Run `./mvnw test jacoco:report`; open `target/site/jacoco/index.html` and
  capture line-coverage percentage for the new `deletePet` handler and the
  `Owner.pets` relationship change (must be ≥90%).
- Run each structural `grep` command from the validation file and record
  output (must match expected results).
- Run `cd e2e-tests && npm test -- --grep "Pet Management"` and capture
  passing output including `"can delete a pet with no visits"` and
  `"can delete a pet with visits and sees visit-count warning"`.
- Confirm `delete-modal-no-visit.png` and `delete-modal-with-visit-warning.png`
  were written to the Playwright output path.
- Build the coverage matrix in `08-validation-delete-pet.md` — set all rows
  to `PASS` with real evidence references.
- Tick every checkbox in the Definition of Done in the validation file.

**Proof:** 08-proofs/08-task-05-proofs.md
