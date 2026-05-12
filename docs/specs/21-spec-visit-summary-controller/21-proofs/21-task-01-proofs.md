# Proofs: Task 01 — Write failing VisitSummaryControllerTests (RED)

Covers: AC-8.a

## Evidence

Command (controller and DTO not yet present):

```text
./mvnw test -Dtest=VisitSummaryControllerTests
```

Maven output (excerpt):

```text
[ERROR] COMPILATION ERROR :
[ERROR] .../VisitSummaryControllerTests.java:[41,13] cannot find symbol
[ERROR]   symbol: class VisitSummaryController
[ERROR] Failed to execute goal ... testCompile ... Compilation failure
```

## Completion notes

Tests were added first; compilation failed until `VisitSummaryController` existed, satisfying strict TDD RED evidence for AC-8.a.
