# Spec 8: Provision the baseline ECS task definition and service

## Summary

Define the baseline ECS workload configuration that runs the Spring Boot app behind the ALB.

## Problem statement

The application cannot actually run in AWS until a task definition and ECS service are defined. This is the point where the container contract, networking, logging, and ALB wiring converge. It deserves its own spec because it is the first true end-to-end infrastructure slice.

## In scope

- baseline task definition
- initial image reference strategy
- container port mapping
- CPU/memory sizing
- log configuration
- environment variable injection approach
- ECS service
- target group attachment
- health grace period
- deployment settings for a single-task POC

## Out of scope

- automatic CI-driven image rollout
- autoscaling
- multi-environment behavior
- database migration away from H2

## Decisions already made

- `desired_count = 1`
- H2 remains for the POC
- ECS service will be fronted by the ALB
- CI, not Terraform, will own later task definition revisions

## Deliverables

- ECS task definition baseline
- ECS service baseline
- First running app topology in AWS

## Acceptance criteria

- ECS task can run successfully with the chosen image contract
- ALB routes traffic to a healthy target
- Service is only reachable through the ALB
- Logging and health settings are explicit

## Dependencies

- Spec 1 for container contract
- Spec 4 for security groups
- Spec 5 for image source assumptions
- Spec 6 for ALB/target group
- Spec 7 for ECS cluster/logging/execution role

## Implementation notes

- The spec should define whether Terraform uses a placeholder image URI, seed image, or other bootstrap strategy
- The single-task H2 limitation should be called out explicitly so rollout expectations are realistic

## Risks and open questions

- Whether startup timing causes false negatives during initial health checks
- Whether bootstrap of the very first image requires a temporary manual step

## Suggested labels

- `spec`
- `aws`
- `ecs`
- `terraform`
- `deployment`
