---
name: create-sdd-spec
description: >-
  Creates Spec-Driven Development (SDD) spec bundles. Use when the user asks
  to write a spec, create a spec, start SDD, draft a design doc, or begin
  planning a non-trivial feature or change. For amending existing specs use
  amend-sdd-spec; for replacing them use supersede-sdd-spec; for building
  them use implement-sdd-spec.
---

# Create SDD Spec

Guides the full Spec-Driven Development authoring lifecycle: scope
classification, source-material freezing, spec authoring, task breakdown,
validation plan, and proof stubs.

This skill produces specs in the `draft` or `proposed` lifecycle state. State
transitions to `accepted`, `in_progress`, `delivered`, or `superseded` are
owned by the implement / amend / supersede skills.

---

## Context-rot marker

Begin every response in this skill with the marker `📋 SDD-CREATE`. The
marker is a coarse signal that the skill's instructions are still being
followed. If the marker disappears mid-conversation, context has degraded
and the user should restart the session with fresh context.

The marker goes on the first line of every response, before any other
content.

---

## Step 0: Read project SDD conventions

Before anything else, look for a project conventions file at the repo root,
in this order of precedence:

1. `AGENTS.md`
2. `CLAUDE.md`
3. `CONVENTIONS.md`

Read whichever exists. Extract:

- **Spec location buckets** (e.g. `docs/specs/`, `src/main/specs/`), and
  the **prefix** assigned to each bucket (e.g. `docs-`, `feature-`).
- **Bundle file-naming convention** (default `<prefix>NNN-spec-<slug>.md`).
- **Sequence width** (default `NNN`, 3-digit, zero-padded).
- **Domain reference directories** (e.g. `docs/domain/`) that specs must
  cite instead of ephemeral source directories.
- **Tech stack constraints** (Java version, Spring Boot version, libraries
  already chosen).
- **Source-of-truth declarations** (e.g. "the Spring Boot DTO package is
  the source of truth for wire shapes").
- **Command cheatsheet** (compile, lint, test, build per module).
- **Any project-specific acceptance-criteria patterns.**

If none of those files exist, apply the defaults in this skill and propose
creating an `AGENTS.md` before authoring the first spec.

---

## Step 1: Freeze source material

If the spec will reference material that lives in a directory scheduled for
**deletion, major refactor, or external change** (e.g. a scaffold repo being
migrated, a vendored snapshot, a third-party doc set that can rotate), you
must freeze the relevant excerpts into a durable location **before** writing
the spec.

Material is "ephemeral" if any of:

- The user has stated the directory will be deleted.
- It's a generator/scaffold output (Spring Initializr template,
  auto-generated OpenAPI stubs, etc.).
- It's under `target/`, `.m2/`, `tmp/`, or a fork pending replacement.

Procedure:

1. Identify the minimum excerpts the spec needs (types, DTOs, requirements
   text, state diagrams, screen inventories).
2. Write them into the project's domain-reference directory — typically
   `docs/domain/<topic>.md` — with stable anchors (`## pet-status-enum`,
   `## owner-dto`, …).
3. Reference those stable anchors from the spec's **Source excerpts**
   section. Never reference the ephemeral directory directly.

If `docs/domain/` does not yet exist, create it along with a `README.md`
indexing the frozen material.

This step comes before scope classification because the freeze decision
affects what the spec is allowed to reference, which affects how it gets
scoped.

---

## Step 2: Classify scope

Classify explicitly before writing anything. State the classification in
your first response.

| Classification | Description | Action |
|---|---|---|
| `epic` | Rewriting subsystems, migrating full databases, multi-team initiatives, multi-month efforts | Propose an epic (`<prefix>000-epic-<slug>/`). Split into child specs. See Step 4b. |
| `feature` | One API endpoint, one service method, one DB migration, one user story, one UI component, one screen | Proceed with a full SDD bundle. |
| `chore_bundle` | Several individually trivial chores that are only meaningful together (Checkstyle + SpotBugs config; .gitignore + .editorconfig + .nvmrc) | One bundle, one cohesive goal, bulleted tasks per chore. |
| `direct` | A log statement, a color change, a typo fix | Skip SDD. Do direct implementation with a one-line task note. |

If `epic`, propose the epic split before continuing.

---

## Step 3: Resolve key questions first

