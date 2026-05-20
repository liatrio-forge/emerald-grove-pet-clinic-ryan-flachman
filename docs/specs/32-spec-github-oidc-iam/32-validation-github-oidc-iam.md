# 32 Validation - GitHub OIDC IAM

## 1) Executive Summary

- **Overall:** FAIL
  Gates tripped: **GATE A** (HIGH issue present), **GATE C** (not all proof artifacts are accessible/functional)
- **Implementation Ready:** No. The Terraform, workflow, tests, and repo-owned verification path validate successfully, but required screenshot proof artifacts for tasks 2 and 3 were not produced.
- **Key metrics:** 100% Functional Requirements Verified, 80% Proof Artifacts Working, 18 changed files vs 19 relevant files listed

## 2) Coverage Matrix

### Functional Requirements

| Requirement ID/Name | Status | Evidence |
| --- | --- | --- |
| FR-1.1 Use GitHub OIDC provider `token.actions.githubusercontent.com` | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:31), [GitHubOidcTrustPolicyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubOidcTrustPolicyContractTest.java:31), commit `6057cfd` |
| FR-1.2 Require `aud = sts.amazonaws.com` for every role | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:17), [infra/terraform/app/dev/locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:5), contract test run `./mvnw -q -Dtest=GitHubOidcTrustPolicyContractTest test` exit `0` |
| FR-1.3 Require exact `sub` matching and forbid repo wildcard trust | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:23), [infra/terraform/app/dev/locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:8), [GitHubOidcTrustPolicyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubOidcTrustPolicyContractTest.java:47) |
| FR-1.4 Scope every trusted subject to repository `liatrio-forge/emerald-grove-pet-clinic-ryan-flachman` | Verified | [infra/terraform/app/dev/locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:4), sanitized verification script plan output |
| FR-1.5 Keep v1 trust boundary to `sub` and `aud` claims | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:17), [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:23) |
| FR-2.1 Define separate Terraform apply and destroy roles | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:97), [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:107), [infra/terraform/app/dev/outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:119), [GitHubTerraformWorkflowRolesContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubTerraformWorkflowRolesContractTest.java:35) |
| FR-2.2 Scope apply role to exact protected `dev` environment subject | Verified | [infra/terraform/app/dev/locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:9), [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:99) |
| FR-2.3 Scope destroy role to exact protected `dev-destroy` environment subject | Verified | [infra/terraform/app/dev/locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:10), [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:109) |
| FR-2.4 Keep destroy stricter than apply with separate role and environment | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:55), [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:56) |
| FR-2.5 Keep Terraform permissions broad for POC but below admin access | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:42), [GitHubTerraformWorkflowRolesContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubTerraformWorkflowRolesContractTest.java:65), verification script plan output |
| FR-2.6 Document required IAM actions explicitly | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:63) |
| FR-3.1 Define separate app deploy role | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:127), [infra/terraform/app/dev/outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:129), [GitHubDeployRoleAndConfigurationContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubDeployRoleAndConfigurationContractTest.java:34) |
| FR-3.2 Scope deploy role to protected `dev` environment jobs | Verified | [infra/terraform/app/dev/locals.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/locals.tf:11), [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:163) |
| FR-3.3 Keep AWS access environment-based even if workflow source is `main` | Verified | [.GitHub/workflows/terraform-apply-dev.yml](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/.github/workflows/terraform-apply-dev.yml:23), [.GitHub/workflows/terraform-apply-dev.yml](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/.github/workflows/terraform-apply-dev.yml:37) |
| FR-3.4 Keep deploy permissions limited to ECS rollout path and reads | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:127), [GitHubDeployRoleAndConfigurationContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubDeployRoleAndConfigurationContractTest.java:48) |
| FR-3.5 Do not reuse Terraform role for deploy | Verified | [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:97), [infra/terraform/app/dev/main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:160) |
| FR-4.1 Use protected GitHub environments as primary home for role ARNs and deployment-sensitive values | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:83), [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:89) |
| FR-4.2 Allow only non-sensitive defaults at repository variable scope | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:91) |
| FR-4.3 Define required environment names `dev` and `dev-destroy` | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:55), [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:56) |
| FR-4.4 Define minimum GitHub configuration inputs including region, role ARNs, and backend-state variables | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:89), [GitHubDeployRoleAndConfigurationContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubDeployRoleAndConfigurationContractTest.java:65) |
| FR-4.5 Prohibit long-lived AWS access keys in GitHub secrets for these workflows | Verified | [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:98), [.GitHub/workflows/terraform-apply-dev.yml](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/.github/workflows/terraform-apply-dev.yml:60) |
| FR-4.6 Require AWS-assuming jobs to declare the protected environment matching the trusted subject | Verified | [.GitHub/workflows/terraform-apply-dev.yml](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/.github/workflows/terraform-apply-dev.yml:34), [GitHubDeployRoleAndConfigurationContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubDeployRoleAndConfigurationContractTest.java:65) |

