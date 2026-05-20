# 34 Questions Round 1 - Full Dev Infra Lifecycle Cleanup

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Foundation Teardown Entry Point

How should the repository handle the final teardown of the foundation resources that bootstrap everything else, specifically the Terraform backend (`state/dev`) and the GitHub OIDC identity resources?

- [ ] (A) Provide a separate GitHub Actions workflow that uses the protected `dev-bootstrap` environment and temporary bootstrap credentials to destroy `identity/dev` and `state/dev` after `app/dev` is gone.
- [ ] (B) Require operators to destroy `identity/dev` and `state/dev` manually from a local terminal using their own AWS credentials; no repo-owned destroy workflow for those layers.
- [ ] (C) Use the normal OIDC-driven Terraform destroy flow to remove `identity/dev` and `state/dev` too.
- [ ] (D) Keep backend and identity resources permanently and only destroy `app/dev`.
- [ ] (E) Other (describe)

**Current best-practice context:** Current GitHub and AWS guidance recommends using GitHub OIDC for normal automation, constraining environment access with protection rules, and avoiding long-lived cloud credentials in GitHub. That makes a separate bootstrap-only path reasonable for create/destroy actions that cannot be performed by the OIDC roles themselves.

**Recommended answer(s):** [(A)]

**Why these are recommended:**

- `(A)` preserves the clean separation between steady-state OIDC workflows and the exceptional bootstrap/foundation lifecycle, while still giving the POC a fully repo-owned way to remove everything.
- `(A)` avoids the circular dependency and self-deleting-role problem that makes `(C)` unsafe for foundation teardown.
- `(B)` is workable, but it weakens repeatability and proof collection because the most critical teardown path would live outside the repository workflows.
- `(D)` conflicts with your stated goal of removing absolutely everything at the end.

## 2. Bootstrap Credential Persistence

What should happen to the admin-backed bootstrap AWS credentials after the foundation and app stacks are created?

- [ ] (A) Keep them in the protected `dev-bootstrap` environment so the repository can later run bootstrap or foundation teardown workflows without re-entering credentials.
- [ ] (B) Remove them after bootstrap and require operators to add them back only when a future foundation bootstrap or teardown is needed.
- [ ] (C) Store them as repository-wide secrets rather than environment-scoped secrets.
- [ ] (D) Do not support GitHub-hosted bootstrap credentials at all; bootstrap and foundation teardown must always be local-only.
- [ ] (E) Other (describe)

**Current best-practice context:** GitHub's OIDC guidance exists specifically to avoid long-lived cloud credentials in GitHub. If a bootstrap exception is needed, current best practice is to keep it narrowly scoped behind a protected environment and minimize how long those credentials remain stored.

**Recommended answer(s):** [(B)]

**Why these are recommended:**

- `(B)` best matches current security guidance by minimizing the lifespan of privileged non-OIDC credentials.
- `(B)` keeps the exceptional path exceptional, which makes the steady-state model clearer and easier to audit.
- `(A)` is more convenient, but it turns a one-time exception into a standing secret-management policy.
- `(C)` is weaker than `(A)` because it removes the extra environment protection boundary.

## 3. Normal Rebuild Boundary

After the initial bootstrap is complete, what should be possible without using bootstrap/admin credentials again?

- [ ] (A) Operators should be able to destroy and recreate `app/dev` only, using GitHub OIDC workflows, while `identity/dev` and `state/dev` remain in place.
- [ ] (B) Operators should be able to destroy and recreate both `app/dev` and `identity/dev` using only GitHub OIDC workflows.
- [ ] (C) Every destroy and recreate cycle should re-run the full bootstrap from scratch, including identity and backend resources.
- [ ] (D) Only app creation is needed; app destroy/recreate is out of scope for this feature.
- [ ] (E) Other (describe)

**Current best-practice context:** Separating long-lived foundation resources from frequently rebuilt application resources is the standard way to avoid circular dependencies and reduce blast radius in Terraform-based environments.

**Recommended answer(s):** [(A)]

**Why these are recommended:**

- `(A)` is the cleanest boundary: `app/dev` becomes independently rebuildable, while foundation resources stay stable until intentional final teardown.
- `(A)` directly solves the current problem where destroying the app stack risks deleting the very roles the workflows depend on.
- `(B)` sounds convenient, but it reintroduces the self-destruction and bootstrap dependency problem unless there is yet another higher-privilege layer above it.
- `(C)` works for a throwaway POC, but it makes ordinary testing and iteration slower and riskier than necessary.

## 4. GitHub Configuration Cleanup

When the operator performs the final full teardown, what should happen to the GitHub Actions variables and bootstrap secrets?

- [ ] (A) The final teardown flow should include explicit cleanup instructions for deleting the stale variables and bootstrap secrets from GitHub after AWS resource deletion.
- [ ] (B) The final teardown flow should attempt to delete the GitHub variables and bootstrap secrets automatically as part of the workflow.
- [ ] (C) The final teardown flow may leave stale GitHub configuration in place because it is harmless.
- [ ] (D) Only bootstrap secrets should be deleted; environment variables should remain forever.
- [ ] (E) Other (describe)

**Current best-practice context:** GitHub environment protection and secret scoping reduce exposure, but stale high-privilege configuration is still operational debt. For destructive flows, explicit operator-visible cleanup steps are often more reliable than self-mutating workflow logic.

**Recommended answer(s):** [(A)]

**Why these are recommended:**

- `(A)` is reliable and reviewable: the workflow can summarize exactly what must be removed without relying on fragile self-modifying GitHub API behavior.
- `(A)` keeps proof artifacts observable and reduces the chance that a partially failed cleanup workflow leaves its own repository configuration in an uncertain state.
- `(B)` may be possible, but it adds a second layer of privileged GitHub mutation logic to a path that is already destructive.
- `(C)` and `(D)` conflict with your goal of cleaning up absolutely everything.
