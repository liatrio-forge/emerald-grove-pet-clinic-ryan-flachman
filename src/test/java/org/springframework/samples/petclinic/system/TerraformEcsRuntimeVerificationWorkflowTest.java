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

class TerraformEcsRuntimeVerificationWorkflowTest {

	private static final Path SCRIPT = Path.of("scripts/verify-ecs-runtime-foundation-contract.sh");

	private static final Path FLOCI_README = Path.of("infra/terraform/floci/README.md");

	@Test
	void verificationWorkflowUsesFlociAndSanitizedTerraformChecksForTheEcsRuntimeContract() throws IOException {
		assertThat(SCRIPT).exists();
		assertThat(FLOCI_README).exists();

		String script = Files.readString(SCRIPT);
		String readme = Files.readString(FLOCI_README);

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
		assertThat(script).contains("infra/terraform/app/dev/outputs.tf");
		assertThat(script).contains("scripts/verify-ecs-runtime-foundation-contract.sh");
		assertThat(script).doesNotContain("AKIA");
		assertThat(readme).contains("./scripts/verify-ecs-runtime-foundation-contract.sh");
		assertThat(readme).contains("terraform -chdir=infra/terraform/app/dev validate");
		assertThat(readme).contains("terraform -chdir=infra/terraform/app/dev plan -no-color");
		assertThat(readme).contains("floci");
		assertThat(readme).contains("ECS runtime contract");
	}

}
