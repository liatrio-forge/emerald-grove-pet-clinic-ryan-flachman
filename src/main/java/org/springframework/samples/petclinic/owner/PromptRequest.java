package org.springframework.samples.petclinic.owner;

public record PromptRequest(String systemPrompt, String userMessage) {
}
