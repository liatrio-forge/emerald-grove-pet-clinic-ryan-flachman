# Proofs: Task 04 — Add Anthropic properties to `application.properties` (GREEN)

Covers: AC-1.a, AC-1.b, AC-1.c

## Planned evidence

- `grep` output showing all three Anthropic property lines in `application.properties`.
- `AsyncConfigPropertiesTest` passing output from
  `./mvnw test -Dtest=AsyncConfigPropertiesTest`.

## Completion notes

### AC-1.a: `anthropic.api.key=${ANTHROPIC_API_KEY:}` appears in `application.properties`

### AC-1.b: `anthropic.api.url=https://api.anthropic.com/v1/messages` appears in `application.properties`

### AC-1.c: `anthropic.model=claude-haiku-4-5-20251001` appears in `application.properties`

#### `grep -n "anthropic" src/main/resources/application.properties`

```text
28:anthropic.api.key=${ANTHROPIC_API_KEY:}
29:anthropic.api.url=https://api.anthropic.com/v1/messages
30:anthropic.model=claude-haiku-4-5-20251001
```

All three required lines are present at lines 28–30. The `# Anthropic / Claude API` section
header appears at line 27.

#### `./mvnw test -Dtest="AsyncConfigPropertiesTest"` (GREEN phase)

```text
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.437 s -- in org.springframework.samples.petclinic.system.AsyncConfigPropertiesTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

All 3 `containsProperty` assertions return `true`.
