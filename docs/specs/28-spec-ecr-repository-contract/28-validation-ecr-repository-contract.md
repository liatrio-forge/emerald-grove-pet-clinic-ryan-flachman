# 28 Validation Report - ECR Repository Contract

## 1) Executive Summary

- **Overall:** PASS
  Gates tripped: none
- **Implementation Ready:** Yes. The spec-28 ECR contract is implemented, mapped to the planned files, and its spec-owned verification path passes end to end; one proof-document accuracy issue remains non-blocking.
- **Key metrics:** 100% requirements verified (18/18), 100% required proof artifacts working (16/16), files changed vs expected `21 / 17` with `4` unlisted supporting proof files and `0` unmapped core files

## 2) Coverage Matrix

### Functional Requirements

| Requirement ID/Name | Status | Evidence (file:lines, commit, or artifact) |
| --- | --- | --- |
| FR-1 One private ECR repository in `dev` | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:5), commit `b717b20`, `./scripts/verify-ecr-repository-contract.sh` plan output showed `aws_ecr_repository.app` |
| FR-2 Environment-scoped repository name | Verified | [infra/terraform/app/dev/locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:29), [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:6), plan output showed `name = "dev-petclinic"` |
| FR-3 Prevent overwriting existing tags | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:7), [TerraformEcrRepositoryContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryContractTest.java:47) |
| FR-4 Immutable Git SHA tags only | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:42), [TerraformEcrRepositoryContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryContractTest.java:55) |
| FR-5 No mutable convenience tags in v1 | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:48), [TerraformEcrRepositoryContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryContractTest.java:56) |
| FR-6 Common tags plus ECR-specific clarity tags | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:10), plan output showed `RepositoryRole = "application-image"` |
| FR-7 Lifecycle policy exists | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:16), plan output showed `aws_ecr_lifecycle_policy.app` |
| FR-8 Untagged images expire automatically | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:22), [TerraformEcrLifecyclePolicyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrLifecyclePolicyContractTest.java:43) |
| FR-9 Retain bounded count of tagged Git SHA images | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:34), plan output showed `countNumber = 5` |
| FR-10 Count-based retention, not day-based | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:38), [TerraformEcrLifecyclePolicyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrLifecyclePolicyContractTest.java:46) |
| FR-11 Lifecycle rules readable to junior developer | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:23), [docs/specs/28-spec-ecr-repository-contract/28-proofs/28-task-02-proofs.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/28-spec-ecr-repository-contract/28-proofs/28-task-02-proofs.md:19) |
| FR-12 Lifecycle preview documented before AWS enforcement | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:50), [infra/terraform/floci/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/floci/README.md:75) |
| FR-13 Expose deterministic repository URI for CI | Verified | [infra/terraform/app/dev/outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:49), plan output `repository_uri = (known after apply)` |
| FR-14 Expose exactly `repository_uri` and `repository_name` as repository outputs | Verified | [infra/terraform/app/dev/outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:49), [TerraformEcrRepositoryOutputsAndDestroyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryOutputsAndDestroyContractTest.java:46) |
| FR-15 CI push contract documented with immutable Git SHA tags | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:61), [TerraformEcrRepositoryOutputsAndDestroyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryOutputsAndDestroyContractTest.java:52) |
| FR-16 Terraform destroy may delete repo even with images present | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:8), plan output showed `force_delete = true` |
| FR-17 Force-delete behavior explicit in infrastructure code | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:8), [TerraformEcrRepositoryOutputsAndDestroyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryOutputsAndDestroyContractTest.java:45) |
| FR-18 Destroy consequence documented for dev-only POC | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:67), [TerraformEcrRepositoryOutputsAndDestroyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryOutputsAndDestroyContractTest.java:51) |

### Repository Standards

