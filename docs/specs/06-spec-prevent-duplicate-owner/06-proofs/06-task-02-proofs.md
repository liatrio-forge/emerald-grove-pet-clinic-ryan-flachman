# Proofs: Task 02 — Write failing OwnerControllerTests for duplicate detection (RED)

Covers: AC-3.a, AC-3.b, AC-3.c

## Planned evidence

- `OwnerControllerTests.java` diff showing `@MockitoBean OwnerService ownerService`, default stub in `@BeforeEach`, and the new `testProcessCreationFormDuplicateRejected` test method.
- `./mvnw test -Dtest=OwnerControllerTests` failure output confirming `testProcessCreationFormDuplicateRejected` fails because `OwnerService` is not yet wired in the controller (RED phase).

## Completion notes

### AC-3.a, AC-3.b, AC-3.c: `OwnerControllerTests` changes

Changes applied to `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java`:

1. Added `@MockitoBean private OwnerService ownerService;` field.
2. Added default stub in `@BeforeEach`: `given(ownerService.isDuplicate(any(), any(), any())).willReturn(false)` — covers AC-3.c so existing tests pass once the GREEN phase wires OwnerService into the controller.
3. Added `testProcessCreationFormDuplicateRejected()`:
   - Stubs `isDuplicate("Joe", "Bloggs", "1316761638")` to return `true`
   - Asserts HTTP 200 (AC-3.a)
   - Asserts view `owners/createOrUpdateOwnerForm` (AC-3.a)
   - Asserts `BindingResult` has exactly one global error with code `"duplicate"` (AC-3.a)
   - Verifies `owners.save()` is never invoked (AC-3.b)

### RED phase: `./mvnw test -Dtest=OwnerControllerTests`

```text
[ERROR] COMPILATION ERROR :
[ERROR] /src/test/java/.../owner/OwnerControllerTests.java:[76,17] cannot find symbol
  symbol:   class OwnerService
  location: class ...OwnerControllerTests
[ERROR] /src/test/java/.../owner/OwnerServiceTests.java:[34,17] cannot find symbol
  symbol:   class OwnerService
  location: class ...OwnerServiceTests
[INFO] BUILD FAILURE
```

Compile fails as expected — `OwnerService` does not exist yet. RED phase confirmed.

### Notes

Commit deferred: the pre-commit `maven-test-check` hook requires all tests to pass. Since this task's RED phase intentionally produces a compile failure, the commit is deferred to after Task 04 (which creates `OwnerService` and restores compilation). RED phase is evidenced by the captured compiler output above.
