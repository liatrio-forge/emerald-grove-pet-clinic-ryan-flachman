# Task 01 Proofs - Manual workflow entrypoint and safety gates

## Task Summary

This task proves the repository now defines one manual GitHub Actions entrypoint
for `dev` Terraform apply work, with explicit branch and typed-confirmation
safety checks and a protected `dev` environment gate in front of the
apply-capable job.

## What This Task Proves

- The workflow is manual-only via `workflow_dispatch`.
- The workflow requires the operator to type `apply dev`.
- The apply-capable job is restricted to the `main` branch.
- The apply-capable job is attached to the protected `dev` environment.

## Evidence Summary

- The dispatch contract test passes, confirming the workflow file exists and
  enforces the required manual safety contract.
- The workflow YAML shows the confirmation input, `main`-branch apply guard, and
  `environment: dev` binding directly.
- Live GitHub UI screenshots were not captured locally because this CLI sandbox
  does not provide authenticated browser access to an uncommitted workflow. The
  contract evidence below is therefore repository-based and reproducible.

## Artifact: Dispatch contract test

**What it proves:** The workflow file exists and preserves manual invocation,
typed confirmation, main-branch gating, and protected-environment wiring.

**Why it matters:** This is the automated regression guard for the safety model
 reviewers rely on before any live infrastructure apply.

Command:

```bash
./mvnw -Dtest=TerraformApplyWorkflowDispatchContractTest test
```

**Result summary:** The targeted contract test passed with 2 assertions and no
failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformApplyWorkflowDispatchContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Workflow safety-gate lines

**What it proves:** The workflow contains the exact operator input and gate
strings reviewers need to audit quickly.

**Why it matters:** Reviewers can confirm the safety contract from one file
without running the workflow.

Command:

```bash
rg -n "workflow_dispatch|confirmation:|github\.ref == 'refs/heads/main'|environment: dev|Apply requires the main branch|Apply requires confirmation: apply dev" .github/workflows/terraform-apply-dev.yml
```

**Result summary:** The workflow defines the manual trigger, confirmation input,
main-branch apply condition, and `dev` environment binding in one place.

```text
4:  workflow_dispatch:
6:      confirmation:
25:          echo "Apply requires the main branch." >&2
31:          echo "Apply requires confirmation: apply dev" >&2
95:    if: github.ref == 'refs/heads/main' && github.event.inputs.confirmation == 'apply dev'
97:    environment: dev
```

## Artifact: YAML quality check

**What it proves:** The new workflow file is valid YAML under the repository’s
pre-commit policy.

**Why it matters:** A broken workflow file would invalidate the operator-facing
contract even if the string-based tests passed.

Command:

```bash
pre-commit run check-yaml --files .github/workflows/terraform-apply-dev.yml
```

**Result summary:** The repository YAML validation hook passed for the workflow.

```text
check yaml...............................................................Passed
```

## Reviewer Conclusion

Task 01 is implemented: the repository now has a single manual workflow entry
point for `dev` applies, guarded by typed confirmation, main-branch checks, and
the protected `dev` environment.
