# Spec 2: Bootstrap Terraform remote state for the dev environment

## Summary

Define the Terraform backend foundation for the dev-only AWS POC using S3 for remote state and DynamoDB for locking.

## Problem statement

The infrastructure workflows are intended to run from GitHub Actions. That requires stable remote Terraform state and state locking. Running Terraform from ephemeral GitHub runners with local state is fragile and not acceptable even for a POC that may be repeatedly created and destroyed.

## In scope

- S3 bucket for Terraform state
- DynamoDB table for Terraform state locking
- Backend configuration approach
- Naming and tagging conventions for backend resources
- Decision on how backend resources are bootstrapped

## Out of scope

- Application infrastructure
- ECS
- ALB
- ECR
- GitHub Actions implementation beyond backend assumptions

## Decisions already made

- Terraform backend will use `S3 + DynamoDB`
- Only one environment exists initially: `dev`

## Deliverables

- Terraform backend design
- Bootstrap sequence for backend resources
- Remote state usage guidance for later specs

## Acceptance criteria

- Terraform backend ownership is unambiguous
- State locking strategy is documented
- Later Terraform specs can assume remote state without additional design work
- The bootstrap approach avoids self-referential destroy/apply problems

## Dependencies

- None

## Implementation notes

- The biggest design concern is whether backend resources live:
  - in a separate bootstrap stack, or
  - as manually created one-time resources
- The main app stack destroy workflow should not accidentally deadlock or destroy its own backend in an unsafe order

## Risks and open questions

- Whether backend resources are considered shared/team assets versus POC-local assets
- Whether destroy of the main stack should exclude backend resources entirely

## Suggested labels

- `spec`
- `terraform`
- `state`
- `aws`
