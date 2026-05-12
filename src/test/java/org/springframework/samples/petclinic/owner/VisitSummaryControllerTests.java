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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web layer tests for {@link VisitSummaryController}.
 */
@WebMvcTest(VisitSummaryController.class)
@DisabledInNativeImage
@DisabledInAotMode
class VisitSummaryControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VisitRepository visitRepository;

	@Test
	void getSummaryReturnsPendingWhenStatusIsPending() throws Exception {
		Visit visit = new Visit();
		visit.setId(1);
		visit.setAiStatus(AiStatus.PENDING);
		given(this.visitRepository.findById(1)).willReturn(Optional.of(visit));

		this.mockMvc.perform(get("/visits/1/summary"))
			.andExpect(status().isOk())
			.andExpect(content().json("{\"status\":\"PENDING\"}", true));
	}

	@Test
	void getSummaryReturnsPendingWhenStatusIsProcessing() throws Exception {
		Visit visit = new Visit();
		visit.setId(1);
		visit.setAiStatus(AiStatus.PROCESSING);
		given(this.visitRepository.findById(1)).willReturn(Optional.of(visit));

		this.mockMvc.perform(get("/visits/1/summary"))
			.andExpect(status().isOk())
			.andExpect(content().json("{\"status\":\"PENDING\"}", true));
	}

	@Test
	void getSummaryReturnsDoneResponse() throws Exception {
		Visit visit = new Visit();
		visit.setId(1);
		visit.setAiStatus(AiStatus.DONE);
		visit.setAiSummary("Routine check");
		visit.setAiTags("wellness,annual");
		visit.setAiUrgency("ROUTINE");
		visit.setAiFollowUp("Return in 12 months");
		given(this.visitRepository.findById(1)).willReturn(Optional.of(visit));

		this.mockMvc.perform(get("/visits/1/summary"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("DONE"))
			.andExpect(jsonPath("$.summary").value("Routine check"))
			.andExpect(jsonPath("$.tags.length()").value(2))
			.andExpect(jsonPath("$.tags[0]").value("wellness"))
			.andExpect(jsonPath("$.tags[1]").value("annual"))
			.andExpect(jsonPath("$.urgency").value("routine"))
			.andExpect(jsonPath("$.followUp").value("Return in 12 months"));
	}

	@Test
	void getSummaryReturnsDoneResponseWithNullFollowUp() throws Exception {
		Visit visit = new Visit();
		visit.setId(1);
		visit.setAiStatus(AiStatus.DONE);
		visit.setAiSummary("S");
		visit.setAiTags("limp,pain");
		visit.setAiUrgency("MONITOR");
		visit.setAiFollowUp(null);
		given(this.visitRepository.findById(1)).willReturn(Optional.of(visit));

		this.mockMvc.perform(get("/visits/1/summary"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("DONE"))
			.andExpect(jsonPath("$.followUp").doesNotExist());
	}

	static Stream<Arguments> blankOrNullTags() {
		return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("   "));
	}

	@ParameterizedTest
	@MethodSource("blankOrNullTags")
	void getSummaryReturnsDoneResponseWithEmptyTags(String aiTags) throws Exception {
		Visit visit = new Visit();
		visit.setId(1);
		visit.setAiStatus(AiStatus.DONE);
		visit.setAiSummary("x");
		visit.setAiTags(aiTags);
		visit.setAiUrgency("URGENT");
		given(this.visitRepository.findById(1)).willReturn(Optional.of(visit));

		this.mockMvc.perform(get("/visits/1/summary"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.tags").isArray())
			.andExpect(jsonPath("$.tags.length()").value(0));
	}

	@Test
	void getSummaryReturnsFailedWhenStatusIsFailed() throws Exception {
		Visit visit = new Visit();
		visit.setId(1);
		visit.setAiStatus(AiStatus.FAILED);
		given(this.visitRepository.findById(1)).willReturn(Optional.of(visit));

		this.mockMvc.perform(get("/visits/1/summary"))
			.andExpect(status().isOk())
			.andExpect(content().json("{\"status\":\"FAILED\"}", true));
	}

	@Test
	void getSummaryReturns404WhenVisitNotFound() throws Exception {
		given(this.visitRepository.findById(999)).willReturn(Optional.empty());

		this.mockMvc.perform(get("/visits/999/summary")).andExpect(status().isNotFound());
	}

}
