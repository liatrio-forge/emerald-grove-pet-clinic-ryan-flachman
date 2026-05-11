# Questions: AI Visits Schema (12) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | Does the PostgreSQL schema need the `description` column extension? | **No.** `db/postgres/schema.sql` already defines `description TEXT`, which is unbounded. No modification required. |
| Q-2 | Are `ADD COLUMN IF NOT EXISTS` statements safe to re-run on MySQL and PostgreSQL? | **Yes.** MySQL 5.7+ and PostgreSQL 9.6+ both support `ADD COLUMN IF NOT EXISTS`; re-running on a table that already has the column is a no-op. |
| Q-3 | Is `MODIFY COLUMN description VARCHAR(2000)` idempotent on MySQL? | **Yes.** Re-issuing `MODIFY COLUMN` with the same type is a no-op once the column is already `VARCHAR(2000)`. |
| Q-4 | Do H2 and HSQLDB support `DEFAULT 'PENDING'` in a `CREATE TABLE` column definition? | **Yes.** Both engines support inline `DEFAULT '<literal>'` in `CREATE TABLE`. |
| Q-5 | What Java/Spring Boot test slice is appropriate to verify the schema at test time? | `@DataJpaTest` with an injected `JdbcTemplate`. The H2 in-memory DB is rebuilt from `db/h2/schema.sql` on each test run, making it the authoritative surface for schema verification in CI. |
| Q-6 | Should a test for description length check the H2 `INFORMATION_SCHEMA`? | **Yes.** `SELECT CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='VISITS' AND COLUMN_NAME='DESCRIPTION'` is available in H2 and HSQLDB. |
| Q-7 | Does this task include any data migration for existing rows? | **No.** `ai_status` defaults to `'PENDING'` for new rows only. Existing rows in dev/test databases get the default on schema rebuild; production ALTER TABLE adds the column with the default applied automatically by MySQL/PostgreSQL. |

## Open

None.
