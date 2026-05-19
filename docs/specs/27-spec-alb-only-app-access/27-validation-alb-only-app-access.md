# Spec 27 Validation Report

**Validation Completed:** 2026-05-19 08:17 CDT
**Validation Performed By:** GPT-5 Codex

## 1) Executive Summary

- **Overall:** PASS
- **Gates:** A PASS, B PASS, C PASS, D PASS, E PASS, F PASS
- **Implementation Ready:** Yes. All 15 functional requirements were verified with current Terraform, documentation, automated contract tests, and a live `floci`-backed verification run.
- **Key metrics:** 100% Requirements Verified (15/15), 100% Proof Artifacts Working (12/12), 20 Files Changed Since Spec Creation vs 17 Relevant Files Listed

## 2) Coverage Matrix

### Functional Requirements

| Requirement ID/Name | Status | Evidence |
| --- | --- | --- |
| FR-1.1 Dedicated ALB security group exists in dev VPC | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:30), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:113), [TerraformAlbSecurityGroupContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformAlbSecurityGroupContractTest.java:47), commit `f277c16` |
| FR-1.2 Internet ingress allowed only on ALB listener port contract | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:31), [variables.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/variables.tf:41), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:125), `./scripts/verify-alb-only-app-access-contract.sh` plan output showed ALB ingress on port `80` only |
| FR-1.3 ECS tasks have no direct inbound internet access | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:32), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:143), [TerraformAlbSecurityGroupContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformAlbSecurityGroupContractTest.java:55), live plan showed only `ecs_task_from_alb` ingress |
| FR-1.4 ALB outbound traffic reaches ECS tasks on app port | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:33), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:163), [TerraformAlbOnlyTrafficFlowContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyTrafficFlowContractTest.java:41), live plan showed ALB egress on `8080` |
| FR-1.5 Health-check port contract matches app port | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:34), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:165), [README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:53), live plan showed ALB egress only on `8080` |
| FR-2.1 Dedicated ECS task security group exists for private subnets | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:48), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:143), [outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:44), commit `f277c16` |
| FR-2.2 ECS ingress allowed only from ALB security group | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:49), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:154), [TerraformEcsTaskSecurityGroupContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcsTaskSecurityGroupContractTest.java:48), live plan showed `referenced_security_group_id` only |
| FR-2.3 ECS ingress restricted to Spring Boot port `8080` | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:50), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:158), [TerraformEcsTaskSecurityGroupContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcsTaskSecurityGroupContractTest.java:51), [README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:53) |
| FR-2.4 ALB is modeled by SG reference, not broad CIDR | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:51), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:157), [TerraformEcsTaskSecurityGroupContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcsTaskSecurityGroupContractTest.java:53) |
| FR-2.5 ECS SG stays compatible with `awsvpc` task networking | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:52), [outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:39), [TerraformEcsTaskSecurityGroupContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcsTaskSecurityGroupContractTest.java:56) |
| FR-3.1 Inbound internet traffic allowed only to ALB and not ECS tasks | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:66), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:125), [README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:44), live plan showed public ingress only on `alb_listener_ipv4` and `alb_listener_ipv6` |
| FR-3.2 Approved end-to-end path is documented | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:67), [README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:42), [TerraformAlbOnlyTrafficFlowContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyTrafficFlowContractTest.java:50) |
| FR-3.3 ECS task SG uses default-open egress in v1 | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:68), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:172), [README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:54), live plan showed IPv4 and IPv6 open egress rules |
| FR-3.4 ALB egress is limited to ECS SG on app/health-check port contract | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:69), [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:163), [TerraformAlbOnlyTrafficFlowContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyTrafficFlowContractTest.java:41), live plan showed only `8080` egress from ALB to ECS |
| FR-3.5 Follow-on hardening is recorded as out of scope | Verified | [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:70), [README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:64), [TerraformAlbOnlyTrafficFlowContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyTrafficFlowContractTest.java:57) |

### Repository Standards

