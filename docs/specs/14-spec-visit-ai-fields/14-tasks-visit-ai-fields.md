# Tasks: Visit AI Fields (14)

## Task 01 — Write failing `AiStatusTest`

Covers: AC-1.b, AC-1.c

- Create `src/test/java/org/springframework/samples/petclinic/owner/AiStatusTest.java`.
- Import `AiStatus` (does not exist yet — this causes a compile failure, confirming RED phase).
- Assert `AiStatus.values().length == 4`.
- Assert the four values are `PENDING`, `PROCESSING`, `DONE`, `FAILED` in that order.
- Assert `AiStatus.valueOf("PENDING") == AiStatus.PENDING`.
- Run `./mvnw test -Dtest=AiStatusTest` and confirm it fails to compile.

**Proof:** 14-proofs/14-task-01-proofs.md

## Task 02 — Create `AiStatus` enum

Covers: AC-1.a, AC-1.b, AC-1.c

- Create `src/main/java/org/springframework/samples/petclinic/owner/AiStatus.java`.
- Declare `public enum AiStatus` with values `PENDING`, `PROCESSING`, `DONE`, `FAILED`.
- Run `./mvnw test -Dtest=AiStatusTest` and confirm it passes (GREEN phase).

**Proof:** 14-proofs/14-task-02-proofs.md

## Task 03 — Write failing `VisitAiFieldsTest` and `VisitAiFieldsIT`

Covers: AC-2.a–e, AC-3.a, AC-4.a–b, AC-5.a–b, AC-6.a–b

- Create `src/test/java/org/springframework/samples/petclinic/owner/VisitAiFieldsTest.java` (plain JUnit 5, no Spring context):
  - Assert `new Visit().getAiStatus() == AiStatus.PENDING` (AC-4.a).
  - Assert `new Visit().getAiSummary() == null`, `getAiTags() == null`, `getAiUrgency() == null`, `getAiFollowUp() == null` (AC-4.b).
  - Assert `setAiStatus(AiStatus.DONE)` → `getAiStatus() == AiStatus.DONE` (AC-5.a).
  - Assert setter/getter round-trips for `aiSummary`, `aiTags`, `aiUrgency`, `aiFollowUp` (AC-5.b).
- Create `src/test/java/org/springframework/samples/petclinic/owner/VisitAiFieldsIT.java` (`@DataJpaTest`):
  - Persist a `Visit` with all five AI fields populated; reload by ID and assert all five values are preserved (AC-6.a).
  - Persist a `Visit` with no AI fields set; reload by ID and assert `aiStatus == PENDING` and the four string fields are `null` (AC-6.b).
- Run `./mvnw test -Dtest=VisitAiFieldsTest,VisitAiFieldsIT` and confirm compile errors (RED phase — AI getters/setters do not exist yet on `Visit`).

**Proof:** 14-proofs/14-task-03-proofs.md

## Task 04 — Add AI fields to `Visit.java`

Covers: AC-2.a, AC-2.b, AC-2.c, AC-2.d, AC-2.e, AC-3.a, AC-4.a, AC-4.b, AC-5.a, AC-5.b, AC-6.a, AC-6.b

- Add `@Column(length = 2000)` to the existing `description` field (AC-3.a).
- Add the following five fields with their JPA annotations:

  ```java
  @Column(name = "ai_status", length = 20)
  @Enumerated(EnumType.STRING)
  private AiStatus aiStatus;

  @Column(name = "ai_summary", length = 1000)
  private String aiSummary;

  @Column(name = "ai_tags", length = 500)
  private String aiTags;

  @Column(name = "ai_urgency", length = 20)
  private String aiUrgency;

  @Column(name = "ai_follow_up", length = 500)
  private String aiFollowUp;
  ```

- In the `Visit()` constructor, add `this.aiStatus = AiStatus.PENDING;` (AC-4.a).
- Add getters and setters for all five new fields (AC-5.a, AC-5.b).
- Add required imports: `jakarta.persistence.Enumerated`, `jakarta.persistence.EnumType`.
- Run `./mvnw test -Dtest=VisitAiFieldsTest,VisitAiFieldsIT` and confirm both test classes pass (GREEN phase).

**Proof:** 14-proofs/14-task-04-proofs.md

## Task 05 — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture the full output (AC-7.a).
- Run structural grep checks from `14-validation-visit-ai-fields.md` and capture outputs.
- Run `./mvnw test jacoco:report` and record line coverage percentage for the `owner` package.
- Confirm each AC ID has at least one passing proof artifact.
- Update the coverage matrix in `14-validation-visit-ai-fields.md` from `PENDING` to `PASS`.

**Proof:** 14-proofs/14-task-05-proofs.md
