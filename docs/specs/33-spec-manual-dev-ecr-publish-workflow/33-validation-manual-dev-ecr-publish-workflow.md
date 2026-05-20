# 33 Validation - Manual Dev ECR Publish Workflow

## 1) Executive Summary

- **Overall:** FAIL
  Gates tripped: **GATE A** (HIGH issues present), **GATE C** (not all proof artifacts are accessible/functional)
- **Implementation Ready:** No. The repository-level workflow, Terraform, tests, and documentation contracts are in place, but required GitHub/AWS runtime proof artifacts were not captured.
- **Key metrics:** 100% Functional Requirements Verified, 69% Proof Artifacts Working, 18 changed files vs 15 relevant files listed

## 2) Coverage Matrix

### Functional Requirements

| Requirement ID/Name | Status | Evidence |
| --- | --- | --- |
| FR-1.1 Define one `workflow_dispatch` workflow | Verified | `.github/workflows/manual-dev-ecr-publish.yml:1-8`, `ManualDevEcrPublishWorkflowDispatchContractTest.java:31-42`, commit `cb4d287` |
| FR-1.2 Allow publication only from `main` | Verified | `.github/workflows/manual-dev-ecr-publish.yml:22-36`, `ManualDevEcrPublishWorkflowDispatchContractTest.java:44-59` |
| FR-1.3 Require typed confirmation for dev image publication | Verified | `.github/workflows/manual-dev-ecr-publish.yml:5-8`, `.github/workflows/manual-dev-ecr-publish.yml:28-31`, `ManualDevEcrPublishWorkflowDispatchContractTest.java:50-58` |
| FR-1.4 Use protected `dev` environment for AWS-backed publish job | Verified | `.github/workflows/manual-dev-ecr-publish.yml:34-42`, `ManualDevEcrPublishWorkflowDispatchContractTest.java:52-53` |
| FR-1.5 Prevent overlapping runs with queued concurrency | Verified | `.github/workflows/manual-dev-ecr-publish.yml:14-16`, `ManualDevEcrPublishWorkflowDispatchContractTest.java:54-56` |
| FR-2.1 Run the repository Maven wrapper package path before publish | Verified | `.github/workflows/manual-dev-ecr-publish.yml:62-63`, `ManualDevEcrPublishWorkflowBuildAndAuthContractTest.java:31-42` |
| FR-2.2 Use `./mvnw package -DskipTests` as the pre-push JVM build contract | Verified | `.github/workflows/manual-dev-ecr-publish.yml:62-63`, `ManualDevEcrPublishWorkflowBuildAndAuthContractTest.java:37-41` |
| FR-2.3 Authenticate to AWS through GitHub OIDC instead of long-lived keys | Verified | `.github/workflows/manual-dev-ecr-publish.yml:10-12`, `.github/workflows/manual-dev-ecr-publish.yml:65-69`, `ManualDevEcrPublishWorkflowBuildAndAuthContractTest.java:45-60` |
| FR-2.4 Use dedicated environment-scoped `APP_PUBLISH_ROLE_ARN` | Verified | `.github/workflows/manual-dev-ecr-publish.yml:45-47`, `.github/workflows/manual-dev-ecr-publish.yml:69`, `infra/terraform/app/dev/main.tf:127-170`, `infra/terraform/app/dev/outputs.tf:129-132`, `GitHubDeployRoleAndConfigurationContractTest.java:53-65` |
| FR-2.5 Request only required GitHub Actions permissions | Verified | `.github/workflows/manual-dev-ecr-publish.yml:10-12`, `ManualDevEcrPublishWorkflowBuildAndAuthContractTest.java:50-59` |
| FR-3.1 Build from the repository-owned root `Dockerfile` | Verified | `.github/workflows/manual-dev-ecr-publish.yml:74-75`, `ManualDevEcrPublishWorkflowPushContractTest.java:31-43` |
| FR-3.2 Push only to the existing `dev-petclinic` ECR repository via exported repository URI | Verified | `.github/workflows/manual-dev-ecr-publish.yml:39-41`, `.github/workflows/manual-dev-ecr-publish.yml:77-78`, `infra/terraform/app/dev/README.md:95-97`, `ManualDevEcrPublishWorkflowPushContractTest.java:37-40` |
| FR-3.3 Tag the published image with the full Git SHA | Verified | `.github/workflows/manual-dev-ecr-publish.yml:42`, `.github/workflows/manual-dev-ecr-publish.yml:83-86`, `ManualDevEcrPublishWorkflowPushContractTest.java:45-57` |
| FR-3.4 Do not publish mutable convenience tags such as `latest` or `main` | Verified | `ManualDevEcrPublishWorkflowPushContractTest.java:55-57`, `.github/workflows/manual-dev-ecr-publish.yml:74-92` |
| FR-3.5 Surface the fully qualified image reference and pushed digest in workflow-visible output | Verified | `.github/workflows/manual-dev-ecr-publish.yml:80-92`, `ManualDevEcrPublishWorkflowPushContractTest.java:51-54` |
| FR-3.6 Keep ECS registration and service updates out of scope | Verified | `ManualDevEcrPublishWorkflowPushContractTest.java:60-69`, `.github/workflows/manual-dev-ecr-publish.yml:1-92` |

