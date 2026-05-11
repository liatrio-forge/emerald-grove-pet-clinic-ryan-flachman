package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentBlockTest {

	@Test
	void shouldStoreTypeAndText() {
		ContentBlock block = new ContentBlock("text", "hello");

		assertThat(block.type()).isEqualTo("text");
		assertThat(block.text()).isEqualTo("hello");
	}

}
