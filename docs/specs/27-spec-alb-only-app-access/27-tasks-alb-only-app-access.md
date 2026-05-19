## Relevant Files

| File | Why It Is Relevant |
| --- | --- |
| `docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md` | Source spec that defines the ALB-only access requirements, non-goals, proof expectations, and open questions this plan must cover. |
| `docs/specs/27-spec-alb-only-app-access/27-tasks-alb-only-app-access.md` | Task-plan artifact that captures execution order, proof artifacts, and junior-developer guidance for this feature. |
| `docs/specs/27-spec-alb-only-app-access/27-audit-alb-only-app-access.md` | Planning-audit artifact that records gate status, repository-standards evidence, and any remediation decisions. |
| `infra/terraform/app/dev/main.tf` | Main Terraform entry point where the ALB and ECS task security groups plus their rules will be defined. |
| `infra/terraform/app/dev/variables.tf` | Planned input definitions for listener-port values, tagging, and any reusable security-group contract inputs. |
| `infra/terraform/app/dev/locals.tf` | Planned shared naming, port, and tagging locals so the security-group contract stays reviewer-readable and consistent with prior dev-network work. |
| `infra/terraform/app/dev/outputs.tf` | Planned outputs for security-group IDs or names that later ALB and ECS specs can consume without rediscovering resource addresses. |
| `infra/terraform/app/dev/README.md` | Operator-facing documentation for the approved traffic path, allowed-traffic matrix, and the v1 egress posture. |
| `infra/terraform/app/dev/backend.hcl.example` | Existing backend contract used when local `terraform init` and `terraform plan` proof artifacts are exercised. |
| `infra/terraform/floci/README.md` | Local AWS-resources validation guidance that should document how this security-group contract is exercised before AWS use. |
| `infra/terraform/floci/docker-compose.yml` | Existing compose-managed `floci` environment that the verification workflow will rely on for local Terraform checks. |
| `scripts/verify-terraform-remote-state-contract.sh` | Existing repository pattern for Terraform verification workflow scripts that the new ALB-only access verification entry point should mirror where appropriate. |
| `scripts/verify-alb-only-app-access-contract.sh` | Planned repository-owned verification script for validating the security-group contract reproducibly against local tooling and `floci`. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbSecurityGroupContractTest.java` | Planned contract test for the dedicated ALB security group, internet ingress boundaries, and listener-port expectations. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsTaskSecurityGroupContractTest.java` | Planned contract test for ALB-only ECS ingress, app-port restriction, and the no-public-ingress rule. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyTrafficFlowContractTest.java` | Planned contract test for the approved end-to-end traffic path, allowed-traffic matrix, and the documented v1 egress stance. |
| `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyAppAccessVerificationWorkflowTest.java` | Planned contract test for the verification script and operator guidance that prove the local validation workflow is reproducible and sanitized. |

### Notes

- Follow strict TDD during implementation: each task slice begins with a failing contract test or failing verification check before Terraform, documentation, or script changes.
- Use `terraform -chdir=infra/terraform/app/dev validate` and sanitized `terraform plan -no-color` output as the main infrastructure proof path, then run `./mvnw test` before completion when Java contract tests are added or changed.
- Keep proof artifacts sanitized: use placeholder credentials such as `AWS_ACCESS_KEY_ID=test`, avoid live AWS account identifiers, and do not commit raw Terraform state.
- Treat ALB listener ports as configurable contract inputs for this spec; do not silently decide the later HTTP-versus-HTTPS listener question here.
- Keep non-goals intact: this task plan does not add ALB resources, ECS services, target groups, listeners, WAF, VPC endpoints, or narrowed ECS egress hardening beyond documenting follow-on work.

## Tasks

### [x] 1.0 Define the public ALB security-group contract for the dev app stack

#### 1.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf` and `infra/terraform/app/dev/variables.tf` demonstrate a dedicated ALB security group with internet-facing ingress limited to the configured listener-port contract
- CLI: `terraform -chdir=infra/terraform/app/dev validate` exits `0` and demonstrates the ALB security-group contract is syntactically valid before live AWS use
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows internet ingress on the ALB security group and no direct internet ingress on the ECS task security group
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbSecurityGroupContractTest.java` passes and demonstrates the ALB security group is separate, readable, and limited to the documented listener-port interface

#### 1.0 Tasks

- [x] 1.1 Add a failing contract test that asserts the dev app stack defines a dedicated ALB security group, keeps listener ingress configurable, and does not reuse the ECS task security group for internet-facing access.
- [x] 1.2 Add the minimum Terraform resources and inputs needed to define the ALB security group with ingress limited to the selected listener-port contract in the existing dev VPC.
- [x] 1.3 Add readable naming, tags, and any shared locals needed so a junior reviewer can identify the ALB security group and its role directly from Terraform plan output.
- [x] 1.4 Capture `terraform validate` and sanitized `terraform plan -no-color` proof output showing that public ingress terminates at the ALB security group only.

### [x] 2.0 Define the ECS task security-group ingress contract with ALB-only access

#### 2.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf` demonstrates a dedicated ECS task security group with ingress sourced by security-group reference instead of CIDR ranges
- File: `infra/terraform/app/dev/outputs.tf` demonstrates the security-group identifiers or naming outputs needed for later ECS-service and ALB integration work
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows ECS task ingress is limited to the Spring Boot app port `8080` from the ALB security group only
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformEcsTaskSecurityGroupContractTest.java` passes and demonstrates the ECS task security group remains compatible with `awsvpc` task networking and has no `0.0.0.0/0` or `::/0` ingress

#### 2.0 Tasks

- [x] 2.1 Add a failing contract test that asserts ECS task ingress is allowed only from the ALB security group reference, only on app port `8080`, and never from `0.0.0.0/0`, `::/0`, or broad subnet CIDRs.
- [x] 2.2 Add the minimum Terraform resources and rules needed to define the ECS task security group and reference the ALB security group as the only ingress source.
- [x] 2.3 Add outputs or equivalent reviewer-visible identifiers for both security groups so later ECS-service and ALB integration work can attach them without rediscovering resource addresses.
- [x] 2.4 Capture sanitized `terraform plan -no-color` proof output showing the ECS task security group exposes only the app port to the ALB security group and remains isolated from direct internet ingress.

### [x] 3.0 Define the v1 egress posture and reviewer-visible traffic-flow documentation

#### 3.0 Proof Artifact(s)

- File: `infra/terraform/app/dev/main.tf` demonstrates ALB egress is limited to the ECS task security group on the app and health-check port contract while ECS task egress remains default-open in v1
- File: `infra/terraform/app/dev/README.md` documents the approved traffic path `internet client -> ALB -> ECS task on app port`, the allowed-traffic matrix, and the explicit follow-on hardening non-goals
- CLI: `AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color` shows the ALB and ECS egress rules match the documented traffic-flow matrix
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyTrafficFlowContractTest.java` passes and demonstrates the documented traffic model, port contract, and v1 egress stance remain synchronized

