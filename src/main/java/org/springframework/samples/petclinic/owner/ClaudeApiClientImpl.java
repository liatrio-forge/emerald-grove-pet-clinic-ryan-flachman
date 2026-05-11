package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@ConditionalOnExpression("not '${anthropic.api.key:}'.empty")
class ClaudeApiClientImpl implements ClaudeApiClient {

	private final RestClient restClient;

	private final String apiKey;

	private final String model;

	ClaudeApiClientImpl(RestClient.Builder restClientBuilder, @Value("${anthropic.api.url}") String apiUrl,
			@Value("${anthropic.api.key}") String apiKey, @Value("${anthropic.model}") String model) {
		this.apiKey = apiKey;
		this.model = model;
		this.restClient = restClientBuilder.baseUrl(apiUrl)
			.defaultHeader("x-api-key", this.apiKey)
			.defaultHeader("anthropic-version", "2023-06-01")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@Override
	public String complete(String systemPrompt, String userMessage) {
		ClaudeRequest request = new ClaudeRequest(this.model, 1024, systemPrompt,
				List.of(new Message("user", userMessage)));
		try {
			ClaudeResponse response = this.restClient.post()
				.uri("")
				.body(request)
				.retrieve()
				.body(ClaudeResponse.class);
			if (response == null || response.content() == null || response.content().isEmpty()) {
				throw new ClaudeApiException("Claude returned empty content");
			}
			return response.content().get(0).text();
		}
		catch (RestClientResponseException ex) {
			throw new ClaudeApiException("Claude API error: HTTP " + ex.getStatusCode(), ex);
		}
		catch (RestClientException ex) {
			throw new ClaudeApiException("Claude API transport error: " + ex.getMessage(), ex);
		}
	}

}
