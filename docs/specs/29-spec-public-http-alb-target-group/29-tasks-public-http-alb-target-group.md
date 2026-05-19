## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/29-spec-public-http-alb-target-group/29-spec-public-http-alb-target-group.md` | Source spec that defines the ALB, listener, target-group, output, proof, and non-goal requirements this plan must cover. |
| `docs/specs/29-spec-public-http-alb-target-group/29-tasks-public-http-alb-target-group.md` | Task-plan artifact that captures execution order, proof artifacts, and junior-developer implementation guidance for this feature. |
| `docs/specs/29-spec-public-http-alb-target-group/29-audit-public-http-alb-target-group.md` | Planning-audit artifact that records gate status, repository-standards evidence, and any later remediation decisions. |
| `infra/terraform/app/dev/main.tf` | Main Terraform entry point where the ALB, HTTP listener, and application target group will be defined and wired together. |
| `infra/terraform/app/dev/locals.tf` | Planned shared naming and tagging locals for reviewer-readable ALB and target-group identifiers that match the existing stack conventions. |
| `infra/terraform/app/dev/variables.tf` | Planned Terraform inputs or constants for listener and target-group values that need to stay explicit and reusable. |
| `infra/terraform/app/dev/outputs.tf` | Planned downstream outputs for the ALB DNS name, hosted zone ID, ARNs, and human-readable names. |
| `infra/terraform/app/dev/README.md` | Operator-facing documentation for the public endpoint identifier, HTTP-only scope, and target-group health-check contract. |
| `infra/terraform/app/dev/backend.hcl.example` | Existing backend contract used when local `terraform init`, `validate`, and `plan` proof artifacts are exercised reproducibly. |
| `infra/terraform/floci/README.md` | Local AWS-resources validation guidance that should document how this ALB contract is exercised before AWS use. |
| `infra/terraform/floci/docker-compose.yml` | Existing compose-managed `floci` environment that the verification workflow will rely on for local Terraform checks. |
| `scripts/verify-alb-only-app-access-contract.sh` | Existing repository pattern for Terraform verification workflow scripts that the new ALB contract verification entry point should mirror where appropriate. |
| `scripts/verify-public-http-alb-target-group-contract.sh` | Planned repository-owned verification script for validating the public HTTP ALB contract reproducibly against local tooling and `floci`. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformPublicAlbContractTest.java` | Planned contract test for the internet-facing ALB resource, public-subnet attachment, security-group reuse, and naming or tagging clarity. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbListenerAndTargetGroupContractTest.java` | Planned contract test for the HTTP listener, default forward action, `ip` target type, port `8080`, and explicit health-check settings. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOutputsContractTest.java` | Planned contract test for the approved output names, ALB DNS endpoint identifier, ARNs, and human-readable names. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformPublicAlbVerificationWorkflowTest.java` | Planned contract test for the local verification script, `floci` workflow, sanitized credentials, and reproducible proof path. |

### Notes

