# Spec 7: Provision the ECS cluster, logging, and IAM execution role

## Summary

Define the shared ECS runtime foundation required before any service can run cleanly.

## Problem statement

Even before the application service exists, ECS requires supporting resources such as a cluster, a log destination, and an execution role that can pull images and write logs. These are stable platform pieces and should be specified separately from service rollout mechanics.

## In scope

- ECS cluster
- CloudWatch log group
- ECS task execution role
- minimal IAM policies needed for image pull and log publishing
- task role strategy if future app AWS access is needed

## Out of scope

- ECS service
- task definition rollout logic
- autoscaling
- GitHub workflow permissions

## Decisions already made

- Runtime platform is ECS Fargate
- Logs should go to CloudWatch
- Task execution role is required even if the app itself needs no direct AWS API access

## Deliverables

- ECS shared runtime resource design
- Logging destination contract
- IAM execution baseline

## Acceptance criteria

- ECS has a cluster to run services in
- Task execution has the permissions required to pull images and publish logs
- Log group naming and retention behavior are explicit

## Dependencies

- Spec 2 for Terraform backend design
- Spec 5 for ECR assumptions

## Implementation notes

- The spec should keep task role and execution role responsibilities distinct
- Avoid over-scoping this into service deployment logic

## Risks and open questions

- Whether log retention should be short-lived to minimize cost for the POC

## Suggested labels

- `spec`
- `aws`
- `ecs`
- `iam`
- `observability`
