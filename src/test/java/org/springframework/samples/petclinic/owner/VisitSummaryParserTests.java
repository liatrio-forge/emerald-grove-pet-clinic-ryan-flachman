package org.springframework.samples.petclinic.owner;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisitSummaryParserTests {

	private final VisitSummaryParser parser = new VisitSummaryParser();

	@Test
	void parseHappyPath() {
		String json = """
				{"summary":"Routine follow-up advised.","tags":["bloodwork","labs"],"urgency":"MONITOR","follow_up":"Repeat labs in one week"}
				""";

		VisitSummary result = parser.parse(json);

		assertThat(result.summary()).isNotNull().isEqualTo("Routine follow-up advised.");
		assertThat(result.tags()).isNotEmpty().hasSize(2);
		assertThat(result.urgency()).isEqualTo(VisitUrgency.MONITOR);
		assertThat(result.followUp()).isNotNull().isEqualTo("Repeat labs in one week");
	}

	@Test
	void parseMissingFollowUpReturnsNull() {
		String json = """
				{"summary":"Brief note","tags":["x"],"urgency":"ROUTINE","follow_up":null}
				""";

		VisitSummary result = parser.parse(json);

		assertThat(result.followUp()).isNull();
	}

	@Test
	void parseUnknownUrgencyDefaultsToRoutine() {
		String json = """
				{"summary":"Brief note","tags":["tag"],"urgency":"CATASTROPHIC","follow_up":"none"}
				""";

		VisitSummary result = parser.parse(json);

		assertThat(result.urgency()).isEqualTo(VisitUrgency.ROUTINE);
	}

	@Test
	void parseMalformedJsonThrowsParseException() {
		assertThatThrownBy(() -> parser.parse("not valid json")).isInstanceOf(VisitSummaryParseException.class);
	}

	@Test
	void parseEmptyTagsReturnsEmptyList() {
		String json = """
				{"summary":"x","tags":[],"urgency":"ROUTINE","follow_up":null}
				""";

		VisitSummary result = parser.parse(json);

		assertThat(result.tags()).isEmpty();
	}

	@Test
	void parseSingleTag() {
		String json = """
				{"summary":"x","tags":["diabetes"],"urgency":"ROUTINE","follow_up":null}
				""";

		assertThat(parser.parse(json).tags()).containsExactly("diabetes");
	}

	@Test
	void parseMultipleTags() {
		String json = """
				{"summary":"x","tags":["a","b","c"],"urgency":"ROUTINE","follow_up":null}
				""";

		assertThat(parser.parse(json).tags()).containsExactlyElementsOf(Arrays.asList("a", "b", "c"));
	}

	@Test
	void parseMissingUrgencyKeyDefaultsToRoutine() {
		String json = """
				{"summary":"x","tags":["a"],"follow_up":null}
				""";

		assertThat(parser.parse(json).urgency()).isEqualTo(VisitUrgency.ROUTINE);
	}

	@Test
	void parseBlankUrgencyDefaultsToRoutine() {
		String json = """
				{"summary":"x","tags":["a"],"urgency":"   ","follow_up":null}
				""";

		assertThat(parser.parse(json).urgency()).isEqualTo(VisitUrgency.ROUTINE);
	}

	@Test
	void parseOmitsFollowUpKeyReturnsNull() {
		String json = """
				{"summary":"x","tags":["a"],"urgency":"ROUTINE"}
				""";

		assertThat(parser.parse(json).followUp()).isNull();
	}

	@Test
	void parseNonArrayTagsThrowsParseException() {
		String json = """
				{"summary":"x","tags":"not-an-array","urgency":"ROUTINE","follow_up":null}
				""";

		assertThatThrownBy(() -> parser.parse(json)).isInstanceOf(VisitSummaryParseException.class)
			.hasMessageContaining("tags");
	}

}
