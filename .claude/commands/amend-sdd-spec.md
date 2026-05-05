---
name: amend-sdd-spec
description: >-
  Amends an existing SDD spec when requirements evolve. Use when the user asks
  to amend a spec, add a field to a spec, extend a spec, clarify a spec, or
  capture a change discovered during implementation. Refuses on specs in
  draft/proposed (use create-sdd-spec) and superseded (use supersede-sdd-spec).
  For breaking changes that invalidate the spec's identity or goal, redirects
  to supersede-sdd-spec.
---

# Amend SDD Spec

Handles non-breaking evolution of an existing spec: clarifications, additive
changes, and retirements. Produces an amendment doc, updates the original
spec's front-matter and Revisions table, and propagates changes to tasks,
validation, and proofs.

The amendment-vs-supersession decision is the core judgment this skill
enforces. Specs that pass through here remain themselves — same identity,
same goal, same prefixed number. Anything that changes those things goes to
`supersede-sdd-spec`.

---

## Context-rot marker

Begin every response in this skill with the marker `✏️ SDD-AMEND`. The
marker is a coarse signal that the skill's instructions are still being
followed. If the marker disappears mid-conversation, context has degraded
and the user should restart the session with fresh context.

The marker goes on the first line of every response, before any other
content.

---

## Step 0: Read project SDD conventions

Read `AGENTS.md` (or `CLAUDE.md` / `CONVENTIONS.md`) at the repo root. This
skill needs the same fields `create-sdd-spec` extracts: bucket layout,
prefix table, file-naming convention, sequence width.

If no conventions file exists, stop and direct the user to write one before
amending. Amendment without a conventions file is unsafe — the skill cannot
guarantee it's writing files where the rest of the system expects to read
them.

---

## Step 1: Identify the spec to amend

Resolve the target spec from the user's request:

- If the user gives a prefixed number (e.g. "amend `backend-042`"), open
  that bundle directly.
- If the user describes a feature ("amend the proposal review spec"),
  search bucket READMEs by slug. If multiple match, ask before
  proceeding.

Once located, read **all** of:

- `<prefix>NNN-spec-<slug>.md` — front-matter, all sections, full
  Revisions table
- `<prefix>NNN-tasks-<slug>.md` — task list and AC coverage
- `<prefix>NNN-validation-<slug>.md` — DoD list
- All prior amendment docs in the bundle
  (`<prefix>NNN-amendment-1-<slug>.md`, `-2-`, …)

State the spec's current `status` and amendment count in your first
response. The skill's behavior depends on both.

---

## Step 2: Refuse on incompatible states

Check `status` from the front-matter:

| Status | Action |
|---|---|
| `draft` | Refuse. Specs in draft are mutable directly; use `create-sdd-spec` to continue editing. |
| `proposed` | Refuse. Same reason as draft — review feedback is normal editing, not an amendment. |
| `accepted` | Proceed. |
| `in_progress` | Proceed, but note that completed tasks may need re-validation (see Step 7). |
| `delivered` | Proceed, but note that re-implementation may be required (see Step 7). |
| `superseded` | Refuse. The spec is a historical artifact. Amend the successor instead, or use `supersede-sdd-spec` if the successor is also wrong. |

---

## Step 3: Classify the change

Walk this decision tree before writing anything. The classification
determines which flow Step 4 takes.

| Question | Yes → | No → continue |
|---|---|---|
| Is the change purely cosmetic (typo, formatting, broken link)? | **Cosmetic flow** — edit in place, no Revisions entry, no amendment doc. Done. | continue |
| Does the spec's **Goal** section need to change? | **Stop** — this is supersession. Hand off to `supersede-sdd-spec`. | continue |
| Does an existing AC's **meaning** change (not just wording)? | **Stop** — this is supersession. Retiring an AC and adding a new one is acceptable; silently changing what `AC-3` means is not. | continue |
| Does the change retire more than 30% of existing tasks? | **Stop** — supersession. The spec is no longer the same plan. | continue |
| Has the spec already been amended 4 or more times? | Strongly consider supersession. The Revisions table is becoming hard to read; a fresh spec may serve better. Confirm with the user before proceeding. | continue |
| Is this purely a clarification (existing wording was ambiguous, no semantic change)? | **Clarification flow** | continue |
| Does the change introduce new ACs, tasks, or fields without retiring anything? | **Additive flow** | continue |
| Does the change retire an AC without replacing it? | **Retirement flow** | continue |
| None of the above? | Stop. The change does not fit a clean classification. Ask the user to describe the change differently or escalate to supersession. | — |

