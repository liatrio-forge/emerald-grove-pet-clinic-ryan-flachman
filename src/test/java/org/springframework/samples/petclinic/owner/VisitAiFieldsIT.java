package org.springframework.samples.petclinic.owner;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class VisitAiFieldsIT {

	@Autowired
	private OwnerRepository owners;

	@Test
	@Transactional
	void persistAndReloadVisitWithAllAiFieldsSet() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		Pet pet = owner.getPet(7);

		Visit visit = new Visit();
		visit.setDescription("ai-round-trip-unique");
		visit.setAiStatus(AiStatus.DONE);
		visit.setAiSummary("test summary");
		visit.setAiTags("tag1,tag2");
		visit.setAiUrgency("ROUTINE");
		visit.setAiFollowUp("Follow up note");

		owner.addVisit(pet.getId(), visit);
		owners.save(owner);

		optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		owner = optionalOwner.get();
		pet = owner.getPet(7);

		Visit reloaded = pet.getVisits()
			.stream()
			.filter(v -> "ai-round-trip-unique".equals(v.getDescription()))
			.findFirst()
			.orElseThrow(() -> new AssertionError("visit not found after save"));

		assertThat(reloaded.getId()).isNotNull();
		assertThat(reloaded.getAiStatus()).isEqualTo(AiStatus.DONE);
		assertThat(reloaded.getAiSummary()).isEqualTo("test summary");
		assertThat(reloaded.getAiTags()).isEqualTo("tag1,tag2");
		assertThat(reloaded.getAiUrgency()).isEqualTo("ROUTINE");
		assertThat(reloaded.getAiFollowUp()).isEqualTo("Follow up note");
	}

	@Test
	@Transactional
	void freshVisitDefaultsToAiStatusPendingAndNullableFieldsAreNull() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		Pet pet = owner.getPet(7);

		Visit visit = new Visit();
		visit.setDescription("ai-defaults-unique");
		owner.addVisit(pet.getId(), visit);
		owners.save(owner);

		optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		owner = optionalOwner.get();
		pet = owner.getPet(7);

		Visit reloaded = pet.getVisits()
			.stream()
			.filter(v -> "ai-defaults-unique".equals(v.getDescription()))
			.findFirst()
			.orElseThrow(() -> new AssertionError("visit not found after save"));

		assertThat(reloaded.getAiStatus()).isEqualTo(AiStatus.PENDING);
		assertThat(reloaded.getAiSummary()).isNull();
		assertThat(reloaded.getAiTags()).isNull();
		assertThat(reloaded.getAiUrgency()).isNull();
		assertThat(reloaded.getAiFollowUp()).isNull();
	}

}
