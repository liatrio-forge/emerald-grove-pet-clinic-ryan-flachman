package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ClaudeApiClientImpl.class)
@TestPropertySource(properties = { "anthropic.api.key=test-key", "anthropic.api.url=http://localhost/v1/messages",
		"anthropic.model=claude-haiku-4-5-20251001" })
@DisabledInNativeImage
@DisabledInAotMode
class ClaudeApiClientImplTest {

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private ClaudeApiClientImpl client;

	@BeforeEach
	void setUp() {
		this.server.reset();
	}

	@Test
	void completeSendsCorrectRequestAndReturnsText() {
		this.server.expect(requestTo("http://localhost/v1/messages"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(header("x-api-key", "test-key"))
			.andExpect(header("anthropic-version", "2023-06-01"))
			.andExpect(header("Content-Type", "application/json"))
			.andExpect(content().json(
					"{\"model\":\"claude-haiku-4-5-20251001\",\"max_tokens\":1024,\"system\":\"sys\",\"messages\":[{\"role\":\"user\",\"content\":\"hello\"}]}"))
			.andRespond(withSuccess(
					"{\"id\":\"1\",\"type\":\"message\",\"content\":[{\"type\":\"text\",\"text\":\"summary result\"}],\"stop_reason\":\"end_turn\"}",
					MediaType.APPLICATION_JSON));

		assertThat(this.client.complete("sys", "hello")).isEqualTo("summary result");
	}

	@Test
	void completeThrowsWhenContentIsEmpty() {
		this.server.expect(requestTo("http://localhost/v1/messages"))
			.andExpect(method(HttpMethod.POST))
			.andRespond(withSuccess("{\"id\":\"1\",\"type\":\"message\",\"content\":[],\"stop_reason\":\"end_turn\"}",
					MediaType.APPLICATION_JSON));

		assertThatThrownBy(() -> this.client.complete("sys", "hello")).isInstanceOf(ClaudeApiException.class);
	}

	@Test
	void completeThrowsOnRateLimitResponse() {
		this.server.expect(requestTo("http://localhost/v1/messages"))
			.andExpect(method(HttpMethod.POST))
			.andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

		assertThatThrownBy(() -> this.client.complete("sys", "hello")).isInstanceOf(ClaudeApiException.class);
	}

	@Test
	void completeThrowsOnServiceUnavailableResponse() {
		this.server.expect(requestTo("http://localhost/v1/messages"))
			.andExpect(method(HttpMethod.POST))
			.andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

		assertThatThrownBy(() -> this.client.complete("sys", "hello")).isInstanceOf(ClaudeApiException.class);
	}

}
