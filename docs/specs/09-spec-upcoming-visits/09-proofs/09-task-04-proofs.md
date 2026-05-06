# Proofs: Task 04 — Add nav link and upcomingVisits i18n key to all 9 language files

Covers: AC-6.a, AC-7.a

## Planned evidence

- Output of `grep -n "visits/upcoming" src/main/resources/templates/fragments/layout.html` returning at least one match.
- Output of `grep -rn "upcomingVisits" src/main/resources/messages/` returning at least 9 matches (one per language file).
- Output of `./mvnw test` exiting 0 including `I18nPropertiesSyncTest` passing.

## Completion notes

### AC-6.a: Nav link referencing `/visits/upcoming` present in `layout.html`

```text
$ grep -n "visits/upcoming" src/main/resources/templates/fragments/layout.html
62:          <li th:replace="~{::menuItem ('/visits/upcoming','visits','upcoming visits','calendar',#{upcomingVisits})}">
63:            <a href="/visits/upcoming">
```

### AC-7.a: `upcomingVisits` i18n key present in all 9 language files (9 matches)

```text
$ grep -rn "upcomingVisits" src/main/resources/messages/
src/main/resources/messages/messages_en.properties:2:upcomingVisits=Upcoming Visits
src/main/resources/messages/messages_ko.properties:73:upcomingVisits=Upcoming Visits
src/main/resources/messages/messages_de.properties:73:upcomingVisits=Upcoming Visits
src/main/resources/messages/messages_fa.properties:73:upcomingVisits=Upcoming Visits
src/main/resources/messages/messages_pt.properties:73:upcomingVisits=Upcoming Visits
src/main/resources/messages/messages_es.properties:73:upcomingVisits=Upcoming Visits
src/main/resources/messages/messages_tr.properties:73:upcomingVisits=Upcoming Visits
src/main/resources/messages/messages_ru.properties:73:upcomingVisits=Upcoming Visits
src/main/resources/messages/messages.properties:73:upcomingVisits=Upcoming Visits
```

9 matches — one per file. ✓

### Full suite including I18nPropertiesSyncTest

```text
$ ./mvnw test

[WARNING] Tests run: 80, Failures: 0, Errors: 0, Skipped: 5
[INFO] BUILD SUCCESS
[INFO] Total time:  13.778 s
[INFO] Finished at: 2026-05-06T13:32:57-05:00
```

`I18nPropertiesSyncTest` passes (`checkNonInternationalizedStrings` and `checkI18nPropertyFilesAreInSync`).

### Notes

Initial nav item markup used `<span>Upcoming Visits</span>` as fallback content without
`th:text`, which `I18nPropertiesSyncTest.checkNonInternationalizedStrings` flagged. Fixed
to `<span th:text="#{upcomingVisits}">Upcoming Visits</span>` consistent with all other
nav items in `layout.html`.
