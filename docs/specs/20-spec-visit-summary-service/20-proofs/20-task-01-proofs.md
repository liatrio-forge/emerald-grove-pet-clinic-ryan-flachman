# Proofs: Task 01 — Write failing `VisitSummaryParserTests` (RED)

Covers: AC-15.a

## Captured Maven output (`./mvnw test -Dtest=VisitSummaryParserTests`)

Test classes existed; production sources `VisitSummaryParser.java`,
`VisitSummaryParseException.java`, and `VisitSummaryService.java` were moved out
temporarily so the module could compile main code only (`VisitSummaryService`
depends on `VisitSummaryParser`; without the parser neither main nor tests
linked). Maven then fails at **testCompile** before any tests ran — acceptable
RED evidence for AC-15.a (“failing compilation before `VisitSummaryParser.java`
is created”). Excerpt:

```text
[INFO] BUILD FAILURE
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.14.1:testCompile (default-testCompile) ...
[ERROR] ... VisitSummaryParserTests.java:[12,23] cannot find symbol
[ERROR]   symbol:   class VisitSummaryParser
...
[ERROR] ... VisitSummaryParserTests.java:[52,87] cannot find symbol
[ERROR]   symbol:   class VisitSummaryParseException
...
Finished at: 2026-05-12T10:21:24-05:00
```

(Full raw log archived during capture; above matches the reproduced failure.)

## Completion notes

Evidence captured on branch `feat/visit-summary-spec-docs`,
2026-05-12 (`Total time: ~3.9 s`, BUILD FAILURE).
