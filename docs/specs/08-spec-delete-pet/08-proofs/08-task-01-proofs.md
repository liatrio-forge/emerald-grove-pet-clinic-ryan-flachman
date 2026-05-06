# Proofs: Task 01 — Write failing PetControllerTests delete tests (RED)

Covers: AC-4.b, AC-4.c, AC-5.a, AC-6.a, AC-7.a

## Planned evidence

- `./mvnw test -Dtest=PetControllerTests` output showing the four new tests
  (`testDeletePetSuccess`, `testDeletePetWithVisitsCascade`,
  `testDeletePetOwnerNotFound`, `testDeletePetNotFound`) failing — expected
  failures because no handler exists yet.

## Completion notes

### RED phase: `./mvnw test -Dtest=PetControllerTests`

```text
[ERROR] org.springframework.samples.petclinic.owner.PetControllerTests.testDeletePetSuccess -- Time elapsed: 0.007 s <<< FAILURE!
java.lang.AssertionError: Range for response status value 404 expected:<REDIRECTION> but was:<CLIENT_ERROR>
	at org.springframework.test.util.AssertionErrors.fail(AssertionErrors.java:62)
	at org.springframework.test.util.AssertionErrors.assertEquals(AssertionErrors.java:129)
	at org.springframework.test.web.servlet.result.StatusResultMatchers.lambda$is3xxRedirection$0(StatusResultMatchers.java:88)
	at org.springframework.test.web.servlet.MockMvc$1.andExpect(MockMvc.java:212)
	at org.springframework.samples.petclinic.owner.PetControllerTests.testDeletePetSuccess(PetControllerTests.java:192)

[ERROR] Failures:
[ERROR]   PetControllerTests.testDeletePetSuccess:192 Range for response status value 404 expected:<REDIRECTION> but was:<CLIENT_ERROR>
[ERROR]   PetControllerTests.testDeletePetWithVisitsCascade:199 Range for response status value 404 expected:<REDIRECTION> but was:<CLIENT_ERROR>

[ERROR] Tests run: 15, Failures: 2, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

### Notes

- `testDeletePetSuccess` and `testDeletePetWithVisitsCascade` fail as expected:
  no handler exists, Spring returns HTTP 404 (CLIENT_ERROR), but tests expect
  a 3xx redirection.
- `testDeletePetOwnerNotFound` and `testDeletePetNotFound` pass in RED phase
  because Spring returns 404 for unmapped routes, which coincidentally satisfies
  `isNotFound()`. They will be properly tested in GREEN once the handler exists
  and distinguishes "no mapping" from "owner/pet not found".
- Spring format check required `./mvnw spring-javaformat:apply` before tests ran.
