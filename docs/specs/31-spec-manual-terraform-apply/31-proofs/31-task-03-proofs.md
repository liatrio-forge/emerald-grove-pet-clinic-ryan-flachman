# Task 03 Proofs - Exact saved-plan apply, concurrency, and failure handling

## Task Summary

This task proves the workflow applies the previously reviewed `tfplan` artifact,
blocks overlapping `dev` applies through one concurrency boundary, and fails
with explicit operator-visible messages when critical prerequisites are missing.

## What This Task Proves

- The apply job downloads and applies the saved plan artifact instead of
  recomputing a fresh plan.
- The workflow defines one `dev` apply concurrency boundary.
- The workflow fails clearly for missing configuration, missing plan artifact,
  and non-zero apply execution.

## Evidence Summary

- The apply contract test passes with 2 assertions and no failures.
- The workflow YAML contains the concurrency block, artifact download step, saved-plan
  verification, and explicit apply failure messaging.
- A full `./mvnw test` run still fails in this sandbox for existing embedded
  Tomcat integration tests that cannot bind a server socket; the new workflow
  contract tests remain green and are isolated from that environment issue.

## Artifact: Apply contract test

**What it proves:** The exact-plan apply and failure-path contract is enforced by
automated tests.

**Why it matters:** This is the main regression guard against unsafe
re-planning or silent apply failures.

Command:

```bash
./mvnw -Dtest=TerraformApplyWorkflowApplyContractTest test
```

**Result summary:** The targeted apply contract test passed with 2 assertions
and no failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformApplyWorkflowApplyContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Concurrency and apply-gate lines

**What it proves:** The workflow contains one concurrency boundary, downloads
the reviewed plan artifact, checks that it exists, and applies it with explicit
failure messaging.

**Why it matters:** Reviewers can confirm the operational safety behavior from
the workflow source directly.

Command:

```bash
rg -n "concurrency:|group: terraform-apply-dev|cancel-in-progress: false|download-artifact@v4|Reviewed plan artifact is unavailable|terraform apply -input=false tfplan|Terraform apply failed" .github/workflows/terraform-apply-dev.yml
```

**Result summary:** The workflow now enforces one `dev` apply lane and applies
only the previously reviewed saved plan.

```text
14:concurrency:
15:  group: terraform-apply-dev
16:  cancel-in-progress: false
109:        uses: actions/download-artifact@v4
117:            echo "Reviewed plan artifact is unavailable." >&2
124:          terraform apply -input=false tfplan || {
125:            echo "Terraform apply failed." >&2
```

## Artifact: Repository test-suite limitation

**What it proves:** The repo-wide quality gate was attempted, but sandbox
constraints prevent a clean full-suite result unrelated to this workflow change.

**Why it matters:** Reviewers need to distinguish workflow-contract health from
pre-existing environment limits in this execution sandbox.

Command:

```bash
./mvnw test
```

**Result summary:** The full suite failed because existing integration tests
that start embedded Tomcat could not bind a local socket in the sandbox. The
new workflow tests passed before this broader environment failure occurred.

```text
[ERROR] Tests run: 256, Failures: 0, Errors: 4, Skipped: 5
[ERROR] Caused by: java.net.SocketException: Operation not permitted
[ERROR] Failed to start bean 'webServerStartStop'
```

## Reviewer Conclusion

Task 03 is implemented: the workflow applies the exact reviewed plan, prevents
overlapping `dev` applies, and emits explicit failure diagnostics for the main
operator risk paths.
