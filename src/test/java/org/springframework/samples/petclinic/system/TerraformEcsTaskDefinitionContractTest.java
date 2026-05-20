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

class TerraformEcsTaskDefinitionContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	private static final Path VARIABLES = APP_DEV_DIRECTORY.resolve("variables.tf");

	@Test
	void devAppStackDefinesExactlyOneFargateTaskDefinitionForTheApplicationRuntime() throws IOException {
		assertThat(MAIN).exists();

		String main = Files.readString(MAIN);

		assertThat(main).contains("resource \"aws_ecs_task_definition\" \"application\"");
		assertThat(main).containsOnlyOnce("resource \"aws_ecs_task_definition\"");
		assertThat(main).contains("requires_compatibilities = [\"FARGATE\"]");
		assertThat(main).contains("network_mode             = \"awsvpc\"");
		assertThat(main).contains("cpu                      = \"1024\"");
		assertThat(main).contains("memory                   = \"2048\"");
		assertThat(main).contains("containerPort = 8080");
		assertThat(main).contains("hostPort      = 8080");
	}

	@Test
	void devAppStackUsesTheExistingRuntimeRolesLogGroupAndMinimalNonSecretConfiguration() throws IOException {
		assertThat(MAIN).exists();

		String main = Files.readString(MAIN);

		assertThat(main).contains("execution_role_arn       = aws_iam_role.ecs_task_execution.arn");
		assertThat(main).contains("task_role_arn            = aws_iam_role.ecs_task.arn");
		assertThat(main).contains("logDriver = \"awslogs\"");
		assertThat(main).contains("\"awslogs-group\"         = aws_cloudwatch_log_group.application.name");
		assertThat(main).contains("\"awslogs-stream-prefix\" = local.ecs_task_log_stream_prefix");
		assertThat(main).contains("\"awslogs-region\"        = var.aws_region");
		assertThat(main).doesNotContain("environment =");
		assertThat(main).doesNotContain("secrets =");
		assertThat(main).doesNotContain("environmentFiles =");
	}

	@Test
	void devAppStackRequiresOneImmutableBootstrapImageReference() throws IOException {
		assertThat(MAIN).exists();
		assertThat(VARIABLES).exists();

		String main = Files.readString(MAIN);
		String variables = Files.readString(VARIABLES);

		assertThat(main).contains("image     = var.bootstrap_image");
		assertThat(variables).contains("variable \"bootstrap_image\"");
		assertThat(variables)
			.contains("description = \"Immutable bootstrap image reference for the baseline ECS service.\"");
		assertThat(variables).contains("condition     = can(regex(\".+@sha256:[0-9a-f]{64}$\", var.bootstrap_image))");
		assertThat(variables)
			.contains("error_message = \"Provide an immutable bootstrap image reference pinned by digest.\"");
		assertThat(variables).doesNotContain("latest");
		assertThat(main).doesNotContain(":latest");
	}

}
