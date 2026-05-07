# Proofs: Task 04 — Wire search criteria to CSV handler

Covers: AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-5.a

## Planned evidence

- `./mvnw test -Dtest=OwnerControllerTests` output showing all Task 03 tests now **PASS**.
- `./mvnw test` output showing full suite passes with no regressions.

## Completion notes

### No-op note

The `@RequestParam` parameters (`lastName`, `telephone`, `city`) and `nullIfBlank`
application were already implemented in Task 02's handler. Task 04 produces no
additional code changes. The evidence for AC-3.a–d and AC-5.a is in the Task 03 proof.

### AC-3.a–3.d and AC-5.a: already satisfied

From `OwnerController.java` (unchanged since Task 02):

```java
@GetMapping(value = "/owners.csv", produces = "text/csv")
public ResponseEntity<String> exportOwnersCsv(
        @RequestParam(required = false, defaultValue = "") String lastName,
        @RequestParam(required = false, defaultValue = "") String telephone,
        @RequestParam(required = false, defaultValue = "") String city) {
    Page<Owner> results = owners.findBySearchCriteria(nullIfBlank(lastName), nullIfBlank(telephone),
            nullIfBlank(city), Pageable.unpaged());
    ...
}
```

All five Task 03 tests (filter by lastName, telephone, city, combined, empty result)
pass as confirmed in Task 03's proof (27 tests, 0 failures).

Full suite re-verification (for completeness):

```text
[WARNING] Tests run: 91, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
```
