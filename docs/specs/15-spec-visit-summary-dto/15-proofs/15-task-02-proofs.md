# Proofs: Task 02 — Implement VisitUrgency enum (GREEN)

Covers: AC-1.a, AC-1.b, AC-1.c

## Planned evidence

- `VisitUrgency.java` created at
  `src/main/java/org/springframework/samples/petclinic/owner/VisitUrgency.java`.
- Output of `./mvnw test -Dtest=VisitUrgencyTest` showing `Tests run: 2,
  Failures: 0, Errors: 0` (or similar passing count).

## Completion notes

**Artifact.** `VisitUrgency.java` is present at
`src/main/java/org/springframework/samples/petclinic/owner/VisitUrgency.java`
with values `ROUTINE`, `MONITOR`, `URGENT` in that order (AC-1.b).

**Command.** `./mvnw test -Dtest=VisitUrgencyTest` (2026-05-11):

```text
[INFO] Running org.springframework.samples.petclinic.owner.VisitUrgencyTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.102 s -- in org.springframework.samples.petclinic.owner.VisitUrgencyTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

Three tests run: count, declaration order (AC-1.b), and `valueOf` for each
constant (AC-1.c).
