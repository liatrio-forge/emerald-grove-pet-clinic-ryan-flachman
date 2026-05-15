# GitHub issue drafts for AWS ECS/Terraform POC

This directory contains copy/paste-ready issue drafts for the AWS ECS/Terraform deployment POC.

Files:

- `00-epic-aws-ecs-terraform-poc.md`: epic issue
- `01-16-*.md`: spec-sized child issues
- `create-gh-issues.sh`: helper script that creates the issues with `gh issue create`

Expected usage:

1. Re-authenticate GitHub CLI:
   - `gh auth login -h github.com`
2. Optionally verify repo access:
   - `gh repo view liatrio-forge/emerald-grove-pet-clinic-ryan-flachman`
3. Create issues:
   - `./.github/issue-drafts/create-gh-issues.sh liatrio-forge/emerald-grove-pet-clinic-ryan-flachman`

Notes:

- The script uses the first Markdown heading in each file as the issue title.
- The remainder of the file body becomes the issue description.
- Issues are created in filename order so the epic is created first.
