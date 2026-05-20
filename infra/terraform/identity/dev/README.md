# Dev Identity Foundation Contract

The `identity/dev` stack consumes the shared account-level GitHub OIDC provider
and owns the GitHub-assumable IAM roles that repository workflows use for
`Terraform apply`, `Terraform destroy`, `app publish`, and `app deploy`. This
stack sits between `state/dev` and `app/dev` in the dev lifecycle.

## Ownership Boundary

- `state/dev` owns only the Terraform backend bucket and lock table.
- `identity/dev` looks up the shared account-level GitHub OIDC provider by URL
  and owns the workflow IAM roles.
- `app/dev` owns runtime infrastructure such as networking, ECR, ECS, and ALB
  resources.
- `app/dev` does not own the GitHub OIDC provider or GitHub workflow IAM roles
  directly.
- Final cleanup must not delete the shared GitHub OIDC provider because other
  repositories in the AWS account may depend on it.

## Lifecycle Sequence

1. Apply `state/dev` first so downstream stacks can use the remote backend.
2. Ensure the shared GitHub OIDC provider already exists in the AWS account,
   then apply `identity/dev` so repository workflows have assumable roles.
3. Apply `app/dev` after the backend and workflow identity layers exist.
4. For final teardown, destroy `app/dev` before `identity/dev`, then destroy
   `state/dev`.

## Backend Contract

- `identity/dev` uses the stable state key `identity/dev/terraform.tfstate`.
- Backend settings stay outside reusable source through
  `backend.hcl.example` or equivalent operator-provided input.
- Validate the stack only after backend configuration is materialized, just as
  the repository already does for other Terraform stacks.

## Shared OIDC Provider Contract

- The GitHub OIDC provider is shared account-level infrastructure at
  `https://token.actions.githubusercontent.com`.
- `identity/dev` reads that provider by URL and reuses its ARN in role trust
  policies.
- If the provider is missing, operators must create it once at the account
  level before bootstrapping repository-scoped IAM roles.

## Workflow Role Contract

- `terraform-apply-dev` trusts the protected `dev` environment subject.
- `terraform-destroy-dev` trusts the protected `dev-destroy` environment subject.
- `app-publish-dev` trusts the protected `dev` environment subject.
- `app-deploy-dev` trusts the protected `dev` environment subject.

These roles are exported from Terraform outputs so repository workflows and
bootstrap handoff documentation can promote stable GitHub variable names without
reconstructing ARNs in YAML.

## Terraform IAM-sensitive actions

The shared Terraform GitHub policy is intentionally broad for the dev proof of
concept, but it still avoids unconstrained administrator access. Reviewer-sensitive
IAM actions are called out explicitly in policy source rather than hidden behind
`iam:*` or `Action: "*"` so maintainers can review the identity boundary directly.
