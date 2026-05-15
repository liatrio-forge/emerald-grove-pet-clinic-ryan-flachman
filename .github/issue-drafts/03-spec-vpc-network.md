# Spec 3: Provision the base VPC network for the dev POC

## Summary

Define the dedicated AWS network layout that supports a public ALB and private ECS tasks for the dev deployment.

## Problem statement

The requested architecture depends on clear network separation. The ALB must be internet-facing, while ECS tasks must not be directly reachable from the internet. This cannot be implemented safely without a deliberate subnet and routing design.

## In scope

- VPC
- CIDR strategy
- public subnets
- private subnets
- internet gateway
- route tables
- subnet associations

## Out of scope

- Security groups
- ALB resource creation
- ECS cluster and service
- ECR
- NAT expansion unless the design explicitly requires it

## Decisions already made

- Use a new dedicated VPC
- Use public ALB + private ECS task placement
- Optimize for low-cost dev experimentation

## Deliverables

- Network topology for `dev`
- Reusable subnet model for later ALB and ECS specs

## Acceptance criteria

- Public subnets exist for the ALB
- Private subnets exist for ECS tasks
- Routing intent is explicit
- The design is simple enough for a POC but still honors the public/private separation requirement

## Dependencies

- Spec 2 for Terraform backend design

## Implementation notes

- Cost and realism tradeoffs should be documented rather than hidden
- The spec should be explicit about whether any additional egress infrastructure is intentionally omitted for cost reasons

## Risks and open questions

- Whether strict private-subnet egress requirements introduce more recurring cost than justified for a POC
- Whether the chosen layout leaves enough room for later expansion if the POC grows

## Suggested labels

- `spec`
- `aws`
- `networking`
- `terraform`
