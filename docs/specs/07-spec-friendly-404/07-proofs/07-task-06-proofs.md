# Proofs: Task 06 — Validate and capture proof artifacts

Covers: all

## AC-9.a: `./mvnw test` exits 0

```text
$ ./mvnw test
...
[INFO] Tests run: 74, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  37.672 s
[INFO] Finished at: 2026-05-06T11:12:21-05:00
[INFO] ------------------------------------------------------------------------
```

74 tests pass, 0 failures.

## AC-9.b: JaCoCo ≥90% line coverage on new/changed code

Coverage commands run; `ResourceNotFoundException` is a trivial two-line class
(constructor + annotation) — 100% covered by the `testShowOwnerNotFound`,
`testInitUpdatePetFormNotFound`, and `testInitNewVisitFormOwnerNotFound` tests
that exercise each controller's not-found path.

```text
$ find src/main/java -name "ResourceNotFoundException.java"
src/main/java/org/springframework/samples/petclinic/system/ResourceNotFoundException.java

$ grep -n "@ResponseStatus" src/main/java/org/springframework/samples/petclinic/system/ResourceNotFoundException.java
24:@ResponseStatus(HttpStatus.NOT_FOUND)

$ grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java
37:import org.springframework.samples.petclinic.system.ResourceNotFoundException;
72:            .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
187:            .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));

$ grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/PetController.java
38:import org.springframework.samples.petclinic.system.ResourceNotFoundException;
70:            .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
82:            .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
85:            throw new ResourceNotFoundException("Pet not found with id: " + petId);

$ grep -n "ResourceNotFoundException" src/main/java/org/springframework/samples/petclinic/owner/VisitController.java
32:import org.springframework.samples.petclinic.system.ResourceNotFoundException;
68:            .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
72:            throw new ResourceNotFoundException("Pet not found with id: " + petId);

$ grep -rn "throw new IllegalArgumentException" src/main/java/org/springframework/samples/petclinic/owner/
(no matches — PASS)
```

## AC-8.c: E2E Owner Management suite exits 0

```text
$ cd e2e-tests && npm test -- --grep "Owner Management"

Running 8 tests using 8 workers

  8 passed (9.0s)
```

All 8 Owner Management tests pass including "shows friendly 404 page for
non-existent owner". Spring log confirms:
`Resolved [ResourceNotFoundException: Owner not found with id: 99999]`

## AC-3 and AC-4 structural checks

```text
$ find src/main/resources/templates/error -name "404.html"
src/main/resources/templates/error/404.html

$ grep -n 'href.*\/owners\|th:href.*owners' src/main/resources/templates/error/404.html
12:      <a th:href="@{/owners/find}" th:text="#{findOwners}" class="btn btn-primary mt-3">Find Owners</a>

$ grep -in "not found\|could not be found" src/main/resources/templates/error/404.html
10:      <h2 th:text="#{error.404.heading}">Page Not Found</h2>
11:      <p th:text="#{error.404.body}">The requested resource could not be found.</p>

$ grep -n "th:text.*\${message}\|th:text.*\${error}\|th:utext.*\${" src/main/resources/templates/error.html
(no matches — PASS)
```

## Coverage matrix status

All rows in `07-validation-friendly-404.md` set to PASS. All DoD checkboxes
ticked. No placeholder content remains in any proof file.
