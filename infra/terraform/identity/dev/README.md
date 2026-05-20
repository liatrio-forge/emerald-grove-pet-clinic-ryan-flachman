# Dev Identity Foundation Contract

The `identity/dev` stack owns the GitHub OIDC provider and the GitHub-assumable
IAM roles that repository workflows use for `Terraform apply`, `Terraform destroy`,
`app publish`, and `app deploy`. This stack sits between `state/dev` and
`app/dev` in the dev lifecycle.

## Ownership Boundary

- `state/dev` owns only the Terraform backend bucket and lock table.
- `identity/dev` owns the GitHub OIDC provider plus the workflow IAM roles.
- `app/dev` owns runtime infrastructure such as networking, ECR, ECS, and ALB
  resources.
- `app/dev` does not own the GitHub OIDC provider or GitHub workflow IAM roles
  directly.

## Lifecycle Sequence

1. Apply `state/dev` first so downstream stacks can use the remote backend.
2. Apply `identity/dev` next so GitHub OIDC workflows have assumable roles.
3. Apply `app/dev` after the backend and workflow identity layers exist.
4. For final teardown, destroy `app/dev` before `identity/dev`, then destroy
   `state/dev`.

## Backend Contract

- `identity/dev` uses the stable state key `identity/dev/terraform.tfstate`.
- Backend settings stay outside reusable source through
  `backend.hcl.example` or equivalent operator-provided input.
- Validate the stack only after backend configuration is materialized, just as
  the repository already does for other Terraform stacks.

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
