# Task 04 Proofs - Reproducible local verification for the public HTTP ALB contract

## Task Summary

This task adds a repository-owned verification entry point for the public HTTP
ALB contract. Reviewers and operators can now run one documented script to
start `floci`, initialize the dev Terraform stack, validate the configuration,
and generate a sanitized plan that shows the ALB, listener, target group, and
output contract.

## What This Task Proves

- `scripts/verify-public-http-alb-target-group-contract.sh` exists and follows
  the repository’s established `floci` verification pattern.
- The script checks required files up front, uses placeholder credentials, and
  runs `terraform init`, `validate`, and sanitized `plan` in the correct order.
- `infra/terraform/floci/README.md` now documents the exact public HTTP ALB
  verification flow.
- The end-to-end script exits `0` and produces a reproducible `floci`-backed
  proof path for the ALB contract.

## Evidence Summary

- `TerraformPublicAlbVerificationWorkflowTest` failed before the verification
  script existed, then passed after the script and `floci` documentation were
  added.
- The script completed successfully with sanitized local credentials and the
  repository’s existing backend-deprecation warning only.
- The verification run produced the same reviewer-usable ALB, listener,
  target-group, and output evidence as the earlier proof tasks, but now from a
  single documented entry point.

## Artifact: Automated verification-workflow contract test

**What it proves:** The repository now guards the existence and structure of
the public HTTP ALB verification workflow.

**Why it matters:** A local verification path is only reliable if later edits
cannot silently remove its `floci`, placeholder-credential, or command-order
contract.

**Command:**

```bash
./mvnw test -Dtest=TerraformPublicAlbVerificationWorkflowTest
```

**Result summary:** The workflow test passed after the new script and `floci`
README section were added.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformPublicAlbVerificationWorkflowTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Repository-owned end-to-end verification script run

**What it proves:** The documented local verification path works end to end for
the public HTTP ALB contract.

**Why it matters:** This is the operational proof that a reviewer can reproduce
the Terraform validation path without assembling commands manually.

**Command:**

```bash
./scripts/verify-public-http-alb-target-group-contract.sh
```

**Result summary:** The script started `floci`, initialized the dev stack,
completed `terraform validate`, and produced a sanitized plan showing the ALB,
listener, target group, and exported outputs before cleaning up the local
containers.

```text
Successfully configured the backend "s3"!
Success! The configuration is valid, but there were some
validation warnings as shown above.
Plan: 28 to add, 0 to change, 0 to destroy.
Changes to Outputs:
  + alb_dns_name                  = (known after apply)
  + alb_hosted_zone_id            = (known after apply)
  + alb_name                      = "dev-public-http"
  + application_target_group_name = "dev-application"
```

## Artifact: Operator-facing `floci` documentation

**What it proves:** Reviewers have a stable, repository-local set of
instructions for reproducing the public HTTP ALB verification path.

**Why it matters:** A verification script alone is not enough if operators do
not know when or how to use it.

**Artifact path:** `infra/terraform/floci/README.md`

**Result summary:** The README now documents the public HTTP ALB contract
section, the exact script entry point, the `terraform validate` and
`terraform plan -no-color` steps, and the placeholder credentials required for
local runs.

## Reviewer Conclusion

Task `4.0` is implemented and reproducible: the repository now has one
documented, test-backed verification script that exercises the public HTTP ALB
contract locally against `floci` with sanitized credentials only.
