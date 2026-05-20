# Task 04 Proofs - Operator documentation and verification path

## Task Summary

This task proves the repository now documents the manual publish workflow,
variable ownership, verification commands, and explicit non-goals in both the
root README and the dev-stack Terraform README.

## What This Task Proves

- The repository documents `Manual Dev ECR Publish` and its `workflow_dispatch`
  safety model.
- The docs name `APP_PUBLISH_ROLE_ARN`, `REPOSITORY_URI`, and `AWS_REGION` as
  part of the workflow contract.
- The docs provide `gh workflow run`, `gh run view --log`, and
  `aws ecr describe-images` verification commands.
- The docs state that automatic publish, ECS rollout, mutable tags, and a
  repo-owned verification script are out of scope.

## Evidence Summary

- The focused documentation contract test passed.
- Grep output shows the workflow name, confirmation text, variable contract, and
  verification commands in the updated READMEs.
- Markdown lint passed for the touched documentation files.

## Artifact: Documentation contract test

**What it proves:** The repository documentation preserves the workflow
contract, variable ownership, verification path, and scope boundaries.

**Why it matters:** The workflow is manual by design, so maintainers need
in-repo instructions that are stable and reviewable.

**Command:**

```bash
./mvnw -Dtest=ManualDevEcrPublishWorkflowDocumentationContractTest test
```

**Result summary:** The focused documentation contract test passed with 2
assertions and no failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.ManualDevEcrPublishWorkflowDocumentationContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Documentation contract lines

**What it proves:** The updated READMEs contain the exact workflow name,
confirmation text, GitHub OIDC guidance, verification commands, and scope
boundaries.

**Why it matters:** Reviewers can confirm the operator experience from the repo
without relying on tribal knowledge.

**Command:**

```bash
rg -n "Manual Dev ECR Publish|publish dev image|workflow_dispatch|GitHub OIDC|APP_PUBLISH_ROLE_ARN|REPOSITORY_URI|gh workflow run|gh run view --log|aws ecr describe-images|automatic publish|ECS rollout|verification script" README.md infra/terraform/app/dev/README.md
```

**Result summary:** The root README now introduces the workflow, and the
app-dev README documents the variable contract, verification commands, and
scope boundaries.

```text
README.md:164:### Manual Dev ECR Publish Workflow
README.md:166:The repository-owned GitHub Actions workflow for publishing a `dev` container
README.md:172:- It starts only through `workflow_dispatch` and runs only from the `main`
README.md:174:- It requires the operator to type `publish dev image` before any AWS-backed
README.md:176:- It uses the protected `dev` environment plus GitHub OIDC instead of
infra/terraform/app/dev/README.md:95:| `APP_PUBLISH_ROLE_ARN` | Environment-scoped (`dev`) | Protected value consumed by manual ECR publish workflows so image publication stays separate from Terraform and ECS rollout authority. |
infra/terraform/app/dev/README.md:97:| `REPOSITORY_URI` | Environment-scoped (`dev`) | Protected value that points the manual publish workflow at the Terraform-managed `repository_uri` output without reconstructing repository names in YAML. |
infra/terraform/app/dev/README.md:145:## Manual Dev ECR Publish Workflow Contract
infra/terraform/app/dev/README.md:160:gh workflow run "Manual Dev ECR Publish" --ref main -f confirmation="publish dev image"
infra/terraform/app/dev/README.md:162:gh run view <run-id> --log
infra/terraform/app/dev/README.md:163:aws ecr describe-images --repository-name <repository-name> --image-ids imageTag=<full-git-sha>
infra/terraform/app/dev/README.md:169:If you already know the run id, `gh run view --log <run-id>` is the direct log
infra/terraform/app/dev/README.md:178:- Out of scope: automatic publish, ECS rollout, mutable convenience tags, and
infra/terraform/app/dev/README.md:179:  any repo-owned verification script for this workflow.
```

## Artifact: Markdown quality check

**What it proves:** The touched markdown files satisfy the repository’s
documentation lint gate.

**Why it matters:** Reviewable documentation is part of the feature contract,
not an afterthought.

**Command:**

```bash
pre-commit run markdownlint --files README.md infra/terraform/app/dev/README.md docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-spec-manual-dev-ecr-publish-workflow.md docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-tasks-manual-dev-ecr-publish-workflow.md docs/specs/33-spec-manual-dev-ecr-publish-workflow/33-audit-manual-dev-ecr-publish-workflow.md
```

**Result summary:** Markdown lint passed for the touched documentation files.

```text
markdownlint.............................................................Passed
```

## Reviewer Conclusion

Task 04 is implemented: the repository documents the manual publish workflow,
its variable contract, the expected verification commands, and the intended
scope boundaries in reviewer-facing README files.
