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

class TerraformPublicAlbContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	private static final Path LOCALS = APP_DEV_DIRECTORY.resolve("locals.tf");

	private static final Path VARIABLES = APP_DEV_DIRECTORY.resolve("variables.tf");

	@Test
	void devAppStackDefinesOneInternetFacingAlbInPublicSubnetsUsingTheExistingAlbSecurityGroup() throws IOException {
		assertThat(MAIN).exists();
		assertThat(LOCALS).exists();
		assertThat(VARIABLES).exists();

		String main = Files.readString(MAIN);
		String locals = Files.readString(LOCALS);
		String variables = Files.readString(VARIABLES);

		assertThat(main).contains("resource \"aws_lb\" \"public\"");
		assertThat(main).contains("load_balancer_type = \"application\"");
		assertThat(main).contains("internal           = false");
		assertThat(main).contains("subnets            = sort([for subnet in values(aws_subnet.public) : subnet.id])");
		assertThat(main).contains("security_groups    = [aws_security_group.alb.id]");
		assertThat(main).contains("resource \"aws_vpc_security_group_ingress_rule\" \"alb_listener_ipv4\"");
		assertThat(main).contains("resource \"aws_vpc_security_group_ingress_rule\" \"alb_listener_ipv6\"");
		assertThat(main).contains("var.alb_listener_port");
		assertThat(variables).contains("variable \"alb_listener_port\"");
		assertThat(variables).contains("default     = 80");
		assertThat(locals).contains("public_alb_name");
	}

}
