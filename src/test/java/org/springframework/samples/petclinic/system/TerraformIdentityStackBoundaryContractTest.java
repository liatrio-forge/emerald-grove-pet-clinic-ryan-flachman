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

class TerraformIdentityStackBoundaryContractTest {

	private static final Path IDENTITY_DEV_DIRECTORY = Path.of("infra/terraform/identity/dev");

	private static final Path IDENTITY_MAIN = IDENTITY_DEV_DIRECTORY.resolve("main.tf");

	private static final Path IDENTITY_LOCALS = IDENTITY_DEV_DIRECTORY.resolve("locals.tf");

	private static final Path IDENTITY_OUTPUTS = IDENTITY_DEV_DIRECTORY.resolve("outputs.tf");

	private static final Path APP_MAIN = Path.of("infra/terraform/app/dev/main.tf");

	@Test
	void dedicatedIdentityStackOwnsGithubOidcProviderAndWorkflowRoles() throws IOException {
		assertThat(IDENTITY_DEV_DIRECTORY).isDirectory();
		assertThat(IDENTITY_MAIN).exists();
		assertThat(IDENTITY_LOCALS).exists();
		assertThat(IDENTITY_OUTPUTS).exists();

		String main = Files.readString(IDENTITY_MAIN);
		String locals = Files.readString(IDENTITY_LOCALS);
		String outputs = Files.readString(IDENTITY_OUTPUTS);

		assertThat(main).containsOnlyOnce("resource \"aws_iam_openid_connect_provider\" \"github_actions\"");
		assertThat(main).contains("resource \"aws_iam_role\" \"terraform_apply_github_actions\"");
		assertThat(main).contains("resource \"aws_iam_role\" \"terraform_destroy_github_actions\"");
		assertThat(main).contains("resource \"aws_iam_role\" \"app_publish_github_actions\"");
		assertThat(main).contains("resource \"aws_iam_role\" \"app_deploy_github_actions\"");
		assertThat(locals).contains("github_actions_subjects");
		assertThat(outputs).contains("terraform_apply_role_arn");
		assertThat(outputs).contains("terraform_destroy_role_arn");
		assertThat(outputs).contains("app_publish_role_arn");
		assertThat(outputs).contains("app_deploy_role_arn");
	}

	@Test
	void appStackNoLongerDeclaresGithubOidcProviderOrGithubWorkflowRoles() throws IOException {
		assertThat(APP_MAIN).exists();

		String appMain = Files.readString(APP_MAIN);

		assertThat(appMain).doesNotContain("resource \"aws_iam_openid_connect_provider\" \"github_actions\"");
		assertThat(appMain).doesNotContain("resource \"aws_iam_role\" \"terraform_apply_github_actions\"");
		assertThat(appMain).doesNotContain("resource \"aws_iam_role\" \"terraform_destroy_github_actions\"");
		assertThat(appMain).doesNotContain("resource \"aws_iam_role\" \"app_publish_github_actions\"");
		assertThat(appMain).doesNotContain("resource \"aws_iam_role\" \"app_deploy_github_actions\"");
		assertThat(appMain).doesNotContain("resource \"aws_iam_policy\" \"terraform_github_actions\"");
		assertThat(appMain).doesNotContain("resource \"aws_iam_policy\" \"app_publish_github_actions\"");
		assertThat(appMain).doesNotContain("resource \"aws_iam_policy\" \"app_deploy_github_actions\"");
	}

}
