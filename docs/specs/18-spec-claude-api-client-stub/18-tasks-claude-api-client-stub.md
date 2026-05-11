# Tasks: ClaudeApiClientStub (18)

## Task 01 — Write failing unit tests for ClaudeApiClientStub (RED phase)

Covers: AC-2.a, AC-3.a, AC-4.a, AC-5.a, AC-6.a, AC-7.a, AC-8.a, AC-8.b, AC-9.a, AC-9.b, AC-10.a

- Create `src/test/java/org/springframework/samples/petclinic/owner/ClaudeApiClientStubTests.java`.
- Annotate with `@ExtendWith(MockitoExtension.class)` (no Spring context needed).
- Add `import com.fasterxml.jackson.databind.JsonNode;` and
  `import com.fasterxml.jackson.databind.ObjectMapper;` for structural JSON assertions.
- Declare a private `ClaudeApiClientStub stub;` field and instantiate it in `@BeforeEach setUp()`.
- Write the following test methods, each calling `stub.complete(systemPrompt, userMessage)`
  and asserting on the parsed JSON node:
  - `completeWithLimpReturnsUrgentJson` — userMessage `"Dog is limping on left front leg"`,
    assert `urgency == "URGENT"` (AC-2.a).
  - `completeWithPainReturnsUrgentJson` — userMessage `"Cat seems to be in pain after eating"`,
    assert `urgency == "URGENT"` (AC-3.a).
  - `completeWithCheckupReturnsRoutineJson` — userMessage `"Annual checkup for Max"`,
    assert `urgency == "ROUTINE"` (AC-4.a).
  - `completeWithUnknownKeywordReturnsMonitorJson` — userMessage `"Seems a bit tired lately"`,
    assert `urgency == "MONITOR"` (AC-5.a).
  - `completeWithBothLimpAndCheckupReturnsUrgent` — userMessage `"Annual checkup; dog is limping"`,
    assert `urgency == "URGENT"` (AC-6.a).
  - `completeIsCaseInsensitive` — userMessage `"DOG IS LIMPING"`,
    assert `urgency == "URGENT"` (AC-7.a).
  - `completeWithBlankMessageReturnsMonitorJson` — userMessage `""`,
    assert `urgency == "MONITOR"`, no exception (AC-8.a).
  - `completeWithNullMessageReturnsMonitorJson` — userMessage `null`,
    assert `urgency == "MONITOR"`, no exception (AC-8.b).
- Every test method parses the returned string with `new ObjectMapper().readTree(result)`
  and asserts that nodes `summary`, `tags`, `urgency`, and `follow_up` are present (AC-9.a),
  and that `tags` is a non-empty JSON array (AC-9.b).
- Run `./mvnw test -Dtest=ClaudeApiClientStubTests` and capture the output showing
  compilation or runtime failure (RED phase proof for AC-10.a).
- Paste the Maven failure output into `18-proofs/18-task-01-proofs.md`.

**May break compile, fixed by:** Task 02 (`ClaudeApiClientStub.java` does not exist yet).

**Proof:** 18-proofs/18-task-01-proofs.md

---

## Task 02 — Implement ClaudeApiClientStub (GREEN phase)

Covers: AC-1.a, AC-1.b, AC-1.c, AC-1.d, AC-1.e, AC-2.a, AC-3.a, AC-4.a, AC-5.a, AC-6.a, AC-7.a, AC-8.a, AC-8.b, AC-9.a, AC-9.b, AC-11.a

- Create
  `src/main/java/org/springframework/samples/petclinic/owner/ClaudeApiClientStub.java`.
- Add imports: `org.springframework.boot.autoconfigure.condition.ConditionalOnExpression`,
  `org.springframework.stereotype.Component`.
- Annotate the class:

  ```java
  @Component
  @ConditionalOnExpression("'${anthropic.api.key:}'.trim().isEmpty()")
  ```

- Declare `public class ClaudeApiClientStub implements ClaudeApiClient`.
- Implement `public String complete(String systemPrompt, String userMessage)`:
  - Null-safe: treat `null` userMessage as empty string.
  - Convert to lowercase before keyword matching.
  - Check for `"limp"` or `"pain"` first → return the URGENT canned response.
  - Then check for `"checkup"` → return the ROUTINE canned response.
  - Otherwise → return the MONITOR canned response.
  - The `systemPrompt` parameter is accepted but not inspected (document with
    an inline comment: `// systemPrompt is intentionally ignored by the stub`).
- Define three private static final String constants holding the canned JSON bodies:

  ```text
  URGENT_LIMP_JSON  — urgency "URGENT", tags include "limping"
  URGENT_PAIN_JSON  — urgency "URGENT", tags include "pain"
  ROUTINE_JSON      — urgency "ROUTINE", tags include "checkup"
  MONITOR_JSON      — urgency "MONITOR", tags include "general"
  ```

  Each must include `summary`, `tags` (array ≥1 element), `urgency`, and `follow_up`.
  Note: use `URGENT_LIMP_JSON` when "limp" matches and `URGENT_PAIN_JSON` when
  "pain" matches (allows AC-2.a and AC-3.a to assert distinct tags if desired,
  while both carry `urgency == "URGENT"`).
- Run `./mvnw compile` and confirm exit 0 (AC-1.e).
- Run `./mvnw test -Dtest=ClaudeApiClientStubTests` and confirm all 8 tests pass.
- Run the structural greps for AC-1.b, AC-1.c, AC-1.d and confirm each returns a match.
- Paste compile output, test output, and grep outputs into `18-proofs/18-task-02-proofs.md`.

**Proof:** 18-proofs/18-task-02-proofs.md

---

## Task 03 — Validate and capture proof artifacts

Covers: all ACs

- Run `./mvnw test` from the repo root and confirm exit 0 with zero failures (AC-11.a).
- Run `./mvnw test jacoco:report` and open `target/site/jacoco/index.html`; locate
  `ClaudeApiClientStub` and record line coverage (must be ≥90%) and branch coverage
  (must be 100% on the keyword-routing logic).
- Transition every row in the coverage matrix in `18-validation-claude-api-client-stub.md`
  from `PENDING` to `PASS`.
- Tick every checkbox in the Definition of Done section of that file.
- Paste full `./mvnw test` output and the JaCoCo coverage numbers into
  `18-proofs/18-task-03-proofs.md`.
- Update `18-spec-claude-api-client-stub.md` front-matter: set `status: delivered`
  and `last_amended: <date>`.

**Proof:** 18-proofs/18-task-03-proofs.md
