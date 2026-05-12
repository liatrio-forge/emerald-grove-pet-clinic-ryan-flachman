# Validation: VisitSummaryIntegrationTest (22)

## Automated verification

From repository root:

```bash
# 1. Verify test compilation — AC-1.a
./mvnw test-compile
# Expected: BUILD SUCCESS

# 2. VisitSummaryHappyPathIT annotations — AC-2.b, AC-2.c
grep -c "@SpringBootTest" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
grep -c "@AutoConfigureMockMvc" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
grep -c "@DisabledInNativeImage" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
grep -c "@DisabledInAotMode" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
! grep -q "@Transactional" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java && echo "no @Transactional"
# Expected: 1 / 1 / 1 / 1 / "no @Transactional"

# 3. VisitSummaryFailureIT annotations — AC-5.b, AC-5.c, AC-5.d
grep -c "@SpringBootTest" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
grep -c "@AutoConfigureMockMvc" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
grep -c "@MockitoBean" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
! grep -q "@Transactional" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java && echo "no @Transactional"
# Expected: 1 / 1 / 1 / "no @Transactional"

# 4. Run only the new integration tests — AC-3, AC-4, AC-6
./mvnw test -Dtest="VisitSummaryHappyPathIT,VisitSummaryFailureIT"
# Expected: BUILD SUCCESS, 4 test methods pass (shouldGenerateSummaryAfterVisitSave,
#           shouldMapDescriptionKeywordToUrgency[1], shouldMapDescriptionKeywordToUrgency[2],
#           shouldMarkVisitFailedWhenClientThrows)

# 5. Full test suite — AC-7.a
./mvnw test
# Expected: BUILD SUCCESS, zero failures, zero errors
```

## Traceability

- Feature spec: `22-spec-visit-summary-integration-test.md`
- Task breakdown: `22-tasks-visit-summary-integration-test.md`
- Questions and decisions: `22-questions-1-visit-summary-integration-test.md`
- Per-task evidence: `22-proofs/22-task-NN-proofs.md`
- Upstream specs: spec-12 (schema), spec-13 (async config), spec-14 (Visit entity
  - AiStatus), spec-15 (VisitSummary DTO), spec-16 (VisitPromptBuilder), spec-17
  (ClaudeApiClient interface), spec-18 (ClaudeApiClientStub), spec-20
  (VisitSummaryService), spec-21 (VisitSummaryController + VisitController trigger)
- Parent epic: `docs/epic-ai-visit-summary.md`

## Manual checks

None — all criteria are verifiable by Maven commands or grep.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `./mvnw test-compile` exits 0 with Awaitility import in both test files | `22-proofs/22-task-01-proofs.md` | command output | PASS |
| AC-2.a | `VisitSummaryHappyPathIT.java` file exists | `22-proofs/22-task-02-proofs.md` | file creation | PASS |
| AC-2.b | Class has `@SpringBootTest`, `@AutoConfigureMockMvc`, `@DisabledInNativeImage`, `@DisabledInAotMode` | `22-proofs/22-task-02-proofs.md` | command output | PASS |
| AC-2.c | Class has no `@Transactional` | `22-proofs/22-task-02-proofs.md` | command output | PASS |
| AC-3.a | `shouldGenerateSummaryAfterVisitSave` passes — POST redirects, aiStatus reaches DONE within 5s | `22-proofs/22-task-02-proofs.md` | Maven test pass | PASS |
| AC-3.b | After DONE, `aiSummary` is non-null and non-blank | `22-proofs/22-task-02-proofs.md` | Maven test pass | PASS |
| AC-3.c | After DONE, `aiTags` is non-null and non-blank | `22-proofs/22-task-02-proofs.md` | Maven test pass | PASS |
| AC-3.d | After DONE, `aiUrgency` is non-null and non-blank | `22-proofs/22-task-02-proofs.md` | Maven test pass | PASS |
| AC-3.e | GET `/visits/{id}/summary` returns DONE JSON with non-null summary, tags, urgency | `22-proofs/22-task-02-proofs.md` | Maven test pass | PASS |
| AC-4.a | `shouldMapDescriptionKeywordToUrgency[limp → urgent]` passes | `22-proofs/22-task-02-proofs.md` | Maven test pass | PASS |
| AC-4.b | `shouldMapDescriptionKeywordToUrgency[checkup → routine]` passes | `22-proofs/22-task-02-proofs.md` | Maven test pass | PASS |
| AC-5.a | `VisitSummaryFailureIT.java` file exists | `22-proofs/22-task-03-proofs.md` | file creation | PASS |
| AC-5.b | Class has `@SpringBootTest`, `@AutoConfigureMockMvc`, `@DisabledInNativeImage`, `@DisabledInAotMode` | `22-proofs/22-task-03-proofs.md` | command output | PASS |
| AC-5.c | Class declares `@MockitoBean ClaudeApiClient claudeApiClient` | `22-proofs/22-task-03-proofs.md` | command output | PASS |
| AC-5.d | Class has no `@Transactional` | `22-proofs/22-task-03-proofs.md` | command output | PASS |
| AC-6.a | `shouldMarkVisitFailedWhenClientThrows` — aiStatus reaches FAILED within 5s | `22-proofs/22-task-03-proofs.md` | Maven test pass | PASS |
| AC-6.b | GET `/visits/{id}/summary` returns `{"status":"FAILED"}` | `22-proofs/22-task-03-proofs.md` | Maven test pass | PASS |
| AC-7.a | `./mvnw test` exits 0, zero failures | `22-proofs/22-task-04-proofs.md` | command output | PASS |

## Definition of done

- [x] AC-1.a: `./mvnw test-compile` exits 0 with Awaitility import in both test files
- [x] AC-2.a: `VisitSummaryHappyPathIT.java` exists at the correct path
- [x] AC-2.b: Class has all four required Spring annotations
- [x] AC-2.c: Class has no `@Transactional`
- [x] AC-3.a: `shouldGenerateSummaryAfterVisitSave` passes — redirect and DONE within 5s
- [x] AC-3.b: After DONE, `aiSummary` is non-null and non-blank
- [x] AC-3.c: After DONE, `aiTags` is non-null and non-blank
- [x] AC-3.d: After DONE, `aiUrgency` is non-null and non-blank
- [x] AC-3.e: Polling endpoint returns DONE JSON with non-null summary, tags, urgency
- [x] AC-4.a: `shouldMapDescriptionKeywordToUrgency[limp → urgent]` passes
- [x] AC-4.b: `shouldMapDescriptionKeywordToUrgency[checkup → routine]` passes
- [x] AC-5.a: `VisitSummaryFailureIT.java` exists at the correct path
- [x] AC-5.b: Class has all four required Spring annotations
- [x] AC-5.c: Class declares `@MockitoBean ClaudeApiClient claudeApiClient`
- [x] AC-5.d: Class has no `@Transactional`
- [x] AC-6.a: `shouldMarkVisitFailedWhenClientThrows` — FAILED status within 5s
- [x] AC-6.b: Polling endpoint returns `{"status":"FAILED"}`
- [x] AC-7.a: `./mvnw test` exits 0 with zero failures
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [ ] Parent epic child-registry checkbox ticked (if applicable).
