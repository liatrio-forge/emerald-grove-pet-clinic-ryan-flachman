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

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@code healthTimeline} Thymeleaf fragment (spec 19).
 */
class HealthTimelineFragmentTest {

	private ClassLoaderTemplateResolver templateResolver;

	private SpringTemplateEngine templateEngine;

	@BeforeEach
	void setUp() {
		templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(false);

		templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages/messages");
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		templateEngine.setMessageSource(messageSource);
	}

	private String render(Pet pet) {
		Context context = new Context(Locale.ENGLISH);
		context.setVariable("pet", pet);
		return templateEngine.process("fragments/health-timeline", context);
	}

	private Pet buildPet(Visit... visits) {
		Pet pet = new Pet();
		pet.setName("Buddy");
		int nextId = 1;
		for (Visit visit : visits) {
			if (visit.isNew()) {
				visit.setId(nextId++);
			}
			pet.addVisit(visit);
		}
		return pet;
	}

	private Visit buildVisit(int id, LocalDate date, AiStatus status) {
		Visit visit = new Visit();
		visit.setId(id);
		visit.setDate(date);
		visit.setAiStatus(status);
		visit.setDescription("test visit");
		return visit;
	}

	private static String beforeInlineScript(String html) {
		int i = html.indexOf("<script");
		return i >= 0 ? html.substring(0, i) : html;
	}

	@Test
	void shouldRenderVisitsInReverseChronologicalOrder() {
		Visit earlier = buildVisit(1, LocalDate.of(2026, 1, 1), AiStatus.DONE);
		Visit later = buildVisit(2, LocalDate.of(2026, 3, 15), AiStatus.DONE);
		String html = render(buildPet(earlier, later));

		assertThat(html.indexOf("2026-03-15")).isLessThan(html.indexOf("2026-01-01"));
	}

	@Test
	void shouldRenderDataVisitIdOnEachEntry() {
		Visit visit = buildVisit(7, LocalDate.of(2026, 5, 1), AiStatus.DONE);
		String html = render(buildPet(visit));

		assertThat(html).contains("data-visit-id=\"7\"");
	}

	@Test
	void shouldRenderDataAiStatusOnEachEntry() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.PENDING);
		String html = render(buildPet(visit));

		assertThat(html).contains("data-ai-status=\"PENDING\"");
	}

	@Test
	void shouldRenderDataVisitDateOnEachEntry() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.PENDING);
		String html = render(buildPet(visit));

		assertThat(html).contains("data-visit-date=\"2026-05-01\"");
	}

	@Test
	void shouldRenderSpinnerForPendingVisit() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.PENDING);
		String html = render(buildPet(visit));

		assertThat(html).contains("ai-spinner");
		assertThat(html).contains("Generating summary");
	}

	@Test
	void shouldRenderSpinnerForProcessingVisit() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.PROCESSING);
		String html = render(buildPet(visit));

		assertThat(html).contains("ai-spinner");
		assertThat(html).contains("Generating summary");
	}

	@Test
	void shouldNotRenderUrgencyBadgeForPendingOrProcessingVisit() {
		Visit pending = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.PENDING);
		String pendingHtml = render(buildPet(pending));
		assertThat(pendingHtml).doesNotContain("urgency-routine");
		assertThat(pendingHtml).doesNotContain("urgency-monitor");
		assertThat(pendingHtml).doesNotContain("urgency-urgent");

		Visit processing = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.PROCESSING);
		String processingHtml = render(buildPet(processing));
		assertThat(processingHtml).doesNotContain("urgency-routine");
		assertThat(processingHtml).doesNotContain("urgency-monitor");
		assertThat(processingHtml).doesNotContain("urgency-urgent");
	}

	@Test
	void shouldRenderDateForDoneVisit() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 4, 10), AiStatus.DONE);
		String html = render(buildPet(visit));

		assertThat(html).contains("2026-04-10");
	}

	@Test
	void shouldRenderRoutineUrgencyBadge() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.DONE);
		visit.setAiUrgency("ROUTINE");
		String html = render(buildPet(visit));

		assertThat(html).contains("urgency-routine");
	}

	@Test
	void shouldRenderMonitorUrgencyBadge() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.DONE);
		visit.setAiUrgency("MONITOR");
		String html = render(buildPet(visit));

		assertThat(html).contains("urgency-monitor");
	}

	@Test
	void shouldRenderUrgentUrgencyBadge() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.DONE);
		visit.setAiUrgency("URGENT");
		String html = render(buildPet(visit));

		assertThat(html).contains("urgency-urgent");
	}

	@Test
	void shouldRenderTwoTagChipsFromCommaSeparatedTags() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.DONE);
		visit.setAiTags("diabetes,weight");
		String html = render(buildPet(visit));

		assertThat(StringUtils.countOccurrencesOf(beforeInlineScript(html), "health-tag")).isEqualTo(2);
		assertThat(html).contains("diabetes");
		assertThat(html).contains("weight");
	}

	@Test
	void shouldRenderSummaryText() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.DONE);
		visit.setAiSummary("Routine wellness exam");
		String html = render(buildPet(visit));

		assertThat(html).contains("Routine wellness exam");
	}

	@Test
	void shouldRenderFollowUpWhenNonNull() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.DONE);
		visit.setAiFollowUp("Recheck in 3 months");
		String html = render(buildPet(visit));

		assertThat(html).contains("Recheck in 3 months");
	}

	@Test
	void shouldNotRenderFollowUpWhenNull() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.DONE);
		visit.setAiSummary("ok");
		visit.setAiFollowUp(null);
		String html = render(buildPet(visit));

		assertThat(beforeInlineScript(html).toLowerCase()).doesNotContain("follow");
	}

	@Test
	void shouldRenderErrorIndicatorForFailedVisit() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.FAILED);
		String html = render(buildPet(visit));

		assertThat(html).contains("ai-error");
		assertThat(html).contains("Unable to generate summary");
	}

	@Test
	void shouldNotRenderSpinnerForFailedVisit() {
		Visit visit = buildVisit(1, LocalDate.of(2026, 5, 1), AiStatus.FAILED);
		String html = render(buildPet(visit));

		assertThat(html).doesNotContain("ai-spinner");
	}

}
