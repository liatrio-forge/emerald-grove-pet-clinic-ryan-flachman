# Proofs: Task 04 — Add nav link and upcomingVisits i18n key to all 9 language files

Covers: AC-6.a, AC-7.a

## Planned evidence

- Output of `grep -n "visits/upcoming" src/main/resources/templates/fragments/layout.html` returning at least one match.
- Output of `grep -rn "upcomingVisits" src/main/resources/messages/` returning at least 9 matches (one per language file).
- Output of `./mvnw test` exiting 0 including `I18nPropertiesSyncTest` passing.

## Completion notes

(Filled in by `implement-sdd-spec`.)
