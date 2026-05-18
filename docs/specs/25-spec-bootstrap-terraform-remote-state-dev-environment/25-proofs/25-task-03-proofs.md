# Task 03 Proofs - Reusable remote-state consumer contract for dev and floci

## Task Summary

This task defines how downstream Terraform consumers attach to the shared dev
remote-state backend. It adds a partial S3 backend block for the application
stack, sanitized backend configuration examples, and a compose-managed `floci`
local AWS environment that operators can use to validate the contract before
touching AWS.

## What This Task Proves

- The application stack uses partial backend configuration instead of hard-coded
  backend values in source.
- The shared state key for the main application stack is fixed at
  `app/dev/terraform.tfstate`.
- The local `floci` workflow is now compose-managed and documented in-repo.
- The documented consumer init command can attach successfully when the local
  backend resources exist.
- Automated tests enforce the consumer contract and compose-based local workflow.

## Evidence Summary

- `TerraformRemoteStateConsumerContractTest` passes, proving the app/dev
  contract, the `floci` examples, and the compose workflow are all documented
  consistently.
- `docker compose -f infra/terraform/floci/docker-compose.yml up -d floci`
  starts the local AWS-resources environment used for consumer validation.
- Seeded local S3 and DynamoDB resources allow
  `terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure`
  to succeed against the compose-managed backend.

## Artifact: Consumer contract test

**What it proves:** The repository enforces partial backend configuration, a
stable state key, compose-managed local guidance, and shared backend values for
operators and GitHub Actions.

**Why it matters:** This task is about making later Terraform stacks reuse one
backend contract instead of inventing incompatible attachment patterns.

**Command:**

```bash
./mvnw test -Dtest=TerraformRemoteStateConsumerContractTest
```

**Result summary:** The targeted consumer contract suite passed with `2` tests
and `0` failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformRemoteStateConsumerContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.060 s -- in org.springframework.samples.petclinic.system.TerraformRemoteStateConsumerContractTest
[INFO] BUILD SUCCESS
```

## Artifact: Compose-managed floci startup

**What it proves:** The repository-owned compose file can start the local AWS
environment used by the consumer contract.

**Why it matters:** The user asked for a compose-based workflow instead of
ad-hoc Docker startup, and the local validation path now depends on that file.

**Command:**

```bash
docker compose -f infra/terraform/floci/docker-compose.yml up -d floci
```

**Result summary:** Docker Compose created the local `floci` service and left it
running for backend validation.

```text
Container floci-floci-1 Creating
Container floci-floci-1 Started
```

## Artifact: Consumer backend attachment

**What it proves:** The documented app/dev consumer init command can attach to
the shared remote-state backend contract when the local bucket and lock table
exist.

**Why it matters:** This is the primary runtime proof that a downstream stack
can reuse the documented backend pattern instead of owning backend resources.

**Command:**

```bash
terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure
```

**Result summary:** Terraform configured the `s3` backend successfully against
the compose-managed local backend and completed initialization. The warning
about `dynamodb_table` is expected because this spec intentionally preserves the
S3 plus DynamoDB locking contract for the dev proof of concept.

```text
Successfully configured the backend "s3"!
Terraform has been successfully initialized!
```

## Reviewer Conclusion

These artifacts show that the consumer-side remote-state contract is now stable,
documented, and executable: downstream stacks use partial backend
configuration, local validation runs through the repository-owned compose
workflow, and the documented init command attaches successfully to the shared
backend pattern.
