# Proofs: Task 03 — Write failing @WebMvcTest for search filtering and empty result

Covers: AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-5.a

## Planned evidence

- `./mvnw test -Dtest=OwnerControllerTests` output showing the five new filter/empty
  test methods failing (parameters not yet wired in the handler).

## Completion notes

### Deviation from plan

Task 02 implemented the full handler including `@RequestParam` parameters and
`nullIfBlank` filtering — work that the spec planned for Task 04. As a result, these
tests went directly to GREEN (no RED phase observed). The AC coverage is not affected:
the tests verify the correct filtering behaviour and all pass.

### AC-3.a: `?lastName=X` returns only owners whose last name prefix-matches X

Test `testCsvFilterByLastName`:

- `GET /owners.csv?lastName=Franklin`
- Mockito `verify` confirms `findBySearchCriteria("Franklin", null, null, any())` called
- Response body contains "Franklin"

```text
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.341 s
        -- in org.springframework.samples.petclinic.owner.OwnerControllerTests
[INFO] BUILD SUCCESS
```

### AC-3.b: `?telephone=X` returns only owners whose telephone starts with X

Test `testCsvFilterByTelephone`: `GET /owners.csv?telephone=608`
Mockito `verify` confirms `findBySearchCriteria(null, "608", null, any())` called. PASS.

### AC-3.c: `?city=X` returns only owners whose city prefix-matches X

Test `testCsvFilterByCity`: `GET /owners.csv?city=Madison`
Mockito `verify` confirms `findBySearchCriteria(null, null, "Madison", any())` called. PASS.

### AC-3.d: Multiple parameters combine with AND logic

Test `testCsvFilterCombined`: `GET /owners.csv?lastName=Franklin&city=Madison`
Mockito `verify` confirms `findBySearchCriteria("Franklin", null, "Madison", any())` called. PASS.

### AC-5.a: No-match query returns HTTP 200 with header row only

Test `testCsvEmptyResultReturnsHeaderOnly`:

- Stub: `findBySearchCriteria("NOMATCH", null, null, any())` returns empty `PageImpl`
- `GET /owners.csv?lastName=NOMATCH` → HTTP 200
- Body equals exactly `"First Name,Last Name,Address,City,Telephone\n"` (header only). PASS.
