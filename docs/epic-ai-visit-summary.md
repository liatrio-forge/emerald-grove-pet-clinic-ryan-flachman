# Epic: AI Visit Notes Summarizer + Health Timeline

## Summary

When a vet saves a visit, an async background job calls the Claude API with the
visit description and pet context. Claude returns structured JSON (summary, tags,
urgency, follow-up) that is stored back on the `Visit` record. The owner detail
page renders a collapsible health timeline per pet, polling until each summary is
ready.

**Key design decisions:**

- AI call is async (visit saves instantly; background thread does the work)
- Claude output is structured JSON, not free text
- Health timeline lives in the existing owner detail page (collapsible per pet)
- API key supplied via env var `ANTHROPIC_API_KEY`; app auto-stubs when key is absent

---

## Task Inventory

### TASK-01 — DB Schema: add AI columns to visits (all 4 DB variants)

Add 5 new columns to the `visits` table across all four schema files, and extend
the existing `description` column from `VARCHAR(255)` to `VARCHAR(2000)`. The
approach differs by DB variant because of how each schema file is structured:

**Columns to add:**

| Column | Type | Notes |
|---|---|---|
| `ai_status` | `VARCHAR(20)` | default `'PENDING'` |
| `ai_summary` | `VARCHAR(1000)` | nullable |
| `ai_tags` | `VARCHAR(500)` | comma-joined, nullable |
| `ai_urgency` | `VARCHAR(20)` | nullable |
| `ai_follow_up` | `VARCHAR(500)` | nullable |

**H2 and HSQLDB** (`db/h2/schema.sql`, `db/hsqldb/schema.sql`):
Both use `DROP TABLE ... IF EXISTS` followed by `CREATE TABLE`, so the schema is
fully torn down and rebuilt on every app start. Add the 5 columns directly inside
the existing `CREATE TABLE visits (...)` block. No manual migration needed — they
appear automatically on the next start.

**MySQL and PostgreSQL** (`db/mysql/schema.sql`, `db/postgres/schema.sql`):
Both use `CREATE TABLE IF NOT EXISTS`, so an already-existing `visits` table is
never recreated and new columns will never appear from a `CREATE TABLE` change
alone. Append `ALTER TABLE` statements at the bottom of each file using the
idempotent `ADD COLUMN IF NOT EXISTS` form. These run on every startup and are
safe to re-run:

```sql
-- MySQL
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_status   VARCHAR(20)   DEFAULT 'PENDING';
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_summary  VARCHAR(1000);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_tags     VARCHAR(500);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_urgency  VARCHAR(20);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_follow_up VARCHAR(500);

-- PostgreSQL (same syntax, supported since PG 9.6)
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_status    VARCHAR(20)   DEFAULT 'PENDING';
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_summary   VARCHAR(1000);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_tags      VARCHAR(500);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_urgency   VARCHAR(20);
ALTER TABLE visits ADD COLUMN IF NOT EXISTS ai_follow_up VARCHAR(500);
```

If you have a local MySQL or PostgreSQL container running right now with an
existing `visits` table, run these `ALTER TABLE` statements once manually (or
just restart the app with the profile active — Spring will execute the updated
schema.sql automatically on startup via `spring.sql.init.mode`).

**Depends on:** nothing
**Blocks:** TASK-02

---

### TASK-02 — Visit entity: add AI fields + AiStatus enum

Update `Visit.java` to map the 5 new columns. Add an `AiStatus` enum
(`PENDING`, `PROCESSING`, `DONE`, `FAILED`) in the same package. Default
`aiStatus` to `PENDING` in the `Visit()` constructor.

**Depends on:** TASK-01
**Blocks:** TASK-04

---

### TASK-03 — Configuration: async thread pool + Anthropic properties

Two independent sub-tasks that ship together:

1. `application.properties` — add:

   ```properties
   anthropic.api.key=${ANTHROPIC_API_KEY:}
   anthropic.api.url=https://api.anthropic.com/v1/messages
   anthropic.model=claude-haiku-4-5-20251001
   ```

2. `AsyncConfig.java` — `@Configuration @EnableAsync` class with a named
   `ThreadPoolTaskExecutor` bean (`visitSummaryExecutor`): core=2, max=5,
   queue=25, rejection policy = CallerRunsPolicy.

**Depends on:** nothing
**Blocks:** TASK-09, TASK-10

---

### TASK-04 — VisitSummary DTO

Immutable record/value object in the `owner` package representing the parsed
Claude response:

```java
public record VisitSummary(
    String summary,
    List<String> tags,
    AiStatus urgency,   // reuse or create VisitUrgency enum: ROUTINE/MONITOR/URGENT
    String followUp
) {}
```

Write unit tests verifying construction and nullability contracts.

**Depends on:** TASK-02
**Blocks:** TASK-05, TASK-06, TASK-07

---

### TASK-05 — VisitPromptBuilder

`VisitPromptBuilder.java` — pure utility class (no Spring annotations). Takes a
`Visit` + `Pet`, returns a `PromptRequest` (two strings: system prompt, user
message).

