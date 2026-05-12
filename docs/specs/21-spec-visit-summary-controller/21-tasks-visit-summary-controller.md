# Tasks: VisitSummaryController (21)

## Task 01 — Write failing VisitSummaryControllerTests (RED)

Covers: AC-8.a

- Create
  `src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryControllerTests.java`
  with the following structure (all tests will fail to compile or run because
  neither `VisitSummaryController` nor `VisitSummaryResponse` exist yet):

  ```java
  @WebMvcTest(VisitSummaryController.class)
  @DisabledInNativeImage
  @DisabledInAotMode
  class VisitSummaryControllerTests {

      @Autowired
      MockMvc mockMvc;

      @MockitoBean
      VisitRepository visitRepository;

      // Test methods listed below
  }
  ```

- Write the following test methods:
  - `getSummaryReturnsPendingWhenStatusIsPending` — stubs `visitRepository.findById(1)`
    to return a `Visit` with `aiStatus == AiStatus.PENDING`; performs
    `GET /visits/1/summary`; asserts HTTP 200 and JSON body
    `{"status":"PENDING"}` with no other keys present.
  - `getSummaryReturnsPendingWhenStatusIsProcessing` — same as above but
    `aiStatus == AiStatus.PROCESSING`; asserts HTTP 200 and body
    `{"status":"PENDING"}`.
  - `getSummaryReturnsDoneResponse` — stubs a visit with `aiStatus == DONE`,
    `aiSummary = "Routine check"`, `aiTags = "wellness,annual"`,
    `aiUrgency = "ROUTINE"`, `aiFollowUp = "Return in 12 months"`; asserts
    HTTP 200 and JSON body contains `"status":"DONE"`, `"summary":"Routine check"`,
    `"tags":["wellness","annual"]`, `"urgency":"routine"`,
    `"followUp":"Return in 12 months"`.
  - `getSummaryReturnsDoneResponseWithNullFollowUp` — same DONE setup but
    `aiFollowUp = null`; asserts HTTP 200 and body does NOT contain the key
    `"followUp"` at all.
  - `getSummaryReturnsDoneResponseWithEmptyTags` — same DONE setup but
    `aiTags = null`; asserts HTTP 200 and `"tags":[]`.
  - `getSummaryReturnsFailedWhenStatusIsFailed` — stubs `aiStatus == FAILED`;
    asserts HTTP 200 and body `{"status":"FAILED"}`.
  - `getSummaryReturns404WhenVisitNotFound` — stubs
    `visitRepository.findById(999)` to return `Optional.empty()`; performs
    `GET /visits/999/summary`; asserts HTTP 404.

- Run `./mvnw test -Dtest=VisitSummaryControllerTests` and capture the failing
  output (compilation error expected). Record in the proof file as RED evidence
  for AC-8.a.

**May break compile, fixed by:** Task 02

**Proof:** `21-proofs/21-task-01-proofs.md`

---

## Task 02 — Create VisitSummaryResponse and VisitSummaryController (GREEN)

Covers: AC-1.a, AC-1.b, AC-2.a, AC-2.b, AC-2.c, AC-3.a, AC-3.b, AC-4.a,
AC-4.b, AC-4.c, AC-5.a, AC-6.a, AC-7.a, AC-7.b

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryResponse.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  import com.fasterxml.jackson.annotation.JsonInclude;
  import java.util.List;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record VisitSummaryResponse(
      String status,
      String summary,
      List<String> tags,
      String urgency,
      String followUp
  ) {}
  ```

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/VisitSummaryController.java`:

  ```java
  package org.springframework.samples.petclinic.owner;

  import java.util.List;
  import java.util.Locale;
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PathVariable;
  import org.springframework.web.bind.annotation.RestController;

  @RestController
  class VisitSummaryController {

      private final VisitRepository visitRepository;

      VisitSummaryController(VisitRepository visitRepository) {
          this.visitRepository = visitRepository;
      }

      @GetMapping("/visits/{visitId}/summary")
      public ResponseEntity<VisitSummaryResponse> getSummary(@PathVariable Integer visitId) {
          return visitRepository.findById(visitId)
              .map(this::toResponse)
              .map(ResponseEntity::ok)
              .orElse(ResponseEntity.notFound().build());
      }

      private VisitSummaryResponse toResponse(Visit visit) {
          AiStatus status = visit.getAiStatus();
          if (status == AiStatus.DONE) {
              List<String> tags = (visit.getAiTags() == null || visit.getAiTags().isBlank())
                  ? List.of()
                  : List.of(visit.getAiTags().split(","));
              String urgency = visit.getAiUrgency() != null
                  ? visit.getAiUrgency().toLowerCase(Locale.ROOT)
                  : null;
              return new VisitSummaryResponse(
                  "DONE",
                  visit.getAiSummary(),
                  tags,
                  urgency,
                  visit.getAiFollowUp()
              );
          }
          if (status == AiStatus.FAILED) {
              return new VisitSummaryResponse("FAILED", null, null, null, null);
          }
          // PENDING and PROCESSING both report as PENDING
          return new VisitSummaryResponse("PENDING", null, null, null, null);
      }
  }
  ```

- Run `./mvnw test -Dtest=VisitSummaryControllerTests` and capture the passing
  output.
- Run structural greps and capture their output:
  - `grep -n "@RestController" src/main/java/.../VisitSummaryController.java`
  - `grep -n "@GetMapping" src/main/java/.../VisitSummaryController.java`
  - `grep -n "@JsonInclude" src/main/java/.../VisitSummaryResponse.java`
- Record all output in the proof file.

**Proof:** `21-proofs/21-task-02-proofs.md`

---

## Task 03 — Validate and capture proof artifacts

Covers: AC-9.a, AC-9.b (all)

- Run `./mvnw test` and capture full output — verifies AC-9.a (zero failures,
  BUILD SUCCESS).
- Run `./mvnw test jacoco:report` and capture the JaCoCo summary for
  `VisitSummaryController` and `VisitSummaryResponse` — verifies AC-9.b (≥ 90%
  line coverage).
- Run all structural grep commands from `21-validation-visit-summary-controller.md`
  and capture their output.
- Confirm every row in the coverage matrix has been updated to `PASS`.
- Update each proof file with real output (no placeholders).

**Proof:** `21-proofs/21-task-03-proofs.md`
