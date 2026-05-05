# Proofs: Task 01 — Write failing VetControllerTests for specialty filter param (RED)

Covers: AC-2.a, AC-2.b, AC-2.c

## Planned evidence

- New test methods added to `VetControllerTests.java` for `?specialty=radiology`, `?specialty=none`, and unfiltered requests.
- Output of `./mvnw test -Dtest=VetControllerTests` showing the new tests **failing** (RED phase confirmed).

## Completion notes

### AC-2.a: `GET /vets.html?specialty=radiology` returns HTTP 200, model attribute `selectedSpecialty` equals `"radiology"`, and `listVets` contains only radiology vets

Test `testShowVetListFilteredBySpecialty` written in `VetControllerTests.java` asserting:

- `model().attribute("selectedSpecialty", is("radiology"))`
- `model().attributeExists("allSpecialties")`
- `model().attribute("listVets", hasSize(1))`
- `model().attribute("listVets", hasItem(hasProperty("lastName", is("Leary"))))`

**RED failure output:**

```text
[ERROR] VetControllerTests.testShowVetListFilteredBySpecialty:97 Model attribute 'selectedSpecialty'
Expected: is "radiology"
     but: was null
```

### AC-2.b: `GET /vets.html?specialty=none` returns HTTP 200, model attribute `selectedSpecialty` equals `"none"`, and `listVets` contains only no-specialty vets

Test `testShowVetListFilteredByNone` written asserting:

- `model().attribute("selectedSpecialty", is("none"))`
- `model().attributeExists("allSpecialties")`
- `model().attribute("listVets", hasSize(1))`
- `model().attribute("listVets", hasItem(hasProperty("lastName", is("Carter"))))`

**RED failure output:**

```text
[ERROR] VetControllerTests.testShowVetListFilteredByNone:108 Model attribute 'selectedSpecialty'
Expected: is "none"
     but: was null
```

### AC-2.c: No `specialty` param → all vets returned (existing behaviour)

Test `testShowVetListNoFilterExposesAllSpecialtiesInModel` written asserting:

- `model().attributeExists("allSpecialties")`
- `model().attributeDoesNotExist("selectedSpecialty")`

**RED failure output:**

```text
[ERROR] VetControllerTests.testShowVetListNoFilterExposesAllSpecialtiesInModel:119 Model attribute 'allSpecialties' does not exist
```

### Full Maven output

```text
[INFO] Results:

[ERROR] Failures:
[ERROR]   VetControllerTests.testShowVetListFilteredByNone:108 Model attribute 'selectedSpecialty'
Expected: is "none"
     but: was null
[ERROR]   VetControllerTests.testShowVetListFilteredBySpecialty:97 Model attribute 'selectedSpecialty'
Expected: is "radiology"
     but: was null
[ERROR]   VetControllerTests.testShowVetListNoFilterExposesAllSpecialtiesInModel:119 Model attribute 'allSpecialties' does not exist

[ERROR] Tests run: 5, Failures: 3, Errors: 0, Skipped: 0

[INFO] BUILD FAILURE
```

Existing tests `testShowVetListHtml` and `testShowResourcesVetList` continue to pass (2/5 pass). Three new tests fail for the correct reasons — RED phase confirmed.
