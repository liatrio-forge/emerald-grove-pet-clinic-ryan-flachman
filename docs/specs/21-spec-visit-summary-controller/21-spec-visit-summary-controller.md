---
status: accepted
created: 2026-05-12
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: VisitSummaryController (21)

## Goal

The AI Visit Notes Summarizer epic needs a lightweight polling endpoint so that
the browser's JS poller (TASK-17) and the integration test suite (TASK-13) can
discover whether a visit's AI summary is ready without reloading the full owner
detail page. This spec delivers `VisitSummaryController`, the `@RestController`
that exposes `GET /visits/{visitId}/summary`, reads the visit's AI fields from
the database, and returns a small JSON payload describing the current generation
status. It is the first piece that bridges the async back-end (spec-20) to the
front-end polling loop.

## Scope

### In scope

- `VisitSummaryResponse.java` — `@JsonInclude(NON_NULL)` record that carries the
  JSON wire shape for all three status branches (PENDING, DONE, FAILED).
- `VisitSummaryController.java` — `@RestController` with a single
  `@GetMapping("/visits/{visitId}/summary")` method.
- `VisitSummaryControllerTests.java` — `@WebMvcTest` tests covering all four
  response variants: PENDING, DONE, FAILED, and 404.

### Out of scope

- `VisitSummaryService` — already delivered in spec-20.
- `VisitController` trigger (TASK-12) — separate spec.
- `VisitSummaryIntegrationTest` (TASK-13) — separate spec.
- Any Thymeleaf template or CSS change.
- Retry logic or manual re-trigger for FAILED visits.
- Pagination or filtering of summaries.

## Source excerpts

- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java`
  (spec-14, delivered) — entity with `aiStatus` (`AiStatus` enum), `aiSummary`,
  `aiTags` (comma-joined), `aiUrgency` (uppercase enum name), `aiFollowUp`.
- `src/main/java/org/springframework/samples/petclinic/owner/AiStatus.java`
  (spec-14) — `PENDING`, `PROCESSING`, `DONE`, `FAILED`.
- `src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java`
  (spec-20, delivered) — `Optional<Visit> findById(Integer id)`.
- `docs/epic-ai-visit-summary.md` TASK-11 — canonical description of the polling
  endpoint contract.
- `21-questions-1-visit-summary-controller.md` — all design decisions resolved
  before this spec was written.

## Contract

`GET /visits/{visitId}/summary`

**Response shapes:**

```json
// 200 — PENDING (also returned for PROCESSING status)
{ "status": "PENDING" }

// 200 — DONE
{
  "status": "DONE",
  "summary": "Routine annual wellness exam; all vitals normal.",
  "tags": ["wellness", "annual"],
  "urgency": "routine",
  "followUp": "Return in 12 months for next annual exam."
}

// 200 — DONE with null followUp (field omitted, not present as null)
{
  "status": "DONE",
  "summary": "...",
  "tags": ["limp", "pain"],
  "urgency": "urgent"
}

// 200 — FAILED
{ "status": "FAILED" }

// 404 — visitId not found
(standard Spring ResponseEntity.notFound())
```

**Response DTO (Java):**

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VisitSummaryResponse(
    String status,
    String summary,
    List<String> tags,
    String urgency,
    String followUp
) {}
```

- `urgency` is stored as uppercase in `aiUrgency` (`"ROUTINE"`, `"MONITOR"`, `"URGENT"`);
  the controller converts to lowercase before placing into the DTO.
- `tags` is stored as a comma-joined string in `aiTags`; the controller splits on
  `","` into a `List<String>`. A null or blank `aiTags` maps to `List.of()`.
- `followUp` and `summary` are null for PENDING/FAILED responses; `@JsonInclude(NON_NULL)`
  omits them from the serialized JSON.

## Acceptance criteria

- **AC-1: VisitSummaryResponse DTO**
  - AC-1.a: `VisitSummaryResponse.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryResponse.java`.
  - AC-1.b: The class is annotated `@JsonInclude(JsonInclude.Include.NON_NULL)` —
    verified by grep.

- **AC-2: VisitSummaryController class structure**
  - AC-2.a: `VisitSummaryController.java` exists at
    `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryController.java`.
  - AC-2.b: The class is annotated `@RestController` — verified by grep.
  - AC-2.c: The class declares a `@GetMapping("/visits/{visitId}/summary")` method —
    verified by grep.

