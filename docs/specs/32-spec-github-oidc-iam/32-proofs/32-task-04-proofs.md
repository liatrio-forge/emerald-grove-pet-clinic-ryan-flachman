# Task 04 Proofs - Reviewer-facing docs and reproducible IAM verification

## Task Summary

This task proves the repository now owns a repeatable verification path for the
GitHub OIDC IAM contract and documents exactly how maintainers should validate
the trust and role boundary locally without long-lived AWS keys.

## What This Task Proves

- A repository-owned verification script exists at
  `./scripts/verify-github-oidc-iam-contract.sh`.
- The verification script checks for required files, reuses
  `backend.hcl.example`, runs `terraform validate`, and runs a sanitized
  `terraform plan -no-color`.
- The app-dev and floci READMEs both document the same placeholder-credential
  verification sequence.
- The docs explicitly tell maintainers not to use long-lived AWS access keys
  for these GitHub OIDC workflows.

## Evidence Summary

- The focused documentation and verification contract test passes.
- The verification script exited `0` and produced both Terraform validation and
  sanitized plan output.
- The READMEs now point reviewers to the same script and placeholder
  credentials, which keeps the local verification path reproducible.

## Artifact: Focused documentation and verification contract test

**What it proves:** The script and README files contain the required
verification path, environment names, role names, and no-long-lived-key
guidance.

**Why it matters:** This prevents the proof workflow from becoming tribal
knowledge or drifting away from the Terraform contract.

**Command:**

```bash
./mvnw -q -Dtest=GitHubOidcIamDocumentationContractTest test
```

**Result summary:** The focused documentation contract test exited `0`.

```text
Exit code: 0
```

## Artifact: Repository-owned IAM verification script run

**What it proves:** Reviewers can validate the IAM contract locally through one
repo-owned command that materializes the backend stub, validates Terraform, and
captures a sanitized plan.

**Why it matters:** This is the reproducible proof path that later validation
work can rerun without reconstructing ad hoc Terraform commands.

**Command:**

```bash
./scripts/verify-github-oidc-iam-contract.sh
```

**Artifact path:** `scripts/verify-github-oidc-iam-contract.sh`

**Result summary:** The script initialized the backend against `floci`,
reported Terraform validation success, and printed a sanitized plan showing the
OIDC provider plus the apply, destroy, and deploy role outputs.

```text
Success! The configuration is valid, but there were some validation warnings as shown above.

Terraform will perform the following actions:
  # data.aws_iam_policy_document.github_actions_oidc_trust["app_deploy"] ...
  # data.aws_iam_policy_document.github_actions_oidc_trust["terraform_apply"] ...
  # data.aws_iam_policy_document.github_actions_oidc_trust["terraform_destroy"] ...

Changes to Outputs:
  + app_deploy_role_arn        = (known after apply)
  + terraform_apply_role_arn   = (known after apply)
  + terraform_destroy_role_arn = (known after apply)
```

## Artifact: App-dev README verification guidance

**What it proves:** The main operator-facing Terraform README documents the
verification command, placeholder credentials, role matrix, and GitHub variable
contract in one place.

**Why it matters:** Reviewers need a stable landing page for the IAM contract,
not just raw script contents.

**Artifact path:** `infra/terraform/app/dev/README.md`

**Result summary:** The README now names `terraform-apply-dev`,
`terraform-destroy-dev`, and `app-deploy-dev`, includes the GitHub variable
table, and tells maintainers to use GitHub OIDC instead of long-lived AWS
access keys.

## Artifact: Floci README verification guidance

**What it proves:** The local AWS-emulator README points maintainers to the
same verification script and placeholder credentials used by the proof path.

**Why it matters:** The verification flow depends on `floci`, so reviewers need
the emulator-specific README to agree with the app stack README.

**Artifact path:** `infra/terraform/floci/README.md`

**Result summary:** The floci README now documents
`./scripts/verify-github-oidc-iam-contract.sh`, the `terraform validate` and
`plan -no-color` sequence, and the explicit no-long-lived-key guidance.

## Reviewer Conclusion

The repository now has one reproducible, reviewer-facing IAM verification path:
the script runs end to end with sanitized credentials, and both READMEs explain
how to use it without depending on long-lived AWS secrets.
