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

class TerraformAlbListenerAndTargetGroupContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	private static final Path VARIABLES = APP_DEV_DIRECTORY.resolve("variables.tf");

	@Test
	void devAppStackDefinesOneHttpListenerForwardingPort80TrafficFromThePublicAlb() throws IOException {
		assertThat(MAIN).exists();
		assertThat(VARIABLES).exists();

		String main = Files.readString(MAIN);
		String variables = Files.readString(VARIABLES);

		assertThat(main).contains("resource \"aws_lb_listener\" \"http\"");
		assertThat(main).contains("load_balancer_arn = aws_lb.public.arn");
		assertThat(main).contains("port              = var.alb_listener_port");
		assertThat(main).contains("protocol          = \"HTTP\"");
		assertThat(main).contains("default_action {");
		assertThat(main).contains("type             = \"forward\"");
		assertThat(main).contains("target_group_arn = aws_lb_target_group.application.arn");
		assertThat(main).contains("resource \"aws_lb_target_group\" \"application\"");
		assertThat(main).contains("target_type = \"ip\"");
		assertThat(main).contains("port        = 8080");
		assertThat(main).contains("protocol    = \"HTTP\"");
		assertThat(main).contains("health_check {");
		assertThat(main).contains("path                = \"/actuator/health\"");
		assertThat(main).contains("port                = \"traffic-port\"");
		assertThat(main).contains("matcher             = \"200-299\"");
		assertThat(main).contains("interval            = 15");
		assertThat(main).contains("timeout             = 5");
		assertThat(main).contains("healthy_threshold   = 2");
		assertThat(main).contains("unhealthy_threshold = 3");
		assertThat(variables).contains("variable \"alb_listener_port\"");
		assertThat(variables).contains("default     = 80");
	}

}
