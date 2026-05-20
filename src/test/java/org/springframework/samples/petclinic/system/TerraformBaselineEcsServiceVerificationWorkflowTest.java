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

class TerraformBaselineEcsServiceVerificationWorkflowTest {

	private static final Path SCRIPT = Path.of("scripts/verify-baseline-ecs-task-definition-service-contract.sh");

	private static final Path FLOCI_README = Path.of("infra/terraform/floci/README.md");

	private static final Path APP_DEV_README = Path.of("infra/terraform/app/dev/README.md");

	@Test
	void verificationWorkflowUsesFlociAndChecksForAnImmutableBootstrapImageBeforeAwsDeployment() throws IOException {
		assertThat(SCRIPT).exists();
		assertThat(FLOCI_README).exists();
		assertThat(APP_DEV_README).exists();

		String script = Files.readString(SCRIPT);
		String flociReadme = Files.readString(FLOCI_README);
		String appDevReadme = Files.readString(APP_DEV_README);

		assertThat(script)
			.contains("terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure");
		assertThat(script).contains("terraform -chdir=infra/terraform/app/dev validate");
		assertThat(script).contains("terraform -chdir=infra/terraform/app/dev plan -no-color");
		assertThat(script).contains("docker compose -f infra/terraform/floci/docker-compose.yml up -d floci");
		assertThat(script).contains("docker compose -f infra/terraform/floci/docker-compose.yml down");
		assertThat(script).contains("AWS_ACCESS_KEY_ID=test");
		assertThat(script).contains("AWS_SECRET_ACCESS_KEY=test");
		assertThat(script).contains("AWS_EC2_METADATA_DISABLED=true");
		assertThat(script).contains("missing required file");
		assertThat(script).contains("bootstrap_image");
		assertThat(script).contains("@sha256:");
		assertThat(script).doesNotContain("AKIA");
		assertThat(flociReadme).contains("./scripts/verify-baseline-ecs-task-definition-service-contract.sh");
		assertThat(flociReadme).contains("terraform -chdir=infra/terraform/app/dev validate");
		assertThat(flociReadme).contains("terraform -chdir=infra/terraform/app/dev plan -no-color");
		assertThat(flociReadme).contains("baseline ECS service contract");
		assertThat(appDevReadme).contains("baseline_task_definition_arn");
		assertThat(appDevReadme).contains("single-task replacement with brief downtime");
	}

	@Test
	void verificationWorkflowDocumentsTheExactAwsEvidenceCommandsAndScopeBoundaries() throws IOException {
		assertThat(SCRIPT).exists();
		assertThat(APP_DEV_README).exists();

		String script = Files.readString(SCRIPT);
		String appDevReadme = Files.readString(APP_DEV_README);

		assertThat(script).contains("aws ecs describe-services");
		assertThat(script).contains("aws ecs list-tasks");
		assertThat(script).contains("aws elbv2 describe-target-health");
		assertThat(script).contains("curl -fsS http://");
		assertThat(script).contains("aws logs get-log-events");
		assertThat(script).contains("assign_public_ip = false");
		assertThat(appDevReadme).contains("build and push a real immutable Git SHA image");
		assertThat(appDevReadme).contains("verify ECS steady state");
		assertThat(appDevReadme).contains("verify target health");
		assertThat(appDevReadme).contains("verify ALB reachability");
		assertThat(appDevReadme).contains("verify CloudWatch logs");
		assertThat(appDevReadme).contains("no direct task public-IP path exists");
		assertThat(appDevReadme).doesNotContain("Secrets Manager");
	}

}
