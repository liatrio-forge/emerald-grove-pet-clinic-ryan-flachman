---
iteration: 1
created: 2026-05-12
---

# Questions: Health Timeline Fragment (19)

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | Does the fragment receive a `Pet` object or a pre-sorted list of visits? | `Pet` object (from epic TASK-14). The fragment is responsible for ordering. |
| Q-2 | How is `aiUrgency` stored — as a typed enum or a plain string? | Plain `String` in `Visit.java` (spec-14, delivered). Values are uppercase: `"ROUTINE"`, `"MONITOR"`, `"URGENT"`. The CSS class is the lowercase form, e.g. `urgency-routine`. |
| Q-3 | How are tags stored? | Comma-joined `VARCHAR` on `Visit.aiTags` (e.g. `"diabetes,weight"`). The fragment splits and renders each as a chip. |
| Q-4 | Does the fragment directly use `VisitSummary` DTO (TASK-04)? | No. The template reads AI fields directly from the `Visit` entity (`aiStatus`, `aiSummary`, `aiTags`, `aiUrgency`, `aiFollowUp`). TASK-04 is a service-layer concern; the template is unaffected by it. |
| Q-5 | What CSS classes are in scope for this fragment? | `.urgency-routine`, `.urgency-monitor`, `.urgency-urgent`, `.health-tag`, `.ai-spinner` (from epic TASK-16). The error-state class `ai-error` is introduced by this spec to provide a stable hook for TASK-17 polling. |
| Q-6 | What Thymeleaf fragment name and parameter convention? | Fragment name `healthTimeline`; context variable `pet` supplied by the calling template. No fragment-parameter syntax needed — the fragment reads `${pet}` from the model context. |
| Q-7 | How do we test a bare Thymeleaf fragment without a Spring context? | Use `ClassLoaderTemplateResolver` + `SpringTemplateEngine` directly in a JUnit 5 unit test (`HealthTimelineFragmentTest`). No `@SpringBootTest` or `@WebMvcTest` needed. |
| Q-8 | Is TASK-04 (VisitSummary DTO) a hard prerequisite for this spec? | No. The fragment reads `Visit` entity fields (spec-14, delivered). TASK-04 is listed in the epic as a dependency for "data shape" awareness, but no TASK-04 Java class is referenced at runtime by this template. |
| Q-9 | What text should appear in the FAILED state? | "Unable to generate summary" — deterministic and testable. |

## Open

None — all questions resolved before spec authoring.
