package org.springframework.samples.petclinic.owner;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VisitSummaryService {

	private static final Logger log = LoggerFactory.getLogger(VisitSummaryService.class);

	private final VisitSummaryTransactionSteps transactionSteps;

	private final ClaudeApiClient claudeApiClient;

	private final VisitSummaryParser parser;

	public VisitSummaryService(VisitSummaryTransactionSteps transactionSteps, ClaudeApiClient claudeApiClient,
			VisitSummaryParser parser) {
		this.transactionSteps = transactionSteps;
		this.claudeApiClient = claudeApiClient;
		this.parser = parser;
	}

	@Async("visitSummaryExecutor")
	public void generate(Integer visitId) {
		Optional<Visit> visitOpt = transactionSteps.loadVisitForGeneration(visitId);
		if (visitOpt.isEmpty()) {
			log.warn("Visit {} not found for AI summary generation", visitId);
			return;
		}

		Visit visit = visitOpt.get();
		if (!transactionSteps.markVisitProcessing(visitId)) {
			log.warn("Visit {} not found when marking PROCESSING for AI summary generation", visitId);
			return;
		}

		try {
			PromptRequest prompt = VisitPromptBuilder.build(visit, visit.getPet());
			String raw = claudeApiClient.complete(prompt.systemPrompt(), prompt.userMessage());
			VisitSummary summary = parser.parse(raw);
			transactionSteps.finalizeWithSummary(visitId, summary);
		}
		catch (Exception e) {
			log.error("AI summary generation failed for visit {}", visitId, e);
			transactionSteps.finalizeWithFailure(visitId);
		}
	}

}
