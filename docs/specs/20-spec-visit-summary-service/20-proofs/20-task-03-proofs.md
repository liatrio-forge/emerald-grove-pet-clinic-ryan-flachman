# Proofs: Task 03 — Extend `VisitRepository`, `Visit.java`, and correct `VisitPromptBuilder` key

Covers: AC-12.a, AC-12.b, AC-13.a, AC-13.b, AC-14.a, AC-14.b

## Planned evidence

- `grep "Optional<Visit> findById" .../VisitRepository.java`
- `grep "Visit save" .../VisitRepository.java`
- `grep "ManyToOne" .../Visit.java`
- `grep "insertable = false" .../Visit.java`
- `grep "follow_up" .../VisitPromptBuilder.java`
- Maven output from `./mvnw compile` showing `BUILD SUCCESS`.
- Maven output from `./mvnw test -Dtest=VisitPromptBuilderTest` showing all tests
  passing (confirms the key rename did not break existing tests).

## Completion notes

(Filled in by `implement-sdd-spec`.)
