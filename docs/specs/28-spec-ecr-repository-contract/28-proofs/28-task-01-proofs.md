# Task 01 Proofs - Dev ECR repository identity and immutable tagging contract

## Task Summary

This task proves the dev app stack now defines a single private ECR repository
with an environment-scoped name, explicit reviewer-readable tags, and immutable
image tags for the Git SHA-only v1 contract.

## What This Task Proves

- The Terraform stack defines one `aws_ecr_repository` named `dev-petclinic`.
- The repository uses `IMMUTABLE` tag mutability and explicit resource tags.
- The app-stack documentation states immutable Git SHA tags are the only
  approved v1 deploy references and excludes mutable convenience tags.

## Evidence Summary

- `infra/terraform/app/dev/main.tf` now defines `aws_ecr_repository.app` with
  `name = local.ecr_repository_name`, `image_tag_mutability = "IMMUTABLE"`, and
  repository metadata tags.
- The local verification workflow reports `Success! The configuration is valid`
  before planning.
- The plan output shows one `aws_ecr_repository.app` resource with
  `name = "dev-petclinic"` and `image_tag_mutability = "IMMUTABLE"`.
- `TerraformEcrRepositoryContractTest` passes and locks the contract in a
  reviewer-readable unit test.

## Artifact: Terraform repository definition

**What it proves:** The repository contract is explicit in source, not implicit
in provider defaults.

**Why it matters:** Downstream CI and ECS work can consume a stable image source
without reconstructing repository identity or tag behavior.

**Artifact path:** `infra/terraform/app/dev/main.tf`

**Result summary:** The stack defines one private ECR repository named from the
environment, enables immutable tags, and adds repository-role metadata.

```hcl
resource "aws_ecr_repository" "app" {
  name                 = local.ecr_repository_name
  image_tag_mutability = "IMMUTABLE"
  force_delete         = true

  tags = merge(local.common_tags, {
    Name           = local.ecr_repository_name
    RepositoryRole = "application-image"
  })
}
```

## Artifact: Terraform validate success

**What it proves:** The ECR contract is syntactically valid in the local
verification workflow.

**Why it matters:** Reviewers need a reproducible validation step before
relying on plan output.

**Command:**

```bash
./scripts/verify-ecr-repository-contract.sh
```

**Result summary:** The workflow printed a successful validation result before
the plan step.

```text
Success! The configuration is valid, but there were some
validation warnings as shown above.
```

## Artifact: Planned ECR repository contract

**What it proves:** Terraform plans one deterministic repository target with
immutable tagging enabled.

**Why it matters:** This is the runtime-facing proof that the repository
identity and mutability settings are observable before apply.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
  terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The plan creates `aws_ecr_repository.app` as
`dev-petclinic` and keeps `image_tag_mutability = "IMMUTABLE"` explicit.

```text
# aws_ecr_repository.app will be created
+ resource "aws_ecr_repository" "app" {
    + force_delete         = true
    + image_tag_mutability = "IMMUTABLE"
    + name                 = "dev-petclinic"
```

## Artifact: Targeted repository contract test

**What it proves:** The repository name, privacy, tags, and immutable-tag
behavior are protected by automated tests.

**Why it matters:** This keeps the contract from drifting in later Terraform
edits.

**Command:**

```bash
./mvnw -Dtest=TerraformEcrRepositoryContractTest test
```

**Result summary:** The targeted contract test passed.

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Reviewer Conclusion

The dev app stack now exposes a deterministic private ECR repository contract
with immutable Git SHA tagging and reviewer-readable source and test coverage.
