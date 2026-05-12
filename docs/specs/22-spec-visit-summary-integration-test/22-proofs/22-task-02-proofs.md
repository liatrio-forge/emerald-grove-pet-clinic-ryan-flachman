# Proofs: Task 02 — Create VisitSummaryHappyPathIT

Covers: AC-2.a–AC-2.c, AC-3.a–AC-3.e, AC-4.a–AC-4.b

## File

`src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java` — present.

**Note:** Spring Boot 4 uses `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc` (not `org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc`).

## Maven — `VisitSummaryHappyPathIT` only

```bash
./mvnw test -Dtest=VisitSummaryHappyPathIT
```

```text
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.378 s -- in org.springframework.samples.petclinic.owner.VisitSummaryHappyPathIT
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Annotation greps

```bash
grep -c "@SpringBootTest" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
grep -c "@AutoConfigureMockMvc" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
grep -c "@DisabledInNativeImage" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
grep -c "@DisabledInAotMode" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
! grep -q "@Transactional" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java && echo "no @Transactional"
```

Output:

```text
1
1
1
1
no @Transactional
```
