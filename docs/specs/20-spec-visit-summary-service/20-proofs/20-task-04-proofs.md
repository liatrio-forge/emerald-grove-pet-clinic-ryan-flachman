# Proofs: Task 04 — Write failing `VisitSummaryServiceTests` (RED)

Covers: AC-15.b

## Captured Maven output (`./mvnw test -Dtest=VisitSummaryServiceTests`)

`VisitSummaryService.java` moved out temporarily. Main sources still compile with
parser and exception present; **testCompile** fails resolving
`VisitSummaryService` referenced by tests.

```text
[INFO] BUILD FAILURE
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.14.1:testCompile ...
[ERROR] ... VisitSummaryServiceTests.java:[38,17] cannot find symbol
[ERROR]   symbol:   class VisitSummaryService
...
Finished at: 2026-05-12T10:21:35-05:00
```

## Completion notes

RED evidence reproduced 2026-05-12 (BUILD FAILURE during test-compile).
