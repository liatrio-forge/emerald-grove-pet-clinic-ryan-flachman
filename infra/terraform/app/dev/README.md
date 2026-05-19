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

## ECR Repository Contract

- The dev app stack defines one private ECR repository named
  `dev-petclinic`.
- immutable Git SHA tags are the only approved push and deploy reference
  format in v1.
- mutable convenience tags such as `latest` are intentionally excluded.

## ECR Lifecycle Policy Contract

- Use a lifecycle policy preview before applying the stack in AWS so a reviewer
  can confirm cleanup behavior from the planned policy document.
- The policy expires untagged images automatically.
- The policy retains the most recent 5 tagged Git SHA images with count-based
  retention in v1.
- Review the lifecycle policy preview in the sanitized
  `terraform -chdir=infra/terraform/app/dev plan -no-color` output and confirm
  the policy keeps tagged and untagged rules separate.

## CI Consumption and Destroy Contract

- CI pushes immutable Git SHA tags to `repository_uri` and later deployment
  workflows consume the same repository name without reconstructing it.
- The dev app stack exports exactly `repository_uri` and `repository_name` for
  downstream CI and ECS use.
- The repository destroy deletes all contained images because the repository is
  configured with explicit `force_delete` behavior for this dev-only POC.

## Approved Traffic Path

The only approved inbound path is `internet client -> ALB -> ECS task on app port`.

## Public Entrypoint Contract

- The dev app stack defines one internet-facing Application Load Balancer named
  `dev-public-http`.
- This ALB is the approved v1 public entrypoint resource contract for later
  listener, target-group, ECS, and DNS wiring.
- The ALB stays attached to the existing exported public subnets and the
  existing ALB security group so v1 does not reopen VPC or security-group
  design.

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
