# Questions: ClaudeApiClientStub (18) — Round 1

## Resolved questions

**Q1: How should `@ConditionalOnProperty` activate the stub when `anthropic.api.key` is blank?**

Spring's `@ConditionalOnProperty` without `havingValue` checks that the property is
not `"false"` — an empty string `""` passes that check, so the real impl and the stub
would both activate on a blank key. Resolution: use `@ConditionalOnExpression` to
test the value's emptiness explicitly.

> **Resolved:** Stub uses:
>
> ```java
> @ConditionalOnExpression("'${anthropic.api.key:}'.trim().isEmpty()")
> ```
>
> Real impl (`ClaudeApiClientImpl`, TASK-09) must use the complement:
>
> ```java
> @ConditionalOnExpression("!'${anthropic.api.key:}'.trim().isEmpty()")
> ```

---

**Q2: What is the exact JSON format the stub must return?**

The `ClaudeApiClient.complete()` contract returns a raw `String`. The downstream
`VisitSummaryParser` (TASK-06, not yet specced) will parse that string. The epic
defines the `VisitSummary` record with fields `summary`, `tags`, `urgency`,
`followUp`. The stub spec owns the JSON format because it is the first concrete
producer; the parser spec must consume the same format.

> **Resolved:** Stub returns JSON matching this shape (snake_case keys consistent
> with Jackson's default `@JsonProperty` mapping):
>
> ```json
> {
>   "summary": "<non-blank string>",
>   "tags": ["<tag1>", "<tag2>"],
>   "urgency": "ROUTINE | MONITOR | URGENT",
>   "follow_up": "<nullable string>"
> }
> ```
>
> The `urgency` field uses the three clinical assessment values, NOT the
> pipeline-state values from `AiStatus` (PENDING/PROCESSING/DONE/FAILED).
> The parser spec (TASK-06) is authoritative for the `VisitUrgency` enum shape,
> but must accept the string values produced by this stub.

---

**Q3: What happens when both "checkup" and an urgent keyword appear in the same description?**

The epic does not specify precedence.

> **Resolved:** Urgent keywords ("limp", "pain") take precedence over "checkup".
> If neither urgent nor checkup keywords are present, return MONITOR.
> Priority: URGENT > ROUTINE > MONITOR (default).

---

**Q4: Is keyword matching case-sensitive?**

The epic does not specify.

> **Resolved:** Case-insensitive. Convert `userMessage` to lowercase before
> substring matching.

---

**Q5: What is the urgency value when no keywords match?**

The epic lists "checkup → ROUTINE" and "limp/pain → URGENT" but does not specify
a default.

> **Resolved:** Default is MONITOR. This is the middle-ground clinical signal
> meaning "watch but not immediately alarming", appropriate for any description
> that does not match a known pattern.

---

**Q6: Should the `systemPrompt` parameter influence the canned response?**

The real impl will use it to configure Claude's behaviour. The stub only needs
to return deterministic canned JSON; the system prompt is not diagnostic
information for this stub.

> **Resolved:** No. The stub ignores `systemPrompt` and inspects only
> `userMessage` for keyword matching.

---

## Open questions

None.
