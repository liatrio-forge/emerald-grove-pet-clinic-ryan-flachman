# Proofs: Task 01 — Write failing VisitValidatorTests.java (RED)

Covers: AC-1.b, AC-3.a, AC-3.b, AC-5.a

## Planned evidence

- Output of `find src/test -name "VisitValidatorTests.java"` confirming the file exists (AC-5.a)
- Output of `./mvnw test -Dtest=VisitValidatorTests` showing **compilation failure** because `VisitValidator.java` does not yet exist — this is the RED phase confirmation

## Completion notes

### AC-5.a: `find src/test -name "VisitValidatorTests.java"` returns one match

```text
$ find src/test -name "VisitValidatorTests.java"
src/test/java/org/springframework/samples/petclinic/owner/VisitValidatorTests.java
```

### AC-1.b / AC-3.a / AC-3.b: RED phase — compilation failure (VisitValidator does not exist yet)

```text
$ ./mvnw test -Dtest=VisitValidatorTests
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/owner/VisitValidatorTests.java:[35,17] cannot find symbol
  symbol:   class VisitValidator
  location: class org.springframework.samples.petclinic.owner.VisitValidatorTests
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/owner/VisitValidatorTests.java:[39,33] cannot find symbol
  symbol:   class VisitValidator
  location: class org.springframework.samples.petclinic.owner.VisitValidatorTests
[INFO] 2 errors
[INFO] -------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] Total time:  3.250 s
[INFO] Finished at: 2026-05-06T14:47:57-05:00
```

Compile fails as expected. Will be fixed by Task 03 when `VisitValidator.java` is created.
