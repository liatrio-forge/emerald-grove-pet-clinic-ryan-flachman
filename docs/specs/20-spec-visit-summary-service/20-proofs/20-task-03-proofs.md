# Proofs: Task 03 — Extend `VisitRepository`, `Visit.java`, and correct `VisitPromptBuilder` key

Covers: AC-12.a, AC-12.b, AC-13.a, AC-13.b, AC-14.a, AC-14.b

## Grep verification

### `grep "Optional<Visit> findById"` / `Visit save` (`VisitRepository.java`)

```text
	Optional<Visit> findById(Integer id);
	Visit save(Visit visit);
```

### `grep "@ManyToOne"` (`Visit.java`)

```text
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id", insertable = false, updatable = false)
```

### `grep "follow_up"` (`VisitPromptBuilder.java`)

```text
			"tags", "urgency", and "follow_up".
```

### `grep "insertable = false"`

See Pet FK line above (`insertable = false, updatable = false`).

## `./mvnw compile` (excerpt)

```text
[INFO] BUILD SUCCESS
[INFO] Total time:  2.818 s
[INFO] Finished at: 2026-05-12T10:21:56-05:00
```

## `./mvnw test -Dtest=VisitPromptBuilderTest` (excerpt)

```text
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  3.608 s
[INFO] Finished at: 2026-05-12T10:22:00-05:00
```

## Completion notes

Repository, Visit back-reference, and prompt key corrections verified by grep
plus compile and existing `VisitPromptBuilderTest` still green.
