# Dev App Network and ALB-Only Access Contract

The main application stack in `infra/terraform/app/dev` consumes remote state
that is already managed by the `state/dev` stack. This directory must never
create or modify the backend bucket or lock table directly.

This consumer assumes remote state is already managed by the state/dev stack.

## Stable Backend Contract

- Remote state is already managed by the state/dev stack.
- The main application stack uses the stable state key
  `app/dev/terraform.tfstate`.
- Backend settings stay outside reusable source through partial backend
  configuration in `backend.hcl.example` or equivalent operator-provided input.
- GitHub Actions, local operators, and the compose-managed `floci` environment
  must all reuse the same bucket, key, region, and lock-table contract.

## Initialization Workflow

1. Bootstrap or validate the backend resources from `infra/terraform/state/dev`.
2. Start the local AWS-resources environment with
   `docker compose -f infra/terraform/floci/docker-compose.yml up -d floci`
   when exercising the contract locally.
3. Initialize the consumer stack with:

   ```bash
   terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
   ```

4. In GitHub Actions, provide the same backend inputs through generated or
   secret-backed files rather than hard-coding them in `main.tf`.

## Network Reuse Contract

- Later ALB resources must use the exported public subnets from this stack.
- Later ECS services and tasks must use the exported private subnets from this
  stack.
- This stack keeps a single shared NAT gateway as a deliberate dev-cost
  tradeoff.

## Approved Traffic Path

The only approved inbound path is `internet client -> ALB -> ECS task on app port`.

private subnets alone are not treated as sufficient protection. The ECS task security group is the explicit boundary that rejects direct internet-originated traffic even when the tasks run in private subnets.

## Allowed Traffic Matrix

| Source | Destination | Protocol | Ports | Why allowed |
| --- | --- | --- | --- | --- |
| Internet client | ALB security group | TCP | 80 | Public users must reach the future ALB listener, but not the application tasks directly. |
| ALB security group | ECS task security group | TCP | 8080 | The ALB forwards application and health-check traffic to the Spring Boot container port only. |
| ECS task security group | Internet via NAT-backed private subnets | All required outbound traffic | All | v1 keeps ECS egress open so image pulls, telemetry, and approved dependencies do not break during the dev proof of concept. |

## Security-Group Roles

- The ALB security group is the only group that accepts `0.0.0.0/0` or `::/0`
  ingress.
- The ECS task security group accepts ingress only from the ALB security group
  and only on port `8080`.
- The ALB security group may send traffic only to the ECS task security group
  on port `8080`.
- ECS task egress remains default-open in v1; tighter ECS egress restrictions
  and VPC endpoint-based hardening stay as follow-on work after the first
  end-to-end deployment path is proven.
