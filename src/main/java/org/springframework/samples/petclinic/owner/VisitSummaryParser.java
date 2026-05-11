/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Parses Claude JSON payloads into {@link VisitSummary} instances.
 */
public class VisitSummaryParser {

	private static final Logger log = LoggerFactory.getLogger(VisitSummaryParser.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	public VisitSummary parse(String json) {
		try {
			JsonNode root = this.objectMapper.readTree(json);
			String summary = readSummary(root);
			List<String> tags = readTags(root);
			VisitUrgency urgency = mapUrgency(root);
			String followUp = readFollowUp(root);
			return new VisitSummary(summary, tags, urgency, followUp);
		}
		catch (JacksonException ex) {
			throw new VisitSummaryParseException("Malformed JSON for visit summary", ex);
		}
	}

	private static String readSummary(JsonNode root) {
		JsonNode n = root.get("summary");
		if (n != null && n.isTextual()) {
			return n.asText();
		}
		return "";
	}

	private static List<String> readTags(JsonNode root) {
		List<String> out = new ArrayList<>();
		JsonNode tagsNode = root.get("tags");
		if (tagsNode != null && tagsNode.isArray()) {
			for (JsonNode el : tagsNode) {
				if (el.isTextual()) {
					out.add(el.asText());
				}
			}
		}
		return out;
	}

	private static String readFollowUp(JsonNode root) {
		if (!root.has("followUp")) {
			return null;
		}
		JsonNode n = root.get("followUp");
		if (n == null || n.isNull()) {
			return null;
		}
		if (n.isTextual()) {
			return n.asText();
		}
		return null;
	}

	private VisitUrgency mapUrgency(JsonNode root) {
		JsonNode n = root.get("urgency");
		if (n == null || !n.isTextual()) {
			log.warn("Missing or non-textual urgency in visit summary JSON; defaulting to {}", VisitUrgency.ROUTINE);
			return VisitUrgency.ROUTINE;
		}
		String raw = n.asText().trim();
		if (raw.isEmpty()) {
			log.warn("Blank urgency in visit summary JSON; defaulting to {}", VisitUrgency.ROUTINE);
			return VisitUrgency.ROUTINE;
		}
		String key = raw.toLowerCase(Locale.ROOT);
		return switch (key) {
			case "routine" -> VisitUrgency.ROUTINE;
			case "monitor" -> VisitUrgency.MONITOR;
			case "urgent" -> VisitUrgency.URGENT;
			default -> {
				log.warn("Unknown urgency value \"{}\" in visit summary JSON; defaulting to {}", raw,
						VisitUrgency.ROUTINE);
				yield VisitUrgency.ROUTINE;
			}
		};
	}

}
