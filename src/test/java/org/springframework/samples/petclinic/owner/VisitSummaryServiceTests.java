package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitSummaryServiceTests {

	@Mock
	private VisitRepository visitRepository;

	@Mock
	private ClaudeApiClient claudeApiClient;

	@Mock
	private VisitSummaryParser parser;

	@InjectMocks
	private VisitSummaryService service;

	private final List<AiStatus> statusesOnSave = new ArrayList<>();

	private Visit visitWithPet(int id, Pet pet) {
		Visit visit = new Visit();
		visit.setId(id);
		ReflectionTestUtils.setField(visit, "pet", pet);
		return visit;
	}

	@BeforeEach
	void saveRecordsStatus() {
		statusesOnSave.clear();
		lenient().when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> {
			Visit v = invocation.getArgument(0);
			statusesOnSave.add(v.getAiStatus());
			return v;
		});
	}

	@Test
	void generateHappyPathSetsProcessingThenDone() {
		Pet pet = new Pet();
		pet.setName("N");
		Visit visit = visitWithPet(1, pet);
		when(visitRepository.findById(eq(1))).thenReturn(Optional.of(visit));
		when(claudeApiClient.complete(anyString(), anyString())).thenReturn("{}");
		when(parser.parse(anyString()))
			.thenReturn(new VisitSummary("Brief", List.of("one", "two"), VisitUrgency.MONITOR, "next"));

		service.generate(1);

		assertThat(statusesOnSave).containsExactly(AiStatus.PROCESSING, AiStatus.DONE);
	}

	@Test
	void generateHappyPathWritesAiFields() {
		Pet pet = new Pet();
		pet.setName("N");
		Visit visit = visitWithPet(1, pet);
		when(visitRepository.findById(eq(1))).thenReturn(Optional.of(visit));
		when(claudeApiClient.complete(anyString(), anyString())).thenReturn("{}");
		VisitSummary summary = new VisitSummary("S", List.of("a", "b"), VisitUrgency.URGENT, "later");
		when(parser.parse(anyString())).thenReturn(summary);

		service.generate(1);

		assertThat(visit.getAiSummary()).isEqualTo("S");
		assertThat(visit.getAiTags()).isEqualTo("a,b");
		assertThat(visit.getAiUrgency()).isEqualTo(VisitUrgency.URGENT.name());
		assertThat(visit.getAiFollowUp()).isEqualTo("later");
	}

	@Test
	void generateClientExceptionSetsFailedStatus() {
		Pet pet = new Pet();
		pet.setName("N");
		Visit visit = visitWithPet(1, pet);
		when(visitRepository.findById(eq(1))).thenReturn(Optional.of(visit));
		when(claudeApiClient.complete(anyString(), anyString())).thenThrow(new RuntimeException("network error"));

		assertThatCode(() -> service.generate(1)).doesNotThrowAnyException();

		assertThat(statusesOnSave).containsExactly(AiStatus.PROCESSING, AiStatus.FAILED);
	}

	@Test
	void generateParseExceptionSetsFailedStatus() {
		Pet pet = new Pet();
		pet.setName("N");
		Visit visit = visitWithPet(1, pet);
		when(visitRepository.findById(eq(1))).thenReturn(Optional.of(visit));
		when(claudeApiClient.complete(anyString(), anyString())).thenReturn("{}");
		when(parser.parse(anyString())).thenThrow(new VisitSummaryParseException("bad json", null));

		assertThatCode(() -> service.generate(1)).doesNotThrowAnyException();

		assertThat(statusesOnSave).containsExactly(AiStatus.PROCESSING, AiStatus.FAILED);
	}

	@Test
	void generateVisitNotFoundLogsAndReturns() {
		when(visitRepository.findById(eq(99))).thenReturn(Optional.empty());

		assertThatCode(() -> service.generate(99)).doesNotThrowAnyException();

		verify(claudeApiClient, never()).complete(anyString(), anyString());
		verify(visitRepository, never()).save(any(Visit.class));
	}

}