### Repository Standards

| Standard Area | Status | Evidence & Compliance Notes |
| --- | --- | --- |
| Maven-first CI contract | Verified | Workflow packages with `./mvnw package -DskipTests` before Docker publish in `.github/workflows/manual-dev-ecr-publish.yml:62-78`. |
| GitHub Actions guard/environment pattern reuse | Verified | Workflow uses explicit safety gates, protected environment, and concurrency in `.github/workflows/manual-dev-ecr-publish.yml:14-38`, matching the repo’s existing environment-bound workflow style. |
| Focused Java contract-test pattern | Verified | Focused workflow/doc/IAM tests exist and passed: `ManualDevEcrPublishWorkflowDispatchContractTest.java:27-61`, `ManualDevEcrPublishWorkflowBuildAndAuthContractTest.java:27-62`, `ManualDevEcrPublishWorkflowPushContractTest.java:27-72`, `ManualDevEcrPublishWorkflowDocumentationContractTest.java:27-71`, `GitHubDeployRoleAndConfigurationContractTest.java:27-107`. |
| Quality gates | Verified | `./mvnw -Dtest=ManualDevEcrPublishWorkflowDispatchContractTest,ManualDevEcrPublishWorkflowBuildAndAuthContractTest,ManualDevEcrPublishWorkflowPushContractTest,ManualDevEcrPublishWorkflowDocumentationContractTest,GitHubDeployRoleAndConfigurationContractTest test` exited `0`; `pre-commit run check-yaml --files .github/workflows/manual-dev-ecr-publish.yml` passed; `pre-commit run markdownlint --files ...` passed. |
| Documentation-first spec workflow | Verified | Spec, tasks, audit, and task proof docs are present under `docs/specs/33-spec-manual-dev-ecr-publish-workflow/`. |
| Proof sanitization / secret handling | Verified | Secret scan found placeholder credentials only in `infra/terraform/app/dev/README.md:123-125`; no live keys or tokens detected in the workflow, docs, or proof files searched. |
| Strict TDD traceability | Failed | The final implementation landed in a single feature commit `cb4d287`, so the git history does not preserve a reviewer-auditable RED → GREEN → REFACTOR sequence even though the resulting contract tests exist and pass. |

### Proof Artifacts

