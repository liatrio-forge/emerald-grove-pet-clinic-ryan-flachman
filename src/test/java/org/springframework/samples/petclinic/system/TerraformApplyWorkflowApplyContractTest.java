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

class TerraformApplyWorkflowApplyContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/terraform-apply-dev.yml");

	@Test
	void workflowReinitializesTerraformBeforeApplyingThePreviouslySavedPlanArtifact() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);
		String applyJob = workflow.substring(workflow.indexOf("  apply-dev:"));

		assertThat(workflow).contains("concurrency:");
		assertThat(workflow).contains("group: terraform-apply-dev");
		assertThat(workflow).contains("cancel-in-progress: false");
		assertThat(applyJob).contains("environment: dev");
		assertThat(applyJob).contains("actions/download-artifact@v4");
		assertThat(applyJob).contains("name: terraform-apply-dev-plan");
		assertThat(applyJob).contains("TF_BACKEND_CONFIG_FILE: infra/terraform/app/dev/backend.hcl");
		assertThat(applyJob).contains("bucket         = \"${{ vars.TF_STATE_BUCKET }}\"");
		assertThat(applyJob).contains("key            = \"app/dev/terraform.tfstate\"");
		assertThat(applyJob).contains("dynamodb_table = \"${{ vars.TF_LOCK_TABLE }}\"");
		assertThat(applyJob)
			.contains("terraform -chdir=infra/terraform/app/dev init -input=false -backend-config=backend.hcl");
		assertThat(applyJob).contains("terraform apply -input=false tfplan");
		assertThat(applyJob).doesNotContain("terraform apply -auto-approve");
	}

	@Test
	void workflowFailsClearlyForMissingConfigurationMissingPlanAndNonZeroApply() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("Terraform apply role ARN is required.");
		assertThat(workflow).contains("Terraform backend bucket is required.");
		assertThat(workflow).contains("Terraform lock table is required.");
		assertThat(workflow).contains("Reviewed plan artifact is unavailable.");
		assertThat(workflow).contains("Terraform apply failed.");
		assertThat(workflow).contains("if [[ ! -f infra/terraform/app/dev/tfplan ]]");
	}

}
