# Proofs: Task 01 — Create failing Playwright polling tests and fixture HTML (RED)

Covers: AC-9.a

## Command

```bash
cd e2e-tests && npm test -- --grep "health-timeline polling"
```

## Result (RED)

With `health-timeline-fixture.html` containing no `<script>` block and `health-timeline.html` unchanged, all 14 polling tests failed. Representative failures:

- `initialises intervals for all PENDING entries on load`: `expect(received).toBe(expected)` with `Received: 0`, `Expected: 2` (no `fetch` to `/visits/*/summary` without the poller).
- `replaces spinner with summary HTML on DONE response`: `expect(locator).toHaveCount(expected)` — spinner still present after advancing the fake clock.
- Several tests failed with `TypeError: page.clock.tick is not a function` until the suite was updated to Playwright’s `page.clock.runFor()` API (implementation note, not a spec change).

This satisfies AC-9.a: Playwright tests existed and failed before the inline `<script>` was added to `health-timeline.html`.
