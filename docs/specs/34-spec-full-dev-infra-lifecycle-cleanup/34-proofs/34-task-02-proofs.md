# Task 02 Proofs - Independent OIDC app destroy and rebuild restored

## Task Summary

This task adds a repository-owned `Terraform Destroy Dev` workflow for
`app/dev`, keeps that destroy path separate from foundation teardown, and
documents the exact normal destroy-and-recreate sequence that reuses the
existing GitHub OIDC variable contract.

## What This Task Proves

- The repository now contains a manual `terraform-destroy-dev.yml` workflow for
  `app/dev`.
- The destroy workflow uses the protected `dev-destroy` environment and the
  stable `TERRAFORM_DESTROY_ROLE_ARN`, `TF_STATE_BUCKET`, and `TF_LOCK_TABLE`
  variables.
- Documentation now distinguishes normal `app/dev` teardown from final
  `state/dev` / `identity/dev` teardown.

## Evidence Summary

- The Task 2 workflow and documentation contract tests pass.
- The destroy workflow materializes backend configuration, initializes
  `infra/terraform/app/dev`, and destroys only `app/dev`.
- Root and app-stack docs now tell reviewers that normal rebuilds leave backend
  and identity resources intact.

## Artifact: Destroy workflow and lifecycle separation tests

**What it proves:** The new destroy workflow exists and the repo docs describe
it as separate from final foundation teardown.

**Why it matters:** Task 2 is about restoring routine `app/dev` rebuilds
without reintroducing bootstrap credentials or tearing down foundational
stacks.

**Command:**

```bash
./mvnw test -Dtest=TerraformDestroyDevWorkflowContractTest,TerraformLifecycleSeparationDocumentationContractTest,TerraformApplyWorkflowDocumentationContractTest
```

**Result summary:** The focused Task 2 slice passed with 5 tests and 0
failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformApplyWorkflowDocumentationContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformDestroyDevWorkflowContractTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformLifecycleSeparationDocumentationContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Terraform Destroy Dev workflow

**What it proves:** The repository owns a normal OIDC-only app destroy path for
`app/dev`.

**Why it matters:** Operators can now tear down runtime infrastructure without
touching the backend or identity stacks that normal rebuilds depend on.

**Artifact path:** `.github/workflows/terraform-destroy-dev.yml`

**Result summary:** The workflow is manual-only, `main`-only, gated by typed
confirmation, uses `dev-destroy`, assumes `TERRAFORM_DESTROY_ROLE_ARN`, and
does not reference `state/dev` or `identity/dev` for destruction.

```text
name: Terraform Destroy Dev
workflow_dispatch:
Type destroy dev to confirm the dev Terraform destroy.
environment: dev-destroy
role-to-assume: ${{ vars.TERRAFORM_DESTROY_ROLE_ARN }}
terraform -chdir=infra/terraform/app/dev init -input=false -backend-config="$TF_BACKEND_CONFIG_FILE"
terraform -chdir=infra/terraform/app/dev destroy -auto-approve -input=false
```

## Artifact: Normal rebuild documentation boundary

**What it proves:** The docs now separate everyday `app/dev` rebuilds from the
final multi-stack teardown path.

**Why it matters:** This is the main reviewer-facing guardrail against
accidentally treating a normal runtime rebuild as a final cleanup action.

**Artifact paths:**

- `README.md`
- `infra/terraform/app/dev/README.md`

**Result summary:** The docs explicitly say normal application rebuilds operate
on `app/dev` only, final cleanup destroys `app/dev` first then foundation
stacks, and the `Terraform Destroy Dev` workflow leaves backend and identity
resources intact.

```text
README.md: Normal application rebuilds should operate on `app/dev` only.
README.md: Final cleanup destroys `app/dev` first, then `identity/dev`, then `state/dev`.
README.md: The repository-owned `Terraform Destroy Dev` workflow handles normal `app/dev` teardown...
infra/terraform/app/dev/README.md: This workflow destroys only `app/dev` runtime infrastructure and keeps backend and identity resources intact for later rebuilds.
infra/terraform/app/dev/README.md: Normal `app/dev` destroy and recreate is distinct from final foundation teardown.
```

## Reviewer Conclusion

These artifacts show Task 2 is in place: `app/dev` now has its own repo-owned
OIDC destroy workflow, and the repository docs clearly separate routine runtime
rebuilds from final foundation teardown.
