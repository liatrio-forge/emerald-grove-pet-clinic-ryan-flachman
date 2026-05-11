# Proofs: Task 01 — Write failing construction tests for all four POJOs (RED)

Covers: AC-2.d, AC-3.c, AC-4.d, AC-5.c, AC-6.a

## Evidence

Command (from repository root, before any production types existed):

```bash
./mvnw test -Dtest="ClaudeRequestTest,MessageTest,ClaudeResponseTest,ContentBlockTest"
```

**Result:** `BUILD FAILURE` — `testCompile` failed with `cannot find symbol` for
`ClaudeRequest`, `Message`, `ClaudeResponse`, and `ContentBlock` (10 compilation
errors across the four test classes). Excerpt:

```text
[ERROR] COMPILATION ERROR :
[ERROR] .../ClaudeRequestTest.java:[13,17] cannot find symbol
  symbol:   class ClaudeRequest
[ERROR] .../ClaudeRequestTest.java:[14,45] cannot find symbol
  symbol:   class Message
[ERROR] .../ClaudeResponseTest.java:[13,17] cannot find symbol
  symbol:   class ClaudeResponse
[ERROR] .../ClaudeResponseTest.java:[14,45] cannot find symbol
  symbol:   class ContentBlock
[ERROR] .../ContentBlockTest.java:[11,17] cannot find symbol
  symbol:   class ContentBlock
[ERROR] .../MessageTest.java:[11,17] cannot find symbol
  symbol:   class Message
[INFO] BUILD FAILURE
```

This satisfies AC-6.a (RED phase: tests do not compile until production code exists).

## Completion notes

Captured 2026-05-11 after adding only the four test classes.
