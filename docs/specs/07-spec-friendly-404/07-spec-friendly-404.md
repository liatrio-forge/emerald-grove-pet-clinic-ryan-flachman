---
status: in_progress
created: 2026-05-06
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Friendly 404 Pages for Missing Resources (07)

## Goal

When a staff member navigates to an owner, pet, or visit URL that references a
non-existent record, the application throws an `IllegalArgumentException` that
Spring maps to a 500 response — surfacing a raw exception view with internal
details. This spec replaces that path with a proper HTTP 404 and a branded
"not found" page that gives users a clear message and a link back to Find
Owners, without exposing any stack traces or exception text.

## Scope

### In scope

- New `ResourceNotFoundException` in the `system/` package, annotated
  `@ResponseStatus(HttpStatus.NOT_FOUND)`, used as the single signal for all
  missing-resource paths
- `OwnerController.findOwner` — replace `IllegalArgumentException` with
  `ResourceNotFoundException`
- `PetController.findOwner` — replace `IllegalArgumentException` with
  `ResourceNotFoundException`
- `PetController.findPet` — add a null guard throwing `ResourceNotFoundException`
  when `owner.getPet(petId)` returns `null` (currently unguarded)
- `VisitController` — replace `IllegalArgumentException` throws for missing
  owner and missing pet with `ResourceNotFoundException`
- New `src/main/resources/templates/error/404.html` — Spring Boot
  auto-resolved 404 page with a generic not-found message and a link to
  `/owners` (Find Owners)
- `src/main/resources/templates/error.html` — remove the raw exception-message
  display for all status codes
- `OwnerControllerTests` — new test `testShowOwnerNotFound` asserting
  `status().isNotFound()` for a request with an unknown owner ID (RED before
  implementation)
- `PetControllerTests` — new test asserting `status().isNotFound()` for a
  request with an unknown pet ID (RED before implementation)
- `VisitControllerTests` — new test asserting `status().isNotFound()` for a
  request with an unknown owner or pet ID (RED before implementation)
- Playwright E2E test in `owner-management.spec.ts` asserting the 404 page
  renders a friendly message and a Find Owners link when navigating to a
  non-existent owner URL

### Out of scope

- Custom 500 or other status-specific error pages beyond 404
- Any change to the JSON error response shape (`/owners.json`)
- Duplicate-owner or validation-error flows (covered by spec 06)
- Visit-specific Playwright test (JUnit coverage is sufficient given the shared
  exception mechanism)