Before writing the spec, surface blockers. Produce a short Q&A table covering:

- Exact boundaries (what is in / out of scope?)
- Dependencies on other specs or packages
- Tech constraints (library choices, schema migrations, API compatibility)
- Security / auth implications if any
- For cross-layer work: contract ownership (who defines the
  request/response DTOs?)
- Source of truth for any shape this spec will reference (cite `AGENTS.md`)

Write this as `<prefix>NNN-questions-1-<slug>.md`. The `-1-` is an iteration
counter: if the spec is re-opened after implementation and new questions
arise, append `-2-`, `-3-`, etc. Never overwrite a prior round.

Each file separates **Resolved** from **Open** questions. Resolved questions
become constraints in the spec.

---

## Step 4: Determine location and sequence number

### Location and prefix

Take the bucket list from `AGENTS.md`. Each bucket has a **prefix** that is
applied to every file in the bundle so specs are unambiguously identifiable
in conversation (e.g. "`service-042`" vs. "`web-042`").

Pick the **most specific** bucket. A spec that defines an API endpoint
consumed by a client belongs in the cross-cutting bucket (typically
`docs/specs/` with prefix `docs-`) and is a **contract spec**
(see Step 4c).

### Sequence number

Use the width specified in `AGENTS.md` (default `NNN`, 3-digit,
zero-padded). Sequence is **per-bucket** — each bucket maintains its own
independent counter starting at `001`. Find the current max within the
**target bucket only** and increment. Two specs in different buckets may
share the same number; that is expected. The prefix disambiguates.

### Slug

Short (2–4 words), lowercase, hyphenated, stable. Must match across all
files in the bundle.

### Final bundle layout

```text
<bucket>/<prefix>NNN-spec-<slug>/
├── <prefix>NNN-spec-<slug>.md
├── <prefix>NNN-questions-1-<slug>.md
├── <prefix>NNN-tasks-<slug>.md
├── <prefix>NNN-validation-<slug>.md
└── <prefix>NNN-proofs/
    ├── <prefix>NNN-task-01-proofs.md
    ├── <prefix>NNN-task-02-proofs.md
    └── …
```

---

## Step 5: Write the spec

Use the template below. Omit sections marked *(optional)* when they don't
apply. Keep each section tight — no padding.

````markdown
---
status: draft
created: <YYYY-MM-DD>
last_amended: ~
supersedes: ~
superseded_by: ~
---

# Spec: <Title> (<prefix>NNN)

## Goal

One paragraph. What problem does this solve and why now?

## Scope

### In scope
- Bulleted list of concrete deliverables

### Out of scope
- Explicit exclusions (prevents scope creep during implementation)

## Source excerpts

References to frozen domain material. Use stable anchors, not ephemeral
paths.

- `docs/domain/<topic>.md#<anchor>` — what is cited and why.

## Contract *(optional — cross-layer specs only)*

Request/response DTO shapes, error shape, auth header, event payload. Lives
here when this spec is the **owner** of the contract. Downstream specs
cite it. Source-of-truth declared in `AGENTS.md` governs which
representation is canonical.

```java
// Java interface, DTO record, or OpenAPI fragment
```

## Acceptance criteria

Each criterion has a stable ID (`AC-1`, `AC-2`, `AC-2.a`). IDs never
change across amendments — even if a criterion is deprecated, its ID is
retired, never reused.

Each criterion must answer: how will we know this is done without asking
the author? See the quality bar below.

- **AC-1: <Area>**
  - AC-1.a: <measurable sub-criterion>
  - AC-1.b: <measurable sub-criterion>
- **AC-2: <Area>**
  - AC-2.a: <measurable sub-criterion>

## Conventions

