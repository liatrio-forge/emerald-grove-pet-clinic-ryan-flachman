# 32-audit-GitHub-oidc-iam.md

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

## Standards Evidence Table

| Source File | Read | Standards Extracted | Conflicts |
| --- | --- | --- | --- |
| `AGENTS.md` | yes | Response marker `🤖`; strict TDD; conventional commits required | none |
| `README.md` | yes | Validate infra against `floci` first; keep manual apply `dev` scoped; use OIDC instead of long-lived AWS keys | none |
| `docs/DEVELOPMENT.md` | yes | Red-Green-Refactor required; tests before implementation; docs updated with feature work | none |
| `docs/TESTING.md` | yes | Prefer `floci` before AWS; keep infra tests reproducible; sanitize proof artifacts | none |
| `.pre-commit-config.yaml` | yes | `./mvnw test` is a commit gate; `markdownlint` applies; direct commits to `main` are blocked | none |
| `docs/PRECOMMIT.md` | yes | `shellcheck` and markdown hooks apply; PR-based workflow is enforced; hook bypass is exceptional | none |
| `infra/terraform/app/dev/README.md` | yes | Reuse externalized backend config; keep `infra/terraform/app/dev` as the stack target; preserve narrow operator contracts | none |
| `infra/terraform/floci/README.md` | yes | `floci` remains the local validation path; use placeholder credentials locally; keep local proof separate from live AWS evidence | none |
