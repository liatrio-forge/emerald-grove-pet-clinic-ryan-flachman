# Tasks: Health Timeline Fragment (19)

## Task 01 — Write failing `HealthTimelineFragmentTest` (RED phase)

Covers: AC-2.a, AC-3.a, AC-3.b, AC-4.a, AC-4.b, AC-4.c, AC-4.d, AC-5.a,
AC-5.b, AC-5.c, AC-5.d, AC-5.e, AC-5.f, AC-5.g, AC-5.h, AC-6.a, AC-6.b

- Create
  `src/test/java/org/springframework/samples/petclinic/owner/HealthTimelineFragmentTest.java`.
- In `@BeforeEach setUp()`, instantiate a `ClassLoaderTemplateResolver` (prefix
  `"templates/"`, suffix `".html"`, mode `TemplateMode.HTML`, encoding
  `"UTF-8"`, cacheable `false`) and wire it into a `SpringTemplateEngine`
  (Spring dialect required for SpEL `T(...)` expressions). Store both as fields.
- Add a private helper `render(Pet pet)` that creates an
  `org.thymeleaf.context.Context`, sets variable `"pet"` to the given pet, and
  calls `templateEngine.process("fragments/health-timeline", context)`.
- Add a private helper `buildPet(Visit... visits)` that constructs a `Pet` with
  a fixed name (`"Buddy"`), adds the given visits via `addVisit()`, and assigns
  sequential integer IDs to each visit (1, 2, …) via the `setId()` method
  inherited from `BaseEntity`.
- Add a private helper `buildVisit(int id, LocalDate date, AiStatus status)`
  that creates a `Visit` with the given date, status, and id; all AI string
  fields default to `null`.
- Write the following test methods (each annotated `@Test`). All must fail with
  a Thymeleaf `TemplateInputException` ("template not found") until
  `health-timeline.html` is created in Task 02:

  | Method | Asserts | AC |
  |--------|---------|----|
  | `shouldRenderVisitsInReverseChronologicalOrder` | `render(pet)` where pet has visit dated `2026-01-01` (id=1) and visit dated `2026-03-15` (id=2) — assert `indexOf("2026-03-15") < indexOf("2026-01-01")` | AC-2.a |
  | `shouldRenderDataVisitIdOnEachEntry` | Single DONE visit with id=7 — assert html `contains("data-visit-id=\"7\"")` | AC-3.a |
  | `shouldRenderDataAiStatusOnEachEntry` | Single PENDING visit — assert html `contains("data-ai-status=\"PENDING\"")` | AC-3.b |
  | `shouldRenderSpinnerForPendingVisit` | Single PENDING visit — assert html `contains("ai-spinner")` and `contains("Generating summary")` | AC-4.a, AC-4.b |
  | `shouldRenderSpinnerForProcessingVisit` | Single PROCESSING visit — assert html `contains("ai-spinner")` and `contains("Generating summary")` | AC-4.c |
  | `shouldNotRenderUrgencyBadgeForPendingOrProcessingVisit` | Single PENDING visit — assert html does not contain `"urgency-routine"`, `"urgency-monitor"`, or `"urgency-urgent"`; repeat with PROCESSING visit | AC-4.d |
  | `shouldRenderDateForDoneVisit` | DONE visit dated `2026-04-10` — assert html `contains("2026-04-10")` | AC-5.a |
  | `shouldRenderRoutineUrgencyBadge` | DONE visit with `aiUrgency="ROUTINE"` — assert html `contains("urgency-routine")` | AC-5.b |
  | `shouldRenderMonitorUrgencyBadge` | DONE visit with `aiUrgency="MONITOR"` — assert html `contains("urgency-monitor")` | AC-5.c |
  | `shouldRenderUrgentUrgencyBadge` | DONE visit with `aiUrgency="URGENT"` — assert html `contains("urgency-urgent")` | AC-5.d |
  | `shouldRenderTwoTagChipsFromCommaSeparatedTags` | DONE visit with `aiTags="diabetes,weight"` — assert `countOccurrencesOf(html, "health-tag") == 2` and `contains("diabetes")` and `contains("weight")` (use `org.springframework.util.StringUtils.countOccurrencesOf`) | AC-5.e |
  | `shouldRenderSummaryText` | DONE visit with `aiSummary="Routine wellness exam"` — assert html `contains("Routine wellness exam")` | AC-5.f |
  | `shouldRenderFollowUpWhenNonNull` | DONE visit with `aiFollowUp="Recheck in 3 months"` — assert html `contains("Recheck in 3 months")` | AC-5.g |
  | `shouldNotRenderFollowUpWhenNull` | DONE visit with `aiFollowUp=null` and `aiSummary="ok"` — assert html does not contain `"follow"` (case-insensitive: use `html.toLowerCase()`) | AC-5.h |
  | `shouldRenderErrorIndicatorForFailedVisit` | Single FAILED visit — assert html `contains("ai-error")` and `contains("Unable to generate summary")` | AC-6.a |
  | `shouldNotRenderSpinnerForFailedVisit` | Single FAILED visit — assert html does not contain `"ai-spinner"` | AC-6.b |