#### 3.0 Tasks

- [x] 3.1 Add a failing contract test that asserts the approved traffic path, ALB-to-ECS port contract, default-open ECS egress stance, and out-of-scope hardening follow-on notes are all documented consistently.
- [x] 3.2 Add the minimum Terraform egress rules needed so the ALB security group can reach the ECS task security group on the app and matching health-check port contract while ECS task egress remains open in v1.
- [x] 3.3 Update `infra/terraform/app/dev/README.md` with a reviewer-oriented traffic-flow table or matrix that lists approved source, destination, protocol, and port combinations and explains why private subnets alone are not treated as sufficient protection.
- [x] 3.4 Capture sanitized `terraform plan -no-color` proof output and documentation references showing the implemented egress rules match the documented traffic-flow matrix and non-goal boundaries.

### [x] 4.0 Add reproducible local verification for the ALB-only app-access contract

#### 4.0 Proof Artifact(s)

- File: `scripts/verify-alb-only-app-access-contract.sh` demonstrates a repository-owned verification entry point that initializes the dev stack, exercises the contract against `floci`, and fails clearly when required Terraform or documentation files are missing
- File: `infra/terraform/floci/README.md` documents the exact local verification flow for the security-group contract without requiring live AWS credentials
- CLI: `./scripts/verify-alb-only-app-access-contract.sh` exits `0` and demonstrates the ALB-only access contract can be validated reproducibly with sanitized local credentials
- Test: `src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyAppAccessVerificationWorkflowTest.java` passes and demonstrates the verification script covers `floci`, Terraform validation or planning, and sanitized operator guidance end to end

#### 4.0 Tasks

- [x] 4.1 Add a failing contract test that asserts one repository-owned verification workflow covers the dev-stack validation or planning path, `floci` local-environment usage, sanitized credentials, and clear missing-file failures for this security-group contract.
- [x] 4.2 Create `scripts/verify-alb-only-app-access-contract.sh` so it runs the documented Terraform commands in a reproducible order, reuses the existing backend contract, and fails clearly when required Terraform, script, or documentation files are absent.
- [x] 4.3 Update `infra/terraform/floci/README.md` or the most appropriate operator-facing doc with the exact command sequence for exercising the ALB-only access contract locally before AWS deployment.
- [x] 4.4 Capture the `./scripts/verify-alb-only-app-access-contract.sh` proof path so reviewers can reproduce the local validation flow and confirm the artifacts remain observable, reproducible, scope-linked, and sanitized.
