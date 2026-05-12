# Validation: Owner Detail — Health Timeline Integration (20)

## Automated verification

From repository root:

```bash
# AC-3.c, AC-4.a — compile the project
./mvnw compile

# AC-3.a, AC-3.b, AC-3.c — run OwnerControllerTests only
./mvnw test -Dtest=OwnerControllerTests

# AC-4.a — full suite, no regressions
./mvnw test

# AC-1.a — confirm th:insert is present in the template
grep -n 'health-timeline :: healthTimeline' \
  src/main/resources/templates/owners/ownerDetails.html

# AC-2.a — confirm collapse toggle text is present
grep -n 'Health Timeline' \
  src/main/resources/templates/owners/ownerDetails.html

# AC-2.b — confirm pet-scoped collapse id expression is present
grep -n "health-timeline.*pet.id" \
  src/main/resources/templates/owners/ownerDetails.html

# AC-2.c — confirm data-bs-target expression is present
grep -n 'data-bs-target' \
  src/main/resources/templates/owners/ownerDetails.html
```

**Expected:**

- `./mvnw compile` — exits 0, no compilation errors.
- `./mvnw test -Dtest=OwnerControllerTests` — exits 0, all test methods pass
  (including the new `testOwnerDetailsContainsHealthTimelineToggle`).
- `./mvnw test` — exits 0, BUILD SUCCESS.
- `grep 'health-timeline :: healthTimeline'` — prints at least one matching line.
- `grep 'Health Timeline'` — prints at least one matching line.
- `grep "health-timeline.*pet.id"` — prints at least one matching line.
- `grep 'data-bs-target'` — prints at least one matching line.

## Traceability

- Feature spec: `20-spec-owner-detail-health-timeline.md`
- Task breakdown: `20-tasks-owner-detail-health-timeline.md`
- Questions and decisions: `20-questions-1-owner-detail-health-timeline.md`
- Per-task evidence: `20-proofs/20-task-01-proofs.md`, `20-proofs/20-task-02-proofs.md`, `20-proofs/20-task-03-proofs.md`
- Upstream specs: spec-19 (`health-timeline-fragment`, must be `delivered`)
- Parent epic: `docs/epic-ai-visit-summary.md` TASK-15

## Manual checks

- Load `http://localhost:8080/owners/1` in a browser and confirm the
  `▼ Health Timeline` toggle appears under each pet's visits table.
- Click the toggle and confirm the panel expands and contains spinner or
  summary content (depending on AI job state).
- Confirm that two pets on the same owner page each have independently
  collapsible panels (clicking one does not toggle the other).

## Coverage matrix

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | `ownerDetails.html` contains `th:insert="~{fragments/health-timeline :: healthTimeline}"` inside the pet loop | `20-proofs/20-task-02-proofs.md` | file edit | PENDING |
| AC-1.b | Insert is after inner visits/actions table, within same `<td>` | `20-proofs/20-task-02-proofs.md` | file edit | PENDING |
| AC-2.a | Each pet section renders an element with `data-bs-toggle="collapse"` and text containing `Health Timeline` | `20-proofs/20-task-02-proofs.md` | file edit | PENDING |
| AC-2.b | Collapsible div uses `th:id="'health-timeline-' + ${pet.id}"` | `20-proofs/20-task-02-proofs.md` | file edit | PENDING |
| AC-2.c | Toggle element's `data-bs-target` matches `#health-timeline-{petId}` | `20-proofs/20-task-02-proofs.md` | file edit | PENDING |
| AC-3.a | New test asserts response contains `data-bs-toggle="collapse"` | `20-proofs/20-task-01-proofs.md` | Maven test pass | PENDING |
| AC-3.b | New test asserts response contains text `Health Timeline` | `20-proofs/20-task-01-proofs.md` | Maven test pass | PENDING |
| AC-3.c | `./mvnw test -Dtest=OwnerControllerTests` exits 0 | `20-proofs/20-task-03-proofs.md` | command output | PENDING |
| AC-4.a | `./mvnw test` exits 0, no regressions | `20-proofs/20-task-03-proofs.md` | command output | PENDING |

## Definition of done

- [ ] AC-1.a: `ownerDetails.html` contains `th:insert="~{fragments/health-timeline :: healthTimeline}"` inside the pet loop.
- [ ] AC-1.b: The insert is positioned after the inner visits/actions table, within the same right-hand `<td>`.
- [ ] AC-2.a: Each pet section renders an element with `data-bs-toggle="collapse"` whose visible text contains `Health Timeline`.
- [ ] AC-2.b: The collapsible `<div>` uses `th:id="'health-timeline-' + ${pet.id}"`.
- [ ] AC-2.c: The toggle element's `data-bs-target` attribute equals `#health-timeline-{petId}`.
- [ ] AC-3.a: New test method asserts response contains `data-bs-toggle="collapse"`.
- [ ] AC-3.b: New test method asserts response contains text `Health Timeline`.
- [ ] AC-3.c: `./mvnw test -Dtest=OwnerControllerTests` exits 0.
- [ ] AC-4.a: `./mvnw test` exits 0, no regressions.
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS` (or `RETIRED` for amended specs).
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
- [ ] Parent epic child-registry checkbox ticked (not applicable — no epic doc maintained separately).
