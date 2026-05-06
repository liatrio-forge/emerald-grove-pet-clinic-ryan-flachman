package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

public record UpcomingVisitRow(int id, LocalDate date, String description, String petName, String ownerFirstName,
		String ownerLastName, int ownerId) {
}
