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

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisabledInNativeImage
@DisabledInAotMode
class VisitSummaryFailureIT {

	private static final int OWNER_ID = 6;

	private static final int PET_ID = 7;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private VisitRepository visitRepository;

	@MockitoBean
	private ClaudeApiClient claudeApiClient;

	@Test
	void shouldMarkVisitFailedWhenClientThrows() throws Exception {
		given(claudeApiClient.complete(any(), any())).willThrow(new ClaudeApiException("rate limited"));

		Set<Integer> before = visitIdsForPet(OWNER_ID, PET_ID);

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", OWNER_ID, PET_ID).param("date", "2026-06-03")
				.param("description", "Routine check"))
			.andExpect(status().is3xxRedirection());

		Integer visitId = newVisitId(OWNER_ID, PET_ID, before);

		await().atMost(5, TimeUnit.SECONDS)
			.pollInterval(200, TimeUnit.MILLISECONDS)
			.until(() -> visitRepository.findById(visitId).map(v -> v.getAiStatus() == AiStatus.FAILED).orElse(false));

		mockMvc.perform(get("/visits/{id}/summary", visitId))
			.andExpect(status().isOk())
			.andExpect(content().json("{\"status\":\"FAILED\"}"));
	}

	private Set<Integer> visitIdsForPet(int ownerId, int petId) {
		return ownerRepository.findById(ownerId)
			.orElseThrow()
			.getPets()
			.stream()
			.filter(p -> p.getId() == petId)
			.flatMap(p -> p.getVisits().stream())
			.map(Visit::getId)
			.collect(Collectors.toSet());
	}

	private Integer newVisitId(int ownerId, int petId, Set<Integer> before) {
		return ownerRepository.findById(ownerId)
			.orElseThrow()
			.getPets()
			.stream()
			.filter(p -> p.getId() == petId)
			.flatMap(p -> p.getVisits().stream())
			.map(Visit::getId)
			.filter(id -> !before.contains(id))
			.findFirst()
			.orElseThrow(() -> new AssertionError("New visit not found in DB after POST"));
	}

}
