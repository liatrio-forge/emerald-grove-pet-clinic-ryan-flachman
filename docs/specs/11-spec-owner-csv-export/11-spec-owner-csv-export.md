---
status: in_progress
created: 2026-05-06
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: Owner CSV Export (11)

## Goal

Add a `GET /owners.csv` endpoint so clinic staff can export owner search results as a
downloadable CSV file. This removes the need to manually copy-paste from the HTML list
view when preparing contact lists or mail-merge inputs.

## Scope

### In scope

- New `GET /owners.csv` handler in `OwnerController`
- Accepts the same `lastName`, `telephone`, and `city` query parameters as `GET /owners`
- Returns **all** matching owners in one response (no pagination)
- `Content-Type: text/csv` response header
- `Content-Disposition: attachment; filename="owners.csv"` response header
- CSV columns (in order): `First Name`, `Last Name`, `Address`, `City`, `Telephone`
- Header row always present; a no-match query returns the header row only (HTTP 200)
- `@WebMvcTest` coverage for the new handler

### Out of scope

- Authentication or authorisation on the endpoint
- Additional CSV columns beyond the five specified
- UI download button or any browser-side integration
- Playwright E2E test
- RFC 4180 special-character escaping (commas/quotes in field values are not expected
  in trusted internal data; a follow-on amendment must add escaping if that assumption
  ever breaks)

## Source excerpts

- `OwnerController.processFindForm` — existing search parameter handling (`nullIfBlank`,
  `findBySearchCriteria`) to mirror exactly.
- `OwnerRepository.findBySearchCriteria` — existing JPQL query supporting `lastName`,
  `telephone`, `city` with AND logic. Call with `Pageable.unpaged()` to retrieve all
  results; no new repository method is required.

## Acceptance criteria

- **AC-1: Endpoint availability and response headers**
  - AC-1.a: `GET /owners.csv` (no query parameters) returns HTTP 200.
  - AC-1.b: The response `Content-Type` contains `text/csv`.
  - AC-1.c: The response includes `Content-Disposition: attachment; filename="owners.csv"`.

- **AC-2: CSV structure**
  - AC-2.a: The first line of the response body is exactly
    `First Name,Last Name,Address,City,Telephone`.
  - AC-2.b: Each data row contains five unquoted comma-separated fields in the order
    firstName, lastName, address, city, telephone.

- **AC-3: Search parameter filtering**
  - AC-3.a: `?lastName=X` returns only owners whose last name prefix-matches X
    (case-insensitive).
  - AC-3.b: `?telephone=X` returns only owners whose telephone starts with X (digits).
  - AC-3.c: `?city=X` returns only owners whose city prefix-matches X (case-insensitive).
  - AC-3.d: Multiple parameters combine with AND logic (identical to `GET /owners`).

- **AC-4: Full result export**
  - AC-4.a: All matching owners appear in a single response with no page/size parameters
    required.

- **AC-5: Empty result handling**
  - AC-5.a: A query that matches no owners returns HTTP 200 with the header row only
    (no error body, no redirect).

- **AC-6: CLI proof**
  - AC-6.a: Proof doc contains `curl -i` output showing `Content-Type: text/csv` and
    `Content-Disposition: attachment; filename="owners.csv"` response headers.
  - AC-6.b: Proof doc contains curl output showing the CSV header line and at least one
    data row.

## Conventions

- Mirror `nullIfBlank` and `findBySearchCriteria` from `processFindForm` — do not
  introduce a parallel search path.
- Call `owners.findBySearchCriteria(lastName, telephone, city, Pageable.unpaged())`
  to retrieve all results; avoid adding a new repository method.
- Build CSV output using `StringBuilder` or `String.join` — no CSV library is needed
  for this initial implementation.
- Line endings: LF (`\n`) only.
- Return `ResponseEntity<String>` with explicit `Content-Type` and `Content-Disposition`
  headers, or write directly to `HttpServletResponse` — either approach is acceptable.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
