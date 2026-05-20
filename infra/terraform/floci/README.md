# Floci Local AWS Environment

This directory defines the compose-managed `floci` local AWS-resources
environment used to validate Terraform remote-state behavior before AWS
deployment.

This compose stack is the repository's local AWS-resources environment.

## Startup

Start the local environment with:

```bash
docker compose -f infra/terraform/floci/docker-compose.yml up -d floci
```

Stop it with:

```bash
docker compose -f infra/terraform/floci/docker-compose.yml down
```

## Remote-State Validation

- Use `backend.hcl.example` to supply local S3, DynamoDB, and STS endpoints at
  `http://127.0.0.1:4566`.
- Reuse the same bucket, key, region, and lock-table values as the dev consumer
  contract.
- Verify the consumer attachment path with:

  ```bash
  terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
  ```

## ALB-Only App-Access Contract

Use the repository-owned verification entry point to exercise the ALB-only app-access contract locally:

```bash
./scripts/verify-alb-only-app-access-contract.sh
```

That workflow starts `floci`, initializes the dev stack with
`backend.hcl.example`, runs:

```bash
terraform -chdir=infra/terraform/app/dev validate
terraform -chdir=infra/terraform/app/dev plan -no-color
```

and uses sanitized local credentials such as `AWS_ACCESS_KEY_ID=test`,
`AWS_SECRET_ACCESS_KEY=test`, and `AWS_EC2_METADATA_DISABLED=true` throughout.

## ECR Repository Contract

Use the repository-owned verification entry point to exercise the ECR
repository contract locally:

```bash
./scripts/verify-ecr-repository-contract.sh
```

That workflow starts `floci`, initializes the dev stack with
`backend.hcl.example`, runs:

```bash
terraform -chdir=infra/terraform/app/dev validate
terraform -chdir=infra/terraform/app/dev plan -no-color
```

Use placeholder credentials throughout local verification:
`AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and
`AWS_EC2_METADATA_DISABLED=true`.

Review the lifecycle policy preview in the plan output and confirm the
repository contract shows immutable tags, the separate tagged versus untagged
cleanup rules, and the deterministic repository outputs before applying the
stack in AWS.

## Public HTTP ALB Contract

Use the repository-owned verification entry point to exercise the public HTTP ALB contract locally:

```bash
./scripts/verify-public-http-alb-target-group-contract.sh
```

That workflow starts `floci`, initializes the dev stack with
`backend.hcl.example`, runs:

```bash
terraform -chdir=infra/terraform/app/dev validate
terraform -chdir=infra/terraform/app/dev plan -no-color
```

Use placeholder credentials throughout local verification:
`AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and
`AWS_EC2_METADATA_DISABLED=true`.

## ECS Runtime Contract

Use the repository-owned verification entry point to exercise the ECS runtime contract locally:

```bash
./scripts/verify-ecs-runtime-foundation-contract.sh
```

That workflow starts `floci`, initializes the dev stack with
`backend.hcl.example`, runs:

```bash
terraform -chdir=infra/terraform/app/dev validate
terraform -chdir=infra/terraform/app/dev plan -no-color
```

Use placeholder credentials throughout local verification:
`AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and
`AWS_EC2_METADATA_DISABLED=true`.

## GitHub OIDC IAM Contract

Use the repository-owned verification entry point to exercise the GitHub OIDC
IAM contract locally:

```bash
./scripts/verify-github-oidc-iam-contract.sh
```

That workflow starts `floci`, reuses `backend.hcl.example`, materializes the
partial backend stub for local verification, and runs:

```bash
terraform -chdir=infra/terraform/app/dev validate
terraform -chdir=infra/terraform/app/dev plan -no-color
```

Use placeholder credentials throughout local verification:
`AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and
`AWS_EC2_METADATA_DISABLED=true`.

Do not use long-lived AWS access keys for GitHub OIDC workflows. The point of
this contract is to verify short-lived GitHub OIDC role assumption instead.

## Baseline ECS Service Contract

Use the repository-owned verification entry point to exercise the baseline ECS service contract locally:

```bash
./scripts/verify-baseline-ecs-task-definition-service-contract.sh
```

That workflow starts `floci`, initializes the dev stack with
`backend.hcl.example`, runs:

```bash
terraform -chdir=infra/terraform/app/dev validate
terraform -chdir=infra/terraform/app/dev plan -no-color
```

and enforces the immutable `deploy_image` digest contract before any
AWS-backed deployment proof is attempted. Use placeholder credentials
throughout local verification: `AWS_ACCESS_KEY_ID=test`,
`AWS_SECRET_ACCESS_KEY=test`, and `AWS_EC2_METADATA_DISABLED=true`.

## Floci ECS-Through-ALB Runtime Check

Use the repository-owned verification entry point to verify the ALB health endpoint locally through the ECS service path:

```bash
./scripts/verify-floci-ecs-through-alb.sh
```

That floci ECS-through-ALB runtime check starts `floci`, initializes the dev
stack with `backend.hcl.example`, builds and pushes a digest-pinned deploy
image into local ECR, runs:

```bash
terraform -chdir=infra/terraform/app/dev validate
terraform -chdir=infra/terraform/app/dev apply -auto-approve
terraform -chdir=infra/terraform/app/dev destroy -auto-approve
```

and then verifies the ECS service state, target-group health, and ALB
reachability at `/actuator/health` before teardown.

Use placeholder credentials throughout local verification:
`AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and
`AWS_EC2_METADATA_DISABLED=true`.

This runtime check is still a local floci proof path. It does not replace the
separate live AWS evidence required for the final spec proof artifacts.

The current repository implementation verified the first hard blocker in this
path: local ECR creation returns `501` from the emulator, so this script
currently stops before ECS task launch. Until `floci` uses an emulator/runtime
that supports ECR, ECS, and ELBv2 together, the runtime script serves as a
capability check plus a clear failure boundary rather than a guaranteed
green-path proof.
