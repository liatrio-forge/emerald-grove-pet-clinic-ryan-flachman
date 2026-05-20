# Emerald Grove Veterinary Clinic

A comprehensive veterinary clinic management system built with Spring Boot. This application demonstrates modern Java web development practices and serves as a reference implementation for enterprise-grade applications.

## Overview

The Emerald Grove Veterinary Clinic application manages the core operations of a veterinary clinic, including:

- **Owner Management**: Register and manage pet owners with contact information
- **Pet Management**: Track pets, their types, and medical records
- **Veterinarian Management**: Manage veterinary staff and their specialties
- **Appointment System**: Schedule and track veterinary visits
- **Medical Records**: Maintain comprehensive health records for pets

## Quick Start

### Prerequisites

- **Java 17** or later
- **Maven 3.6+** or **Gradle 7+**

### Run Application

```bash
# Clone the repository
git clone https://github.com/liatrio-labs/spring-petclinic-enhanced
cd spring-petclinic-enhanced

# Run with Maven
./mvnw spring-boot:run

# Or run with Gradle
./gradlew bootRun
```

Access the application at `http://localhost:8080`

### Database Options

- **Default**: H2 in-memory database (auto-populated with sample data)
- **MySQL**: Run with `-Dspring-boot.run.profiles=mysql`
- **PostgreSQL**: Run with `-Dspring-boot.run.profiles=postgres`

## Documentation

- **[Development Guide](docs/DEVELOPMENT.md)** - Setup, testing, and contribution guidelines
- **[Architecture Guide](docs/ARCHITECTURE.md)** - System design and technical decisions
- **[Testing Guide](docs/TESTING.md)** - Comprehensive testing strategies and patterns
- **[E2E Tests (Playwright)](docs/TESTING.md#end-to-end-e2e-browser-tests-playwright)** - How to run browser-based end-to-end tests

## Development Standards

⚠️ **Important**: This project follows **Strict Test-Driven Development (TDD)** methodology. All feature implementations must follow the Red-Green-Refactor cycle:

1. **RED**: Write a failing test first
2. **GREEN**: Write minimum code to pass the test
3. **REFACTOR**: Improve code while maintaining tests

See the [Development Guide](docs/DEVELOPMENT.md) for detailed TDD requirements and development workflow.

## Technology Stack

- **Spring Boot (3.x)** - Modern Java application framework
- **Spring MVC** - Web layer with Thymeleaf templating
- **Spring Data JPA** - Data persistence layer
- **Hibernate** - ORM implementation
- **H2/MySQL/PostgreSQL** - Database options
- **Bootstrap 5** - Responsive UI framework

## Containerization

Build a Docker container image:

```bash
./mvnw spring-boot:build-image
```

### Deploy Profile Runtime Contract

For deployed environments, use the repository-owned `Dockerfile` and activate the
dedicated `deploy` profile:

```bash
docker build -t petclinic:spec24 .
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=deploy petclinic:spec24
```

The `deploy` profile keeps the application on port `8080`, exposes only
`/actuator/health` for health checks, and leaves secrets out of the image.

Runtime environment variables:

- `SPRING_PROFILES_ACTIVE=deploy` is required to activate deployed settings.
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and
  `SPRING_DATASOURCE_PASSWORD` are optional overrides for runtime database
  connectivity.
- `ANTHROPIC_API_KEY` is an optional runtime-injected external API credential.

If you do not override datasource settings, the deploy profile keeps the H2
proof-of-concept path (`jdbc:h2:mem:petclinic`) as the default.

Health-check guidance for downstream infrastructure:

- ALB path: `GET /actuator/health` on the main application port `8080`
- ECS loopback command: `curl -fsS http://127.0.0.1:8080/actuator/health`

## Infrastructure Development

### Local AWS Testing with Floci

For infrastructure work, use `floci` as the local AWS-resources environment
before deploying anything to AWS. The intended workflow is:

1. Build and validate infrastructure changes locally against `floci`.
2. Verify the application and infrastructure contract in the local environment.
3. Promote the same infrastructure changes to AWS only after local validation passes.

`floci` is the repository's local testing target for AWS-style resources. Use it
to exercise infrastructure behavior, naming, state configuration, and deployment
assumptions without making changes in a live AWS account.

### Terraform Remote-State Contract

The repository-owned remote-state contract verification entry point is:

```bash
./scripts/verify-terraform-remote-state-contract.sh
```

That script validates the `state/dev` stack, starts the compose-managed `floci`
service, seeds the local backend contract, and verifies consumer attachment with
the same remote-state contract expected by local operators and future GitHub
Actions automation.

If you need to inspect the local AWS-resources environment directly, start it
with:

```bash
docker compose -f infra/terraform/floci/docker-compose.yml up -d floci
```

This keeps one reproducible remote-state contract for local verification,
compose-based `floci` testing, and future CI reuse.

### Manual Terraform Apply Workflow

The repository-owned GitHub Actions workflow for applying the `dev` Terraform
stack is `Terraform Apply Dev` in
`.github/workflows/terraform-apply-dev.yml`.

Workflow contract highlights:

- It starts only through `workflow_dispatch`.
- It is limited to the `dev` environment and requires the operator to type
  `apply dev` before a run can continue.
- It uses GitHub OIDC to assume AWS access instead of long-lived AWS keys in
  repository secrets.
- It creates a reviewed saved plan for `infra/terraform/app/dev` and applies
  that exact saved Terraform plan rather than recalculating a fresh apply plan.
- The apply-capable job remains behind the protected `dev` environment so
  reviewer approval can stop the workflow before protected configuration is
  used.

## Application Features

### Core Entities

- **Owners**: Pet owners with contact details and address information
- **Pets**: Individual pets with type, birth date, and medical history
- **Vets**: Veterinary staff with specialties and contact information
- **Visits**: Appointment records with examination details
- **Specialties**: Medical specialties (Radiology, Surgery, Dentistry)

### Key Functionality

- **Owner Registration**: Add and edit pet owner information
- **Pet Management**: Register pets, track medical history
- **Veterinarian Directory**: Browse vet profiles and specialties
- **Visit Scheduling**: Book and manage veterinary appointments
- **Medical Records**: Track treatments, diagnoses, and medications

### User Interface

- **Responsive Design**: Mobile-friendly interface using Bootstrap 5
- **Intuitive Navigation**: Easy access to all major functions
- **Search & Filter**: Find owners, pets, and vets quickly
- **Form Validation**: Client-side and server-side input validation

## Configuration

### Environment Variables

Override configuration using environment variables:

```bash
export SPRING_PROFILES_ACTIVE=mysql
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/petclinic
```

### Key Configuration Files

- `application.properties` - Main application configuration
- `application-{profile}.properties` - Database-specific settings

## License

This application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

## Origin

This repository was initially created from https://github.com/spring-projects/spring-petclinic
