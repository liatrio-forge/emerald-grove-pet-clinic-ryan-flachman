package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VisitSummaryTest {

	@Test
	void shouldConstructWithAllFields() {
		List<String> tags = List.of("a", "b");
		VisitSummary summary = new VisitSummary("summary text", tags, VisitUrgency.MONITOR, "follow up");

		assertThat(summary.summary()).isEqualTo("summary text");
		assertThat(summary.tags()).isEqualTo(tags);
		assertThat(summary.urgency()).isEqualTo(VisitUrgency.MONITOR);
		assertThat(summary.followUp()).isEqualTo("follow up");
	}

	@Test
	void shouldAllowNullFollowUp() {
		VisitSummary summary = new VisitSummary("s", List.of(), VisitUrgency.ROUTINE, null);

		assertThat(summary.followUp()).isNull();
	}

	@Test
	void shouldAllowEmptyTags() {
		VisitSummary summary = new VisitSummary("s", List.of(), VisitUrgency.ROUTINE, null);

		assertThat(summary.tags()).isEmpty();
	}

	@Test
	void shouldThrowWhenSummaryIsNull() {
		assertThrows(NullPointerException.class, () -> new VisitSummary(null, List.of(), VisitUrgency.ROUTINE, null));
	}

	@Test
	void shouldThrowWhenTagsIsNull() {
		assertThrows(NullPointerException.class, () -> new VisitSummary("s", null, VisitUrgency.ROUTINE, null));
	}

	@Test
	void shouldThrowWhenUrgencyIsNull() {
		assertThrows(NullPointerException.class, () -> new VisitSummary("s", List.of(), null, null));
	}

	@Test
	void shouldDefensivelyCopyTags() {
		List<String> mutable = new ArrayList<>();
		mutable.add("first");
		VisitSummary visitSummary = new VisitSummary("s", mutable, VisitUrgency.ROUTINE, null);

		mutable.add("second");

		assertThat(visitSummary.tags()).containsExactly("first");
	}

	@Test
	void shouldReturnUnmodifiableTags() {
		VisitSummary visitSummary = new VisitSummary("s", List.of("x"), VisitUrgency.ROUTINE, null);

		assertThrows(UnsupportedOperationException.class, () -> visitSummary.tags().add("y"));
	}

}
