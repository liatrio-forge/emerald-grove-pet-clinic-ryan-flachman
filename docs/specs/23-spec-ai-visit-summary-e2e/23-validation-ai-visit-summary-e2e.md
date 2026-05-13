# Validation: AI Visit Summary E2E (23)

## Automated verification

From repository root:

```bash
# 1. Verify the test file exists
ls e2e-tests/tests/features/ai-visit-summary.spec.ts

# 2. Run the scoped E2E suite (requires app running or auto-started by Playwright)
cd e2e-tests && npm test -- --grep "AI Visit Summary"
```

**Expected:**

1. `ls` exits 0 with no error.
2. `npm test` exits 0; reporter output contains `1 passed` and `0 failed` for
   the `"AI Visit Summary"` describe block.

## Traceability

- Feature spec: `23-spec-ai-visit-summary-e2e.md`
- Task breakdown: `23-tasks-ai-visit-summary-e2e.md`
- Questions and decisions: `23-questions-1-ai-visit-summary-e2e.md`
- Per-task evidence: `23-proofs/23-task-01-proofs.md`, `23-proofs/23-task-02-proofs.md`
- Upstream specs:
  - `22-spec-visit-summary-integration-test` — integration-level async flow
  - `22-spec-js-polling-health-timeline` — JS polling behaviour and DOM classes
  - `21-spec-visit-summary-controller` — `GET /visits/{id}/summary` endpoint
  - `20-spec-owner-detail-health-timeline` — Bootstrap collapse toggle DOM structure
  - `18-spec-claude-api-client-stub` — stub keyword routing ("limp" → URGENT)

## Manual checks

- Open the owner detail page for owner 6 in a browser after a new visit with
  description "Dog is limping" is submitted. Verify the health timeline expands
  and shows a red `urgency-urgent` badge alongside at least one tag chip, with
  no spinning indicator remaining.

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `ai-visit-summary.spec.ts` exists with correct describe/test names | `23-proofs/23-task-01-proofs.md` | file creation | PENDING |
| AC-2.a | Test submits visit for owner 6 / pet 7 with "limp" description | `23-proofs/23-task-01-proofs.md` | file edit | PENDING |
| AC-2.b | URL is `/owners/6`, "Owner Information" heading visible after submit | `23-proofs/23-task-02-proofs.md` | Playwright test pass | PENDING |
| AC-3.a | Entry with matching visit date reaches `data-ai-status="DONE"` within 10 s | `23-proofs/23-task-02-proofs.md` | Playwright test pass | PENDING |
| AC-3.b | `.urgency-urgent` element visible within DONE entry | `23-proofs/23-task-02-proofs.md` | Playwright test pass | PENDING |
| AC-3.c | ≥ 1 `.health-tag` element visible within DONE entry | `23-proofs/23-task-02-proofs.md` | Playwright test pass | PENDING |
| AC-3.d | `span.ai-spinner` count is 0 within DONE entry | `23-proofs/23-task-02-proofs.md` | Playwright test pass | PENDING |
| AC-4.a | `npm test -- --grep "AI Visit Summary"` exits 0, 1 passed | `23-proofs/23-task-02-proofs.md` | command output | PENDING |

## Definition of done

- [ ] AC-1.a: `e2e-tests/tests/features/ai-visit-summary.spec.ts` exists and contains correct describe/test names.
- [ ] AC-2.a: Test submits a visit for owner 6 / pet 7 with description `"Dog is limping on left front leg"`.
- [ ] AC-2.b: After submit, URL is `/owners/6` and "Owner Information" heading is visible.
- [ ] AC-3.a: Entry with matching visit date reaches `data-ai-status="DONE"` within 10 s of expanding the timeline.
- [ ] AC-3.b: `.urgency-urgent` element is visible within the DONE entry.
- [ ] AC-3.c: At least one `.health-tag` element is visible within the DONE entry.
- [ ] AC-3.d: `span.ai-spinner` count is 0 within the DONE entry.
- [ ] AC-4.a: `npm test -- --grep "AI Visit Summary"` exits 0 with 1 passed, 0 failed.
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS`.
