# Task 04 Proofs - Reproducible local ECS runtime verification workflow added

## Task Summary

This task adds a repository-owned verification entry point for the ECS runtime
contract. Reviewers can now exercise the local `floci` workflow with one
script, confirm backend initialization, run Terraform validation, and inspect a
sanitized plan without needing live AWS credentials.

## What This Task Proves

- The repository now contains one ECS runtime verification script:
  `./scripts/verify-ecs-runtime-foundation-contract.sh`.
- The verification script starts `floci`, initializes the backend, runs
  `terraform validate`, and runs a sanitized `terraform plan -no-color`.
- The script uses placeholder local credentials only and fails clearly when
  required files are missing.
- The `floci` README now documents the exact command and local verification
  expectations for the ECS runtime contract.

## Evidence Summary

- `TerraformEcsRuntimeVerificationWorkflowTest` failed before the script
  existed, then passed after the script and README documentation were added.
- The repository-owned verification script completed end to end with the local
  `floci` backend, Terraform validation, and sanitized planning workflow.
- The proof output shows only placeholder credentials and expected Terraform
  deprecation warnings; no live credentials were introduced.

## Artifact: Automated verification-workflow contract test

**What it proves:** The repository now enforces the existence and structure of
the ECS runtime verification workflow, including `floci`, Terraform ordering,
placeholder credentials, and missing-file failure messaging.

**Why it matters:** This protects the local verification path from silent drift
as the ECS runtime contract evolves.

**Command:**

```bash
./mvnw -Dtest=TerraformEcsRuntimeVerificationWorkflowTest test
```

**Result summary:** The task-specific workflow contract test passed after the
script and README entry were added.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformEcsRuntimeVerificationWorkflowTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Repository-owned ECS runtime verification script

**What it proves:** The repository now has a single reproducible local entry
point for ECS runtime verification.

**Why it matters:** Reviewers should not need to reconstruct the local Terraform
workflow manually from multiple documents or prior specs.

**Artifact path:** `scripts/verify-ecs-runtime-foundation-contract.sh`

**Result summary:** The script mirrors the repository’s existing Terraform
verification pattern, checks required files, starts `floci`, materializes the
partial backend for verification, runs `terraform validate`, and runs a
sanitized `terraform plan -no-color`.

```text
docker compose -f infra/terraform/floci/docker-compose.yml up -d floci
terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
terraform -chdir=infra/terraform/app/dev validate
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
  terraform -chdir=infra/terraform/app/dev plan -no-color
```

## Artifact: Documented `floci` verification path

**What it proves:** Operators now have a documented local command for reviewing
the ECS runtime contract before any live AWS use.

**Why it matters:** The repository’s workflow remains reviewer-usable only if
the operational path is documented alongside the script.

**Artifact path:** `infra/terraform/floci/README.md`

**Result summary:** The README now documents the ECS runtime verification
command and the placeholder credential expectations used throughout the local
workflow.

```text
## ECS Runtime Contract

Use the repository-owned verification entry point to exercise the ECS runtime contract locally:

./scripts/verify-ecs-runtime-foundation-contract.sh
```

## Artifact: End-to-end local verification run

**What it proves:** The repository-owned script works end to end against the
local `floci` backend and surfaces reviewer-usable Terraform validation and
sanitized plan output.

**Why it matters:** Static file checks are not enough unless the actual script
can execute successfully in the intended local workflow.

**Command:**

```bash
./scripts/verify-ecs-runtime-foundation-contract.sh
```

**Result summary:** The script started `floci`, initialized the backend,
reported `Success! The configuration is valid`, and produced a sanitized plan
showing the ECS cluster, log group, IAM roles, and runtime outputs. The only
warnings were the expected `dynamodb_table` deprecation notices from Terraform.

```text
Successfully configured the backend "s3"!

Success! The configuration is valid, but there were some
validation warnings as shown above.

Plan: 33 to add, 0 to change, 0 to destroy.

Changes to Outputs:
  + application_log_group_name    = "/aws/ecs/dev-application"
  + ecs_cluster_arn               = (known after apply)
  + ecs_cluster_name              = "dev-shared"
  + ecs_task_execution_role_arn   = (known after apply)
  + ecs_task_role_arn             = (known after apply)
```

## Reviewer Conclusion

Task `4.0` is reviewer-usable: the ECS runtime contract now has a reproducible
local verification script, the `floci` workflow is documented, the script runs
end to end with sanitized credentials, and the repository has an automated test
that keeps the verification path from regressing.
