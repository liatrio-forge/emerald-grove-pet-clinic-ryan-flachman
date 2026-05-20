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

class TerraformStateBootstrapWorkflowContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/bootstrap-dev-state.yml");

	@Test
	void workflowExistsAndUsesProtectedBootstrapSecretsWithoutOidc() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("name: Bootstrap Dev State Backend");
		assertThat(workflow).contains("workflow_dispatch:");
		assertThat(workflow).contains("confirmation:");
		assertThat(workflow).contains("Type bootstrap dev state to confirm the one-time dev backend bootstrap.");
		assertThat(workflow).contains("environment: dev-bootstrap");
		assertThat(workflow).contains("BOOTSTRAP_AWS_ACCESS_KEY_ID");
		assertThat(workflow).contains("BOOTSTRAP_AWS_SECRET_ACCESS_KEY");
		assertThat(workflow).doesNotContain("role-to-assume:");
		assertThat(workflow).doesNotContain("id-token: write");
	}

	@Test
	void workflowBootstrapsOnlyTheBackendResourcesWithExplicitAwsCallsAndSummarizesOutputs() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("STATE_BUCKET_NAME");
		assertThat(workflow).contains("LOCK_TABLE_NAME");
		assertThat(workflow).contains("aws s3api create-bucket");
		assertThat(workflow).contains("aws s3api put-bucket-versioning");
		assertThat(workflow).contains("aws s3api put-bucket-encryption");
		assertThat(workflow).contains("aws s3api put-public-access-block");
		assertThat(workflow).contains("aws dynamodb create-table");
		assertThat(workflow).contains("TF_STATE_BUCKET");
		assertThat(workflow).contains("TF_LOCK_TABLE");
		assertThat(workflow).contains("Run Bootstrap Dev Infrastructure after promoting these backend values.");
		assertThat(workflow).doesNotContain("terraform -chdir=infra/terraform/identity/dev");
		assertThat(workflow).doesNotContain("terraform -chdir=infra/terraform/app/dev");
	}

}
