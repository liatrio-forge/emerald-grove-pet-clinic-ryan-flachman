# Task 04 Proofs - Automated remote-state verification workflow

## Task Summary

This task adds one repository-owned verification script for the Terraform
remote-state contract. The script validates the `state/dev` stack, starts the
compose-managed `floci` environment, seeds the local backend resources with
dummy credentials, verifies consumer attachment, and tears the local stack down
again.

## What This Task Proves

- The repository now has one reproducible verification entry point for the
  remote-state contract.
- The verification flow checks both the state-stack and consumer-side contract.
- The local AWS-resources workflow is compose-managed and self-cleaning.
- The script fails clearly when required repository files are missing.
- The top-level README exposes the same proof path for local operators and
  future GitHub Actions reuse.

## Evidence Summary

- `TerraformRemoteStateVerificationWorkflowTest` passes, proving the script and
  top-level documentation contain the required verification workflow details.
- `./scripts/verify-terraform-remote-state-contract.sh` exits `0`, proving the
  documented Terraform, Docker Compose, and LocalStack-backed AWS CLI sequence
  works as a single sanitized flow.
- The script leaves no committed Terraform working directories behind after the
  run, keeping the repository clean for review.

## Artifact: Verification workflow contract test

**What it proves:** The repository enforces the existence of one verification
script, the compose-managed `floci` lifecycle, the state-stack and consumer init
commands, sanitized local credential usage, and README visibility.

**Why it matters:** This task is about freezing the operator workflow so future
automation and local validation both reuse the same contract.

**Command:**

```bash
./mvnw test -Dtest=TerraformRemoteStateVerificationWorkflowTest
```

**Result summary:** The targeted verification workflow suite passed with `2`
tests and `0` failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformRemoteStateVerificationWorkflowTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.065 s -- in org.springframework.samples.petclinic.system.TerraformRemoteStateVerificationWorkflowTest
[INFO] BUILD SUCCESS
```

## Artifact: Repository-owned remote-state verification script

**What it proves:** The complete remote-state verification path executes in the
required order: local state-stack init, state-stack validate, compose startup,
local backend seeding, consumer backend attachment, and compose teardown.

**Why it matters:** Reviewers need runtime evidence that the documented contract
is executable and not just represented by static files.

**Command:**

```bash
./scripts/verify-terraform-remote-state-contract.sh
```

**Result summary:** The script initialized and validated the state stack,
started `floci`, configured the consumer's S3 backend successfully, and then
shut the compose stack down. Terraform emitted the expected deprecation warning
for `dynamodb_table`, which remains part of this spec's S3 plus DynamoDB lock
contract.

```text
Success! The configuration is valid.
Container floci-floci-1 Running
Successfully configured the backend "s3"!
Terraform has been successfully initialized!
Container floci-floci-1 Removed
```

## Reviewer Conclusion

These artifacts show that the backend verification flow is now reproducible,
documented, and automation-ready. One repository-owned script covers the
state-stack contract, the compose-managed `floci` workflow, and downstream
consumer attachment without committing secrets or manual local setup steps.
