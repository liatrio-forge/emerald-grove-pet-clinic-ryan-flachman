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

class TerraformEcsIamBoundaryContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	@Test
	void devAppStackDefinesSeparateExecutionAndTaskRolesWithEcsTaskTrust() throws IOException {
		assertThat(MAIN).exists();

		String main = Files.readString(MAIN);

		assertThat(main).contains("resource \"aws_iam_role\" \"ecs_task_execution\"");
		assertThat(main).contains("resource \"aws_iam_role\" \"ecs_task\"");
		assertThat(main).containsOnlyOnce("resource \"aws_iam_role_policy_attachment\" \"ecs_task_execution\"");
		assertThat(main)
			.contains("policy_arn = \"arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy\"");
		assertThat(main).contains("role       = aws_iam_role.ecs_task_execution.name");
		assertThat(main).contains("assume_role_policy = jsonencode({");
		assertThat(main).contains("\"ecs-tasks.amazonaws.com\"");
		assertThat(main).contains("name = local.ecs_task_execution_role_name");
		assertThat(main).contains("name = local.ecs_task_role_name");
		assertThat(main).doesNotContain("resource \"aws_iam_role_policy\" \"ecs_task\"");
		assertThat(main).doesNotContain("resource \"aws_iam_policy\" \"ecs_task\"");
		assertThat(main).doesNotContain("resource \"aws_iam_role_policy_attachment\" \"ecs_task\"");
	}

}
