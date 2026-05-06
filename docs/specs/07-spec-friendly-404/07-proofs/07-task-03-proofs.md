# Proofs: Task 03 — Create ResourceNotFoundException; replace throws in all three controllers (GREEN)

Covers: AC-1.a, AC-1.b, AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-5.a, AC-6.a, AC-7.a

## Planned evidence

- `find src/main/java -name "ResourceNotFoundException.java"` output showing the
  file path.
- `grep -n "@ResponseStatus" ...ResourceNotFoundException.java` output.
- `grep -n "ResourceNotFoundException" ...OwnerController.java` output (≥1 match).
- `grep -n "ResourceNotFoundException" ...PetController.java` output (≥1 match).
- `grep -n "ResourceNotFoundException" ...VisitController.java` output (≥1 match).
- `grep -rn "throw new IllegalArgumentException" src/.../owner/` output (no
  matches).
- `./mvnw test -Dtest=OwnerControllerTests` output confirming all tests pass
  including `testShowOwnerNotFound` (GREEN).
- `./mvnw test -Dtest=PetControllerTests` output confirming `testInitUpdatePetFormNotFound`
  passes (GREEN).
- `./mvnw test -Dtest=VisitControllerTests` output confirming
  `testInitNewVisitFormOwnerNotFound` passes (GREEN).

## Completion notes

(Filled in by `implement-sdd-spec`.)
