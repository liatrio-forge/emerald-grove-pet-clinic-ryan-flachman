# Dev App Runtime Infrastructure Contract

The main application stack in `infra/terraform/app/dev` owns runtime
infrastructure only. It consumes remote state that is already managed by the
`state/dev` stack and assumes GitHub workflow identity is already managed by the
`identity/dev` stack.

This directory must never create or modify the backend bucket, lock table,
GitHub OIDC provider, or GitHub workflow IAM roles directly.

In other words, `app/dev` does not own the GitHub OIDC provider or the GitHub
workflow IAM roles.

## Stable Backend Contract

- Remote state is already managed by the `state/dev` stack.
- remote state is already managed by the state/dev stack.
- GitHub workflow identity is already managed by the `identity/dev` stack.
- The main application stack uses the stable state key
  `app/dev/terraform.tfstate`.
- Backend settings stay outside reusable source through partial backend
  configuration in `backend.hcl.example` or equivalent operator-provided input.
- GitHub Actions, local operators, and the compose-managed `floci` environment
  must all reuse the same bucket, key, region, and lock-table contract.

## Initialization Workflow

1. Bootstrap or validate the backend resources from `infra/terraform/state/dev`.
2. Bootstrap or validate the GitHub workflow identity resources from
   `infra/terraform/identity/dev`.
3. Start the local AWS-resources environment with
   `docker compose -f infra/terraform/floci/docker-compose.yml up -d floci`
   when exercising the contract locally.
4. Initialize the consumer stack with:

   ```bash
   terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
   ```

5. In GitHub Actions, provide the same backend inputs through generated or
   secret-backed files rather than hard-coding them in `main.tf`.

## Bootstrap Workflow Exception

- The repository defines one bootstrap-only workflow named
  `Bootstrap Dev Infrastructure` at `.github/workflows/bootstrap-dev-infra.yml`.
- This workflow exists to create the full `state/dev` -> `identity/dev` ->
  `app/dev` lifecycle in one reviewer-visible sequence.
- The bootstrap workflow runs only from the `main branch`, requires
  `workflow_dispatch`, and requires the operator to type `bootstrap dev`.
- The bootstrap-capable job uses a separate protected `dev-bootstrap`
  environment so persistent admin-backed bootstrap credentials stay isolated
  from the normal `dev` OIDC path.
- This is a standing bootstrap exception for the POC. Steady-state Terraform
  apply, image publish, ECS deploy, and app destroy workflows must continue
  using GitHub OIDC instead of long-lived AWS credentials.

## Bootstrap Workflow Inputs

Set these protected GitHub environment secrets on `dev-bootstrap` before the
first live bootstrap:

| Secret | Why it exists |
| --- | --- |
| `BOOTSTRAP_AWS_ACCESS_KEY_ID` | Temporary admin-backed AWS access key for the first live bootstrap only. |
| `BOOTSTRAP_AWS_SECRET_ACCESS_KEY` | Temporary admin-backed AWS secret key paired with the bootstrap access key. |
| `BOOTSTRAP_AWS_SESSION_TOKEN` | Optional session token when the admin credentials are temporary STS credentials. |

Use the repository-scoped variable `AWS_REGION` for the target region, and pass
no application image input during foundation bootstrap.

## Bootstrap Workflow Sequence

1. Run `Bootstrap Dev Infrastructure` from `main`.
2. Type `bootstrap dev`.
3. Let the workflow apply `infra/terraform/state/dev` with local backend mode.
4. Let the workflow materialize backend config, initialize `infra/terraform/app/dev`,
   and apply the app stack.
5. Copy the workflow summary outputs into the protected GitHub variables listed
   below.
6. Publish into the new repository before the first ECS deployment.
   In other words, publish into the new repository before the first ECS deployment.
7. Keep the bootstrap secrets in `dev-bootstrap` as the documented POC
   exception for future foundation rebuilds and final teardown work.

## Manual Terraform Apply Workflow Contract

- The repository defines one manual workflow named `Terraform Apply Dev` at
  `.github/workflows/terraform-apply-dev.yml`.
- The workflow is `dev environment only` and allows apply execution only from
  the `main branch`.
- Operators must use `workflow_dispatch` and type `apply dev` before the
  workflow can proceed beyond the initial safety gate.
