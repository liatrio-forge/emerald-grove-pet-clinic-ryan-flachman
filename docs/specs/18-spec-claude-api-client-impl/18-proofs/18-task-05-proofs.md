# Proofs: Task 05 — Validate and capture proof artifacts

Covers: all

## Planned evidence

- `./mvnw test` output showing zero failures across the full test suite.
- JaCoCo coverage report excerpt showing ≥90% line coverage for
  `ClaudeApiException` and `ClaudeApiClientImpl`.
- Confirmation that all rows in the coverage matrix are `PASS`.

## Completion notes

Full suite: `./mvnw clean test` completed with **BUILD SUCCESS**, **149 tests**,
**0 failures** (same on a subsequent `./mvnw test` run).

JaCoCo (from `target/site/jacoco/jacoco.xml`, `LINE` counters):

| Class               | Lines covered | Line % (approx.) |
|---------------------|---------------|------------------|
| `ClaudeApiException` | 4/4          | 100%             |
| `ClaudeApiClientImpl` | 21/23     | 91.3%            |

Coverage matrix in `18-validation-claude-api-client-impl.md` updated to **PASS** for all AC rows.

**MySQL integration:** Oracle MySQL does not support `ADD COLUMN IF NOT EXISTS` used in
`db/mysql/schema.sql`; AI columns were changed to plain `ADD COLUMN` for fresh DB init.
`db/mysql/data.sql` visit `INSERT` statements now list columns explicitly so defaults apply
when AI columns exist.
