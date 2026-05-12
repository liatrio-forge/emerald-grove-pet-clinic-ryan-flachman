# Specs Index

All SDD spec bundles for the Emerald Grove Veterinary Clinic project.

| # | Slug | Status | Summary |
|---|------|--------|---------|
| 01 | [playwright-test-suite](01-spec-playwright-test-suite/) | delivered | Playwright E2E test suite infrastructure |
| 01 | [performance-testing-ci](01-spec-performance-testing-ci/) | delivered | Performance testing in CI pipeline |
| 02 | [no-direct-commits-to-main](02-spec-no-direct-commits-to-main/) | delivered | Pre-commit hook blocking direct commits to main |
| 03 | [language-selector](03-spec-language-selector/) | delivered | Bootstrap language dropdown in global navbar |
| 04 | [vet-specialty-filter](04-spec-vet-specialty-filter/) | delivered | Specialty filter pills on the vet directory page |
| 05 | [owner-search-filters](05-spec-owner-search-filters/) | delivered | Find Owners multi-field search by telephone and city |
| 06 | [prevent-duplicate-owner](06-spec-prevent-duplicate-owner/) | accepted | Block duplicate owner creation via firstName + lastName + telephone check |
| 07 | [friendly-404](07-spec-friendly-404/) | accepted | Friendly 404 page for missing owner, pet, and visit resources |
| 08 | [delete-pet](08-spec-delete-pet/) | delivered | Delete a pet from owner with confirmation modal and cascade visit removal |
| 09 | [upcoming-visits](09-spec-upcoming-visits/) | accepted | Read-only page listing all visits within the next N days |
| 10 | [disallow-past-scheduling](10-spec-disallow-past-scheduling/) | accepted | Reject visit dates earlier than today with a localized validation message |
| 11 | [owner-csv-export](11-spec-owner-csv-export/) | delivered | CSV export endpoint for owner search results |
| 11 | [preserve-page-filters](11-spec-preserve-page-filters/) | delivered | Preserve active search filters across pagination links on the Owners list |
| 12 | [ai-visits-schema](12-spec-ai-visits-schema/) | accepted | Add AI columns and extend description to visits table across all four DB variants |
| 13 | [async-config](13-spec-async-config/) | accepted | Async thread pool executor bean and Anthropic API properties for AI visit summarizer |
| 14 | [visit-ai-fields](14-spec-visit-ai-fields/) | delivered | `AiStatus` enum and five AI JPA fields on the `Visit` entity |
| 15 | [visit-summary-dto](15-spec-visit-summary-dto/) | delivered | `VisitUrgency` enum and `VisitSummary` immutable record for parsed Claude AI output |
| 16 | [visit-prompt-builder](16-spec-visit-prompt-builder/) | delivered | `PromptRequest` record and `VisitPromptBuilder` static utility for assembling Claude prompts |
| 17 | [claude-api-client](17-spec-claude-api-client/) | delivered | `ClaudeApiClient` interface and four JSON transport POJOs for the Anthropic Messages API |
| 18 | [claude-api-client-stub](18-spec-claude-api-client-stub/) | delivered | `ClaudeApiClientStub` — deterministic canned-JSON stub active when API key is blank |
| 18 | [claude-api-client-impl](18-spec-claude-api-client-impl/) | delivered | `ClaudeApiClientImpl` — production RestClient HTTP implementation of `ClaudeApiClient` |
| 19 | [claude-api-client-stub](18-spec-claude-api-client-stub/) | delivered | `ClaudeApiClientStub` — deterministic canned-JSON stub active when API key is blank |
| 20 | [owner-detail-health-timeline](20-spec-owner-detail-health-timeline/) | draft | Bootstrap collapse toggle and `healthTimeline` fragment insert on the owner detail page |
| 20 | [visit-summary-service](20-spec-visit-summary-service/) | draft | `VisitSummaryParser` + `VisitSummaryService` — async Claude orchestration and JSON parsing |
| 21 | [visit-controller-trigger](21-spec-visit-controller-trigger/) | draft | Wire `VisitSummaryService` into `VisitController` to trigger async AI generation on every saved visit |
| 21 | [visit-summary-controller](21-spec-visit-summary-controller/) | delivered | `VisitSummaryController` — REST polling endpoint for AI summary status |
| 22 | [js-polling-health-timeline](22-spec-js-polling-health-timeline/) | delivered | Vanilla-JS polling block in `health-timeline.html` that live-updates PENDING entries without a page reload |
| 22 | [visit-summary-integration-test](22-spec-visit-summary-integration-test/) | delivered | Full async flow integration tests: happy path (DONE + urgency routing) and failure path (FAILED) |
