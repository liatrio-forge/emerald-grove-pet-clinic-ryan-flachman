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

class TerraformEcsClusterContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	private static final Path LOCALS = APP_DEV_DIRECTORY.resolve("locals.tf");

	@Test
	void devAppStackDefinesExactlyOneMinimalEcsClusterForFutureFargateServices() throws IOException {
		assertThat(MAIN).exists();
		assertThat(LOCALS).exists();

		String main = Files.readString(MAIN);
		String locals = Files.readString(LOCALS);

		assertThat(main).contains("resource \"aws_ecs_cluster\" \"shared\"");
		assertThat(main).containsOnlyOnce("resource \"aws_ecs_cluster\"");
		assertThat(main).contains("name = local.ecs_cluster_name");
		assertThat(main).contains("tags = merge(local.common_tags, {");
		assertThat(main).contains("Name = local.ecs_cluster_name");
		assertThat(main).doesNotContain("capacity_providers");
		assertThat(main).doesNotContain("default_capacity_provider_strategy");
		assertThat(main).doesNotContain("container_insights");
		assertThat(main).doesNotContain("execute_command_configuration");
		assertThat(main).doesNotContain("resource \"aws_ecs_service\"");
		assertThat(locals).contains("ecs_cluster_name");
	}

}
