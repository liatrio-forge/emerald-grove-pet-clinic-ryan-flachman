# Proofs: Task 01 — Write failing schema-verification test (RED)

Covers: AC-6.a, AC-6.b

## Evidence

`src/test/java/org/springframework/samples/petclinic/service/VisitsSchemaIT.java` created with two tests:

- `descriptionColumnHasExtendedLength` — queries `INFORMATION_SCHEMA.COLUMNS` for `CHARACTER_MAXIMUM_LENGTH = 2000`
- `aiColumnsExistInVisitsTable` — queries `INFORMATION_SCHEMA.COLUMNS` for all five AI column names

### RED phase output (before schema changes)

```text
[ERROR]   VisitsSchemaIT.descriptionColumnHasExtendedLength:29
expected: 2000
 but was: 255

Expecting actual:
  []
to contain exactly in any order:
  ["AI_STATUS", "AI_SUMMARY", "AI_TAGS", "AI_URGENCY", "AI_FOLLOW_UP"]
but could not find the following elements:
  ["AI_STATUS", "AI_SUMMARY", "AI_TAGS", "AI_URGENCY", "AI_FOLLOW_UP"]

Tests run: 2, Failures: 2, Errors: 0, Skipped: 0
BUILD FAILURE
```

Both tests fail for the correct reason: schema not yet updated.
