# Proofs: Task 03 — Append ALTER TABLE statements to MySQL schema

Covers: AC-3.a, AC-3.b

## Evidence

Appended to `db/mysql/schema.sql` (lines 57–62):

```sql
ALTER TABLE visits MODIFY COLUMN description VARCHAR(2000);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_summary VARCHAR(1000);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_tags VARCHAR(500);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_urgency VARCHAR(20);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_follow_up VARCHAR(500);
```

`MODIFY COLUMN description` appears once; `ADD COLUMN IF NOT EXISTS` appears five times.
