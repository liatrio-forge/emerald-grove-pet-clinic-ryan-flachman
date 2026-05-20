# Task 03 Proofs - Immutable Docker publication and traceability output

## Task Summary

This task proves the workflow builds from the repository-owned root
`Dockerfile`, publishes to the protected `REPOSITORY_URI`, uses the full Git
SHA as the only image tag, and surfaces the final image reference plus digest
for reviewer inspection.

## What This Task Proves

- The workflow builds from the root `Dockerfile`.
- The workflow pushes only `$REPOSITORY_URI:$IMAGE_TAG` where `IMAGE_TAG` is
  `${{ github.sha }}`.
- The workflow emits `Published image:` and `Pushed digest:` output plus
  structured step outputs for later workflow consumers.
- The workflow does not include ECS rollout steps or mutable tag aliases.

## Evidence Summary

- The focused push contract test passed.
- The workflow file contains the root-Dockerfile build, SHA-tag push, digest
  lookup, and reviewer summary strings.
- Live GitHub Actions run logs and ECR inspection output are not yet captured in
  this sandbox and remain a follow-up artifact.

## Artifact: Push contract test

**What it proves:** The workflow preserves immutable publication, repository URI
reuse, digest visibility, and non-goal boundaries.

**Why it matters:** This is the regression guard that keeps later workflow edits
from adding mutable tags or rollout logic.

**Command:**

```bash
./mvnw -Dtest=ManualDevEcrPublishWorkflowPushContractTest test
```

**Result summary:** The focused push contract test passed with 3 assertions and
no failures.

```text
[INFO] Running org.springframework.samples.petclinic.system.ManualDevEcrPublishWorkflowPushContractTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Workflow traceability lines

**What it proves:** The YAML contains the SHA tag, immutable publish command,
digest lookup, and workflow summary output.

**Why it matters:** Reviewers can see the exact image-contract behavior without
opening external systems.

**Command:**

```bash
rg -n "IMAGE_TAG:|docker build --file Dockerfile|docker push|published_image=|image_digest=|Published image:|Pushed digest:|amazon-ecs-deploy-task-definition|aws ecs update-service" .github/workflows/manual-dev-ecr-publish.yml
```

**Result summary:** The workflow uses the Git SHA tag, writes image metadata to
`$GITHUB_OUTPUT`, writes reviewer summary lines, and contains no ECS rollout
commands.

```text
41:      IMAGE_TAG: ${{ github.sha }}
72:        run: docker build --file Dockerfile --tag "$REPOSITORY_URI:$IMAGE_TAG" .
75:        run: docker push "$REPOSITORY_URI:$IMAGE_TAG"
81:          echo "published_image=$REPOSITORY_URI:$IMAGE_TAG" >> "$GITHUB_OUTPUT"
82:          echo "image_digest=$image_digest" >> "$GITHUB_OUTPUT"
83:          echo "Published image: $published_image"
84:          echo "Pushed digest: $image_digest"
86:            echo "Published image: $published_image"
87:            echo "Pushed digest: $image_digest"
```

## Artifact: Live publish proof status

**What it proves:** The remaining gap is runtime capture, not repository
contract definition.

**Why it matters:** The spec requested GitHub run logs and ECR inspection
evidence, so validation should know why those artifacts are not yet committed.

**Result summary:** This sandbox cannot run the workflow in GitHub or query a
live AWS account, so the sanitized successful publish run and ECR inspection
output remain a follow-up artifact to capture after the workflow is available in
the remote repository.

## Reviewer Conclusion

Task 03 is implemented at the repository level: the workflow publishes one
immutable SHA-tagged image from the root `Dockerfile`, emits traceability
metadata, and stays out of ECS rollout scope. Live run and ECR evidence remain
an explicit follow-up artifact.
