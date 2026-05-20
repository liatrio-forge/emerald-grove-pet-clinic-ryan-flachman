# Terraform State Stack for Dev

This `state/dev` stack owns the Terraform remote-state backend resources for the
`dev` environment. The `identity/dev` and `app/dev` stacks do not own backend
resources and must treat them as an already-bootstrapped dependency.

The main application stack does not own backend resources.

This stack must exist before any downstream stack attempts remote-backend initialization.

## Ownership Boundary

- The `state/dev` stack is the only repository area allowed to create, update,
  or destroy the remote-state S3 bucket and DynamoDB lock table for `dev`.
- Downstream stacks may consume the backend contract, but they must not recreate
  or modify backend resources as part of normal identity or application
  infrastructure work.
- Destroy the application stack before manually tearing down the backend resources.
- Destroy the application stack before tearing down the identity stack.
- Destroy the identity stack before manually tearing down the backend resources.
- This ordering prevents Terraform from removing the storage or lock table that
  still hold active remote state for another stack.

## Lifecycle Sequence

### Create

1. Start in `infra/terraform/state/dev`.
2. Run `terraform init -backend=false` before any downstream stack attempts
   remote-backend initialization.
3. Apply the state stack to create the remote-state backend resources.
4. After the backend exists, initialize `identity/dev`, then `app/dev`, with
   their documented backend configuration.

### Update

1. Update backend resource definitions only in `infra/terraform/state/dev`.
2. Re-run local validation for the state stack before touching downstream
   consumers.
3. Reconfigure downstream stacks only after the backend contract remains stable.

### Destroy

1. Destroy all downstream application infrastructure that depends on the remote
   backend.
2. Destroy the GitHub identity stack after application resources are gone.
3. Confirm no stack still points at the backend resources.
4. Manually tear down the `state/dev` backend resources as a separate operator
   action.

## Local Initialization Guardrail

Run `terraform init -backend=false` in this directory until the backend
resources exist. This prevents a circular dependency where the stack would need
its own remote backend before the backend resources have been created.

## Resource Contract

- Storage: the stack creates exactly one S3 bucket for dev Terraform state.
- Locking: the stack creates exactly one DynamoDB table with the `LockID`
  string hash key expected by Terraform state locking.
- Recovery: S3 bucket versioning stays enabled so prior state revisions can be
  recovered after accidental changes.
- Security: server-side encryption stays enabled with Amazon S3 managed
  encryption at rest.
- Naming and tags: bucket and table names are environment-scoped and
  human-readable, while shared tags identify the application, environment,
  stack, and Terraform ownership consistently.