System prompt: scoped clinical veterinary assistant instruction with JSON output
contract.

User message template:

```text
Pet: {petName}, {petType}, age {petAge} years
Visit date: {visitDate}
Visit notes: "{description}"
```

Unit tests: correct pet age calculation from birthdate, description injection,
null/blank description handling.

**Depends on:** TASK-04
**Blocks:** TASK-10

---

### TASK-06 — VisitSummaryParser

`VisitSummaryParser.java` — parses Claude's raw JSON string into a `VisitSummary`.

Unit tests:

- Happy path (all fields populated)
- Missing optional `followUp` (should be null, not exception)
- Unknown urgency value (map to ROUTINE with a log warning)
- Malformed JSON (throw `VisitSummaryParseException`)
- Tags array: empty, single, multiple

**Depends on:** TASK-04
**Blocks:** TASK-10

---

### TASK-07 — ClaudeApiClient interface

```java
public interface ClaudeApiClient {
    String complete(String systemPrompt, String userMessage);
}
```

Also define the inner request/response model POJOs used for JSON serialization
(`ClaudeRequest`, `ClaudeResponse`, `ContentBlock`).

**Depends on:** TASK-04
**Blocks:** TASK-08, TASK-09, TASK-10

---

### TASK-08 — ClaudeApiClientStub

`ClaudeApiClientStub.java` — `@Component @ConditionalOnProperty(...)` (active
when `anthropic.api.key` is blank). Returns deterministic canned JSON responses
based on description content (e.g. contains "checkup" → routine, contains "limp"
or "pain" → urgent). Ensures the full UI flow works without a real API key.

Unit tests verifying each canned path.

**Depends on:** TASK-07
**Blocks:** TASK-10, TASK-13

---

### TASK-09 — ClaudeApiClientImpl

`ClaudeApiClientImpl.java` — `@Component @ConditionalOnProperty(...)` (active
when `anthropic.api.key` is set). Uses Spring `RestClient` to POST to
`/v1/messages`. Sets `x-api-key`, `anthropic-version`, `Content-Type` headers.
Extracts `content[0].text` from response.

WireMock unit tests:

- 200 with valid JSON content
- 200 with empty content array (throw `ClaudeApiException`)
- 429 rate limit (throw `ClaudeApiException`)
- 503 service unavailable (throw `ClaudeApiException`)
- Network timeout

**Depends on:** TASK-07, TASK-03
**Blocks:** (production key path; stub covers dev/test)

---

### TASK-10 — VisitSummaryService

`VisitSummaryService.java` — `@Service`. Core async orchestration:

```java
@Async("visitSummaryExecutor")
public void generate(Integer visitId) {
    // 1. load Visit + owning Pet from repo
    // 2. set aiStatus = PROCESSING, save
    // 3. build prompt (VisitPromptBuilder)
    // 4. call ClaudeApiClient.complete(...)
    // 5. parse response (VisitSummaryParser)
    // 6. set fields + aiStatus = DONE, save
    // on any exception: set aiStatus = FAILED, save
}
```

Unit tests (Mockito): happy path; client exception → FAILED; parse exception →
FAILED; visit not found → log + return (no exception propagation).

**Depends on:** TASK-05, TASK-06, TASK-07 (interface), TASK-03
**Blocks:** TASK-11, TASK-12

---

### TASK-11 — VisitSummaryController (polling endpoint)

```text
GET /visits/{visitId}/summary
→ 200 { "status": "DONE", "summary": "...", "tags": [...], "urgency": "monitor", "followUp": "..." }
→ 200 { "status": "PENDING" }
→ 404 if visitId not found
```

`@WebMvcTest` tests: PENDING response shape; DONE response shape; FAILED response
shape; 404.

**Depends on:** TASK-10
**Blocks:** TASK-17, TASK-13

---

### TASK-12 — Update VisitController to trigger async generation

In `VisitController.processNewVisitForm()`, after `this.owners.save(owner)`,
extract the saved visit's ID and call `visitSummaryService.generate(visitId)`.

Inject `VisitSummaryService` into `VisitController`.

Add `@MockitoBean VisitSummaryService` to existing `VisitControllerTests` and
verify `generate()` is called exactly once on successful save and not called when
validation fails.

**Depends on:** TASK-10
**Blocks:** TASK-13

---

### TASK-13 — VisitSummaryIntegrationTest (full async flow)

`@SpringBootTest` integration test using the stub client. Sequence:

1. POST a new visit via MockMvc
2. Wait for async completion (poll `/visits/{id}/summary` up to 5s or use
   `CountDownLatch` if stub is instrumented)
3. Assert `aiStatus == DONE` in DB
4. Assert all five AI columns are non-null

Also test the failure path: configure stub to throw, verify `aiStatus == FAILED`.

**Depends on:** TASK-08, TASK-11, TASK-12
**Blocks:** TASK-18

---

### TASK-14 — Health timeline Thymeleaf fragment

`src/main/resources/templates/fragments/health-timeline.html`

Receives a `pet` model object. Renders visits in reverse chronological order.
Each entry:

