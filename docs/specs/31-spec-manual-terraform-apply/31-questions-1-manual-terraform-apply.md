# 31 Questions Round 1 - Manual Terraform Apply

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Apply Approval Gate

What approval model should the manual `dev` Terraform apply workflow require before infrastructure changes are executed?

- [ ] (A) Typed confirmation input only, such as entering `apply-dev`, with no GitHub environment review gate
- [ ] (B) GitHub environment required reviewers only, with no separate typed confirmation input
- [ ] (C) Both GitHub environment required reviewers and a typed confirmation input
- [ ] (D) No approval gate beyond manually starting the workflow from GitHub Actions
- [ ] (E) Other (describe)

**Current best-practice context:** Current GitHub Actions guidance allows `workflow_dispatch` inputs for manual intent capture and allows environments to require reviewers, optionally prevent self-review, and block jobs from accessing environment secrets until protection rules pass. For infrastructure-changing workflows, those two controls solve different problems: reviewer approval reduces unauthorized or insufficiently reviewed changes, while typed confirmation reduces accidental clicks.

**Recommended answer(s):** [(C)]

**Why these are recommended:**

- `(C)` gives this workflow two explicit safety layers without expanding the scope into a separate change-management system.
- `(C)` maps cleanly to the issue's stated need for manual operator control plus safeguards, while staying reviewable for a junior developer.
- `(B)` is stronger than `(A)` for governance, but it does less to prevent accidental triggering by an already-authorized maintainer.
- `(A)` is simpler, but it places too much trust in a single operator click path for a workflow that can mutate shared infrastructure.
- `(D)` conflicts with the issue's safety requirement and would make the eventual spec meaningfully weaker.

## 2. Allowed Source Ref For Apply

Which Git ref should this workflow be allowed to apply from in v1?

- [ ] (A) `main` only, so the workflow applies only the repository's primary reviewed branch
- [ ] (B) Any manually selected branch, so maintainers can apply preview or feature-branch infrastructure changes
- [ ] (C) Tags only, so applies happen only from explicitly versioned revisions
- [ ] (D) A small allowlist of protected branches, such as `main` plus future release branches
- [ ] (E) Other (describe)

**Current best-practice context:** Current GitHub and AWS OIDC guidance recommends tightly scoping trust conditions and deployment entry points. Restricting the allowed branch or environment keeps the `sub` claim narrow and reduces the risk of an unintended branch being able to assume the apply role.

**Recommended answer(s):** [(A)]

**Why these are recommended:**

- `(A)` matches the current repo reality that only `dev` exists and keeps the trust boundary narrow.
- `(A)` makes the eventual OIDC trust policy and workflow behavior easier to reason about than `(B)` or `(D)`, which expand the branch surface immediately.
- `(B)` is flexible, but it materially increases the chance of drift between reviewed code and applied infrastructure.
- `(C)` can be valid later, but it adds release-process overhead that the current issue does not require.

## 3. Plan And Apply Execution Shape

How should the workflow connect the Terraform plan step to the apply step?

- [ ] (A) Run `terraform apply -auto-approve` after approval without using a saved plan artifact
- [ ] (B) Run a plan job first, save the reviewed Terraform plan and required working-directory artifacts, then run apply from that saved plan after approval
- [ ] (C) Run plan in GitHub Actions, but require the operator to apply outside GitHub Actions
- [ ] (D) Run plan and apply in one uninterrupted job with only workflow inputs as the approval signal
- [ ] (E) Other (describe)

**Current best-practice context:** Current HashiCorp automation guidance recommends a human-reviewed plan/apply sequence and shows `terraform plan -out=tfplan` followed by `terraform apply tfplan` for automation. The same guidance also warns that when plan and apply happen on different machines, the workflow must preserve the working directory and plan artifacts carefully because saved plans depend on matching files, plugins, paths, and platform assumptions.

**Recommended answer(s):** [(B)]

**Why these are recommended:**

- `(B)` best preserves the guarantee that the reviewed changes are the exact changes that get applied.
- `(B)` matches the issue's expectation that the workflow should define a predictable `init`, `plan`, and `apply` sequence rather than re-planning at apply time.
- `(A)` is simpler operationally, but it can apply a different plan than the operator reviewed if state or code changes between steps.
- `(D)` keeps everything on one runner, but it weakens the review boundary because approval and execution become one uninterrupted operation.
- `(C)` avoids CI complexity, but it fails the issue's goal of allowing maintainers to apply dev infrastructure from GitHub Actions itself.
