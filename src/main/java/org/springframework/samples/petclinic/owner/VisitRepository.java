package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface VisitRepository extends Repository<Visit, Integer> {

	@Query("""
			SELECT new org.springframework.samples.petclinic.owner.UpcomingVisitRow(
			    v.id, v.date, v.description,
			    p.name,
			    o.firstName, o.lastName, o.id
			)
			FROM Pet p JOIN p.visits v JOIN p.owner o
			WHERE v.date BETWEEN :start AND :end
			ORDER BY v.date ASC
			""")
	List<UpcomingVisitRow> findUpcomingVisits(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
