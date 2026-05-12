package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisitUrgencyTest {

	@Test
	void shouldHaveExactlyThreeValues() {
		assertThat(VisitUrgency.values()).hasSize(3);
	}

	@Test
	void shouldDeclareValuesInOrder() {
		VisitUrgency[] values = VisitUrgency.values();
		assertThat(values[0]).isEqualTo(VisitUrgency.ROUTINE);
		assertThat(values[1]).isEqualTo(VisitUrgency.MONITOR);
		assertThat(values[2]).isEqualTo(VisitUrgency.URGENT);
	}

	@Test
	void shouldResolveAllThreeValues() {
		assertThat(VisitUrgency.valueOf("ROUTINE")).isEqualTo(VisitUrgency.ROUTINE);
		assertThat(VisitUrgency.valueOf("MONITOR")).isEqualTo(VisitUrgency.MONITOR);
		assertThat(VisitUrgency.valueOf("URGENT")).isEqualTo(VisitUrgency.URGENT);
	}

}
