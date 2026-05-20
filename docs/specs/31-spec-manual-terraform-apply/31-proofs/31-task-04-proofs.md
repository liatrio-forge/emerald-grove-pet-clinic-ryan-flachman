# Task 04 Proofs - Operator documentation and verification guidance

## Task Summary

This task proves the repository now documents the manual apply workflow in both
the root infrastructure guidance and the `infra/terraform/app/dev` operator
contract, including OIDC usage, reviewed-plan rules, scope boundaries, and
review commands.

## What This Task Proves

- The workflow contract is documented in-repository.
- Operators are told to use GitHub OIDC and the existing backend contract.
- Review commands are documented for post-run verification.
- Scope boundaries explicitly exclude image build, ECS rollout, and destroy
  behavior from this workflow.

## Evidence Summary

- The documentation contract test passes with 2 assertions and no failures.
- The root README names `Terraform Apply Dev` and explains the reviewed-plan and
  GitHub OIDC behavior.
- The stack README documents the `dev`-only scope, `main` branch rule, reviewer
  approval, `backend.hcl.example` reuse, `gh run view`, and non-goals.

## Artifact: Documentation contract test

**What it proves:** The operator-facing documentation requirements are enforced
by automated regression tests.

**Why it matters:** This prevents the workflow contract from drifting away from
the repository’s written guidance.

Command:

```bash
./mvnw -Dtest=TerraformApplyWorkflowDocumentationContractTest test
```

**Result summary:** The documentation contract test passed with 2 assertions and
no failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformApplyWorkflowDocumentationContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Documentation contract lines

**What it proves:** The root and stack-specific READMEs now carry the key
workflow contract and scope statements.

**Why it matters:** Reviewers and future operators can verify the documented
workflow behavior quickly without reading the entire spec bundle.

Command:

```bash
rg -n "Terraform Apply Dev|workflow_dispatch|GitHub OIDC|main branch|reviewer approval|exact saved Terraform plan|dev environment only|backend.hcl.example|image build|ECS rollout|destroy workflow|gh run view|verification commands" README.md infra/terraform/app/dev/README.md
```

**Result summary:** The repository now documents the workflow name, OIDC model,
review commands, backend reuse, and explicit out-of-scope items.

```text
README.md:148:stack is `Terraform Apply Dev` in
README.md:153:- It starts only through `workflow_dispatch`.
README.md:156:- It uses GitHub OIDC to assume AWS access instead of long-lived AWS keys in
README.md:159:  that exact saved Terraform plan rather than recalculating a fresh apply plan.
infra/terraform/app/dev/README.md:36:- The repository defines one manual workflow named `Terraform Apply Dev` at
infra/terraform/app/dev/README.md:38:- The workflow is `dev environment only` and allows apply execution only from
infra/terraform/app/dev/README.md:39:  the `main branch`.
infra/terraform/app/dev/README.md:42:- The apply-capable job uses the protected `dev` environment so reviewer approval
infra/terraform/app/dev/README.md:44:- The workflow uses GitHub OIDC with an assumed AWS role instead of long-lived
infra/terraform/app/dev/README.md:47:  configuration that follows `backend.hcl.example`.
infra/terraform/app/dev/README.md:48:- The workflow creates and applies an `exact saved Terraform plan` for
infra/terraform/app/dev/README.md:57:gh run view <run-id> --log
infra/terraform/app/dev/README.md:70:- Out of scope: image build, ECS rollout, destroy workflow, broader deployment
```

## Artifact: Markdown quality check

**What it proves:** The documentation changes satisfy the repository Markdown
lint policy.

**Why it matters:** The docs need to be readable and repo-compliant, not just
present.

Command:

```bash
pre-commit run markdownlint --files README.md infra/terraform/app/dev/README.md docs/specs/31-spec-manual-terraform-apply/31-tasks-manual-terraform-apply.md docs/specs/31-spec-manual-terraform-apply/31-audit-manual-terraform-apply.md
```

**Result summary:** The repository Markdown lint hook passed for the updated
documentation files.

```text
markdownlint.............................................................Passed
```

## Reviewer Conclusion

Task 04 is implemented: the repository now documents how to run, review, and
scope the manual `dev` Terraform apply workflow in operator-facing guidance.