| Standard Area | Status | Evidence & Compliance Notes |
| --- | --- | --- |
| TDD-aligned contract coverage | Verified | New Terraform contract tests exist for repository, lifecycle, outputs/destroy, and verification workflow: [TerraformEcrRepositoryContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryContractTest.java:37), [TerraformEcrLifecyclePolicyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrLifecyclePolicyContractTest.java:33), [TerraformEcrRepositoryOutputsAndDestroyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryOutputsAndDestroyContractTest.java:35), [TerraformEcrRepositoryVerificationWorkflowTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/TerraformEcrRepositoryVerificationWorkflowTest.java:33). Squashed history does not preserve red-phase chronology, but scope-aligned tests are present. |
| Terraform layout and naming conventions | Verified | ECR work extends existing `app/dev` stack and `dev` naming/tagging patterns: [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:5), [infra/terraform/app/dev/locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:26) |
| Proof artifact hygiene | Verified | Proof path uses sanitized placeholder credentials only; secret scan found no real credentials in spec-28 artifacts. Evidence: regex scan for AWS-key and secret-token patterns, plus [scripts/verify-ecr-repository-contract.sh](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/scripts/verify-ecr-repository-contract.sh:30) |
| Conventional commit and traceability | Verified | Commit `b717b20` uses `feat(terraform): ...` format and references `Spec 28` / `T01-T04` |
| Quality gates | Verified | `./scripts/verify-ecr-repository-contract.sh` exited `0`; focused Maven suite passed `Tests run: 7, Failures: 0, Errors: 0, Skipped: 0`; broader `./mvnw test` failed outside spec-28 with environment-level socket bind errors, not ECR-contract regressions |
| Documentation and reviewer guidance | Verified | Reviewer-facing docs added in [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:42) and [infra/terraform/floci/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/floci/README.md:54) |

### Proof Artifacts

| Unit/Task | Proof Artifact | Status | Verification Result |
| --- | --- | --- | --- |
| 1.0 | File: `infra/terraform/app/dev/main.tf`, `locals.tf`, `README.md` | Verified | Repository resource, name local, immutable tags, and Git-SHA-only docs present |
| 1.0 | CLI: `terraform validate` via `./scripts/verify-ecr-repository-contract.sh` | Verified | Exit `0`; Terraform reported configuration valid with backend deprecation warning only |
| 1.0 | CLI: sanitized `terraform plan -no-color` | Verified | Plan showed one `aws_ecr_repository.app` with `name = "dev-petclinic"` and `image_tag_mutability = "IMMUTABLE"` |
| 1.0 | Test: `TerraformEcrRepositoryContractTest` | Verified | Passed in focused suite |
| 2.0 | File: lifecycle policy in `main.tf` | Verified | Separate `untagged` and `tagged` rules with `countNumber = 5` |
| 2.0 | CLI: sanitized `terraform plan -no-color` | Verified | Plan showed `aws_ecr_lifecycle_policy.app` attached to repository |
| 2.0 | Documentation: lifecycle preview instructions | Verified | Present in [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:50) and [infra/terraform/floci/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/floci/README.md:75) |
| 2.0 | Test: `TerraformEcrLifecyclePolicyContractTest` | Verified | Passed in focused suite |
| 3.0 | File: `outputs.tf` repository outputs | Verified | `repository_uri` and `repository_name` defined in [outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:49) |
| 3.0 | File: `main.tf` + `README.md` destroy contract | Verified | `force_delete = true` and destroy consequence documented |
| 3.0 | CLI: sanitized `terraform plan -no-color` | Verified | Plan showed `force_delete = true`, `repository_name`, and `repository_uri` output |
| 3.0 | Test: `TerraformEcrRepositoryOutputsAndDestroyContractTest` | Verified | Passed in focused suite |
| 4.0 | File: `scripts/verify-ecr-repository-contract.sh` | Verified | Reproducible `floci` workflow with required file checks and sanitized credentials |
| 4.0 | File: local verification docs | Verified | Command sequence and placeholder credentials documented in `floci/README.md` |
| 4.0 | CLI: `./scripts/verify-ecr-repository-contract.sh` | Verified | Exit `0`; initialized backend, validated config, planned resources, cleaned up `floci` |
| 4.0 | Test: `TerraformEcrRepositoryVerificationWorkflowTest` | Verified | Passed in focused suite |

## 3) Validation Issues

| Severity | Issue | Impact | Recommendation |
| --- | --- | --- | --- |
| MEDIUM | Proof artifact drift in [28-task-04-proofs.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/28-spec-ecr-repository-contract/28-proofs/28-task-04-proofs.md:100). The broad-suite subsection claims `Tests run: 228, Failures: 3, Errors: 4` and attributes some failures to now-resolved Terraform spacing assertions, but the current `./mvnw test` result is `Tests run: 228, Failures: 0, Errors: 4, Skipped: 5`, with errors caused by embedded Tomcat socket bind restrictions (`java.net.SocketException: Operation not permitted`). | Verification evidence is partially stale and can mislead reviewers about current repo-level quality signals. | Refresh the task-04 proof doc with the current `./mvnw test` output, explicitly noting the sandbox/environment cause for the four unrelated errors, or remove the broad-suite subsection if it is not a required artifact for spec-28. |

