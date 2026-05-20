# Task 01 Proofs - Manual workflow entrypoint and safety gates

## Task Summary

This task proves the repository now defines one manual GitHub Actions entry
point for `dev` ECR publication, with typed confirmation, `main`-branch
gating, protected-environment binding, and queued concurrency.

## What This Task Proves

- The workflow exists at `.github/workflows/manual-dev-ecr-publish.yml`.
- The workflow is manual-only via `workflow_dispatch`.
- The workflow requires `publish dev image` confirmation and rejects non-`main`
  execution with clear error messages.
- The publish-capable job is bound to the protected `dev` environment and one
  `manual-dev-ecr-publish` concurrency group.

## Evidence Summary

- The focused dispatch contract test passed.
- The workflow file contains the expected trigger, confirmation, guard, and
  environment strings.
- YAML validation passed for the new workflow file.
- Live GitHub UI screenshots were not captured in this local CLI sandbox, so
  the proof below is repository-based and reproducible rather than
  browser-captured.

## Artifact: Dispatch contract test

**What it proves:** The workflow file exists and preserves the manual dispatch
and safety-gate contract.

**Why it matters:** This is the automated regression guard for the operator
entrypoint and approval boundary.

**Command:**

```bash
./mvnw -Dtest=ManualDevEcrPublishWorkflowDispatchContractTest test
```

**Result summary:** The targeted workflow dispatch contract test passed with 2
assertions and no failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.ManualDevEcrPublishWorkflowDispatchContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Workflow safety-gate lines

**What it proves:** The YAML contains the exact trigger, confirmation, branch,
environment, and concurrency strings reviewers need to inspect quickly.

**Why it matters:** Reviewers can confirm the safety model directly from the
repository without needing a live GitHub session.

**Command:**

```bash
rg -n "workflow_dispatch|confirmation:|group: manual-dev-ecr-publish|environment: dev|Publish requires the main branch|Publish requires confirmation: publish dev image" .github/workflows/manual-dev-ecr-publish.yml
```

**Result summary:** The workflow defines the manual trigger, typed confirmation,
queued concurrency, and `dev` environment binding in one file.

```text
4:  workflow_dispatch:
6:      confirmation:
14:  group: manual-dev-ecr-publish
23:          echo "Publish requires the main branch." >&2
29:          echo "Publish requires confirmation: publish dev image" >&2
35:    environment: dev
```

## Artifact: YAML quality check

**What it proves:** The new workflow file is valid YAML under the repository’s
pre-commit policy.

**Why it matters:** A malformed workflow file would invalidate the operator
contract before any AWS-backed publishing could occur.

**Command:**

```bash
pre-commit run check-yaml --files .github/workflows/manual-dev-ecr-publish.yml
```

**Result summary:** The repository YAML hook passed for the new workflow.

```text
check yaml...............................................................Passed
```

## Artifact: Live GitHub UI evidence status

**What it proves:** The remaining gap is operational capture, not repository
contract definition.

**Why it matters:** The task list asked for GitHub UI screenshots, and later
validation should explicitly know why they are not yet present.

**Result summary:** This sandbox cannot open an authenticated GitHub Actions UI
for an uncommitted workflow, so manual-run-form and environment-gate
screenshots remain a follow-up artifact to capture from GitHub after the
workflow is available in the remote repository.

## Reviewer Conclusion

Task 01 is implemented at the repository level: the workflow entrypoint,
confirmation text, `main` guard, environment binding, and concurrency contract
are all present and covered by automated tests. Live GitHub screenshots remain
an explicit follow-up artifact.
