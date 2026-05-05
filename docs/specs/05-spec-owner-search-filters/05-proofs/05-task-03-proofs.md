# Proofs: Task 03 — Implement repository query and update controller (GREEN)

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-3.a, AC-3.b, AC-4.a

## Planned evidence

- `OwnerRepository.findBySearchCriteria` with final `@Query` and `countQuery`
  — show the complete method annotation.
- Updated `OwnerController.processFindForm` and new `findPaginatedForOwners`
  private helper — show the relevant code changes.
- Output of `./mvnw test -Dtest=OwnerControllerTests` showing all controller
  tests **passing** (GREEN for Java).

## Completion notes

### AC-2.a / AC-2.b / AC-2.c / AC-2.d / AC-3.a / AC-3.b / AC-4.a: GREEN phase

`OwnerRepository.findBySearchCriteria` — complete method with `@Query` and `countQuery`:

```java
@Query(value = "SELECT DISTINCT o FROM Owner o WHERE "
        + "(:lastName IS NULL OR LOWER(o.lastName) LIKE LOWER(CONCAT(:lastName, '%'))) AND "
        + "(:telephone IS NULL OR o.telephone LIKE CONCAT(:telephone, '%')) AND "
        + "(:city IS NULL OR LOWER(o.city) LIKE LOWER(CONCAT(:city, '%')))",
        countQuery = "SELECT COUNT(DISTINCT o) FROM Owner o WHERE "
                + "(:lastName IS NULL OR LOWER(o.lastName) LIKE LOWER(CONCAT(:lastName, '%'))) AND "
                + "(:telephone IS NULL OR o.telephone LIKE CONCAT(:telephone, '%')) AND "
                + "(:city IS NULL OR LOWER(o.city) LIKE LOWER(CONCAT(:city, '%')))")
Page<Owner> findBySearchCriteria(@Param("lastName") String lastName, @Param("telephone") String telephone,
        @Param("city") String city, Pageable pageable);
```

`OwnerController.processFindForm` — updated method routing through `findBySearchCriteria`:

```java
@GetMapping("/owners")
public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
        Model model) {
    String lastName = nullIfBlank(owner.getLastName());
    String telephone = nullIfBlank(owner.getTelephone());
    String city = nullIfBlank(owner.getCity());

    if (telephone != null && !telephone.matches("\\d+")) {
        result.rejectValue("telephone", "invalid", "Telephone must contain digits only");
        return "owners/findOwners";
    }

    Page<Owner> ownersResults = findPaginatedForOwners(page, lastName, telephone, city);
    if (ownersResults.isEmpty()) {
        result.reject("notFound", "not found");
        return "owners/findOwners";
    }

    if (ownersResults.getTotalElements() == 1) {
        owner = ownersResults.iterator().next();
        return "redirect:/owners/" + owner.getId();
    }

    return addPaginationModel(page, model, ownersResults);
}

private static String nullIfBlank(String value) {
    return (value == null || value.isBlank()) ? null : value;
}

private Page<Owner> findPaginatedForOwners(int page, String lastName, String telephone, String city) {
    int pageSize = 5;
    Pageable pageable = PageRequest.of(page - 1, pageSize);
    return owners.findBySearchCriteria(lastName, telephone, city, pageable);
}
```

`./mvnw test -Dtest=OwnerControllerTests` output (GREEN):

```text
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.706 s -- in org.springframework.samples.petclinic.owner.OwnerControllerTests
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

All 18 tests pass. GREEN phase confirmed for controller and repository.
