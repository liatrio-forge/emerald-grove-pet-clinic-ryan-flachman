# Proofs: Task 07 — Validate and capture proof artifacts

Covers: all

## Planned evidence

- `./mvnw test` full output confirming `BUILD SUCCESS`.
- JaCoCo HTML report excerpt showing line-coverage percentages for `OwnerService` (≥90%) and `OwnerController` (≥90%).
- `cd e2e-tests && npm test -- --grep "Owner Management"` full passing output.
- File path confirmation for `duplicate-owner-error.png` Playwright screenshot.
- Completed coverage matrix (all rows `PASS`) in `06-validation-prevent-duplicate-owner.md`.
- Completed Definition of Done checklist (all boxes ticked) in `06-validation-prevent-duplicate-owner.md`.

## Completion notes

### Verification block

#### `./mvnw test`

```text
[INFO] Tests run: 70, Failures: 0, Errors: 0, Skipped: 0

[INFO] BUILD SUCCESS
[INFO] Total time:  39.660 s
[INFO] Finished at: 2026-05-06T08:17:51-05:00
```

#### `./mvnw test jacoco:report`

```text
[INFO] Tests run: 70, Failures: 0, Errors: 0, Skipped: 0

[INFO] --- jacoco:0.8.14:report (default-cli) @ spring-petclinic ---
[INFO] Loading execution data file .../target/jacoco.exec
[INFO] Analyzed bundle 'petclinic' with 23 classes
[INFO] BUILD SUCCESS
[INFO] Total time:  38.204 s
```

JaCoCo CSV excerpt (`target/site/jacoco/jacoco.csv`):

```text
petclinic,org.springframework.samples.petclinic.owner,OwnerController,14,269,1,21,0,65,3,23,2,13
petclinic,org.springframework.samples.petclinic.owner,OwnerService,0,13,0,0,0,4,0,2,0,2
```

CSV column order: GROUP, PACKAGE, CLASS, INSTRUCTION_MISSED, INSTRUCTION_COVERED, BRANCH_MISSED, BRANCH_COVERED, LINE_MISSED, LINE_COVERED, ...

- **OwnerController**: 0 lines missed / 65 lines covered = **100% line coverage** ✓
- **OwnerService**: 0 lines missed / 4 lines covered = **100% line coverage** ✓

Both exceed the ≥90% threshold required by AC-6.b.

#### `grep` — AC-1.a

```text
$ grep -n "existsByFirstNameIgnoreCase" src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java
83:  boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone(String firstName, String lastName,
```

Match found. AC-1.a satisfied.

#### `grep` — AC-4.a

```text
$ grep -n "hasGlobalErrors" src/main/resources/templates/owners/createOrUpdateOwnerForm.html
9:    <div th:if="${#fields.hasGlobalErrors()}" class="alert alert-danger" role="alert">
```

Match found. AC-4.a satisfied.

#### `grep` — AC-5.a

```text
$ grep -n "blocks duplicate owner creation" e2e-tests/tests/features/owner-management.spec.ts
125:  test('blocks duplicate owner creation', async ({ page }, testInfo) => {
```

Match found at line 125. The spec's verification command used double-quote delimiters
(`grep -n '"blocks duplicate..."'`) but the TypeScript test file uses single-quote string
literals, which is standard TypeScript style. The test clearly exists and is named exactly
`"blocks duplicate owner creation"`. AC-5.a is satisfied.

#### `cd e2e-tests && npm test -- --grep "Owner Management"`

```text
Running 7 tests using 7 workers
  7 passed (9.0s)
```

All 7 Owner Management tests pass including `"blocks duplicate owner creation"`. AC-5.c satisfied.

#### Screenshot confirmation

```text
e2e-tests/test-results/artifacts/features-owner-management--a2d98-ks-duplicate-owner-creation-chromium/duplicate-owner-error.png
```

Screenshot was written during the passing test run. AC-5.b confirmed.

### Coverage matrix

| AC ID | Status |
|-------|--------|
| AC-1.a | PASS |
| AC-2.a | PASS |
| AC-2.b | PASS |
| AC-3.a | PASS |
| AC-3.b | PASS |
| AC-3.c | PASS |
| AC-4.a | PASS |
| AC-5.a | PASS |
| AC-5.b | PASS |
| AC-5.c | PASS |
| AC-6.a | PASS |
| AC-6.b | PASS |

All 12 active AC rows are PASS at the moment of delivery.

### Definition of done

All items verified against proof artifacts:

- [x] AC-1.a — Task 01 proof, repository method grep returns match
- [x] AC-2.a — Task 04 proof, `./mvnw test -Dtest=OwnerServiceTests` exits 0
- [x] AC-2.b — Task 04 proof, `./mvnw test -Dtest=OwnerServiceTests` exits 0
- [x] AC-3.a — Task 05 proof, `./mvnw test -Dtest=OwnerControllerTests` exits 0 with 19 tests
- [x] AC-3.b — Task 05 proof, controller test verifies `never().save(any())`
- [x] AC-3.c — Task 05 proof, all 19 controller tests pass including pre-existing ones
- [x] AC-4.a — Task 06 proof, template grep returns match at line 9
- [x] AC-5.a — Task 03 proof + Task 07 grep at line 125
- [x] AC-5.b — Task 06 proof, screenshot path confirmed
- [x] AC-5.c — Task 06 + Task 07 proof, 7 tests pass
- [x] AC-6.a — Task 06 + Task 07 proof, `./mvnw test` exits 0 with 70 tests
- [x] AC-6.b — Task 07 proof, JaCoCo: OwnerController 100%, OwnerService 100%
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in PASS.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
