package org.springframework.samples.petclinic.owner;

import java.util.List;
import java.util.Objects;

public record VisitSummary(String summary, List<String> tags, VisitUrgency urgency, String followUp) {

	public VisitSummary {
		summary = Objects.requireNonNull(summary, "summary must not be null");
		tags = Objects.requireNonNull(tags, "tags must not be null");
		urgency = Objects.requireNonNull(urgency, "urgency must not be null");
		tags = List.copyOf(tags);
	}

}