- **AC-3: PENDING response shape**
  - AC-3.a: `VisitSummaryControllerTests#getSummaryReturnsPendingWhenStatusIsPending`
    passes: GET `/visits/{id}/summary` where the visit has `aiStatus == PENDING`
    returns HTTP 200 and a JSON body equal to `{"status":"PENDING"}`.
  - AC-3.b: `VisitSummaryControllerTests#getSummaryReturnsPendingWhenStatusIsProcessing`
    passes: same scenario with `aiStatus == PROCESSING` returns HTTP 200 and
    `{"status":"PENDING"}`.

- **AC-4: DONE response shape**
  - AC-4.a: `VisitSummaryControllerTests#getSummaryReturnsDoneResponse` passes:
    GET `/visits/{id}/summary` where the visit has `aiStatus == DONE` returns HTTP
    200 with JSON containing `"status":"DONE"`, `"summary"` (non-null), `"tags"`
    (JSON array), and `"urgency"` as a lowercase string.
  - AC-4.b: The `urgency` value in the JSON response is lowercase — asserted in the
    DONE test (e.g., `"monitor"` not `"MONITOR"`).
  - AC-4.c: `VisitSummaryControllerTests#getSummaryReturnsDoneResponseWithNullFollowUp`
    passes: when `aiFollowUp` is null, the JSON body contains no `"followUp"` key
    at all (not even `"followUp":null`).

- **AC-5: FAILED response shape**
  - AC-5.a: `VisitSummaryControllerTests#getSummaryReturnsFailedWhenStatusIsFailed`
    passes: GET `/visits/{id}/summary` where the visit has `aiStatus == FAILED`
    returns HTTP 200 and `{"status":"FAILED"}`.

- **AC-6: 404 on unknown visitId**
  - AC-6.a: `VisitSummaryControllerTests#getSummaryReturns404WhenVisitNotFound`
    passes: GET `/visits/999/summary` when `VisitRepository.findById(999)` returns
    `Optional.empty()` → HTTP 404.

- **AC-7: tags serialization**
  - AC-7.a: The DONE test (AC-4.a) asserts `tags` is deserialized as a JSON array,
    not a comma-joined string.
  - AC-7.b: `VisitSummaryControllerTests#getSummaryReturnsDoneResponseWithEmptyTags`
    passes: when `aiTags` is null or blank, the DONE response contains `"tags":[]`.

- **AC-8: TDD compliance**
  - AC-8.a: The RED proof artifact captures Maven output showing
    `VisitSummaryControllerTests` failing (compile error or test failure) before
    `VisitSummaryController.java` is created.

- **AC-9: Existing tests remain green**
  - AC-9.a: `./mvnw test` exits 0 with zero test failures after all changes.
  - AC-9.b: New code has ≥ 90% line coverage per JaCoCo on `VisitSummaryController`
    and `VisitSummaryResponse`.

## Conventions

- All new classes live in `org.springframework.samples.petclinic.owner`. No new
  package.
- The controller injects `VisitRepository` by constructor (no `@Autowired` on the
  field).
- `VisitSummaryResponse` is a Java `record`. The five fields are exactly
  `status`, `summary`, `tags`, `urgency`, `followUp`.
- Urgency lowercasing: `visit.getAiUrgency().toLowerCase(Locale.ROOT)`. Guard null:
  if `aiUrgency` is null (should not happen in DONE state but be defensive),
  pass `null` to the DTO and let `@JsonInclude(NON_NULL)` omit it.
- Tags splitting: `visit.getAiTags() == null || visit.getAiTags().isBlank() ? List.of() : List.of(visit.getAiTags().split(","))`.
- Test class uses `@WebMvcTest(VisitSummaryController.class)`, `@MockitoBean
  VisitRepository visitRepository`, and `@Autowired MockMvc mockMvc`.
- Add `@DisabledInNativeImage` and `@DisabledInAotMode` to the test class, matching
  the convention in `OwnerControllerTests` and `VisitControllerTests`.
- PROCESSING and PENDING both map to `"PENDING"` in the JSON `status` field.
- Strict TDD is mandatory: failing test must be captured in the RED proof artifact
  before `VisitSummaryController.java` is created.
- Depends on: spec-14 (`AiStatus` enum, `Visit` entity), spec-20 (`VisitRepository`
  with `findById`).
- Blocks: TASK-17 (JS polling loop), TASK-13 (integration test).

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