State the classification explicitly before continuing.

---

## Step 4: Execute the chosen flow

### Cosmetic flow

Edit the affected text in place. No amendment doc, no Revisions row, no
front-matter change. Cosmetic changes do not count as amendments.

If you find yourself wanting to add a Revisions entry for a "cosmetic"
change, the change is not actually cosmetic — reclassify.

### Clarification flow

Goals: capture the original wording for audit, replace it with clearer
text, leave AC IDs unchanged.

1. For each clarified line, capture the original verbatim. The amendment
   doc will record `was: <original>` alongside the new wording.
2. Edit the line in the spec in place. AC IDs do not move.
3. **Update the coverage matrix.** The matrix's `Criterion` column
   carries verbatim AC text — update it to match the new wording. Status
   does not change. If the criterion was previously `PASS`, it stays
   `PASS` (the work satisfies the clarified meaning, since clarification
   is by definition meaning-preserving).
4. Tasks and validation typically need no change. If they do, the change
   is probably additive — reclassify.
5. Continue to Step 5 (write amendment doc) and Step 6 (update spec
   front-matter).

### Additive flow

Goals: add new requirements without disturbing existing ones.

1. Determine the next available AC ID. Walk every AC ID ever used in the
   spec — including retired ones. The next ID is `AC-<max+1>`. **Retired
   IDs are never reused.**
2. Write the new AC text into the spec's Acceptance criteria section.
   Place it after the highest existing top-level ID for readability.
3. If the change introduces new tasks:
   - Determine the next available task number. Same rule as ACs —
     retired tasks do not free their numbers.
   - Append the new task to the tasks file. The validate-and-capture
     task may end up not being the highest-numbered task after
     amendment; that is fine. Task IDs are stable; ordering is
     by-position-in-file, not by-number.
   - Each new task carries a `Covers:` line listing the AC IDs it
     advances toward.
   - Each new task gets a proof stub at
     `<prefix>NNN-proofs/<prefix>NNN-task-NN-proofs.md`.
4. If the change introduces new validation commands, append them to the
   verification block in the validation file. For Java/Spring Boot
   projects, validation commands typically include:

   ```bash
   ./mvnw test -Dtest="<TestClass>"
   ./mvnw verify
   ./mvnw jacoco:report
   ```

5. Update the DoD list in the validation file to include the new AC IDs.
6. **Update the coverage matrix.** Append a new row for each added AC,
   with status `PENDING`. Use the same evidence-type and proof-artifact
   columns as the create skill's template. The matrix order matches the
   AC order in the spec.
7. Continue to Step 5 (write amendment doc) and Step 6 (update spec
   front-matter).

### Retirement flow

Goals: remove a requirement without renumbering.

1. Locate the AC line in the spec. Append `[RETIRED in amendment N]` to
   it. Do not delete the line.
2. If retired AC has sub-criteria (`AC-3.a`, `AC-3.b`), retire them all
   together. Sub-criteria cannot outlive their parent.
3. Tasks that referenced only the retired AC: append `[RETIRED in
   amendment N]` to the task title, do not delete. Their proof artifacts
   stay as historical record.
4. DoD entries for retired ACs: prefix with `~~` strikethrough or append
   `[RETIRED]`. Do not delete.
5. Tasks that referenced the retired AC alongside others: edit the
   `Covers:` line to remove the retired ID. The task itself stays
   active.
6. **Update the coverage matrix.** Change the status column for each
   retired AC's row to `RETIRED`. Do not delete the row. The criterion
   text stays as it was at retirement; do not edit it. If the row had
   `PASS` status before retirement (the criterion was previously met),
   that history is preserved by the proof artifact, not by the matrix.
7. Continue to Step 5 (write amendment doc) and Step 6 (update spec
   front-matter).

---

## Step 5: Write the amendment doc

File: `<prefix>NNN-amendment-N-<slug>.md` where N is the next amendment
number (1 if first amendment, 2 if a prior amendment exists, etc.).

Template:

````markdown
---
amendment_to: <prefix>NNN
amendment_number: N
date: <YYYY-MM-DD>
type: clarification | additive | retirement
---

# Amendment N to <Spec Title> (<prefix>NNN)

## Trigger

One paragraph. What changed in the world, what was discovered during
implementation, or what stakeholder request prompted this amendment?
Be specific — "we found that X" beats "we needed to update Y".

## Classification

