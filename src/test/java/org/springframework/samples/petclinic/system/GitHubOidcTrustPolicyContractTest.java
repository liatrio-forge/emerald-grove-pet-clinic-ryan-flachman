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

class GitHubOidcTrustPolicyContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	private static final Path LOCALS = APP_DEV_DIRECTORY.resolve("locals.tf");

	@Test
	void devAppStackDefinesOneGitHubOidcProviderForGitHubActions() throws IOException {
		assertThat(MAIN).exists();
		assertThat(LOCALS).exists();

		String main = Files.readString(MAIN);
		String locals = Files.readString(LOCALS);

		assertThat(main).containsOnlyOnce("resource \"aws_iam_openid_connect_provider\" \"github_actions\"");
		assertThat(main).contains("https://token.actions.githubusercontent.com");
		assertThat(main).contains("sts.amazonaws.com");
		assertThat(locals).contains("github_oidc_provider_url");
		assertThat(locals).contains("token.actions.githubusercontent.com");
		assertThat(locals).contains("github_repository");
		assertThat(locals).contains("liatrio-forge/emerald-grove-pet-clinic-ryan-flachman");
	}

	@Test
	void everyGitHubAssumableRoleUsesExactAudienceAndRepositoryBoundSubjectWithoutWildcardTrust() throws IOException {
		assertThat(MAIN).exists();
		assertThat(LOCALS).exists();

		String main = Files.readString(MAIN);
		String locals = Files.readString(LOCALS);

		assertThat(main).contains("variable = \"token.actions.githubusercontent.com:aud\"");
		assertThat(main).contains("values   = [\"sts.amazonaws.com\"]");
		assertThat(main).contains("variable = \"token.actions.githubusercontent.com:sub\"");
		assertThat(locals).contains("github_repository");
		assertThat(locals).contains("terraform_apply");
		assertThat(locals).contains("repo:${local.github_repository}:environment:dev");
		assertThat(locals).contains("terraform_destroy");
		assertThat(locals).contains("repo:${local.github_repository}:environment:dev-destroy");
		assertThat(main).doesNotContain("repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:*");
		assertThat(locals).doesNotContain("repo:liatrio-forge/emerald-grove-pet-clinic-ryan-flachman:*");
	}

}
