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

class TerraformAlbOnlyTrafficFlowContractTest {

	private static final Path MAIN = Path.of("infra/terraform/app/dev/main.tf");

	private static final Path README = Path.of("infra/terraform/app/dev/README.md");

	@Test
	void terraformAndReadmeDocumentTheApprovedAlbOnlyTrafficFlow() throws IOException {
		assertThat(MAIN).exists();
		assertThat(README).exists();

		String main = Files.readString(MAIN);
		String readme = Files.readString(README);

		assertThat(main).contains("resource \"aws_vpc_security_group_egress_rule\" \"alb_to_ecs_tasks\"");
		assertThat(main).contains("resource \"aws_vpc_security_group_egress_rule\" \"ecs_task_ipv4_egress\"");
		assertThat(main).contains("resource \"aws_vpc_security_group_egress_rule\" \"ecs_task_ipv6_egress\"");
		assertThat(main).contains("security_group_id            = aws_security_group.alb.id");
		assertThat(main).contains("referenced_security_group_id = aws_security_group.ecs_task.id");
		assertThat(main).contains("from_port                    = 8080");
		assertThat(main).contains("to_port                      = 8080");
		assertThat(main).contains("cidr_ipv4   = \"0.0.0.0/0\"");
		assertThat(main).contains("cidr_ipv6   = \"::/0\"");
		assertThat(readme).contains("internet client -> ALB -> ECS task on app port");
		assertThat(readme).contains("| Source | Destination | Protocol | Ports | Why allowed |");
		assertThat(readme).contains("| Internet client | ALB security group | TCP | 80");
		assertThat(readme).contains("| ALB security group | ECS task security group | TCP | 8080");
		assertThat(readme).contains(
				"| ECS task security group | Internet via NAT-backed private subnets | All required outbound traffic |");
		assertThat(readme).contains("private subnets alone are not treated as sufficient protection");
		assertThat(readme).contains("tighter ECS egress restrictions");
		assertThat(readme).contains("VPC endpoint-based hardening");
	}

}
