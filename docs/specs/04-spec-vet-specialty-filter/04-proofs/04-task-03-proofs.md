# Proofs: Task 03 — Add VetRepository query methods and update VetController (GREEN)

Covers: AC-2.a, AC-2.b, AC-2.c

## Planned evidence

- `VetRepository.java` diff showing `findBySpecialtyName`, `findWithNoSpecialties`, and `findAllSpecialties` methods.
- `VetController.java` diff showing the new `specialty` param, branching logic in `findPaginated`, and `allSpecialties` / `selectedSpecialty` model attributes.
- Output of `./mvnw test -Dtest=VetControllerTests` showing new specialty filter tests now **passing** (GREEN for unit tests).

## Completion notes

(Filled in by `implement-sdd-spec`.)
