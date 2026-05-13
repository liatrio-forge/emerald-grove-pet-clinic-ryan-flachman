# Questions: AI Visit Summary E2E (23)

## Resolved

| # | Question | Answer |
|---|----------|--------|
| Q1 | Should the test expand the Bootstrap collapse before asserting on summary content? | Yes — click `▼ Health Timeline` to expand the section, then wait for `data-ai-status="DONE"` and assert the badge and tag chips are visible. Testing via attribute-only (without expanding) would not exercise the UI visible to a real user. |
| Q2 | Should the failure path (stub → FAILED) be covered in this spec? | No. The server-side failure path is fully covered by `VisitSummaryFailureIT` in spec 22. Adding it here would require production code changes to toggle the stub, and would duplicate spec 22's coverage. This spec covers the happy path only. |
| Q3 | Where should the test file live? | `e2e-tests/tests/features/ai-visit-summary.spec.ts` — consistent with all other feature E2E specs. |
| Q4 | Should the spec include urgency-variant tests (e.g. "checkup" → routine)? | No. A single test using description "Dog is limping on left front leg" → URGENT is sufficient. Urgency routing is already tested end-to-end in `VisitSummaryHappyPathIT` (spec 22). The E2E test's unique value is validating the browser-side rendering flow (POST → redirect → collapse expand → spinner → summary), not re-verifying stub routing. |
| Q5 | Which owner and pet to use? | Owner 6 (Jean Coleman), Pet 7 (Samantha). Same fixtures used by spec 22 integration tests. Jean Coleman has only one pet, so the `▼ Health Timeline` toggle button is unambiguous on the page. |
| Q6 | How to locate the specific new visit entry in the health timeline? | Use `data-visit-date` attribute set by the Thymeleaf template. The test computes the same future date it submitted to the form and targets `[data-visit-date="${visitDate}"]`. Since existing sample data visits have historical dates, there is no collision. |
| Q7 | The async generation may complete before Thymeleaf renders the redirect. How should the test handle that? | `expect(entry).toHaveAttribute('data-ai-status', 'DONE', { timeout: 10_000 })` handles both cases: if the render already shows DONE, the assertion passes immediately; if PENDING, the assertion retries until the JS polling updates the attribute. |

## Open

None.
