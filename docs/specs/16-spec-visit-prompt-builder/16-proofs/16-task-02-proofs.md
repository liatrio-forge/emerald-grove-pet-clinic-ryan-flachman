# Proofs: Task 02 — Implement PromptRequest record (GREEN)

Covers: AC-1.a, AC-1.b, AC-1.c, AC-1.d

## Planned evidence

- Listing of `src/main/java/org/springframework/samples/petclinic/owner/PromptRequest.java`
  confirming the `record` declaration and two-component signature (AC-1.a, AC-1.b, AC-1.c).
- `./mvnw test -Dtest="PromptRequestTest"` output showing all `PromptRequestTest`
  cases passing (AC-1.d).

## Completion notes

**Artifact.** `PromptRequest.java` at
`src/main/java/org/springframework/samples/petclinic/owner/PromptRequest.java`:

`public record PromptRequest(String systemPrompt, String userMessage) {}`

Package `org.springframework.samples.petclinic.owner`; no Spring/JPA/validation
annotations (AC-1.a–c).

**Command.** `./mvnw test -Dtest=PromptRequestTest` (2026-05-11):

```text
[INFO] Running org.springframework.samples.petclinic.owner.PromptRequestTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.064 s -- in org.springframework.samples.petclinic.owner.PromptRequestTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
