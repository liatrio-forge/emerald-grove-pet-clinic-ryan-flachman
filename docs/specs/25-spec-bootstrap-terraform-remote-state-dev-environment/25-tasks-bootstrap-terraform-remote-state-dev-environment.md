## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/25-spec-bootstrap-terraform-remote-state-dev-environment/25-spec-bootstrap-terraform-remote-state-dev-environment.md` | Source spec that defines the backend ownership, remote-state contract, and non-goal boundaries this plan must fully cover. |
| `docs/specs/25-spec-bootstrap-terraform-remote-state-dev-environment/25-tasks-bootstrap-terraform-remote-state-dev-environment.md` | Task plan artifact that captures the execution sequence, proof artifacts, and junior-developer guidance for this feature. |
| `infra/terraform/state/dev/README.md` | Planned operator-facing state-stack guide for backend creation, update, and teardown sequencing. |
| `infra/terraform/state/dev/main.tf` | Planned state-stack entry point for the dev S3 state bucket and DynamoDB lock table resources. |
| `infra/terraform/state/dev/variables.tf` | Planned input definitions for environment, region, and reusable naming or tagging values in the state stack. |
| `infra/terraform/state/dev/locals.tf` | Planned shared naming, tagging, and state-key conventions used to keep backend resource metadata consistent. |
| `infra/terraform/state/dev/versions.tf` | Planned Terraform and provider version constraints for the state stack. |
| `infra/terraform/state/dev/outputs.tf` | Planned exported backend values that downstream operators or stacks can reference without re-deciding naming. |
| `infra/terraform/app/dev/backend.hcl.example` | Planned sanitized partial backend configuration example for downstream remote-state consumers. |
| `infra/terraform/app/dev/README.md` | Planned consumer guidance for state key structure, backend inputs, and the rule that app stacks do not own backend resources. |
| `infra/terraform/floci/README.md` | Planned local infrastructure-testing guidance for exercising the remote-state contract against `floci` before AWS deployment. |
| `infra/terraform/floci/backend.hcl.example` | Planned sanitized local backend configuration example showing how `floci` participates in remote-state testing. |
| `scripts/verify-terraform-remote-state-contract.sh` | Planned reproducible verification entry point for validating state-stack and consumer contract assumptions together. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformStateBoundaryContractTest.java` | Planned test class for ownership separation, state-stack sequencing, and teardown-boundary expectations. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformRemoteStateResourceContractTest.java` | Planned test class for S3 versioning, encryption, naming, tagging, and DynamoDB lock-table contract assertions. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformRemoteStateConsumerContractTest.java` | Planned test class for partial backend configuration, stable state key structure, and remote-state consumption rules. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformRemoteStateVerificationWorkflowTest.java` | Planned test class for the verification script and operator workflow coverage. |
| `README.md` | Candidate top-level documentation touchpoint if the remote-state state-stack workflow needs repo-wide operator visibility. |
| `docs/specs/25-spec-bootstrap-terraform-remote-state-dev-environment/25-proofs/25-task-04-proofs.md` | Planned proof artifact file for capturing sanitized verification commands and operator evidence if spec-local proofs are preferred. |

### Notes

- Follow strict TDD during implementation: add the failing contract test for each task slice before creating or modifying the corresponding Terraform, script, or documentation file.
- Use focused verification during development where possible, then run the repository-standard `./mvnw test` before completion if Java test classes are added or changed.
- Keep proof artifacts sanitized: use placeholder AWS account values, never commit credentials, and never capture raw `terraform.tfstate` contents.
- Keep backend ownership separate from any future application stack code; this task plan intentionally avoids ECS, networking, CI workflow YAML, and other non-goal infrastructure.
- Treat `floci` as the default local AWS-resource environment for pre-AWS infrastructure validation.

## Tasks

### [x] 1.0 Establish the state-stack ownership and lifecycle boundary

#### 1.0 Proof Artifact(s)

- File: `docs/specs/25-spec-bootstrap-terraform-remote-state-dev-environment/25-spec-bootstrap-terraform-remote-state-dev-environment.md` updated ownership and teardown language demonstrates the state stack is separate from the main application stack
- File: `infra/terraform/state/dev/README.md` documents the state-stack create, update, and teardown sequence and demonstrates backend resources exist before remote-state consumers initialize
- CLI: `terraform -chdir=infra/terraform/state/dev init -backend=false` exits `0` and demonstrates the state stack can initialize without circular dependency on its own remote backend
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformStateBoundaryContractTest.java` passes and demonstrates the repository enforces a dedicated state-stack boundary and lifecycle guidance

#### 1.0 Tasks

- [x] 1.1 Add a failing contract test that asserts the repository includes a dedicated `state/dev` area, documents that backend resources are owned outside the main application stack, and preserves a separate teardown sequence.
- [x] 1.2 Create the `state/dev` directory structure and a state-stack README that defines create, update, and destroy ordering before any downstream stack attempts remote-backend initialization.
- [x] 1.3 Update the spec-facing documentation where needed so backend ownership, long-lived versus manually torn-down dev assets, and destroy-boundary assumptions are explicit and consistent with the state-stack guide.
- [x] 1.4 Capture the `terraform -chdir=infra/terraform/state/dev init -backend=false` proof path so reviewers can verify state-stack initialization stays local until remote backend resources exist.

### [ ] 2.0 Define the dev remote-state resource contract

#### 2.0 Proof Artifact(s)

- File: `infra/terraform/state/dev/main.tf` demonstrates one S3 bucket and one DynamoDB lock table dedicated to the `dev` environment
- File: `infra/terraform/state/dev/variables.tf` or `locals.tf` demonstrates environment-scoped naming and shared tagging conventions for backend resources
- CLI: `terraform -chdir=infra/terraform/state/dev validate` exits `0` and demonstrates the backend resource contract is syntactically valid and implementable
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformRemoteStateResourceContractTest.java` passes and demonstrates versioning, encryption, naming, tagging, and lock-table schema requirements are encoded in the state stack

