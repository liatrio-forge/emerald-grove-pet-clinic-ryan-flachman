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

import java.util.List;

/**
 * Structured result of parsing Claude JSON for a visit summary.
 *
 * @param summary narrative summary text
 * @param tags tag strings; never null
 * @param urgency mapped urgency level
 * @param followUp optional follow-up instructions; may be null
 */
public record VisitSummary(String summary, List<String> tags, VisitUrgency urgency, String followUp) {

	public VisitSummary {
		tags = List.copyOf(tags);
	}

}
