/*
 * Copyright 2012-2026 the original author or authors.
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

class ManualDevEcrPublishWorkflowDispatchContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/manual-dev-ecr-publish.yml");

	@Test
	void workflowExistsAndIsManualOnly() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("name: Manual Dev ECR Publish");
		assertThat(workflow).contains("on:");
		assertThat(workflow).contains("workflow_dispatch:");
		assertThat(workflow).doesNotContain("push:");
		assertThat(workflow).doesNotContain("pull_request:");
	}

	@Test
	void workflowRequiresMainBranchTypedConfirmationProtectedEnvironmentAndQueuedConcurrency() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("confirmation:");
		assertThat(workflow).contains("Type publish dev image to confirm the dev ECR publication.");
		assertThat(workflow).contains("github.ref == 'refs/heads/main'");
		assertThat(workflow).contains("environment: dev");
		assertThat(workflow).contains("concurrency:");
		assertThat(workflow).contains("group: manual-dev-ecr-publish");
		assertThat(workflow).contains("cancel-in-progress: false");
		assertThat(workflow).contains("Publish requires the main branch.");
		assertThat(workflow).contains("Publish requires confirmation: publish dev image");
	}

}
