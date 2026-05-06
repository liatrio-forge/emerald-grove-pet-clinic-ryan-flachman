# Proofs: Task 01 — Add repository method and write failing OwnerServiceTests (RED)

Covers: AC-1.a, AC-2.a, AC-2.b

## Planned evidence

- `OwnerRepository.java` diff showing the new `existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone` method signature.
- `OwnerServiceTests.java` file listing showing the two new test methods.
- `./mvnw test -Dtest=OwnerServiceTests` compiler error output confirming the test cannot resolve `OwnerService` (RED phase).

## Completion notes

### AC-1.a: `OwnerRepository` declares `existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone`

```diff
--- a/src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java
+++ b/src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java
@@ -81,4 +81,8 @@
 	Optional<Owner> findById(Integer id);

+	boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone(String firstName, String lastName,
+			String telephone);
+
 }
```

### AC-2.a and AC-2.b: `OwnerServiceTests.java` created with both test methods

File: `src/test/java/org/springframework/samples/petclinic/owner/OwnerServiceTests.java`

- `testIsDuplicate_returnsTrueWhenMatchExists()` — stubs repository to return `true`, asserts `isDuplicate` returns `true`
- `testIsDuplicate_returnsFalseWhenNoMatch()` — stubs repository to return `false`, asserts `isDuplicate` returns `false`

### RED phase: `./mvnw test -Dtest=OwnerServiceTests`

```text
[INFO] COMPILATION ERROR :
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/owner/OwnerServiceTests.java:[34,17] cannot find symbol
  symbol:   class OwnerService
  location: class org.springframework.samples.petclinic.owner.OwnerServiceTests
[INFO] 1 error
[INFO] BUILD FAILURE
[INFO] Total time:  3.638 s
```

Compile fails as expected — `OwnerService` does not exist yet. RED phase confirmed.
