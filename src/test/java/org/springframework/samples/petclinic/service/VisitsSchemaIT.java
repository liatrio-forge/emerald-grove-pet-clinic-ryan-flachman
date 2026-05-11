package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class VisitsSchemaIT {

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void descriptionColumnHasExtendedLength() {
		List<Map<String, Object>> rows = jdbc
			.queryForList("SELECT CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS "
					+ "WHERE TABLE_NAME = 'VISITS' AND COLUMN_NAME = 'DESCRIPTION'");
		assertThat(rows).hasSize(1);
		Object maxLen = rows.get(0).get("CHARACTER_MAXIMUM_LENGTH");
		assertThat(((Number) maxLen).intValue()).isEqualTo(2000);
	}

	@Test
	void aiColumnsExistInVisitsTable() {
		List<String> expected = List.of("AI_STATUS", "AI_SUMMARY", "AI_TAGS", "AI_URGENCY", "AI_FOLLOW_UP");
		List<Map<String, Object>> rows = jdbc.queryForList("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS "
				+ "WHERE TABLE_NAME = 'VISITS' AND COLUMN_NAME IN "
				+ "('AI_STATUS','AI_SUMMARY','AI_TAGS','AI_URGENCY','AI_FOLLOW_UP')");
		List<String> found = rows.stream().map(r -> r.get("COLUMN_NAME").toString()).toList();
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
	}

}
