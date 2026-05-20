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

class TerraformApplyWorkflowDispatchContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/terraform-apply-dev.yml");

	@Test
	void workflowExistsAndIsManualOnly() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("name: Terraform Apply Dev");
		assertThat(workflow).contains("on:");
		assertThat(workflow).contains("workflow_dispatch:");
		assertThat(workflow).doesNotContain("push:");
		assertThat(workflow).doesNotContain("pull_request:");
	}

	@Test
	void workflowRequiresMainBranchTypedConfirmationAndProtectedEnvironment() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("confirmation:");
		assertThat(workflow).contains("deploy_image:");
		assertThat(workflow).contains("Type apply dev to confirm the dev Terraform apply.");
		assertThat(workflow).contains("Provide the immutable deploy image reference pinned by digest.");
		assertThat(workflow).contains("github.ref == 'refs/heads/main'");
		assertThat(workflow).contains("environment: dev");
		assertThat(workflow).contains("Apply requires the main branch.");
		assertThat(workflow).contains("Apply requires confirmation: apply dev");
		assertThat(workflow).contains("Apply requires a deploy image pinned by digest.");
	}

}