- **Type:** clarification | additive | retirement
- **Spec status at amendment:** accepted | in_progress | delivered
- **Affected AC IDs:** <list, or "none">
- **New AC IDs introduced:** <list, or "none">
- **Retired AC IDs:** <list, or "none">
- **New tasks introduced:** <list, or "none">
- **Retired tasks:** <list, or "none">

## Diff

### Acceptance criteria added *(omit if none)*

- **AC-N:** <full text of new criterion>
  - AC-N.a: <sub-criterion>

### Acceptance criteria retired *(omit if none)*

- **AC-M** — retired because <reason>.
  Original wording:
  > <verbatim original text>

### Acceptance criteria clarified *(omit if none)*

- **AC-K** — wording change only, semantic meaning unchanged.
  Was:
  > <verbatim original text>

  Now:
  > <verbatim new text>

### Tasks added *(omit if none)*

- **Task NN:** <title> — covers AC-N.

### Validation changes *(omit if none)*

- New verification commands:
  ```bash
  ./mvnw test -Dtest="<TestClass>"
  ./mvnw jacoco:report
  ```
- DoD additions: AC-N.a, AC-N.b
- DoD retirements: AC-M
- Coverage matrix rows added: AC-N.a, AC-N.b (status: PENDING)
- Coverage matrix rows retired: AC-M (status changed: PASS → RETIRED)
- Coverage matrix criterion text updated: AC-K (clarification only)

## Implementation impact

State which of the following applies and why:

- **No impact.** Spec was `accepted`; no implementation has started; the
  amendment will be picked up when work begins.
- **Active impact.** Spec was `in_progress`; the implementer needs to
  pick up the new requirements. List which completed tasks (if any)
  need re-validation.
- **Re-implementation required.** Spec was `delivered`; the running
  system does not yet meet the amended spec. List which proof artifacts
  need refresh and queue an implementation pass with
  `implement-sdd-spec`.

## Rationale

Why amendment rather than supersession? Confirm explicitly that:

- The Goal is unchanged.
- No existing AC's meaning has shifted (even if wording was clarified).
- More than 70% of existing tasks remain relevant.

If any of these don't hold, stop and use `supersede-sdd-spec`.
````

Keep the amendment doc tight. Diff sections appear only when they apply —
omit empty sections rather than writing "(none)".

---

## Step 6: Update the original spec

Two updates to the spec file:

### Front-matter

```yaml
---
status: <unchanged>
created: <unchanged>
last_amended: <YYYY-MM-DD>      # set to today
supersedes: <unchanged>
superseded_by: <unchanged>
---
```

`status` is **never** changed by this skill. State transitions are owned
by `implement-sdd-spec` and `supersede-sdd-spec`.

### Revisions table

Append a new row at the bottom of the spec's Revisions table:

```markdown
| <YYYY-MM-DD> | clarification \| additive \| retirement | <one-line summary> | <prefix>NNN-amendment-N-<slug>.md |
```

The summary is a single line — no more. Detail belongs in the amendment
doc.

---

## Step 7: Propagate to tasks, validation, and proofs

The Step 4 flow may have produced changes to:

- `<prefix>NNN-tasks-<slug>.md` — new tasks appended, retired tasks
  marked, `Covers:` lines updated
- `<prefix>NNN-validation-<slug>.md` — verification block extended,
  DoD list updated, retired ACs struck through, **coverage matrix
  updated**
- `<prefix>NNN-proofs/` — new proof stubs created for new tasks

Verify after writing:

- Every active AC ID appears in at least one active task's `Covers:`
  line.
- Every active AC ID appears in the DoD list.
- Every active AC ID appears in the coverage matrix with status
  `PENDING` (newly added) or its prior status (unchanged by this
  amendment).
- Every retired AC ID is marked `RETIRED` in the matrix, not deleted.
- Every active task has a proof stub.
- Retired ACs and tasks are marked, not deleted.

If any of these fail, the amendment is incomplete. Fix and re-verify
before stopping.

For Java/Spring Boot tasks, new proof stubs should reference the
relevant test class and method (e.g.
`OwnerControllerTests#testProcessCreationFormSuccess`), the Maven
command that exercises it (`./mvnw test -Dtest="OwnerControllerTests"`),
and the JaCoCo coverage report path
(`target/site/jacoco/index.html`).

---

## Step 8: Update the bucket README

The bucket's `README.md` lists each spec by prefixed number, slug,
status, and last amendment date. Update the row for this spec:

