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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * JSON payload for {@link VisitSummaryController} polling responses.
 *
 * @param status {@code PENDING}, {@code DONE}, or {@code FAILED}
 * @param summary AI-generated summary text when done
 * @param tags tag list when done
 * @param urgency lowercase urgency label when done
 * @param followUp optional follow-up recommendation when done
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VisitSummaryResponse(String status, String summary, List<String> tags, String urgency, String followUp) {
}
