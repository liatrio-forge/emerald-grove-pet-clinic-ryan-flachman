# 31-audit-manual-terraform-apply.md

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
| `README.md` | yes | Use `floci` before AWS; rely on repo-owned verification entry points; keep infrastructure changes narrow and sanitized | none |
| `docs/DEVELOPMENT.md` | yes | Red-Green-Refactor required; tests pass before commit; update docs with feature work | none |
| `docs/TESTING.md` | yes | Prefer local infra validation first; keep tests reproducible; avoid live AWS as first feedback loop | none |
| `.pre-commit-config.yaml` | yes | YAML must validate; Markdown is linted; direct commits to `main` are blocked | none |
| `docs/PRECOMMIT.md` | yes | `markdownlint`, `shellcheck`, and commit hooks apply; PR-based workflow is enforced | none |
| `infra/terraform/app/dev/README.md` | yes | Reuse externalized backend config; keep `infra/terraform/app/dev` as the stack target; preserve narrow operator contracts | none |
| `infra/terraform/floci/README.md` | yes | `floci` remains the local validation path; use placeholder credentials locally; separate local proof from later AWS evidence | none |
