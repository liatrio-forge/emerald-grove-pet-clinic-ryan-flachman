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

class TerraformApplyWorkflowPlanContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/terraform-apply-dev.yml");

	@Test
	void workflowRequestsOnlyMinimalPermissionsAndUsesOidcForAwsAuthentication() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("permissions:");
		assertThat(workflow).contains("contents: read");
		assertThat(workflow).contains("id-token: write");
		assertThat(workflow).doesNotContain("packages: write");
		assertThat(workflow).doesNotContain("security-events: write");
		assertThat(workflow).contains("aws-actions/configure-aws-credentials@v4");
		assertThat(workflow).contains("role-to-assume:");
		assertThat(workflow).contains("aws-region:");
		assertThat(workflow).doesNotContain("aws-access-key-id:");
		assertThat(workflow).doesNotContain("aws-secret-access-key:");
	}

	@Test
	void workflowInitializesTerraformWithExternalizedBackendInputsAndCreatesAReviewedSavedPlanArtifact()
			throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("TF_BACKEND_CONFIG_FILE: infra/terraform/app/dev/backend.hcl");
		assertThat(workflow).doesNotContain("TF_BACKEND_CONFIG_FILE: ${{ runner.temp }}/backend.hcl");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/app/dev init -input=false");
		assertThat(workflow).contains("-backend-config=\"$TF_BACKEND_CONFIG_FILE\"");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/app/dev plan -out=tfplan -input=false");
		assertThat(workflow).contains("> terraform-plan.txt");
		assertThat(workflow).contains("name: terraform-apply-dev-plan");
		assertThat(workflow).contains("path: |");
		assertThat(workflow).contains("infra/terraform/app/dev/tfplan");
		assertThat(workflow).contains("infra/terraform/app/dev/terraform-plan.txt");
	}

}
