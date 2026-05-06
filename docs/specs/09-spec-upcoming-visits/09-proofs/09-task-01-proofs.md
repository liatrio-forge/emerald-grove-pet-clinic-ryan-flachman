# Proofs: Task 01 — Write failing UpcomingVisitsControllerTests (RED)

Covers: AC-1.a, AC-2.a, AC-3.a

## Planned evidence

- Output of `./mvnw test -Dtest=UpcomingVisitsControllerTests` showing both tests failing (compile error or `NoSuchBeanDefinitionException` — not a test-logic failure, but failing for the correct reason: no controller exists yet).

## Completion notes

### AC-1.a / AC-2.a / AC-3.a: RED phase — tests fail because `UpcomingVisitsController` and `VisitRepository` do not exist yet

File created: `src/test/java/org/springframework/samples/petclinic/owner/UpcomingVisitsControllerTests.java`

```text
./mvnw test -Dtest=UpcomingVisitsControllerTests

[INFO] --- compiler:3.14.1:testCompile (default-testCompile) ---
[INFO] Recompiling the module because of changed source code.
[ERROR] COMPILATION ERROR :
[ERROR] /…/UpcomingVisitsControllerTests.java:[35,17] cannot find symbol
  symbol:   class VisitRepository
  location: class org.springframework.samples.petclinic.owner.UpcomingVisitsControllerTests
[ERROR] /…/UpcomingVisitsControllerTests.java:[26,13] cannot find symbol
  symbol: class UpcomingVisitsController
[INFO] 2 errors
[INFO] BUILD FAILURE
[INFO] Total time:  3.144 s
[INFO] Finished at: 2026-05-06T13:03:48-05:00
```

Fails for the correct reason: production classes `UpcomingVisitsController` and `VisitRepository` do not exist yet. This is the expected RED state.
