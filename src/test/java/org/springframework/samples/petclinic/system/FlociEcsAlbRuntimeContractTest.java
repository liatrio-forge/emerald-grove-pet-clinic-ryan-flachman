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

class FlociEcsAlbRuntimeContractTest {

	private static final Path FLOCI_COMPOSE = Path.of("infra/terraform/floci/docker-compose.yml");

	@Test
	void flociEnablesTheAwsServicesAndDockerAccessNeededForLocalEcsAlbRuntimeChecks() throws IOException {
		assertThat(FLOCI_COMPOSE).exists();

		String compose = Files.readString(FLOCI_COMPOSE);

		assertThat(compose).contains("localstack/localstack:3");
		assertThat(compose).contains("SERVICES: s3,dynamodb,sts,ec2,ecr,ecs,elbv2,iam,logs");
		assertThat(compose).contains("/var/run/docker.sock:/var/run/docker.sock");
		assertThat(compose).contains("ECS_REMOVE_CONTAINERS: 1");
		assertThat(compose).contains("\"4566:4566\"");
	}

}
