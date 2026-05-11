package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClaudeRequestTest {

	@Test
	void shouldStoreAllComponents() {
		ClaudeRequest request = new ClaudeRequest("claude-haiku-4-5-20251001", 1024, "system text",
				List.of(new Message("user", "hello")));

		assertThat(request.model()).isEqualTo("claude-haiku-4-5-20251001");
		assertThat(request.maxTokens()).isEqualTo(1024);
		assertThat(request.system()).isEqualTo("system text");
		assertThat(request.messages()).hasSize(1);
		assertThat(request.messages().get(0).role()).isEqualTo("user");
		assertThat(request.messages().get(0).content()).isEqualTo("hello");
	}

}
