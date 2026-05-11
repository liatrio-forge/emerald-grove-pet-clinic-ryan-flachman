# Proofs: Task 02 — Update H2 and HSQLDB CREATE TABLE visits blocks

Covers: AC-1.a, AC-1.b, AC-1.c, AC-2.a, AC-2.b

## Planned evidence

- Diff of `src/main/resources/db/h2/schema.sql` showing `description VARCHAR(2000)`
  and all five AI columns inside the `CREATE TABLE visits (...)` block.
- Diff of `src/main/resources/db/hsqldb/schema.sql` showing the same changes.
- `./mvnw test -Dtest=VisitsSchemaIT` output showing the test now passing.

## Completion notes

(Filled in by `implement-sdd-spec`.)
