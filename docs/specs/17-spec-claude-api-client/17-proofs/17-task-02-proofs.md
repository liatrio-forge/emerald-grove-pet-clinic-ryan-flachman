# Proofs: Task 02 — Create ClaudeApiClient interface and all four POJO records (GREEN)

Covers: AC-1.a, AC-1.b, AC-1.c, AC-1.d, AC-2.a, AC-2.b, AC-2.c, AC-2.d,
        AC-3.a, AC-3.b, AC-3.c, AC-4.a, AC-4.b, AC-4.c, AC-4.d,
        AC-5.a, AC-5.b, AC-5.c

## Planned evidence

- Output of `./mvnw test -Dtest="ClaudeRequestTest,MessageTest,ClaudeResponseTest,ContentBlockTest"`
  showing all four test classes passing.
- Output of `./mvnw compile` exiting 0.
- Output of `grep` for the interface method signature.
- Output of `grep` for no Spring annotations on the interface.
- Output of `grep` for `@JsonProperty("max_tokens")` and `@JsonProperty("stop_reason")`.
- File existence checks for all five types.

## Completion notes

(Filled in by `implement-sdd-spec`.)
