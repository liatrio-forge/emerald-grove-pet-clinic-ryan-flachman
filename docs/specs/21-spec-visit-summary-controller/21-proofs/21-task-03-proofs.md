# Proofs: Task 03 — Validate and capture proof artifacts

Covers: AC-9.a, AC-9.b (all criteria)

## Full test suite (`./mvnw test`)

Runs complete with **exit code 0**, no test failures (BUILD SUCCESS).

## JaCoCo (`./mvnw test jacoco:report`)

Excerpt from `target/site/jacoco/jacoco.csv` (JaCoCo CSV columns include missed/covered lines):

```text
petclinic,org.springframework.samples.petclinic.owner,VisitSummaryResponse,...,0,1,...,0,1
petclinic,org.springframework.samples.petclinic.owner,VisitSummaryController,...,0,16,...,1,7
```

Line coverage: **16/16** lines covered for `VisitSummaryController`, **1/1** for `VisitSummaryResponse` (both ≥ 90%).

## Structural validation greps (from `21-validation-visit-summary-controller.md`)

```text
grep -r "@RestController" .../VisitSummaryController.java   → match @RestController
grep -r "@GetMapping.*visits.*visitId.*summary" ...         → match @GetMapping("/visits/{visitId}/summary")
grep -r "@JsonInclude" .../VisitSummaryResponse.java         → match @JsonInclude
```

## Coverage matrix

All AC rows for spec 21 are satisfied; matrix in `21-validation-visit-summary-controller.md` updated to **PASS**.
