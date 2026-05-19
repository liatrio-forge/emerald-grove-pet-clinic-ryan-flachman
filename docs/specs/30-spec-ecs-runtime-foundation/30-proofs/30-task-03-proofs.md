# Task 03 Proofs - ECS IAM role boundary and runtime outputs defined

## Task Summary

This task makes the ECS runtime IAM boundary explicit in
`infra/terraform/app/dev`. The stack now defines separate execution and task
roles, attaches only the AWS-managed execution baseline to the execution role,
and exports the lean runtime outputs that later ECS task-definition and service
work should consume directly.

## What This Task Proves

- The dev app stack defines separate ECS task execution and task roles with
  `ecs-tasks.amazonaws.com` trust relationships.
- Only the execution role receives the AWS-managed
  `AmazonECSTaskExecutionRolePolicy` baseline.
- The task role intentionally carries no application-specific AWS permissions
  in v1.
- The stack exports the lean runtime output surface:
  `ecs_cluster_arn`, `ecs_cluster_name`, `application_log_group_name`,
  `ecs_task_execution_role_arn`, and `ecs_task_role_arn`.

## Evidence Summary

- `TerraformEcsIamBoundaryContractTest` and
  `TerraformEcsRuntimeOutputsContractTest` both failed before the role and
  output contract existed, then passed after the minimum Terraform changes were
  added.
- The README now explains why the task role exists even though it has no app
  permissions in v1 and why execution-role responsibilities must remain
  separate.
- The sanitized Terraform plan shows both IAM roles, the managed execution
  policy attachment, and the exact new runtime outputs.

## Artifact: Automated IAM-boundary and outputs contract tests

**What it proves:** The repository now enforces the separate role boundary, the
AWS-managed execution baseline, the zero-permission task-role stance, and the
runtime output surface later ECS work will consume.

**Why it matters:** This prevents later infrastructure changes from silently
blending execution and application permissions or from expanding the runtime
handoff surface without review.

**Command:**

```bash
./mvnw -Dtest=TerraformEcsIamBoundaryContractTest,TerraformEcsRuntimeOutputsContractTest test
```

**Result summary:** Both task-specific contract tests passed after the IAM
roles, policy attachment, and outputs were added.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformEcsIamBoundaryContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformEcsRuntimeOutputsContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Runtime IAM-boundary documentation

**What it proves:** Operators now have a reviewer-facing explanation of the
role split and the intentional absence of task-role permissions in v1.

**Why it matters:** Terraform resources alone do not explain why two roles
exist before the application uses AWS APIs directly.

**Artifact path:** `infra/terraform/app/dev/README.md`

**Result summary:** The README now documents the separate execution and task
roles, explains the limited purpose of the execution baseline, and states that
the task role is intentionally empty until later app-facing AWS access is
specified.

```text
## Runtime IAM Boundary Contract

- The dev app stack defines a separate execution role and task role for ECS.
- The execution role keeps the AWS-managed
  `AmazonECSTaskExecutionRolePolicy` baseline so image pulls from ECR and log
  publishing to CloudWatch stay outside application code permissions.
- The task role intentionally exists with no application-specific AWS
  permissions in v1.
```

## Artifact: Sanitized Terraform plan showing IAM roles and runtime outputs

**What it proves:** The plan now includes both IAM roles, the managed
execution-policy attachment, and the exact new runtime outputs later ECS specs
should consume.

**Why it matters:** This is the concrete proof that the role boundary and
output contract are not just documented but actually materialize in the stack
plan.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
  terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The sanitized plan shows the two ECS roles with
`ecs-tasks.amazonaws.com` trust, a single managed execution-policy attachment,
and the exact runtime outputs `ecs_cluster_arn`, `ecs_cluster_name`,
`application_log_group_name`, `ecs_task_execution_role_arn`, and
`ecs_task_role_arn`.

```text
# aws_iam_role.ecs_task_execution will be created
+ resource "aws_iam_role" "ecs_task_execution" {
    + name = "dev-ecs-task-execution"
  }

# aws_iam_role_policy_attachment.ecs_task_execution will be created
+ resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
    + policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
    + role       = "dev-ecs-task-execution"
  }

# aws_iam_role.ecs_task will be created
+ resource "aws_iam_role" "ecs_task" {
    + name = "dev-ecs-task"
  }

Changes to Outputs:
  + application_log_group_name  = "/aws/ecs/dev-application"
  + ecs_cluster_arn             = (known after apply)
  + ecs_cluster_name            = "dev-shared"
  + ecs_task_execution_role_arn = (known after apply)
  + ecs_task_role_arn           = (known after apply)
```

## Reviewer Conclusion

Task `3.0` is reviewer-usable: the ECS runtime contract now has a clean IAM
split between execution and application roles, the execution baseline stays
minimal and AWS-managed, the task role remains intentionally empty in v1, and
later ECS specs can consume a stable, lean output surface directly.
