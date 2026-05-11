package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClaudeResponseTest {

	@Test
	void shouldStoreAllComponents() {
		ClaudeResponse response = new ClaudeResponse("msg_01", "message",
				List.of(new ContentBlock("text", "summary text")), "end_turn");

		assertThat(response.id()).isEqualTo("msg_01");
		assertThat(response.type()).isEqualTo("message");
		assertThat(response.content()).hasSize(1);
		assertThat(response.content().get(0).type()).isEqualTo("text");
		assertThat(response.content().get(0).text()).isEqualTo("summary text");
		assertThat(response.stopReason()).isEqualTo("end_turn");
	}

}
