# Task 02 Proofs - Application CloudWatch log-group contract defined

## Task Summary

This task makes the ECS runtime logging destination explicit in
`infra/terraform/app/dev`. The stack now defines one reviewer-readable
CloudWatch log group for future ECS application logs and overrides the default
indefinite retention behavior with explicit `7` day retention for the dev POC.

## What This Task Proves

- The dev app stack defines exactly one `aws_cloudwatch_log_group.application`
  resource.
- The log-group name is environment-scoped and reviewer-readable:
  `/aws/ecs/dev-application`.
- The log group pins `retention_in_days = 7` instead of accepting indefinite
  CloudWatch retention.
- The source contract is enforced by an automated repository test and visible
  in the sanitized Terraform plan.

## Evidence Summary

- `TerraformEcsLogGroupContractTest` failed before the log-group resource
  existed, then passed after the minimum Terraform and README changes were
  added.
- The README now explains why the dev POC intentionally overrides CloudWatch's
  indefinite default retention.
- The sanitized Terraform plan shows one log group named
  `/aws/ecs/dev-application` with explicit `7` day retention and shared tags.

## Artifact: Automated contract test for the application log group

**What it proves:** The repository now enforces the explicit log-group resource,
reviewer-readable naming, shared tags, and `7` day retention behavior.

**Why it matters:** This prevents later ECS work from silently relying on
implicit log-group creation or the default indefinite retention setting.

**Command:**

```bash
./mvnw -Dtest=TerraformEcsLogGroupContractTest test
```

**Result summary:** The task-specific Maven test passed after the CloudWatch
log-group resource and naming local were added.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformEcsLogGroupContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Runtime logging documentation

**What it proves:** Operators now have a short, reviewer-facing explanation of
the chosen ECS log destination and the explicit retention tradeoff.

**Why it matters:** Terraform source alone does not explain why indefinite
retention is intentionally overridden in this dev-only proof of concept.

**Artifact path:** `infra/terraform/app/dev/README.md`

**Result summary:** The README documents the `/aws/ecs/dev-application` log
group, explains that CloudWatch retains logs indefinitely by default, and
states that this stack intentionally pins `7` day retention.

```text
## Runtime Logging Contract

- The dev app stack defines one CloudWatch log group named
  `/aws/ecs/dev-application` for future ECS task logs.
- CloudWatch Logs retains log events indefinitely by default, but this dev POC
  intentionally overrides that default with explicit `7` day retention.
```

## Artifact: Sanitized Terraform plan showing explicit log retention

**What it proves:** The plan now includes one application log group with the
expected environment-scoped name and explicit `7` day retention.

**Why it matters:** This is the concrete proof that the source contract becomes
an actual infrastructure plan rather than a documentation-only promise.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
  terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The sanitized plan shows the `aws_cloudwatch_log_group`
resource named `/aws/ecs/dev-application` with `retention_in_days = 7` and the
expected shared tags.

```text
# aws_cloudwatch_log_group.application will be created
+ resource "aws_cloudwatch_log_group" "application" {
    + name              = "/aws/ecs/dev-application"
    + retention_in_days = 7
    + tags              = {
        + "Application" = "emerald-grove-pet-clinic"
        + "Environment" = "dev"
        + "ManagedBy"   = "terraform"
        + "Name"        = "/aws/ecs/dev-application"
        + "Role"        = "application-logs"
        + "Stack"       = "app-dev-network"
      }
  }

Plan: 30 to add, 0 to change, 0 to destroy.
```

## Reviewer Conclusion

Task `2.0` is implemented and reviewer-usable: the ECS runtime contract now
includes one explicit application log destination, the retention policy is
intentionally bounded for dev use, the README explains that decision, and the
repository has an automated guardrail that keeps the log-group contract from
regressing.
