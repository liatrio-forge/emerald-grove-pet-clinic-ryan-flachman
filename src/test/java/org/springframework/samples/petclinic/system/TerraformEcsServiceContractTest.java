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

class TerraformEcsServiceContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	@Test
	void devAppStackDefinesOneEcsServiceAttachedToTheSharedClusterAndApplicationTargetGroup() throws IOException {
		assertThat(MAIN).exists();

		String main = Files.readString(MAIN);

		assertThat(main).contains("resource \"aws_ecs_service\" \"application\"");
		assertThat(main).containsOnlyOnce("resource \"aws_ecs_service\"");
		assertThat(main).contains("count                              = local.ecs_runtime_enabled ? 1 : 0");
		assertThat(main).contains("cluster                            = aws_ecs_cluster.shared.id");
		assertThat(main).contains("task_definition                    = aws_ecs_task_definition.application[0].arn");
		assertThat(main).contains("desired_count                      = 1");
		assertThat(main).contains("target_group_arn = aws_lb_target_group.application.arn");
		assertThat(main).contains("container_name   = \"application\"");
		assertThat(main).contains("container_port   = 8080");
	}

	@Test
	void devAppStackKeepsTheServiceInPrivateSubnetsWithoutPublicTaskIps() throws IOException {
		assertThat(MAIN).exists();

		String main = Files.readString(MAIN);

		assertThat(main).contains("subnets          = sort([for subnet in values(aws_subnet.private) : subnet.id])");
		assertThat(main).contains("security_groups  = [aws_security_group.ecs_task.id]");
		assertThat(main).contains("assign_public_ip = false");
		assertThat(main)
			.doesNotContain("subnets          = sort([for subnet in values(aws_subnet.public) : subnet.id])");
	}

	@Test
	void devAppStackDocumentsSingleTaskReplacementBehaviorExplicitlyInTheServiceContract() throws IOException {
		assertThat(MAIN).exists();

		String main = Files.readString(MAIN);

		assertThat(main).contains("health_check_grace_period_seconds  = 120");
		assertThat(main).contains("deployment_minimum_healthy_percent = 0");
		assertThat(main).contains("deployment_maximum_percent         = 100");
	}

}
