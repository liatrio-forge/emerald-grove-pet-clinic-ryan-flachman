# Tasks: VisitSummaryIntegrationTest (22)

## Task 01 — Verify Awaitility is on the test classpath

Covers: AC-1.a

- Run the following command from the repository root and capture the output:

  ```bash
  ./mvnw dependency:tree -Dincludes=org.awaitility:awaitility
  ```

  Expected output contains a line such as:

  ```text
  [INFO]    \- org.awaitility:awaitility:jar:4.x.x:test
  ```

- If the dependency is **present**: no `pom.xml` change needed. Record the
  command output in the proof file.

- If the dependency is **absent**: add the following inside the `<dependencies>`
  block of `pom.xml` (test scope, no version — managed by Spring Boot BOM):

  ```xml
  <dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <scope>test</scope>
  </dependency>
  ```

  Then re-run `./mvnw test-compile` and capture the BUILD SUCCESS output.

- Record output in `22-proofs/22-task-01-proofs.md`.

**Proof:** `22-proofs/22-task-01-proofs.md`

---

## Task 02 — Create VisitSummaryHappyPathIT

Covers: AC-2.a, AC-2.b, AC-2.c, AC-3.a, AC-3.b, AC-3.c, AC-3.d, AC-3.e,
AC-4.a, AC-4.b

- Create the file
  `src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java`
  with the content below. Because all production code is already implemented,
  the tests should be green immediately (no RED/GREEN split needed; this task
  is pure test-code authoring):

  ```java
  package org.springframework.samples.petclinic.owner;

  import static org.assertj.core.api.Assertions.assertThat;
  import static org.awaitility.Awaitility.await;
  import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
  import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
  import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
  import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

  import java.util.Set;
  import java.util.concurrent.TimeUnit;
  import java.util.stream.Collectors;

  import org.junit.jupiter.api.condition.DisabledInNativeImage;
  import org.junit.jupiter.params.ParameterizedTest;
  import org.junit.jupiter.params.provider.CsvSource;
  import org.junit.jupiter.api.Test;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
  import org.springframework.boot.test.context.SpringBootTest;
  import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
  import org.springframework.test.context.aot.DisabledInAotMode;
  import org.springframework.test.web.servlet.MockMvc;

  @SpringBootTest(webEnvironment = WebEnvironment.MOCK)
  @AutoConfigureMockMvc
  @DisabledInNativeImage
  @DisabledInAotMode
  class VisitSummaryHappyPathIT {

      private static final int OWNER_ID = 6;
      private static final int PET_ID   = 7;

      @Autowired MockMvc mockMvc;
      @Autowired OwnerRepository ownerRepository;
      @Autowired VisitRepository visitRepository;

      @Test
      void shouldGenerateSummaryAfterVisitSave() throws Exception {
          Set<Integer> before = visitIdsForPet(OWNER_ID, PET_ID);

          mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new",
                          OWNER_ID, PET_ID)
                  .param("date", "2026-06-01")
                  .param("description", "Dog is limping on left front leg"))
              .andExpect(status().is3xxRedirection());

          Integer visitId = newVisitId(OWNER_ID, PET_ID, before);

          await()
              .atMost(5, TimeUnit.SECONDS)
              .pollInterval(200, TimeUnit.MILLISECONDS)
              .until(() -> visitRepository.findById(visitId)
                  .map(v -> v.getAiStatus() == AiStatus.DONE)
                  .orElse(false));

          Visit done = visitRepository.findById(visitId).orElseThrow();
          assertThat(done.getAiStatus()).isEqualTo(AiStatus.DONE);
          assertThat(done.getAiSummary()).isNotBlank();
          assertThat(done.getAiTags()).isNotBlank();
          assertThat(done.getAiUrgency()).isNotBlank();

          mockMvc.perform(get("/visits/{id}/summary", visitId))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.status").value("DONE"))
              .andExpect(jsonPath("$.summary").isNotEmpty())
              .andExpect(jsonPath("$.tags").isArray())
              .andExpect(jsonPath("$.tags").isNotEmpty())
              .andExpect(jsonPath("$.urgency").isNotEmpty());
      }

      @ParameterizedTest
      @CsvSource({
          "Dog is limping badly,        urgent",
          "Annual checkup looks great,  routine"
      })
      void shouldMapDescriptionKeywordToUrgency(String description,
                                                String expectedUrgency)
              throws Exception {
          Set<Integer> before = visitIdsForPet(OWNER_ID, PET_ID);

          mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new",
                          OWNER_ID, PET_ID)
                  .param("date", "2026-06-02")
                  .param("description", description.trim()))
              .andExpect(status().is3xxRedirection());

          Integer visitId = newVisitId(OWNER_ID, PET_ID, before);

          await()
              .atMost(5, TimeUnit.SECONDS)
              .pollInterval(200, TimeUnit.MILLISECONDS)
              .until(() -> visitRepository.findById(visitId)
                  .map(v -> v.getAiStatus() == AiStatus.DONE)
                  .orElse(false));

          mockMvc.perform(get("/visits/{id}/summary", visitId))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.urgency").value(expectedUrgency));
      }

      // --- helpers ---

      private Set<Integer> visitIdsForPet(int ownerId, int petId) {
          return ownerRepository.findById(ownerId).orElseThrow()
              .getPets().stream()
              .filter(p -> p.getId() == petId)
              .flatMap(p -> p.getVisits().stream())
              .map(Visit::getId)
              .collect(Collectors.toSet());
      }

      private Integer newVisitId(int ownerId, int petId, Set<Integer> before) {
          return ownerRepository.findById(ownerId).orElseThrow()
              .getPets().stream()
              .filter(p -> p.getId() == petId)
              .flatMap(p -> p.getVisits().stream())
              .map(Visit::getId)
              .filter(id -> !before.contains(id))
              .findFirst()
              .orElseThrow(() -> new AssertionError("New visit not found in DB after POST"));
      }
  }
  ```

