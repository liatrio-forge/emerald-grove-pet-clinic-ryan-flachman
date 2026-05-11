# Validation: Visit AI Fields (14)

## Automated verification

From repository root:

```bash
# Compile — must exit 0 after all tasks complete
./mvnw compile

# Full test suite — must exit 0 with no failures
./mvnw test

# Structural checks: AiStatus enum exists and declares four values
grep -c "PENDING\|PROCESSING\|DONE\|FAILED" \
  src/main/java/org/springframework/samples/petclinic/owner/AiStatus.java
# Expected: 4

# Visit.java carries @Enumerated(EnumType.STRING) annotation
grep -c "EnumType.STRING" \
  src/main/java/org/springframework/samples/petclinic/owner/Visit.java
# Expected: 1 (or more)

# Visit.java has description column length of 2000
grep "length = 2000" \
  src/main/java/org/springframework/samples/petclinic/owner/Visit.java
# Expected: at least one match

# Visit.java declares all five AI column names
grep -c "ai_status\|ai_summary\|ai_tags\|ai_urgency\|ai_follow_up" \
  src/main/java/org/springframework/samples/petclinic/owner/Visit.java
# Expected: 5

# Coverage report
./mvnw test jacoco:report
# View: target/site/jacoco/index.html
# Expected: ≥90% line coverage on new code in owner package
```

## Traceability

- Feature spec: `14-spec-visit-ai-fields.md`
- Task breakdown: `14-tasks-visit-ai-fields.md`
- Questions and decisions: `14-questions-1-visit-ai-fields.md`
- Per-task evidence: `14-proofs/14-task-NN-proofs.md`
- Upstream specs: spec 12 (`ai-visits-schema`, delivered) — DB columns exist
- Parent epic: `docs/epic-ai-visit-summary.md`

## Manual checks

_None — all criteria are verifiable by automated command._

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `AiStatus.java` exists at the correct path | `14-proofs/14-task-02-proofs.md` | file creation | PASS |
| AC-1.b | Enum declares exactly `PENDING`, `PROCESSING`, `DONE`, `FAILED` | `14-proofs/14-task-02-proofs.md` | file creation | PASS |
| AC-1.c | `AiStatusTest` passes asserting all four values | `14-proofs/14-task-02-proofs.md` | Maven test pass | PASS |
| AC-2.a | `aiStatus` field with `@Column(name="ai_status", length=20)` and `@Enumerated(EnumType.STRING)` | `14-proofs/14-task-04-proofs.md` | file edit | PENDING |
| AC-2.b | `aiSummary` field with `@Column(name="ai_summary", length=1000)` | `14-proofs/14-task-04-proofs.md` | file edit | PENDING |
| AC-2.c | `aiTags` field with `@Column(name="ai_tags", length=500)` | `14-proofs/14-task-04-proofs.md` | file edit | PENDING |
| AC-2.d | `aiUrgency` field with `@Column(name="ai_urgency", length=20)` | `14-proofs/14-task-04-proofs.md` | file edit | PENDING |
| AC-2.e | `aiFollowUp` field with `@Column(name="ai_follow_up", length=500)` | `14-proofs/14-task-04-proofs.md` | file edit | PENDING |
| AC-3.a | `description` field has `@Column(length=2000)` | `14-proofs/14-task-04-proofs.md` | file edit | PENDING |
| AC-4.a | `new Visit().getAiStatus()` returns `AiStatus.PENDING` | `14-proofs/14-task-04-proofs.md` | Maven test pass | PENDING |
| AC-4.b | Four nullable fields return `null` on new Visit | `14-proofs/14-task-04-proofs.md` | Maven test pass | PENDING |
| AC-5.a | `setAiStatus` / `getAiStatus` round-trip | `14-proofs/14-task-04-proofs.md` | Maven test pass | PENDING |
| AC-5.b | Setters/getters for remaining four string fields round-trip | `14-proofs/14-task-04-proofs.md` | Maven test pass | PENDING |
| AC-6.a | `@DataJpaTest` persists and reloads Visit with all five AI fields set | `14-proofs/14-task-04-proofs.md` | Maven test pass | PENDING |
| AC-6.b | `@DataJpaTest` confirms fresh Visit reads back with `PENDING` and nulls | `14-proofs/14-task-04-proofs.md` | Maven test pass | PENDING |
| AC-7.a | `./mvnw test` exits 0 after all changes | `14-proofs/14-task-05-proofs.md` | command output | PENDING |

## Definition of done

- [ ] AC-1.a: `AiStatus.java` exists at the correct path.
- [ ] AC-1.b: Enum declares exactly `PENDING`, `PROCESSING`, `DONE`, `FAILED`.
- [ ] AC-1.c: `AiStatusTest` passes asserting all four values.
- [ ] AC-2.a: `aiStatus` field mapped with `@Column` and `@Enumerated(EnumType.STRING)`.
- [ ] AC-2.b: `aiSummary` field mapped with `@Column(name="ai_summary", length=1000)`.
- [ ] AC-2.c: `aiTags` field mapped with `@Column(name="ai_tags", length=500)`.
- [ ] AC-2.d: `aiUrgency` field mapped with `@Column(name="ai_urgency", length=20)`.
- [ ] AC-2.e: `aiFollowUp` field mapped with `@Column(name="ai_follow_up", length=500)`.
- [ ] AC-3.a: `description` field carries `@Column(length=2000)`.
- [ ] AC-4.a: `new Visit().getAiStatus()` returns `AiStatus.PENDING`.
- [ ] AC-4.b: Four nullable fields return `null` on new Visit.
- [ ] AC-5.a: `setAiStatus` / `getAiStatus` round-trip verified by test.
- [ ] AC-5.b: Setter/getter round-trips for all four string fields verified.
- [ ] AC-6.a: `@DataJpaTest` persist-and-reload with all five AI fields set passes.
- [ ] AC-6.b: `@DataJpaTest` fresh-Visit defaults pass.
- [ ] AC-7.a: `./mvnw test` exits 0 after all changes.
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
