# Proofs: Task 03 — Write failing `VisitAiFieldsTest` and `VisitAiFieldsIT`

Covers: AC-2.a–e, AC-3.a, AC-4.a–b, AC-5.a–b, AC-6.a–b

## Planned evidence

- `./mvnw test -Dtest=VisitAiFieldsTest,VisitAiFieldsIT` command output
  showing compilation errors or test failures (RED phase — AI fields do not
  exist on `Visit.java` yet).

## Completion notes

### RED phase — compile failure before AI fields exist on Visit

```text
$ ./mvnw test -Dtest="VisitAiFieldsTest,VisitAiFieldsIT"
[ERROR] COMPILATION ERROR :
[ERROR] .../VisitAiFieldsIT.java:[31,22] cannot find symbol
  symbol:   method setAiStatus(org.springframework.samples.petclinic.owner.AiStatus)
[ERROR] .../VisitAiFieldsIT.java:[32,22] cannot find symbol
  symbol:   method setAiSummary(java.lang.String)
[ERROR] .../VisitAiFieldsIT.java:[33,22] cannot find symbol
  symbol:   method setAiTags(java.lang.String)
[ERROR] .../VisitAiFieldsIT.java:[34,22] cannot find symbol
  symbol:   method setAiUrgency(java.lang.String)
[ERROR] .../VisitAiFieldsIT.java:[35,22] cannot find symbol
  symbol:   method setAiFollowUp(java.lang.String)
[ERROR] .../VisitAiFieldsIT.java:[50,36] cannot find symbol
  symbol:   method getAiStatus()
[ERROR] .../VisitAiFieldsIT.java:[51,36] cannot find symbol
  symbol:   method getAiSummary()
[ERROR] .../VisitAiFieldsIT.java:[52,36] cannot find symbol
  symbol:   method getAiTags()
[ERROR] .../VisitAiFieldsIT.java:[53,36] cannot find symbol
  symbol:   method getAiUrgency()
[ERROR] .../VisitAiFieldsIT.java:[54,36] cannot find symbol
```

Files created:

- `src/test/java/org/springframework/samples/petclinic/owner/VisitAiFieldsTest.java`
  (plain JUnit 5, no Spring context — tests constructor defaults and getter/setter round-trips)
- `src/test/java/org/springframework/samples/petclinic/owner/VisitAiFieldsIT.java`
  (`@DataJpaTest` — persists and reloads visits with AI fields via `OwnerRepository`)

RED phase confirmed: AI getters/setters do not yet exist on `Visit`; compile fails as expected.
Task 04 will add the fields and move to GREEN.

### Notes

The spring-javaformat plugin enforced formatting before the compile check could reveal
the missing symbols. After running `./mvnw spring-javaformat:apply`, the compile
error was captured as shown above.