- Run the new tests and capture output:

  ```bash
  ./mvnw test -Dtest=VisitSummaryHappyPathIT
  ```

  Expected: BUILD SUCCESS, 3 tests pass (`shouldGenerateSummaryAfterVisitSave`,
  `shouldMapDescriptionKeywordToUrgency[1]`,
  `shouldMapDescriptionKeywordToUrgency[2]`).

- Run the annotation greps and capture output:

  ```bash
  grep -c "@SpringBootTest"     src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
  grep -c "@AutoConfigureMockMvc" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
  grep -c "@DisabledInNativeImage" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
  grep -c "@DisabledInAotMode"    src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java
  ! grep -q "@Transactional"      src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryHappyPathIT.java && echo "no @Transactional"
  ```

  Expected: `1 / 1 / 1 / 1 / "no @Transactional"`.

- Record all output in `22-proofs/22-task-02-proofs.md`.

**Proof:** `22-proofs/22-task-02-proofs.md`

---

## Task 03 — Create VisitSummaryFailureIT

Covers: AC-5.a, AC-5.b, AC-5.c, AC-5.d, AC-6.a, AC-6.b

- Create the file
  `src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java`
  with the content below:

  ```java
  package org.springframework.samples.petclinic.owner;

  import static org.awaitility.Awaitility.await;
  import static org.mockito.ArgumentMatchers.any;
  import static org.mockito.BDDMockito.given;
  import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
  import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
  import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
  import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

  import java.util.Set;
  import java.util.concurrent.TimeUnit;
  import java.util.stream.Collectors;

  import org.junit.jupiter.api.Test;
  import org.junit.jupiter.api.condition.DisabledInNativeImage;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
  import org.springframework.boot.test.context.SpringBootTest;
  import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
  import org.springframework.test.context.aot.DisabledInAotMode;
  import org.springframework.test.context.bean.override.mockito.MockitoBean;
  import org.springframework.test.web.servlet.MockMvc;

  @SpringBootTest(webEnvironment = WebEnvironment.MOCK)
  @AutoConfigureMockMvc
  @DisabledInNativeImage
  @DisabledInAotMode
  class VisitSummaryFailureIT {

      private static final int OWNER_ID = 6;
      private static final int PET_ID   = 7;

      @Autowired MockMvc mockMvc;
      @Autowired OwnerRepository ownerRepository;
      @Autowired VisitRepository visitRepository;

      @MockitoBean
      ClaudeApiClient claudeApiClient;

      @Test
      void shouldMarkVisitFailedWhenClientThrows() throws Exception {
          given(claudeApiClient.complete(any(), any()))
              .willThrow(new ClaudeApiException("rate limited"));

          Set<Integer> before = visitIdsForPet(OWNER_ID, PET_ID);

          mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new",
                          OWNER_ID, PET_ID)
                  .param("date", "2026-06-03")
                  .param("description", "Routine check"))
              .andExpect(status().is3xxRedirection());

          Integer visitId = newVisitId(OWNER_ID, PET_ID, before);

          await()
              .atMost(5, TimeUnit.SECONDS)
              .pollInterval(200, TimeUnit.MILLISECONDS)
              .until(() -> visitRepository.findById(visitId)
                  .map(v -> v.getAiStatus() == AiStatus.FAILED)
                  .orElse(false));

          mockMvc.perform(get("/visits/{id}/summary", visitId))
              .andExpect(status().isOk())
              .andExpect(content().json("{\"status\":\"FAILED\"}"));
      }

      // --- helpers ---

      private Set<Integer> visitIdsForPet(int ownerId, int petId) {
          return ownerRepository.findById(ownerId).orElseThrow()
              .getPets().stream()
              .filter(p -> p.getId() == petId)
              .flatMap(p -> p.getVisits().stream())
              .map(Visit::getId)
              .collect(Collectors.toSet());
      }

      private Integer newVisitId(int ownerId, int petId, Set<Integer> before) {
          return ownerRepository.findById(ownerId).orElseThrow()
              .getPets().stream()
              .filter(p -> p.getId() == petId)
              .flatMap(p -> p.getVisits().stream())
              .map(Visit::getId)
              .filter(id -> !before.contains(id))
              .findFirst()
              .orElseThrow(() -> new AssertionError("New visit not found in DB after POST"));
      }
  }
  ```

  > **Note on `@MockitoBean` import**: Spring Boot 3.4+ moved this annotation
  > to `org.springframework.test.context.bean.override.mockito.MockitoBean`.
  > If the project is on an older Boot version that uses
  > `org.springframework.boot.test.mock.mockito.MockitoBean`, adjust the
  > import accordingly.

