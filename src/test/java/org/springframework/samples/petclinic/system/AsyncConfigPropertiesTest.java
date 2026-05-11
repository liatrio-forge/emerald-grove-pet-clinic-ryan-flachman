package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AsyncConfigPropertiesTest {

	@Autowired
	private Environment environment;

	@Test
	void anthropicApiKeyPropertyIsRegistered() {
		assertThat(environment.containsProperty("anthropic.api.key")).isTrue();
	}

	@Test
	void anthropicApiUrlPropertyIsRegistered() {
		assertThat(environment.containsProperty("anthropic.api.url")).isTrue();
	}

	@Test
	void anthropicModelPropertyIsRegistered() {
		assertThat(environment.containsProperty("anthropic.model")).isTrue();
	}

}
