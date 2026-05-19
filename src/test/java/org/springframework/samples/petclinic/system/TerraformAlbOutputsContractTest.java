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

class TerraformAlbOutputsContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path OUTPUTS = APP_DEV_DIRECTORY.resolve("outputs.tf");

	private static final Path README = APP_DEV_DIRECTORY.resolve("README.md");

	@Test
	void devAppStackExportsReviewerReadableAlbAndTargetGroupIdentifiers() throws IOException {
		assertThat(OUTPUTS).exists();
		assertThat(README).exists();

		String outputs = Files.readString(OUTPUTS);
		String readme = Files.readString(README);

		assertThat(outputs).contains("output \"alb_dns_name\"");
		assertThat(outputs).contains("output \"alb_hosted_zone_id\"");
		assertThat(outputs).contains("output \"alb_arn\"");
		assertThat(outputs).contains("output \"alb_name\"");
		assertThat(outputs).contains("output \"http_listener_arn\"");
		assertThat(outputs).contains("output \"application_target_group_arn\"");
		assertThat(outputs).contains("output \"application_target_group_name\"");
		assertThat(outputs).contains("value       = aws_lb.public.dns_name");
		assertThat(outputs).contains("value       = aws_lb.public.zone_id");
		assertThat(outputs).contains("value       = aws_lb.public.arn");
		assertThat(outputs).contains("value       = aws_lb.public.name");
		assertThat(outputs).contains("value       = aws_lb_listener.http.arn");
		assertThat(outputs).contains("value       = aws_lb_target_group.application.arn");
		assertThat(outputs).contains("value       = aws_lb_target_group.application.name");
		assertThat(readme).contains("ALB DNS name");
		assertThat(readme).contains("approved v1 public endpoint identifier");
		assertThat(readme).contains("end-to-end application reachability");
		assertThat(readme).contains("later ECS service attachment");
	}

}
