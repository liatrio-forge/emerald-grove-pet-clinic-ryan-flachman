# Proofs: Task 04 — Append ALTER TABLE statements to PostgreSQL schema

Covers: AC-4.a, AC-4.b

## Planned evidence

- Diff of `src/main/resources/db/postgres/schema.sql` showing five `ADD COLUMN
  IF NOT EXISTS` statements appended at the bottom of the file.
- `grep -n "description" src/main/resources/db/postgres/schema.sql` output
  confirming the only match is the original `description TEXT` line (unchanged).

## Completion notes

(Filled in by `implement-sdd-spec`.)
