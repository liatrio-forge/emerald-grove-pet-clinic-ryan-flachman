# Task 02 Proofs - OIDC authentication and reviewed saved-plan creation

## Task Summary

This task proves the workflow uses GitHub OIDC instead of long-lived AWS keys,
initializes Terraform with externalized backend inputs, and preserves the exact
reviewed plan as an artifact before any apply step can run.

## What This Task Proves

- The workflow requests only minimal GitHub Actions permissions.
- AWS access is obtained through OIDC role assumption.
- Backend configuration stays externalized instead of being hard-coded in
  reusable Terraform source.
- The plan job produces a saved `tfplan` artifact plus reviewer-friendly plan
  output.

## Evidence Summary

- The plan contract test passes with 2 assertions and no failures.
- The workflow YAML contains the expected permissions, OIDC action, backend file
  materialization, `terraform init`, `terraform plan`, and artifact upload.
- The workflow-level YAML quality check passes.

## Artifact: Plan contract test

**What it proves:** The workflow preserves the OIDC authentication and reviewed
plan contract automatically.

**Why it matters:** This is the main regression guard for the reviewed-plan
workflow contract.

Command:

```bash
./mvnw -Dtest=TerraformApplyWorkflowPlanContractTest test
```

**Result summary:** The targeted plan contract test passed with 2 assertions and
no failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformApplyWorkflowPlanContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: OIDC and saved-plan workflow lines

**What it proves:** The workflow file contains the exact permission, OIDC, init,
plan, and artifact-upload contract required by the spec.

**Why it matters:** A reviewer can verify the workflow’s trust and plan behavior
without reading every workflow step in full.

Command:

```bash
rg -n "permissions:|contents: read|id-token: write|configure-aws-credentials@v4|role-to-assume:|terraform -chdir=infra/terraform/app/dev init -input=false|-backend-config=\"\\$TF_BACKEND_CONFIG_FILE\"|terraform -chdir=infra/terraform/app/dev plan -out=tfplan -input=false|terraform-apply-dev-plan" .github/workflows/terraform-apply-dev.yml
```

**Result summary:** The workflow defines the minimal permission block, OIDC role
assumption, externalized backend configuration, saved-plan creation, and plan
artifact upload.

```text
10:permissions:
11:  contents: read
12:  id-token: write
60:        uses: aws-actions/configure-aws-credentials@v4
63:          role-to-assume: ${{ vars.TERRAFORM_APPLY_ROLE_ARN }}
76:        run: terraform -chdir=infra/terraform/app/dev init -input=false -backend-config="$TF_BACKEND_CONFIG_FILE"
80:          terraform -chdir=infra/terraform/app/dev plan -out=tfplan -input=false -no-color > terraform-plan.txt
86:          name: terraform-apply-dev-plan
```

## Artifact: Workflow YAML validation

**What it proves:** The workflow remains syntactically valid after the plan-job
changes.

**Why it matters:** A syntactically invalid workflow would break plan review in
GitHub even if the contract tests still matched strings locally.

Command:

```bash
pre-commit run check-yaml --files .github/workflows/terraform-apply-dev.yml
```

**Result summary:** The repository YAML validation hook passed for the workflow.

```text
check yaml...............................................................Passed
```

## Reviewer Conclusion

Task 02 is implemented: the workflow now uses GitHub OIDC, externalizes backend
inputs, and preserves the exact reviewed Terraform plan for later apply.
