---
name: supersede-sdd-spec
description: >-
  Replaces an existing SDD spec with a new one when the change is too large
  for amendment. Use when the user asks to supersede, replace, rewrite, or
  obsolete a spec, when a spec's Goal needs to change, or when amend-sdd-spec
  has redirected here. Creates a successor spec, marks the original superseded,
  and cross-references both. Refuses on draft/proposed (just edit them) and on
  already-superseded specs.
---

# Supersede SDD Spec

Replaces a spec wholesale when amendment is no longer sufficient. Produces
a new spec bundle in the normal `create-sdd-spec` shape, marks the original
as `superseded`, and writes bidirectional cross-references so the historical
trail is intact.

This skill exists because some changes can't be honestly captured by
appending to a Revisions table. When the Goal shifts, when an AC's meaning
changes, or when the original plan no longer represents the work being
done — the right move is a fresh spec, not a sixth amendment.

The amend-vs-supersede decision is owned by `amend-sdd-spec` Step 3. This
skill re-applies that decision tree to confirm supersession is actually
warranted before writing anything.

---

## Context-rot marker

Begin every response in this skill with the marker `♻️ SDD-SUPERSEDE`. The
marker is a coarse signal that the skill's instructions are still being
followed. If the marker disappears mid-conversation, context has degraded
and the user should restart the session with fresh context.

The marker goes on the first line of every response, before any other
content.

---

## Step 0: Read project SDD conventions

Read `AGENTS.md` at the repo root for bucket layout, prefix table,
file-naming convention, and sequence width. This skill creates a new spec
bundle and updates the original; both operations need the conventions file.

If no conventions file exists, refuse and direct the user to write one.

---

## Step 1: Identify the spec being superseded

Resolve the target from the user's request:

- Prefixed number (e.g. "supersede `03`" or `"supersede backend-042"`) — open
  directly.
- Description — search bucket READMEs by slug; if multiple match, ask before
  proceeding.

Read the full bundle:

- `NN-spec-<slug>.md` — front-matter, all sections, Revisions
- `NN-tasks-<slug>.md`
- `NN-validation-<slug>.md`
- All amendment docs (`NN-amendment-N-<slug>.md`)
- Parent epic (if any)

State the original's sequence number, slug, status, and amendment count in
your first response.

---

## Step 2: Refuse on incompatible states

Check `status` from the front-matter:

| Status | Action |
|---|---|
| `draft` | Refuse. Drafts are mutable directly — edit the spec via `create-sdd-spec` instead. |
| `proposed` | Refuse. Proposed specs are still in review — edit, don't supersede. |
| `accepted` | Proceed. |
| `in_progress` | Proceed, with caveats (see Step 6 on in-flight work). |
| `delivered` | Proceed, with caveats (see Step 6 on running system drift). |
| `superseded` | Refuse. The spec already has a successor. If that successor is also wrong, supersede *it* instead — do not chain supersessions through retired specs. |

---

## Step 3: Verify supersession is warranted

Walk the decision tree. At least one of these must hold:

| Trigger | Confirmed? |
|---|---|
| The spec's **Goal** is changing. | yes / no |
| An existing AC's **meaning** is changing (not just wording). | yes / no |
| More than 30% of existing tasks are being retired. | yes / no |
| The spec has been amended 4+ times and the structure is no longer reviewable. | yes / no |
| The fundamental approach is changing (different architecture, different contract owner, different bucket). | yes / no |

If **none** hold, stop. The change is probably an amendment — hand off to
`amend-sdd-spec`. State this explicitly; do not guess at which trigger applies.

If **at least one** holds, state which one(s) in your response. The
successor spec's Background section will reference the same trigger.

---

## Step 4: Determine what carries forward

The successor is a fresh spec. AC IDs and task IDs do **not** carry forward —
those start at `AC-1` and `Task 01` in the new spec.

Decide explicitly for each element:

