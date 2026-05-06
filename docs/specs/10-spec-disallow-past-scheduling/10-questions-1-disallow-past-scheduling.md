# Questions: Disallow Past Scheduling (10)

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q1 | **Implementation approach**: Bean Validation `@FutureOrPresent` annotation on `Visit.date`, or a custom Spring `Validator` like `PetValidator`? | **Custom Spring `VisitValidator`** — consistent with `PetValidator` in this codebase; gives full control over the error code and default message without fighting Jakarta annotation message resolution. |
| Q2 | **Null/missing date**: If the user clears the date field before submitting, what should happen? | Reject with error code `required`, default message `"required"` — matches the `PetValidator` pattern for missing required fields. The `Visit()` constructor defaults to today, so a blank field is a deliberate action. |
| Q3 | **Validation message text**: What exact message should be displayed? | Key `visit.date.pastNotAllowed`, English default `"must be today or in the future"`. The key must be present in `messages.properties` (base) and all seven non-English locale files (`de`, `es`, `fa`, `ko`, `pt`, `ru`, `tr`) to satisfy `I18nPropertiesSyncTest`. `messages_en.properties` is exempt (falls back to base per project convention). |
| Q4 | **Existing Playwright E2E test**: `visit-scheduling.spec.ts` hardcodes past date `2024-02-02`. After the validator lands that test will fail. | Update the date in the *success path* test to a dynamically computed future date (e.g. one year from today using `new Date()` arithmetic) before the validator implementation task, so the existing test stays green throughout. |
| Q5 | **i18n scope**: Which locale files need the new key? | All files that `I18nPropertiesSyncTest.checkI18nPropertyFilesAreInSync` checks: every file matching `messages*.properties` EXCEPT `messages.properties` (base) and `messages_en.properties`. That is: `de`, `es`, `fa`, `ko`, `pt`, `ru`, `tr`. Machine translations are acceptable; the sync test only checks key presence. |
| Q6 | **Validation layer**: Should the rule live in `VisitValidator` only, or also in `Visit` entity via Bean Validation? | `VisitValidator` only. The `Visit` entity already uses `@NotBlank` for description; no additional annotations needed. Adding a custom annotation to the entity would require a separate annotation class and is out of scope per "keep it simple." |
| Q7 | **Controller wiring**: How should `VisitValidator` be registered? | Via `@InitBinder` in `VisitController`, same mechanism `PetController` uses for `PetValidator`. The validator is added with `dataBinder.addValidators(new VisitValidator())`. |

## Open

None.
