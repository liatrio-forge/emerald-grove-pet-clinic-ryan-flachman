# Proofs: Task 04 — Add visit.date.pastNotAllowed to all message property files

Covers: AC-2.a, AC-2.b, AC-2.c

## Planned evidence

- Output of `grep -c "visit.date.pastNotAllowed" src/main/resources/messages/messages.properties` returning `1` (AC-2.a)
- Output of `grep -rn "visit.date.pastNotAllowed" src/main/resources/messages/` showing ≥8 matches across all locale files (AC-2.b)
- Output of `./mvnw test -Dtest=I18nPropertiesSyncTest` showing **BUILD SUCCESS** (AC-2.c)

## Completion notes

### AC-2.a: messages.properties contains key visit.date.pastNotAllowed

```text
$ grep -c "visit.date.pastNotAllowed" src/main/resources/messages/messages.properties
1
```

### AC-2.b: All 7 non-English locale files contain visit.date.pastNotAllowed (≥8 matches)

```text
$ grep -rn "visit.date.pastNotAllowed" src/main/resources/messages/
src/main/resources/messages/messages_ko.properties:78:visit.date.pastNotAllowed=오늘 이후 날짜여야 합니다
src/main/resources/messages/messages_de.properties:78:visit.date.pastNotAllowed=muss heute oder in der Zukunft liegen
src/main/resources/messages/messages_ru.properties:78:visit.date.pastNotAllowed=должна быть сегодня или в будущем
src/main/resources/messages/messages_es.properties:78:visit.date.pastNotAllowed=debe ser hoy o en el futuro
src/main/resources/messages/messages_pt.properties:78:visit.date.pastNotAllowed=deve ser hoje ou no futuro
src/main/resources/messages/messages_fa.properties:78:visit.date.pastNotAllowed=باید امروز یا در آینده باشد
src/main/resources/messages/messages.properties:78:visit.date.pastNotAllowed=must be today or in the future
src/main/resources/messages/messages_tr.properties:78:visit.date.pastNotAllowed=bugün veya gelecekte olmalıdır
```

8 matches total (base + de, es, fa, ko, pt, ru, tr).

### AC-2.c: I18nPropertiesSyncTest exits 0

```text
$ ./mvnw test -Dtest=I18nPropertiesSyncTest
[INFO] Running org.springframework.samples.petclinic.system.I18nPropertiesSyncTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.044 s
[INFO] BUILD SUCCESS
[INFO] Total time:  3.108 s
[INFO] Finished at: 2026-05-06T14:54:27-05:00
```
