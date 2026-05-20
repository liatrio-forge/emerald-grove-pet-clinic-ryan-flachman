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

class TerraformEcsServiceOutputsContractTest {

	private static final Path OUTPUTS = Path.of("infra/terraform/app/dev/outputs.tf");

	@Test
	void devAppStackExportsOnlyTheLeanBaselineServiceAndTaskDefinitionIdentifiers() throws IOException {
		assertThat(OUTPUTS).exists();

		String outputs = Files.readString(OUTPUTS);

		assertThat(outputs).contains("output \"baseline_ecs_service_name\"");
		assertThat(outputs).contains("output \"baseline_ecs_service_arn\"");
		assertThat(outputs).contains("output \"baseline_task_definition_family\"");
		assertThat(outputs).contains("output \"baseline_task_definition_arn\"");
		assertThat(outputs).contains("value       = aws_ecs_service.application.name");
		assertThat(outputs).contains("value       = aws_ecs_service.application.id");
		assertThat(outputs).contains("value       = aws_ecs_task_definition.application.family");
		assertThat(outputs).contains("value       = aws_ecs_task_definition.application.arn");
		assertThat(outputs).doesNotContain("output \"baseline_task_definition_revision\"");
	}

}
