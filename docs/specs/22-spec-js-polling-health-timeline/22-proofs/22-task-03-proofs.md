# Proofs: Task 03 — Validate and capture proof artifacts

Covers: AC-10.a, AC-10.b

## `./mvnw test`

```bash
./mvnw test -q
```

Completed with **exit code 0** (no test failures) after:

- Adding `data-visit-date` and the inline polling script to `health-timeline.html`
- Adjusting `HealthTimelineFragmentTest` to exclude the `<script>` block from assertions that count `health-tag` or scan for the substring `follow`
- Adding `shouldRenderDataVisitDateOnEachEntry` for the new attribute

## Playwright polling slice

```bash
cd e2e-tests && npm test -- --grep "health-timeline polling"
```

Completed with **exit code 0**; **14** polling scenarios passed.

## Coverage matrix

All rows in `22-validation-js-polling-health-timeline.md` are updated to **PASS**.
