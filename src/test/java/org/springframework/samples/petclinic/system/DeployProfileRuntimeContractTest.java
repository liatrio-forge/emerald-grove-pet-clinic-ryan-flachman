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

class DeployProfileRuntimeContractTest {

	private static final Path DEPLOY_PROPERTIES = Path.of("src/main/resources/application-deploy.properties");

	@Test
	void deployProfileDefinesTheRuntimePortAndEnvironmentDrivenOverrides() throws IOException {
		assertThat(DEPLOY_PROPERTIES)
			.as("The deploy profile should be defined in a dedicated application-deploy.properties file")
			.exists();

		String properties = Files.readString(DEPLOY_PROPERTIES);

		assertThat(properties).contains("server.port=8080");
		assertThat(properties).contains("spring.datasource.url=${SPRING_DATASOURCE_URL:");
		assertThat(properties).contains("spring.datasource.username=${SPRING_DATASOURCE_USERNAME:");
		assertThat(properties).contains("spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:");
		assertThat(properties).contains("anthropic.api.key=${ANTHROPIC_API_KEY:");
		assertThat(properties).doesNotContain("spring.profiles.active=deploy");
	}

	@Test
	void dockerfileDocumentsDeployProfileActivationThroughSpringProfilesActive() throws IOException {
		String dockerfile = Files.readString(Path.of("Dockerfile"));

		assertThat(dockerfile).contains("SPRING_PROFILES_ACTIVE=deploy");
	}

}
