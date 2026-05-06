---
name: 11-validation-preserve-page-filters
description: Validation plan for spec 11 — preserve page filters
type: project
---

# Validation: Preserve Page Filters Across Pagination (11)

## Automated verification

From repository root:

```bash
# Compile (must exit 0 — catches signature change regressions)
./mvnw compile

# Unit + integration tests (must exit 0)
./mvnw test

# Coverage report — open target/site/jacoco/index.html and verify OwnerController ≥ 90%
./mvnw test jacoco:report

# E2E tests (from e2e-tests/ directory)
cd e2e-tests && npm test
```

**Expected:**

- `./mvnw compile` — BUILD SUCCESS, no errors
- `./mvnw test` — BUILD SUCCESS, all tests pass (0 failures, 0 errors)
- JaCoCo report — `OwnerController` line coverage ≥ 90%
- `npm test` — all Playwright specs pass including the new `"preserves lastName filter when navigating to next page"` test

## Traceability

- Feature spec: `11-spec-preserve-page-filters.md`
- Task breakdown: `11-tasks-preserve-page-filters.md`
- Questions and decisions: `11-questions-1-preserve-page-filters.md`
- Per-task evidence: `11-proofs/11-task-NN-proofs.md`
- Related specs: `05-spec-owner-search-filters` (delivered — introduced the multi-field filter params this spec threads through pagination)

## Manual checks

- In a running app (`./mvnw spring-boot:run`), navigate to `/owners?lastName=F&page=1` and verify:
  - Only owners whose last name starts with "F" appear
  - Pagination links in the browser all include `lastName=F` in their `href` (inspect via browser dev tools)
  - Clicking next/previous keeps the filter active

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `GET /owners?lastName=Davis` → model has `filterLastName=Davis`, `filterTelephone=null`, `filterCity=null` | `11-proofs/11-task-01-proofs.md` | Maven test pass | PENDING |
| AC-1.b | `GET /owners?telephone=608` → model has `filterTelephone=608`, others null | `11-proofs/11-task-01-proofs.md` | Maven test pass | PENDING |
| AC-1.c | `GET /owners?lastName=D&telephone=6&city=M` → all three filter model attributes set | `11-proofs/11-task-01-proofs.md` | Maven test pass | PENDING |
| AC-2.a | Rendered HTML with active `lastName=Davis` contains hrefs matching `/owners?page=\d+&lastName=Davis` | `11-proofs/11-task-03-proofs.md` | Maven test pass | PENDING |
| AC-2.b | No-filter pagination hrefs contain only `page=N` — no empty filter params | `11-proofs/11-task-03-proofs.md` | Maven test pass | PENDING |
| AC-3.a | Playwright: click next-page with `lastName=F` active → URL contains `lastName=F` | `11-proofs/11-task-04-proofs.md` | Playwright screenshot | PENDING |
| AC-3.b | Playwright: second page shows only owners whose name starts with "F" | `11-proofs/11-task-04-proofs.md` | behavioral evidence | PENDING |
| AC-4.a | `./mvnw test` exits 0 with all pre-existing `OwnerControllerTests` cases passing | `11-proofs/11-task-05-proofs.md` | command output | PENDING |
| AC-5.a | JaCoCo: `OwnerController` line coverage ≥ 90% | `11-proofs/11-task-05-proofs.md` | JaCoCo coverage report | PENDING |

## Definition of done

- [ ] AC-1.a: `GET /owners?lastName=Davis` → model has `filterLastName=Davis`, `filterTelephone=null`, `filterCity=null`
- [ ] AC-1.b: `GET /owners?telephone=608` → model has `filterTelephone=608`, others null
- [ ] AC-1.c: `GET /owners?lastName=D&telephone=6&city=M` → all three filter model attributes set
- [ ] AC-2.a: Rendered HTML with active `lastName=Davis` contains hrefs with `lastName=Davis`
- [ ] AC-2.b: No-filter pagination hrefs contain only `page=N`
- [ ] AC-3.a: Playwright: clicking next-page preserves `lastName=F` in URL
- [ ] AC-3.b: Playwright: filtered second page shows only owners starting with "F"
- [ ] AC-4.a: `./mvnw test` exits 0 with all pre-existing tests passing
- [ ] AC-5.a: `OwnerController` line coverage ≥ 90%
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥ 90% line coverage on new code.
