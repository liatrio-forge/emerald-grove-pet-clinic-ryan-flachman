# Validation: Find Owners Multi-Field Search (05)

## Automated verification

From repository root:

```bash
# AC-6.a — full Java test suite
./mvnw test

# AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-4.a — controller unit tests
./mvnw test -Dtest=OwnerControllerTests

# AC-6.b — coverage report (open target/site/jacoco/index.html to verify ≥90% on OwnerController and OwnerRepository)
./mvnw test jacoco:report

# AC-1.a — confirm #city and #telephone inputs exist in rendered HTML
./mvnw spring-boot:run &
sleep 10
curl -s http://localhost:8080/owners/find | grep -E 'id="(city|telephone)"'
kill %1

# AC-5.a — confirm "can find owner by telephone" test exists
grep -n '"can find owner by telephone"' e2e-tests/tests/features/owner-management.spec.ts

# AC-5.b — confirm "can find owner by city" test exists
grep -n '"can find owner by city"' e2e-tests/tests/features/owner-management.spec.ts

# AC-5.c — E2E Owner Management tests
cd e2e-tests && npm test -- --grep "Owner Management"
```

**Expected:**

- `./mvnw test` exits 0; `BUILD SUCCESS`; all OwnerControllerTests pass including
  the new `testProcessFindFormByCity`, `testProcessFindFormByTelephone`,
  `testProcessFindFormByCombinedCriteria`, `testProcessFindFormInvalidTelephone`,
  and `testProcessFindFormValidPartialTelephone` tests.
- `curl ... | grep -E 'id="(city|telephone)"'` prints two matching lines.
- Both `grep` commands print at least one match.
- `npm test -- --grep "Owner Management"` exits 0; all tests including
  `"can find owner by telephone"` and `"can find owner by city"` pass.

## Traceability

- Feature spec: `05-spec-owner-search-filters.md`
- Task breakdown: `05-tasks-owner-search-filters.md`
- Questions and decisions: `05-questions-1-owner-search-filters.md`
- Per-task evidence: `05-proofs/05-task-NN-proofs.md`
- Upstream specs: none
- Parent epic: none

## Manual checks

1. Start the app: `./mvnw spring-boot:run`
2. Navigate to `http://localhost:8080/owners/find` — confirm City and Telephone
   inputs appear below Last name.
3. Submit the empty form — confirm all owners are listed (no error).
4. Enter city `"Madison"` — confirm only Madison owners appear.
5. Enter telephone `"608"` — confirm only owners with 608-prefix telephone appear.
6. Enter lastName `"Davis"` and city `"Sun"` — confirm only Betty Davis appears,
   not Harold Davis.
7. Enter telephone `"608-555"` — confirm an inline validation error appears on
   the Telephone field.
8. Enter criteria that match nothing — confirm a global error message appears at
   the top of the form (not inline under Last name).

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `GET /owners/find` HTML contains `<input id="city">` and `<input id="telephone">` | `05-proofs/05-task-04-proofs.md` | file edit | PASS |
| AC-1.b | `GET /owners` (no params) returns HTTP 200, view `owners/ownersList` | `05-proofs/05-task-03-proofs.md` | Maven test pass | PASS |
| AC-2.a | `?city=Madison` → `listOwners` contains only Madison owners | `05-proofs/05-task-03-proofs.md` | Maven test pass | PASS |
| AC-2.b | `?telephone=6085551` → `listOwners` contains only matching owners | `05-proofs/05-task-03-proofs.md` | Maven test pass | PASS |
| AC-2.c | `?lastName=Davis&city=Sun` → Betty Davis only, not Harold Davis | `05-proofs/05-task-03-proofs.md` | Maven test pass | PASS |
| AC-2.d | `?lastName=Franklin` (single result) → redirect to `/owners/{id}` | `05-proofs/05-task-03-proofs.md` | Maven test pass | PASS |
| AC-3.a | `?telephone=608-555` → HTTP 200, field error `"invalid"` on `telephone`, view `findOwners` | `05-proofs/05-task-03-proofs.md` | Maven test pass | PASS |
| AC-3.b | `?telephone=608` (digits only) → no field error on `telephone` | `05-proofs/05-task-03-proofs.md` | Maven test pass | PASS |
| AC-4.a | No results → global error `"notFound"`, no field error on `lastName`, view `findOwners` | `05-proofs/05-task-03-proofs.md` | Maven test pass | PASS |
| AC-5.a | `owner-management.spec.ts` contains `"can find owner by telephone"` | `05-proofs/05-task-02-proofs.md` | file creation | PASS |
| AC-5.b | `owner-management.spec.ts` contains `"can find owner by city"` | `05-proofs/05-task-02-proofs.md` | file creation | PASS |
| AC-5.c | `npm test -- --grep "Owner Management"` exits 0 | `05-proofs/05-task-05-proofs.md` | command output | PASS |
| AC-5.d | Playwright screenshot of filtered results captured | `05-proofs/05-task-05-proofs.md` | Playwright screenshot | PASS |
| AC-6.a | `./mvnw test` exits 0 | `05-proofs/05-task-05-proofs.md` | Maven test pass | PASS |
| AC-6.b | JaCoCo ≥90% line coverage on `OwnerController` and `OwnerRepository` | `05-proofs/05-task-05-proofs.md` | JaCoCo coverage report | PASS |

## Definition of done

- [x] AC-1.a: `GET /owners/find` HTML contains `<input id="city">` and `<input id="telephone">`
- [x] AC-1.b: `GET /owners` (no params) returns HTTP 200, view `owners/ownersList`
- [x] AC-2.a: `?city=Madison` → `listOwners` contains only Madison owners
- [x] AC-2.b: `?telephone=6085551` → `listOwners` contains only matching owners
- [x] AC-2.c: `?lastName=Davis&city=Sun` → Betty Davis only, not Harold Davis
- [x] AC-2.d: `?lastName=Franklin` (single result) → redirect to `/owners/{id}`
- [x] AC-3.a: `?telephone=608-555` → HTTP 200, field error `"invalid"` on `telephone`, view `findOwners`
- [x] AC-3.b: `?telephone=608` (digits only) → no field error on `telephone`
- [x] AC-4.a: No results → global error `"notFound"`, no field error on `lastName`, view `findOwners`
- [x] AC-5.a: `owner-management.spec.ts` contains `"can find owner by telephone"`
- [x] AC-5.b: `owner-management.spec.ts` contains `"can find owner by city"`
- [x] AC-5.c: `cd e2e-tests && npm test -- --grep "Owner Management"` exits 0
- [x] AC-5.d: Playwright screenshot of filtered results captured as proof artifact
- [x] AC-6.a: `./mvnw test` exits 0
- [x] AC-6.b: JaCoCo ≥90% line coverage on `OwnerController` and `OwnerRepository`
- [x] All proof artifacts contain real outputs, not placeholders.
- [x] Coverage matrix has all rows in `PASS`.
- [x] `./mvnw test` exits 0 with ≥90% line coverage on new code.
