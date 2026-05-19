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

class TerraformEcsTaskSecurityGroupContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	private static final Path OUTPUTS = APP_DEV_DIRECTORY.resolve("outputs.tf");

	private static final Path LOCALS = APP_DEV_DIRECTORY.resolve("locals.tf");

	@Test
	void ecsTasksAllowIngressOnlyFromAlbSecurityGroupOnApplicationPort() throws IOException {
		assertThat(MAIN).exists();
		assertThat(OUTPUTS).exists();
		assertThat(LOCALS).exists();

		String main = Files.readString(MAIN);
		String outputs = Files.readString(OUTPUTS);
		String locals = Files.readString(LOCALS);

		assertThat(main).contains("resource \"aws_security_group\" \"ecs_task\"");
		assertThat(main).contains("resource \"aws_vpc_security_group_ingress_rule\" \"ecs_task_from_alb\"");
		assertThat(main).contains("security_group_id            = aws_security_group.ecs_task.id");
		assertThat(main).contains("referenced_security_group_id = aws_security_group.alb.id");
		assertThat(main).contains("from_port                    = 8080");
		assertThat(main).contains("to_port                      = 8080");
		assertThat(main).doesNotContain("cidr_ipv4         = \"0.0.0.0/0\"");
		assertThat(main).doesNotContain("cidr_ipv6         = \"::/0\"");
		assertThat(main).doesNotContain("cidr_ipv4         = aws_vpc.dev.cidr_block");
		assertThat(outputs).contains("output \"alb_security_group_id\"");
		assertThat(outputs).contains("output \"ecs_task_security_group_id\"");
		assertThat(locals).contains("ecs_task_security_group_name");
	}

}
