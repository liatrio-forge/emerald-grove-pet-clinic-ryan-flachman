---
name: implement-sdd-spec
description: >-
  Drives an accepted SDD spec through implementation to delivery. Use when the
  user asks to implement a spec, build a spec, start work on a spec, resume
  work on an in-progress spec, or close drift on a delivered spec that has
  pending amendments. Walks tasks in order, produces code, fills proof
  artifacts with real outputs, runs the validation block, and transitions
  status from accepted → in_progress → delivered. Refuses on draft, proposed,
  and superseded specs.
---

# Implement SDD Spec

Drives a spec from `accepted` to `delivered` by walking tasks, producing code,
filling proof artifacts with real evidence, and running validation. Owns the
forward-moving lifecycle transitions. Re-engages on `delivered` specs whose
running system has drifted from amendments.

This is the only skill that produces code as output. The other three skills
produce specs and amendments. Discipline matters more here than anywhere
else: the proofs this skill writes become the audit trail that says "the
running system actually matches the spec."

---

## Context-rot marker

Begin every response in this skill with the marker `🔨 SDD-IMPLEMENT`. The
marker is a coarse signal that the skill's instructions are still being
followed. If the marker disappears mid-conversation, context has degraded
and the user should restart the session with fresh context.

The marker goes on the first line of every response, before any other
content. This skill runs longer than the others — multi-task implementation
sessions accumulate context fast — so the marker discipline matters more
here.

---

## Step 0: Read project SDD conventions

Read `AGENTS.md` (or `CLAUDE.md` / `CONVENTIONS.md`) at the repo root.
Extract:

- **Verification commands** for each kind of work (Maven lifecycle, test
  commands, coverage). The validation file's verification block uses these.
- **Compile command** per module. Each task ends by running this.
- **Source-of-truth rules** that determine where types are canonical
  (e.g. "domain entities live in the `model` package").
- **Determinism / banned-pattern rules** that the spec's ACs may
  reference.
- **Bucket and prefix table** to locate the spec.
- **Git commit conventions** if defined (commit-message format,
  prefix style, sign-off requirements). The skill commits at the end
  of each task; the format follows `AGENTS.md`. If `AGENTS.md` says
  nothing about commit format, default to
  `<prefix>NNN/Task NN: <task title>` as the commit subject.

If the conventions file does not exist, refuse. Implementation without
project conventions is unsafe — the verification commands and
source-of-truth rules cannot be guessed.

---

## Step 1: Identify and read the spec

Resolve the target from the user's request:

- Prefixed number (e.g. "implement `backend-042`") — open directly.
- Description — search bucket READMEs by slug; if multiple match, ask
  before proceeding.

Read the full bundle:

- `<prefix>NNN-spec-<slug>.md` — front-matter, all sections, Revisions
- `<prefix>NNN-tasks-<slug>.md` — task list with `Covers:` lines
- `<prefix>NNN-validation-<slug>.md` — verification block, DoD list
- All proof files under `<prefix>NNN-proofs/`
- All amendment docs (`<prefix>NNN-amendment-N-<slug>.md`)

State the spec's prefixed number, slug, status, amendment count, and
proof completion in your first response. The skill's behavior depends
on all four.

---

## Step 2: Refuse on incompatible states

Check `status` from the front-matter:

| Status | Action |
|---|---|
| `draft` | Refuse. The spec is not ready to build. Use `create-sdd-spec` to finish authoring. |
| `proposed` | Refuse. The spec is in review. Wait for it to be `accepted`. |
| `accepted` | Proceed (fresh implementation flow — Step 3.A). |
| `in_progress` | Proceed (resumption flow — Step 3.B). |
| `delivered` | Conditionally proceed. Check for unresolved amendments (Step 3.C). |
| `superseded` | Refuse. The spec is a historical artifact. Implement the successor instead. |

---

## Step 3: Determine work scope

The flow depends on the spec's current state and what's already been
done.

### 3.A — Fresh implementation (status: `accepted`)

All tasks are unstarted. All proofs are stubs. All ACs need work. State
this in your response and proceed to Step 4.

### 3.B — Resumption (status: `in_progress`)

