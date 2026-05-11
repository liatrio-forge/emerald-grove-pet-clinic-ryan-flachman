# Validation: AI Visits Schema (12)

## Automated verification

From the repository root:

```bash
# AC-1: grep H2 schema for AI columns and extended description
grep -n "VARCHAR(2000)" src/main/resources/db/h2/schema.sql
grep -n "ai_status" src/main/resources/db/h2/schema.sql
grep -n "ai_summary" src/main/resources/db/h2/schema.sql
grep -n "ai_tags" src/main/resources/db/h2/schema.sql
grep -n "ai_urgency" src/main/resources/db/h2/schema.sql
grep -n "ai_follow_up" src/main/resources/db/h2/schema.sql

# AC-2: grep HSQLDB schema
grep -n "VARCHAR(2000)" src/main/resources/db/hsqldb/schema.sql
grep -n "ai_status\|ai_summary\|ai_tags\|ai_urgency\|ai_follow_up" src/main/resources/db/hsqldb/schema.sql

# AC-3: grep MySQL schema
grep -n "MODIFY COLUMN description" src/main/resources/db/mysql/schema.sql
grep -n "ADD COLUMN IF NOT EXISTS" src/main/resources/db/mysql/schema.sql

# AC-4.a: grep PostgreSQL schema for AI column ALTER statements
grep -n "ADD COLUMN IF NOT EXISTS" src/main/resources/db/postgres/schema.sql
# AC-4.b: confirm description TEXT is unchanged
grep -n "description" src/main/resources/db/postgres/schema.sql

# AC-5 + AC-6: run full test suite (includes VisitsSchemaIT)
./mvnw test
```

**Expected:**

- AC-1/2: All six `grep` commands for H2 and HSQLDB each return at least one match.
- AC-3: `MODIFY COLUMN description` matches once; `ADD COLUMN IF NOT EXISTS` matches five times.
- AC-4.a: `ADD COLUMN IF NOT EXISTS` matches five times in the PostgreSQL file.
- AC-4.b: The only `description` line in `db/postgres/schema.sql` reads `description TEXT` (unchanged).
- AC-5 / AC-6: `./mvnw test` exits 0; `VisitsSchemaIT` is listed in the Surefire output as passing.

## Traceability

- Feature spec: `12-spec-ai-visits-schema.md`
- Task breakdown: `12-tasks-ai-visits-schema.md`
- Questions and decisions: `12-questions-1-ai-visits-schema.md`
- Per-task evidence: `12-proofs/12-task-NN-proofs.md`
- Parent epic: `docs/epic-ai-visit-summary.md` (TASK-01)

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `description VARCHAR(2000)` in H2 `CREATE TABLE visits` | `12-proofs/12-task-02-proofs.md` | file edit | PENDING |
| AC-1.b | `ai_status VARCHAR(20) DEFAULT 'PENDING'` in H2 `CREATE TABLE visits` | `12-proofs/12-task-02-proofs.md` | file edit | PENDING |
| AC-1.c | `ai_summary`, `ai_tags`, `ai_urgency`, `ai_follow_up` in H2 `CREATE TABLE visits` | `12-proofs/12-task-02-proofs.md` | file edit | PENDING |
| AC-2.a | `description VARCHAR(2000)` in HSQLDB `CREATE TABLE visits` | `12-proofs/12-task-02-proofs.md` | file edit | PENDING |
| AC-2.b | All five AI columns in HSQLDB `CREATE TABLE visits` | `12-proofs/12-task-02-proofs.md` | file edit | PENDING |
| AC-3.a | `MODIFY COLUMN description VARCHAR(2000)` at bottom of MySQL schema | `12-proofs/12-task-03-proofs.md` | file edit | PENDING |
| AC-3.b | Five `ADD COLUMN IF NOT EXISTS` statements in MySQL schema | `12-proofs/12-task-03-proofs.md` | file edit | PENDING |
| AC-4.a | Five `ADD COLUMN IF NOT EXISTS` statements in PostgreSQL schema | `12-proofs/12-task-04-proofs.md` | file edit | PENDING |
| AC-4.b | `description TEXT` unchanged in PostgreSQL schema | `12-proofs/12-task-04-proofs.md` | command output | PENDING |
| AC-5.a | `./mvnw test` exits 0 after all schema changes | `12-proofs/12-task-05-proofs.md` | command output | PENDING |
| AC-6.a | `VisitsSchemaIT` queries AI columns without error | `12-proofs/12-task-01-proofs.md` | Maven test pass | PENDING |
| AC-6.b | `VisitsSchemaIT` confirms `description` max length = 2000 via `INFORMATION_SCHEMA` | `12-proofs/12-task-01-proofs.md` | Maven test pass | PENDING |

## Definition of done

- [ ] AC-1.a: `description VARCHAR(2000)` in H2 `CREATE TABLE visits`
- [ ] AC-1.b: `ai_status VARCHAR(20) DEFAULT 'PENDING'` in H2 `CREATE TABLE visits`
- [ ] AC-1.c: `ai_summary`, `ai_tags`, `ai_urgency`, `ai_follow_up` in H2 `CREATE TABLE visits`
- [ ] AC-2.a: `description VARCHAR(2000)` in HSQLDB `CREATE TABLE visits`
- [ ] AC-2.b: All five AI columns in HSQLDB `CREATE TABLE visits`
- [ ] AC-3.a: `MODIFY COLUMN description VARCHAR(2000)` at bottom of MySQL schema
- [ ] AC-3.b: Five `ADD COLUMN IF NOT EXISTS` statements in MySQL schema
- [ ] AC-4.a: Five `ADD COLUMN IF NOT EXISTS` statements in PostgreSQL schema
- [ ] AC-4.b: `description TEXT` unchanged in PostgreSQL schema
- [ ] AC-5.a: `./mvnw test` exits 0 after all schema changes
- [ ] AC-6.a: `VisitsSchemaIT` queries AI columns without error
- [ ] AC-6.b: `VisitsSchemaIT` confirms `description` max length = 2000
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
