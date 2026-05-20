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

class GitHubDeployRoleAndConfigurationContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	private static final Path OUTPUTS = APP_DEV_DIRECTORY.resolve("outputs.tf");

	private static final Path README = APP_DEV_DIRECTORY.resolve("README.md");

	private static final Path WORKFLOW = Path.of(".github/workflows/terraform-apply-dev.yml");

	@Test
	void devAppStackDefinesOneDeployRoleSeparateFromTerraformRoles() throws IOException {
		assertThat(MAIN).exists();
		assertThat(OUTPUTS).exists();

		String main = Files.readString(MAIN);
		String outputs = Files.readString(OUTPUTS);

		assertThat(main).contains("resource \"aws_iam_role\" \"app_deploy_github_actions\"");
		assertThat(main).doesNotContain(
				"name = local.terraform_apply_role_name\n  assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust[\"app_deploy\"].json");
		assertThat(outputs).contains("app_deploy_role_arn");
	}

	@Test
	void deployRoleTrustsProtectedDevEnvironmentAndStaysNarrowerThanTerraformRoles() throws IOException {
		assertThat(MAIN).exists();

		String main = Files.readString(MAIN);

		assertThat(main).contains(
				"assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust[\"app_deploy\"].json");
		assertThat(main).contains("resource \"aws_iam_policy\" \"app_deploy_github_actions\"");
		assertThat(main).contains("\"ecs:RegisterTaskDefinition\"");
		assertThat(main).contains("\"ecs:UpdateService\"");
		assertThat(main).contains("\"ecs:DescribeServices\"");
		assertThat(main).contains("\"iam:PassRole\"");
		assertThat(main).contains("resource \"aws_iam_policy\" \"app_deploy_github_actions\"");
	}

	@Test
	void workflowAndDocumentationUseProtectedEnvironmentsAndDistinctRoleVariables() throws IOException {
		assertThat(WORKFLOW).exists();
		assertThat(README).exists();

		String workflow = Files.readString(WORKFLOW);
		String readme = Files.readString(README);

		assertThat(workflow).contains("id-token: write");
		assertThat(workflow).contains("environment: dev");
		assertThat(workflow).contains("TERRAFORM_APPLY_ROLE_ARN");
		assertThat(workflow).doesNotContain("AWS_ACCESS_KEY_ID");
		assertThat(workflow).doesNotContain("AWS_SECRET_ACCESS_KEY");
		assertThat(readme).contains("AWS_REGION");
		assertThat(readme).contains("TERRAFORM_APPLY_ROLE_ARN");
		assertThat(readme).contains("TERRAFORM_DESTROY_ROLE_ARN");
		assertThat(readme).contains("APP_DEPLOY_ROLE_ARN");
		assertThat(readme).contains("TF_STATE_BUCKET");
		assertThat(readme).contains("TF_LOCK_TABLE");
		assertThat(readme).contains("Environment-scoped");
		assertThat(readme).contains("Repository-scoped");
	}

}
