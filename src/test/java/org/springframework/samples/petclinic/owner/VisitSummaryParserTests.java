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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link VisitSummaryParser}.
 */
class VisitSummaryParserTests {

	private VisitSummaryParser parser;

	@BeforeEach
	void setUp() {
		this.parser = new VisitSummaryParser();
	}

	@Test
	void shouldParseAllFieldsFromValidJson() {
		String json = "{\"summary\":\"Annual checkup complete\",\"tags\":[\"wellness\",\"checkup\"],\"urgency\":\"routine\",\"followUp\":\"Return in 12 months\"}";
		VisitSummary result = this.parser.parse(json);
		assertThat(result.summary()).isEqualTo("Annual checkup complete");
		assertThat(result.tags()).containsExactly("wellness", "checkup");
		assertThat(result.urgency()).isEqualTo(VisitUrgency.ROUTINE);
		assertThat(result.followUp()).isEqualTo("Return in 12 months");
	}

	@Test
	void shouldReturnNullFollowUpWhenAbsent() {
		String json = "{\"summary\":\"Done\",\"tags\":[],\"urgency\":\"monitor\"}";
		VisitSummary result = this.parser.parse(json);
		assertThat(result.followUp()).isNull();
	}

	@Test
	void shouldMapUnknownUrgencyToRoutine() {
		String json = "{\"summary\":\"S\",\"tags\":[],\"urgency\":\"critical\"}";
		VisitSummary result = this.parser.parse(json);
		assertThat(result.urgency()).isEqualTo(VisitUrgency.ROUTINE);
	}

	@Test
	void shouldMapUrgencyCaseInsensitively() {
		String json = "{\"summary\":\"S\",\"tags\":[],\"urgency\":\"URGENT\"}";
		assertThat(this.parser.parse(json).urgency()).isEqualTo(VisitUrgency.URGENT);
	}

	@Test
	void shouldDefaultToRoutineWhenUrgencyKeyMissing() {
		String json = "{\"summary\":\"S\",\"tags\":[]}";
		assertThat(this.parser.parse(json).urgency()).isEqualTo(VisitUrgency.ROUTINE);
	}

	@Test
	void shouldThrowParseExceptionForMalformedJson() {
		assertThatThrownBy(() -> this.parser.parse("not json")).isInstanceOf(VisitSummaryParseException.class);
	}

	@Test
	void shouldHandleEmptyTagsArray() {
		String json = "{\"summary\":\"S\",\"tags\":[],\"urgency\":\"routine\"}";
		VisitSummary result = this.parser.parse(json);
		assertThat(result.tags()).isEmpty();
	}

	@Test
	void shouldHandleSingleTag() {
		String json = "{\"summary\":\"S\",\"tags\":[\"checkup\"],\"urgency\":\"routine\"}";
		VisitSummary result = this.parser.parse(json);
		assertThat(result.tags()).containsExactly("checkup");
	}

	@Test
	void shouldHandleMultipleTags() {
		String json = "{\"summary\":\"S\",\"tags\":[\"wellness\",\"checkup\",\"annual\"],\"urgency\":\"routine\"}";
		VisitSummary result = this.parser.parse(json);
		assertThat(result.tags()).containsExactly("wellness", "checkup", "annual");
	}

}
