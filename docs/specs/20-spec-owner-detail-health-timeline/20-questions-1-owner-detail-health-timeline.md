# Questions: Owner Detail — Health Timeline Integration (20)

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | What fragment name and path does TASK-14 / spec-19 define? | Fragment named `healthTimeline` in `fragments/health-timeline.html`. Include via `th:insert="~{fragments/health-timeline :: healthTimeline}"`. |
| Q-2 | Should the fragment be inserted with `th:insert` or `th:replace`? | `th:insert` — preserves the outer container `<td>`, allowing the collapse wrapper `<div>` to live inside the existing pet row cell without disrupting the table structure. |
| Q-3 | How should the Bootstrap collapse id be scoped per pet? | Use `th:id="'health-timeline-' + ${pet.id}"` on the collapsible `<div>` so multiple pets on the same page each get a unique id (e.g., `health-timeline-3`, `health-timeline-7`). The toggle's `th:data-bs-target` must match. |
| Q-4 | Where in the pet row does the toggle + collapse live? | After the inner `<table class="table-condensed liatrio-table">` (which contains Visit Date / Description rows and the Edit Pet / Add Visit / Delete links), still inside the same `<td valign="top">` on the right side of the outer pet row. |
| Q-5 | What visible text should the toggle carry? | `▼ Health Timeline` — matches the epic spec verbatim. |
| Q-6 | What does the test approach look like? | Extend `OwnerControllerTests` (`@WebMvcTest`). The `george()` fixture already provides a pet with one visit. Add a new `@Test` that calls `GET /owners/{ownerId}` and asserts the rendered HTML contains `data-bs-toggle="collapse"` and the text `Health Timeline`. This test must be written and confirmed failing **before** `ownerDetails.html` is modified (TDD RED phase). |
| Q-7 | Does spec-19 need to be delivered first? | Yes. `ownerDetails.html` with `th:insert` for the fragment will throw a Thymeleaf TemplateProcessingException if `health-timeline.html` does not exist. Implementation of spec-20 is blocked until spec-19 status is `delivered`. |
| Q-8 | Are any controller or repository changes needed? | No. `OwnerController.showOwner()` already adds the `owner` object to the model; `Pet.getVisits()` is already called by Thymeleaf. No Java production changes beyond the HTML template. |
| Q-9 | Does `OwnerControllerTests` need any new mocks? | No. The existing `given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george()))` stub is sufficient; the `george()` fixture already carries a pet with one visit. |

## Open

*None.*
