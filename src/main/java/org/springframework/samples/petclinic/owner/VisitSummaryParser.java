package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
public class VisitSummaryParser {

	private static final Logger log = LoggerFactory.getLogger(VisitSummaryParser.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public VisitSummary parse(String rawJson) {
		try {
			JsonNode root = MAPPER.readTree(rawJson);
			String summary = root.path("summary").asText();
			List<String> tags = extractTags(root);
			VisitUrgency urgency = parseUrgency(root.path("urgency").asText(null));
			String followUp = extractFollowUp(root);
			return new VisitSummary(summary, tags, urgency, followUp);
		}
		catch (VisitSummaryParseException e) {
			throw e;
		}
		catch (Exception e) {
			throw new VisitSummaryParseException("Failed to parse VisitSummary JSON", e);
		}
	}

	private static List<String> extractTags(JsonNode root) {
		JsonNode node = root.path("tags");
		if (!(node instanceof ArrayNode array)) {
			throw new VisitSummaryParseException("JSON field \"tags\" must be an array", null);
		}
		List<String> tags = new ArrayList<>(array.size());
		for (JsonNode elt : array) {
			tags.add(elt.asText());
		}
		return tags;
	}

	private VisitUrgency parseUrgency(String raw) {
		if (raw == null || raw.isBlank()) {
			log.warn("Urgency missing or blank in AI JSON — defaulting to ROUTINE");
			return VisitUrgency.ROUTINE;
		}
		try {
			return VisitUrgency.valueOf(raw.trim().toUpperCase());
		}
		catch (IllegalArgumentException e) {
			log.warn("Unknown urgency '{}' in AI JSON — defaulting to ROUTINE", raw.trim());
			return VisitUrgency.ROUTINE;
		}
	}

	private static String extractFollowUp(JsonNode root) {
		JsonNode node = root.get("follow_up");
		if (node == null || node.isNull()) {
			return null;
		}
		return node.asText();
	}

}
