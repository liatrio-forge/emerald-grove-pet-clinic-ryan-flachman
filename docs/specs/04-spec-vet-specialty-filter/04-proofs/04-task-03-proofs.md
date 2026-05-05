# Proofs: Task 03 — Add VetRepository query methods and update VetController (GREEN)

Covers: AC-2.a, AC-2.b, AC-2.c

## Planned evidence

- `VetRepository.java` diff showing `findBySpecialtyName`, `findWithNoSpecialties`, and `findAllSpecialties` methods.
- `VetController.java` diff showing the new `specialty` param, branching logic in `findPaginated`, and `allSpecialties` / `selectedSpecialty` model attributes.
- Output of `./mvnw test -Dtest=VetControllerTests` showing new specialty filter tests now **passing** (GREEN for unit tests).

## Completion notes

### AC-2.a: `GET /vets.html?specialty=radiology` returns HTTP 200, model attribute `selectedSpecialty` equals `"radiology"`, and `listVets` contains only radiology vets

**VetRepository additions:**

```java
@Transactional(readOnly = true)
@Query(value = "SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.name = :name",
        countQuery = "SELECT COUNT(DISTINCT v) FROM Vet v JOIN v.specialties s WHERE s.name = :name")
Page<Vet> findBySpecialtyName(String name, Pageable pageable);

@Transactional(readOnly = true)
@Query("SELECT DISTINCT s FROM Vet v JOIN v.specialties s ORDER BY s.name")
List<Specialty> findAllSpecialties();
```

**VetController updated `showVetList`:**

```java
@GetMapping("/vets.html")
public String showVetList(@RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String specialty, Model model) {
    Page<Vet> paginated = findPaginated(page, specialty);
    model.addAttribute("allSpecialties", vetRepository.findAllSpecialties());
    if (specialty != null) {
        model.addAttribute("selectedSpecialty", specialty);
    }
    return addPaginationModel(page, paginated, specialty, model);
}
```

**`findPaginated` routing:**

```java
private Page<Vet> findPaginated(int page, String specialty) {
    int pageSize = 5;
    Pageable pageable = PageRequest.of(page - 1, pageSize);
    if ("none".equalsIgnoreCase(specialty)) {
        return vetRepository.findWithNoSpecialties(pageable);
    }
    if (specialty != null && !specialty.isBlank()) {
        return vetRepository.findBySpecialtyName(specialty, pageable);
    }
    return vetRepository.findAll(pageable);
}
```

Test `testShowVetListFilteredBySpecialty` now **passes** with mock stub:
`given(this.vets.findBySpecialtyName(eq("radiology"), any(Pageable.class))).willReturn(new PageImpl<>(List.of(helen())))`

### AC-2.b: `GET /vets.html?specialty=none` returns HTTP 200, model attribute `selectedSpecialty` equals `"none"`, and `listVets` contains only no-specialty vets

**VetRepository addition:**

```java
@Transactional(readOnly = true)
@Query("SELECT v FROM Vet v WHERE v.specialties IS EMPTY")
Page<Vet> findWithNoSpecialties(Pageable pageable);
```

Test `testShowVetListFilteredByNone` now **passes** with mock stub:
`given(this.vets.findWithNoSpecialties(any(Pageable.class))).willReturn(new PageImpl<>(List.of(james())))`

### AC-2.c: No `specialty` param → all vets returned (existing behaviour)

`VetControllerTests.@BeforeEach` extended with `findAllSpecialties()` stub:
`given(this.vets.findAllSpecialties()).willReturn(List.of(radiology()))`

Test `testShowVetListNoFilterExposesAllSpecialtiesInModel` now **passes**.
Test `testShowVetListHtml` continues to **pass** (existing behaviour unchanged).

### Maven GREEN output

```text
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.023 s -- in org.springframework.samples.petclinic.vet.VetControllerTests

[INFO] Results:

[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0

[INFO] BUILD SUCCESS
[INFO] Total time:  9.353 s
```

All 5 tests pass. GREEN phase confirmed for controller unit tests.
