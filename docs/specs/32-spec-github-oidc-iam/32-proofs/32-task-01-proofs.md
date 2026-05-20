# Task 01 Proofs - Shared GitHub OIDC trust baseline

## Task Summary

This task proves the dev Terraform stack now defines a single GitHub Actions
OIDC provider plus reusable trust-policy documents that require the AWS STS
audience and exact repository-bound GitHub environment subjects.

## What This Task Proves

- The stack defines one `aws_iam_openid_connect_provider` for
  `token.actions.githubusercontent.com`.
- Reusable trust documents lock GitHub-assumable roles to
  `aud = sts.amazonaws.com`.
- Trust subjects are exact environment subjects for this repository and do not
  allow repo-wide wildcard trust.
- The repository-wide Maven suite still passes after the new contract test and
  Terraform changes.

## Evidence Summary

- The focused contract test for the OIDC provider and exact subject rules
  passes.
- `terraform validate` succeeds when run with a temporary materialized backend
  configuration, which matches the repo's existing verification pattern for the
  partial backend stub.
- The sanitized Terraform plan shows the new OIDC provider plus exact `sub`
  conditions for `dev` and `dev-destroy`.
- The full Maven suite passes outside the sandbox, confirming no regression in
  the broader codebase.

## Artifact: Focused OIDC contract test

**What it proves:** The source tree contains one GitHub OIDC provider plus the
required exact `aud` and `sub` trust constraints.

**Why it matters:** This is the RED-to-GREEN contract test that prevents the
shared OIDC baseline from drifting in later role work.

**Command:**

```bash
./mvnw -q -Dtest=GitHubOidcTrustPolicyContractTest test
```

**Result summary:** The focused Maven test exited `0`, so the new OIDC contract
assertions are green.

```text
Exit code: 0
```

## Artifact: Terraform validate with materialized backend values

**What it proves:** The Terraform configuration remains syntactically valid once
the partial backend stub is materialized with the local floci backend values
used elsewhere in the repository.

**Why it matters:** The committed source intentionally keeps backend values out
of `main.tf`, so validation needs to follow the repository's backend-materialization
pattern instead of hardcoding state configuration.

**Command:**

```bash
/bin/zsh -lc 'backup=$(mktemp /private/tmp/github-oidc-main.XXXXXX) && \
cp infra/terraform/app/dev/main.tf "$backup" && \
perl -0pi -e "s/backend \"s3\" \{\}/backend \"s3\" {\n    bucket         = \"emerald-grove-pet-clinic-dev-terraform-state\"\n    key            = \"app\/dev\/terraform.tfstate\"\n    region         = \"us-east-1\"\n    dynamodb_table = \"emerald-grove-pet-clinic-dev-terraform-locks\"\n    encrypt        = true\n  }/" infra/terraform/app/dev/main.tf && \
terraform -chdir=infra/terraform/app/dev validate; \
rc=$?; cp "$backup" infra/terraform/app/dev/main.tf; rm -f "$backup"; exit $rc'
```

**Result summary:** Terraform reported the configuration as valid. The only
warning was the existing `dynamodb_table` deprecation from the backend example.

```text
Warning: Deprecated Parameter
The parameter "dynamodb_table" is deprecated. Use parameter "use_lockfile" instead.

Success! The configuration is valid, but there were some validation warnings as shown above.
```

## Artifact: Sanitized Terraform plan

**What it proves:** The plan includes the new GitHub OIDC provider and exact
subject-scoped trust documents for the repository's protected environments.

**Why it matters:** Reviewers can see the precise trust claims Terraform will
materialize before any live AWS apply is attempted.

**Command:**

```bash
/bin/zsh -lc 'AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test \
AWS_EC2_METADATA_DISABLED=true terraform -chdir=infra/terraform/app/dev plan -no-color'
```

**Result summary:** The plan shows one
`aws_iam_openid_connect_provider.github_actions` resource plus
`data.aws_iam_policy_document.github_actions_oidc_trust` entries with exact
subjects for `dev` and `dev-destroy`.

```text
# data.aws_iam_policy_document.github_actions_oidc_trust["terraform_apply"] will be read during apply
<= data "aws_iam_policy_document" "github_actions_oidc_trust" {
    statement {
      actions = ["sts:AssumeRoleWithWebIdentity"]
      condition {
        test     = "StringEquals"
        values   = ["repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:environment:dev"]
        variable = "token.actions.githubusercontent.com:sub"
      }
      condition {
        test     = "StringEquals"
        values   = ["sts.amazonaws.com"]
        variable = "token.actions.githubusercontent.com:aud"
      }
    }
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

# aws_iam_openid_connect_provider.github_actions will be created
+ resource "aws_iam_openid_connect_provider" "github_actions" {
    client_id_list  = ["sts.amazonaws.com"]
    thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1"]
    url             = "https://token.actions.githubusercontent.com"
}
```

## Artifact: Full Maven regression suite

**What it proves:** The broader application and infrastructure contract suite
still passes after introducing the new Terraform contract test and OIDC
baseline.

**Why it matters:** This task changes shared Terraform files and adds a new
system-contract test, so the parent-task gate must show repo-wide regression
coverage.

**Command:**

```bash
./mvnw test
```

**Result summary:** The full Maven suite passed outside the sandbox with all
`258` tests green after enabling Docker access for Testcontainers.

```text
Results:

Tests run: 258, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
Total time: 41.354 s
Finished at: 2026-05-20T06:08:51-05:00
```

## Reviewer Conclusion

The task is complete: the dev app stack now owns one shared GitHub OIDC
provider and reviewer-readable exact trust subjects, and both focused and
repo-wide verification passed with sanitized Terraform evidence.
