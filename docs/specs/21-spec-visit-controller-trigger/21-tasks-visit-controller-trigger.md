# Tasks: VisitController Async Trigger (21)

## Task 01 — RED: add failing tests to VisitControllerTests

Covers: AC-4.a, AC-4.b, AC-4.c, AC-4.d, AC-4.e

- Add `private static final int TEST_VISIT_ID = 42;` constant alongside
  `TEST_OWNER_ID` and `TEST_PET_ID`.
- Add `@MockitoBean private VisitSummaryService visitSummaryService;`
  field to the test class.
- In `@BeforeEach init()`, after the existing `given(...)` stub, add a
  `willAnswer` stub on `owners.save(any(Owner.class))` that iterates
  the visits on the saved owner's pet and calls `v.setId(TEST_VISIT_ID)`
  on each. The lambda must return the mutated `Owner` argument:

  ```java
  willAnswer(inv -> {
      Owner o = inv.getArgument(0);
      Pet p = o.getPet(TEST_PET_ID);
      if (p != null) {
          p.getVisits().forEach(v -> v.setId(TEST_VISIT_ID));
      }
      return o;
  }).given(owners).save(any(Owner.class));
  ```

- Add `testGenerateCalledOnSuccessfulVisit()`:

  ```java
  @Test
  void testGenerateCalledOnSuccessfulVisit() throws Exception {
      mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new",
                  TEST_OWNER_ID, TEST_PET_ID)
              .param("description", "Annual checkup"))
          .andExpect(status().is3xxRedirection());
      verify(visitSummaryService).generate(TEST_VISIT_ID);
  }
  ```

- Add `testGenerateNotCalledWhenValidationFails()`:

  ```java
  @Test
  void testGenerateNotCalledWhenValidationFails() throws Exception {
      mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new",
                  TEST_OWNER_ID, TEST_PET_ID))
          // no description param — VisitValidator rejects
          .andExpect(status().isOk())
          .andExpect(view().name("pets/createOrUpdateVisitForm"));
      verifyNoInteractions(visitSummaryService);
  }
  ```

- Run `./mvnw test -Dtest=VisitControllerTests` and confirm the two new
  tests **fail** (RED phase — compilation succeeds but `generate()` is
  not yet called in production code). The 7 pre-existing tests must
  still pass.

**Proof:** 21-proofs/21-task-01-proofs.md

---

## Task 02 — GREEN: inject VisitSummaryService and add guarded generate() call

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b, AC-3.a, AC-4.c, AC-4.d,
AC-5.a, AC-5.b

- Add `private final VisitSummaryService visitSummaryService;` field to
  `VisitController`.
- Update the constructor to accept `VisitSummaryService` as its second
  parameter and assign it to the field:

  ```java
  public VisitController(OwnerRepository owners,
                         VisitSummaryService visitSummaryService) {
      this.owners = owners;
      this.visitSummaryService = visitSummaryService;
  }
  ```

- In `processNewVisitForm()`, after `this.owners.save(owner)`, add the
  null-guarded generate call:

  ```java
  owner.addVisit(petId, visit);
  this.owners.save(owner);
  if (visit.getId() != null) {
      visitSummaryService.generate(visit.getId());
  }
  redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
  return "redirect:/owners/{ownerId}";
  ```

- Run `./mvnw compile` — must exit 0.
- Run `./mvnw test -Dtest=VisitControllerTests` — all 9 tests (7
  pre-existing + 2 new) must now **pass** (GREEN phase).

**Proof:** 21-proofs/21-task-02-proofs.md

---

## Task 03 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture the full output. Confirm 0 failures.
- Run `./mvnw test jacoco:report` and capture the coverage summary for
  `VisitController.java`. Confirm ≥90% line coverage on the modified
  class.
- Run each `grep` command from
  `21-validation-visit-controller-trigger.md` and capture the output.
- Fill every row in the coverage matrix in
  `21-validation-visit-controller-trigger.md` with `PASS`.
- Fill completion notes in each proof file under `21-proofs/`.

**Proof:** 21-proofs/21-task-03-proofs.md
