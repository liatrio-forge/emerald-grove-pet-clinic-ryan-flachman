# Proofs: Task 02 — Implement delete endpoint; add orphanRemoval to Owner.pets (GREEN)

Covers: AC-3.a, AC-4.a, AC-4.b, AC-4.c, AC-5.a, AC-6.a, AC-7.a, AC-7.b

## Planned evidence

- `grep -n "orphanRemoval" src/main/java/.../owner/Owner.java` output showing
  `orphanRemoval = true` on the `pets` `@OneToMany`.
- `grep -n "pets/{petId}/delete\|/delete" src/main/java/.../owner/PetController.java`
  output showing the new `@PostMapping`.
- `grep -n "Pet has been deleted" src/main/java/.../owner/PetController.java`
  output showing the flash attribute.
- `./mvnw test -Dtest=PetControllerTests` output showing all four new tests
  passing alongside pre-existing tests (`BUILD SUCCESS`).
- `./mvnw test` full-suite output (`BUILD SUCCESS`, no regressions).

## Completion notes

(Filled in by `implement-sdd-spec`.)