| Standard Area | Status | Evidence & Compliance Notes |
| --- | --- | --- |
| Strict TDD workflow | Verified | Task list requires failing contract tests first before Terraform/doc changes in [27-tasks-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-tasks-alb-only-app-access.md:25) and every unit includes a dedicated contract test in [27-tasks-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-tasks-alb-only-app-access.md:37), [27-tasks-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-tasks-alb-only-app-access.md:53), [27-tasks-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-tasks-alb-only-app-access.md:69), [27-tasks-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-tasks-alb-only-app-access.md:85). |
| Terraform layout and repository patterns | Verified | Changes stay within `infra/terraform/app/dev`, `infra/terraform/floci`, `scripts/`, and `src/test/.../system`, matching the layout requirement in [27-spec-alb-only-app-access.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/27-spec-alb-only-app-access/27-spec-alb-only-app-access.md:91). |
| Spec-driven workflow and conventional commits | Verified | Implementation commit `f277c16` and proof commits `bfe4b3f`, `6876616`, `3ae27e0` are scope-linked and conventional; task/proof artifacts remain under `docs/specs/27-spec-alb-only-app-access/`. |
| Local infrastructure validation centered on `floci` | Verified | [floci README](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/floci/README.md:35) and [verify-alb-only-app-access-contract.sh](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/scripts/verify-alb-only-app-access-contract.sh:106) define and execute the local `floci` flow. |
| Readability of names, tags, and outputs | Verified | Readable SG names and common tags appear in [locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:26), SG tags in [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:119), and downstream outputs in [outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:39). |

### Proof Artifacts

| Unit/Task | Proof Artifact | Status | Verification Result |
| --- | --- | --- | --- |
| Unit 1 / Task 1 | File: `infra/terraform/app/dev/main.tf`, `variables.tf` | Verified | ALB SG and configurable listener port are present at [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:113) and [variables.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/variables.tf:41). |
| Unit 1 / Task 1 | CLI: `terraform -chdir=infra/terraform/app/dev validate` | Verified | Succeeded during `./scripts/verify-alb-only-app-access-contract.sh`; output included `Success! The configuration is valid` plus one existing backend deprecation warning. |
| Unit 1 / Task 1 | CLI: sanitized `terraform ... plan -no-color` | Verified | Live plan showed `aws_security_group.alb`, public IPv4/IPv6 ingress on port `80`, and no public ECS ingress. |
| Unit 1 / Task 1 | Test: `TerraformAlbSecurityGroupContractTest` | Verified | `./mvnw -q -Dtest=TerraformAlbSecurityGroupContractTest,... test` exited `0`; assertions are in [TerraformAlbSecurityGroupContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformAlbSecurityGroupContractTest.java:37). |
| Unit 2 / Task 2 | File: `infra/terraform/app/dev/main.tf`, `outputs.tf` | Verified | ECS SG ingress and downstream IDs are defined at [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:154) and [outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:39). |
| Unit 2 / Task 2 | CLI: sanitized `terraform ... plan -no-color` | Verified | Live plan showed `aws_vpc_security_group_ingress_rule.ecs_task_from_alb` on `8080` only and output values for both SG IDs. |
| Unit 2 / Task 2 | Test: `TerraformEcsTaskSecurityGroupContractTest` | Verified | Targeted Maven run exited `0`; assertions are in [TerraformEcsTaskSecurityGroupContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcsTaskSecurityGroupContractTest.java:37). |
| Unit 3 / Task 3 | File: `infra/terraform/app/dev/main.tf`, `README.md` | Verified | ALB egress and ECS open egress are defined at [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:163); traffic matrix and non-goal notes are in [README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:48). |
| Unit 3 / Task 3 | CLI: sanitized `terraform ... plan -no-color` | Verified | Live plan showed ALB egress only to ECS on `8080`, plus ECS IPv4/IPv6 open egress rules. |
| Unit 3 / Task 3 | Test: `TerraformAlbOnlyTrafficFlowContractTest` | Verified | Targeted Maven run exited `0`; assertions are in [TerraformAlbOnlyTrafficFlowContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyTrafficFlowContractTest.java:33). |
| Unit 4 / Task 4 | File: `scripts/verify-alb-only-app-access-contract.sh`, `infra/terraform/floci/README.md` | Verified | Script and local-operator docs are present at [verify-alb-only-app-access-contract.sh](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/scripts/verify-alb-only-app-access-contract.sh:90) and [floci README](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/floci/README.md:35). |
| Unit 4 / Task 4 | CLI: `./scripts/verify-alb-only-app-access-contract.sh` | Verified | Exit code `0`; started `floci`, initialized backend, validated Terraform, produced `Plan: 23 to add, 0 to change, 0 to destroy`, then cleaned up containers. |
| Unit 4 / Task 4 | Test: `TerraformAlbOnlyAppAccessVerificationWorkflowTest` | Verified | Targeted Maven run exited `0`; assertions are in [TerraformAlbOnlyAppAccessVerificationWorkflowTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformAlbOnlyAppAccessVerificationWorkflowTest.java:33). |

