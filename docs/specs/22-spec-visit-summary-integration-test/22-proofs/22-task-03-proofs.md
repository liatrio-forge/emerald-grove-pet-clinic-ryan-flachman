# Proofs: Task 03 — Create VisitSummaryFailureIT

Covers: AC-5.a–AC-5.d, AC-6.a–AC-6.b

## File

`src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java` — present.

`@MockitoBean` import: `org.springframework.test.context.bean.override.mockito.MockitoBean`.

## Maven — `VisitSummaryFailureIT` only

```bash
./mvnw test -Dtest=VisitSummaryFailureIT
```

```text
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.448 s -- in org.springframework.samples.petclinic.owner.VisitSummaryFailureIT
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Annotation greps

```bash
grep -c "@SpringBootTest" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
grep -c "@AutoConfigureMockMvc" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
grep -c "@DisabledInNativeImage" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
grep -c "@DisabledInAotMode" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
grep -c "@MockitoBean" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
! grep -q "@Transactional" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java && echo "no @Transactional"
```

Output:

```text
1
1
1
1
1
no @Transactional
```