- `last_amended` column → today's date
- amendment count column (if present) → increment

Specs that are currently `delivered` and have just been amended need
attention from the implement skill before they can be considered
re-delivered. The implement skill handles this transition; this skill
does not modify status.

---

## Step 9: Stop and hand off

This skill produces an amendment that is ready for the implement skill
to pick up. Decide handoff based on the spec's `status` at amendment
time:

| Status at amendment | Handoff |
|---|---|
| `accepted` | None. Amendment will be picked up when implementation begins. |
| `in_progress` | Notify the implementer. Re-validate any completed tasks whose `Covers:` AC IDs were affected. |
| `delivered` | Queue an implementation pass with `implement-sdd-spec`. The running system no longer matches the amended spec until that pass runs. |

State which case applies and what the next action is. Do not invoke
other skills automatically — surface the handoff and let the user
decide.

---

## Java/Spring Boot–specific guidance

### Test commands

Use Maven wrapper commands consistently:

```bash
# Run specific test class
./mvnw test -Dtest="<TestClass>"

# Run all tests matching a pattern
./mvnw test -Dtest="*ControllerTests"

# Run with coverage report
./mvnw clean test jacoco:report

# Run integration tests only
./mvnw test -Dtest="*IntegrationTests"

# Full verify (includes integration tests)
./mvnw verify
```

Each new AC that requires a test should specify whether it needs:

- A **unit test** (`@WebMvcTest`, MockMvc, Mockito) for controller/web layer
- An **integration test** (`@DataJpaTest`) for repository/data layer
- A **full stack test** (`@SpringBootTest`) for end-to-end flows
- A **contract/validation test** (Bean Validation, custom validator)

### Minimum coverage requirement

New code added during an additive amendment must meet the project's
coverage threshold (>90% line coverage, 100% branch coverage for
critical business logic). If the amendment doc describes new behavior,
the corresponding tasks must include a JaCoCo verification step.

### TDD compliance

Per project mandate, every new task introduced by an amendment must
follow the Red-Green-Refactor cycle:

1. **RED** — task must produce a failing test before production code
2. **GREEN** — minimal implementation to pass the test
3. **REFACTOR** — improve without breaking tests

State this explicitly in each new task's description in the tasks file.

### Layered architecture alignment

New ACs should identify the layer(s) they touch:

- **Presentation** — Spring MVC controllers, Thymeleaf templates
- **Business** — service classes, validation, business logic
- **Data** — Spring Data JPA repositories, JPA entities

Proof stubs for new tasks should name the expected class or interface,
the package it belongs to (e.g.
`org.springframework.samples.petclinic.owner`), and the test class that
validates it.

---

## Guiding principles

**Amendment preserves identity.** A spec that has been amended is still
the same spec. Same prefixed number, same goal, same purpose. If those
change, it is a different spec — supersede.

**AC IDs and task IDs are sacred.** They are how amendments reference
the spec across time. Renumbering on edit destroys the amendment trail.
Retired IDs are never reused.

**Capture original wording when clarifying.** A clarification that
silently overwrites the original wording is indistinguishable from a
breaking change at audit time. The amendment doc's `was: ...` lines
exist for this.

**Retired things stay in the file.** Retired ACs, retired tasks, retired
DoD entries — all stay, marked. The historical structure is part of
what makes the spec reviewable years later.

**Empty diff sections are deleted, not written as "(none)".** The
amendment doc is not a checklist; it is a record of what changed.

**Soft cap on amendments.** No hard limit, but a spec amended 4+ times
is a candidate for supersession. The structure is asking to be
rewritten.

**Status is never modified by this skill.** Amendment is orthogonal to
lifecycle state. The implement skill owns the `accepted` →
`in_progress` → `delivered` path; the supersede skill owns
`superseded`. Amendment touches `last_amended` only.

**The coverage matrix mirrors the AC list.** Every active AC has a
matrix row; retired ACs keep their rows marked `RETIRED`; clarification
updates the row's criterion text but not its status. The matrix and the
spec's AC list cannot drift — checking they match is part of every
amendment.

**The context-rot marker is a coarse signal, not a guarantee.** If the
marker is absent or wrong, treat that as a hard signal that context has
degraded. Stop and surface the issue rather than continuing under
uncertainty.

**The amendment doc is the audit trail.** Treat it as the durable
artifact — git history will lose context over time, but the amendment
doc explains *why* alongside *what*. Write the Trigger section as if
explaining to a future engineer who has no context.