| Element | Default | When to import |
|---|---|---|
| Goal | New | The Goal is what's changing — write fresh. |
| Out of scope | Import | Project-level exclusions usually still apply. Trim what no longer fits. |
| Source excerpts | Import | Domain references typically don't change between supersession events. |
| Conventions | Import | Project rules don't change because a spec changed. |
| Acceptance criteria | New | All AC IDs start fresh. |
| Tasks | New | All task IDs start fresh. |
| Validation commands | Mostly import | The verification block is project-wide; carry it forward and adjust. |
| Coverage matrix | New | Built from the new AC list. The original's matrix stays as historical record. |
| Resolved questions | Import selectively | Decisions that still hold get imported. Re-resolve anything now uncertain. |

State which elements are being imported before writing the successor.

The coverage matrix line bears emphasis: the original's matrix is **not**
imported, even if many of its rows would map to similar new ACs. Importing
the matrix is a tell that supersession was the wrong call.

---

## Step 5: Create the successor spec bundle

The successor follows the normal `create-sdd-spec` workflow with two
differences:

1. The successor's `status` starts at `draft`, same as any new spec.
2. The successor's spec file includes a **Background** section referencing
   the predecessor.
3. The successor's front-matter includes `supersedes:` pointing to the
   original.

### Bucket and prefix

The successor does **not** have to live in the same bucket as the original.
If supersession is happening because the scope has changed, pick the right
bucket per `AGENTS.md` rules — same as any new spec.

State the chosen bucket explicitly. If the bucket changes from the original,
call that out in the Background section.

### Sequence number

Use the next available number in the chosen bucket. This is a fresh spec —
it gets a fresh number. Do not reuse the original's number with a version
suffix.

### Front-matter

```yaml
---
status: draft
created: <YYYY-MM-DD>
last_amended: ~
supersedes: <original>NN
superseded_by: ~
---
```

### Background section

Insert a **Background** section between Goal and Scope:

```markdown
## Background

This spec supersedes [NN](<relative path to original>),
delivered on <date if delivered, else "in <state>"> and superseded on
<today>.

### Why supersession rather than amendment

<One paragraph. Cite the trigger from Step 3. Be concrete — "the Goal
changed from X to Y" beats "requirements evolved".>

### What carries forward

<Bullet list of imported elements per Step 4.>

### Migration impact

State which of the following applies:

- **No migration needed.** Original was `accepted`; no implementation
  has started.
- **In-flight work to reconcile.** Original was `in_progress`; list
  which completed work salvages into the successor and which is
  abandoned.
- **Running system drift.** Original was `delivered`; the running
  system matches the predecessor. Until the successor is implemented,
  the system is in drift. Implementation of the successor closes the
  drift.
```

### Rest of the bundle

Write the remaining files (`NN-questions-1-<slug>.md`,
`NN-tasks-<slug>.md`, `NN-validation-<slug>.md`, `NN-proofs/`) per the
normal `create-sdd-spec` workflow.

The successor's questions doc carries imported decisions in its Resolved
section, with a note that they came from the predecessor:

```markdown
## Resolved

- **Q:** <question>
  **A:** <answer>
  **Source:** Imported from NN questions, still valid as of <today>.
```

### Command cheatsheet (Java/Spring Boot)

All build and verification command examples must use Maven or Gradle:

| Action | Command |
|--------|---------|
| Compile | `./mvnw compile` |
| Lint / style | `./mvnw checkstyle:check` |
| Test | `./mvnw test` |
| Coverage report | `./mvnw test jacoco:report` |
| Full build | `./mvnw package` |

Do not reference `cargo`, `tsc`, `npm run`, or other non-Java tooling in
spec bundles for this project.

---

## Step 6: Mark the original as superseded

Three updates to the original spec file:

### Update front-matter

```yaml
---
status: superseded
created: <unchanged>
last_amended: <YYYY-MM-DD>      # set to today
supersedes: <unchanged>
superseded_by: <new>NN
---
```

### Callout at the top

Insert a callout immediately after the title, before the first section:

```markdown
> **Superseded by [NN](<relative path>).**
> As of <today>, this spec is no longer the active design.
> See the successor for the current intent.
> Reason: <one-line summary of the trigger from Step 3>.
```

### Revisions table entry

Append a final row:

