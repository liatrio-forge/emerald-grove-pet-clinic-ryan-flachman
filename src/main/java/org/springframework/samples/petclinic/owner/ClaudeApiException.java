package org.springframework.samples.petclinic.owner;

class ClaudeApiException extends RuntimeException {

	ClaudeApiException(String message) {
		super(message);
	}

	ClaudeApiException(String message, Throwable cause) {
		super(message, cause);
	}

}
