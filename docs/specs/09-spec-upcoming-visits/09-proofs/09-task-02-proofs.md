# Proofs: Task 02 — Add UpcomingVisitRow record and VisitRepository with date-range query

Covers: AC-4.a, AC-4.b

## Planned evidence

- Output of `./mvnw compile` exiting 0 after `UpcomingVisitRow.java` and `VisitRepository.java` are created.
- Output of `find src/main/java -name "VisitRepository.java"` returning the expected path.
- Output of `grep -n "@Query" src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java` showing the JPQL constructor-expression query.

## Completion notes

### AC-4.a: `VisitRepository.java` is present

```text
$ find src/main/java -name "VisitRepository.java"
src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java
```

### AC-4.b: `VisitRepository.java` contains a `@Query` annotation with a JPQL expression

```text
$ grep -n "@Query" src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java
12:	@Query("""
```

The full query joins `Pet.visits` and `Pet.owner`:

```java
@Query("""
    SELECT new org.springframework.samples.petclinic.owner.UpcomingVisitRow(
        v.id, v.date, v.description,
        p.name,
        o.firstName, o.lastName, o.id
    )
    FROM Pet p JOIN p.visits v JOIN p.owner o
    WHERE v.date BETWEEN :start AND :end
    ORDER BY v.date ASC
    """)
List<UpcomingVisitRow> findUpcomingVisits(@Param("start") LocalDate start, @Param("end") LocalDate end);
```

### Compile check

```text
$ ./mvnw compile (after spring-javaformat:apply)

[INFO] --- compiler:3.14.1:compile (default-compile) ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 34 source files with javac [debug parameters release 17] to target/classes
[INFO] BUILD SUCCESS
[INFO] Total time:  2.966 s
[INFO] Finished at: 2026-05-06T13:25:20-05:00
```

### Controller tests still fail (expected — UpcomingVisitsController not yet created)

```text
$ ./mvnw test -Dtest=UpcomingVisitsControllerTests

[ERROR] COMPILATION ERROR :
[ERROR] UpcomingVisitsControllerTests.java:[26,13] cannot find symbol
  symbol: class UpcomingVisitsController
[INFO] BUILD FAILURE
[INFO] Total time:  3.170 s
```

Only `UpcomingVisitsController` remains missing. `VisitRepository` resolves cleanly.