| Unit/Task | Proof Artifact | Status | Verification Result |
| --- | --- | --- | --- |
| Unit 1 / Task 1 | File: `.github/workflows/manual-dev-ecr-publish.yml` | Verified | Workflow contains `workflow_dispatch`, typed confirmation, main-branch guards, protected `dev` environment, and queued concurrency at `.github/workflows/manual-dev-ecr-publish.yml:1-38`. |
| Unit 1 / Task 1 | Screenshot: manual-run form | Failed | Required by `33-tasks-manual-dev-ecr-publish-workflow.md:36-39`, but `33-task-01-proofs.md:24-26` and `33-task-01-proofs.md:97-108` explicitly say the screenshot was not captured. |
| Unit 1 / Task 1 | Screenshot: publish job/environment details | Failed | Required by `33-tasks-manual-dev-ecr-publish-workflow.md:37-39`, but `33-task-01-proofs.md:24-26` and `33-task-01-proofs.md:97-108` explicitly say the environment-gate screenshot remains a follow-up artifact. |
| Unit 1 / Task 1 | Test: `ManualDevEcrPublishWorkflowDispatchContractTest` | Verified | Focused Maven run exited `0`; assertions map to trigger, confirmation, branch, environment, and concurrency contract. |
| Unit 2 / Task 2 | File: workflow build/auth contract | Verified | Workflow contains Maven packaging, `id-token: write`, and OIDC role assumption at `.github/workflows/manual-dev-ecr-publish.yml:10-12`, `.github/workflows/manual-dev-ecr-publish.yml:62-72`. |
| Unit 2 / Task 2 | CLI: sanitized GitHub Actions job log excerpt | Failed | Required by `33-tasks-manual-dev-ecr-publish-workflow.md:53-56`, but `33-task-02-proofs.md:52-79` provides only repository grep output and no actual workflow run log excerpt. |
| Unit 2 / Task 2 | Documentation: variable contract table | Verified | Variable ownership is documented in `infra/terraform/app/dev/README.md:90-104`. |
| Unit 2 / Task 2 | Test: `ManualDevEcrPublishWorkflowBuildAndAuthContractTest` plus IAM contract coverage | Verified | Focused Maven run exited `0`; workflow and publish-role contract assertions passed. |
| Unit 3 / Task 3 | File: immutable publish workflow contract | Verified | Workflow uses root `Dockerfile`, `REPOSITORY_URI`, and full SHA tagging at `.github/workflows/manual-dev-ecr-publish.yml:39-42`, `.github/workflows/manual-dev-ecr-publish.yml:74-92`. |
| Unit 3 / Task 3 | CLI: workflow logs or job summary for published image and digest | Failed | Required by `33-tasks-manual-dev-ecr-publish-workflow.md:71-74`, but `33-task-03-proofs.md:19-25` and `33-task-03-proofs.md:80-91` state live workflow logs were not captured. |
| Unit 3 / Task 3 | CLI: ECR verification command output | Failed | Required by `33-tasks-manual-dev-ecr-publish-workflow.md:71-74`, but `33-task-03-proofs.md:80-91` states ECR inspection output remains a follow-up artifact. |
| Unit 3 / Task 3 | Test: `ManualDevEcrPublishWorkflowPushContractTest` | Verified | Focused Maven run exited `0`; immutable SHA-only publication and non-goal assertions passed. |
| Unit 4 / Task 4 | File: README documentation of workflow contract | Verified | Root and app/dev READMEs document the workflow name, guardrails, variables, verification commands, and scope boundaries at `README.md:164-182` and `infra/terraform/app/dev/README.md:149-190`. |
| Unit 4 / Task 4 | CLI: documented verification commands | Verified | `gh workflow run`, `gh run view --log`, and `aws ecr describe-images` are documented in `infra/terraform/app/dev/README.md:168-183`. |
| Unit 4 / Task 4 | Diff: documentation changes remain in scope | Verified | Commit `cb4d287` updates only the expected README and proof/doc files for the workflow contract. |
| Unit 4 / Task 4 | Test: `ManualDevEcrPublishWorkflowDocumentationContractTest` | Verified | Focused Maven run exited `0`; documentation contract assertions passed. |

## 3) Validation Issues

