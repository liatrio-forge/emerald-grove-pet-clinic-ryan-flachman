# Questions: Delete a Pet from Owner (08) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | What should happen when a pet has existing visit history — block deletion, warn, or silently cascade? | **Warn with extra confirmation** — the modal shows the visit count and a stronger "This will also permanently delete N visit record(s)" message; confirm button reads "Delete anyway". Visit history is permanently deleted via the existing `CascadeType.ALL` on `Pet.visits`. |
| Q-2 | How should the confirmation be presented — modal dialog or a separate server-side page? | **Modal dialog** — inline on the owner details page, no navigation. Single modal instance; JS sets pet name, visit count, and form action dynamically from `data-*` attributes on the trigger. |
| Q-3 | Should deletion be hard (permanent) or soft (inactive flag)? | **Hard delete** — pet and all cascaded visits are permanently removed. No schema migration needed. Consistent with how the rest of the application handles data. |
| Q-4 | How do we delete a pet given there is no PetRepository? | **Remove from Owner aggregate**: call `owner.getPets().remove(pet)`, then `owners.save(owner)`. This requires adding `orphanRemoval = true` to the `@OneToMany` on `Owner.pets`. Without `orphanRemoval`, removing the pet from the collection does not delete the row — it would only nullify the foreign key (or throw a constraint violation). `orphanRemoval = true` is a JPA metadata change only; no DDL migration is needed. |
| Q-5 | Does the delete handler need its own owner/pet lookup logic? | **No** — reuse existing `@ModelAttribute("owner")` and `@ModelAttribute("pet")` methods. These already throw `ResourceNotFoundException` (→ 404) if the owner or pet is missing. The handler method receives resolved `Owner` and `Pet` model attributes directly. |
| Q-6 | What HTTP method should the delete endpoint use? | **POST** — HTML forms do not natively support DELETE. The endpoint is mapped as `@PostMapping("/pets/{petId}/delete")`. No `_method` override needed. |
| Q-7 | Where should new Playwright delete tests live? | **`pet-management.spec.ts`** — the existing pet management suite already tests create and add-visit flows. Delete tests are a natural extension; no new spec file is created. |
| Q-8 | Should the visit count in the modal be fetched via AJAX or rendered server-side? | **Server-side** — Thymeleaf renders `th:data-visit-count="${pet.visits.size()}"` on the trigger element. JS reads the attribute on click. No extra HTTP round trip. |
| Q-9 | What flash message is shown after successful delete? | **`"Pet has been deleted"`** set on flash attribute key `"message"` (consistent with existing pet create/edit flash messages). |

## Open

None.
