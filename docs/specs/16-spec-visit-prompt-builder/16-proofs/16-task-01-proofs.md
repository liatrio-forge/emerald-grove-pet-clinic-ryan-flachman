# Proofs: Task 01 — Write failing tests for PromptRequest and VisitPromptBuilder (RED)

Covers: AC-1.d, AC-2.b, AC-2.c, AC-3.a, AC-3.b, AC-3.c, AC-4.a, AC-4.b,
AC-4.c, AC-4.d, AC-4.e, AC-5.a, AC-5.b, AC-5.c, AC-5.d, AC-6.a

## Planned evidence

- `./mvnw test -Dtest="PromptRequestTest,VisitPromptBuilderTest"` output showing
  compilation failure (classes `PromptRequest` and `VisitPromptBuilder` do not
  yet exist). Captures the RED phase required by AC-6.a.

## Completion notes

**Command.** `./mvnw test -Dtest="PromptRequestTest,VisitPromptBuilderTest"` (2026-05-11)
with only the two test sources present and **no** `PromptRequest.java` or
`VisitPromptBuilder.java` under `src/main`. Maven fails at `testCompile` because
the production types are missing (AC-6.a — RED before production code).

**Excerpt** (compilation errors; representative lines):

```text
[ERROR] COMPILATION ERROR :
[ERROR] .../PromptRequestTest.java:[11,17] cannot find symbol
[ERROR]   symbol:   class PromptRequest
[ERROR] .../VisitPromptBuilderTest.java:[33,40] cannot find symbol
[ERROR]   symbol:   variable VisitPromptBuilder
[ERROR] Failed to execute goal ...: Compilation failure
```
