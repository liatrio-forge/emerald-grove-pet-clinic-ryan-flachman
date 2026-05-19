# Task 02 Proofs - ECR lifecycle retention and previewable cleanup rules

## Task Summary

This task proves the dev ECR repository now carries a readable lifecycle policy
that cleans up untagged images automatically and retains the most recent five
tagged Git SHA images with count-based retention.

## What This Task Proves

- The repository has a dedicated `aws_ecr_lifecycle_policy`.
- Untagged cleanup and tagged-image retention rules are separated and readable.
- Tagged retention uses `imageCountMoreThan` with a retain count of `5`.
- Documentation tells reviewers to inspect lifecycle policy preview output
  before applying in AWS.

## Evidence Summary

- `infra/terraform/app/dev/main.tf` defines `aws_ecr_lifecycle_policy.app`.
- The plan output shows both lifecycle rules and the count-based tagged-image
  retention.
- `TerraformEcrLifecyclePolicyContractTest` passes and locks the retention
  contract in source control.

## Artifact: Lifecycle policy source

**What it proves:** The lifecycle contract is explicit and reviewer-readable in
Terraform.

**Why it matters:** The cleanup rules need to be understandable before anyone
  trusts the policy in AWS.

**Artifact path:** `infra/terraform/app/dev/main.tf`

**Result summary:** The policy keeps untagged cleanup and tagged retention
separate, and tagged retention is count-based at five images.

```hcl
resource "aws_ecr_lifecycle_policy" "app" {
  repository = aws_ecr_repository.app.name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        selection = {
          tagStatus   = "untagged"
          countType   = "imageCountMoreThan"
          countNumber = 1
        }
      },
      {
        rulePriority = 2
        selection = {
          tagStatus   = "tagged"
          countType   = "imageCountMoreThan"
          countNumber = 5
        }
      }
    ]
  })
}
```

## Artifact: Planned lifecycle policy

**What it proves:** The lifecycle rules remain observable in the plan output
before apply.

**Why it matters:** Reviewers can preview retention behavior without making live
AWS changes.

**Command:**

```bash
./scripts/verify-ecr-repository-contract.sh
```

**Result summary:** The plan shows separate `untagged` and `tagged` rules, with
tagged retention using `countNumber = 5`.

```text
# aws_ecr_lifecycle_policy.app will be created
+ resource "aws_ecr_lifecycle_policy" "app" {
    + policy = jsonencode(
          {
            + rules = [
                + {
                    + selection = {
                        + countNumber = 1
                        + countType   = "imageCountMoreThan"
                        + tagStatus   = "untagged"
                      }
                  },
                + {
                    + selection = {
                        + countNumber = 5
                        + countType   = "imageCountMoreThan"
                        + tagStatus   = "tagged"
```

## Artifact: Targeted lifecycle contract test

**What it proves:** The lifecycle policy remains count-based, readable, and
free of age-based retention assumptions.

**Why it matters:** The test prevents silent drift to a different cleanup
strategy.

**Command:**

```bash
./mvnw -Dtest=TerraformEcrLifecyclePolicyContractTest test
```

**Result summary:** The lifecycle contract test passed.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Reviewer Conclusion

The repository now has a previewable lifecycle policy with separate untagged
cleanup and a five-image tagged retention window that is enforced by tests.
