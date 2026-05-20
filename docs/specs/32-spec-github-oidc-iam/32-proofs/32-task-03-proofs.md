# Task 03 Proofs - Separate app deploy role and GitHub configuration contract

## Task Summary

This task proves the dev stack now defines a dedicated app deploy role that is
separate from Terraform mutation roles, narrows its policy to the ECS rollout
path, and documents the exact GitHub variable ownership contract for downstream
workflows.

## What This Task Proves

- `app-deploy-dev` exists as a separate GitHub-assumable role.
- The deploy role trusts the protected `dev` environment subject and keeps a
  narrower action surface than the Terraform roles.
- The apply workflow binds its AWS-assuming plan job to the protected `dev`
  environment.
- The README documents `AWS_REGION`, `TERRAFORM_APPLY_ROLE_ARN`,
  `TERRAFORM_DESTROY_ROLE_ARN`, `APP_DEPLOY_ROLE_ARN`, `TF_STATE_BUCKET`, and
  `TF_LOCK_TABLE` with repository-versus-environment ownership.

## Evidence Summary

- The focused deploy-role and workflow-configuration contract test passes.
- The Terraform source now defines a dedicated deploy policy, deploy role, and
- output ARN.
- The workflow file now marks the `plan-dev` job with `environment: dev`, which
  aligns the AWS-assuming plan path with the exact OIDC trust subject.
- Like task 2, the requested GitHub configuration screenshot is represented by
  repository-backed contract evidence because the live GitHub UI is not stored
  in the repo.

## Artifact: Focused deploy-role and GitHub configuration contract test

**What it proves:** The deploy role stays separate, the deploy policy is
rollout-specific, and the workflow/docs consume the distinct role variables.

**Why it matters:** This is the guardrail that prevents future workflow work
from reusing the broad Terraform roles for ECS deployment convenience.

**Command:**

```bash
./mvnw -q -Dtest=GitHubDeployRoleAndConfigurationContractTest test
```

**Result summary:** The focused deploy-role contract test exited `0`.

```text
Exit code: 0
```

## Artifact: Deploy role Terraform contract

**What it proves:** Terraform defines a separate deploy role and a narrower
deploy policy for the ECS rollout path.

**Why it matters:** Reviewers need to see that deployment is not implicitly
covered by the broad Terraform apply/destroy policy.

**Artifact path:** `infra/terraform/app/dev/main.tf`

**Result summary:** The stack now includes `aws_iam_policy.app_deploy_github_actions`,
`aws_iam_role.app_deploy_github_actions`, and the deploy-role policy attachment.

```text
resource "aws_iam_policy" "app_deploy_github_actions" {
  Action = [
    "ecs:DescribeServices",
    "ecs:DescribeTaskDefinition",
    "ecs:RegisterTaskDefinition",
    "ecs:UpdateService",
    "ecs:ListTasks",
    "ecr:BatchGetImage",
    "ecr:DescribeImages",
    "ecr:DescribeRepositories",
    "iam:PassRole",
  ]
}

resource "aws_iam_role" "app_deploy_github_actions" {
  name               = local.app_deploy_role_name
  assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust["app_deploy"].json
}
```

## Artifact: Workflow environment and role-variable contract

**What it proves:** The existing Terraform apply workflow now declares the
protected `dev` environment on the AWS-assuming plan job and continues using
OIDC plus the apply-specific role ARN variable.

**Why it matters:** Without the environment declaration, the OIDC trust subject
for `terraform-apply-dev` would not match the job that actually assumes the
role.

**Artifact path:** `.github/workflows/terraform-apply-dev.yml`

**Result summary:** The `plan-dev` job now includes `environment: dev`, keeps
`id-token: write`, and consumes `TERRAFORM_APPLY_ROLE_ARN`.

```text
permissions:
  contents: read
  id-token: write

plan-dev:
  environment: dev
  env:
    AWS_REGION: ${{ vars.AWS_REGION }}
...
      role-to-assume: ${{ vars.TERRAFORM_APPLY_ROLE_ARN }}
```

## Artifact: GitHub configuration ownership table

**What it proves:** The README now names the required GitHub variables and
separates environment-owned deployment values from repository-stable defaults.

**Why it matters:** Downstream workflow specs can now reuse one public contract
instead of re-deciding where sensitive AWS values belong.

**Artifact path:** `infra/terraform/app/dev/README.md`

**Result summary:** The README includes the full variable table and names the
`app-deploy-dev` role explicitly in the GitHub OIDC role matrix.

```text
| `AWS_REGION` | Repository-scoped | ...
| `TERRAFORM_APPLY_ROLE_ARN` | Environment-scoped (`dev`) | ...
| `TERRAFORM_DESTROY_ROLE_ARN` | Environment-scoped (`dev-destroy`) | ...
| `APP_DEPLOY_ROLE_ARN` | Environment-scoped (`dev`) | ...
| `TF_STATE_BUCKET` | Environment-scoped (`dev`) | ...
| `TF_LOCK_TABLE` | Environment-scoped (`dev`) | ...
```

## Reviewer Conclusion

The deploy role is now independent from Terraform mutation roles, the workflow
contract aligns with protected-environment OIDC trust, and the GitHub variable
ownership model is explicit enough for downstream deployment automation.
