# Proofs: Task 04 — Add visit.date.pastNotAllowed to all message property files

Covers: AC-2.a, AC-2.b, AC-2.c

## Planned evidence

- Output of `grep -c "visit.date.pastNotAllowed" src/main/resources/messages/messages.properties` returning `1` (AC-2.a)
- Output of `grep -rn "visit.date.pastNotAllowed" src/main/resources/messages/` showing ≥8 matches across all locale files (AC-2.b)
- Output of `./mvnw test -Dtest=I18nPropertiesSyncTest` showing **BUILD SUCCESS** (AC-2.c)

## Completion notes

(Filled in by `implement-sdd-spec`.)
