# Proofs: Task 02 — Write failing OwnerControllerTests for duplicate detection (RED)

Covers: AC-3.a, AC-3.b, AC-3.c

## Planned evidence

- `OwnerControllerTests.java` diff showing `@MockitoBean OwnerService ownerService`, default stub in `@BeforeEach`, and the new `testProcessCreationFormDuplicateRejected` test method.
- `./mvnw test -Dtest=OwnerControllerTests` failure output confirming `testProcessCreationFormDuplicateRejected` fails because `OwnerService` is not yet wired in the controller (RED phase).

## Completion notes

(Filled in by `implement-sdd-spec`.)
