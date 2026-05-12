package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PromptRequestTest {

	@Test
	void shouldStoreSystemPromptAndUserMessage() {
		PromptRequest request = new PromptRequest("system text", "user text");

		assertThat(request.systemPrompt()).isEqualTo("system text");
		assertThat(request.userMessage()).isEqualTo("user text");
	}

}
