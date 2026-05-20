# Task 02 Proofs - Maven-first build and GitHub OIDC publish-role contract

## Task Summary

This task proves the workflow now packages the application before publication,
assumes AWS credentials through a dedicated `APP_PUBLISH_ROLE_ARN`, and keeps
the GitHub variable contract documented beside the dev Terraform stack.

## What This Task Proves

- The workflow runs `./mvnw package -DskipTests` before Docker build and push.
- The workflow requests minimal permissions and uses GitHub OIDC instead of
  long-lived AWS keys.
- Terraform defines a dedicated `app-publish-dev` role and exports
  `app_publish_role_arn`.
- The dev stack documentation names `APP_PUBLISH_ROLE_ARN`, `REPOSITORY_URI`,
  and `AWS_REGION` as the publish workflow contract.

## Evidence Summary

- The focused build/auth workflow contract test passed.
- The shared IAM/configuration contract test passed after the publish role and
  output were added.
- The workflow and Terraform docs now contain the expected role, variable, and
  build-step strings.

## Artifact: Build/auth and IAM contract tests

**What it proves:** The workflow and Terraform/readme files preserve the build
order, OIDC role assumption, and dedicated publish-role contract.

**Why it matters:** These tests keep future workflow edits from silently
reintroducing broad permissions or long-lived AWS credentials.

**Command:**

```bash
./mvnw -Dtest=ManualDevEcrPublishWorkflowBuildAndAuthContractTest,GitHubDeployRoleAndConfigurationContractTest test
```

**Result summary:** The focused workflow and IAM/configuration contract tests
passed with 6 assertions groups and no failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.ManualDevEcrPublishWorkflowBuildAndAuthContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.GitHubDeployRoleAndConfigurationContractTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Workflow build and auth lines

**What it proves:** The workflow contains the package step, protected variable
checks, OIDC credential setup, and ECR login steps.

**Why it matters:** Reviewers can audit the exact build/auth path without
executing the workflow.

**Command:**

```bash
rg -n "./mvnw package -DskipTests|APP_PUBLISH_ROLE_ARN|AWS_REGION|REPOSITORY_URI|configure-aws-credentials|amazon-ecr-login" .github/workflows/manual-dev-ecr-publish.yml infra/terraform/app/dev/README.md
```

**Result summary:** The workflow consumes the protected variable contract and
configures AWS credentials through `APP_PUBLISH_ROLE_ARN` before ECR login.

```text
.github/workflows/manual-dev-ecr-publish.yml:39:      AWS_REGION: ${{ vars.AWS_REGION }}
.github/workflows/manual-dev-ecr-publish.yml:40:      REPOSITORY_URI: ${{ vars.REPOSITORY_URI }}
.github/workflows/manual-dev-ecr-publish.yml:45:          if [[ -z "${{ vars.APP_PUBLISH_ROLE_ARN }}" ]]; then
.github/workflows/manual-dev-ecr-publish.yml:60:        run: ./mvnw package -DskipTests
.github/workflows/manual-dev-ecr-publish.yml:63:        uses: aws-actions/configure-aws-credentials@v4
.github/workflows/manual-dev-ecr-publish.yml:66:          role-to-assume: ${{ vars.APP_PUBLISH_ROLE_ARN }}
.github/workflows/manual-dev-ecr-publish.yml:69:        uses: aws-actions/amazon-ecr-login@v2
infra/terraform/app/dev/README.md:95:| `APP_PUBLISH_ROLE_ARN` | Environment-scoped (`dev`) | Protected value consumed by manual ECR publish workflows so image publication stays separate from Terraform and ECS rollout authority. |
infra/terraform/app/dev/README.md:97:| `REPOSITORY_URI` | Environment-scoped (`dev`) | Protected value that points the manual publish workflow at the Terraform-managed `repository_uri` output without reconstructing repository names in YAML. |
```

## Artifact: Terraform publish-role contract

**What it proves:** Terraform now creates a dedicated publish role instead of
reusing the deploy or Terraform roles.

**Why it matters:** The spec requires a narrower authority boundary for image
publication.

**Artifact path:** `infra/terraform/app/dev/main.tf`, `infra/terraform/app/dev/locals.tf`, `infra/terraform/app/dev/outputs.tf`

**Result summary:** The dev stack now includes `app_publish` OIDC trust,
`app-publish-dev` naming, and an `app_publish_role_arn` output.

## Reviewer Conclusion

Task 02 is implemented: the workflow is Maven-first, the AWS auth path is
GitHub OIDC with a dedicated publish role, and the GitHub variable ownership is
documented in-repository.