| Severity | Issue | Impact | Recommendation |
| --- | --- | --- | --- |
| HIGH | Missing required GitHub UI screenshots for Unit 1. The task list requires a manual-run form screenshot and a protected-environment job-details screenshot in `33-tasks-manual-dev-ecr-publish-workflow.md:36-39`, but the proof doc explicitly says those screenshots were not captured in `33-task-01-proofs.md:24-26` and `33-task-01-proofs.md:97-108`. | Verification | Capture sanitized screenshots from the remote GitHub Actions UI after the workflow is available on the default branch, or formally amend the task/spec proof requirements if repository-only evidence is intended to be sufficient. |
| HIGH | Missing required runtime log proof for Unit 2. The task list requires a sanitized GitHub Actions job log excerpt in `33-tasks-manual-dev-ecr-publish-workflow.md:53-56`, but `33-task-02-proofs.md:52-79` contains only static repository grep output and no executed workflow log evidence. | Verification | Run the workflow in GitHub from `main`, capture the sanitized log excerpt showing `./mvnw package -DskipTests` and OIDC credential setup, and add it to the proof doc. |
| HIGH | Missing required live publish and ECR verification proof for Unit 3. The task list requires completed workflow log/job-summary evidence and ECR inspection output in `33-tasks-manual-dev-ecr-publish-workflow.md:71-74`, but `33-task-03-proofs.md:24-25` and `33-task-03-proofs.md:80-91` explicitly defer both artifacts. | Functionality and verification | Execute one successful publish run from GitHub, commit sanitized workflow-visible image/digest evidence, and add sanitized `aws ecr describe-images` output for the expected SHA tag. |
| MEDIUM | Strict TDD is not auditable from git history. The repository standard requires RED → GREEN → REFACTOR, but the implementation, tests, docs, and proofs all landed together in commit `cb4d287`, so reviewers cannot verify failing-test-first progression from history alone. | Traceability | Preserve incremental commits for RED, GREEN, and REFACTOR in future spec work, or record explicit TDD checkpoints in the audit/proof artifacts before squashing. |

## 4) Evidence Appendix

### Git commits analyzed

- `cb4d287` `feat(ci): add manual dev ecr publish workflow`
  Files: workflow, Terraform publish role contract, documentation, proof artifacts, and focused contract tests for Spec 33

### File comparison results

- Relevant files listed in task plan: `15`
- Changed files in the implementation commit: `18`
- All changed core files map to the spec/task list.
- Four changed supporting proof docs were not listed in the Relevant Files table, but they are clearly linked to tasks 1-4 and are acceptable supporting changes under GATE D2.
- One listed relevant pattern-reference file remained unchanged: `src/test/java/org/springframework/samples/petclinic/system/TerraformApplyWorkflowDispatchContractTest.java`
- Unrelated worktree change observed during validation and not assessed as part of Spec 33: `docs/specs/README.md`

### Commands executed and results

```bash
git log --stat -10 -- docs/specs/33-spec-manual-dev-ecr-publish-workflow .
git show --name-only --format=fuller cb4d2879e6ee8a3158e36be37f3569143a3e737e
```

- Confirmed the implementation is represented by commit `cb4d287` and that the changed files align with the Spec 33 scope.

```bash
./mvnw -Dtest=ManualDevEcrPublishWorkflowDispatchContractTest,ManualDevEcrPublishWorkflowBuildAndAuthContractTest,ManualDevEcrPublishWorkflowPushContractTest,ManualDevEcrPublishWorkflowDocumentationContractTest,GitHubDeployRoleAndConfigurationContractTest test
```

- Exit `0`
- `13` tests run, `0` failures, `0` errors, `0` skipped

```bash
pre-commit run check-yaml --files .github/workflows/manual-dev-ecr-publish.yml
```

- Passed

```bash
pre-commit run markdownlint --files README.md infra/terraform/app/dev/README.md docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-spec-manual-dev-ecr-publish-workflow.md docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-tasks-manual-dev-ecr-publish-workflow.md docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-audit-manual-dev-ecr-publish-workflow.md
```

- Passed

```bash
rg -n "AKIA|ASIA|aws_secret_access_key|AWS_SECRET_ACCESS_KEY|BEGIN (RSA|OPENSSH|EC|DSA) PRIVATE KEY|ghp_|github_pat_|xox[baprs]-|token|password" docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-proofs README.md infra/terraform/app/dev/README.md .github/workflows/manual-dev-ecr-publish.yml
```

- No live credentials detected
- Matches were limited to expected `id-token: write` references and placeholder credentials in documentation

---

**Validation Completed:** 2026-05-20 09:35:37 CDT
**Validation Performed By:** GPT-5 Codex