### Repository Standards

| Standard Area | Status | Evidence & Compliance Notes |
| --- | --- | --- |
| TDD / contract-first workflow | Verified | Commit sequence shows tests introduced with implementation slices across `6057cfd` and `83aa215`; spec and task notes explicitly require strict TDD; focused contract suite passes. |
| Infrastructure layout and patterns | Verified | Changes stay within `infra/terraform/app/dev`, `infra/terraform/floci`, repo proof docs, and `scripts/verify-*` pattern: [scripts/verify-GitHub-oidc-iam-contract.sh](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/scripts/verify-github-oidc-iam-contract.sh:1). |
| Testing patterns | Verified | Focused Java contract tests exist for Terraform, workflow, and docs: [GitHubOidcTrustPolicyContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubOidcTrustPolicyContractTest.java:1), [GitHubTerraformWorkflowRolesContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubTerraformWorkflowRolesContractTest.java:1), [GitHubDeployRoleAndConfigurationContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubDeployRoleAndConfigurationContractTest.java:1), [GitHubOidcIamDocumentationContractTest.java](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/GitHubOidcIamDocumentationContractTest.java:1). |
| Quality gates / reproducible validation | Verified | `./mvnw -q -Dtest=GitHubOidcTrustPolicyContractTest,GitHubTerraformWorkflowRolesContractTest,GitHubDeployRoleAndConfigurationContractTest,GitHubOidcIamDocumentationContractTest test` exit `0`; `./scripts/verify-github-oidc-iam-contract.sh` exit `0` with `floci`. |
| Documentation-first spec workflow | Verified | Proof docs are present for tasks 1-4 and front-load what each artifact proves: [32-task-01-proofs.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-proofs/32-task-01-proofs.md:1) through [32-task-04-proofs.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-proofs/32-task-04-proofs.md:1). |
| Proof sanitization / secret handling | Verified | Evidence uses placeholder credentials only and no live keys were found in searched proof/docs/script/workflow files. |

### Proof Artifacts

| Unit/Task | Proof Artifact | Status | Verification Result |
| --- | --- | --- | --- |
| Unit 1 / Task 1 | Contract test: `GitHubOidcTrustPolicyContractTest` | Verified | Serial focused Maven run exited `0`. |
| Unit 1 / Task 1 | CLI: `terraform validate` | Verified | `./scripts/verify-github-oidc-iam-contract.sh` exited `0`; Terraform validate succeeded with only existing backend deprecation warning. |
| Unit 1 / Task 1 | CLI: sanitized `terraform plan -no-color` | Verified | Verification script exited `0`; plan showed GitHub OIDC provider and exact `aud` / exact `sub` trust conditions. |
| Unit 1 / Task 1 | Proof doc `32-task-01-proofs.md` | Verified | Accessible and explains each artifact before raw evidence. |
| Unit 2 / Task 2 | Contract test: `GitHubTerraformWorkflowRolesContractTest` | Verified | Serial focused Maven run exited `0`. |
| Unit 2 / Task 2 | File evidence: Terraform roles, outputs, README role matrix | Verified | Distinct roles and role outputs present in [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:97), [outputs.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/outputs.tf:119), [README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:51). |
| Unit 2 / Task 2 | CLI: sanitized `terraform plan -no-color` | Verified | Verification script exited `0`; plan showed `terraform-apply-dev` and `terraform-destroy-dev` plus outputs. |
| Unit 2 / Task 2 | Screenshot: GitHub environment settings for `dev` and `dev-destroy` | Failed | Task list requires screenshots, but proof doc explicitly substitutes narrative repo-backed evidence instead of screenshots: [32-tasks-GitHub-oidc-iam.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-tasks-github-oidc-iam.md:59), [32-task-02-proofs.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-proofs/32-task-02-proofs.md:26). |
| Unit 3 / Task 3 | Contract test: `GitHubDeployRoleAndConfigurationContractTest` | Verified | Serial focused Maven run exited `0`. |
| Unit 3 / Task 3 | File evidence: deploy role, workflow contract, GitHub variable table | Verified | Present in [main.tf](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/main.tf:127), [terraform-apply-dev.yml](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/.github/workflows/terraform-apply-dev.yml:34), [README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:89). |
| Unit 3 / Task 3 | Screenshot: GitHub environment or variable configuration | Failed | Task list requires screenshots, but proof doc explicitly substitutes repository-backed evidence because the UI is not stored in repo: [32-tasks-GitHub-oidc-iam.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-tasks-github-oidc-iam.md:77), [32-task-03-proofs.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-proofs/32-task-03-proofs.md:28). |
| Unit 4 / Task 4 | Contract test: `GitHubOidcIamDocumentationContractTest` | Verified | Serial focused Maven run exited `0`. |
| Unit 4 / Task 4 | File: verification docs in app/dev and floci READMEs | Verified | Present and aligned: [infra/terraform/app/dev/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/app/dev/README.md:103), [infra/terraform/floci/README.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/infra/terraform/floci/README.md:123). |
| Unit 4 / Task 4 | File/CLI: `scripts/verify-github-oidc-iam-contract.sh` | Verified | Script exists and elevated execution exited `0` after `floci` startup, `terraform init`, `terraform validate`, and sanitized `terraform plan -no-color`. |

