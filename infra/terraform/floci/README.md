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
