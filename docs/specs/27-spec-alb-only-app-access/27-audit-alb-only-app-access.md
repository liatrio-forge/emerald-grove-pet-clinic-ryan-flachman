# 27-audit-alb-only-app-access.md

## Executive Summary

- Overall Status: PASS
- Required Gate Failures: 0
- Flagged Risks: 0

## Gateboard

| Gate | Status | Why it failed (<=10 words) | Exact fix target |
| --- | --- | --- | --- |
| Requirement-to-test traceability | PASS | - | - |
| Proof artifact verifiability | PASS | - | - |
| Repository standards consistency | PASS | - | - |
| Open question resolution | PASS | - | - |
| Regression-risk blind spots | PASS | - | - |
| Non-goal leakage | PASS | - | - |

## Standards Evidence Table

| Source File | Read | Standards Extracted | Conflicts |
| --- | --- | --- | --- |
| `AGENTS.md` | yes | Responses use `🤖`; strict TDD is mandatory; conventional commits required | none |
| `README.md` | yes | Deploy/runtime app port is `8080`; infrastructure validation should use `floci`; remote-state verification has a repository-owned script entry point | none |
| `docs/DEVELOPMENT.md` | yes | Red-Green-Refactor is required; tests precede implementation; `./mvnw test` is the baseline repo verification command | none |
| `docs/TESTING.md` | yes | Prefer local infrastructure validation with `floci`; proof artifacts must stay reproducible and sanitized; AWS is not the first feedback loop | none |
| `docs/PRECOMMIT.md` | yes | Markdown quality is enforced; pre-commit runs tests; PR-based workflow is expected | none |
| `.pre-commit-config.yaml` | yes | Markdown lint runs automatically; Java changes trigger `./mvnw test`; direct commits to `main` are blocked | none |
| `pom.xml` | yes | Java 17 baseline; Maven wrapper is a supported build path; formatting and Checkstyle are enforced | none |
| `build.gradle` | yes | Gradle support exists without overriding the documented Maven or TDD workflow | none |
| `infra/terraform/app/dev/README.md` | yes | `app/dev` is a remote-state consumer only; backend settings stay external; local init reuses `floci` guidance | none |
| `infra/terraform/floci/README.md` | yes | `floci` is the compose-managed local AWS-resources environment; Terraform consumer init path is documented | none |
| `CONTRIBUTING.md` | not found | n/a | none |
| `.github/pull_request_template.md` | not found | n/a | none |
