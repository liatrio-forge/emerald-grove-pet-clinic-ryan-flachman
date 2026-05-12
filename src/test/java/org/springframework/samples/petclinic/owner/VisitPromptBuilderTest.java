package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class VisitPromptBuilderTest {

	private static final LocalDate VISIT_DATE = LocalDate.of(2026, 5, 11);

	private static Pet happyPathPet() {
		Pet pet = new Pet();
		pet.setName("Rex");
		PetType dog = new PetType();
		dog.setName("Dog");
		pet.setType(dog);
		pet.setBirthDate(LocalDate.of(2023, 5, 11));
		return pet;
	}

	private static Visit happyPathVisit() {
		Visit visit = new Visit();
		visit.setDate(VISIT_DATE);
		visit.setDescription("Annual checkup, vaccinations updated.");
		return visit;
	}

	@Test
	void shouldReturnNonBlankSystemPrompt() {
		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), happyPathPet());

		assertThat(result.systemPrompt()).isNotNull().isNotBlank();
	}

	@Test
	void shouldMentionJsonFieldsInSystemPrompt() {
		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), happyPathPet());
		String system = result.systemPrompt();

		assertThat(system).containsIgnoringCase("summary");
		assertThat(system).containsIgnoringCase("tags");
		assertThat(system).containsIgnoringCase("urgency");
		assertThat(system).containsIgnoringCase("followUp");
	}

	@Test
	void shouldMentionJsonOutputInSystemPrompt() {
		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), happyPathPet());

		assertThat(result.systemPrompt()).containsIgnoringCase("json");
	}

	@Test
	void shouldIncludePetNameInUserMessage() {
		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), happyPathPet());

		assertThat(result.userMessage()).contains("Rex");
	}

	@Test
	void shouldIncludePetTypeInUserMessage() {
		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), happyPathPet());

		assertThat(result.userMessage()).contains("Dog");
	}

	@Test
	void shouldIncludeCorrectAgeInYearsInUserMessage() {
		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), happyPathPet());

		assertThat(result.userMessage()).contains("3");
	}

	@Test
	void shouldIncludeVisitDateIsoInUserMessage() {
		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), happyPathPet());

		assertThat(result.userMessage()).contains("2026-05-11");
	}

	@Test
	void shouldIncludeDescriptionInUserMessage() {
		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), happyPathPet());

		assertThat(result.userMessage()).contains("Annual checkup, vaccinations updated.");
	}

	@Test
	void shouldUseUnknownForNullBirthDate() {
		Pet pet = happyPathPet();
		pet.setBirthDate(null);

		assertThatCode(() -> VisitPromptBuilder.build(happyPathVisit(), pet)).doesNotThrowAnyException();

		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), pet);
		assertThat(result.userMessage()).contains("age unknown years");
	}

	@Test
	void shouldUsePlaceholderForNullDescription() {
		Visit visit = happyPathVisit();
		visit.setDescription(null);

		assertThatCode(() -> VisitPromptBuilder.build(visit, happyPathPet())).doesNotThrowAnyException();

		PromptRequest result = VisitPromptBuilder.build(visit, happyPathPet());
		assertThat(result.userMessage()).contains("(no description provided)");
	}

	@Test
	void shouldUsePlaceholderForBlankDescription() {
		Visit visit = happyPathVisit();
		visit.setDescription("   ");

		assertThatCode(() -> VisitPromptBuilder.build(visit, happyPathPet())).doesNotThrowAnyException();

		PromptRequest result = VisitPromptBuilder.build(visit, happyPathPet());
		assertThat(result.userMessage()).contains("(no description provided)");
	}

	@Test
	void shouldUseUnknownForNullPetType() {
		Pet pet = happyPathPet();
		pet.setType(null);

		assertThatCode(() -> VisitPromptBuilder.build(happyPathVisit(), pet)).doesNotThrowAnyException();

		PromptRequest result = VisitPromptBuilder.build(happyPathVisit(), pet);
		assertThat(result.userMessage()).contains("Rex, unknown, age");
	}

}
