---
status: accepted
created: 2026-05-06
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Delete a Pet from Owner (with Confirmation) (08)

## Goal

Staff currently have no way to remove a pet from an owner record. The only
recourse when a pet is entered in error — or an owner no longer has the
animal — is manual database intervention. This spec adds a Delete action on
the owner details page with a confirmation modal. If the pet has visit
history, the modal escalates to a stronger warning before allowing
irreversible deletion.

## Scope

### In scope

- A "Delete" trigger rendered for each pet row in `ownerDetails.html`'s
  Pets and Visits table
- A Bootstrap-compatible confirmation modal rendered server-side inside
  `ownerDetails.html`; vanilla JS wires up the trigger → modal → form
  submit flow — no new JS framework dependency
- Server-rendered `data-pet-name` and `data-visit-count` attributes on each
  Delete trigger so JS can personalise the modal without an extra HTTP round
  trip
- Modal text logic:
  - visit count == 0 → "This cannot be undone." / confirm button: "Delete"
  - visit count >= 1 → "This will also permanently delete N visit record(s).
    This cannot be undone." / confirm button: "Delete anyway"
- New `POST /owners/{ownerId}/pets/{petId}/delete` handler in `PetController`
- `orphanRemoval = true` added to the `@OneToMany` on `Owner.pets` so that
  removing a pet from the collection and saving the owner cascades to a hard
  delete of the `Pet` row; `CascadeType.ALL` on `Pet.visits` then cascades
  the delete to all associated `Visit` rows
- `PetControllerTests` — four new tests written in RED phase before the
  handler is implemented:
  `testDeletePetSuccess`, `testDeletePetWithVisitsCascade`,
  `testDeletePetOwnerNotFound`, `testDeletePetNotFound`
- `pet-management.spec.ts` — two new Playwright E2E tests:
  create-then-delete (no visits), create-pet-add-visit-then-delete (with
  visits)
- Flash message "Pet has been deleted" on redirect to owner details

### Out of scope

- Soft delete / archive / inactive flag on Pet
- Any schema migration (the `orphanRemoval` change is a JPA metadata change
  only, not a DDL change)
