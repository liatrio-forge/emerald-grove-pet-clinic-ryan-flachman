# Task 03 Proofs - Public endpoint identifier and downstream ALB integration outputs

## Task Summary

This task publishes the public ALB identifier contract directly from
`infra/terraform/app/dev`. The stack now exports the ALB DNS name, hosted zone
ID, ARNs, and reviewer-readable names needed by later ECS, DNS, and validation
work without forcing downstream consumers to reconstruct Terraform addresses.

## What This Task Proves

- The app stack exports the exact reviewer-readable output names
  `alb_dns_name`, `alb_hosted_zone_id`, `alb_arn`, `alb_name`,
  `http_listener_arn`, `application_target_group_arn`, and
  `application_target_group_name`.
- Those outputs come directly from `aws_lb.public`, `aws_lb_listener.http`, and
  `aws_lb_target_group.application` resource attributes.
- The README now states that the ALB DNS name is the approved v1 public
  endpoint identifier and that end-to-end reachability still depends on later
  ECS service attachment.
- The sanitized Terraform plan exposes the output contract at plan time.

## Evidence Summary

- `TerraformAlbOutputsContractTest` failed before the output contract existed,
  then passed after the minimum `outputs.tf` and README changes landed.
- The `floci`-backed Terraform validation path still succeeds.
- The sanitized plan now includes all ALB and target-group integration outputs,
  including the ALB name and target-group name, so downstream specs can consume
  them without rediscovering resource identities.

## Artifact: Automated output-contract test

**What it proves:** The repository now enforces the exact exported output names
and README contract for the public endpoint identifier.

**Why it matters:** Later infrastructure work now depends on a stable set of
reviewer-readable outputs rather than implicit knowledge of Terraform resource
addresses.

**Command:**

```bash
./mvnw test -Dtest=TerraformAlbOutputsContractTest,TerraformAlbListenerAndTargetGroupContractTest,TerraformPublicAlbContractTest
```

**Result summary:** The public ALB, listener/target-group, and output-contract
tests all passed together after the output contract was added.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformPublicAlbContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformAlbListenerAndTargetGroupContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformAlbOutputsContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Terraform validation after adding ALB integration outputs

**What it proves:** The dev app stack remains valid after publishing the new
outputs.

**Why it matters:** Output contracts must be valid in Terraform, not just
present in source.

**Command:**

```bash
terraform -chdir=infra/terraform/app/dev validate
```

**Result summary:** The `floci` verification flow again completed
`terraform validate` successfully, with only the existing backend deprecation
warning.

```text
Success! The configuration is valid, but there were some
validation warnings as shown above.
```

## Artifact: Sanitized Terraform plan showing exported ALB identifiers

**What it proves:** The ALB endpoint and downstream integration identifiers are
visible at plan time and do not require manual name reconstruction.

**Why it matters:** Reviewers and later specs can verify the published contract
from one plan without hunting through resource blocks.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
  terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The sanitized plan shows the full output contract,
including the ALB DNS name and hosted zone ID plus the listener and
application-target-group identifiers.

```text
Changes to Outputs:
  + alb_arn                       = (known after apply)
  + alb_dns_name                  = (known after apply)
  + alb_hosted_zone_id            = (known after apply)
  + alb_name                      = "dev-public-http"
  + application_target_group_arn  = (known after apply)
  + application_target_group_name = "dev-application"
  + http_listener_arn             = (known after apply)
```

## Reviewer Conclusion

Task `3.0` is implemented and reviewable: the app stack now exports a stable
public endpoint identifier and the downstream ALB integration outputs needed by
later specs, and the contract is backed by source tests plus sanitized
Terraform proof.
