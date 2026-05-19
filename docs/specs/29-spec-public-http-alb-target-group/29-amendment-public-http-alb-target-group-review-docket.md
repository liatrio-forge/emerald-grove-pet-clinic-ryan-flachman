# 29 Amendment Review Docket: Public HTTP ALB Target Group

## Purpose

This amendment docket captures the open review challenges for
`29-spec-public-http-alb-target-group.md` and converts them into explicit
decision points. Each section includes the question, viable solution options,
the recommended choice, and the reasoning behind that recommendation so the
spec can be tightened before implementation or task planning.

## Recommended Approach Summary

The strongest path is to keep this spec as a **plan-time infrastructure
contract** for the dev app stack, make all target-group health values explicit
in the spec itself, narrow claims about the ALB DNS name so they do not imply
end-to-end application reachability, and move unresolved hardening items into
either non-goals or follow-on specs with rationale.

## Decision Docket

### 1. Why is the most operationally sensitive part of the contract, health-check timing, still unresolved if the spec claims to define a stable target-group contract?

Options:

1. Keep the question open and let the implementer choose values during task
   execution.
2. Define exact interval, timeout, healthy-threshold, and unhealthy-threshold
   values in the spec now.
3. Define a bounded range in the spec and let the task list choose the final
   numbers after local validation.

Recommendation:

Choose option 2.

Reasoning:

The current spec already requires explicit values, so leaving them unresolved
creates a contract gap. Exact values are necessary for deterministic tests,
reviewable Terraform plan assertions, and stable downstream expectations.
Option 1 weakens TDD and invites ad hoc implementation. Option 3 still delays
the actual contract and keeps the spec partially undefined.

Suggested amendment:

Add one requirement that names the exact four values and remove the open
question unless there is a proven measurement dependency that cannot be settled
yet.

Answer:

I think the interval should be every 15s, timeout of 5s, healthy threshold of 2 and unhealthy threshold of 3. I think there is already a health enpoint.

### 2. What exact failing tests do we expect to write first for this spec, and which of them are impossible until health-check values are finalized?

Options:

1. Do not describe tests in the amendment and let the later task list infer
   them.
2. Add a short expected failing-test inventory to guide TDD sequencing.
3. Skip tests for the spec and rely only on `terraform plan` proof artifacts.

Recommendation:

Choose option 2.

Reasoning:

This repository requires strict TDD. The spec should at least make the initial
 RED-phase targets obvious: ALB resource contract, listener contract,
 target-group contract, output contract, and health-check value assertions.
 Option 1 leaves too much ambiguity for task authors. Option 3 conflicts with
 the repository workflow and makes review weaker.

Suggested amendment:

Add a short note in repository standards or technical considerations naming the
 first expected contract tests:

- ALB is internet-facing and uses exported public subnet IDs
- Listener is HTTP on port `80` with a default forward action
- Target group uses `ip`, port `8080`, `/actuator/health`, matcher `200`, and
  the chosen threshold values
- Outputs expose DNS name, hosted zone ID, ALB ARN, listener ARN, and
  target-group ARN

Answer:

go with you suggested amendments

### 3. Are we defining an ALB contract, a plan-time contract, or a deployed dev-environment contract?

Options:

1. Keep the current mixed proof model using file, plan, and output artifacts.
2. Define the spec explicitly as a plan-time contract and keep proofs centered
   on source, validation, `floci`, and sanitized `terraform plan`.
3. Upgrade the spec into a deployed-environment contract that requires live AWS
   apply output.

Recommendation:

Choose option 2.

Reasoning:

The rest of the repository standards point toward local contract validation
before live deployment. That aligns with spec work and task planning better than
requiring a real AWS apply. Option 1 leaves reviewers unsure what counts as
proof. Option 3 raises the cost of review and pulls the spec toward environment
provisioning acceptance criteria.

Suggested amendment:

State explicitly that this spec defines a **Terraform contract that is
reviewable at source and plan time**, with live deployment verification deferred
to later validation work.

Answer:

go with option 2

### 4. If the ALB can fail open when all targets are unhealthy, what user-visible behavior are we willing to tolerate in v1, and where is that called out as an explicit risk?

Options:

1. Ignore fail-open because it is standard ALB behavior.
2. Add an explicit risk note describing possible transient routing to unhealthy
   tasks and accept that limitation in the POC.
3. Expand scope now to include additional mitigation such as ECS grace-period
   tuning or alternate readiness behavior.

Recommendation:

Choose option 2.

Reasoning:

The spec already references fail-open behavior, but it does not translate that
into an operator-facing risk statement. The clean move is to acknowledge the
limitation without expanding scope into ECS service behavior. Option 1 hides a
real operational caveat. Option 3 mixes service tuning into an ALB-only
contract.

Suggested amendment:

Add a technical or security note that v1 may briefly route requests to newly
registered or unhealthy targets if the entire target set is unhealthy, and that
mitigation belongs to the later ECS service spec.

Answer:

go with option 2

### 5. Why is matcher `200` correct for the Spring Boot health model, and what breaks if the application later returns another 2xx or introduces readiness groups?

Options:

1. Keep matcher `200` as a deliberately strict contract tied to the current
   application health response.
2. Broaden the matcher to `200-299` for future flexibility.
3. Leave the matcher unspecified beyond "successful health response."

Recommendation:

Choose option 1 unless there is concrete evidence the app contract is expected
to change soon.

Reasoning:

The current container contract documents `/actuator/health` as the stable
endpoint and the spec is intentionally narrow. A strict matcher keeps behavior
easy to review and prevents accidental acceptance of changed response semantics.
Option 2 trades clarity for hypothetical flexibility. Option 3 is too vague to
test.

