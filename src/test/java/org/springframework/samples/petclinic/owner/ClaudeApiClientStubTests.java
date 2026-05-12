/*
 * Copyright 2012-2024 the original author or authors.
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

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ClaudeApiClientStub}.
 */
@ExtendWith(MockitoExtension.class)
@DisabledInNativeImage
public class ClaudeApiClientStubTests {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private ClaudeApiClientStub stub;

	@BeforeEach
	void setUp() {
		stub = new ClaudeApiClientStub();
	}

	@Test
	void completeWithLimpReturnsUrgentJson() throws Exception {
		String result = stub.complete("", "Dog is limping on left front leg");
		JsonNode root = assertValidCannedJson(result);
		assertEquals("URGENT", root.get("urgency").asText());
	}

	@Test
	void completeWithPainReturnsUrgentJson() throws Exception {
		String result = stub.complete("", "Cat seems to be in pain after eating");
		JsonNode root = assertValidCannedJson(result);
		assertEquals("URGENT", root.get("urgency").asText());
	}

	@Test
	void completeWithCheckupReturnsRoutineJson() throws Exception {
		String result = stub.complete("", "Annual checkup for Max");
		JsonNode root = assertValidCannedJson(result);
		assertEquals("ROUTINE", root.get("urgency").asText());
	}

	@Test
	void completeWithUnknownKeywordReturnsMonitorJson() throws Exception {
		String result = stub.complete("", "Seems a bit tired lately");
		JsonNode root = assertValidCannedJson(result);
		assertEquals("MONITOR", root.get("urgency").asText());
	}

	@Test
	void completeWithBothLimpAndCheckupReturnsUrgent() throws Exception {
		String result = stub.complete("", "Annual checkup; dog is limping");
		JsonNode root = assertValidCannedJson(result);
		assertEquals("URGENT", root.get("urgency").asText());
	}

	@Test
	void completeIsCaseInsensitive() throws Exception {
		String result = stub.complete("", "DOG IS LIMPING");
		JsonNode root = assertValidCannedJson(result);
		assertEquals("URGENT", root.get("urgency").asText());
	}

	@Test
	void completeWithBlankMessageReturnsMonitorJson() throws Exception {
		String result = stub.complete("", "");
		JsonNode root = assertValidCannedJson(result);
		assertEquals("MONITOR", root.get("urgency").asText());
	}

	@Test
	void completeWithNullMessageReturnsMonitorJson() throws Exception {
		String result = stub.complete("", null);
		JsonNode root = assertValidCannedJson(result);
		assertEquals("MONITOR", root.get("urgency").asText());
	}

	@Test
	void classDeclaresComponentAndConditionalOnBlankApiKey() {
		assertTrue(ClaudeApiClientStub.class.isAnnotationPresent(Component.class));
		ConditionalOnExpression conditional = ClaudeApiClientStub.class.getAnnotation(ConditionalOnExpression.class);
		assertNotNull(conditional);
		assertEquals("'${anthropic.api.key:}'.trim().isEmpty()", conditional.value());
	}

	private static JsonNode assertValidCannedJson(String result) throws Exception {
		JsonNode root = MAPPER.readTree(result);
		assertNotNull(root.get("summary"), "summary node must be present");
		assertNotNull(root.get("tags"), "tags node must be present");
		assertNotNull(root.get("urgency"), "urgency node must be present");
		assertTrue(root.has("follow_up"), "follow_up node must be present (may be null)");
		JsonNode tags = root.get("tags");
		assertTrue(tags.isArray(), "tags must be a JSON array");
		assertTrue(tags.size() >= 1, "tags must have at least one element");
		return root;
	}

}
