# Proofs: Task 03 — Write failing `AsyncConfigPropertiesTest` (RED)

Covers: AC-1.d

## Planned evidence

- Test failure output showing `AsyncConfigPropertiesTest` failing because
  `containsProperty("anthropic.api.key")` (and/or the other two) returns `false`,
  confirming the test is genuinely red before the properties are added.

## Completion notes

### AC-1.d: `AsyncConfigPropertiesTest` (a `@SpringBootTest`) passes (RED phase captured here)

File created: `src/test/java/org/springframework/samples/petclinic/system/AsyncConfigPropertiesTest.java`

#### `./mvnw test -Dtest="AsyncConfigPropertiesTest"` (RED phase — all 3 assertions fail)

```text
[ERROR] Tests run: 3, Failures: 3, Errors: 0, Skipped: 0, Time elapsed: 2.684 s <<< FAILURE! -- in org.springframework.samples.petclinic.system.AsyncConfigPropertiesTest
[ERROR] org.springframework.samples.petclinic.system.AsyncConfigPropertiesTest.anthropicApiKeyPropertyIsRegistered -- Time elapsed: 0.544 s <<< FAILURE!
[ERROR] org.springframework.samples.petclinic.system.AsyncConfigPropertiesTest.anthropicModelPropertyIsRegistered -- Time elapsed: 0.003 s <<< FAILURE!
[ERROR] org.springframework.samples.petclinic.system.AsyncConfigPropertiesTest.anthropicApiUrlPropertyIsRegistered -- Time elapsed: 0.003 s <<< FAILURE!
[ERROR] Failures:
[ERROR]   AsyncConfigPropertiesTest.anthropicApiKeyPropertyIsRegistered:18
[ERROR]   AsyncConfigPropertiesTest.anthropicApiUrlPropertyIsRegistered:23
[ERROR]   AsyncConfigPropertiesTest.anthropicModelPropertyIsRegistered:28
[ERROR] Tests run: 3, Failures: 3, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

All 3 assertions return `false` before the properties are added. Task 04 fixes this.

### Notes

Committed Task 03 (test, RED) and Task 04 (properties, GREEN) together in one commit
because the pre-commit hook runs `./mvnw test` and blocks commits with test failures.
The RED phase evidence is preserved in this proof.
