package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisitAiFieldsTest {

	@Test
	void newVisitDefaultsAiStatusToPending() {
		assertThat(new Visit().getAiStatus()).isEqualTo(AiStatus.PENDING);
	}

	@Test
	void newVisitNullableAiFieldsAreNull() {
		Visit visit = new Visit();
		assertThat(visit.getAiSummary()).isNull();
		assertThat(visit.getAiTags()).isNull();
		assertThat(visit.getAiUrgency()).isNull();
		assertThat(visit.getAiFollowUp()).isNull();
	}

	@Test
	void aiStatusSetterGetterRoundTrip() {
		Visit visit = new Visit();
		visit.setAiStatus(AiStatus.DONE);
		assertThat(visit.getAiStatus()).isEqualTo(AiStatus.DONE);
	}

	@Test
	void stringAiFieldSetterGetterRoundTrips() {
		Visit visit = new Visit();
		visit.setAiSummary("test summary");
		visit.setAiTags("tag1,tag2");
		visit.setAiUrgency("URGENT");
		visit.setAiFollowUp("Follow up");

		assertThat(visit.getAiSummary()).isEqualTo("test summary");
		assertThat(visit.getAiTags()).isEqualTo("tag1,tag2");
		assertThat(visit.getAiUrgency()).isEqualTo("URGENT");
		assertThat(visit.getAiFollowUp()).isEqualTo("Follow up");
	}

}
