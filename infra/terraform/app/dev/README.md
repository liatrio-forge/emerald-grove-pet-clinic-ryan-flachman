# Dev Remote-State Consumer Contract

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
