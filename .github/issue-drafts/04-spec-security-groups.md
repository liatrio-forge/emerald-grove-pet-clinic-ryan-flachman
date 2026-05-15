# Spec 4: Add security groups enforcing ALB-only access to the app

## Summary

Define the security group model that makes the ECS service reachable only from the ALB and not directly from the public internet.

## Problem statement

“Private ECS behind a public ALB” is not achieved by subnet placement alone. The requirement depends on security groups enforcing the allowed traffic path. If these rules are not explicitly designed, the POC can accidentally expose the app directly.

## In scope

- ALB security group
- ECS security group
- ingress rules
- egress rules
- app port restrictions so only ALB can reach ECS

## Out of scope

- ALB resource creation
- ECS service creation
- IAM
- WAF
- NACL tuning

## Decisions already made

- ECS tasks must only be accessible from the ALB
- Public users should connect to the ALB, not directly to tasks
- App port is expected to be the Spring Boot container port

## Deliverables

- Security group design for ALB and ECS
- Clear statement of allowed traffic flows

## Acceptance criteria

- Internet ingress is allowed to the ALB only
- ECS ingress is limited to the ALB security group on the app port
- The design is compatible with later ECS service and target group wiring

## Dependencies

- Spec 1 for app port/health assumptions
- Spec 3 for VPC/subnet model

## Implementation notes

- The spec should state whether ECS egress is fully open for simplicity or intentionally restricted
- The design should not rely on subnet privacy as the sole protective measure

## Risks and open questions

- Whether later AWS integrations require broader egress than initially expected

## Suggested labels

- `spec`
- `aws`
- `security`
- `networking`
