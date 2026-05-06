# Questions: Friendly 404 Pages (07) — Round 1

## Resolved

| # | Question | Resolution |
|---|----------|------------|
| Q-1 | How should the code signal a missing resource? | **Custom `ResourceNotFoundException`** annotated `@ResponseStatus(HttpStatus.NOT_FOUND)`. Reusable across all three controllers, cleanest for TDD. |
| Q-2 | Which controllers are in scope? | **OwnerController, PetController, and VisitController** — all three have the same `IllegalArgumentException` pattern for missing owners/pets. |
| Q-3 | How should the 404 page be built? | **Dedicated `error/404.html` template** — Spring Boot auto-resolves this for all 404 responses. Gives full control over the message and Find Owners link without modifying the shared `error.html` status-switch logic. |
| Q-4 | What should the not-found message say? | **Generic** — "The requested owner could not be found" / "The requested pet could not be found". No internal IDs or exception details exposed. |
| Q-5 | Should the raw exception message be visible in any error page? | **No** — remove the exception message display from `error.html` entirely for all status codes, not just 404. The acceptance criteria explicitly prohibit exposing internal exception details. |
| Q-6 | Where should `ResourceNotFoundException` live? | **`system/` package** — it is a cross-cutting infrastructure concern, not specific to `owner/` or `vet/`. Consistent with `CrashController` and `CacheConfiguration` placement. |
| Q-7 | Does `PetController.findPet` need a null guard? | **Yes** — the existing `findPet` method calls `owner.getPet(petId)` and returns `null` without throwing. A null check throwing `ResourceNotFoundException` must be added so the pet-not-found path also yields a 404. |

## Open

None.
