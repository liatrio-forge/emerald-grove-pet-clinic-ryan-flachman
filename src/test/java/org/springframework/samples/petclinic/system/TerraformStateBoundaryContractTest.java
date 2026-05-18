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

class TerraformStateBoundaryContractTest {

	private static final Path STATE_STACK_DIRECTORY = Path.of("infra/terraform/state/dev");

	private static final Path STATE_STACK_README = STATE_STACK_DIRECTORY.resolve("README.md");

	private static final Path SPEC = Path
		.of("docs/specs/25-spec-bootstrap-terraform-remote-state-dev-environment/25-spec-bootstrap-terraform-remote-state-dev-environment.md");

	@Test
	void repositoryProvidesADedicatedDevStateStackAreaAndLifecycleGuide() throws IOException {
		assertThat(STATE_STACK_DIRECTORY)
			.as("Remote-state bootstrap resources should live in their own dev state stack")
			.isDirectory();
		assertThat(STATE_STACK_README).as("The state stack should explain its lifecycle").exists();

		String readme = Files.readString(STATE_STACK_README);

		assertThat(readme).contains("state/dev");
		assertThat(readme).contains("before any downstream stack attempts remote-backend initialization");
		assertThat(readme).contains("terraform init -backend=false");
		assertThat(readme).contains("create");
		assertThat(readme).contains("update");
		assertThat(readme).contains("destroy");
		assertThat(readme).contains("main application stack does not own backend resources");
		assertThat(readme).contains("Destroy the application stack before manually tearing down the backend resources");
	}

	@Test
	void specDocumentsSeparateBackendOwnershipAndManualTeardownBoundary() throws IOException {
		assertThat(SPEC).exists();

		String spec = Files.readString(SPEC);

		assertThat(spec)
			.contains("dedicated bootstrap concern separate from the main application infrastructure stack");
		assertThat(spec)
			.contains("destroying the main application stack does not implicitly destroy the backend resources");
		assertThat(spec).contains("short-lived POC assets with a controlled manual teardown sequence");
		assertThat(spec).contains("long-lived versus manually torn-down dev assets");
	}

}
