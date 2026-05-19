# 30-audit-ecs-runtime-foundation.md

## Executive Summary

- Overall Status: PASS
- Required Gate Failures: 0
- Flagged Risks: 0

## Gate Overview

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
| `AGENTS.md` | yes | Strict TDD with failing tests first; minimum 90% coverage for new code; conventional commits required | none |
| `README.md` | yes | Use `floci` before AWS; deploy contract stays on port `8080`; infrastructure proof should be reproducible | none |
| `docs/PRECOMMIT.md` | yes | Pre-commit runs `./mvnw test`; markdown and shell quality gates apply; direct commits to `main` are blocked | none |
| `.pre-commit-config.yaml` | yes | Shell scripts need shebang or executable compliance; markdownlint runs on docs; Maven tests run on code changes | none |
| `CONTRIBUTING.md` | not found | n/a | none |
| `.github/pull_request_template.md` | not found | n/a | none |
