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

class TerraformDestroyDevWorkflowContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/terraform-destroy-dev.yml");

	@Test
	void workflowExistsAndIsManualMainOnlyWithTypedConfirmation() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("name: Terraform Destroy Dev");
		assertThat(workflow).contains("workflow_dispatch:");
		assertThat(workflow).contains("confirmation:");
		assertThat(workflow).contains("Type destroy dev to confirm the dev Terraform destroy.");
		assertThat(workflow).contains("github.ref == 'refs/heads/main'");
		assertThat(workflow).doesNotContain("push:");
		assertThat(workflow).doesNotContain("pull_request:");
	}

	@Test
	void workflowUsesProtectedDestroyEnvironmentOidcAndStableBackendVariables() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("permissions:");
		assertThat(workflow).contains("id-token: write");
		assertThat(workflow).contains("environment: dev-destroy");
		assertThat(workflow).contains("TERRAFORM_DESTROY_ROLE_ARN");
		assertThat(workflow).contains("TF_STATE_BUCKET");
		assertThat(workflow).contains("TF_LOCK_TABLE");
		assertThat(workflow).contains("role-to-assume: ${{ vars.TERRAFORM_DESTROY_ROLE_ARN }}");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/app/dev init -input=false");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/app/dev destroy -auto-approve -input=false");
		assertThat(workflow).doesNotContain("aws-access-key-id:");
		assertThat(workflow).doesNotContain("aws-secret-access-key:");
		assertThat(workflow).doesNotContain("infra/terraform/state/dev");
		assertThat(workflow).doesNotContain("infra/terraform/identity/dev");
	}

}
