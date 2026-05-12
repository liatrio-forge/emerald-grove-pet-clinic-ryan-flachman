# Tasks: Owner Detail — Health Timeline Integration (20)

## Task 01 — Write failing OwnerControllerTests test (RED phase)

Covers: AC-3.a, AC-3.b, AC-3.c

- Open `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java`.
- Add a new `@Test` method named `testOwnerDetailsContainsHealthTimelineToggle`:
  - Perform `mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))`.
  - Assert `status().isOk()`.
  - Assert the response body contains the string `data-bs-toggle="collapse"` using `.andExpect(content().string(containsString("data-bs-toggle=\"collapse\"")))`.
  - Assert the response body contains the string `Health Timeline` using `.andExpect(content().string(containsString("Health Timeline")))`.
- Run `./mvnw test -Dtest=OwnerControllerTests` and confirm the new test **fails** (RED). The existing tests must still pass; only the new one should fail.
- Capture the failing test output in the proof file.

**Proof:** 20-proofs/20-task-01-proofs.md

## Task 02 — Update ownerDetails.html with collapse toggle and fragment insert (GREEN phase)

Covers: AC-1.a, AC-1.b, AC-2.a, AC-2.b, AC-2.c

- Open `src/main/resources/templates/owners/ownerDetails.html`.
- Locate the `<td valign="top">` on the right side of the `th:each="pet : ${owner.pets}"` loop (the cell that contains `<table class="table-condensed liatrio-table">`).
- After the closing `</table>` of the inner visits/actions table, and still inside the same `<td>`, append:

  ```html
  <div class="mt-2">
    <button class="btn btn-sm btn-outline-secondary"
            type="button"
            data-bs-toggle="collapse"
            th:data-bs-target="'#health-timeline-' + ${pet.id}"
            th:aria-controls="'health-timeline-' + ${pet.id}"
            aria-expanded="false">
      &#9660; Health Timeline
    </button>
    <div class="collapse mt-2"
         th:id="'health-timeline-' + ${pet.id}">
      <div th:insert="~{fragments/health-timeline :: healthTimeline}"></div>
    </div>
  </div>
  ```

- Run `./mvnw test -Dtest=OwnerControllerTests` and confirm the previously failing test now **passes** (GREEN). All other tests must remain green.
- Capture the passing test output in the proof file.

**Proof:** 20-proofs/20-task-02-proofs.md

## Task 03 — Validate and capture proof artifacts

Covers: all (AC-1.a, AC-1.b, AC-2.a, AC-2.b, AC-2.c, AC-3.a, AC-3.b, AC-3.c, AC-4.a)

- Run `./mvnw compile` and capture output (must exit 0).
- Run `./mvnw test -Dtest=OwnerControllerTests` and capture full output showing every test method passing.
- Run `./mvnw test` and capture the BUILD SUCCESS summary.
- Run each structural `grep` check from `20-validation-owner-detail-health-timeline.md` and capture output.
- Update the coverage matrix in `20-validation-owner-detail-health-timeline.md`, setting every `PENDING` row to `PASS`.
- Record all outputs in `20-proofs/20-task-03-proofs.md`.

**Proof:** 20-proofs/20-task-03-proofs.md