## 4) Evidence Appendix

### Git commits analyzed

- `b717b20` `feat(terraform): add dev ecr repository contract`
  Changed files: 21
  Scope linkage: `Related to T01-T04 in Spec 28`
- `git log --stat -10 --oneline` showed no later code commit superseding spec-28 implementation

### Changed-file integrity

- Core files changed and mapped to task-list relevant files:
  - `infra/terraform/app/dev/main.tf`
  - `infra/terraform/app/dev/locals.tf`
  - `infra/terraform/app/dev/outputs.tf`
  - `infra/terraform/app/dev/README.md`
  - `infra/terraform/floci/README.md`
  - `scripts/verify-ecr-repository-contract.sh`
  - `src/test/java/.../TerraformEcrRepositoryContractTest.java`
  - `src/test/java/.../TerraformEcrLifecyclePolicyContractTest.java`
  - `src/test/java/.../TerraformEcrRepositoryOutputsAndDestroyContractTest.java`
  - `src/test/java/.../TerraformEcrRepositoryVerificationWorkflowTest.java`
- Supporting files changed with clear linkage:
  - `docs/specs/28-spec-ecr-repository-contract/28-proofs/28-task-01-proofs.md`
  - `docs/specs/28-spec-ecr-repository-contract/28-proofs/28-task-02-proofs.md`
  - `docs/specs/28-spec-ecr-repository-contract/28-proofs/28-task-03-proofs.md`
  - `docs/specs/28-spec-ecr-repository-contract/28-proofs/28-task-04-proofs.md`
- Additional supporting context files changed and listed:
  - `28-spec-ecr-repository-contract.md`
  - `28-tasks-ecr-repository-contract.md`
  - `28-audit-ecr-repository-contract.md`
  - `28-questions-1-ecr-repository-contract.md`
- Out-of-scope core files without linkage: none

### Commands executed with results

```text
$ git status --short
clean working tree

$ git show --stat --name-only --format=fuller b717b20
commit uses conventional format and maps to Spec 28 / T01-T04

$ ./scripts/verify-ecr-repository-contract.sh
exit 0
terraform validate: Success! The configuration is valid
terraform plan: Plan: 25 to add, 0 to change, 0 to destroy
outputs include repository_name = "dev-petclinic" and repository_uri = (known after apply)

$ ./mvnw -Dtest=TerraformAlbSecurityGroupContractTest,TerraformAlbOnlyTrafficFlowContractTest,TerraformEcsTaskSecurityGroupContractTest,TerraformEcrRepositoryContractTest,TerraformEcrLifecyclePolicyContractTest,TerraformEcrRepositoryOutputsAndDestroyContractTest,TerraformEcrRepositoryVerificationWorkflowTest test
exit 0
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0

$ ./mvnw test
exit 1
Tests run: 228, Failures: 0, Errors: 4, Skipped: 5
Unrelated errors:
- PetClinicIntegrationTests.testFindAll
- PetClinicIntegrationTests.testOwnerDetails
- CrashControllerIntegrationTests.testTriggerExceptionHtml
- CrashControllerIntegrationTests.testTriggerExceptionJson
Root cause in this environment: java.net.SocketException: Operation not permitted while starting embedded Tomcat

$ rg -n "AKIA|ASIA|SECRET_ACCESS_KEY|aws_secret_access_key|token|password" docs/specs/28-spec-ecr-repository-contract infra/terraform/app/dev infra/terraform/floci scripts/verify-ecr-repository-contract.sh
No real credentials found; only sanitized placeholder values were present
```

### Validation gate results

- Gate A: PASS. No CRITICAL or HIGH issues found.
- Gate B: PASS. Functional requirement matrix has no `Unknown` entries.
- Gate C: PASS. All required proof artifacts were accessible and functional.
- Gate D: PASS. No unmapped out-of-scope core changes; unlisted changed files were supporting proof docs with clear linkage.
- Gate E: PASS. Implementation follows repository Terraform patterns, adds aligned contract tests, and uses a reproducible verification workflow; full-suite failure was environment-specific and outside spec-28 scope.
- Gate F: PASS. Proof artifacts used placeholder credentials and exposed no sensitive secrets.

---

**Validation Completed:** 2026-05-19 10:53:40 CDT
**Validation Performed By:** GPT-5 Codex
