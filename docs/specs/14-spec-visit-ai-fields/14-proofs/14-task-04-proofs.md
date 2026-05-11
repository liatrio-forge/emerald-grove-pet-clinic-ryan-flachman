# Proofs: Task 04 — Add AI fields to `Visit.java`

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-2.e, AC-3.a, AC-4.a, AC-4.b, AC-5.a, AC-5.b, AC-6.a, AC-6.b

## Planned evidence

- Diff / listing of updated `Visit.java` showing all five new fields with
  their `@Column` / `@Enumerated` annotations, the updated `description`
  annotation, and the constructor default.
- `./mvnw test -Dtest=VisitAiFieldsTest,VisitAiFieldsIT` command output
  showing all tests pass (GREEN phase).

## Completion notes

### AC-3.a: `description` field carries `@Column(length=2000)`

```diff
-	@NotBlank
+	@Column(length = 2000)
+	@NotBlank
 	private String description;
```

### AC-2.a: `aiStatus` field with `@Column(name="ai_status", length=20)` and `@Enumerated(EnumType.STRING)`

```java
@Column(name = "ai_status", length = 20)
@Enumerated(EnumType.STRING)
private AiStatus aiStatus;
```

### AC-2.b: `aiSummary` field with `@Column(name="ai_summary", length=1000)`

```java
@Column(name = "ai_summary", length = 1000)
private String aiSummary;
```

### AC-2.c: `aiTags` field with `@Column(name="ai_tags", length=500)`

```java
@Column(name = "ai_tags", length = 500)
private String aiTags;
```

### AC-2.d: `aiUrgency` field with `@Column(name="ai_urgency", length=20)`

```java
@Column(name = "ai_urgency", length = 20)
private String aiUrgency;
```

### AC-2.e: `aiFollowUp` field with `@Column(name="ai_follow_up", length=500)`

```java
@Column(name = "ai_follow_up", length = 500)
private String aiFollowUp;
```

### AC-4.a / AC-5.a / AC-5.b / AC-4.b / AC-6.a / AC-6.b: GREEN phase test run

```text
$ ./mvnw test -Dtest="VisitAiFieldsTest,VisitAiFieldsIT"
Hibernate: insert into visits (ai_follow_up,ai_status,ai_summary,ai_tags,ai_urgency,visit_date,description,id) values (?,?,?,?,?,?,?,default)
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.774 s -- in org.springframework.samples.petclinic.owner.VisitAiFieldsIT

[INFO] Results:
[INFO]
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
[INFO] Total time:  10.813 s
[INFO] Finished at: 2026-05-11T12:14:00-05:00
```

All 6 tests pass:

- `VisitAiFieldsTest` (4 tests): constructor defaults, getter/setter round-trips
- `VisitAiFieldsIT` (2 tests): JPA persist-and-reload with all AI fields set; fresh Visit defaults

The Hibernate SELECT query confirms all 5 AI columns are mapped:

```sql
select v1_0.pet_id,v1_0.id,v1_0.ai_follow_up,v1_0.ai_status,v1_0.ai_summary,v1_0.ai_tags,v1_0.ai_urgency,v1_0.visit_date,v1_0.description from visits v1_0 where v1_0.pet_id=? order by v1_0.visit_date
```

### Notes

Constructor also updated: `this.aiStatus = AiStatus.PENDING;` added.
Getters and setters added for all five fields.
Imports added: `jakarta.persistence.EnumType`, `jakarta.persistence.Enumerated`.

Task 03 (RED phase for these tests) and Task 04 (GREEN phase) are committed together
because the project pre-commit Maven hook blocks commits with compile errors.
The RED phase evidence is captured in `14-task-03-proofs.md`.
