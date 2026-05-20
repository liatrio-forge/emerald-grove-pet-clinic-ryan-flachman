# Validation Report: Spec 31 Manual Terraform Apply

## 1) Executive Summary

- **Overall:** FAIL
  Gates tripped: **A**, **C**, **E**
- **Implementation Ready:** No. The workflow implementation is mostly present, but mandatory proof artifacts are incomplete and the recorded implementation order does not satisfy the repository's spec-first, strict-TDD workflow.
- **Key metrics:** 94% Requirements Verified (16/17), 63% Proof Artifacts Working (10/16), 15 files changed vs 12 files listed as relevant

## 2) Coverage Matrix

### Functional Requirements

| Requirement ID/Name | Status | Evidence |
| --- | --- | --- |
| U1-FR1 Manual-only workflow_dispatch | Verified | `.github/workflows/terraform-apply-dev.yml:3-8`; `TerraformApplyWorkflowDispatchContractTest`; commit `163fef5` |
| U1-FR2 Scope to `infra/terraform/app/dev` for `dev` only | Verified | `.github/workflows/terraform-apply-dev.yml:76,80,88-89,97,112,123`; `infra/terraform/app/dev/README.md:34-49` |
| U1-FR3 Apply allowed only from `main` | Verified | `.github/workflows/terraform-apply-dev.yml:22-26,95`; `TerraformApplyWorkflowDispatchContractTest` |
| U1-FR4 Typed confirmation for `dev` apply | Verified | `.github/workflows/terraform-apply-dev.yml:5-8,28-32,95`; `README.md:153-159` |
| U1-FR5 Environment approval gate before apply-capable access | Verified | `.github/workflows/terraform-apply-dev.yml:91-97`; `infra/terraform/app/dev/README.md:42-49` |
| U1-FR6 Document reviewer approval and preferred self-approval prevention | Failed | Spec requires self-approval prevention guidance at `31-spec-manual-terraform-apply.md:34`; no matching self-approval-prevention note was found in the workflow docs or proof artifacts. |
| U2-FR1 AWS auth via GitHub OIDC, not long-lived keys | Verified | `.github/workflows/terraform-apply-dev.yml:10-12,59-63,102-106`; `TerraformApplyWorkflowPlanContractTest`; `README.md:156-159` |
| U2-FR2 Minimal GitHub Actions permissions | Verified | `.github/workflows/terraform-apply-dev.yml:10-12`; `TerraformApplyWorkflowPlanContractTest` |
| U2-FR3 Terraform init against existing remote backend contract | Verified | `.github/workflows/terraform-apply-dev.yml:37-39,65-76`; `infra/terraform/app/dev/README.md:31-49` |
| U2-FR4 Automation-mode saved plan artifact | Verified | `.github/workflows/terraform-apply-dev.yml:78-89`; `TerraformApplyWorkflowPlanContractTest` |
| U2-FR5 Reviewed plan output available through logs and saved artifacts | Verified | `.github/workflows/terraform-apply-dev.yml:80-89`; `infra/terraform/app/dev/README.md:51-63` |
| U2-FR6 Backend/environment config externalized | Verified | `.github/workflows/terraform-apply-dev.yml:37-39,65-73`; `infra/terraform/app/dev/README.md:31-49` |
| U3-FR1 Apply exact previously saved plan artifact | Verified | `.github/workflows/terraform-apply-dev.yml:108-125`; `TerraformApplyWorkflowApplyContractTest` |
| U3-FR2 Preserve saved-plan working-directory context | Verified | `.github/workflows/terraform-apply-dev.yml:112,123-124`; spec guidance at `31-spec-manual-terraform-apply.md:98-99` matched by implementation |
| U3-FR3 Single concurrency boundary for `dev` applies | Verified | `.github/workflows/terraform-apply-dev.yml:14-16`; `TerraformApplyWorkflowApplyContractTest` |
| U3-FR4 Surface success/failure clearly in job status/logs | Verified | `.github/workflows/terraform-apply-dev.yml:114-126`; GitHub job success/failure status is inherent to step exits; failure messages are explicit |
| U3-FR5 Fail clearly for invalid confirmation, missing config/artifact, or non-zero apply | Verified | `.github/workflows/terraform-apply-dev.yml:22-32,41-54,114-126`; `TerraformApplyWorkflowApplyContractTest` |

### Repository Standards

