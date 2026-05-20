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

class TerraformBootstrapWorkflowContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/bootstrap-dev-infra.yml");

	@Test
	void workflowExistsAndIsManualOnly() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("name: Bootstrap Dev Infrastructure");
		assertThat(workflow).contains("workflow_dispatch:");
		assertThat(workflow).contains("confirmation:");
		assertThat(workflow).doesNotContain("bootstrap_image:");
		assertThat(workflow).doesNotContain("push:");
		assertThat(workflow).doesNotContain("pull_request:");
	}

	@Test
	void workflowUsesProtectedBootstrapEnvironmentAndAdminCredentialSecrets() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("Type bootstrap dev to confirm the one-time dev bootstrap.");
		assertThat(workflow).contains("github.ref == 'refs/heads/main'");
		assertThat(workflow).contains("environment: dev-bootstrap");
		assertThat(workflow).contains("BOOTSTRAP_AWS_ACCESS_KEY_ID");
		assertThat(workflow).contains("BOOTSTRAP_AWS_SECRET_ACCESS_KEY");
		assertThat(workflow).contains("aws-access-key-id: ${{ secrets.BOOTSTRAP_AWS_ACCESS_KEY_ID }}");
		assertThat(workflow).contains("aws-secret-access-key: ${{ secrets.BOOTSTRAP_AWS_SECRET_ACCESS_KEY }}");
		assertThat(workflow).doesNotContain("role-to-assume:");
		assertThat(workflow).doesNotContain("id-token: write");
	}

	@Test
	void workflowBootstrapsStateThenIdentityThenAppStackAndSurfacesGitHubVariableOutputs() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("terraform -chdir=infra/terraform/state/dev init -backend=false");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/state/dev apply -auto-approve -input=false");
		assertThat(workflow).contains(
				"terraform -chdir=infra/terraform/identity/dev init -input=false -backend-config=infra/terraform/identity/dev/backend.hcl");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/identity/dev apply -auto-approve -input=false");
		assertThat(workflow).contains(
				"terraform -chdir=infra/terraform/app/dev init -input=false -backend-config=\"$TF_BACKEND_CONFIG_FILE\"");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/app/dev plan -out=tfplan -input=false");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/app/dev apply -input=false tfplan");
		assertThat(workflow)
			.contains("terraform -chdir=infra/terraform/identity/dev output -raw terraform_apply_role_arn");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/identity/dev output -raw app_publish_role_arn");
		assertThat(workflow).contains("repository_uri");
		assertThat(workflow).contains("TF_STATE_BUCKET");
		assertThat(workflow).contains("TF_LOCK_TABLE");
		assertThat(workflow).contains("Publish one immutable Git SHA image to the new repository.");
		assertThat(workflow).contains("Run Terraform Apply Dev with that exact digest to create the ECS runtime.");
	}

}