- Operators must also provide `deploy_image` as one deploy image reference
  pinned by digest.
- The apply-capable job uses the protected `dev` environment so reviewer approval
  happens before protected configuration is available.
- The workflow uses GitHub OIDC with an assumed AWS role instead of long-lived
  AWS access keys stored in repository secrets.
- The workflow reuses the existing backend contract through generated backend
  configuration that follows `backend.hcl.example`.
- The workflow creates and applies an `exact saved Terraform plan` for
  `infra/terraform/app/dev`; it does not recompute a fresh implicit apply plan.
- This is the post-publish ECS deployment step for the first runtime rollout
  and later reviewed image updates.

## Manual Terraform Destroy Workflow Contract

- The repository defines one manual workflow named `Terraform Destroy Dev` at
  `.github/workflows/terraform-destroy-dev.yml`.
- The workflow uses `workflow_dispatch`, requires the operator to type
  `destroy dev`, and allows destructive execution only from the `main branch`.
- The destroy-capable job uses the protected `dev-destroy` environment so
  destructive access stays behind a separate reviewer boundary.
- The workflow uses GitHub OIDC with `TERRAFORM_DESTROY_ROLE_ARN` and reuses
  the same `TF_STATE_BUCKET` and `TF_LOCK_TABLE` variable names as the apply
  workflow.
- This workflow destroys only `app/dev` runtime infrastructure and keeps
  backend and identity resources intact for later rebuilds.
- It is not the final foundation teardown path; use the bootstrap-destroy
  workflow for the full `app/dev` -> `identity/dev` -> `state/dev` teardown.

## Normal App Rebuild Sequence

1. Run `Terraform Destroy Dev` from `main`.
2. Type `destroy dev`.
3. Let the workflow destroy only the `app/dev` stack.
4. Keep `state/dev` and `identity/dev` in place.
5. Run `Terraform Apply Dev` with the current `deploy_image` digest to recreate
   `app/dev` with the same stable GitHub variable names.

Normal `app/dev` destroy and recreate is distinct from final foundation
teardown.

## GitHub OIDC Terraform Role Matrix

| Role | Trusted GitHub subject | Environment boundary | Why it exists |
| --- | --- | --- | --- |
| `terraform-apply-dev` | `repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:environment:dev` | protected `dev` environment | Allows reviewed Terraform apply work for the dev stack without sharing destroy access. |
| `terraform-destroy-dev` | `repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:environment:dev-destroy` | separate protected `dev-destroy` environment | Keeps destructive cleanup behind a stricter approval boundary than normal apply work. |
| `app-publish-dev` | `repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:environment:dev` | protected `dev` environment | Allows manual ECR image publication without granting Terraform mutation or ECS rollout authority. |
| `app-deploy-dev` | `repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:environment:dev` | protected `dev` environment | Keeps ECS rollout access narrower than Terraform mutation access for downstream deployment workflows. |

The Terraform apply and destroy roles intentionally share one broad proof-of-concept
policy document so the stack can move forward without premature least-privilege
authoring, but role trust remains split by environment subject.

These roles are owned by `infra/terraform/identity/dev`, not by the runtime
stack in this directory.

## Terraform IAM-sensitive actions

The shared Terraform GitHub policy is intentionally broad for the dev proof of
concept, but it still avoids unconstrained administrator access. Reviewer-sensitive
IAM actions are called out explicitly in policy source rather than hidden behind
`iam:*` or `Action: "*"`.

The current policy intentionally grants these IAM-sensitive actions:

- `iam:CreateRole`
- `iam:DeleteRole`
- `iam:AttachRolePolicy`
- `iam:DetachRolePolicy`
- `iam:PutRolePolicy`
- `iam:DeleteRolePolicy`
- `iam:PassRole`
- `iam:UpdateAssumeRolePolicy`
- read and tagging actions required to inspect and manage the GitHub OIDC
  provider and Terraform-managed roles

## GitHub configuration contract

The downstream GitHub workflows must consume the following variables and keep
deployment-sensitive AWS values on protected environments instead of long-lived
AWS secrets.