- Run `./mvnw test -Dtest=HealthTimelineFragmentTest`; confirm all tests fail
  with `TemplateInputException` (not a compilation error — the test code must
  compile cleanly).
- Capture the failing output in
  `19-proofs/19-task-01-proofs.md`.

**Proof:** `19-proofs/19-task-01-proofs.md`

---

## Task 02 — Create `health-timeline.html` Thymeleaf fragment (GREEN + REFACTOR)

Covers: AC-1.a, AC-1.b, AC-2.a, AC-3.a, AC-3.b, AC-4.a, AC-4.b, AC-4.c,
AC-4.d, AC-5.a, AC-5.b, AC-5.c, AC-5.d, AC-5.e, AC-5.f, AC-5.g, AC-5.h,
AC-6.a, AC-6.b

- Create
  `src/main/resources/templates/fragments/health-timeline.html`.
- Root element: `<div xmlns:th="https://www.thymeleaf.org"
  th:fragment="healthTimeline">`. A browser-readable, valid HTML5 file.
- Inside the fragment, sort `pet.visits` in reverse chronological order and
  iterate with `th:each`. Implementation strategy is the implementer's choice;
  one viable approach is to sort via
  `${#lists.sort(pet.getVisits().stream().sorted(T(java.util.Comparator).comparing('date').reversed()).collect(T(java.util.stream.Collectors).toList()))}`,
  or to add a `getVisitsSortedDesc()` convenience method to `Pet.java` and
  call that instead.
- Each visit entry is an element (e.g. `<div>`) with:
  - `th:attr="data-visit-id=${visit.id},data-ai-status=${visit.aiStatus}"`
- Inside each entry, use `th:if` / `th:switch` or equivalent to branch on
  `visit.aiStatus`:
  - **PENDING or PROCESSING**: render `<span class="ai-spinner"></span>` and
    the text `Generating summary…`. No urgency badge or tags.
  - **DONE**: render
    - visit date (`th:text="${visit.date}"`)
    - urgency badge: `<span th:class="${'urgency-' +
      visit.aiUrgency.toLowerCase()}" th:text="${visit.aiUrgency}"></span>`
    - tags: `<span th:each="tag : ${#strings.arraySplit(visit.aiTags, ',')}"
      class="health-tag" th:text="${tag.trim()}"></span>` (only when
      `visit.aiTags` is non-null)
    - summary: element with `th:text="${visit.aiSummary}"`
    - follow-up: element with `th:if="${visit.aiFollowUp != null and
      !visit.aiFollowUp.blank}"` and `th:text="${visit.aiFollowUp}"`
  - **FAILED**: render `<div class="ai-error">Unable to generate summary</div>`.
    No spinner.
- Run `./mvnw test -Dtest=HealthTimelineFragmentTest`; confirm all tests pass
  (`BUILD SUCCESS`).
- Run `ls` and `grep` checks per the validation file.
- Capture output in `19-proofs/19-task-02-proofs.md`.

**Proof:** `19-proofs/19-task-02-proofs.md`

---

## Task 03 — Validate and capture proof artifacts

Covers: all (AC-1 through AC-7)

- Run `./mvnw test -Dtest=HealthTimelineFragmentTest`; capture full output.
- Run `./mvnw test`; confirm `BUILD SUCCESS` with no regressions.
- Run `ls src/main/resources/templates/fragments/health-timeline.html`.
- Run `grep -c 'th:fragment="healthTimeline"' src/main/resources/templates/fragments/health-timeline.html`.
- Update every row in the coverage matrix in
  `19-validation-health-timeline-fragment.md` from `PENDING` to `PASS`.
- Tick every item in the Definition of done checklist.
- Capture all command output in `19-proofs/19-task-03-proofs.md`.

**Proof:** `19-proofs/19-task-03-proofs.md`
