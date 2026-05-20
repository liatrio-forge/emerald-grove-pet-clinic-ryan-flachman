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

class TerraformBootstrapDestroyWorkflowContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/bootstrap-destroy-dev-infra.yml");

	@Test
	void workflowExistsAndIsManualMainOnlyWithTypedConfirmationAndBootstrapSecrets() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("name: Bootstrap Destroy Dev Infrastructure");
		assertThat(workflow).contains("workflow_dispatch:");
		assertThat(workflow).contains("confirmation:");
		assertThat(workflow).contains("Type destroy bootstrap dev to confirm the final dev foundation teardown.");
		assertThat(workflow).contains("github.ref == 'refs/heads/main'");
		assertThat(workflow).contains("environment: dev-bootstrap");
		assertThat(workflow).contains("BOOTSTRAP_AWS_ACCESS_KEY_ID");
		assertThat(workflow).contains("BOOTSTRAP_AWS_SECRET_ACCESS_KEY");
		assertThat(workflow).doesNotContain("role-to-assume:");
		assertThat(workflow).doesNotContain("id-token: write");
		assertThat(workflow).doesNotContain("secrets:");
	}

	@Test
	void workflowDestroysAppThenIdentityThenStateAndSummarizesCleanupHandoff() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("uses: hashicorp/setup-terraform@v3");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/app/dev destroy -auto-approve -input=false");
		assertThat(workflow)
			.contains("terraform -chdir=infra/terraform/identity/dev destroy -auto-approve -input=false");
		assertThat(workflow).contains("terraform -chdir=infra/terraform/state/dev destroy -auto-approve -input=false");
		assertThat(workflow).contains("blank the AWS-derived GitHub variable values");
		assertThat(workflow).contains("dev-bootstrap secrets remain in place");
	}

}