| Variable | Ownership | Why it belongs there |
| --- | --- | --- |
| `AWS_REGION` | Repository-scoped | Stable non-secret default reused by apply, destroy, and deploy automation. |
| `TERRAFORM_APPLY_ROLE_ARN` | Environment-scoped (`dev`) | Protected value consumed by Terraform apply jobs that assume the `terraform-apply-dev` role. |
| `TERRAFORM_DESTROY_ROLE_ARN` | Environment-scoped (`dev-destroy`) | Protected value consumed by destructive cleanup workflows only. |
| `APP_PUBLISH_ROLE_ARN` | Environment-scoped (`dev`) | Protected value consumed by manual ECR publish workflows so image publication stays separate from Terraform and ECS rollout authority. |
| `APP_DEPLOY_ROLE_ARN` | Environment-scoped (`dev`) | Protected value consumed by ECS rollout workflows so app deploy stays separate from Terraform. |
| `REPOSITORY_URI` | Environment-scoped (`dev`) | Protected value that points the manual publish workflow at the Terraform-managed `repository_uri` output without reconstructing repository names in YAML. |
| `TF_STATE_BUCKET` | Environment-scoped (`dev`) | Backend-state value coupled to the protected dev stack. |
| `TF_LOCK_TABLE` | Environment-scoped (`dev`) | Backend-lock value coupled to the protected dev stack. |

All AWS-assuming jobs must request `id-token: write`, declare the protected
GitHub environment whose exact `sub` claim the trusted role permits, and avoid
long-lived `AWS_ACCESS_KEY_ID` or `AWS_SECRET_ACCESS_KEY` repository secrets.
Downstream workflows must use GitHub OIDC and do not use long-lived AWS access keys.

Bootstrap output promotion requirements:

- Promote `TERRAFORM_APPLY_ROLE_ARN`, `TERRAFORM_DESTROY_ROLE_ARN`,
  `APP_PUBLISH_ROLE_ARN`, `APP_DEPLOY_ROLE_ARN`, `REPOSITORY_URI`,
  `TF_STATE_BUCKET`, and `TF_LOCK_TABLE` from the bootstrap workflow summary
  into the protected GitHub variables above.
- The bootstrap workflow summary is the intended handoff point for the standing
  bootstrap exception. After promotion, use the normal OIDC workflows for day
  to day operations and keep `dev-bootstrap` secrets protected for future
  foundation lifecycle actions.

## Final Cleanup Checklist

After the final `Bootstrap Destroy Dev Infrastructure` workflow completes:

1. Set the AWS-derived GitHub variable values to empty strings.
   In other words, set the AWS-derived GitHub variable values to empty strings.
2. Preserve the variable names for future reuse.
   This means preserve the variable names for future reuse.
3. Keep `AWS_REGION`, `TERRAFORM_APPLY_ROLE_ARN`, `TERRAFORM_DESTROY_ROLE_ARN`,
   `APP_PUBLISH_ROLE_ARN`, `APP_DEPLOY_ROLE_ARN`, `REPOSITORY_URI`,
   `TF_STATE_BUCKET`, and `TF_LOCK_TABLE` in place with intentionally blank
   AWS-derived values after teardown.
4. Keep the `dev-bootstrap` secrets as the standing POC bootstrap exception.

Protected environment matrix:

- `dev`: `TERRAFORM_APPLY_ROLE_ARN`, `APP_PUBLISH_ROLE_ARN`,
  `APP_DEPLOY_ROLE_ARN`, `REPOSITORY_URI`, `TF_STATE_BUCKET`, `TF_LOCK_TABLE`
- `dev-destroy`: `TERRAFORM_DESTROY_ROLE_ARN`
- `dev-bootstrap`: `BOOTSTRAP_AWS_ACCESS_KEY_ID`,
  `BOOTSTRAP_AWS_SECRET_ACCESS_KEY`, `BOOTSTRAP_AWS_SESSION_TOKEN`

## GitHub OIDC verification workflow

Use the repository-owned verification entry point to validate the IAM contract
locally before relying on live AWS:

```bash
./scripts/verify-github-oidc-iam-contract.sh
```

That workflow starts `floci`, reuses `backend.hcl.example`, materializes the
partial backend stub for local verification, and runs:

```bash
terraform -chdir=infra/terraform/app/dev validate
terraform -chdir=infra/terraform/app/dev plan -no-color
```

