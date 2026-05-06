# Proofs: Task 02 — Write failing PetController and VisitController 404 tests (RED)

Covers: AC-6.a, AC-7.a

## Planned evidence

- `./mvnw test -Dtest=PetControllerTests` output showing `testInitUpdatePetFormNotFound`
  FAILED (confirming RED state — null pet produces 500, not 404).
- `./mvnw test -Dtest=VisitControllerTests` output showing `testInitNewVisitFormOwnerNotFound`
  FAILED (confirming RED state — IllegalArgumentException produces 500, not 404).

## Completion notes

(Filled in by `implement-sdd-spec`.)
