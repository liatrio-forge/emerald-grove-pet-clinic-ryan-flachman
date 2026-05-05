# Proofs: Task 06 — Update createOrUpdateOwnerForm template to render global errors (GREEN)

Covers: AC-4.a, AC-5.b, AC-5.c

## Planned evidence

- `createOrUpdateOwnerForm.html` diff showing the new global error `<div>` block.
- `grep -n "hasGlobalErrors" src/main/resources/templates/owners/createOrUpdateOwnerForm.html` output showing at least one match.
- `./mvnw test` passing output confirming all Java tests pass.
- `cd e2e-tests && npm test -- --grep "Owner Management"` passing output confirming `"blocks duplicate owner creation"` passes (GREEN phase).
- `duplicate-owner-error.png` screenshot path from Playwright output.

## Completion notes

(Filled in by `implement-sdd-spec`.)