Use placeholder credentials throughout this verification flow:
`AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and
`AWS_EC2_METADATA_DISABLED=true`.

## Manual Terraform Apply Verification Commands

Use GitHub CLI after a run to review the workflow and artifacts:

```bash
gh run list --workflow "Terraform Apply Dev"
gh run view <run-id> --log
gh run download <run-id> --name terraform-apply-dev-plan
```

These verification commands let a maintainer inspect the plan job logs, confirm
the saved artifact exists, and review apply-job diagnostics without direct
runner shell access.

## Manual Terraform Apply Scope Boundaries

- In scope: manual `dev` stack apply, reviewer approval, typed confirmation,
  GitHub OIDC authentication, one `deploy_image` digest, reviewed saved-plan
  creation, and exact-plan apply.
- Out of scope: image build, destroy workflow, foundation stack teardown, and
  unrelated CI/CD orchestration.

## Runtime Ownership Boundary

- `state/dev` owns the backend bucket and lock table.
- `identity/dev` owns the GitHub OIDC provider and workflow IAM roles.
- `app/dev` owns runtime infrastructure such as VPC, subnets, ALB, ECR, ECS,
  and log groups.
- Normal `app/dev` destroy and recreate must leave backend and identity
  resources intact.

## Manual Dev ECR Publish Workflow Contract

- The repository defines one manual workflow named `Manual Dev ECR Publish` at
  `.github/workflows/manual-dev-ecr-publish.yml`.
- The workflow starts only through `workflow_dispatch` and allows publication
  only from the `main branch`.
- Operators must type `publish dev image` before the workflow can continue
  beyond the initial safety gate.
- The publish-capable job uses the protected `dev` environment, so reviewer
  approval happens before protected AWS configuration is available.
- The workflow uses GitHub OIDC with `APP_PUBLISH_ROLE_ARN` and does not use
  long-lived AWS access keys.
- The workflow consumes `REPOSITORY_URI` directly from protected GitHub
  configuration rather than reconstructing repository names in YAML.
- The workflow builds from the repository-owned root `Dockerfile` and publishes
  exactly one immutable Git SHA image per successful run.
- The workflow surfaces the fully qualified image reference plus the pushed
  digest in workflow-visible output for later rollout review.
- After publish, copy the published digest into the `deploy_image` input on
  `Terraform Apply Dev`.
- In other words, copy the published digest into the deploy_image input.
- The workflow does not roll out ECS on its own.

## Manual Dev ECR Publish Verification Commands

Use GitHub CLI and AWS CLI after a run to review the workflow end to end:

```bash
gh workflow run "Manual Dev ECR Publish" --ref main -f confirmation="publish dev image"
gh run list --workflow "Manual Dev ECR Publish"
gh run view <run-id> --log
aws ecr describe-images --repository-name <repository-name> --image-ids imageTag=<full-git-sha>
```

These verification commands let a maintainer confirm the workflow name,
inspect the Maven and publish logs, and verify that the expected SHA-tagged
image exists in ECR.

If you already know the run id, `gh run view --log <run-id>` is the direct log
inspection form for this workflow.

## Manual Dev ECR Publish Scope Boundaries

- In scope: manual image publication, typed confirmation, `main`-branch
  restriction, protected `dev` environment approval, GitHub OIDC
  authentication, one immutable Git SHA tag, and workflow-visible image/digest
  output.
- Out of scope: automatic publish, mutable convenience tags, direct ECS
  deployment, and any repo-owned verification script for this workflow.

## Network Reuse Contract

- Later ALB resources must use the exported public subnets from this stack.
- Later ECS services and tasks must use the exported private subnets from this
  stack.
- This stack keeps a single shared NAT gateway as a deliberate dev-cost
  tradeoff.

## Runtime Logging Contract

- The dev app stack defines one CloudWatch log group named
  `/aws/ecs/dev-application` for future ECS task logs.
- CloudWatch Logs retains log events indefinitely by default, but this dev POC
  intentionally overrides that default with explicit `7` day retention.
- Later task-definition work should reuse `application_log_group_name` instead
  of reconstructing the log destination or relying on implicit log-group
  creation.

## Runtime IAM Boundary Contract

- The dev app stack defines a separate execution role and task role for ECS.
- The execution role keeps the AWS-managed
  `AmazonECSTaskExecutionRolePolicy` baseline so image pulls from ECR and log
  publishing to CloudWatch stay outside application code permissions.
- The task role intentionally exists with no application-specific AWS
  permissions in v1. That preserves a clean future expansion point for app AWS
  access without letting the task role absorb execution responsibilities.
- Later ECS task-definition work should consume `ecs_task_execution_role_arn`
  and `ecs_task_role_arn` directly rather than recreating or merging the role
  boundary.

## Baseline ECS Task-Definition Contract

- The dev app stack defines one ECS Fargate task definition for the application
  runtime.
- The task definition uses `awsvpc` networking with fixed task sizing of `1024`
  CPU units and `2048` MiB memory.
- The task family name and CloudWatch log stream prefix are reviewer-readable
  locals so later service and proof workflows reuse a stable runtime contract.
- The application container exposes only port `8080`, matching the existing
  repository-owned deploy and ALB health-check contracts.
- The task definition reuses the existing execution role, task role, and
  application log group instead of redefining runtime IAM or logging
  destinations.
- The deploy image input must be an immutable image reference pinned by
  digest when the ECS runtime is enabled. Mutable tags such as `latest` are
  intentionally excluded from the Terraform contract.
- The baseline task definition intentionally keeps the runtime configuration
  surface minimal: no Terraform-managed secret injection, no environment files,
  and no extra environment variables beyond what the container image already
  owns.

## Baseline ECS Service Contract

- The dev app stack defines one ECS service attached to the shared ECS cluster,
  the baseline application task definition, and the existing application target
  group.
- The service runs exactly one task in the existing private subnets and reuses
  the existing ECS task security group.
- `assign_public_ip = false` is required, so tasks are not directly
  internet-reachable and remain available only through the ALB ingress path.
- The service uses `health_check_grace_period_seconds = 120` to give the Spring
  Boot application time to start before ALB health checks count against it.
- The deployment settings intentionally allow single-task replacement with brief downtime
  for the dev H2 proof of concept: `minimumHealthyPercent = 0` and
  `maximumPercent = 100`.
- Later rollout automation should consume only the exported
  `baseline_ecs_service_name`, `baseline_ecs_service_arn`,
  `baseline_task_definition_family`, and `baseline_task_definition_arn`
  identifiers instead of reconstructing names or owning task-definition
  revision churn.

## Baseline ECS Verification Order

- build and push a real immutable Git SHA image before the first ECS deployment.
- run Terraform Apply Dev with that exact digest before verifying the ECS runtime.
- verify ECS steady state with `aws ecs describe-services --cluster <cluster-name> --services <service-name>`.
- verify the running task identity with `aws ecs list-tasks --cluster <cluster-name> --service-name <service-name>`.
- verify target health with `aws elbv2 describe-target-health --target-group-arn <target-group-arn>`.
- verify ALB reachability with `curl -fsS http://<alb-dns-name>/actuator/health`.
- verify CloudWatch logs with `aws logs get-log-events --log-group-name /aws/ecs/dev-application --log-stream-name <stream-name>`.
- no direct task public-IP path exists because the baseline ECS service keeps
  `assign_public_ip = false`.
- This baseline verification flow stays within scope and intentionally excludes
  CI rollout automation, autoscaling, and database redesign.

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

## Health Contract

- The public ALB exposes one HTTP listener on port `80`.
- The listener forwards by default to one application target group that uses
  ECS-compatible `ip` targets on application port `8080`.
- The target group treats `GET /actuator/health` on `traffic-port` as the v1
  traffic-readiness signal and matches success codes `200-299`.
- The target group keeps explicit v1 health settings of interval `15`, timeout
  `5`, healthy threshold `2`, and unhealthy threshold `3`.
- ECS startup-grace behavior stays out of scope for this ALB contract and must
  be handled later at the ECS service layer.

## Public Endpoint Identifier Contract

- The ALB DNS name is the approved v1 public endpoint identifier for
  infrastructure wiring and review.
- The stack exports `alb_dns_name`, `alb_hosted_zone_id`, `alb_arn`,
  `alb_name`, `http_listener_arn`, `application_target_group_arn`, and
  `application_target_group_name` directly from Terraform resource attributes.
- The ALB DNS name is suitable for downstream integrations, but end-to-end application reachability still depends on later ECS service attachment.

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
