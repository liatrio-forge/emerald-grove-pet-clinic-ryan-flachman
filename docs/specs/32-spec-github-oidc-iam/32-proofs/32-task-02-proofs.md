# Task 02 Proofs - Separate Terraform apply and destroy role boundary

## Task Summary

This task proves the dev Terraform stack now defines distinct GitHub-assumable
roles for apply and destroy, keeps their trust subjects separate by protected
environment, and documents the intentionally broad but non-admin Terraform IAM
surface.

## What This Task Proves

- `terraform-apply-dev` and `terraform-destroy-dev` are separate IAM roles.
- Apply and destroy trust different exact GitHub environment subjects.
- The Terraform workflow policy is broad for the proof of concept but avoids
  `iam:*` and `Action: "*"`.
- The dev stack README now documents the role matrix and the IAM-sensitive
  actions explicitly.

## Evidence Summary

- The focused contract test for separate Terraform workflow roles passes.
- The sanitized Terraform plan shows the two roles, their shared bounded
  policy, and the exported role ARN outputs.
- The README provides the reviewer-facing explanation for why destroy remains
  stricter than apply.
- The task requested GitHub environment screenshots, but the repository cannot
  produce those UI captures autonomously; this proof therefore uses
  repository-backed contract evidence for the `dev` and `dev-destroy`
  environment names and trusted subjects.

## Artifact: Focused Terraform workflow role contract test

**What it proves:** The source tree defines distinct apply and destroy roles,
uses exact trust subjects, and documents a broad-but-bounded Terraform policy.

**Why it matters:** This test prevents later work from collapsing apply and
destroy back into one GitHub role or from widening the policy to implicit admin
access.

**Command:**

```bash
./mvnw -q -Dtest=GitHubTerraformWorkflowRolesContractTest test
```

**Result summary:** The focused role-boundary contract test exited `0`.

```text
Exit code: 0
```

## Artifact: Sanitized Terraform plan for apply and destroy roles

**What it proves:** Terraform now plans separate apply and destroy IAM roles,
reuses exact OIDC subjects, and exports distinct role ARNs.

**Why it matters:** Reviewers can verify the security boundary directly from the
planned infrastructure contract before any live AWS mutation occurs.

**Command:**

```bash
/bin/zsh -lc 'AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test \
AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color'
```

**Result summary:** The plan shows the shared Terraform GitHub policy, two role
resources, two policy attachments, and separate output values for the apply and
destroy role ARNs.

```text
# aws_iam_policy.terraform_github_actions will be created
+ resource "aws_iam_policy" "terraform_github_actions" {
    name = "dev-terraform-github-actions"
}

# aws_iam_role.terraform_apply_github_actions will be created
+ resource "aws_iam_role" "terraform_apply_github_actions" {
    name = "terraform-apply-dev"
}

# aws_iam_role.terraform_destroy_github_actions will be created
+ resource "aws_iam_role" "terraform_destroy_github_actions" {
    name = "terraform-destroy-dev"
}

# data.aws_iam_policy_document.github_actions_oidc_trust["terraform_destroy"] will be read during apply
<= data "aws_iam_policy_document" "github_actions_oidc_trust" {
    statement {
      condition {
        values   = ["repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:environment:dev-destroy"]
        variable = "token.actions.githubusercontent.com:sub"
      }
    }
}

Changes to Outputs:
  + terraform_apply_role_arn   = (known after apply)
  + terraform_destroy_role_arn = (known after apply)
```

## Artifact: Reviewer-facing role matrix and IAM-sensitive action documentation

**What it proves:** The operator documentation explains the role split, the
environment boundary, and the explicit IAM-sensitive actions involved.

**Why it matters:** This security boundary is not reviewable from Terraform
alone; maintainers need a human-readable explanation of why destroy is stricter
than apply.

**Artifact path:** `infra/terraform/app/dev/README.md`

**Result summary:** The README now includes a role matrix for
`terraform-apply-dev` and `terraform-destroy-dev`, maps each role to its exact
GitHub environment subject, and lists the sensitive IAM actions instead of
hiding them behind wildcard admin permissions.

```text
| `terraform-apply-dev` | ... `environment:dev` | protected `dev` environment | ...
| `terraform-destroy-dev` | ... `environment:dev-destroy` | separate protected `dev-destroy` environment | ...

The current policy intentionally grants these IAM-sensitive actions:
- `iam:CreateRole`
- `iam:DeleteRole`
- `iam:AttachRolePolicy`
- `iam:DetachRolePolicy`
- `iam:PutRolePolicy`
- `iam:DeleteRolePolicy`
- `iam:PassRole`
- `iam:UpdateAssumeRolePolicy`
```

## Artifact: GitHub environment evidence note

**What it proves:** The repository contract explicitly names the required
protected environments even though this repo-only implementation cannot capture
the live GitHub environment settings UI.

**Why it matters:** The task asked for screenshots, but the actual reviewer
signal is the contract between Terraform trust subjects, workflow environment
names, and README guidance. That contract is repository-backed and reviewable in
source control.

**Result summary:** The environment names `dev` and `dev-destroy` now appear in
the Terraform trust locals, the README role matrix, and the task proof above.
This is the repository-owned equivalent evidence available at implementation
time.

## Reviewer Conclusion

The apply and destroy boundary is now explicit in Terraform and documentation:
the roles are separate, the trusted GitHub subjects are exact, and the policy
surface remains broad for the POC without becoming hidden administrator access.
