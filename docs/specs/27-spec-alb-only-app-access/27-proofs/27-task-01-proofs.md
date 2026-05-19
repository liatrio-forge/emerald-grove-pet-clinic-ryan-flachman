# Task 01 Proofs - Public ALB security-group contract

## Task Summary

This task proves the `dev` app stack now defines a dedicated, readable ALB
security group with configurable public listener ingress, while preserving the
existing remote-state consumer boundary.

## What This Task Proves

- The Terraform app stack includes a distinct ALB security group instead of
  reusing the ECS task group for internet-facing access.
- Public ingress terminates at the ALB security group on the listener contract
  port only.
- The infrastructure contract is covered by an automated system test and a
  successful Terraform validation path.

## Evidence Summary

- `TerraformAlbSecurityGroupContractTest` passes and asserts the ALB security
  group, listener-port input, and readable naming contract.
- The `floci`-backed verification script completes `terraform validate` and
  `terraform plan -no-color` successfully.
- The Terraform plan shows ALB listener ingress rules on port `80` and no
  public ingress rules attached to the ECS task security group.

## Artifact: ALB contract system test

**What it proves:** The repository has an automated guardrail for dedicated ALB
security-group resources, configurable listener ingress, and reviewer-visible
naming.

**Why it matters:** This keeps later ALB work from collapsing the intended
network boundary back into a shared or implicit security-group design.

**Command:**

```bash
./mvnw -q -Dtest=TerraformAlbSecurityGroupContractTest test
```

**Result summary:** The targeted contract test exited successfully after the
Terraform resources, listener-port input, and naming locals were added.

```text
Exit status: 0
```

## Artifact: Terraform validation path

**What it proves:** The ALB security-group contract is syntactically valid in
the same verification path used for local infrastructure review.

**Why it matters:** A passing source-level test is not enough if Terraform
cannot validate the configuration that reviewers and operators are expected to
use.

**Command:**

```bash
./scripts/verify-alb-only-app-access-contract.sh
```

**Result summary:** The script initialized the local backend, ran
`terraform -chdir=infra/terraform/app/dev validate`, and reported a valid
configuration. The only Terraform warning was the existing deprecated
`dynamodb_table` backend parameter.

```text
Success! The configuration is valid, but there were some
validation warnings as shown above.
```

## Artifact: Terraform plan excerpt for public ingress

**What it proves:** Public ingress is attached to the ALB security group on the
listener port and not to the ECS task security group.

**Why it matters:** This is the clearest reviewer-facing evidence that inbound
internet traffic reaches the ALB first.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The plan creates the ALB security group
`dev-application-load-balancer` and two public listener ingress rules on port
`80`. The ECS task group resource appears separately without any public CIDR
ingress rules.

```text
# aws_security_group.alb will be created
  + name = "dev-application-load-balancer"

# aws_vpc_security_group_ingress_rule.alb_listener_ipv4 will be created
  + cidr_ipv4 = "0.0.0.0/0"
  + from_port = 80
  + to_port   = 80

# aws_vpc_security_group_ingress_rule.alb_listener_ipv6 will be created
  + cidr_ipv6 = "::/0"
  + from_port = 80
  + to_port   = 80

# aws_security_group.ecs_task will be created
  + name = "dev-ecs-task"
```

## Reviewer Conclusion

These artifacts show the public edge is now explicit: internet ingress is
modeled on a dedicated ALB security group, validated locally, and protected by
an automated contract test.
