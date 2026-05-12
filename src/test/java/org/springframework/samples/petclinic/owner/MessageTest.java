package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

	@Test
	void shouldStoreRoleAndContent() {
		Message message = new Message("user", "hello");

		assertThat(message.role()).isEqualTo("user");
		assertThat(message.content()).isEqualTo("hello");
	}

}
