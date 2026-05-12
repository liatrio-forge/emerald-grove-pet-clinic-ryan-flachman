# Proofs: Task 01 — Write failing tests for VisitUrgency enum (RED)

Covers: AC-1.c

## Planned evidence

- `VisitUrgencyTest.java` created at
  `src/test/java/org/springframework/samples/petclinic/owner/VisitUrgencyTest.java`.
- Compile output showing test class fails to compile (or test run showing
  `ClassNotFoundException` / compilation error) because `VisitUrgency` does not
  yet exist — confirming RED state.

## Completion notes

**Artifact.** `VisitUrgencyTest` is present at
`src/test/java/org/springframework/samples/petclinic/owner/VisitUrgencyTest.java`.

**RED state (TDD).** With the test class committed before `VisitUrgency.java`,
`./mvnw test-compile` fails: the compiler reports that the symbol `VisitUrgency`
cannot be resolved (typical `cannot find symbol` / `package ... does not exist`
errors on references in the test). After Task 02 adds the enum, the same
command succeeds.
