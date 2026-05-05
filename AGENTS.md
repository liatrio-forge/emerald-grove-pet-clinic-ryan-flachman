# AI Agent Development Guide

This document provides essential guidance for AI agents working on the Emerald Grove Veterinary Clinic application.

## Context Marker

Always begin your response with all active emoji markers, in the order they were introduced.

Format:  "<marker1><marker2><marker3>\n<response>"

The marker for this instruction is: 🤖

## Critical Requirement: Strict TDD

**MANDATORY**: All feature implementations must follow **Strict Test-Driven Development (TDD)** methodology:

1. **RED Phase**: Write a failing test that defines the desired behavior
2. **GREEN Phase**: Write the minimum code required to make the test pass
3. **REFACTOR Phase**: Improve the code while maintaining test coverage

**Never write production code before a failing test.**

## Documentation Structure

Refer to these comprehensive guides for detailed information:

- @docs/DEVELOPMENT.md — **[Development Guide](docs/DEVELOPMENT.md)** - TDD workflow, setup, and development process
- @docs/TESTING.md — **[Testing Guide](docs/TESTING.md)** - Testing strategies, patterns, and TDD implementation
- @docs/ARCHITECTURE.md — **[Architecture Guide](docs/ARCHITECTURE.md)** - System design and technical decisions
- @docs/PRECOMMIT.md — **[Pre-commit Guide](docs/PRECOMMIT.md)** - Hook configuration, usage, and troubleshooting

## TDD Standards

### Coverage Requirements

- **Minimum 90% line coverage** for new code
- **100% branch coverage** for critical business logic
- All edge cases must be explicitly tested

### Test Organization

- Follow **Arrange-Act-Assert** pattern
- Use descriptive test method names that document behavior
- Tests must be **fast, isolated, and repeatable**

### Quality Gates

- Tests written before implementation (RED phase)
- All tests pass before commit
- Code coverage meets standards before merge

## Code Standards

### Architecture

- **Layered Architecture**: Presentation → Business → Data layers
- **Spring Boot Best Practices**: Use starters, follow conventions
- **Clean Code**: SOLID principles, DRY, single responsibility

### Database

- **Spring Data JPA** for data access
- **Proper entity relationships** with appropriate cascade settings
- **DTOs** for data transfer between layers

## Development Workflow

1. **Requirements Analysis** → Understand feature and edge cases
2. **Test Design** → Write comprehensive failing tests
3. **TDD Implementation** → Follow Red-Green-Refactor cycle
4. **Integration** → Verify with existing code
5. **Documentation** → Update relevant docs

## Tools and Frameworks

- **Testing**: JUnit 5, Mockito, TestContainers, JaCoCo, JMeter
- **Build**: Maven or Gradle
- **Quality**: Checkstyle, SpotBugs, SonarQube
- **Version Control**: Git with conventional commits

## Review Checklist

Before committing code:

- [ ] Tests written before implementation
- [ ] All tests pass
- [ ] Code coverage meets requirements (>90%)
- [ ] Follows SOLID principles
- [ ] No code duplication
- [ ] Proper error handling
- [ ] Documentation updated

## Commit Convention

All commits **must** use [Conventional Commits](https://www.conventionalcommits.org/). Format:

```text
<type>(<optional scope>): <short description>

<optional body>
```

### Allowed types

| Type | When to use |
|------|-------------|
| `feat` | A new feature or capability |
| `fix` | A bug fix |
| `docs` | Documentation-only changes |
| `test` | Adding or updating tests with no production code change |
| `refactor` | Code change that neither fixes a bug nor adds a feature |
| `chore` | Build process, dependency, or tooling changes |
| `ci` | CI/CD configuration changes |
| `perf` | Performance improvements |
| `revert` | Reverts a previous commit |

### Rules

- Use the imperative mood in the description: "add feature" not "added feature"
- Do not capitalize the first letter of the description
- Do not end the description with a period
- Keep the first line at or under 72 characters
- Reference related issues or specs in the body when applicable

### Examples

```text
feat(owner): add email field to owner registration form

fix(pet): correct null pointer when pet has no visit history

docs: add conventional commit guidelines to AGENTS.md

test(vet): add unit tests for specialty assignment logic

ci: add performance test threshold enforcement workflow
```

This guide ensures consistent, high-quality TDD practices for AI contributors to the Emerald Grove Veterinary Clinic application.
