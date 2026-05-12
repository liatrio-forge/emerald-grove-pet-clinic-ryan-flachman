package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.test.context.aot.DisabledInAotMode;

import static org.assertj.core.api.Assertions.assertThat;

@DisabledInNativeImage
@DisabledInAotMode
class ClaudeApiExceptionTest {

	@Test
	void singleArgConstructorSetsMessage() {
		assertThat(new ClaudeApiException("boom").getMessage()).isEqualTo("boom");
	}

	@Test
	void twoArgConstructorSetsMessageAndCause() {
		RuntimeException cause = new RuntimeException("cause");
		ClaudeApiException ex = new ClaudeApiException("boom", cause);

		assertThat(ex.getMessage()).isEqualTo("boom");
		assertThat(ex.getCause()).isSameAs(cause);
		assertThat(ex.getCause().getMessage()).isEqualTo("cause");
	}

}
