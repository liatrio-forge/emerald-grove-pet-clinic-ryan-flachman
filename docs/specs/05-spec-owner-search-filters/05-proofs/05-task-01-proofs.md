# Proofs: Task 01 — Write failing OwnerControllerTests (RED)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-4.a

## Planned evidence

- `OwnerRepository.findBySearchCriteria` method signature (with `@Query`) added
  to the interface — show the new method.
- `OwnerControllerTests` updated `@BeforeEach` — show the new stub wiring.
- New test methods added — show `testProcessFindFormByCity`,
  `testProcessFindFormByTelephone`, `testProcessFindFormByCombinedCriteria`,
  `testProcessFindFormInvalidTelephone`, `testProcessFindFormValidPartialTelephone`,
  and the updated `testProcessFindFormNoOwnersFound`.
- Output of `./mvnw test -Dtest=OwnerControllerTests` showing the new tests
  **failing** (RED phase confirmed).

## Completion notes

(Filled in by `implement-sdd-spec`.)
