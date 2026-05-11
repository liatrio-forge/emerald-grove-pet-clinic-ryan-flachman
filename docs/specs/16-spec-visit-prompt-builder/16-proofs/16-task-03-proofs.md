# Proofs: Task 03 — Implement VisitPromptBuilder static utility (GREEN + REFACTOR)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-3.c, AC-4.a,
AC-4.b, AC-4.c, AC-4.d, AC-4.e, AC-5.a, AC-5.b, AC-5.c, AC-5.d

## Planned evidence

- Listing of `src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java`
  confirming no Spring annotations and the static `build` method (AC-2.a, AC-2.b, AC-2.c).
- `./mvnw compile` output showing `BUILD SUCCESS` (AC-2.d).
- `grep` output (empty) for Spring annotations on `VisitPromptBuilder.java` (AC-2.b).
- `./mvnw test -Dtest="PromptRequestTest,VisitPromptBuilderTest"` output showing all
  cases passing, including all edge-case tests (AC-3, AC-4, AC-5).

## Completion notes

**Artifact.** `VisitPromptBuilder.java` at
`src/main/java/org/springframework/samples/petclinic/owner/VisitPromptBuilder.java`:
`public static PromptRequest build(Visit visit, Pet pet)`; `private static final String SYSTEM_PROMPT`;
private helpers `resolveAgeYears`, `resolveTypeName`, `resolveDescription` (AC-2.a, AC-2.c).

**Spring annotation grep** (AC-2.b):

```text
grep -r "@Component\|@Service\|@Bean" src/main/java/.../VisitPromptBuilder.java
# (no matching lines; exit status 1)
```

**Compile** (AC-2.d):

```text
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.801 s
[INFO] Finished at: 2026-05-11T14:00:29-05:00
[INFO] ------------------------------------------------------------------------
```

**Tests** `./mvnw test -Dtest="PromptRequestTest,VisitPromptBuilderTest"` (2026-05-11):

```text
[INFO] Running org.springframework.samples.petclinic.owner.VisitPromptBuilderTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.083 s -- in org.springframework.samples.petclinic.owner.VisitPromptBuilderTest
[INFO] Running org.springframework.samples.petclinic.owner.PromptRequestTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in org.springframework.samples.petclinic.owner.PromptRequestTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
