# Task 03 Proofs - Repo-owned bootstrap create and final foundation teardown workflows

## Task Summary

This task upgrades the bootstrap create workflow to manage all three Terraform
layers in order and adds a repo-owned final teardown workflow that destroys
`app/dev`, then `identity/dev`, then `state/dev` with bootstrap-secret-backed
credentials and reviewer-facing cleanup handoff output.

## What This Task Proves

- `bootstrap-dev-infra.yml` now bootstraps `state/dev` -> `identity/dev` ->
  `app/dev`.
- `bootstrap-destroy-dev-infra.yml` exists and is the repo-owned final teardown
  path for the dev foundation.
- Root and stack docs now distinguish normal `app/dev` rebuilds from final
  foundation teardown while preserving the standing `dev-bootstrap` secret
  exception.

## Evidence Summary

- The Task 3 workflow/doc contract slice passed with 7 tests and 0 failures.
- The create workflow now initializes and applies the identity stack before the
  app stack and reads GitHub role outputs from `identity/dev`.
- The destroy workflow is manual-only, `main`-only, `dev-bootstrap`-protected,
  ordered, and ends with explicit GitHub cleanup handoff guidance.

## Artifact: Bootstrap workflow contract tests

**What it proves:** The repository-owned create and destroy bootstrap workflows
match the required ordering, protection, and secret-handling contracts.

**Why it matters:** Parent Task 3 is primarily workflow orchestration, so the
contract tests are the clearest proof that the repo now encodes the intended
foundation lifecycle.

**Command:**

```bash
./mvnw test -Dtest=TerraformBootstrapWorkflowContractTest,TerraformBootstrapDestroyWorkflowContractTest,TerraformBootstrapWorkflowDocumentationContractTest
```

**Result summary:** The focused Task 3 slice passed with 7 tests and 0
failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformBootstrapWorkflowDocumentationContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformBootstrapWorkflowContractTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformBootstrapDestroyWorkflowContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Updated bootstrap create workflow

**What it proves:** The create workflow now includes the identity layer in the
foundation bootstrap order and promotes role outputs from the correct stack.

**Why it matters:** Without this change, the repo would still have a circular
dependency between app infrastructure and workflow identity.

**Artifact path:** `.github/workflows/bootstrap-dev-infra.yml`

**Result summary:** The workflow now materializes separate backend configs for
`identity/dev` and `app/dev`, applies `identity/dev` before `app/dev`, and
reads role outputs from the identity stack.

```text
terraform -chdir=infra/terraform/state/dev apply -auto-approve -input=false
terraform -chdir=infra/terraform/identity/dev init -input=false -backend-config=infra/terraform/identity/dev/backend.hcl
terraform -chdir=infra/terraform/identity/dev apply -auto-approve -input=false
terraform -chdir=infra/terraform/app/dev init -input=false -backend-config="$TF_BACKEND_CONFIG_FILE"
terraform -chdir=infra/terraform/identity/dev output -raw terraform_apply_role_arn
terraform -chdir=infra/terraform/identity/dev output -raw app_publish_role_arn
```

## Artifact: New bootstrap destroy workflow

**What it proves:** The repository now contains a dedicated final teardown path
for all dev foundation layers.

**Why it matters:** This is the workflow that closes the POC cleanly without
forcing operators to improvise destroy order or GitHub cleanup steps.

**Artifact path:** `.github/workflows/bootstrap-destroy-dev-infra.yml`

**Result summary:** The workflow is manual-only, `main`-only, protected by
`dev-bootstrap`, uses bootstrap secrets from the environment, destroys in the
required order, and emits a cleanup handoff summary.

```text
name: Bootstrap Destroy Dev Infrastructure
Type destroy bootstrap dev to confirm the final dev foundation teardown.
environment: dev-bootstrap
terraform -chdir=infra/terraform/app/dev destroy -auto-approve -input=false
terraform -chdir=infra/terraform/identity/dev destroy -auto-approve -input=false
terraform -chdir=infra/terraform/state/dev destroy -auto-approve -input=false
blank the AWS-derived GitHub variable values
dev-bootstrap secrets remain in place
```

## Artifact: Bootstrap and teardown documentation

**What it proves:** The docs now explain bootstrap create versus final teardown
responsibilities and keep the persistent bootstrap-secret exception explicit.

**Why it matters:** Reviewers and junior maintainers need this wording to know
which workflow to use for normal rebuilds versus final cleanup.

**Artifact paths:**

- `README.md`
- `infra/terraform/app/dev/README.md`

**Result summary:** The docs now call out the `Bootstrap Destroy Dev
Infrastructure` workflow, the ordered final teardown path, and the fact that
`dev-bootstrap` secrets persist by design.

```text
README.md: Bootstrap Destroy Dev Infrastructure
README.md: It destroys `app/dev` first, then `identity/dev`, then `state/dev`.
README.md: It ends with a cleanup handoff that tells the operator to blank the AWS-derived GitHub variable values...
infra/terraform/app/dev/README.md: use the bootstrap-destroy workflow for the full `app/dev` -> `identity/dev` -> `state/dev` teardown.
```

## Reviewer Conclusion

These artifacts show Task 3 is complete: the repository now owns both the
three-stack bootstrap create path and the final ordered teardown path, and the
supporting documentation explains when to use each workflow.
