# Proofs: Task 01 — Write failing `AsyncConfigTest` (RED)

Covers: AC-2.c, AC-2.d, AC-2.e, AC-2.f, AC-2.g

## Planned evidence

- Compilation failure (or test failure) output showing `AsyncConfig` class not found,
  confirming the test is genuinely red before the implementation exists.

## Completion notes

### AC-2.c through AC-2.g: Test written and confirmed RED

File created: `src/test/java/org/springframework/samples/petclinic/system/AsyncConfigTest.java`

#### `./mvnw test -Dtest="AsyncConfigTest"` (RED phase — compile failure)

```text
[ERROR]   symbol:   class AsyncConfig
[ERROR]   location: class org.springframework.samples.petclinic.system.AsyncConfigTest
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/AsyncConfigTest.java:[25,42] cannot find symbol
[ERROR]   symbol:   class AsyncConfig
[ERROR]   location: class org.springframework.samples.petclinic.system.AsyncConfigTest
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/AsyncConfigTest.java:[33,17] cannot find symbol
[ERROR]   symbol:   class AsyncConfig
[ERROR]   location: class org.springframework.samples.petclinic.system.AsyncConfigTest
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/AsyncConfigTest.java:[33,42] cannot find symbol
[ERROR]   symbol:   class AsyncConfig
[ERROR]   location: class org.springframework.samples.petclinic.system.AsyncConfigTest
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/AsyncConfigTest.java:[41,17] cannot find symbol
[ERROR]   symbol:   class AsyncConfig
[ERROR]   location: class org.springframework.samples.petclinic.system.AsyncConfigTest
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/AsyncConfigTest.java:[41,42] cannot find symbol
[ERROR]   symbol:   class AsyncConfig
[ERROR]   location: class org.springframework.samples.petclinic.system.AsyncConfigTest
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/AsyncConfigTest.java:[50,17] cannot find symbol
[ERROR]   symbol:   class AsyncConfig
[ERROR]   location: class org.springframework.samples.petclinic.system.AsyncConfigTest
[ERROR] /Users/ryan/Repos/emerald-grove-pet-clinic-ryan-flachman/src/test/java/org/springframework/samples/petclinic/system/AsyncConfigTest.java:[50,42] cannot find symbol
[ERROR]   symbol:   class AsyncConfig
[ERROR]   location: class org.springframework.samples.petclinic.system.AsyncConfigTest
[ERROR] -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
```

Compile fails as expected — `AsyncConfig` class not found. Will be fixed by Task 02.
