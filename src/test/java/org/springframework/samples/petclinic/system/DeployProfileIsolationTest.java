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

package org.springframework.samples.petclinic.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeployProfileIsolationTest {

	private static final Path DEFAULT_PROPERTIES = Path.of("src/main/resources/application.properties");

	private static final Path DEPLOY_PROPERTIES = Path.of("src/main/resources/application-deploy.properties");

	@Test
	void deployProfileKeepsOnlyDeploySpecificOverridesAndRuntimeHooks() throws IOException {
		String defaultProperties = Files.readString(DEFAULT_PROPERTIES);
		String deployProperties = Files.readString(DEPLOY_PROPERTIES);

		assertThat(defaultProperties).contains("management.endpoints.web.exposure.include=*");

		assertThat(deployProperties).contains("server.port=8080");
		assertThat(deployProperties).contains("management.endpoints.web.exposure.include=health");
		assertThat(deployProperties).contains("management.endpoint.health.show-details=never");
		assertThat(deployProperties).contains("management.endpoint.health.probes.enabled=true");
		assertThat(deployProperties).contains("spring.datasource.url=${SPRING_DATASOURCE_URL:");
		assertThat(deployProperties).contains("spring.datasource.username=${SPRING_DATASOURCE_USERNAME:");
		assertThat(deployProperties).contains("spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:");
		assertThat(deployProperties).contains("anthropic.api.key=${ANTHROPIC_API_KEY:");

		assertThat(deployProperties).doesNotContain("database=h2");
		assertThat(deployProperties).doesNotContain("spring.sql.init.schema-locations=");
		assertThat(deployProperties).doesNotContain("spring.sql.init.data-locations=");
		assertThat(deployProperties).doesNotContain("spring.thymeleaf.mode=");
		assertThat(deployProperties).doesNotContain("spring.jpa.hibernate.ddl-auto=");
		assertThat(deployProperties).doesNotContain("spring.jpa.open-in-view=");
		assertThat(deployProperties).doesNotContain("spring.messages.basename=");
		assertThat(deployProperties).doesNotContain("logging.level.org.springframework=");
		assertThat(deployProperties).doesNotContain("spring.web.resources.cache.cachecontrol.max-age=");
		assertThat(deployProperties).doesNotContain("anthropic.api.url=");
		assertThat(deployProperties).doesNotContain("anthropic.model=");
		assertThat(deployProperties).doesNotContain("spring.profiles.active=deploy");
	}

}
