# Spec 10: Add GitHub OIDC IAM trust and permissions for workflows

## Summary

Define the AWS IAM trust and permission model used by GitHub Actions for Terraform and ECS deployment.

## Problem statement

The workflows will interact with AWS. Using GitHub OIDC avoids long-lived keys, but the trust relationship and permissions must be designed carefully. This is separate from workflow YAML because the security model should be reviewable on its own.

## In scope

- GitHub OIDC trust model
- IAM role design
- permission boundaries for:
  - Terraform apply
  - Terraform destroy
  - app deploy
- repo/branch/workflow restrictions in trust conditions
- required GitHub variables/secrets/environment metadata

## Out of scope

- workflow YAML implementation details except where needed to define permissions
- application runtime IAM task role

## Decisions already made

- Authentication model is GitHub OIDC
- Apply and deploy are separate workflows
- Destroy is separate and manual

## Deliverables

- IAM trust policy design
- Role separation strategy
- Least-privilege direction for workflows

## Acceptance criteria

- Workflows can assume AWS roles through OIDC
- No long-lived AWS access keys are required in GitHub secrets
- Permissions are sufficiently scoped for each workflow responsibility
- OIDC trust policies use explicit `StringEquals` claim matching for exact-match constraints
- OIDC trust policies require `aud` to equal `sts.amazonaws.com`
- OIDC trust policies tightly scope `sub` to the minimum allowed GitHub subject, using patterns such as `repo:ORG/REPO:ref:refs/heads/BRANCH-NAME` or `repo:ORG/REPO:environment:ENV-NAME`
- The spec defines minimum scoping rules for allowed repositories, branches, and environments so trust is not granted to broad repo-wide or org-wide subjects

## Dependencies

- None, but this unblocks workflow implementation

## Implementation notes

- The spec should decide whether a single broad role is acceptable for the POC or whether apply/destroy/deploy should use separate roles
- Branch and workflow restrictions should be explicit if supported
- Trust-policy examples should show exact `StringEquals` conditions for `aud` and the allowed `sub` values or patterns chosen for each workflow

## Risks and open questions

- Over-broad IAM permissions in the name of speed may create bad precedent for later iterations

## Suggested labels

- `spec`
- `aws`
- `iam`
- `github-actions`
- `security`
