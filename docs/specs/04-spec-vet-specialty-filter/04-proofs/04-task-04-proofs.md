# Proofs: Task 04 — Update vetList.html with filter pills and add i18n message keys (GREEN)

Covers: AC-1.a, AC-1.b, AC-1.c, AC-1.d, AC-3.a, AC-3.b, AC-3.c, AC-4.a, AC-4.b, AC-6.b

## Planned evidence

- `vetList.html` diff showing `[data-testid="specialty-filter"]` div with All, specialty, and None pills.
- Confirmation that pagination links include `specialty` param.
- `messages.properties` diff showing `vets.filter.all` and `vets.filter.none` keys.
- `grep` output confirming keys present in all 8 required property files.
- Output of `./mvnw test -Dtest=I18nPropertiesSyncTest` showing **passing** (no hard-coded strings, all locales in sync).

## Completion notes

(Filled in by `implement-sdd-spec`.)
