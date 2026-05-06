# Proofs: Task 03 — Create ResourceNotFoundException; replace throws in all three controllers (GREEN)

Covers: AC-1.a, AC-1.b, AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-5.a, AC-6.a, AC-7.a

## Planned evidence

- `find src/main/java -name "ResourceNotFoundException.java"` output showing the
  file path.
- `grep -n "@ResponseStatus" ...ResourceNotFoundException.java` output.
- `grep -n "ResourceNotFoundException" ...OwnerController.java` output (≥1 match).
- `grep -n "ResourceNotFoundException" ...PetController.java` output (≥1 match).
- `grep -n "ResourceNotFoundException" ...VisitController.java` output (≥1 match).
- `grep -rn "throw new IllegalArgumentException" src/.../owner/` output (no
  matches).
- `./mvnw test -Dtest=OwnerControllerTests` output confirming all tests pass
  including `testShowOwnerNotFound` (GREEN).
- `./mvnw test -Dtest=PetControllerTests` output confirming `testInitUpdatePetFormNotFound`
  passes (GREEN).
- `./mvnw test -Dtest=VisitControllerTests` output confirming
  `testInitNewVisitFormOwnerNotFound` passes (GREEN).

## Completion notes

### AC-1.a: `ResourceNotFoundException.java` exists in `system/` package

```text
$ find src/main/java -name "ResourceNotFoundException.java"
src/main/java/org/springframework/samples/petclinic/system/ResourceNotFoundException.java
```

### AC-1.b: Class carries `@ResponseStatus(HttpStatus.NOT_FOUND)`

```text
$ grep -n "@ResponseStatus" src/main/java/org/springframework/samples/petclinic/system/ResourceNotFoundException.java
24:@ResponseStatus(HttpStatus.NOT_FOUND)
```

### AC-2.a: `OwnerController` references `ResourceNotFoundException`

```text
$ grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java
37:import org.springframework.samples.petclinic.system.ResourceNotFoundException;
72:			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
187:			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
```

### AC-2.b: `PetController` references `ResourceNotFoundException`

```text
$ grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/PetController.java
38:import org.springframework.samples.petclinic.system.ResourceNotFoundException;
70:			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
82:			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
85:			throw new ResourceNotFoundException("Pet not found with id: " + petId);
```

### AC-2.c: `VisitController` references `ResourceNotFoundException`

```text
$ grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/VisitController.java
32:import org.springframework.samples.petclinic.system.ResourceNotFoundException;
68:			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
72:			throw new ResourceNotFoundException("Pet not found with id: " + petId);
```

### AC-2.d: No `IllegalArgumentException` thrown for missing resources in `owner/` package

```text
$ grep -rn "throw new IllegalArgumentException" src/main/java/org/springframework/samples/petclinic/owner/
(no output — zero matches)
```

### AC-5.a, AC-6.a, AC-7.a: All three controller 404 tests pass (GREEN)

```text
$ ./mvnw test -Dtest="OwnerControllerTests,PetControllerTests,VisitControllerTests"

[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0 -- in OwnerControllerTests
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0  -- in PetControllerTests$ProcessUpdateFormHasErrors
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0  -- in PetControllerTests$ProcessCreationFormHasErrors
[INFO] Tests run: 0, Failures: 0, Errors: 0, Skipped: 0  -- in PetControllerTests
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0  -- in VisitControllerTests
[INFO] Tests run: 35, Failures: 0, Errors: 0, Skipped: 0

[INFO] BUILD SUCCESS
```

`testShowOwnerNotFound`, `testInitUpdatePetFormNotFound`, and `testInitNewVisitFormOwnerNotFound` all pass.