```markdown
| <YYYY-MM-DD> | supersession | Replaced by NN — <one-line reason> | NN-spec-<slug>.md |
```

The original's tasks, validation, and proof files stay unchanged. They
remain readable as historical record. Do not edit them to point at the
successor — that mixes timelines.

Do **not** transition the original's coverage matrix rows to `RETIRED`.
Supersession is a spec-level event; the matrix captures the moment-in-time
snapshot from when the spec was active.

---

## Step 7: Update registries and indexes

### Bucket README (`docs/specs/README.md`)

Two edits:

1. The original's row: change status to `superseded`, append `→ NN` to
   the slug or status note.
2. Add a new row for the successor, status `draft`.

If the successor is in a different bucket, both bucket READMEs get edits.

### Parent epic registry (if applicable)

If the original was a child spec of an epic:

| Original spec | Action |
|---|---|
| Was checked (delivered) | Leave the row, add `[SUPERSEDED → NN]` to the spec column |
| Was unchecked (any earlier state) | Same — mark it as superseded |

Then add a **new row** for the successor in the registry, in the
appropriate phase.

The principle: registry rows are append-only. Supersession events are
recorded by adding entries, not by deleting them.

---

## Step 8: Stop and hand off

Supersession is complete when:

- The successor bundle exists in `draft` state.
- The original carries `status: superseded`, `superseded_by:`, the
  callout, and the final Revisions row.
- `docs/specs/README.md` and any parent epic registry reflect both specs.

Hand off based on the original's status at supersession:

| Original status | Handoff |
|---|---|
| `accepted` | Author iterates on the successor through `create-sdd-spec` until it reaches `accepted`. No further action on the original. |
| `in_progress` | In-flight implementation work needs reconciliation. Notify the implementer. Decide which completed tasks salvage into the successor's task list and update the successor accordingly *before* the successor leaves draft. |
| `delivered` | Running system is in drift. Once the successor reaches `accepted`, queue an implementation pass with `implement-sdd-spec` to close the drift. The original's `superseded` status doesn't change — but the system needs to catch up. |

State which case applies and the next action. Do not invoke other skills
automatically — surface the handoff and let the user decide.

---

## Guiding principles

**Supersession changes identity.** A superseded spec is not the same spec
as its successor. New sequence number, new ACs, new tasks. The relationship
is captured by `supersedes` / `superseded_by` cross-references and by the
Background section, not by retaining structure.

**Registry is append-only.** Supersession events are recorded by adding
entries, never by deleting. The full history should be visible to anyone
reading the registry years later.

**The original stays intact.** Its tasks, validation, and proofs are
unchanged. They are historical artifacts — readable, citable, but no
longer authoritative.

**Background is the audit trail.** The successor's Background section is
where future engineers learn why the original was insufficient. Write it
for someone with no context on the supersession decision.

**Do not chain supersessions through retired specs.** If `03` is superseded
by `07`, and `07` later needs replacing, supersede `07` directly. Never
update `03.superseded_by` after the fact — it falsifies the audit trail.

**Bucket changes are part of the supersession story.** If the successor
moves to a different bucket because the scope has changed, that fact is
worth calling out in the Background section.

**Supersession does not undo past delivery.** A `delivered` spec that gets
superseded was still delivered. The system that runs against the delivered
version is still legitimate code. Supersession says "we no longer want this
to be the design," not "this never happened."

**The matrix is a current-state snapshot, not a global flag.** The
original's coverage matrix stays as it was at the moment of supersession.
The matrix records what was true while the spec was active.

**Status changes here are surgical.** This skill sets exactly one status:
`superseded` on the original. The successor's status starts at `draft` per
the normal create skill.

**TDD applies to implementation, not to spec governance.** When the
successor spec is implemented via `implement-sdd-spec`, every task that
introduces production Java code must be preceded by a failing test (RED
phase). The supersession workflow itself (doc writes, registry updates) is
not subject to TDD.

**The context-rot marker is a coarse signal, not a guarantee.** If the
marker is absent or wrong, treat that as a hard signal that context has
degraded. Stop and surface the issue rather than continuing under
uncertainty.