Walk every proof file. A proof is **complete** if its
`## Completion notes` section contains real output (command logs, file
diffs, test output, etc.) — not the placeholder
`(Filled in by implement-sdd-spec.)`.

For each task, classify as `done` or `pending`. Pick up from the first
`pending` task. State the resume point in your response: "Resuming at
Task NN. Tasks 01–\<NN-1\> have completed proofs."

If no proofs are complete, the spec was likely transitioned to
`in_progress` without work starting. Treat as fresh implementation
(3.A).

### 3.C — Amendment re-engagement (status: `delivered`)

A `delivered` spec means the running system previously matched the
spec. If amendments have been added since delivery, the running system
is in drift.

Detect drift by comparing:

- The spec's current AC list (from the spec file) — including any added
  by amendment, and excluding any retired
- The set of AC IDs covered by completed proof artifacts

If every active AC ID has a completed proof covering it, there is no
drift. Refuse: the spec is delivered and current, nothing to do.

If active AC IDs lack proofs, drift exists. Identify the affected
tasks:

- Tasks added by amendment (new task numbers) → fresh work
- Tasks listed in an amendment's "Implementation impact" → re-validation

State the drift scope explicitly: "Spec was delivered on \<date\>.
Amendment N introduced AC-\<list\> and Task \<list\>; these need fresh
work. Tasks \<list\> need re-validation per the amendment's
implementation impact."

Then transition status `delivered → in_progress` for the duration of
re-engagement. Status returns to `delivered` once drift closes.

---

## Step 3.5: Select checkpoint mode

Before transitioning state, ask the user how often the skill should
stop and let the user review.

State the question explicitly:

> This spec has \<N\> tasks. Pick a checkpoint mode:
>
> - **`task`** (default) — stop after each task for review.
> - **`continuous`** — run all tasks without stopping; review at the
>   end. Best for short specs (≤5 tasks) or trusted refactors.
> - **`batch <N>`** — stop after every N tasks. Best for long specs
>   where per-task review is too granular but end-of-run review is
>   too coarse.

Default to `task` if the user does not specify. State the chosen mode
in your response and announce it again at each stopping point so the
user knows what to expect.

Mode selection rules:

- **Skill always stops on failure** regardless of mode. Compile
  breaks, test failures, ambiguity, and AC-can't-be-satisfied
  conditions halt the skill and surface to the user.
- **Skill always stops at the validate-and-capture task.** Even in
  `continuous` mode, the validation block runs as its own checkpoint
  with explicit confirmation before transitioning to `delivered`.
- **Mode can be changed mid-run.** If a `continuous` run feels
  uncomfortable, the user can interrupt and request `task` mode for
  remaining work. Capture the mode change in the next task's proof
  notes.
- **Amendment re-engagement defaults to `task` mode.** Re-validating
  completed work after an amendment is high-stakes; the default
  reflects that.

---

## Step 4: Transition to `in_progress`

Update the spec's front-matter:

```yaml
---
status: in_progress
created: <unchanged>
last_amended: <unchanged>
supersedes: <unchanged>
superseded_by: <unchanged>
---
```

State the transition explicitly: "Setting status to `in_progress`.
Beginning Task \<NN\>." This is the audit signal that work has started.

For amendment re-engagement (3.C), the transition is from `delivered`,
not from `accepted`. State this: "Re-engaging on amendments.
Transitioning `delivered → in_progress`."

Do not transition status until you are about to start the first task.
Reading the spec without committing to work should not change state.

---

## Step 5: Walk tasks in order

For each task that needs work, in the order the tasks file lists them:

### 5.1 — Announce the task

State the task number, title, and the AC IDs it covers:

> Starting Task 03: Implement owner search by telephone.
> Covers: AC-2.a, AC-2.b.

### 5.2 — Read and execute

Read the task's bullets. Do exactly what they say. The bullets are the
implementation contract — if a bullet says "in
`src/main/java/org/springframework/samples/petclinic/owner/`," that is
where the code goes.

If a bullet is ambiguous, stop. Do not guess. Surface the ambiguity to
the user with a specific question. The amend skill exists for the case
where the spec is genuinely unclear; do not silently interpret.

