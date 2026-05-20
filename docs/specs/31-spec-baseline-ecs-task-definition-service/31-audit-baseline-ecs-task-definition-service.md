# 31-audit-baseline-ecs-task-definition-service.md

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
| `README.md` | yes | Use `floci` before AWS; deploy contract stays on port `8080`; infrastructure proofs should be reproducible and sanitized | none |
| `docs/DEVELOPMENT.md` | yes | Red-Green-Refactor is required; all tests pass before commit; documentation updates are part of delivery | none |
| `docs/TESTING.md` | yes | Prefer `floci` before AWS; infrastructure validation must stay reproducible; proof artifacts must be sanitized | none |
| `docs/PRECOMMIT.md` | yes | `./mvnw test` is a pre-commit gate; markdown and shell quality checks apply; direct commits to `main` are blocked | none |
| `.pre-commit-config.yaml` | yes | `markdownlint`, `shellcheck`, and Maven test hooks apply; large-file and formatting hooks are enforced | none |
| `pom.xml` | yes | Java 17 baseline; Spring format validates in Maven; Checkstyle runs during validation | none |
| `CONTRIBUTING.md` | not found | n/a | none |
| `.github/pull_request_template.md` | not found | n/a | none |
