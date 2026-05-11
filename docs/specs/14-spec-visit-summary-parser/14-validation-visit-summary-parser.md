# Validation: VisitSummaryParser (14)

## Automated verification

From repository root:

```bash
# Compile check — all new files must compile cleanly
./mvnw compile

# Full test suite — all existing and new tests must pass
./mvnw test

# Coverage report — confirm ≥90% line coverage on new code
./mvnw test jacoco:report
# View at: target/site/jacoco/index.html
```

**Expected:** `BUILD SUCCESS`, zero test failures, zero compilation errors.
**Coverage:** `VisitSummaryParser` and `VisitSummaryParseException` appear in the
JaCoCo report with ≥90% line coverage.

## Traceability

- Feature spec: `14-spec-visit-summary-parser.md`
- Task breakdown: `14-tasks-visit-summary-parser.md`
- Questions and decisions: `14-questions-1-visit-summary-parser.md`
- Per-task evidence: `14-proofs/14-task-NN-proofs.md`
- Upstream specs: spec-12 (AI visits schema), spec-13 (async config)
- TASK-04 (VisitSummary DTO + VisitUrgency enum) — must be `delivered` before
  implementation of this spec begins

## Manual checks

None.

## Coverage matrix

| AC ID  | Criterion                                                                                   | Proof artifact                    | Evidence type    | Status  |
|--------|---------------------------------------------------------------------------------------------|-----------------------------------|------------------|---------|
| AC-1.a | `VisitSummaryParseException.java` exists in `owner` package                                 | `14-proofs/14-task-02-proofs.md`  | file creation    | PENDING |
| AC-1.b | Class declaration includes `extends RuntimeException`                                       | `14-proofs/14-task-02-proofs.md`  | file creation    | PENDING |
| AC-1.c | Constructor `(String message, Throwable cause)` exists                                      | `14-proofs/14-task-02-proofs.md`  | file creation    | PENDING |
| AC-2.a | `VisitSummaryParser.java` exists in `owner` package                                         | `14-proofs/14-task-03-proofs.md`  | file creation    | PENDING |
| AC-2.b | No Spring stereotype annotations on the class                                               | `14-proofs/14-task-03-proofs.md`  | file creation    | PENDING |
| AC-2.c | `parse(String json)` method with return type `VisitSummary` exists                          | `14-proofs/14-task-03-proofs.md`  | file creation    | PENDING |
| AC-3.a | `shouldParseAllFieldsFromValidJson` passes                                                  | `14-proofs/14-task-03-proofs.md`  | Maven test pass  | PENDING |
| AC-4.a | `shouldReturnNullFollowUpWhenAbsent` passes                                                 | `14-proofs/14-task-03-proofs.md`  | Maven test pass  | PENDING |
| AC-5.a | `shouldMapUnknownUrgencyToRoutine` passes                                                   | `14-proofs/14-task-03-proofs.md`  | Maven test pass  | PENDING |
| AC-6.a | `shouldThrowParseExceptionForMalformedJson` passes                                          | `14-proofs/14-task-03-proofs.md`  | Maven test pass  | PENDING |
| AC-7.a | `shouldHandleEmptyTagsArray` passes                                                         | `14-proofs/14-task-03-proofs.md`  | Maven test pass  | PENDING |
| AC-7.b | `shouldHandleSingleTag` passes                                                              | `14-proofs/14-task-03-proofs.md`  | Maven test pass  | PENDING |
| AC-7.c | `shouldHandleMultipleTags` passes                                                           | `14-proofs/14-task-03-proofs.md`  | Maven test pass  | PENDING |
| AC-8.a | `./mvnw test` exits 0 after all changes applied                                             | `14-proofs/14-task-04-proofs.md`  | command output   | PENDING |

## Definition of done

- [ ] AC-1.a: `VisitSummaryParseException.java` exists in `owner` package.
- [ ] AC-1.b: Class declaration includes `extends RuntimeException`.
- [ ] AC-1.c: Constructor `(String message, Throwable cause)` exists.
- [ ] AC-2.a: `VisitSummaryParser.java` exists in `owner` package.
- [ ] AC-2.b: No Spring stereotype annotations on the class.
- [ ] AC-2.c: `parse(String json)` method with return type `VisitSummary` exists.
- [ ] AC-3.a: `shouldParseAllFieldsFromValidJson` passes.
- [ ] AC-4.a: `shouldReturnNullFollowUpWhenAbsent` passes.
- [ ] AC-5.a: `shouldMapUnknownUrgencyToRoutine` passes.
- [ ] AC-6.a: `shouldThrowParseExceptionForMalformedJson` passes.
- [ ] AC-7.a: `shouldHandleEmptyTagsArray` passes.
- [ ] AC-7.b: `shouldHandleSingleTag` passes.
- [ ] AC-7.c: `shouldHandleMultipleTags` passes.
- [ ] AC-8.a: `./mvnw test` exits 0 after all changes.
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