- Date + urgency badge (color-coded via CSS class)
- Tag chips
- Summary text
- Follow-up note (if present)
- Spinner + "Generating summary…" text when `aiStatus != DONE`
- Error indicator when `aiStatus == FAILED`

Include `data-visit-id` and `data-ai-status` attributes on each entry for JS
polling.

**Depends on:** TASK-02, TASK-04 (data shape)
**Blocks:** TASK-15, TASK-17

---

### TASK-15 — Owner detail page: integrate health timeline

Update `src/main/resources/templates/owners/ownerDetails.html`:

- Include `health-timeline` fragment beneath the existing visits list for each pet
- Add a `▼ Health Timeline` toggle (Bootstrap collapse) per pet section

**Depends on:** TASK-14
**Blocks:** TASK-17, TASK-18

---

### TASK-16 — CSS: urgency badges, tag chips, spinner

Add to `petclinic.css` (or a new `health-timeline.css` included from layout):

- `.urgency-routine` — green badge
- `.urgency-monitor` — amber badge
- `.urgency-urgent` — red badge
- `.health-tag` — pill chip style
- `.ai-spinner` — CSS keyframe spin animation for pending state

**Depends on:** nothing (aesthetic, no Java deps)
**Blocks:** TASK-15 (visual), TASK-18 (E2E assertions on color)

---

### TASK-17 — JS polling for pending summaries

Small vanilla JS block included via Thymeleaf fragment (or inline in
`ownerDetails.html`). On page load:

1. Find all `[data-ai-status="PENDING"], [data-ai-status="PROCESSING"]`
2. For each, start `setInterval` (3 s) hitting `GET /visits/{id}/summary`
3. On `status == DONE`: replace spinner with rendered summary/tags/badge; cancel
   interval
4. On `status == FAILED`: show error message; cancel interval
5. Cancel all intervals when none remain

**Depends on:** TASK-11 (endpoint), TASK-14/15 (DOM structure)
**Blocks:** TASK-18

---

### TASK-18 — Playwright E2E test

New test file `e2e-tests/tests/ai-visit-summary.spec.ts`.

Happy path:

1. Navigate to owner, add a new visit with description "Dog is limping on left
   front leg"
2. Assert redirect back to owner detail page
3. Assert health timeline is visible and contains a spinner for the new visit
4. Wait (up to 10 s) for spinner to be replaced by summary content
5. Assert urgency badge is present and colored
6. Assert at least one tag chip is visible

Failure path (if stub can be toggled to FAILED mode via a test flag):

- Assert error indicator appears, no spinner loops forever

**Depends on:** TASK-13, TASK-15, TASK-16, TASK-17

---

## Dependency Graph

```text
TASK-01 ──► TASK-02 ──► TASK-04 ──► TASK-05 ──┐
                                    TASK-06 ──┤
TASK-03 ──────────────── TASK-07 ──► TASK-08 ──┤
            │                        TASK-09   │
            └────────────────────────────────► TASK-10 ──► TASK-11 ──► TASK-13
                                                       └── TASK-12 ──┘
                                                                        │
TASK-02 ──► TASK-14 ──► TASK-15 ──► TASK-17 ◄── TASK-11               │
TASK-16 ──► TASK-15                                                     │
                                                                        ▼
                                                                     TASK-18
```

---

## Execution Waves (parallel work within each wave)

| Wave | Tasks | Notes |
|---|---|---|
| **0** | TASK-01, TASK-03, TASK-16 | Fully independent; all three can start simultaneously |
| **1** | TASK-02 | Needs TASK-01 |
| **2** | TASK-04 | Needs TASK-02 |
| **3** | TASK-05, TASK-06, TASK-07 | All need TASK-04; fully parallel with each other |
| **4** | TASK-08, TASK-09, TASK-14 | TASK-08/09 need TASK-07; TASK-14 needs TASK-02+04; all three parallel |
| **5** | TASK-10, TASK-15 | TASK-10 needs TASK-05+06+08+03; TASK-15 needs TASK-14; parallel |
| **6** | TASK-11, TASK-12 | Both need TASK-10; parallel with each other |
| **7** | TASK-13, TASK-17 | TASK-13 needs TASK-11+12; TASK-17 needs TASK-11+14+15; parallel |
| **8** | TASK-18 | Needs everything above |

---

## Open Decisions to Resolve Before Implementing

1. **Retry on FAILED** — should there be a manual "Retry" button on the timeline,
   a `@Scheduled` re-queue job, or just log-and-leave? Affects TASK-10 and TASK-17.

2. **Tag storage format** — comma-joined `VARCHAR` is simple but makes SQL
   `WHERE tags CONTAINS` impossible. If tag-based filtering is ever wanted, use
   a separate `visit_ai_tags` join table instead. Affects TASK-01 and TASK-02.

3. ~~**`description` column length**~~ — **Resolved**: extend `description` to
   `VARCHAR(2000)` in TASK-01 alongside the AI columns.

4. ~~**Thread pool rejection policy**~~ — **Resolved**: use `CallerRunsPolicy`.
   Acceptable for expected clinic load; revisit if sustained bursts cause HTTP
   latency issues.
