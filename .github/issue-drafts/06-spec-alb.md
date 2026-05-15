# Spec 6: Provision the ALB and target group for public HTTP access

## Summary

Define the public load-balancing layer that exposes the application over HTTP and forwards traffic to ECS tasks.

## Problem statement

The public entrypoint for the POC is an ALB. Without a concrete ALB, listener, and target group design, the ECS service cannot be reached consistently and health checks cannot be aligned with application readiness.

## In scope

- Internet-facing ALB
- target group
- HTTP listener on port `80`
- health check path and matcher
- ALB outputs such as DNS name

## Out of scope

- HTTPS
- ACM
- Route 53
- ECS service internals beyond target group integration

## Decisions already made

- Public endpoint is HTTP only for v1
- No domain is currently available
- ALB sits in public subnets and fronts the ECS service

## Deliverables

- ALB design
- Target group health model
- Public URL output contract

## Acceptance criteria

- ALB is reachable from the internet over HTTP
- Target group health checks align with the app health contract
- ALB is ready for later ECS service attachment

## Dependencies

- Spec 1 for health endpoint and port assumptions
- Spec 3 for public subnet design
- Spec 4 for security group design

## Implementation notes

- The spec should be explicit that HTTPS is deferred and not silently implied
- Health check thresholds and success codes should be chosen intentionally, not left as defaults without discussion

## Risks and open questions

- Whether startup latency requires a more forgiving health-check posture

## Suggested labels

- `spec`
- `aws`
- `alb`
- `terraform`
