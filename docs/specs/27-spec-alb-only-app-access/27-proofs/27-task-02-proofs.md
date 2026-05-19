# Task 02 Proofs - ECS task ingress limited to the ALB

## Task Summary

This task proves the application-tier security group accepts traffic only from
the ALB security group, only on the Spring Boot container port `8080`, and
exports the identifiers needed for later ECS and ALB integration work.

## What This Task Proves

- The ECS task security group is a separate resource from the ALB security
  group.
- ECS ingress is sourced by security-group reference, not public or broad CIDR
  ranges.
- The Terraform outputs publish both security-group identifiers for downstream
  specs.

## Evidence Summary

- `TerraformEcsTaskSecurityGroupContractTest` passes and asserts ALB-only
  ingress on port `8080`.
- The Terraform plan shows the ingress rule is a security-group reference from
  the ALB to the ECS task group.
- The plan publishes `alb_security_group_id` and
  `ecs_task_security_group_id`.

## Artifact: ECS ingress contract system test

**What it proves:** The repository has an automated guardrail for ALB-only ECS
ingress on port `8080`.

**Why it matters:** This prevents future Terraform changes from reopening ECS
tasks to public CIDRs or subnet-wide access.

**Command:**

```bash
./mvnw -q -Dtest=TerraformEcsTaskSecurityGroupContractTest test
```

**Result summary:** The targeted test exited successfully after the ECS task
security group, ALB-sourced ingress rule, and output identifiers were added.

```text
Exit status: 0
```

## Artifact: Terraform plan excerpt for ALB-only ECS ingress

**What it proves:** ECS task ingress is restricted to port `8080` from the ALB
security group only.

**Why it matters:** This is the core network-access contract for keeping the
application private behind the future ALB.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The plan creates a dedicated `dev-ecs-task` security group
and a single ingress rule that references the ALB security group on port
`8080`. No public IPv4 or IPv6 ingress rule is attached to the ECS task group.

```text
# aws_security_group.ecs_task will be created
  + name = "dev-ecs-task"

# aws_vpc_security_group_ingress_rule.ecs_task_from_alb will be created
  + referenced_security_group_id = (known after apply)
  + from_port                    = 8080
  + to_port                      = 8080
```

## Artifact: Output contract for downstream attachment

**What it proves:** Later ECS service and ALB work can consume stable security-
group identifiers without rediscovering resource addresses manually.

**Why it matters:** Clear outputs reduce integration mistakes in follow-on
specs.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The plan includes output values for both security groups,
making the intended downstream contract explicit to reviewers.

```text
Changes to Outputs:
  + alb_security_group_id      = (known after apply)
  + ecs_task_security_group_id = (known after apply)
```

## Reviewer Conclusion

These artifacts show the application tier is now explicitly private: only the
ALB security group can reach ECS tasks, and the downstream attachment contract
is visible in Terraform outputs.
