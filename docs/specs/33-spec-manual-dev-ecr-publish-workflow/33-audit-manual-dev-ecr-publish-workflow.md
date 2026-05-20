# 33-audit-manual-dev-ecr-publish-workflow.md

## Executive Summary

- Overall Status: PASS
- Required Gate Failures: 0
- Flagged Risks: 0

## Gateboard

| Gate | Status | Why it failed (<=10 words) | Exact fix target |
| --- | --- | --- | --- |
| Requirement-to-test traceability | PASS | n/a | n/a |
| Proof artifact verifiability | PASS | n/a | n/a |
| Repository standards consistency | PASS | n/a | n/a |
| Open question resolution | PASS | n/a | n/a |
| Regression-risk blind spots | PASS | n/a | n/a |
| Non-goal leakage | PASS | n/a | n/a |

## Standards Evidence Table (Required)

| Source File | Read | Standards Extracted | Conflicts |
| --- | --- | --- | --- |
| `AGENTS.md` | yes | Strict TDD is mandatory; keep coverage and tests strong; use conventional commits | none |
| `README.md` | yes | Maven-first runtime/build guidance exists; repo-owned Dockerfile and deploy contract are documented; manual GitHub workflow patterns are already documented | none |
| `CONTRIBUTING.md` | not found | none | none |
| `.github/pull_request_template.md` | not found | none | none |
| `.pre-commit-config.yaml` | yes | `./mvnw test` is a commit gate; markdown and YAML must stay valid; direct commits to `main` are blocked | none |
| `.github/workflows/e2e-tests.yml` | yes | Workflow style uses explicit setup steps and artifact uploads; artifacts are preserved with `upload-artifact@v4` | none |
