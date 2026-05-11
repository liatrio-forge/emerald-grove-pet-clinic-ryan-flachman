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

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Deterministic {@link ClaudeApiClient} used when no Anthropic API key is configured.
 */
@Component
@ConditionalOnExpression("'${anthropic.api.key:}'.trim().isEmpty()")
public class ClaudeApiClientStub implements ClaudeApiClient {

	private static final String URGENT_LIMP_JSON = """
			{"summary":"Orthopedic concern: limping noted; expedited evaluation recommended.","tags":["limping","orthopedic","urgent"],"urgency":"URGENT","follow_up":null}
			"""
		.trim();

	private static final String URGENT_PAIN_JSON = """
			{"summary":"Pain or discomfort reported; prompt clinical attention advised.","tags":["pain","discomfort","urgent"],"urgency":"URGENT","follow_up":null}
			"""
		.trim();

	private static final String ROUTINE_JSON = """
			{"summary":"Routine wellness or preventive visit; standard scheduling.","tags":["checkup","annual","routine"],"urgency":"ROUTINE","follow_up":null}
			"""
		.trim();

	private static final String MONITOR_JSON = """
			{"summary":"General presentation without matched routing keywords; monitor and reassess.","tags":["general","monitor"],"urgency":"MONITOR","follow_up":null}
			"""
		.trim();

	@Override
	public String complete(String systemPrompt, String userMessage) {
		// systemPrompt is intentionally ignored by the stub
		String normalized = userMessage == null ? "" : userMessage.toLowerCase();
		if (normalized.contains("limp")) {
			return URGENT_LIMP_JSON;
		}
		if (normalized.contains("pain")) {
			return URGENT_PAIN_JSON;
		}
		if (normalized.contains("checkup")) {
			return ROUTINE_JSON;
		}
		return MONITOR_JSON;
	}

}
