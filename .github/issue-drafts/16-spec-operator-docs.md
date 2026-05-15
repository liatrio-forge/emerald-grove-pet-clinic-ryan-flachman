# Spec 16: Document operator flow for create, deploy, and destroy

## Summary

Define the documentation needed so another engineer can bootstrap, apply, deploy, and tear down the POC without tribal knowledge.

## Problem statement

The technical implementation will span Terraform, ECS, ECR, ALB, IAM, and multiple GitHub Actions workflows. Without explicit documentation, the POC will only be usable by the person who built it, and cleanup/recreation will become error-prone.

## In scope

- README or deployment-doc updates
- backend bootstrap order
- infra apply workflow usage
- deploy workflow usage
- destroy workflow usage
- known POC limitations
- expected cost drivers and caveats

## Out of scope

- broad architecture documentation unrelated to deployment
- production operations runbooks

## Decisions already made

- The POC is dev-only
- H2 remains in use
- Public access is HTTP only in v1
- Terraform apply and destroy are manual
- App deploy on merge to `main` is automatic

## Deliverables

- Operator-facing deployment documentation
- Clear explanation of what the POC is and is not

## Acceptance criteria

- Another engineer can create the environment from docs
- Another engineer can deploy the app from docs
- Another engineer can tear down the environment from docs
- The limitations of H2 and HTTP-only access are explicit and not hidden

## Dependencies

- Depends on the earlier specs for final technical details

## Implementation notes

- Documentation should explain why the POC is intentionally not production-like in some areas
- Cost warnings should be explicit enough to prevent accidental “leave it running” behavior

## Risks and open questions

- If backend bootstrap is handled separately, the docs must make that sequence impossible to miss

## Suggested labels

- `spec`
- `documentation`
- `aws`
- `cicd`
