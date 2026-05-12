# Questions: VisitController Async Trigger (21) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q1 | How should the controller obtain the saved visit's ID? | Use `visit.getId()` directly. JPA cascade assigns the ID to the visit reference after `owners.save(owner)`, so the same object reference already carries the persisted ID. |
| Q2 | In `@WebMvcTest`, mocked `owners.save()` won't assign real IDs. How should the test verify the correct ID was passed to `generate()`? | Configure `owners.save()` mock via `willAnswer` in `@BeforeEach` to set ID `42` on every visit on the pet with `TEST_PET_ID`, then assert `verify(visitSummaryService).generate(42)`. |
| Q3 | Should `generate()` verification be added to the existing `testProcessNewVisitFormSuccess`, or in a dedicated new test? | New test method `testGenerateCalledOnSuccessfulVisit()`. Keeps redirect-behavior and async-trigger concerns in separate tests. |
| Q4 | Should the controller guard against a null `visit.getId()` after save? | Yes — add `if (visit.getId() != null)` before calling `generate()`. Defensive; can never fire in production (JPA always assigns an ID on persist) but user-approved as policy. No dedicated test for the null-guard path is required since this branch is dead code in practice. |
| Q5 | Which test covers the validation-failure no-call path — the existing `testProcessNewVisitFormHasErrors`, or a new test? | New test `testGenerateNotCalledWhenValidationFails()`. The existing test focuses on the error-message assertion; this new test isolates the async non-trigger concern. |

## Open

None.