## 3) Validation Issues

| Severity | Issue | Impact | Recommendation |
| --- | --- | --- | --- |
| HIGH | Missing required screenshot proof artifact for task 2. The task list explicitly requires GitHub environment screenshots for `dev` and `dev-destroy`, but the proof doc states the repository cannot produce those captures and substitutes narrative evidence instead: [32-tasks-GitHub-oidc-iam.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-tasks-github-oidc-iam.md:59), [32-tasks-GitHub-oidc-iam.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-tasks-github-oidc-iam.md:68), [32-task-02-proofs.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-proofs/32-task-02-proofs.md:26). | Verification gate failure for Unit 2 proof completeness | Add sanitized screenshots of the GitHub `dev` and `dev-destroy` environment settings to the proof set, or formally amend the task list/spec to accept repo-backed evidence instead of screenshots. |
| HIGH | Missing required screenshot proof artifact for task 3. The task list requires GitHub environment or variable configuration screenshots, but the proof doc again substitutes narrative repo-backed evidence: [32-tasks-GitHub-oidc-iam.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-tasks-github-oidc-iam.md:77), [32-tasks-GitHub-oidc-iam.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-tasks-github-oidc-iam.md:86), [32-task-03-proofs.md](/Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/docs/specs/32-spec-github-oidc-iam/32-proofs/32-task-03-proofs.md:28). | Verification gate failure for Unit 3 proof completeness | Add sanitized screenshots showing protected environment ownership of role ARNs and backend variables, or formally amend the planned proof requirements. |
| MEDIUM | Parallel Maven validation is not reliable in this repository because concurrent surefire runs collide in shared `target/surefire` state. Evidence: parallel run produced `SurefireBooterForkException` while the same focused suite passed serially. | Validation noise and false negatives during automated review | Keep focused Maven validation serial for this repo, or isolate test output directories if parallel verification is needed later. |

## 4) Evidence Appendix

### Git commits analyzed

- `6057cfd` `feat: add github oidc trust baseline`
  Files: spec, tasks, audit, task-01 proof, Terraform trust baseline, `GitHubOidcTrustPolicyContractTest`
- `83aa215` `feat: add github oidc role contract`
  Files: workflow, task-02/03/04 proofs, Terraform role/config/docs/script changes, three contract tests

### File comparison results

- Relevant files listed in task plan: `19`
- Changed files since spec implementation started: `18`
- All changed core files map to the spec/task list.
- One listed relevant file remained unchanged: `infra/terraform/app/dev/backend.hcl.example`
- Supporting proof/docs/test files are linked to the core Terraform and workflow changes.
- Unrelated worktree change observed during validation and not assessed as part of spec 32: `docs/specs/README.md`

### Commands executed and results

```bash
git log --stat -10 --oneline
```

- Confirmed implementation commits `6057cfd` and `83aa215` cover spec 32 work.

```bash
./mvnw -q -Dtest=GitHubOidcTrustPolicyContractTest,GitHubTerraformWorkflowRolesContractTest,GitHubDeployRoleAndConfigurationContractTest,GitHubOidcIamDocumentationContractTest test
```

- Exit `0`

```bash
./scripts/verify-github-oidc-iam-contract.sh
```

- First sandboxed attempt failed with Docker socket permission denial.
- Elevated rerun exit `0`.
- `floci` started successfully.
- `terraform init` succeeded.
- `terraform validate` succeeded with the existing `dynamodb_table` backend deprecation warning.
- Sanitized `terraform plan -no-color` succeeded and showed:
  - one GitHub OIDC provider
  - exact `sub` conditions for `dev` and `dev-destroy`
  - separate apply, destroy, and deploy role outputs

```bash
rg -n "AKIA|ASIA|aws_secret_access_key|AWS_SECRET_ACCESS_KEY=|AWS_ACCESS_KEY_ID=|ghp_|github_pat_|BEGIN RSA PRIVATE KEY|BEGIN OPENSSH PRIVATE KEY|token" docs/specs/32-spec-github-oidc-iam infra/terraform/app/dev/README.md infra/terraform/floci/README.md scripts/verify-github-oidc-iam-contract.sh .github/workflows/terraform-apply-dev.yml
```

- No live credentials detected. Placeholder credentials only.

---

**Validation Completed:** 2026-05-20 08:24:52 CDT
**Validation Performed By:** GPT-5 Codex
