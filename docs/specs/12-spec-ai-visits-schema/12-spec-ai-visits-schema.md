---
status: delivered
created: 2026-05-11
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: AI Visits Schema (12)

## Goal

The AI Visit Notes Summarizer epic (see `docs/epic-ai-visit-summary.md`) stores
Claude-generated analysis on each visit. Before the Java entity layer can map
those fields, all four database variants must carry the five new AI columns and
the extended `description` column. This spec adds those columns to each schema
file using the approach appropriate to each engine.

## Scope

### In scope

- Extend `description` from `VARCHAR(255)` to `VARCHAR(2000)` in H2, HSQLDB,
  and MySQL schema files.
- Add five new nullable AI columns to the `visits` table in all four DB variant
  schema files: `ai_status`, `ai_summary`, `ai_tags`, `ai_urgency`,
  `ai_follow_up`.
- Write a failing `@DataJpaTest` schema-verification test before modifying any
  schema file (TDD RED phase).

### Out of scope

- Java entity field mappings (`Visit.java` / `AiStatus` enum) — covered by
  TASK-02.
- Any data migration for rows that predate this change.
- Application properties, async configuration, or any service layer.

## Source excerpts

Schema files are stable production assets under
`src/main/resources/db/<variant>/schema.sql`. No freeze required.

- `src/main/resources/db/h2/schema.sql` — current `visits` DDL; `description
  VARCHAR(255)`.
- `src/main/resources/db/hsqldb/schema.sql` — same pattern.
- `src/main/resources/db/mysql/schema.sql` — uses `CREATE TABLE IF NOT EXISTS`;
  `description VARCHAR(255)`.
- `src/main/resources/db/postgres/schema.sql` — uses `CREATE TABLE IF NOT
  EXISTS`; `description TEXT` (already unbounded; no change needed).

## Acceptance criteria

- **AC-1: H2 schema**
  - AC-1.a: `description VARCHAR(2000)` appears inside the `CREATE TABLE visits`
    block in `db/h2/schema.sql`.
  - AC-1.b: `ai_status VARCHAR(20) DEFAULT 'PENDING'` appears inside the
    `CREATE TABLE visits` block in `db/h2/schema.sql`.
  - AC-1.c: `ai_summary VARCHAR(1000)`, `ai_tags VARCHAR(500)`,
    `ai_urgency VARCHAR(20)`, and `ai_follow_up VARCHAR(500)` appear inside the
    `CREATE TABLE visits` block in `db/h2/schema.sql` (nullable, no default).

- **AC-2: HSQLDB schema**
  - AC-2.a: `description VARCHAR(2000)` appears inside the `CREATE TABLE visits`
    block in `db/hsqldb/schema.sql`.
  - AC-2.b: All five AI columns (same types and defaults as AC-1.b / AC-1.c)
    appear inside the `CREATE TABLE visits` block in `db/hsqldb/schema.sql`.

- **AC-3: MySQL schema**
  - AC-3.a: `ALTER TABLE visits MODIFY COLUMN description VARCHAR(2000)` appears
    at the bottom of `db/mysql/schema.sql`.
  - AC-3.b: Five `ALTER TABLE visits ADD COLUMN IF NOT EXISTS` statements appear
    at the bottom of `db/mysql/schema.sql` with the column names, types, and
    `ai_status` default as specified.

- **AC-4: PostgreSQL schema**
  - AC-4.a: Five `ALTER TABLE visits ADD COLUMN IF NOT EXISTS` statements appear
    at the bottom of `db/postgres/schema.sql` with the column names, types, and
    `ai_status` default as specified.
  - AC-4.b: The `description TEXT` column definition in `db/postgres/schema.sql`
    is not modified (it is already unbounded).

- **AC-5: Existing test suite remains green**
  - AC-5.a: `./mvnw test` exits 0 with no test failures after all schema
    modifications are applied.

- **AC-6: New columns verified by automated test (H2)**
  - AC-6.a: A `@DataJpaTest` that queries `ai_status`, `ai_summary`, `ai_tags`,
    `ai_urgency`, and `ai_follow_up` from `visits` compiles and passes.
  - AC-6.b: A `@DataJpaTest` that queries `INFORMATION_SCHEMA.COLUMNS` confirms
    `description` has `CHARACTER_MAXIMUM_LENGTH = 2000` in H2.

## Conventions

- Schema files for H2 and HSQLDB are fully rebuilt on each app start; changes
  go directly into the `CREATE TABLE visits (...)` block.
- Schema files for MySQL and PostgreSQL use `CREATE TABLE IF NOT EXISTS` and
  are safe only to append; all new statements use `ADD COLUMN IF NOT EXISTS` or
  `MODIFY COLUMN` (idempotent under re-run).
- TDD is mandatory: the schema-verification test (`VisitsSchemaIT.java`) must
  be written and confirmed to fail before any schema file is modified.
- All columns follow the column inventory in `docs/epic-ai-visit-summary.md`
  TASK-01.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