| Standard Area | Status | Evidence & Compliance Notes |
| --- | --- | --- |
| Strict TDD workflow | Failed | Commit `163fef5` introduced the full workflow, including plan/apply logic, before plan/apply tests were added in `2280acb` and `069a78e`. This conflicts with `31-spec-manual-terraform-apply.md:86-90` and `AGENTS.md` strict TDD rules. |
| Documentation-first spec workflow | Failed | The implementation commits `163fef5`, `2280acb`, and `069a78e` predate the spec/task commit `84882c5`, which added `31-spec-manual-terraform-apply.md` and `31-tasks-manual-terraform-apply.md`. |
| Infrastructure alignment | Verified | Workflow targets only `infra/terraform/app/dev` and reuses backend conventions documented in `infra/terraform/app/dev/README.md:1-49`. |
| Conventional commits | Verified | Commits `163fef5`, `2280acb`, `069a78e`, and `84882c5` use valid conventional-commit prefixes and task references. |
| Proof artifact hygiene and reviewer usability | Failed | Proof docs are sanitized, but required screenshots, workflow log excerpts, and artifact metadata are absent for Units 1-3. See task expectations in `31-tasks-manual-terraform-apply.md:30-35,47-52,65-70`. |
| Quality gates | Failed | Targeted contract tests and lint checks passed, but `./mvnw test` exited 1 in this environment with 4 integration-test errors while starting application contexts. |

### Proof Artifacts

| Unit/Task | Proof Artifact | Status | Verification Result |
| --- | --- | --- | --- |
| Unit 1 | File: workflow YAML | Verified | `.github/workflows/terraform-apply-dev.yml` contains manual trigger, branch gate, typed confirmation, and `environment: dev`. |
| Unit 1 | Screenshot: manual-run form | Failed | No screenshot file or embedded image found; proof doc explicitly states screenshots were not captured (`31-proofs/31-task-01-proofs.md:23-25`). |
| Unit 1 | Screenshot: environment review gate | Failed | No screenshot file or embedded image found; proof doc explicitly states screenshots were not captured (`31-proofs/31-task-01-proofs.md:23-25`). |
| Unit 1 | Test: dispatch contract | Verified | `./mvnw -Dtest=TerraformApplyWorkflowDispatchContractTest test` passed on 2026-05-19. |
| Unit 2 | File: workflow YAML | Verified | `.github/workflows/terraform-apply-dev.yml:10-89` shows permissions, OIDC, backend config, init, plan, and upload-artifact. |
| Unit 2 | CLI: sanitized workflow log excerpt | Failed | Proof doc contains only repo-local test output and `rg` results, not a workflow log excerpt from a run (`31-proofs/31-task-02-proofs.md:18-95`). |
| Unit 2 | Artifact: saved plan and artifact metadata | Failed | No downloaded artifact listing, metadata capture, or embedded artifact proof was provided; proof doc cites YAML only (`31-proofs/31-task-02-proofs.md:48-95`). |
| Unit 2 | Test: plan contract | Verified | `./mvnw -Dtest=TerraformApplyWorkflowPlanContractTest test` passed on 2026-05-19. |
| Unit 3 | File: workflow YAML | Verified | `.github/workflows/terraform-apply-dev.yml:14-16,108-126` shows concurrency, artifact download, verification, and apply. |
| Unit 3 | CLI: sanitized apply-job log excerpt | Failed | Proof doc does not include a workflow apply log excerpt; it contains only test output, grep output, and a full-suite limitation note (`31-proofs/31-task-03-proofs.md:17-99`). |
| Unit 3 | Screenshot: completed workflow success/failure run | Failed | No screenshot file or embedded image found for either success or failed safety case. |
| Unit 3 | Test: apply contract | Verified | `./mvnw -Dtest=TerraformApplyWorkflowApplyContractTest test` passed on 2026-05-19. |
| Unit 4 | File: operator documentation | Verified | `README.md:145-162` and `infra/terraform/app/dev/README.md:34-71` document workflow contract, scope, OIDC, and verification commands. |
| Unit 4 | CLI: verification commands | Verified | `infra/terraform/app/dev/README.md:51-63` documents `gh run list`, `gh run view`, and `gh run download`. |
| Unit 4 | Diff: documentation captured in repo | Verified | Commit `84882c5` adds the README updates, spec/task bundle, and documentation proof file. |
| Unit 4 | Test: documentation contract | Verified | `./mvnw -Dtest=TerraformApplyWorkflowDocumentationContractTest test` passed on 2026-05-19. |

## 3) Validation Issues