- Follow strict TDD during implementation: each task slice begins with a failing Terraform contract test or failing verification-workflow check before Terraform, documentation, or script changes.
- Use `terraform -chdir=infra/terraform/app/dev validate` and sanitized `terraform plan -no-color` output as the main infrastructure proof path, then run `./mvnw test` before completion when Java contract tests are added or changed.
- Keep proof artifacts sanitized: use placeholder credentials such as `AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and `AWS_EC2_METADATA_DISABLED=true`; avoid live AWS account identifiers, tokens, and raw Terraform state output.
- Reuse the existing `infra/terraform/app/dev` naming, tagging, and output conventions so later ECS, validation, and DNS work can consume the ALB contract without reconstructing resource names.
- Keep non-goals intact: this plan does not add HTTPS, ACM, Route 53 records, ECS services or target registration, WAF, advanced listener rules, stickiness tuning, or ALB access logging.

## Tasks

### [x] 1.0 Define the public ALB resource contract in the dev app stack

#### 1.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf` and `infra/terraform/app/dev/locals.tf` demonstrate one internet-facing ALB attached to the existing exported public subnets and existing ALB security group with repository-consistent naming and tags
- CLI: `terraform -chdir=infra/terraform/app/dev validate` exits `0` and demonstrates the ALB resource contract is syntactically valid before live AWS use
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows one ALB created in the dev app stack with public-subnet placement and the expected internet-facing contract
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformPublicAlbContractTest.java` passes and demonstrates the ALB is internet-facing, uses the existing public subnets, reuses the ALB security group, and preserves naming and tagging conventions

#### 1.0 Tasks

- [x] 1.1 Add a failing contract test that asserts the dev app stack defines exactly one internet-facing ALB, attaches it to the existing public subnets, reuses the existing ALB security group, and keeps the public listener reachable on port `80` in v1.
- [x] 1.2 Add the minimum Terraform ALB resource and supporting locals needed to reuse the current public-subnet and ALB-security-group contract without reopening VPC or security-group design.
- [x] 1.3 Add reviewer-readable ALB naming, tags, and any short operator-facing documentation note needed so the ALB is clearly identified as the approved v1 public entrypoint contract.
- [x] 1.4 Capture `terraform validate` and sanitized `terraform plan -no-color` proof output showing one public ALB with the expected subnet placement, security-group attachment, and internet-facing contract.

### [x] 2.0 Define the HTTP listener and ECS-compatible target-group health contract

#### 2.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf` and `infra/terraform/app/dev/variables.tf` demonstrate one HTTP listener on port `80`, a default forward action, one target group with target type `ip`, traffic port `8080`, and explicit health-check path, matcher, interval, timeout, and threshold settings
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows the listener forwards to the application target group and the target-group health-check contract is explicit and reviewer-readable
- Documentation: `infra/terraform/app/dev/README.md` demonstrates the v1 health-check routing contract for `/actuator/health` on port `8080` and states that ECS startup-grace behavior remains out of scope for this spec
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbListenerAndTargetGroupContractTest.java` passes and demonstrates the HTTP listener, forward action, `ip` target type, port `8080`, and explicit health-check settings remain synchronized with the spec

#### 2.0 Tasks

- [x] 2.1 Add a failing contract test that asserts the app stack defines one HTTP listener on port `80` with a default forward action to one application target group.
- [x] 2.2 Extend the failing contract test coverage to assert the target group uses target type `ip`, application port `8080`, and HTTP health checks on `traffic-port` with path `/actuator/health` and matcher `200-299`.
- [x] 2.3 Add the minimum Terraform listener and target-group resources needed to make the forwarding contract explicit, including interval `15`, timeout `5`, healthy threshold `2`, and unhealthy threshold `3` rather than provider defaults.
- [x] 2.4 Update the app-stack documentation with a short health-contract summary that explains the v1 readiness signal and explicitly keeps ECS startup-grace behavior out of scope.
- [x] 2.5 Capture sanitized `terraform plan -no-color` proof output showing listener-to-target-group wiring plus all explicit target-group health-check settings.

### [x] 3.0 Publish the public endpoint identifier and downstream integration outputs

#### 3.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/outputs.tf` demonstrates the exact downstream outputs `alb_dns_name`, `alb_hosted_zone_id`, `alb_arn`, `alb_name`, `http_listener_arn`, `application_target_group_arn`, and `application_target_group_name`
- File: `infra/terraform/app/dev/README.md` demonstrates that the ALB DNS name is the approved v1 public endpoint identifier while end-to-end application reachability remains out of scope until later ECS attachment exists
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows the output contract and listener-to-target-group wiring are observable at plan time without name reconstruction
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOutputsContractTest.java` passes and demonstrates the public endpoint identifier, integration ARNs, and human-readable names are exported explicitly with reviewer-readable output names

#### 3.0 Tasks

- [x] 3.1 Add a failing contract test that asserts the app stack exports the exact reviewer-readable output names `alb_dns_name`, `alb_hosted_zone_id`, `alb_arn`, `alb_name`, `http_listener_arn`, `application_target_group_arn`, and `application_target_group_name`.
- [x] 3.2 Add the minimum Terraform outputs needed to publish the ALB DNS name, hosted zone ID, ARNs, and human-readable names directly from resource attributes without manual reconstruction.
- [x] 3.3 Update the app-stack documentation so it states that the ALB DNS name is the approved v1 public endpoint identifier and that end-to-end application reachability still depends on later ECS service attachment.
- [x] 3.4 Capture sanitized `terraform plan -no-color` proof output showing the output contract and confirming downstream consumers can use exported identifiers without rediscovering resource addresses or names.

### [x] 4.0 Add reproducible local verification for the public HTTP ALB contract

#### 4.0 Proof Artifact(s)

- File: `scripts/verify-public-http-alb-target-group-contract.sh` demonstrates a repository-owned verification entry point that initializes the dev stack, validates the Terraform contract, and runs a sanitized local planning workflow against `floci`
- File: `infra/terraform/floci/README.md` or `infra/terraform/app/dev/README.md` documents the exact local verification sequence, placeholder credentials, and the proof expectations for validating the ALB, listener, target group, and outputs locally before AWS use
- CLI: `./scripts/verify-public-http-alb-target-group-contract.sh` exits `0` and demonstrates the ALB contract can be checked reproducibly with sanitized local credentials and clear missing-file failures
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformPublicAlbVerificationWorkflowTest.java` passes and demonstrates the local verification path covers `floci`, Terraform validation and planning, sanitized credentials, and reviewer-visible proof guidance end to end

#### 4.0 Tasks

- [x] 4.1 Add a failing contract test that asserts one repository-owned verification workflow covers `floci`, Terraform init and validate and plan ordering, sanitized placeholder credentials, and clear missing-file failures for this ALB contract.
- [x] 4.2 Create `scripts/verify-public-http-alb-target-group-contract.sh` so it mirrors the repository’s existing verification-script pattern, reuses the backend contract, and validates the ALB, listener, target group, and outputs reproducibly before AWS use.
- [x] 4.3 Update the most appropriate operator-facing README with the exact local verification sequence, including `floci`, `terraform validate`, sanitized `terraform plan -no-color`, and placeholder-credential expectations.
- [x] 4.4 Capture the `./scripts/verify-public-http-alb-target-group-contract.sh` proof path so reviewers can reproduce the local validation flow and confirm the artifacts remain observable, reproducible, scope-linked, and sanitized.
