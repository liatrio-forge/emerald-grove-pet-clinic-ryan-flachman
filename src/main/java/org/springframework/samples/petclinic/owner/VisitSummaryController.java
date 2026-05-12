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
import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint for polling AI visit summary generation status.
 */
@RestController
class VisitSummaryController {

	/** Persistence access for visit AI fields. */
	private final VisitRepository visitRepository;

	VisitSummaryController(final VisitRepository repository) {
		this.visitRepository = repository;
	}

	@GetMapping("/visits/{visitId}/summary")
	public ResponseEntity<VisitSummaryResponse> getSummary(@PathVariable final Integer visitId) {
		return this.visitRepository.findById(visitId)
			.map(this::toResponse)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	private VisitSummaryResponse toResponse(final Visit visit) {
		AiStatus status = visit.getAiStatus();
		if (status == AiStatus.DONE) {
			List<String> tags = emptyTagsIfBlank(visit.getAiTags());
			String urgency = visit.getAiUrgency() != null ? visit.getAiUrgency().toLowerCase(Locale.ROOT) : null;
			return new VisitSummaryResponse("DONE", visit.getAiSummary(), tags, urgency, visit.getAiFollowUp());
		}
		if (status == AiStatus.FAILED) {
			return new VisitSummaryResponse("FAILED", null, null, null, null);
		}
		// PENDING and PROCESSING both report as PENDING
		return new VisitSummaryResponse("PENDING", null, null, null, null);
	}

	private static List<String> emptyTagsIfBlank(final String aiTags) {
		if (aiTags == null || aiTags.isBlank()) {
			return List.of();
		}
		return List.of(aiTags.split(","));
	}

}
