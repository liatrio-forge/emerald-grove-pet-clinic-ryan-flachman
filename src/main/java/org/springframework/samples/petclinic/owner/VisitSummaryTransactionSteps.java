package org.springframework.samples.petclinic.owner;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Short, focused transactions for AI visit-summary persistence. Invoked from
 * {@link VisitSummaryService} so remote Claude work stays outside a transaction.
 */
@Service
public class VisitSummaryTransactionSteps {

	private final VisitRepository visitRepository;

	public VisitSummaryTransactionSteps(VisitRepository visitRepository) {
		this.visitRepository = visitRepository;
	}

	@Transactional(readOnly = true)
	public Optional<Visit> loadVisitForGeneration(Integer visitId) {
		return visitRepository.findById(visitId).map(visit -> {
			visit.getPet();
			return visit;
		});
	}

	@Transactional
	public boolean markVisitProcessing(Integer visitId) {
		return visitRepository.findById(visitId).map(visit -> {
			visit.setAiStatus(AiStatus.PROCESSING);
			visitRepository.save(visit);
			return true;
		}).orElse(false);
	}

	@Transactional
	public void finalizeWithSummary(Integer visitId, VisitSummary summary) {
		Visit visit = visitRepository.findById(visitId)
			.orElseThrow(() -> new IllegalStateException(
					"Visit %d not found when persisting AI summary".formatted(visitId)));
		visit.setAiSummary(summary.summary());
		visit.setAiTags(String.join(",", summary.tags()));
		visit.setAiUrgency(summary.urgency().name());
		visit.setAiFollowUp(summary.followUp());
		visit.setAiStatus(AiStatus.DONE);
		visitRepository.save(visit);
	}

	@Transactional
	public void finalizeWithFailure(Integer visitId) {
		visitRepository.findById(visitId).ifPresent(visit -> {
			visit.setAiStatus(AiStatus.FAILED);
			visitRepository.save(visit);
		});
	}

}
