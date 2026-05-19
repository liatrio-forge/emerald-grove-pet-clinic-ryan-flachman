# 28-audit-ecr-repository-contract.md

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
| `AGENTS.md` | yes | Use `🤖`; follow strict TDD; use conventional commits | none |
| `README.md` | yes | Use `floci` before AWS; app-stack contracts are documented in repo docs; infra validation should be reproducible | none |
| `docs/DEVELOPMENT.md` | yes | Red-Green-Refactor required; tests precede implementation; `./mvnw test` is a standard verification command | none |
| `docs/TESTING.md` | yes | Prefer local infrastructure validation with `floci`; keep proof artifacts reproducible and sanitized; AWS is not the first feedback loop | none |
| `docs/PRECOMMIT.md` | yes | Markdown quality is enforced; pre-commit runs test checks; PR-based workflow is expected | none |
| `.pre-commit-config.yaml` | yes | Markdownlint runs automatically; Java changes trigger `./mvnw test`; direct commits to `main` are blocked | none |
| `pom.xml` | yes | Maven wrapper is a supported build path; Java 17 baseline is established; test tooling is available | none |
| `build.gradle` | yes | Gradle support exists without overriding the documented Maven or TDD workflow | none |
| `infra/terraform/app/dev/README.md` | yes | Backend settings stay external; `app/dev` is a consumer stack; reviewer-facing infra contract docs belong here | none |
| `infra/terraform/floci/README.md` | yes | `floci` is the compose-managed local AWS-resources environment; local validation should reuse documented entry points | none |
| `CONTRIBUTING.md` | not found | n/a | none |
| `.github/pull_request_template.md` | not found | n/a | none |
