# Task 03 Proofs - Traffic-flow documentation and v1 egress posture

## Task Summary

This task proves the approved `internet client -> ALB -> ECS task on app port`
flow is documented for reviewers, the ALB egress path is restricted to ECS
tasks on port `8080`, and ECS egress remains intentionally open in v1.

## What This Task Proves

- The Terraform plan models ALB egress to ECS tasks only on the application and
  health-check port contract.
- ECS task egress stays open in v1 and is visible in Terraform as explicit
  egress rules.
- The operator-facing README contains a reviewer-usable traffic matrix and
  explains why private subnets alone are not sufficient protection.

## Evidence Summary

- `TerraformAlbOnlyTrafficFlowContractTest` passes and synchronizes Terraform
  and documentation expectations.
- The plan creates explicit egress rules for ALB-to-ECS, ECS IPv4 egress, and
  ECS IPv6 egress.
- The README now includes the allowed-traffic matrix and the documented hardening
  follow-on boundary.

## Artifact: Traffic-flow contract system test

**What it proves:** The repository has an automated guardrail for the documented
traffic path, port contract, and v1 egress posture.

**Why it matters:** This keeps Terraform behavior and reviewer-facing
documentation synchronized as later ALB or ECS work evolves.

**Command:**

```bash
./mvnw -q -Dtest=TerraformAlbOnlyTrafficFlowContractTest test
```

**Result summary:** The targeted contract test exited successfully once the
README matrix and explicit egress rules were added.

```text
Exit status: 0
```

## Artifact: Terraform plan excerpt for egress posture

**What it proves:** The ALB can only send traffic to ECS tasks on port `8080`,
while ECS tasks keep explicit open egress in v1.

**Why it matters:** This is the runtime enforcement of the documented traffic
matrix.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The plan includes one ALB egress rule to ECS tasks on port
`8080` plus explicit IPv4 and IPv6 ECS egress rules.

```text
# aws_vpc_security_group_egress_rule.alb_to_ecs_tasks will be created
  + from_port = 8080
  + to_port   = 8080

# aws_vpc_security_group_egress_rule.ecs_task_ipv4_egress will be created
  + cidr_ipv4   = "0.0.0.0/0"
  + ip_protocol = "-1"

# aws_vpc_security_group_egress_rule.ecs_task_ipv6_egress will be created
  + cidr_ipv6   = "::/0"
  + ip_protocol = "-1"
```

## Artifact: README traffic matrix

**What it proves:** Reviewers can see the approved source, destination,
protocol, and port combinations without reverse-engineering Terraform.

**Why it matters:** The spec explicitly requires a junior-reviewer visible
traffic model and clear follow-on hardening boundaries.

**Artifact path:** `infra/terraform/app/dev/README.md`

**Result summary:** The README names the only approved inbound path, includes a
traffic matrix, states that private subnets alone are not sufficient
protection, and records tighter ECS egress restrictions and VPC endpoint-based
hardening as later work.

```text
The only approved inbound path is `internet client -> ALB -> ECS task on app port`.

| Source | Destination | Protocol | Ports | Why allowed |
| Internet client | ALB security group | TCP | 80 | ... |
| ALB security group | ECS task security group | TCP | 8080 | ... |
| ECS task security group | Internet via NAT-backed private subnets | All required outbound traffic | All | ... |
```

## Reviewer Conclusion

These artifacts show the written traffic model and the Terraform egress model
now agree: the ALB reaches ECS tasks only on port `8080`, ECS egress remains
open in v1, and the next hardening steps stay explicit and out of scope here.