### 5.3 — Compile / test at the boundary

Run `./mvnw compile` (or the project's equivalent from `AGENTS.md`)
after the task's work is complete. The command must exit 0 before
moving to the next task.

For tasks that add new behavior, also run the relevant test class:

```bash
./mvnw test -Dtest="<TestClass>"
```

The exception: tasks flagged `**May break compile, fixed by:** Task NN+1`.
For these:

- Run the compile command anyway and capture the failure output.
- Record in the proof: "Compile fails as expected; will be fixed by
  Task NN+1." Include the failure output.
- Proceed to Task NN+1.
- After Task NN+1, run compile again. It must exit 0. If it does not,
  stop — Task NN+1 did not actually fix the breakage.

### 5.4 — Fill the proof artifact

See Step 6.

### 5.5 — Update the coverage matrix

Open the validation file. Walk every AC ID this task covers (`Covers:`
line). For each:

- If the proof captures evidence that satisfies the criterion, change
  the matrix row's status from `PENDING` to `PASS`.
- If the proof captures evidence that *attempts* the criterion but
  fails (test runs but fails, command exits non-zero), change the
  status to `FAIL`. **Do not transition status to `delivered` while
  any active row is `FAIL`** — see Step 7's failure mode.
- If the proof contains no evidence for the criterion, leave the row
  at `PENDING`. The task did not satisfy the criterion even if it
  ran. Surface this to the user; the task may need re-running.

The matrix is the at-a-glance status of the spec. It must reflect the
current state of the proofs after every task.

### 5.6 — Commit the work

Stage and commit the work performed in this task. The commit message
follows project conventions per `AGENTS.md`. If the project does not
specify a format, default to:

```text
<prefix>NNN/Task NN: <task title>

Covers: AC-<id>, AC-<id>
Proof: <prefix>NNN-proofs/<prefix>NNN-task-NN-proofs.md
```

The audit trail goes commit → task → proof → AC → spec. Skipping the
commit step breaks the chain — every task must end with a commit,
even small ones. If a task produced no code changes (e.g. a
validation-only task), commit the proof file alone with the same
format.

For tasks flagged "may break compile, fixed by Task NN+1": commit
anyway. The commit message body should note the deferred fix:
"Compile breaks; Task NN+1 restores."

If the project requires PR-only workflow (no direct commits to main),
the commit goes to the working branch the implement run is happening
on. The skill does not push or open PRs unless the user explicitly
asks.

### 5.7 — Checkpoint per chosen mode

Apply the checkpoint mode selected in Step 3.5:

- **`task` mode** — stop after this task. State: "Task NN complete.
  AC-\<list\> transitioned to \<status\>. Awaiting confirmation before
  Task NN+1." Wait for user confirmation before continuing.
- **`continuous` mode** — proceed to the next task without stopping.
  No user confirmation required. Continue announcing tasks per 5.1
  so the user can interject if needed.
- **`batch <N>` mode** — stop after every N tasks. If this task
  number is divisible by N, stop and confirm; otherwise proceed.

Always stop on:

- The validate-and-capture task (regardless of mode)
- Compile or test failures (covered by Step 5.3 and the failure modes)
- Ambiguous bullets (covered by Step 5.2)
- Any AC transitioning to `FAIL` in the matrix

### 5.8 — Repeat

Move to the next task. Each task gets its own compile boundary, its
own proof, its own matrix update, its own commit, and its own
checkpoint.

---

## Step 6: Fill proof artifacts with real evidence

The proof file's `## Completion notes` section is where the audit trail
lives. Replace the placeholder with structured evidence per AC ID.

### Format

```markdown
# Proofs: Task NN — <Task title>

Covers: AC-1.a, AC-1.b

## Planned evidence

(Original list, unchanged.)

## Completion notes

### AC-1.a: <criterion verbatim from spec>

<Evidence — see types below.>

### AC-1.b: <criterion verbatim from spec>

<Evidence.>

### Notes

<Optional. Implementation decisions, surprises, deviations from
planned evidence.>
```

### Evidence types

| Evidence | Format |
|---|---|
| Command output | Fenced code block with the command at the top, real stdout/stderr below. Do not abbreviate or paraphrase. |
| File creation | `git diff --stat` or `ls -la` for the new file, plus a relevant excerpt of contents (5–30 lines). |
| File edit | `git diff <file>` excerpt, scoped to the relevant change. |
| Test pass | The Maven/JUnit output, including the summary line (e.g. `Tests run: 4, Failures: 0, Errors: 0, Skipped: 0`). |
| Coverage report | Relevant excerpt from `target/site/jacoco/index.html` or the JaCoCo console output showing line/branch percentages. |
| Schema / API change | Diff against existing DTOs, entities, or OpenAPI spec showing the additive change. |
| UI screenshot | Markdown image link to a captured screenshot, with the file committed alongside the proof. |
| Behavioral evidence | Logs, H2 console queries, Spring Actuator responses — formatted as fenced code blocks with the source command. |

### Forbidden patterns

These patterns are not evidence and must not appear in proof files:

- "Task complete" / "Done" / "Implemented as specified"
- A summary description of the work without command output
- Future tense ("will run tests after deployment")
- Evidence that doesn't match the AC's verbatim wording

If a proof's evidence doesn't address the AC's verbatim wording, the
task is not done — even if the work was performed.

### Re-validation case (Step 3.C)

For tasks being re-validated due to amendment:

- Append a new section `## Re-validation: amendment N — <date>`
  to the existing proof file.
- Do not overwrite the original `## Completion notes`.
- Capture fresh evidence in the new section.

This preserves the historical proof from first delivery while
recording the post-amendment validation.

---

## Step 7: Run the validation block

After all tasks are complete and have filled proofs:

### 7.1 — Run every command in the verification block

The validation file (`<prefix>NNN-validation-<slug>.md`) lists
verification commands. Run each one in the order listed, from the
working directory specified.

Capture the full output of each command.

For Java/Spring Boot projects, the standard verification block is:

```bash
# Compile check
./mvnw compile

# Full test suite
./mvnw test

# Coverage report (verify thresholds)
./mvnw clean test jacoco:report

# Checkstyle
./mvnw checkstyle:check

# Dependency vulnerability scan (if configured)
./mvnw dependency:analyze
```

Run from the repository root unless the task specifies a submodule
path.

### 7.2 — Fill the validate-and-capture task's proof

The final task in every spec is the validate-and-capture task. Its
proof file gets the verification block output in full:

````markdown
## Completion notes

### Verification block

#### `./mvnw test`

```text
<full Maven output including surefire summary>
```

#### `./mvnw clean test jacoco:report`

```text
<full output including coverage percentages>
```

(repeat for each command)

### Definition of done

Walk every AC ID in the DoD list. Each must trace to a proof file
with passing evidence:

- [x] AC-1.a — Task 01 proof, `./mvnw test` exits 0
- [x] AC-1.b — Task 01 proof, JUnit output shows test passing
- [x] AC-2.a — Task 03 proof, repository method exists and tested
- ...

````

### 7.3 — Tick the DoD checkboxes

Update the validation file: replace `[ ]` with `[x]` for each
satisfied AC. Do not tick checkboxes you cannot verify against a proof
artifact.

### 7.4 — Verify the coverage matrix

Walk every row in the coverage matrix. By this stage:

- Every active row must be `PASS`. Rows in `PENDING` mean a task
  was missed; rows in `FAIL` mean a criterion is not satisfied.
- Retired rows stay `RETIRED` (set by `amend-sdd-spec`).

If any active row is not `PASS`, **stop**. Do not proceed to Step 8.
Surface the matrix state to the user and diagnose:

- `PENDING` rows → which task should have covered this AC, and why
  was it not addressed? Either re-run the task or surface a spec
  gap.
- `FAIL` rows → see Step 7.5 for failure diagnosis. The matrix
  cannot lie about FAIL rows; they block delivery until resolved.

Capture the matrix's final state in the validate task's proof under a
`### Coverage matrix` subsection:

```markdown
### Coverage matrix

| AC ID | Status |
|-------|--------|
| AC-1.a | PASS |
| AC-1.b | PASS |
| AC-2.a | PASS |
| AC-3 | RETIRED (amendment 1) |
```

This snapshot is what the validate task's proof captures as the moment
of delivery — it's the durable record that says "every active AC was
in PASS at the moment status moved to `delivered`."

### 7.5 — Failure mode

If any verification command fails, or any active matrix row is in
`FAIL`:

- Capture the failure output in the validate task's proof.
- Do **not** transition status to `delivered`.
- Do **not** tick the DoD checkbox for the failing AC.
- Do **not** force the matrix row to `PASS`. The matrix's status is
  derived from evidence, not from desire.
- Diagnose the cause:
  - **Code bug** → fix and re-run from Step 5.3 onwards for the
    affected task. Capture the fix in that task's proof. Re-run
    Step 5.5 to update the matrix.
  - **Spec is wrong** → stop. Surface to the user. The spec needs
    amendment via `amend-sdd-spec` (or supersession via
    `supersede-sdd-spec` if the goal has shifted). Do not silently
    relax the AC. Do not silently flip the matrix row.

The "spec is wrong" failure is the highest-stakes failure mode in
this skill. AI assistants will reach for the easy path of softening
the AC to make the test pass — or silently flipping a matrix row
from FAIL to PASS to "tidy up." The skill explicitly forbids both.
See Failure Modes below.

---

## Step 8: Transition to `delivered`

Only after **all** of:

- Every task has a non-stub `## Completion notes` section.
- Every active AC ID in the DoD list is ticked.
- Every active row in the coverage matrix is `PASS`.
- The verification block exited 0 across the board.

The four conditions are AND-gated. Missing any one of them blocks the
transition. The matrix gate is the strictest of the four — it cannot
be satisfied by checking a box, only by capturing evidence.

Update the spec's front-matter:

```yaml
---
status: delivered
created: <unchanged>
last_amended: <unchanged>
supersedes: <unchanged>
superseded_by: <unchanged>
---
```

State the transition explicitly: "All DoD items pass. Coverage matrix
fully transitioned. Setting status to `delivered`."

For amendment re-engagement (3.C), this is `in_progress → delivered`
again — the second (or N-th) time the spec has reached delivery.
That's normal; the lifecycle table allows multiple round trips between
`in_progress` and `delivered`.

---

## Step 9: Update registries

### Bucket README

Update the spec's row:

- `status` column → `delivered`
- `last_delivered` column (if present) → today's date

### Parent epic registry

If the spec is a child of an epic:

- Tick the checkbox for this spec's row.
- If the row was previously ticked and is being re-engaged from
  amendment, leave it ticked through the re-engagement and confirm
  it's still ticked at end. Don't bounce the checkbox.

### Cross-cutting indexes

If the project maintains other indexes (e.g. a "Delivered features"
list, a release-notes draft), update them per project convention. The
`AGENTS.md` should list any such indexes; if it doesn't, do not
invent them.

### Delivery commit

After all index updates, commit the spec's transition to `delivered`
along with the registry/README updates. Default commit format:

```text
<prefix>NNN: Delivered

Spec status: in_progress → delivered
Active ACs: <count>, all PASS
Validation block: ./mvnw test exit 0
```

This is the audit trail's final commit for the spec. It marks the
moment the running system officially matches the spec.

For amendment re-engagement, the delivery commit format reflects the
re-delivery:

```text
<prefix>NNN: Re-delivered after amendment N

Spec status: in_progress → delivered
Amendment N reconciled: AC-<list>
Validation block: ./mvnw test exit 0
```

---

## Step 10: Stop

State delivery clearly: "Spec `<prefix>NNN` delivered on \<date\>. All
acceptance criteria pass."

Do not invoke other skills automatically. The implement skill produces
delivery; further evolution (amendment, supersession) requires explicit
user direction.

---

## Java/Spring Boot–specific guidance

### TDD mandate

Every new class or method introduced by a task must follow the
Red-Green-Refactor cycle:

1. **RED** — write a failing test first (`./mvnw test -Dtest="<TestClass>"` must fail)
2. **GREEN** — write the minimum production code to make it pass
3. **REFACTOR** — improve without breaking tests

Capture each phase transition in the task's proof. The RED phase's
failing test output and the GREEN phase's passing output are both
evidence — the proof without the RED phase is incomplete.

### Compile boundary command

The per-task compile check is:

```bash
./mvnw compile
```

For tasks that also modify or add tests, run:

```bash
./mvnw test -Dtest="<AffectedTestClass>"
```

Both must exit 0 (or be flagged "may break compile, fixed by Task NN+1").

### Test layer selection

Each new task should specify which layer its tests belong to:

| Layer | Annotation | When to use |
|---|---|---|
| Web / controller | `@WebMvcTest` + MockMvc | Controller request/response, form binding, validation messages |
| Data / repository | `@DataJpaTest` | JPA queries, entity relationships, persistence behavior |
| Full stack | `@SpringBootTest` | End-to-end flows, caching, actuator, cross-layer integration |
| Unit | No Spring context | Pure business logic, validators, formatters |

Proof artifacts must name the annotation used and show the test
runner's output.

### Coverage requirement

New production code added during implementation must meet the project's
thresholds:

- **>90% line coverage** for new classes
- **100% branch coverage** for critical business logic

After the validate-and-capture task, run:

```bash
./mvnw clean test jacoco:report
```

Include the relevant JaCoCo output in the proof. If a class falls
below threshold, the task is not done — add tests to cover the gap
before transitioning the AC to `PASS`.

### Package placement

New Java files go in the package that matches their layer and domain:

```text
src/main/java/org/springframework/samples/petclinic/
├── model/        # Base entities (BaseEntity, NamedEntity, Person)
├── owner/        # Owner, Pet, Visit, their controllers and repositories
├── vet/          # Vet, Specialty, VetController
└── system/       # Cache config, runtime hints, crash controller
```

Test files mirror the main structure under:

```text
src/test/java/org/springframework/samples/petclinic/
```

The task's proof must record the exact file path created or modified.

### Maven test output format

JUnit 5 test output through Maven Surefire looks like:

```text
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.123 s
[INFO] BUILD SUCCESS
```

This is the evidence format for test-pass proofs. Do not paraphrase
or summarize — capture the Surefire line verbatim.

---

## Failure modes (reference for Steps 5–7)

These are the failure cases AI assistants tend to mishandle. Each has a
specific response.

### Ambiguous task bullet

Stop. Ask the user a specific question. Do not guess at intent.

If the question reveals a missing detail in the spec, hand off to
`amend-sdd-spec` for a clarification. Do not edit the spec yourself.

### Compile breaks unexpectedly

If a task's `./mvnw compile` fails and the task is **not** flagged
"may break compile":

- Capture the failure in the proof.
- Diagnose: was the work incomplete, or did the task's bullets
  describe an inconsistent change?
- Fix the work if the bullets were correct.
- Stop and surface if the bullets themselves were inconsistent —
  this is a spec defect.

### Test fails after work appears complete

- Capture the failure in the proof.
- Diagnose against the AC's verbatim wording. The test is the
  evidence; the AC is the source of truth. Either the work is wrong
  (fix it) or the test is wrong (fix the test, and capture the test
  fix as part of the same task's proof).

### AC cannot be satisfied with the chosen approach

This is the hardest failure mode. Symptoms: tests pass for everything
the implementer wrote, but the AC's wording is not actually satisfied.

The temptation is to relax the AC's wording so the work fits. **Do
not.** Stop. Surface to the user. State explicitly which AC cannot
be satisfied and why. The user decides whether to:

- Push back ("the AC is right, find another approach")
- Amend the spec via `amend-sdd-spec` (the AC was overspecified)
- Supersede via `supersede-sdd-spec` (the approach is fundamentally
  wrong)

The implement skill never edits the spec to make work fit. That
discipline is the entire reason the spec exists.

### Matrix row tampering temptation

Symptoms: a matrix row is in `FAIL` and resists being fixed. The
temptation is to flip the row to `PASS` to "tidy up" the matrix, or
to mark the row `RETIRED` without going through `amend-sdd-spec`.
**Do not.**

The matrix is derived from evidence. A row's status reflects what the
proof captured. If a row is `FAIL`, that is a true statement that
must be addressed by either:

- Fixing the work (and re-running Step 5.5 to re-evaluate the row)
- Amending the spec via `amend-sdd-spec` (which has its own
  retirement flow that handles the matrix correctly)

Silently flipping a matrix row falsifies the audit trail. The matrix
must always reflect the proofs.

### Validation command not in `AGENTS.md`

If the validation file references a command that isn't in `AGENTS.md`,
the spec was authored against a different conventions file or the
conventions file is stale. Stop. Surface to the user. Either the
command needs to be added to `AGENTS.md` (project decision) or the
validation file needs amendment.

### Discovery of a related spec that needs updating

Implementation work sometimes reveals that another spec is wrong or
incomplete. Do not edit other specs from this skill. Capture the
finding in the current task's proof under `### Notes` and surface it
to the user as a separate amendment opportunity.

### Resumption finds inconsistent state

If proof files are partially complete in a way that doesn't match any
expected pattern (e.g. Task 03 done but Task 01 incomplete), stop.
Surface the inconsistency. Do not assume work order can be deduced
from proof state alone — sometimes the inconsistency is intentional
and sometimes it's a process error.

---

## Guiding principles

**The spec is the source of truth.** The implement skill consumes
specs, never edits them. If the spec is wrong, the answer is amendment
or supersession via the dedicated skills — not a quiet edit during
implementation.

**Proof artifacts are the audit trail.** They are not "evidence we did
work." They are the durable record that says "the running system
matches the spec on date X." Treat them as the most important output
of this skill, not the code itself. Code can be regenerated; the
moment-in-time evidence cannot.

**Compile at every boundary.** Each task leaves the workspace in a
state where `./mvnw compile` exits 0. The "may break compile"
exception exists for genuine architectural reasons; it is not a
license to defer cleanup.

**No silent AC relaxation.** The most common failure mode in
AI-driven implementation is softening an acceptance criterion to make
work fit. This skill's most important rule is the explicit refusal to
do so. Stop, surface, hand off — never edit the AC.

**Status transitions are visible.** Each transition is announced
("Setting status to `in_progress`," "Setting status to `delivered`")
and recorded in the front-matter. The transition is part of the audit
trail; silent transitions defeat the purpose.

**Resumption preserves history.** When resuming an `in_progress` spec
or re-engaging a `delivered` one, prior proofs stay intact. New
evidence is appended in dated sections. The historical structure is
load-bearing — overwriting it loses the "we tried this and it
worked" record.

**The validate task is the gate.** Status does not move to `delivered`
until the validate-and-capture task's proof confirms the entire
verification block exits 0, every active AC is ticked in the DoD, and
every active row in the coverage matrix is `PASS`. The gate is
mechanical, not judgmental.

**The matrix is derived from evidence.** A row's status reflects what
the proof captured. Flipping a row to `PASS` without supporting
evidence, or to `RETIRED` without going through `amend-sdd-spec`,
falsifies the audit trail. The matrix and the proofs cannot drift.

**Every task ends in a commit.** The audit trail goes commit → task
→ proof → AC → spec. Skipping the commit step breaks the chain. Even
proof-only tasks get a commit. The commit message is part of the
trail, not boilerplate.

**Checkpoint mode is the user's choice, not the skill's.** Default
to `task` mode but offer all three. Mid-run mode changes are normal
and should be captured in the next task's proof notes.

**Ambiguity is a stop condition.** Ambiguous bullets, ambiguous ACs,
and ambiguous tasks all halt the skill. Resolution happens via the
user, the amend skill, or the supersede skill — never via inferred
intent.

**TDD is non-negotiable.** Per project mandate, no production code is
written before a failing test exists. The RED phase's test failure
output is evidence. A proof that shows only GREEN (passing tests)
without a prior RED phase is incomplete.

**The context-rot marker is a coarse signal, not a guarantee.** This
skill runs longer than the others — multi-task implementation
sessions accumulate context fast. If the marker is absent or wrong
mid-run, treat that as a hard signal that context has degraded. Stop
and surface the issue rather than continuing under uncertainty. For
long specs, consider checkpointing aggressively (`task` mode) to
limit per-session context size.