- Undo / restore capability
- Deleting an owner or a visit in isolation
- Any change to the visit-management or vet-directory flows
- Internationalisation of new UI strings
- Any REST/JSON endpoint for deletion

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/PetController.java`
  — existing `@ModelAttribute` methods `findOwner` and `findPet`; these are
  reused by the new delete handler for owner/pet resolution and 404 handling
- `src/main/java/org/springframework/samples/petclinic/owner/Owner.java` —
  `@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)` on `pets`
  (line ~64); `orphanRemoval = true` must be added here
- `src/main/java/org/springframework/samples/petclinic/owner/Pet.java` —
  `@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)` on
  `visits`; confirms visit cascade on pet delete is automatic
- `src/main/resources/templates/owners/ownerDetails.html` — Pets and Visits
  table (lines 46–79); the Delete trigger and modal are added within the
  `th:each="pet : ${owner.pets}"` loop
- `src/main/java/org/springframework/samples/petclinic/system/ResourceNotFoundException.java`
  — existing exception used by `findOwner` / `findPet`; the delete handler
  inherits 404 behaviour from these `@ModelAttribute` methods
- `e2e-tests/tests/features/pet-management.spec.ts` — existing Playwright
  suite; new delete tests appended here

## Acceptance criteria

- **AC-1: Delete trigger present for each pet on owner details page**
  - AC-1.a: `ownerDetails.html` contains at least one element with a
    `data-pet-name` attribute inside the `th:each` pet loop — verified by
    `grep -n "data-pet-name" src/main/resources/templates/owners/ownerDetails.html`
    returning at least one match.
  - AC-1.b: `ownerDetails.html` contains at least one element with a
    `data-visit-count` attribute — verified by
    `grep -n "data-visit-count" src/main/resources/templates/owners/ownerDetails.html`
    returning at least one match.

- **AC-2: Confirmation modal markup present in ownerDetails.html**
  - AC-2.a: `ownerDetails.html` contains a modal element (identifiable by id
    `deletePetModal` or equivalent) — verified by
    `grep -n "deletePetModal" src/main/resources/templates/owners/ownerDetails.html`
    returning at least one match.
  - AC-2.b: The modal contains a confirm button with id or class that carries
    both label variants ("Delete" / "Delete anyway") driven by JS — verified by
    `grep -n "Delete anyway\|confirmDeleteBtn" src/main/resources/templates/owners/ownerDetails.html`
    returning at least one match.
  - AC-2.c: The modal contains a hidden `<form>` whose `action` is set
    dynamically by JS to `/owners/{ownerId}/pets/{petId}/delete` — verified by
    `grep -n "deleteForm\|/delete" src/main/resources/templates/owners/ownerDetails.html`
    returning at least one match.

- **AC-3: Delete POST endpoint exists in PetController**
  - AC-3.a: `PetController.java` contains a `@PostMapping` annotation for
    `/pets/{petId}/delete` — verified by
    `grep -n "pets/{petId}/delete\|/delete" src/main/java/org/springframework/samples/petclinic/owner/PetController.java`
    returning at least one match.

- **AC-4: Hard delete removes pet (and cascade visits) from database**
  - AC-4.a: `Owner.java` declares `orphanRemoval = true` on the `pets`
    `@OneToMany` — verified by
    `grep -n "orphanRemoval" src/main/java/org/springframework/samples/petclinic/owner/Owner.java`
    returning at least one match.
  - AC-4.b: `testDeletePetSuccess` in `PetControllerTests` verifies the
    handler calls `owners.save(owner)` after removing the pet from the
    collection, and asserts `status().is3xxRedirection()` — verified by
    `./mvnw test -Dtest=PetControllerTests` exiting 0.
  - AC-4.c: `testDeletePetWithVisitsCascade` in `PetControllerTests` sets up
    a pet with at least one visit, performs the delete POST, and asserts
    `status().is3xxRedirection()` — verified by
    `./mvnw test -Dtest=PetControllerTests` exiting 0.

- **AC-5: Missing owner returns HTTP 404**
  - AC-5.a: `testDeletePetOwnerNotFound` in `PetControllerTests` stubs
    `owners.findById(999)` to return `Optional.empty()`, performs
    `POST /owners/999/pets/1/delete`, and asserts `status().isNotFound()` —
    verified by `./mvnw test -Dtest=PetControllerTests` exiting 0.

- **AC-6: Missing pet returns HTTP 404**
  - AC-6.a: `testDeletePetNotFound` in `PetControllerTests` stubs owner
    lookup to return a valid owner whose pet collection does not contain
    pet ID 999, performs `POST /owners/{ownerId}/pets/999/delete`, and asserts
    `status().isNotFound()` — verified by
    `./mvnw test -Dtest=PetControllerTests` exiting 0.

- **AC-7: Successful delete redirects to owner details with flash message**
  - AC-7.a: After successful delete, controller redirects to
    `/owners/{ownerId}` — asserted by `status().is3xxRedirection()` and
    `redirectedUrlPattern("/owners/*")` in `testDeletePetSuccess` — verified
    by `./mvnw test -Dtest=PetControllerTests` exiting 0.
  - AC-7.b: `PetController.java` adds a `"Pet has been deleted"` flash
    attribute on redirect — verified by
    `grep -n "Pet has been deleted" src/main/java/org/springframework/samples/petclinic/owner/PetController.java`
    returning at least one match.

- **AC-8: Playwright E2E — create then delete (no visits)**
  - AC-8.a: `pet-management.spec.ts` contains a test that creates a new pet
    for an owner, clicks the Delete trigger, confirms in the modal, and
    asserts the pet name no longer appears on the owner details page — verified
    by
    `grep -n "delete\|Delete" e2e-tests/tests/features/pet-management.spec.ts`
    returning at least one match.
  - AC-8.b: The test captures a screenshot of the confirmation modal (no-visit
    variant) to the Playwright output path — verified by
    `grep -n "screenshot.*modal\|modal.*screenshot\|confirm.*png\|no-visit" e2e-tests/tests/features/pet-management.spec.ts`
    returning at least one match.
  - AC-8.c: `cd e2e-tests && npm test -- --grep "Pet Management"` exits 0
    with all tests passing including the new delete test.

- **AC-9: Playwright E2E — create pet, add visit, then delete**
  - AC-9.a: `pet-management.spec.ts` contains a test that creates a pet, adds
    a visit to it, then deletes the pet via the modal and verifies the pet no
    longer appears — verified by
    `grep -n "visit.*delete\|delete.*visit\|with.visit\|withVisit" e2e-tests/tests/features/pet-management.spec.ts`
    returning at least one match.
  - AC-9.b: The test captures a screenshot of the modal showing the visit-count
    warning — verified by
    `grep -n "with-visit\|visit.*warning\|Delete anyway" e2e-tests/tests/features/pet-management.spec.ts`
    returning at least one match.
  - AC-9.c: `cd e2e-tests && npm test -- --grep "Pet Management"` exits 0
    with all tests passing including the new with-visit delete test.

- **AC-10: No regressions**
  - AC-10.a: `./mvnw test` exits 0 — all existing and new Java tests pass.
  - AC-10.b: `./mvnw test jacoco:report` produces a JaCoCo report showing
    ≥90% line coverage on the new handler method and the `orphanRemoval`
    change path.

## Conventions

- The delete handler in `PetController` must reuse the existing
  `@ModelAttribute("owner")` / `@ModelAttribute("pet")` resolution methods
  (`findOwner`, `findPet`) so that 404 behaviour is inherited automatically —
  no second owner/pet lookup inside the handler body.
- `orphanRemoval = true` is added to `Owner.pets` only; no other cascade or
  fetch settings on that relationship are changed.
- The modal is a single instance in the page (not one per pet). JS populates
  the pet name, visit count, confirm button label, and hidden form action when
  the trigger is clicked.
- The hidden delete form uses `method="post"`. No `_method` override is
  required; the controller mapping uses `@PostMapping`.
- Flash attribute key is `"message"` (consistent with existing pet create /
  edit flash attributes); value is `"Pet has been deleted"`.
- TDD is mandatory: all four `PetControllerTests` additions (AC-4.b, AC-4.c,
  AC-5.a, AC-6.a) are committed in their RED (failing) state before the
  handler and `orphanRemoval` change are committed.
- The modal and JS are added in a single template commit; no intermediate
  "broken modal" commit is acceptable.
- E2E tests go in the existing `pet-management.spec.ts` file; no new spec
  file is created.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
