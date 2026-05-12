# Questions: VisitSummaryService (20) — Round 1

## Resolved questions

**Q1: Should `VisitSummaryParser` be in this spec or a separate one?**

TASK-06 (VisitSummaryParser) has no spec and no implementation. It is a required
dependency for VisitSummaryService. A separate spec would add overhead without
meaningful isolation — the parser is a small, single-method class.

> **Resolved:** `VisitSummaryParser` and `VisitSummaryParseException` are included
> in this spec's scope. Keeping the deliverable self-contained avoids a blocking
> dependency on an unwritten spec.

---

**Q2: How does `VisitSummaryService` load the owning `Pet` from a `visitId`?**

`Visit.java` has no back-reference to `Pet`. `Pet` owns the association via
`@OneToMany @JoinColumn(name = "pet_id")`, so `pet_id` exists in the `visits`
table but is not mapped on the `Visit` side.

Options considered:

- (a) Add `@ManyToOne(fetch = LAZY) Pet pet` to `Visit.java` with
  `insertable = false, updatable = false`.
- (b) Add a custom JPQL query to a repository that joins visits → pet.

> **Resolved:** Option (a). Add a read-only `@ManyToOne` back-reference to
> `Visit.java`. This avoids a cross-aggregate query and keeps visit loading
> straightforward. The `insertable = false, updatable = false` flags ensure the
> owning side (`Pet.visits`) remains the sole writer of the FK column.

---

**Q3: How does `VisitSummaryService` persist AI fields on an already-saved `Visit`?**

`VisitRepository` currently extends `Repository<Visit, Integer>` with no `save`
or `findById` method. The existing pattern saves visits indirectly via
`OwnerRepository.save(owner)` cascade. Loading the full Owner aggregate just to
save a few string fields is wasteful.

> **Resolved:** Add `Optional<Visit> findById(Integer id)` and
> `Visit save(Visit visit)` to `VisitRepository`. Spring Data derives `findById`
> automatically; `save` is declared explicitly and delegates to
> `SimpleJpaRepository.save`. Both additions are within `VisitRepository`'s
> existing `Repository<Visit, Integer>` contract.

---

**Q4: Should `generate()` be `@Transactional` across the full async lifecycle?**

A single transaction spanning the HTTP call to Claude (potentially seconds) holds
a DB connection for the entire duration and defeats the purpose of the PROCESSING
intermediate state (which is meant to be visible to the polling endpoint before
the Claude call completes).

> **Resolved:** No `@Transactional` on `generate()`. Each `visitRepository.save()`
> call runs in its own implicit Spring Data transaction. The two-save pattern
> (PROCESSING first, DONE/FAILED second) is intentional and idiomatic for this
> async pattern.

---

**Q5: Should `VisitSummaryParser` be a Spring `@Component` or a static utility?**

`VisitSummaryService` unit tests need to mock the parser to simulate parse
failures without a real Jackson dependency in every test. A static utility class
cannot be mocked with Mockito.

> **Resolved:** `@Component`. `VisitSummaryService` injects it via constructor.
> Parser unit tests (`VisitSummaryParserTests`) use the parser directly with no
> Spring context — only the service tests mock it.

---

**Q6: What JSON key name does the parser expect for the `followUp` field?**

`ClaudeApiClientStub` (spec-18, delivered) emits `"follow_up"` (snake_case).
The `VisitPromptBuilder` system prompt currently instructs Claude to use
`"followUp"` (camelCase). This inconsistency means a real Claude response would
differ from the stub response.

> **Resolved:** The parser reads `"follow_up"` (snake_case), matching the stub
> wire format defined in spec-18. The `VisitPromptBuilder` system prompt is
> updated in this spec's scope to replace `"followUp"` with `"follow_up"` so the
> real Claude API and the stub agree on the key name.

---

**Q7: What happens when `generate()` encounters an unexpected exception type
(not `ClaudeApiException` or `VisitSummaryParseException`)?**

The epic says "on any exception: set aiStatus = FAILED, save." A bare
`catch (Exception e)` catches everything including `RuntimeException`, ensuring
the visit is never left in PROCESSING state.

> **Resolved:** A single `catch (Exception e)` block wraps the entire orchestration
> body. It sets `aiStatus = FAILED`, logs the error, and saves. No exception
> propagates out of `generate()`.

---

## Open questions

None.
