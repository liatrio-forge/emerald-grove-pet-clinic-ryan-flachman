# Task 02 Proofs - Dev remote-state resource contract

## Task Summary

This task defines the Terraform resource contract for the dev remote-state
backend: one S3 bucket for shared state storage, one DynamoDB table for locking,
shared naming and tagging conventions, and exported values that downstream
stacks can consume without inventing new backend rules.

## What This Task Proves

- The state stack defines exactly one dev S3 backend bucket and one dev
  DynamoDB lock table.
- Bucket versioning and server-side encryption are part of the contract.
- Backend resource names and tags are environment-scoped and reusable.
- Terraform can initialize provider dependencies and validate the configuration.
- Automated tests enforce the resource contract at the repository level.

## Evidence Summary

- `TerraformRemoteStateResourceContractTest` passes with `3` assertions groups,
  proving the Terraform files encode the required bucket, lock table, naming,
  tagging, versioning, and encryption rules.
- `terraform init -backend=false` installs the AWS provider and creates
  `.terraform.lock.hcl`, proving the state stack is executable as Terraform
  configuration rather than documentation-only scaffolding.
- `terraform validate` succeeds, proving the resource contract is syntactically
  valid and implementable.

## Artifact: Resource contract test

**What it proves:** The repository-level contract test enforces exactly one
backend bucket, exactly one lock table, required encryption and versioning
resources, shared tag usage, and exported backend values.

**Why it matters:** The task is meant to freeze the remote-state design so later
Terraform work can reuse it without re-deciding backend structure.

**Command:**

```bash
./mvnw test -Dtest=TerraformRemoteStateResourceContractTest
```

**Result summary:** The targeted contract suite passed with `3` tests and `0`
failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformRemoteStateResourceContractTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.079 s -- in org.springframework.samples.petclinic.system.TerraformRemoteStateResourceContractTest
[INFO] BUILD SUCCESS
```

## Artifact: Terraform provider initialization

**What it proves:** The state stack can resolve and lock the AWS provider needed
to validate the resource contract.

**Why it matters:** `terraform validate` is only meaningful if the provider can
actually be initialized for this stack.

**Command:**

```bash
terraform -chdir=infra/terraform/state/dev init -backend=false
```

**Result summary:** Terraform installed `hashicorp/aws v6.45.0` and created
`.terraform.lock.hcl`, locking the provider selection for repeatable validation.

```text
- Installed hashicorp/aws v6.45.0 (signed by HashiCorp)
Terraform has been successfully initialized!
```

## Artifact: Terraform validation

**What it proves:** The S3 bucket, versioning, encryption, DynamoDB locking,
outputs, variables, and provider constraints form a valid Terraform
configuration.

**Why it matters:** The resource contract must be executable infrastructure code,
not just a convention written in Markdown.

**Command:**

```bash
terraform -chdir=infra/terraform/state/dev validate
```

**Result summary:** Terraform reported the configuration as valid.

```text
Success! The configuration is valid.
```

## Reviewer Conclusion

These artifacts show that the dev remote-state backend contract is now concrete,
validated, and enforced: one bucket, one lock table, recovery and encryption
controls, and reusable naming and tagging conventions all exist in executable
Terraform.
