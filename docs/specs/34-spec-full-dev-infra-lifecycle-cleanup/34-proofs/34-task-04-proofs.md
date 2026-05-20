# Task 04 Proofs - Final cleanup handoff and persistent bootstrap-secret exception

## Task Summary

This task documents the reviewer-facing GitHub lifecycle configuration for the
dev foundation teardown path, including the exact protected environments,
stable GitHub variable names, final cleanup reset sequence, and the standing
POC exception that keeps `dev-bootstrap` secrets in place for future rebuilds
and final teardown work.

## What This Task Proves

- Root and stack-level docs now document the exact GitHub environments and
  variables used across apply, destroy, publish, deploy, and bootstrap flows.
- Final cleanup guidance now tells operators to blank AWS-derived GitHub
  variable values while preserving the variable names for reuse.
- Repository guidance now treats persistent `dev-bootstrap` secrets as the
  explicit POC exception instead of telling operators to remove them after the
  initial bootstrap.

## Evidence Summary

- The Task 4 documentation contract slice passed with 3 tests and 0 failures.
- `README.md` now contains a dedicated GitHub lifecycle configuration section
  with protected-environment and stable-variable lists plus the cleanup handoff.
- `infra/terraform/app/dev/README.md` now includes the final cleanup checklist
  and protected environment matrix used by reviewers and operators.
- A sanitized proof image now captures the expected environment names and
  variable names without exposing any real secret values or private
  identifiers.

## Artifact: Documentation contract tests

**What it proves:** The repository documents the exact lifecycle configuration,
final variable reset sequence, and persistent bootstrap-secret exception.

**Why it matters:** Parent Task 4 is documentation and reviewer-handoff work,
so the contract tests are the fastest way to prove the wording is present and
stable.

**Command:**

```bash
./mvnw test -Dtest=TerraformFinalCleanupDocumentationContractTest,GitHubLifecycleConfigurationContractTest
```

**Result summary:** The focused Task 4 slice passed with 3 tests and 0
failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.GitHubLifecycleConfigurationContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformFinalCleanupDocumentationContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Root and stack lifecycle documentation

**What it proves:** Reviewer-facing docs now tell operators exactly which
environment names and GitHub variables remain stable across the lifecycle, and
what to do after final teardown.

**Why it matters:** Without this wording, the final cleanup step is easy to do
incorrectly and could either remove reusable configuration names or leave stale
AWS-derived values behind.

**Artifact paths:**

- `README.md`
- `infra/terraform/app/dev/README.md`

**Result summary:** The docs now list `dev`, `dev-destroy`, and
`dev-bootstrap`, enumerate the stable variable names, tell operators to blank
AWS-derived values while preserving variable names, and keep the persistent
bootstrap-secret exception explicit.

```text
README.md: GitHub Lifecycle Configuration
README.md: Blank the AWS-derived GitHub variable values.
README.md: Preserve the variable names for future reuse.
README.md: Keep dev-bootstrap secrets remain stored by design...
infra/terraform/app/dev/README.md: Final Cleanup Checklist
infra/terraform/app/dev/README.md: Protected environment matrix
```

## Artifact: Sanitized reviewer-facing GitHub configuration image

**What it proves:** The spec now has a reviewer-friendly visual reference for
the expected GitHub environment and variable names without exposing any
real-world secret values.

**Why it matters:** This satisfies the proof requirement for a sanitized
artifact that can be reviewed independently of a live GitHub repository.

**Artifact paths:**

- `docs/specs/34-spec-full-dev-infra-lifecycle-cleanup/34-proofs/34-task-04-github-config-sanitized.svg`
- `docs/specs/34-spec-full-dev-infra-lifecycle-cleanup/34-proofs/34-task-04-github-config-sanitized.png`

**Result summary:** The image shows the protected environments `dev`,
`dev-destroy`, and `dev-bootstrap`, plus the stable variable contract and the
post-teardown blank-value expectation.

## Reviewer Conclusion

These artifacts show Task 4 is complete: the repository now documents the
final cleanup handoff clearly, preserves stable GitHub configuration names for
reuse, and keeps the `dev-bootstrap` secret exception explicit and consistent
with Spec 34.