- Run the new test and capture output:

  ```bash
  ./mvnw test -Dtest=VisitSummaryFailureIT
  ```

  Expected: BUILD SUCCESS, 1 test passes (`shouldMarkVisitFailedWhenClientThrows`).

- Run the annotation greps and capture output:

  ```bash
  grep -c "@SpringBootTest"        src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
  grep -c "@AutoConfigureMockMvc"  src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
  grep -c "@DisabledInNativeImage" src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
  grep -c "@DisabledInAotMode"     src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
  grep -c "@MockitoBean"           src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java
  ! grep -q "@Transactional"       src/test/java/org/springframework/samples/petclinic/owner/VisitSummaryFailureIT.java && echo "no @Transactional"
  ```

  Expected: `1 / 1 / 1 / 1 / 1 / "no @Transactional"`.

- Record all output in `22-proofs/22-task-03-proofs.md`.

**Proof:** `22-proofs/22-task-03-proofs.md`

---

## Task 04 — Validate and capture proof artifacts

Covers: AC-7.a, all

- Run the full test suite and capture the complete output:

  ```bash
  ./mvnw test
  ```

  Expected: BUILD SUCCESS, zero test failures, zero errors.

- Confirm every row in the coverage matrix in
  `22-validation-visit-summary-integration-test.md` has been updated to `PASS`.

- Confirm all proof files contain real command output (no placeholders).

- Record the full `./mvnw test` output in `22-proofs/22-task-04-proofs.md`.

**Proof:** `22-proofs/22-task-04-proofs.md`
