# Task 01 Proofs - Dev identity stack extracted from app runtime stack

## Task Summary

This task separates GitHub workflow identity from runtime infrastructure by
adding a dedicated `infra/terraform/identity/dev` stack, removing GitHub OIDC
resources from `infra/terraform/app/dev`, and documenting the resulting
`state/dev` -> `identity/dev` -> `app/dev` lifecycle boundary.

## What This Task Proves

- The repository now contains a dedicated `identity/dev` Terraform stack for
  the GitHub OIDC provider and workflow IAM roles.
- The `app/dev` Terraform stack no longer declares the GitHub OIDC provider or
  the Terraform/app workflow IAM roles directly.
- Repository documentation now describes the three-layer lifecycle split and
  the corresponding destroy ordering.

## Evidence Summary

- The focused Task 1 contract-test slice passes, proving the new identity-stack
  ownership boundary and documentation contract are in place.
- Terraform initialization for `identity/dev` succeeds against the repo's local
  `floci` backend contract after the AWS provider is installed.
- The repository-wide `./mvnw test` gate is partially environment-blocked in
  this sandbox because Spring Boot integration tests cannot bind an embedded
  Tomcat socket (`java.net.SocketException: Operation not permitted`), which is
  independent of the Terraform identity-stack changes.

## Artifact: Focused identity-boundary contract tests

**What it proves:** The new identity stack exists, owns the GitHub workflow
identity resources, and the app stack plus lifecycle docs reflect the split.

**Why it matters:** These tests are the strict TDD proof that the repository
contract changed in the intended direction before any later workflow work.

**Command:**

```bash
./mvnw test -Dtest=TerraformIdentityStackBoundaryContractTest,GitHubIdentityStackDocumentationContractTest,GitHubTerraformWorkflowRolesContractTest,GitHubOidcTrustPolicyContractTest,GitHubDeployRoleAndConfigurationContractTest,TerraformBootstrapWorkflowDocumentationContractTest,TerraformStateBoundaryContractTest
```

**Result summary:** The focused Task 1 slice passed with 16 tests and 0
failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.GitHubTerraformWorkflowRolesContractTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.GitHubIdentityStackDocumentationContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformBootstrapWorkflowDocumentationContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformStateBoundaryContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.GitHubDeployRoleAndConfigurationContractTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.GitHubOidcTrustPolicyContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformIdentityStackBoundaryContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Identity stack Terraform initialization

**What it proves:** The new `identity/dev` stack is wired consistently enough
for Terraform to initialize the AWS provider and configure the local backend
contract.

**Why it matters:** This shows the new stack is not just documentation plus HCL
files; Terraform can recognize it as a real stack with a usable provider lock
and backend contract.

**Command:**

```bash
terraform -chdir=infra/terraform/identity/dev init -backend-config=backend.hcl.example -reconfigure -input=false
```

**Result summary:** Terraform successfully initialized the AWS provider and the
local S3 backend contract after provider download and local `floci` backend
setup.

```text
Initializing provider plugins found in the configuration...
- Reusing previous version of hashicorp/aws from the dependency lock file
- Using previously-installed hashicorp/aws v6.45.0

Initializing the backend...
Successfully configured the backend "s3"!

Terraform has been successfully initialized!
```

## Artifact: Three-stack lifecycle documentation boundary

**What it proves:** Reviewer-facing docs now identify `state/dev` as the
backend owner, `identity/dev` as the GitHub identity owner, and `app/dev` as
the runtime-infrastructure owner.

**Why it matters:** The spec requires maintainers to understand what survives a
normal app rebuild versus what is removed only during final teardown.

**Artifact paths:**

- `README.md`
- `infra/terraform/state/dev/README.md`
- `infra/terraform/identity/dev/README.md`
- `infra/terraform/app/dev/README.md`

**Result summary:** The docs now describe the three-layer create/destroy order
and explicitly state that `app/dev` does not own the GitHub OIDC provider or
workflow IAM roles.

```text
README.md: `state/dev` owns the backend S3 bucket and DynamoDB lock table.
README.md: `identity/dev` owns the GitHub OIDC provider and the Terraform apply, Terraform destroy, app publish, and app deploy IAM roles.
README.md: `app/dev` owns runtime infrastructure such as ECR, VPC, ALB, ECS, and log groups.
infra/terraform/state/dev/README.md: Destroy the application stack before tearing down the identity stack.
infra/terraform/identity/dev/README.md: The `identity/dev` stack owns the GitHub OIDC provider and the GitHub-assumable IAM roles...
infra/terraform/app/dev/README.md: `app/dev` does not own the GitHub OIDC provider or the GitHub workflow IAM roles.
```

## Artifact: Repository-wide test gate caveat

**What it proves:** The remaining full-suite failures are sandbox runtime
limitations, not Task 1 contract regressions.

**Why it matters:** The repo expects `./mvnw test`, so reviewers need to know
why the environment prevented a fully green run even though the Task 1 slice
passed.

**Command:**

```bash
./mvnw test
```

**Result summary:** The run progressed through the unit and contract suites, but
the environment blocked Spring integration tests that need to bind an embedded
Tomcat socket on a random port.

```text
Caused by: java.net.SocketException: Operation not permitted
at java.base/sun.nio.ch.Net.bind0(Native Method)
...
org.springframework.samples.petclinic.PetClinicIntegrationTests
org.springframework.samples.petclinic.system.CrashControllerIntegrationTests
```

## Reviewer Conclusion

These artifacts show Task 1's intended repository change is complete: GitHub
workflow identity moved into `infra/terraform/identity/dev`, the app stack is
runtime-only, and reviewer-facing docs now reflect the three-stack lifecycle
boundary. The only remaining verification gap is sandbox-specific socket
binding for unrelated Spring integration tests.
