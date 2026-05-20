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

class GitHubTerraformWorkflowRolesContractTest {

	private static final Path IDENTITY_DEV_DIRECTORY = Path.of("infra/terraform/identity/dev");

	private static final Path MAIN = IDENTITY_DEV_DIRECTORY.resolve("main.tf");

	private static final Path LOCALS = IDENTITY_DEV_DIRECTORY.resolve("locals.tf");

	private static final Path OUTPUTS = IDENTITY_DEV_DIRECTORY.resolve("outputs.tf");

	private static final Path README = IDENTITY_DEV_DIRECTORY.resolve("README.md");

	@Test
	void devIdentityStackDefinesSeparateApplyAndDestroyTerraformRoles() throws IOException {
		assertThat(MAIN).exists();
		assertThat(LOCALS).exists();
		assertThat(OUTPUTS).exists();

		String main = Files.readString(MAIN);
		String locals = Files.readString(LOCALS);
		String outputs = Files.readString(OUTPUTS);

		assertThat(main).contains("resource \"aws_iam_role\" \"terraform_apply_github_actions\"");
		assertThat(main).contains("resource \"aws_iam_role\" \"terraform_destroy_github_actions\"");
		assertThat(main).doesNotContain("resource \"aws_iam_role\" \"terraform_github_actions\"");
		assertThat(locals).contains("terraform_apply_role_name");
		assertThat(locals).contains("terraform_destroy_role_name");
		assertThat(outputs).contains("terraform_apply_role_arn");
		assertThat(outputs).contains("terraform_destroy_role_arn");
	}

	@Test
	void terraformApplyAndDestroyRolesTrustOnlyTheirExactProtectedEnvironmentSubjects() throws IOException {
		assertThat(MAIN).exists();

		String main = Files.readString(MAIN);

		assertThat(main).contains(
				"assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust[\"terraform_apply\"].json");
		assertThat(main).contains(
				"assume_role_policy = data.aws_iam_policy_document.github_actions_oidc_trust[\"terraform_destroy\"].json");
		assertThat(main).doesNotContain("repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:*");
	}

	@Test
	void terraformWorkflowPermissionsStayBroadForPocWithoutGrantingAdministratorAccess() throws IOException {
		assertThat(MAIN).exists();
		assertThat(README).exists();

		String main = Files.readString(MAIN);
		String readme = Files.readString(README);

		assertThat(main).contains("resource \"aws_iam_policy\" \"terraform_github_actions\"");
		assertThat(main).contains("\"ecs:*\"");
		assertThat(main).contains("\"elasticloadbalancing:*\"");
		assertThat(main).contains("\"dynamodb:DescribeTable\"");
		assertThat(main).contains("\"dynamodb:GetItem\"");
		assertThat(main).contains("\"dynamodb:PutItem\"");
		assertThat(main).contains("\"dynamodb:DeleteItem\"");
		assertThat(main).contains("\"iam:CreateRole\"");
		assertThat(main).contains("\"iam:PassRole\"");
		assertThat(main).doesNotContain("\"Action\": \"*\"");
		assertThat(main).doesNotContain("\"iam:*\"");
		assertThat(readme).contains("terraform-apply-dev");
		assertThat(readme).contains("terraform-destroy-dev");
		assertThat(readme).contains("dev-destroy");
		assertThat(readme).contains("IAM-sensitive actions");
	}

}