## 3) Validation Issues

| Severity | Issue | Impact | Recommendation |
| --- | --- | --- | --- |
| LOW | Full-repository `./mvnw test` does not complete inside this sandbox because unrelated integration tests fail to bind an embedded server port (`java.net.SocketException: Operation not permitted`). Evidence: local run ended with `Tests run: 224, Failures: 0, Errors: 4, Skipped: 5`; failing tests were `PetClinicIntegrationTests` and `CrashControllerIntegrationTests`. | Verification environment only; spec-27 proof path was still fully validated with targeted tests and live Terraform workflow. | Re-run `./mvnw test` in an environment that permits loopback port binding before merge, and attach that result to the PR if repository policy requires a full-suite green run. |

## 4) Evidence Appendix

### Git commits analyzed

- `f277c16` `feat(terraform): define alb-only app access contract`
- `bfe4b3f` `docs(spec-27): record task 02 verification`
- `6876616` `docs(spec-27): record task 03 verification`
- `3ae27e0` `docs(spec-27): record task 04 verification`

### Changed-file comparison

- Changed since spec creation (`09fbd91..HEAD`): 20 files
- Relevant files listed in task plan: 17 files
- Supporting files not listed but clearly linked to the implementation:
  - `docs/specs/27-spec-alb-only-app-access/27-proofs/27-task-01-proofs.md`
  - `docs/specs/27-spec-alb-only-app-access/27-proofs/27-task-02-proofs.md`
  - `docs/specs/27-spec-alb-only-app-access/27-proofs/27-task-03-proofs.md`
  - `docs/specs/27-spec-alb-only-app-access/27-proofs/27-task-04-proofs.md`
  - `infra/terraform/app/dev/.terraform.lock.hcl`
  - `infra/terraform/app/dev/versions.tf`
- File-integrity conclusion: no unmapped out-of-scope core change was found. `versions.tf` and `.terraform.lock.hcl` are supporting Terraform reproducibility files linked to the `app/dev` stack verification path; proof docs are supporting validation artifacts linked directly to Tasks 1.4, 2.4, 3.4, and 4.4.

### Commands executed and results

| Command | Result |
| --- | --- |
| `git log --stat -10 --oneline` | Located implementation commit `f277c16` and follow-up proof commits for spec 27. |
| `git diff --name-only 09fbd91..HEAD` | Confirmed 20 files changed since spec creation, all linked to spec 27 scope. |
| `./mvnw -q -Dtest=TerraformAlbSecurityGroupContractTest,TerraformEcsTaskSecurityGroupContractTest,TerraformAlbOnlyTrafficFlowContractTest,TerraformAlbOnlyAppAccessVerificationWorkflowTest test` | Exit code `0`. All four spec-specific contract tests passed. |
| `./scripts/verify-alb-only-app-access-contract.sh` | Exit code `0`. Started `floci`, installed `hashicorp/aws v6.45.0`, initialized backend, validated Terraform, and produced `Plan: 23 to add, 0 to change, 0 to destroy`. |
| `./mvnw test` | Exit code `1` in sandbox only; unrelated integration tests failed on embedded-server port binding, not on spec-27 Terraform or contract-test behavior. |

### Security and proof-artifact review

- Proof docs are accessible at `docs/specs/27-spec-alb-only-app-access/27-proofs/`.
- Proof docs front-load reviewer context and explain what each artifact proves before raw evidence.
- No real AWS access keys, secrets, passwords, or tokens were found in the proof docs, Terraform files, or verification script.
- Sanitized placeholders were used consistently: `AWS_ACCESS_KEY_ID=test`, `AWS_SECRET_ACCESS_KEY=test`, and `AWS_EC2_METADATA_DISABLED=true`.
