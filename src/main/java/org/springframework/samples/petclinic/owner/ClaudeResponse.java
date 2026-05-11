package org.springframework.samples.petclinic.owner;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ClaudeResponse(String id, String type, List<ContentBlock> content,
		@JsonProperty("stop_reason") String stopReason) {
}