| Severity | Issue | Impact | Recommendation |
| --- | --- | --- | --- |
| HIGH | Missing mandatory proof artifacts for Units 1-3. Task definitions require screenshots, workflow log excerpts, and artifact metadata at `31-tasks-manual-terraform-apply.md:32-35,49-52,67-70`, but the proof set contains no screenshots and no real GitHub Actions run evidence. Task 01 explicitly says screenshots were not captured at `31-proofs/31-task-01-proofs.md:23-25`. | Verification gate failure; reviewers cannot confirm operator-facing behavior or reviewed-plan preservation from real run evidence | Run the workflow in GitHub, capture sanitized screenshots of the manual form, environment approval gate, and success/failure run states, and add sanitized `gh run view` / `gh run download` evidence for the saved plan artifact. |
| HIGH | Repository workflow order violates strict TDD and spec-first planning. Commit `163fef5` introduced the complete workflow, including plan/apply behavior, before the plan/apply tests were committed in `2280acb` and `069a78e`. The spec and task files were only committed later in `84882c5`. | Repository compliance and traceability failure; the implementation history does not meet the required red-green-refactor and docs/spec-first workflow | Rework the history or document an approved exception. The compliant path is: spec/tasks first, failing test commit, minimal implementation commit, then proof/docs. |
| MEDIUM | Requirement gap: self-approval prevention guidance is missing. Spec requirement at `31-spec-manual-terraform-apply.md:34` says the environment configuration should document that self-approval prevention is preferred when repository settings permit it, but no such note appears in the workflow docs or proof artifacts. | Partial requirement coverage for Unit 1 | Add a short operator note in `infra/terraform/app/dev/README.md` describing the preferred environment setting to prevent self-approval when GitHub repository settings support it. |
| MEDIUM | Repository quality gate was not fully re-verified in this environment. `./mvnw test` exited 1 with 4 integration-test errors while loading application contexts; targeted workflow tests passed, but repo-wide green status is not demonstrated here. | Full verification is incomplete in this sandbox | Re-run `./mvnw test` in an environment that allows the integration tests to bind sockets, and attach the passing result or an approved known-failure note if this environment limitation is expected. |

## 4) Evidence Appendix

### Git commits analyzed

- `163fef5` `feat(ci): add manual terraform apply entry workflow`
  Changed: `.github/workflows/terraform-apply-dev.yml`, `31-task-01-proofs.md`, `TerraformApplyWorkflowDispatchContractTest.java`
- `2280acb` `test(ci): add terraform apply plan contract coverage`
  Changed: `31-task-02-proofs.md`, `TerraformApplyWorkflowPlanContractTest.java`
- `069a78e` `test(ci): add terraform apply execution contract coverage`
  Changed: `31-task-03-proofs.md`, `TerraformApplyWorkflowApplyContractTest.java`
- `84882c5` `docs(terraform): document manual terraform apply workflow`
  Changed: `README.md`, `infra/terraform/app/dev/README.md`, spec/tasks/audit/questions bundle, `31-task-04-proofs.md`, `TerraformApplyWorkflowDocumentationContractTest.java`

### Changed-file comparison

- Changed since the first implementation commit: 15 files
- Listed as relevant in the task file: 12 files
- Additional changed supporting files with clear linkage: `docs/specs/31-spec-manual-terraform-apply/31-proofs/31-task-01-proofs.md`, `31-task-02-proofs.md`, `31-task-03-proofs.md`, `31-task-04-proofs.md`, `31-questions-1-manual-terraform-apply.md`
- Listed relevant files left unchanged but acceptable: `infra/terraform/app/dev/backend.hcl.example`, `infra/terraform/app/dev/versions.tf`

### Commands executed and results

```bash
git log --stat -10 --oneline
```

Confirmed the four implementation commits for this feature and their changed files.

```bash
./mvnw -Dtest=TerraformApplyWorkflowDispatchContractTest test
./mvnw -Dtest=TerraformApplyWorkflowPlanContractTest test
./mvnw -Dtest=TerraformApplyWorkflowApplyContractTest test
./mvnw -Dtest=TerraformApplyWorkflowDocumentationContractTest test
```

All four targeted contract tests passed on 2026-05-19.

```bash
pre-commit run check-yaml --files .github/workflows/terraform-apply-dev.yml
pre-commit run markdownlint --files README.md infra/terraform/app/dev/README.md docs/specs/31-spec-manual-terraform-apply/31-tasks-manual-terraform-apply.md docs/specs/31-spec-manual-terraform-apply/31-audit-manual-terraform-apply.md
```

Both checks passed.

```bash
./mvnw test
```

Failed in this environment with:

```text
[ERROR] Tests run: 256, Failures: 0, Errors: 4, Skipped: 5
[ERROR] Failed to load ApplicationContext for PetClinicIntegrationTests
[ERROR] Failed to load ApplicationContext for CrashControllerIntegrationTests
```

```bash
find docs/specs/31-spec-manual-terraform-apply -maxdepth 3 -type f | sort
rg -n "self-approval|self approval|prevent self-review|self-review" README.md infra/terraform/app/dev/README.md docs/specs/31-spec-manual-terraform-apply
```

Confirmed the validation/proof directory contains no screenshot assets and no self-approval-prevention guidance.

---

**Validation Completed:** 2026-05-19 21:37:42 CDT
**Validation Performed By:** GPT-5 Codex
