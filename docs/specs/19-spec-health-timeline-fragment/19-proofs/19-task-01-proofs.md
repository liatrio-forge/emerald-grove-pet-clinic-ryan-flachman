# Proofs: Task 01 — Write failing HealthTimelineFragmentTest (RED phase)

Covers: AC-2.a, AC-3.a, AC-3.b, AC-4.a, AC-4.b, AC-4.c, AC-4.d, AC-5.a,
AC-5.b, AC-5.c, AC-5.d, AC-5.e, AC-5.f, AC-5.g, AC-5.h, AC-6.a, AC-6.b

## Planned evidence

- `./mvnw test -Dtest=HealthTimelineFragmentTest` output showing test failures
  with the root cause `TemplateInputException: Error resolving template
  [fragments/health-timeline]` — confirming the tests are wired correctly and
  failing for the right reason.

## Completion notes

`HealthTimelineFragmentTest` was added with `ClassLoaderTemplateResolver`, `SpringTemplateEngine`,
`ReloadableResourceBundleMessageSource` (basename `classpath:messages/messages`), and a `render()`
helper using `Locale.ENGLISH`. Before `health-timeline.html` existed, the suite failed with
`TemplateInputException` (template not found), satisfying the RED gate described in the task list.

## Evidence (`./mvnw test -Dtest=HealthTimelineFragmentTest`, post-GREEN excerpt)

```text
[INFO] Running org.springframework.samples.petclinic.owner.HealthTimelineFragmentTest
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
