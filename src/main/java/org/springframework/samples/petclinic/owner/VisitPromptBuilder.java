package org.springframework.samples.petclinic.owner;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class VisitPromptBuilder {

	private static final DateTimeFormatter VISIT_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

	private static final String SYSTEM_PROMPT = """
			You are a clinical veterinary assistant. The user message describes one veterinary visit \
			for a specific pet. Respond with JSON only—no markdown fences, no explanations, and no \
			text before or after a single JSON object. The JSON must include these keys: "summary", \
			"tags", "urgency", and "follow_up".
			""".stripIndent().trim();

	private VisitPromptBuilder() {
	}

	public static PromptRequest build(Visit visit, Pet pet) {
		String age = resolveAgeYears(pet, visit);
		String typeName = resolveTypeName(pet.getType());
		String description = resolveDescription(visit.getDescription());
		String visitDate = visit.getDate() != null ? visit.getDate().format(VISIT_DATE) : "";
		String petName = pet.getName() != null ? pet.getName() : "";

		String userMessage = """
				Pet: %s, %s, age %s years
				Visit date: %s
				Visit notes: "%s"
				""".formatted(petName, typeName, age, visitDate, description).stripIndent();

		return new PromptRequest(SYSTEM_PROMPT, userMessage);
	}

	private static String resolveAgeYears(Pet pet, Visit visit) {
		if (pet.getBirthDate() == null || visit.getDate() == null) {
			return "unknown";
		}
		long years = ChronoUnit.YEARS.between(pet.getBirthDate(), visit.getDate());
		return String.valueOf(years);
	}

	private static String resolveTypeName(PetType type) {
		if (type == null) {
			return "unknown";
		}
		String name = type.getName();
		if (name == null || name.isBlank()) {
			return "unknown";
		}
		return name;
	}

	private static String resolveDescription(String raw) {
		if (raw == null || raw.isBlank()) {
			return "(no description provided)";
		}
		return raw;
	}

}
