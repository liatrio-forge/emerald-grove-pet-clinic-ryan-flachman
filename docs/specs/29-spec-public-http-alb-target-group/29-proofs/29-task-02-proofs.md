# Task 02 Proofs - HTTP listener and ECS-compatible target-group health contract

## Task Summary

This task makes the public ALB routing contract explicit in
`infra/terraform/app/dev`. The stack now defines one HTTP listener on port
`80`, forwards by default to one ECS-compatible application target group, and
pins the v1 health-check contract to `/actuator/health` on `traffic-port` with
reviewer-readable thresholds.

## What This Task Proves

- The dev app stack defines one `aws_lb_listener.http` on port `80` with a
  default forward action to `aws_lb_target_group.application`.
- The target group uses `target_type = "ip"` and application port `8080`,
  matching ECS `awsvpc` expectations and the existing Spring Boot port
  contract.
- The target-group health-check contract is explicit: path
  `/actuator/health`, port `traffic-port`, matcher `200-299`, interval `15`,
  timeout `5`, healthy threshold `2`, and unhealthy threshold `3`.
- The app-stack README now explains the v1 readiness signal and explicitly
  keeps ECS startup grace out of scope.

## Evidence Summary

- `TerraformAlbListenerAndTargetGroupContractTest` failed before the listener
  and target-group resources existed, then passed after the minimum Terraform
  changes were added.
- The `floci`-backed Terraform verification flow still reports
  `Success! The configuration is valid`.
- The sanitized Terraform plan now shows both `aws_lb_listener.http` and
  `aws_lb_target_group.application`, including the default forward action and
  all explicit health-check settings.

## Artifact: Automated listener and target-group contract test

**What it proves:** The repository now guards the HTTP listener and ECS-ready
target-group contract directly at test time.

**Why it matters:** This prevents later Terraform edits from silently changing
the forwarding path or health-check model that downstream ECS work depends on.

**Command:**

```bash
./mvnw test -Dtest=TerraformAlbListenerAndTargetGroupContractTest,TerraformPublicAlbContractTest
```

**Result summary:** Both the existing public ALB contract test and the new
listener-and-target-group contract test passed together after the Terraform and
README changes landed.

```text
[INFO] Running org.springframework.samples.petclinic.system.TerraformPublicAlbContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running org.springframework.samples.petclinic.system.TerraformAlbListenerAndTargetGroupContractTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Artifact: Terraform validation after adding listener and target group

**What it proves:** The dev app stack remains valid after adding the listener
and target-group resources.

**Why it matters:** The routing contract must be both explicit in source and
valid for Terraform before any AWS-facing use.

**Command:**

```bash
terraform -chdir=infra/terraform/app/dev validate
```

**Result summary:** The `floci` workflow completed `terraform validate`
successfully. Terraform emitted the same repository-known backend deprecation
warning, but the configuration validated cleanly.

```text
Success! The configuration is valid, but there were some
validation warnings as shown above.
```

## Artifact: Sanitized Terraform plan showing forwarding and health settings

**What it proves:** The listener-to-target-group wiring and explicit health
checks are present in the actual Terraform plan, not only in source text.

**Why it matters:** Reviewers can confirm the routing behavior and health model
without reconstructing intent from several Terraform files.

**Command:**

```bash
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_EC2_METADATA_DISABLED=true \
  terraform -chdir=infra/terraform/app/dev plan -no-color
```

**Result summary:** The sanitized plan shows one HTTP listener on port `80`,
one forward action to the application target group, and all required explicit
health-check values on the target group.

```text
# aws_lb_listener.http will be created
+ resource "aws_lb_listener" "http" {
    + port     = 80
    + protocol = "HTTP"

    + default_action {
        + target_group_arn = (known after apply)
        + type             = "forward"
      }
  }

# aws_lb_target_group.application will be created
+ resource "aws_lb_target_group" "application" {
    + name        = "dev-application"
    + port        = 8080
    + protocol    = "HTTP"
    + target_type = "ip"

    + health_check {
        + healthy_threshold   = 2
        + interval            = 15
        + matcher             = "200-299"
        + path                = "/actuator/health"
        + port                = "traffic-port"
        + protocol            = "HTTP"
        + timeout             = 5
        + unhealthy_threshold = 3
      }
  }

Plan: 28 to add, 0 to change, 0 to destroy.
```

## Reviewer Conclusion

Task `2.0` is implemented and reviewable: the dev app stack now has an
explicit HTTP listener and ECS-compatible target-group routing contract, the
health-check model is documented and fixed in source, and both tests and
sanitized Terraform proof confirm the behavior.
