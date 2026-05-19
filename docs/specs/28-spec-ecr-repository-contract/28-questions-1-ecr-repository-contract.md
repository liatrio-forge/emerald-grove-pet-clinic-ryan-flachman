# 28 Questions Round 1 - ECR Repository Contract

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Convenience Tags

Should this first version allow convenience tags such as `main-latest` in addition to immutable Git SHA tags?

- [x] (A) Git SHA tags only; do not allow convenience tags in v1
- [ ] (B) Allow one mutable convenience tag such as `main-latest` in addition to immutable Git SHA tags
- [ ] (C) Allow multiple convenience tags for different branches or environments in addition to immutable Git SHA tags
- [ ] (D) Keep all tags mutable for maximum flexibility in the POC
- [ ] (E) Other (describe)

**Current best-practice context:** Current Amazon ECR guidance favors tag immutability for images that should not be overwritten. AWS also supports immutable repositories with explicit exclusions for selected mutable tags, which means `latest`-style tags are possible but should be a deliberate exception rather than the default.

**Recommended answer(s):** [(A)]

**Why these are recommended:**

- `(A)` aligns directly with the issue's existing decision that images use immutable Git SHA tags and avoids reopening mutability questions in CI and deployment behavior.
- `(A)` keeps the CI consumption contract deterministic because every deployed image points to one immutable tag rather than a moving alias.
- `(B)` can still be valid later, but it introduces a second consumption path that makes rollback, promotion, and proof artifacts less clear in the first version.
- `(C)` and `(D)` add flexibility at the cost of a weaker repository contract and more room for accidental tag reuse.

## 2. Retention Policy Shape

What retention strategy should this first version define for images stored in the repository?

- [x] (A) Expire untagged images quickly and retain only a bounded count of tagged Git SHA images
- [ ] (B) Expire untagged images quickly and retain tagged Git SHA images for a bounded number of days
- [ ] (C) Keep all tagged Git SHA images for the duration of the POC and clean up only untagged images
- [ ] (D) Do not apply a lifecycle policy in v1; clean up images manually
- [ ] (E) Other (describe)

**Current best-practice context:** Current Amazon ECR guidance recommends using lifecycle policies for automated cleanup and previewing those policies before enforcement. AWS supports both count-based and age-based rules, but the best choice depends on whether this POC values rollback depth or aggressive cleanup more.

**Recommended answer(s):** [(A)]

**Why these are recommended:**

- `(A)` keeps storage bounded even if CI pushes many SHA-tagged images, which makes the dev POC easier to reason about operationally.
- `(A)` is easier to validate in a junior-friendly spec because "keep the newest N tagged images" is more stable than a date-based rule during frequent test pushes.
- `(B)` can work, but it is more sensitive to uneven deployment frequency and may keep either too many or too few images depending on release cadence.
- `(C)` and `(D)` are simpler initially, but they leave cleanup less intentional and weaken the issue's stated goal of documenting retention behavior.

## 3. Destroy-Time Behavior

How should the repository behave when the Terraform-managed dev stack is destroyed?

- [x] (A) Allow Terraform destroy to force-delete the repository and all contained images
- [ ] (B) Require the repository to be empty before destroy, so image cleanup must happen first
- [ ] (C) Keep the repository out of normal destroy flows and require a separate manual teardown step
- [ ] (D) Preserve the repository even when the rest of the app stack is destroyed
- [ ] (E) Other (describe)

**Current best-practice context:** Current Terraform AWS provider guidance exposes explicit repository delete behavior through `force_delete`, while Amazon ECR itself requires either an empty repository or a force delete. The right choice depends on whether the dev POC values teardown convenience or deletion safety more.

**Recommended answer(s):** [(A)]

**Why these are recommended:**

- `(A)` matches the issue's stated concern about destroy-time ambiguity and gives the dev POC a predictable teardown path without extra manual cleanup steps.
- `(A)` fits the repository's broader dev-only AWS POC posture, where fast iteration and disposable infrastructure are valued over long-lived artifact retention.
- `(B)` is safer, but it makes `terraform destroy` incomplete unless operators remember an extra image-cleanup workflow.
- `(C)` and `(D)` may be reasonable for longer-lived shared registries, but they create a lifecycle boundary that the current issue does not otherwise require.