#### 2.0 Tasks

- [ ] 2.1 Add a failing contract test that asserts the state stack defines exactly one dev S3 backend bucket, exactly one dev DynamoDB lock table, required lock-table key schema, bucket versioning, and server-side encryption.
- [ ] 2.2 Create the state-stack Terraform files that encode the bucket, lock table, Terraform and provider constraints, and any outputs needed for downstream backend consumption.
- [ ] 2.3 Add shared naming and tagging definitions that include environment context, keep resource names human-readable, and avoid ambiguous shared backend identifiers.
- [ ] 2.4 Capture the `terraform -chdir=infra/terraform/state/dev validate` proof path and document how the validated resource contract maps back to the spec's storage, locking, recovery, and security requirements.

### [ ] 3.0 Define the reusable remote-state consumer contract for dev and `floci`

#### 3.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/backend.hcl.example` demonstrates the expected bucket, key, region, and lock-table inputs for downstream stacks without committing secrets
- File: `infra/terraform/app/dev/README.md` documents the stable state key structure, the rule that consumer stacks must not recreate backend resources, and how `floci` participates in local validation
- File: `infra/terraform/floci/backend.hcl.example` demonstrates the sanitized local backend inputs used to exercise the remote-state contract in `floci`
- CLI: `terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure` exits `0` against the documented backend pattern and demonstrates consumers can attach using the shared contract
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformRemoteStateConsumerContractTest.java` passes and demonstrates partial backend configuration, stable key structure, and GitHub Actions or local input expectations are documented consistently

#### 3.0 Tasks

- [ ] 3.1 Add a failing contract test that asserts downstream dev stacks use partial backend configuration, a stable application state-key path, explicit documentation that remote state is already managed by the `state/dev` stack, and a documented `floci` local-validation path.
- [ ] 3.2 Create the sanitized `backend.hcl.example`, a `floci` local backend example, and companion consumer guidance that define expected bucket, key, region, and locking inputs for local operators and GitHub Actions.
- [ ] 3.3 Document the state-key naming rule for the main application stack plus the source of backend-config values for `floci`, local operators, and GitHub Actions so future Terraform specs reuse one contract instead of inventing incompatible paths.
- [ ] 3.4 Capture the `terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure` proof path so reviewers can verify downstream stacks have a reproducible remote-state attachment workflow.

### [ ] 4.0 Add automated verification and operator-facing proof for the backend contract and `floci` local testing

#### 4.0 Proof Artifact(s)

- File: `scripts/verify-terraform-remote-state-contract.sh` demonstrates one reproducible entry point for validating state-stack, consumer configuration, and `floci` local-testing assumptions without exposing secrets
- File: `README.md` or `docs/specs/25-spec-bootstrap-terraform-remote-state-dev-environment/25-proofs/25-task-04-proofs.md` documents the exact local and GitHub Actions verification flow for the remote-state contract
- CLI: `./scripts/verify-terraform-remote-state-contract.sh` exits `0` and demonstrates the planned naming, state-stack, consumer, and `floci` checks run together as a sanitized verification flow
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformRemoteStateVerificationWorkflowTest.java` passes and demonstrates the verification script and operator instructions cover the required state-stack assumptions end to end

#### 4.0 Tasks

- [ ] 4.1 Add a failing contract test that asserts a single repository-owned verification workflow checks state-stack initialization, state-stack validation, consumer backend guidance, `floci` local testing, and sanitization requirements together.
- [ ] 4.2 Create the verification script so it runs the documented Terraform commands in a reproducible order and fails clearly when required state-stack, consumer, or `floci` files are missing.
- [ ] 4.3 Document the operator verification flow in the most appropriate repository location and include sanitized expectations for local `floci` runs and future GitHub Actions reuse.
- [ ] 4.4 Capture the `./scripts/verify-terraform-remote-state-contract.sh` proof path and any supporting spec-local proof document so validation evidence is observable, reproducible, scope-linked, and secret-free.
