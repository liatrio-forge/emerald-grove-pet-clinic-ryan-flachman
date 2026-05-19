# Task 01 Proofs - Shared ECS cluster contract defined in the dev app stack

## Task Summary

This task adds the first explicit ECS runtime primitive to
`infra/terraform/app/dev`: one shared ECS cluster for later Fargate-attached
service work. The implementation keeps the cluster intentionally minimal so the
stack does not silently absorb service behavior, capacity-provider strategy,
ECS Exec, or Container Insights before those concerns have their own specs.

## What This Task Proves

- The dev app stack now defines exactly one `aws_ecs_cluster.shared` resource.
- The cluster uses a reviewer-readable local name and the stack's shared tag
  contract.
- The source contract is enforced by an automated repository test that blocks
  later drift into service-level ECS behavior.
- Terraform planning now shows the shared cluster in the dev stack with the
  expected minimal runtime contract.

## Evidence Summary

- `TerraformEcsClusterContractTest` failed before the cluster resource existed,
  then passed after the minimum Terraform changes were added.
- A temporary materialized backend copy validated successfully using the same
  backend values as `backend.hcl.example`, which matches the repository's
  existing verification pattern for partial backend configuration.
- The sanitized Terraform plan now shows one `aws_ecs_cluster.shared` resource
  named `dev-shared` with common tags and no service-level ECS additions.

## Artifact: Automated contract test for the shared ECS cluster

**What it proves:** The repository now enforces the presence of one minimal ECS
cluster, reviewer-readable naming, shared tags, and the absence of service
resources, capacity-provider strategy, ECS Exec, and Container Insights.

**Why it matters:** This is the RED-to-GREEN guardrail that keeps later Terraform
changes from broadening the runtime contract without an explicit follow-on spec.

**Command:**

```bash
./mvnw -Dtest=TerraformEcsClusterContractTest test
```

**Result summary:** The task-specific Maven test passed after the Terraform ECS
cluster resource and supporting local were added.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformEcsClusterContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Terraform validation of the cluster contract

**What it proves:** The Terraform source remains valid after adding the shared
ECS cluster contract.

**Why it matters:** Reviewable source changes are not enough unless Terraform
can still parse and validate the stack before later ECS specs build on it.

**Command:**

```bash
terraform -chdir=infra/terraform/app/dev validate
```

**Result summary:** Because the committed backend block is intentionally partial,
the validation proof used a temporary materialized copy with the same backend
values documented in `backend.hcl.example`. Terraform reported the configuration
as valid and only emitted the existing `dynamodb_table` deprecation warning.

```text
Warning: Deprecated Parameter
The parameter "dynamodb_table" is deprecated. Use parameter "use_lockfile" instead.

Success! The configuration is valid, but there were some
validation warnings as shown above.
```

## Artifact: Sanitized Terraform plan showing the shared ECS cluster

**What it proves:** The plan now includes one shared ECS cluster resource in the
dev app stack with reviewer-readable naming and the expected common tags.

**Why it matters:** This is the concrete infrastructure proof that the source
contract becomes an actual Terraform plan without introducing broader ECS
service behavior.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
  terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The sanitized plan shows one `aws_ecs_cluster.shared`
resource named `dev-shared`, tagged as a shared runtime resource, with no
capacity-provider, ECS Exec, Container Insights, or service-level additions in
this task.

```text
# aws_ecs_cluster.shared will be created
+ resource "aws_ecs_cluster" "shared" {
    + name   = "dev-shared"
    + tags   = {
        + "Application" = "emerald-grove-pet-clinic"
        + "Environment" = "dev"
        + "ManagedBy"   = "terraform"
        + "Name"        = "dev-shared"
        + "Role"        = "shared-runtime"
        + "Stack"       = "app-dev-network"
      }
  }

Plan: 29 to add, 0 to change, 0 to destroy.
```

## Reviewer Conclusion

Task `1.0` now has a stable, reviewer-usable ECS runtime foundation: the dev
app stack defines one explicit shared ECS cluster, the contract is guarded by
an automated test, Terraform planning shows the expected shared runtime shape,
and the implementation stays within the intentionally narrow v1 scope.
