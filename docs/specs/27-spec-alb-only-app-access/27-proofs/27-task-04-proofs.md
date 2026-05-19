# Task 04 Proofs - Reproducible local verification workflow

## Task Summary

This task proves the repository now includes one reusable verification entry
point for the ALB-only app-access contract, aligned with `floci` operator
guidance and backed by automated tests plus a successful end-to-end local run.

## What This Task Proves

- `scripts/verify-alb-only-app-access-contract.sh` exists, is executable, and
  validates the Terraform contract with sanitized local credentials.
- `infra/terraform/floci/README.md` documents the exact local verification flow
  and points reviewers to the repository-owned script.
- The full repository Maven suite passes after the infrastructure contract
  additions.

## Evidence Summary

- `TerraformAlbOnlyAppAccessVerificationWorkflowTest` passes and guards the
  verification script plus `floci` documentation contract.
- The verification script starts `floci`, initializes the backend, runs
  `terraform validate`, and completes a sanitized `terraform plan`.
- `./mvnw test` succeeds outside the sandbox with `224` tests passing.

## Artifact: Verification workflow system test

**What it proves:** The repository has an automated guardrail for the script,
the `floci` documentation, sanitized credentials, and missing-file failures.

**Why it matters:** The local verification path needs to remain reproducible for
future infrastructure work, not just for this one implementation.

**Command:**

```bash
./mvnw -q -Dtest=TerraformAlbOnlyAppAccessVerificationWorkflowTest test
```

**Result summary:** The targeted test exited successfully after the script and
`floci` README were added and aligned.

```text
Exit status: 0
```

## Artifact: End-to-end `floci` verification run

**What it proves:** A reviewer can execute one repository-owned command to
start the local AWS-style environment, validate the Terraform stack, and inspect
the ALB-only access plan with sanitized credentials.

**Why it matters:** This is the spec’s main operational proof that the contract
is reproducible before any live AWS deployment.

**Command:**

```bash
./scripts/verify-alb-only-app-access-contract.sh
```

**Result summary:** The script started `floci`, configured the local backend,
reported a successful Terraform validation, and produced a plan with the
expected ALB and ECS security-group contract. The only warning was the existing
deprecated `dynamodb_table` backend parameter.

```text
Container floci-floci-1 Started
Terraform has been successfully initialized!
Success! The configuration is valid, but there were some
validation warnings as shown above.
Plan: 23 to add, 0 to change, 0 to destroy.
Container floci-floci-1 Removed
```

## Artifact: Full repository Maven suite

**What it proves:** The infrastructure contract changes integrate cleanly with
the broader codebase test suite.

**Why it matters:** This is the repository-level quality gate required before
closing the spec implementation.

**Command:**

```bash
./mvnw test
```

**Result summary:** The full Maven test suite completed successfully outside the
sandbox with all tests passing.

```text
Tests run: 224, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Reviewer Conclusion

These artifacts show the ALB-only access contract is not just implemented but
operationally reviewable: one script reproduces the local Terraform verification
flow, the docs explain it, and the repository test suite remains green.
