package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiStatusTest {

	@Test
	void shouldDeclareExactlyFourValues() {
		assertThat(AiStatus.values()).hasSize(4);
	}

	@Test
	void shouldDeclareValuesInOrder() {
		AiStatus[] values = AiStatus.values();
		assertThat(values[0]).isEqualTo(AiStatus.PENDING);
		assertThat(values[1]).isEqualTo(AiStatus.PROCESSING);
		assertThat(values[2]).isEqualTo(AiStatus.DONE);
		assertThat(values[3]).isEqualTo(AiStatus.FAILED);
	}

	@Test
	void shouldResolveValueOfPending() {
		assertThat(AiStatus.valueOf("PENDING")).isEqualTo(AiStatus.PENDING);
	}

}