Suggested amendment:

Add one sentence explaining that matcher `200` is intentionally strict because
the current deploy-profile health contract is a single stable success code, and
any broadened success range should be introduced by a later spec revision.

Answer:

go with option 2

### 6. If the ECS service is out of scope, what does "public URL contract" really mean besides "a DNS name exists"?

Options:

1. Keep calling it the public URL contract without qualification.
2. Rename it as the public endpoint identifier contract and clarify that it does
   not imply a working application response until a later ECS service spec
   attaches targets.
3. Pull DNS outputs out of this spec entirely until ECS exists.

Recommendation:

Choose option 2.

Reasoning:

The DNS name is still useful as a stable identifier for downstream specs, but
the current wording overclaims what the output proves. Option 2 preserves the
value while fixing the semantics. Option 1 misleads. Option 3 removes useful
integration data for little gain.

Suggested amendment:

Replace phrases like "public v1 access path" with "approved public endpoint
identifier for v1 infrastructure wiring" and note that end-to-end reachability
depends on later ECS service attachment.

Answer:

go with option 2

### 7. Where do we define the listener and resource names that downstream specs should depend on?

Options:

1. Rely on general repository naming conventions without spelling out output or
   resource naming expectations.
2. Define exact output names and a predictable naming pattern for ALB, listener,
   and target-group related resources.
3. Expose only ARNs and avoid all human-readable naming commitments.

Recommendation:

Choose option 2.

Reasoning:

Downstream specs need a contract they can cite without reverse-engineering
Terraform internals. The repo already publishes readable outputs such as
`alb_security_group_id` and `repository_name`, so this spec should continue
that pattern explicitly. Option 1 is underspecified. Option 3 is machine-usable
but reviewer-hostile.

Suggested amendment:

Add a short naming/output table that specifies the exact Terraform output names
expected from this spec and states that resource names must follow existing
`locals.tf` conventions.

Answer:

go with option 2 and 3. we want to alway surface ARNS to use but also surface human-readable names when possible.

### 8. Why is access logging a mere open question instead of an explicit non-goal with rationale, given this is internet-facing infrastructure?

Options:

1. Keep it as an open question.
2. Move ALB access logging into non-goals for this spec and explain that the
   first milestone is entrypoint and health contract clarity, not observability
   hardening.
3. Expand scope now to require access logging.

Recommendation:

Choose option 2.

Reasoning:

This is the cleanest scope boundary. Access logging is important, but it is not
required to establish the ALB/listener/target-group contract. Option 1 leaves
scope muddy. Option 3 is reasonable only if the team wants this spec to become
a broader internet-edge hardening spec.

Suggested amendment:

Move access logging from Open Questions into Non-Goals or Security
Considerations with one rationale sentence and identify it as a follow-on
hardening item.

Answer:

go with option 2

### 9. How will a reviewer distinguish "ALB attached to public subnets correctly" from "ALB accidentally reachable but semantically useless" using only the listed proofs?

Options:

1. Accept that plan output is enough because this spec is only about resource
   shape.
2. Add proof language that separates network placement proof from routing and
   health-contract proof.
3. Require a live HTTP probe against a deployed ALB.

Recommendation:

Choose option 2.

Reasoning:

The issue is not that the current proofs are wrong, but that they blur two
different questions: "is the ALB public?" and "does the routing contract point
to the intended application health model?" Distinct proofs keep reviewers from
conflating those. Option 3 overshoots the likely maturity of the spec.

Suggested amendment:

Split proof expectations so one proof covers internet-facing subnet/security
group placement and another covers listener-to-target-group forwarding plus
explicit health-check semantics.

Answer:

go with option 2

### 10. If HTTPS is intentionally deferred, what is the hard boundary preventing later specs from quietly mutating this listener instead of creating a new secure contract?

Options:

1. Leave the boundary implicit and trust later spec authors.
2. State that this spec establishes a v1 HTTP listener contract and that HTTPS
   must be introduced by additive change, either as a new listener or an
   explicitly versioned contract revision.
3. Forbid any later mutation of the HTTP listener.

Recommendation:

Choose option 2.

Reasoning:

You need a versioning rule, not a permanent freeze. Later work should be able
to evolve the edge contract, but not silently redefine what this spec promised.
Option 2 preserves forward movement while protecting reviewability. Option 1 is
too soft. Option 3 is unnecessarily rigid.

Suggested amendment:

Add a scope-governance sentence that HTTPS work must either create an additive
listener/domain layer or explicitly supersede this spec with a revision that
calls out the changed contract.

Answer:

go with option 2

## Concrete Spec Edits To Make Next

1. Replace the unresolved health-check value question with exact numbers or an
   explicit blocker note that prevents implementation until numbers are chosen.
2. Reframe Unit 3 from "public URL contract" to "public endpoint identifier and
   downstream integration contract."
3. Clarify proof expectations as source-plus-plan-time validation rather than
   mixed plan/apply semantics.
4. Add an explicit risk note covering ALB fail-open behavior and the fact that
   end-to-end availability is not proven by this spec alone.
5. Promote the major design choices from implicit assumptions into the Design
   Considerations section.
6. Move ALB access logging out of Open Questions unless the team truly wants the
   scope left undecided.

## Recommended Final Position

This spec should stay narrow. It should define one reviewable HTTP ALB contract
for dev infrastructure, publish exact listener and target-group settings, expose
reviewer-readable outputs, and avoid implying that a deployable internet
application path exists before ECS service attachment. That gives later task and
validation work a stable contract without pretending the infrastructure is more
complete than it is.
