# Task 03 Proofs - CI outputs and destroy-time repository behavior

## Task Summary

This task proves the dev ECR contract now exports the exact downstream outputs
needed by CI and makes destroy-time image deletion explicit for the dev-only
POC lifecycle.

## What This Task Proves

- The app stack exports exactly `repository_uri` and `repository_name`.
- The repository uses explicit `force_delete = true`.
- Documentation explains how immutable Git SHA tags and `repository_uri` fit
  into later push and deploy workflows.
- Documentation states that destroying the repository deletes all contained
  images.

## Evidence Summary

- `infra/terraform/app/dev/outputs.tf` now exposes `repository_uri` and
  `repository_name`.
- The plan output shows `force_delete = true` on the repository and surfaces
  the new outputs.
- `TerraformEcrRepositoryOutputsAndDestroyContractTest` passes.

## Artifact: Output contract source

**What it proves:** CI and later ECS work can consume the repository contract
without reconstructing names manually.

**Why it matters:** This removes naming ambiguity from downstream automation.

**Artifact path:** `infra/terraform/app/dev/outputs.tf`

**Result summary:** The stack exports the exact two repository outputs required
by the spec.

```hcl
output "repository_uri" {
  description = "Deterministic ECR repository URI for CI image pushes."
  value       = aws_ecr_repository.app.repository_url
}

output "repository_name" {
  description = "Deterministic ECR repository name for CI and ECS references."
  value       = aws_ecr_repository.app.name
}
```

## Artifact: Planned destroy and output contract

**What it proves:** The repository is intentionally force-deletable and the new
outputs are visible in the plan.

**Why it matters:** Reviewers can verify teardown and CI handoff semantics
before apply.

**Command:**

```bash
./scripts/verify-ecr-repository-contract.sh
```

**Result summary:** The plan shows `force_delete = true`, `repository_name =
"dev-petclinic"`, and a concrete `repository_uri` output slot.

```text
# aws_ecr_repository.app will be created
+ resource "aws_ecr_repository" "app" {
    + force_delete         = true
    + name                 = "dev-petclinic"

Changes to Outputs:
  + repository_name = "dev-petclinic"
  + repository_uri  = (known after apply)
```

## Artifact: Targeted outputs/destroy contract test

**What it proves:** The output names and force-delete behavior stay synchronized
with documentation.

**Why it matters:** This protects downstream CI assumptions and the explicit
destroy behavior from drift.

**Command:**

```bash
./mvnw -Dtest=TerraformEcrRepositoryOutputsAndDestroyContractTest test
```

**Result summary:** The targeted outputs/destroy contract test passed.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Reviewer Conclusion

The app stack now publishes the exact CI-facing ECR outputs and makes the
dev-only repository destroy semantics explicit and test-covered.
