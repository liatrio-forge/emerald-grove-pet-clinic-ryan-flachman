package org.springframework.samples.petclinic.owner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitSummaryService {

	private static final Logger log = LoggerFactory.getLogger(VisitSummaryService.class);

	private final VisitRepository visitRepository;

	private final ClaudeApiClient claudeApiClient;

	private final VisitSummaryParser parser;

	public VisitSummaryService(VisitRepository visitRepository, ClaudeApiClient claudeApiClient,
			VisitSummaryParser parser) {
		this.visitRepository = visitRepository;
		this.claudeApiClient = claudeApiClient;
		this.parser = parser;
	}

	@Async("visitSummaryExecutor")
	@Transactional
	public void generate(Integer visitId) {
		var visitOpt = visitRepository.findById(visitId);
		if (visitOpt.isEmpty()) {
			log.warn("Visit {} not found for AI summary generation", visitId);
			return;
		}
		Visit visit = visitOpt.get();

		visit.setAiStatus(AiStatus.PROCESSING);
		visitRepository.save(visit);

		try {
			PromptRequest prompt = VisitPromptBuilder.build(visit, visit.getPet());
			String raw = claudeApiClient.complete(prompt.systemPrompt(), prompt.userMessage());
			VisitSummary summary = parser.parse(raw);
			visit.setAiSummary(summary.summary());
			visit.setAiTags(String.join(",", summary.tags()));
			visit.setAiUrgency(summary.urgency().name());
			visit.setAiFollowUp(summary.followUp());
			visit.setAiStatus(AiStatus.DONE);
		}
		catch (Exception e) {
			log.error("AI summary generation failed for visit {}", visitId, e);
			visit.setAiStatus(AiStatus.FAILED);
		}
		visitRepository.save(visit);
	}

}
