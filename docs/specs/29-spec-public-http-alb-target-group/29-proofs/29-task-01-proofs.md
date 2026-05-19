# Task 01 Proofs - Public ALB resource contract defined in the dev app stack

## Task Summary

This task adds the first explicit public ALB contract to
`infra/terraform/app/dev`. The stack now defines one reviewer-readable,
internet-facing Application Load Balancer that reuses the existing public
subnets and the existing ALB security group without reopening the VPC or
security-group design.

## What This Task Proves

- The dev app stack now contains one `aws_lb.public` resource with
  `load_balancer_type = "application"` and `internal = false`.
- The ALB reuses the existing exported public-subnet contract and the existing
  ALB security group.
- The public listener reachability contract remains pinned to port `80`.
- The source contract is covered by an automated repository test and validated
  through a sanitized Terraform verification run.

## Evidence Summary

- `TerraformPublicAlbContractTest` failed before the ALB resource existed, then
  passed after the minimum Terraform and documentation changes were added.
- The `floci`-backed Terraform verification flow reported
  `Success! The configuration is valid`.
- The sanitized plan output now shows one `aws_lb.public` resource named
  `dev-public-http` with `internal = false`, `load_balancer_type =
  "application"`, and repository-consistent tags.

## Artifact: Automated contract test for the public ALB resource

**What it proves:** The repository now enforces the ALB source contract at test
time, including the dedicated ALB resource, public-subnet reuse, existing ALB
security-group reuse, and listener-port continuity at `80`.

**Why it matters:** This is the RED-to-GREEN proof that future changes cannot
silently remove or reshape the public ALB contract.

**Command:**

```bash
./mvnw test -Dtest=TerraformPublicAlbContractTest
```

**Result summary:** The task-specific Maven test passed after the Terraform ALB
resource and naming local were added.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformPublicAlbContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Terraform validation for the dev app stack

**What it proves:** The Terraform configuration remains syntactically valid
after adding the public ALB resource.

**Why it matters:** Reviewable source changes are not enough unless Terraform
can still validate the stack before any AWS use.

**Command:**

```bash
terraform -chdir=infra/terraform/app/dev validate
```

**Result summary:** The `floci`-backed verification run completed
`terraform validate` successfully. Terraform emitted the repository's existing
backend deprecation warning, but the configuration itself validated.

```text
Success! The configuration is valid, but there were some
validation warnings as shown above.
```

## Artifact: Sanitized Terraform plan showing the public ALB contract

**What it proves:** The plan now includes exactly one public ALB resource with
the expected internet-facing contract, reviewer-readable name, and repository
tags.

**Why it matters:** This is the main proof that the source contract becomes a
concrete infrastructure plan without requiring reviewers to infer ALB shape
from multiple files.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
  terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The sanitized plan shows one `aws_lb.public` resource named
`dev-public-http` with `internal = false`, application load-balancer type, and
the expected `public-entrypoint` tag. The same run also preserves public
listener ingress on port `80`.

```text
# aws_lb.public will be created
+ resource "aws_lb" "public" {
    + internal           = false
    + load_balancer_type = "application"
    + name               = "dev-public-http"
    + tags               = {
        + "Name" = "dev-public-http"
        + "Role" = "public-entrypoint"
      }
  }

# aws_vpc_security_group_ingress_rule.alb_listener_ipv4 will be created
+ resource "aws_vpc_security_group_ingress_rule" "alb_listener_ipv4" {
    + cidr_ipv4 = "0.0.0.0/0"
    + from_port = 80
    + to_port   = 80
  }

# aws_vpc_security_group_ingress_rule.alb_listener_ipv6 will be created
+ resource "aws_vpc_security_group_ingress_rule" "alb_listener_ipv6" {
    + cidr_ipv6 = "::/0"
    + from_port = 80
    + to_port   = 80
  }

Plan: 26 to add, 0 to change, 0 to destroy.
```

## Reviewer Conclusion

Task `1.0` is implemented and reviewer-usable: the dev app stack now defines
one explicit internet-facing ALB contract, Terraform still validates, the plan
shows the intended public entrypoint shape, and the repository has an
automated guardrail that keeps the contract from regressing.
