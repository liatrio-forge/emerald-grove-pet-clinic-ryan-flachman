---
status: delivered
created: 2026-05-05
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Prevent Duplicate Owner Creation (06)

## Goal

The "Add Owner" form creates a new record unconditionally on every valid
submission. Two staff members can accidentally register the same pet owner
twice, polluting the database with duplicate records that break search
results and visit history. This spec adds a case-insensitive duplicate check
on `firstName + lastName + telephone` inside a new `OwnerService`. When a
match is found the form is re-displayed with the submitted data intact and a
global banner error is shown. No duplicate record is persisted.

## Scope

### In scope

- New `OwnerService` (`@Service`) with method
  `boolean isDuplicate(String firstName, String lastName, String telephone)`
- New `OwnerRepository` Spring Data derived method
  `boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone(String, String, String)`
- `OwnerController.processCreationForm` updated to call
  `ownerService.isDuplicate(...)` before `owners.save()`; on detection emit a
  global `"duplicate"` error and return the form view
- `createOrUpdateOwnerForm.html` updated to render a global error block when
  global errors are present
- New `OwnerServiceTests` — unit tests for `isDuplicate` returning `true` and `false`
- `OwnerControllerTests` extended with `testProcessCreationFormDuplicateRejected()`
  covering the duplicate path
- New Playwright E2E test `"blocks duplicate owner creation"` in
  `e2e-tests/tests/features/owner-management.spec.ts`

### Out of scope

- Database-level unique constraint — deferred follow-up
- Duplicate detection on the owner edit / update form
- REST API (`/owners.json`) — contract must not change
- Deduplication UI for existing records already in the database
- Detection using any field other than `firstName`, `lastName`, `telephone`
- Merging or redirecting to an existing owner's page on duplicate

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java`
  — `processCreationForm` handler; the point of injection for the duplicate
  check (after `@Valid` / `BindingResult` check, before `owners.save()`)
- `src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java`
  — receives the new `existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone`
  derived method; all other methods remain unchanged
- `src/main/java/org/springframework/samples/petclinic/owner/Owner.java`
  — `firstName` (inherited from `Person`), `lastName` (inherited from `Person`),
  `telephone` fields; `@Pattern(regexp="\\d{10}")` on `telephone` already
  enforced by `@Valid` on the creation form, so any telephone reaching the
  duplicate check is already 10 digits
- `src/main/resources/templates/owners/createOrUpdateOwnerForm.html`
  — currently has no global error block; needs one added before the field inputs
- `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java`
  — existing creation tests; `@MockitoBean OwnerService` added;
  `testProcessCreationFormDuplicateRejected` appended
- `e2e-tests/tests/features/owner-management.spec.ts`
  — existing owner-management suite; new duplicate test appended

## Acceptance criteria

- **AC-1: Repository duplicate-check method present**
  - AC-1.a: `OwnerRepository` declares
    `boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone(String, String, String)` —
    verified by
    `grep -n "existsByFirstNameIgnoreCase" src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java`
    returning at least one match.

- **AC-2: OwnerService duplicate detection**
  - AC-2.a: `OwnerServiceTests` contains a test `testIsDuplicate_returnsTrueWhenMatchExists`
    that stubs the repository to return `true` and asserts
    `ownerService.isDuplicate("George", "Franklin", "6085551023")` returns `true` —
    verified by `./mvnw test -Dtest=OwnerServiceTests` exiting 0.
  - AC-2.b: `OwnerServiceTests` contains a test `testIsDuplicate_returnsFalseWhenNoMatch`
    that stubs the repository to return `false` and asserts
    `ownerService.isDuplicate(...)` returns `false` —
    verified by `./mvnw test -Dtest=OwnerServiceTests` exiting 0.

- **AC-3: Controller blocks duplicate submission**
  - AC-3.a: `OwnerControllerTests.testProcessCreationFormDuplicateRejected` passes —
    `POST /owners/new` with valid data returns HTTP 200, view is
    `owners/createOrUpdateOwnerForm`, and the `owner` `BindingResult` contains
    exactly one global error with code `"duplicate"` when
    `ownerService.isDuplicate(...)` is stubbed to return `true` —
    verified by `./mvnw test -Dtest=OwnerControllerTests` exiting 0.
  - AC-3.b: The same test asserts `owners.save()` is **never** invoked
    (`verify(owners, never()).save(any())`) when a duplicate is detected.
  - AC-3.c: Existing `testProcessCreationFormSuccess` passes with
    `ownerService.isDuplicate(...)` stubbed to return `false`, returning a 3xx
    redirect — all pre-existing controller tests continue to pass.

- **AC-4: Template renders global errors**
  - AC-4.a: `createOrUpdateOwnerForm.html` contains
    `th:if="${#fields.hasGlobalErrors()}"` — verified by
    `grep -n "hasGlobalErrors" src/main/resources/templates/owners/createOrUpdateOwnerForm.html`
    returning at least one match.

- **AC-5: E2E duplicate prevention**
  - AC-5.a: `e2e-tests/tests/features/owner-management.spec.ts` contains a test
    named `"blocks duplicate owner creation"` — verified by
    `grep -n '"blocks duplicate owner creation"' e2e-tests/tests/features/owner-management.spec.ts`
    returning a match.
  - AC-5.b: The Playwright test creates a unique owner, re-navigates to
    `/owners/new`, submits the same `firstName`, `lastName`, and `telephone`,
    and asserts a visible error element on the page (no redirect to an owner
    detail page).
  - AC-5.c: `cd e2e-tests && npm test -- --grep "Owner Management"` exits 0 with
    all assertions passing, including the new duplicate test.

- **AC-6: No regressions**
  - AC-6.a: `./mvnw test` exits 0 — all existing and new Java tests pass.
  - AC-6.b: `./mvnw test jacoco:report` produces a JaCoCo report showing ≥90%
    line coverage on `OwnerService` and `OwnerController`.

## Conventions

- `OwnerService` is annotated `@Service` and uses constructor injection for
  `OwnerRepository`. No `@Autowired` field injection.
- `OwnerController` constructor is updated to accept `OwnerService ownerService`
  alongside the existing `OwnerRepository owners` parameter.
- The duplicate check is called **after** `@Valid` / `BindingResult` guard
  (i.e. only when the form data itself is structurally valid) and **before**
  `owners.save(owner)`.
- The global error code is `"duplicate"` with a user-readable default message:
  `"An owner with this name and telephone already exists."`
- The Spring Data derived method name
  `existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone` is used as-is —
  no `@Query` annotation required; Spring Data JPA resolves it automatically.
- Telephone comparison is exact-match (Spring Data `And` keyword, no `IgnoreCase`
  suffix) because `@Pattern(regexp="\\d{10}")` already normalises the value to
  10 digits on the creation form.
- TDD is mandatory: `OwnerServiceTests` and the controller duplicate test are
  committed in a failing state before `OwnerService` and the controller
  changes are committed.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