Constraints the implementer must follow. Reference upstream specs by
prefixed number when applicable (e.g. "DTO shape from `docs-004` must
not change").

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|

(Empty until first amendment. Empty revisions sections are a feature —
they signal the spec hasn't drifted.)
````

### Front-matter

Every spec carries the YAML front-matter block. The other lifecycle skills
read these fields:

- `status` — one of `draft`, `proposed`, `accepted`, `in_progress`,
  `delivered`, `superseded`. This skill produces `draft` or `proposed`.
- `created` — date the spec was first written.
- `last_amended` — populated by `amend-sdd-spec`.
- `supersedes` — populated by `supersede-sdd-spec` when this spec replaces
  another.
- `superseded_by` — populated by `supersede-sdd-spec` on the original
  spec when a successor replaces it.

### Acceptance criteria quality bar

Each criterion must be **measurable, specific, bounded, and automated**.

| Quality | Good | Bad |
|---|---|---|
| Measurable | "`./mvnw test` exits 0" | "tests should pass" |
| Specific | "no `<forbidden pattern>` in `src/main/java/`" | "code is clean" |
| Bounded | "all 16 existing tests pass" | "tests pass" |
| Automated | Maven command or grep verifies it | "manually inspect" |

### Acceptance-criteria ID discipline

- IDs are **stable**: `AC-3` always refers to the same criterion across
  every revision of the spec.
- IDs are **never reused**: if `AC-3` is removed by amendment, the
  amendment doc records it as retired and `AC-3` is not assigned to a
  different criterion later.
- Proof artifacts reference criteria by ID, never by ordinal position.
- Sub-criteria use dotted notation (`AC-3.a`, `AC-3.b`).

---

## Step 5b: Epic specs (for `epic` classification)

Epics live at `<bucket>/<prefix>000-epic-<slug>/` and contain only:

- `<prefix>000-epic-<slug>.md` — the epic doc.
- `<prefix>000-questions-1-<slug>.md` — resolved/open questions at epic
  level.

Epic doc template:

````markdown
---
status: draft
created: <YYYY-MM-DD>
last_amended: ~
---

# Epic: <Title> (<prefix>000)

## Goal
One paragraph — what this initiative delivers end-to-end.

## Phases
Ordered list of phases, each a short sentence.

## Child registry

| # | Spec | Phase | Status |
|---|------|-------|--------|
| <prefix>001 | `<prefix>001-spec-<slug>/` | A-1 | [ ] |
| <prefix>002 | `<prefix>002-spec-<slug>/` | A-2 | [ ] |

## Out of scope
Things this epic explicitly does not address.

## Revisions

| Date | Type | Summary | Amendment doc |
|------|------|---------|---------------|
````

Add a new row to the registry every time a child spec is created. The check
mark is filled when the child reaches `delivered` status.

---

## Step 5c: Contract specs (cross-layer)

When a spec owns an interface consumed by multiple layers or services:

- Place it in the cross-cutting bucket from `AGENTS.md`.
- Include the **Contract** section in the spec.
- Acceptance criteria must include a compilation check (the exact command
  lives in `AGENTS.md`; default is `./mvnw compile`).
- Downstream implementation specs cite the contract spec by prefixed
  number and must not redefine the DTO shape.
- The source of truth for the shape is whatever `AGENTS.md` declares — the
  spec follows the project's rule, it does not override it.

---

## Step 6: Write the task breakdown

File: `<prefix>NNN-tasks-<slug>.md`.

Task generation happens in two phases with an explicit human-confirmation
gate between them. This catches scope drift before the full task list is
written, when correction is cheapest.

### Step 6a: Parent tasks only

Generate **parent tasks only** — short imperative titles, no bullets, no
sub-tasks, no proof links yet. Each parent task is a single cohesive unit
of work that maps to roughly one commit.

```markdown
# Tasks: <Title> (<prefix>NNN)

## Parent task plan (awaiting confirmation)

1. <Imperative title> — covers AC-<id>, AC-<id>
2. <Imperative title> — covers AC-<id>
3. …
N. Validate and capture proof artifacts — covers all
```

Then **stop and ask the user to confirm**. State explicitly:

> Parent task plan above. Do these cover the work as you understand it?
> Reply with confirmation, edits to the parent list, or a request to
> rescope before I expand into sub-tasks.

Do not generate sub-tasks until the user has confirmed. The confirmation
gate exists because scope drift discovered at the parent-task stage costs
seconds to fix; scope drift discovered after a 40-bullet task list is
written wastes the entire breakdown.

If the user requests changes, regenerate the parent task plan and ask
again. Iterate until confirmed.

If every AC ID is not covered by at least one parent task, that is a spec
defect — return to Step 5 and fix it before moving on.

### Step 6b: Sub-tasks after confirmation

Once the parent list is confirmed, expand each parent task into the full
form below.

Rules:

- Tasks are ordered — later tasks may depend on earlier ones.
- Each task is a single cohesive unit of work (one commit's worth).
- Each task ends with a `**Proof:** <prefix>NNN-proofs/<prefix>NNN-task-NN-proofs.md`
  line.
- Each task lists the acceptance-criteria IDs it advances toward (`Covers:
  AC-1.a, AC-2`).
- For tasks that legitimately cannot leave the workspace in a compiling
  state — e.g. introducing a Java interface whose implementation lands in
  the next task — the task **must** declare
  `**May break compile, fixed by:** Task NN+1`. Silent compile breakage
  is forbidden.

```markdown
# Tasks: <Title> (<prefix>NNN)

## Task 01 — <Short imperative title>

Covers: AC-1.a, AC-1.b

- Bullet describing exact action
- Bullet describing exact action

**Proof:** <prefix>NNN-proofs/<prefix>NNN-task-01-proofs.md

## Task 02 — <Short imperative title>

Covers: AC-2

…

## Task NN — Validate and capture proof artifacts

Covers: all

- Run `./mvnw test` and capture output.
- Run structural checks from the validation file.
- Confirm each AC ID has at least one passing proof artifact.
- Build the coverage matrix per the validation file template.

**Proof:** <prefix>NNN-proofs/<prefix>NNN-task-NN-proofs.md
```

If the task plan ends without every AC ID covered by at least one task,
that is a spec defect — return to Step 5 and fix it before moving on.

---

## Step 7: Write the validation plan

File: `<prefix>NNN-validation-<slug>.md`.

The verification block contents come from `AGENTS.md`. This skill does not
prescribe specific commands.

Default command cheatsheet for this project (override with `AGENTS.md`):

| Action | Command |
|--------|---------|
| Compile | `./mvnw compile` |
| Lint / style | `./mvnw checkstyle:check` |
| Test | `./mvnw test` |
| Coverage report | `./mvnw test jacoco:report` |
| Full build | `./mvnw package` |

````markdown
# Validation: <Title> (<prefix>NNN)

## Automated verification

From repository root:

```bash
# verification commands per AGENTS.md, ordered by criterion
```

**Expected:** Concrete expected output for each command.

## Traceability

- Feature spec: `<prefix>NNN-spec-<slug>.md`
- Task breakdown: `<prefix>NNN-tasks-<slug>.md`
- Questions and decisions: `<prefix>NNN-questions-1-<slug>.md`
- Per-task evidence: `<prefix>NNN-proofs/<prefix>NNN-task-NN-proofs.md`
- Upstream specs: (list by prefixed number if applicable)
- Parent epic: `<prefix>000-epic-<slug>/<prefix>000-epic-<slug>.md`
  (if any)

## Manual checks (optional)

Steps requiring human judgment that cannot be automated.

## Coverage matrix

The coverage matrix maps every active acceptance criterion to the proof
artifact that demonstrates it, plus the kind of evidence captured. The
implement skill fills the Status column as work progresses.

| AC ID | Criterion | Proof artifact | Evidence type | Status |
|-------|-----------|----------------|---------------|--------|
| AC-1.a | <verbatim from spec> | `<prefix>NNN-proofs/<prefix>NNN-task-NN-proofs.md` | command output | PENDING |
| AC-1.b | <verbatim from spec> | `<prefix>NNN-proofs/<prefix>NNN-task-NN-proofs.md` | Maven test pass | PENDING |
| AC-2.a | <verbatim from spec> | `<prefix>NNN-proofs/<prefix>NNN-task-NN-proofs.md` | JaCoCo coverage | PENDING |

Status values: `PENDING` (not yet evidenced), `PASS` (proof captures real
output that satisfies the criterion), `FAIL` (proof captures evidence
but the evidence does not satisfy the criterion).

Evidence types match the proof formats from `implement-sdd-spec`:
command output, file creation, file edit, Maven test pass, JaCoCo
coverage report, Playwright screenshot, behavioral evidence.

If a criterion needs more than one piece of evidence (e.g. one command
output AND one coverage report), add multiple rows with sub-IDs
(`AC-1.a.i`, `AC-1.a.ii`) — sub-IDs follow the same stability rules as
top-level IDs.

This skill writes the matrix with all rows in `PENDING`. The implement
skill transitions rows to `PASS` or `FAIL`. Retired criteria from
amendments stay in the matrix with status `RETIRED`.

## Definition of done

Each item below references a single acceptance-criterion ID. The DoD list
is the spec's AC list, in order. If the AC list grows by amendment, the
DoD list grows with it. The DoD checklist and the coverage matrix carry
the same information in two formats — the matrix for review at a glance,
the checklist for sequential verification.

- [ ] AC-1.a: <verbatim from spec>
- [ ] AC-1.b: <verbatim from spec>
- [ ] AC-2.a: <verbatim from spec>
- [ ] All proof artifacts contain real outputs, not placeholders.
- [ ] Coverage matrix has all rows in `PASS` (or `RETIRED` for amended specs).
- [ ] `./mvnw test` exits 0 with ≥90% line coverage on new code.
- [ ] Parent epic child-registry checkbox ticked (if applicable).
````

---

## Step 8: Create proof stubs

Create one proof file per task under `<prefix>NNN-proofs/`:

```markdown
# Proofs: Task NN — <Task title>

Covers: AC-1.a, AC-1.b

## Planned evidence

- What file/command output will demonstrate completion?

## Completion notes

(Filled in by `implement-sdd-spec`.)
```

---

## Step 9: Update indexes

1. **Parent epic registry** (if this spec belongs to an epic): add a row
   to the child registry table and leave the checkbox unchecked.
2. **Bucket README** (e.g. `docs/specs/README.md`): if one exists,
   append the spec entry. If none exists, create one with a single-line
   index format.

The implement / amend / supersede skills assume the spec is registered.
Skipping this step makes the spec invisible to downstream lifecycle work,
so it is not optional.

---

## Step 10: Stop

This skill produces a spec in `draft` or `proposed` status. It does not
implement the spec, and it does not transition the spec to `accepted`.

- The reviewer transitions `draft` / `proposed` → `accepted` by editing
  the front-matter directly when the spec is approved.
- `accepted` → `in_progress` is owned by `implement-sdd-spec`.
- `in_progress` → `delivered` is owned by `implement-sdd-spec`.
- Any state → amended is owned by `amend-sdd-spec`.
- Any state → `superseded` is owned by `supersede-sdd-spec`.

Do not modify front-matter beyond what this skill produces.

---

## Guiding principles

**Spec, not design doc.** A spec locks down *what*, not *how*. Leave
implementation decisions to the implementer unless a specific choice is a
constraint (security, compatibility, existing patterns, tech-stack
decisions in `AGENTS.md`).

**Acceptance criteria are the spec.** The Goal and Scope sections exist to
explain context. The acceptance criteria are what gets verified. Write
them first if unsure where to start.

**Out-of-scope is load-bearing.** Explicit exclusions prevent two things:
scope creep during implementation, and re-litigation after the fact.

**Questions before prose.** Unresolved ambiguity left in a spec becomes a
bug report later. Surface and resolve questions before writing acceptance
criteria.

**Proof artifacts are real.** Proof files must contain actual command
output or file listings — not "task complete". The validation file is
the source of truth for what "done" means.

**Specs outlive their sources.** If the material you're porting from will
be deleted, freeze it in `docs/domain/` first.

**Empty revisions sections are a feature.** They signal the spec hasn't
drifted. Treat their presence as a load-bearing structural element, not
boilerplate.

**Stable IDs are load-bearing.** Acceptance-criterion IDs are how
amendments reference the spec across time. Renumbering on edit destroys
amendment traceability.

**Confirm parent tasks before expanding.** Scope drift is cheapest to
catch at the parent-task stage. Generating a 40-bullet sub-task list
before the user has confirmed the parent plan wastes the breakdown when
the plan turns out to be wrong. The Step 6a gate is not optional.

**The context-rot marker is a coarse signal, not a guarantee.** If the
marker is absent or wrong, treat that as a hard signal that context has
degraded. Stop and surface the issue rather than continuing under
uncertainty.

**Project-specific rules live in `AGENTS.md`.** This skill is portable.
If you find yourself wanting to write a project-specific rule into the
skill, put it in `AGENTS.md` instead.

**TDD is mandatory.** This project enforces Strict TDD. Every task that
introduces production code must be preceded by a failing test (RED phase).
Acceptance criteria should reflect this: the test-writing task comes
before the implementation task in the breakdown, and proof artifacts for
implementation tasks must cite a passing test run.