- Internationalisation of the 404 page message
- Any database migration or schema change

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java`
  — `findOwner` `@ModelAttribute` method (lines ~68–73): calls
  `owners.findById(ownerId).orElseThrow(() -> new IllegalArgumentException(...))`;
  the `IllegalArgumentException` is replaced by `ResourceNotFoundException`
- `src/main/java/org/springframework/samples/petclinic/owner/PetController.java`
  — `findOwner` (lines ~67–71): same pattern; `findPet` (lines ~74–86): calls
  `owner.getPet(petId)` with no null guard — null guard added here
- `src/main/java/org/springframework/samples/petclinic/system/CrashController.java`
  — reference for `system/` package placement of cross-cutting concerns
- `src/main/resources/templates/error.html` — existing template with a
  `th:switch` on `${status}` and an exception-message paragraph in muted text;
  the muted-text paragraph is removed by this spec
- `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java`
  — existing controller test; new `testShowOwnerNotFound` appended
- `e2e-tests/tests/features/owner-management.spec.ts` — existing E2E suite;
  new 404 test appended

## Acceptance criteria

- **AC-1: `ResourceNotFoundException` class exists and is correctly annotated**
  - AC-1.a: `src/main/java/org/springframework/samples/petclinic/system/ResourceNotFoundException.java`
    exists — verified by
    `find src/main/java -name "ResourceNotFoundException.java"` returning one
    match.
  - AC-1.b: The class carries `@ResponseStatus(HttpStatus.NOT_FOUND)` —
    verified by
    `grep -n "@ResponseStatus" src/main/java/org/springframework/samples/petclinic/system/ResourceNotFoundException.java`
    returning a match.

- **AC-2: All three controllers use `ResourceNotFoundException` for missing resources**
  - AC-2.a: `OwnerController` references `ResourceNotFoundException` — verified
    by
    `grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java`
    returning at least one match.
  - AC-2.b: `PetController` references `ResourceNotFoundException` — verified
    by the same grep on `PetController.java` returning at least one match.
  - AC-2.c: `VisitController` references `ResourceNotFoundException` — verified
    by the same grep on `VisitController.java` returning at least one match.
  - AC-2.d: No `IllegalArgumentException` is thrown for missing-resource paths
    in the owner package — verified by
    `grep -rn "throw new IllegalArgumentException" src/main/java/org/springframework/samples/petclinic/owner/`
    returning no matches.

- **AC-3: Dedicated `error/404.html` template is present and correct**
  - AC-3.a: `src/main/resources/templates/error/404.html` exists — verified by
    `find src/main/resources/templates/error -name "404.html"` returning one
    match.
  - AC-3.b: The template contains a link to `/owners` — verified by
    `grep -n 'href.*\/owners\|th:href.*owners' src/main/resources/templates/error/404.html`
    returning at least one match.
  - AC-3.c: The template contains a human-readable not-found message — verified
    by
    `grep -in "not found\|could not be found" src/main/resources/templates/error/404.html`
    returning at least one match.

- **AC-4: `error.html` no longer exposes exception/message text**
  - AC-4.a: `error.html` contains no Thymeleaf expression that renders
    `${message}`, `${error}`, `${exception}`, or `${trace}` as page content —
    verified by
    `grep -n "th:text.*\${message}\|th:text.*\${error}\|th:utext.*\${" src/main/resources/templates/error.html`
    returning no matches.

- **AC-5: OwnerController returns HTTP 404 for missing owner (JUnit)**
  - AC-5.a: `OwnerControllerTests` contains a test named
    `testShowOwnerNotFound` that stubs `owners.findById` to return
    `Optional.empty()` and asserts `status().isNotFound()` — verified by
    `./mvnw test -Dtest=OwnerControllerTests` exiting 0.

- **AC-6: PetController returns HTTP 404 for missing pet (JUnit)**
  - AC-6.a: `PetControllerTests` contains a test that requests an edit URL for
    a pet ID not present on the owner and asserts `status().isNotFound()` —
    verified by `./mvnw test -Dtest=PetControllerTests` exiting 0.

- **AC-7: VisitController returns HTTP 404 for missing resources (JUnit)**
  - AC-7.a: `VisitControllerTests` contains a test that requests a visit form
    with a non-existent owner or pet ID and asserts `status().isNotFound()` —
    verified by `./mvnw test -Dtest=VisitControllerTests` exiting 0.

- **AC-8: Playwright E2E verifies friendly 404 page**
  - AC-8.a: `owner-management.spec.ts` contains a test that navigates to
    `/owners/99999` and asserts a visible not-found message on the page —
    verified by
    `grep -n "99999\|not.found\|notFound\|404" e2e-tests/tests/features/owner-management.spec.ts`
    returning at least one match.
  - AC-8.b: The same Playwright test asserts a "Find Owners" link is visible on
    the resulting page.
  - AC-8.c: `cd e2e-tests && npm test -- --grep "Owner Management"` exits 0
    with all assertions passing, including the new 404 test.

- **AC-9: No regressions**
  - AC-9.a: `./mvnw test` exits 0 — all existing and new Java tests pass.
  - AC-9.b: `./mvnw test jacoco:report` produces a JaCoCo report showing ≥90%
    line coverage on `ResourceNotFoundException` and the modified controller
    methods.

## Conventions

- `ResourceNotFoundException` extends `RuntimeException` and is placed in
  `org.springframework.samples.petclinic.system`. It carries no fields beyond
  the message passed to `super(message)`. No `@ControllerAdvice` is introduced
  — `@ResponseStatus` on the exception class is sufficient.
- Exception messages passed to `ResourceNotFoundException` are **not**
  user-facing (they appear in server logs only). The 404 page message is fixed
  in `error/404.html` and is generic — it does not reference any ID or field.
- `error/404.html` follows the same Thymeleaf layout fragment pattern used by
  all other templates in this project (`layout:decorate="~{fragments/layout}"`).
- The null guard added to `PetController.findPet` must throw
  `ResourceNotFoundException`, not return `null` or a new `Pet()`.
- TDD is mandatory: all JUnit test additions (AC-5, AC-6, AC-7) are committed
  in their failing RED state before `ResourceNotFoundException` and the
  controller changes are committed.
- `error.html` is modified only to remove the exception-message paragraph.
  The existing `th:switch` status logic and layout structure are preserved.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
