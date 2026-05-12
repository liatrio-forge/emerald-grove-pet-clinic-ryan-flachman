# Proofs: Task 06 — Validate and capture proof artifacts

Covers: all (AC-16.a, AC-16.b, full regression)

## Full suite + JaCoCo (`./mvnw test jacoco:report`)

Excerpt:

```text
[INFO] Tests run: 192, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- jacoco:0.8.14:report (default-cli)
[INFO] Analyzed bundle 'petclinic' with 43 classes
[INFO] BUILD SUCCESS
[INFO] Total time:  40.877 s
[INFO] Finished at: 2026-05-12T10:22:42-05:00
```

## JaCoCo CSV rows (`target/site/jacoco/jacoco.csv`)

Parsed headers:
`GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,...LINE_MISSED,LINE_COVERED,...`

```text
petclinic,...,VisitSummaryService,0,97,0,2,0,27,0,4,0,3
petclinic,...,VisitSummaryParser,0,130,0,12,0,32,0,12,0,6
```

Interpretation:

- **`VisitSummaryService`**: zero missed instructions / branches / lines in report.
- **`VisitSummaryParser`**: zero missed instructions / branches / lines in report.

Thus **both classes meet the ≥90% line-coverage acceptance bar** for AC-16.b.

## Structural greps (`20-validation-visit-summary-service.md` §7 checklist)

Executed from repo root:

```text
@Component                                    # VisitSummaryParser.java
@Service                                      # VisitSummaryService.java
@Async("visitSummaryExecutor")               # VisitSummaryService.java
Optional<Visit> findById(Integer id);       # VisitRepository.java
Visit save(Visit visit);                    # VisitRepository.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "pet_id", insertable = false, updatable = false)  # Visit.java
follow_up                                      # VisitPromptBuilder.java SYSTEM_PROMPT
public class VisitSummaryParseException extends RuntimeException {   # exception class
```

## Completion notes

AC-16.a (full `./mvnw test`): satisfied — `BUILD SUCCESS`, 0 failures, 0 errors.
AC-16.b: JaCoCo shows full line coverage for `VisitSummaryParser` and `VisitSummaryService` in the aggregated CSV excerpt above.
