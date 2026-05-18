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

class TerraformRemoteStateVerificationWorkflowTest {

	private static final Path SCRIPT = Path.of("scripts/verify-terraform-remote-state-contract.sh");

	private static final Path README = Path.of("README.md");

	@Test
	void verificationScriptChecksStateStackConsumerAndComposeManagedFlociWorkflow() throws IOException {
		assertThat(SCRIPT).exists();

		String script = Files.readString(SCRIPT);

		assertThat(script).contains("terraform -chdir=infra/terraform/state/dev init -backend=false");
		assertThat(script).contains("terraform -chdir=infra/terraform/state/dev validate");
		assertThat(script).contains("docker compose -f infra/terraform/floci/docker-compose.yml up -d floci");
		assertThat(script)
			.contains("terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure");
		assertThat(script).contains("docker compose -f infra/terraform/floci/docker-compose.yml down");
		assertThat(script).contains("AWS_ACCESS_KEY_ID=test");
		assertThat(script).contains("emerald-grove-pet-clinic-dev-terraform-state");
		assertThat(script).contains("emerald-grove-pet-clinic-dev-terraform-locks");
		assertThat(script).contains("missing required file");
		assertThat(script).doesNotContain("AKIA");
	}

	@Test
	void readmeDocumentsTheRepositoryOwnedVerificationEntryPoint() throws IOException {
		assertThat(README).exists();

		String readme = Files.readString(README);

		assertThat(readme).contains("./scripts/verify-terraform-remote-state-contract.sh");
		assertThat(readme).contains("docker compose -f infra/terraform/floci/docker-compose.yml up -d floci");
		assertThat(readme).contains("floci");
		assertThat(readme).contains("remote-state contract");
	}

}
