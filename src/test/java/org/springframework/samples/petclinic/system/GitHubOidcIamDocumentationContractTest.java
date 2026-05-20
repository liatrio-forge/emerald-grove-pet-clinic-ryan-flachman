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

class GitHubOidcIamDocumentationContractTest {

	private static final Path APP_DEV_README = Path.of("infra/terraform/app/dev/README.md");

	private static final Path FLOCI_README = Path.of("infra/terraform/floci/README.md");

	private static final Path VERIFY_SCRIPT = Path.of("scripts/verify-github-oidc-iam-contract.sh");

	@Test
	void repositoryDefinesOneReviewerFacingGithubOidcVerificationPath() throws IOException {
		assertThat(VERIFY_SCRIPT).exists();

		String script = Files.readString(VERIFY_SCRIPT);

		assertThat(script).contains("missing required file:");
		assertThat(script).contains("backend.hcl.example");
		assertThat(script).contains("terraform -chdir=infra/terraform/app/dev validate");
		assertThat(script).contains("terraform -chdir=infra/terraform/app/dev plan -no-color");
		assertThat(script).contains("AWS_ACCESS_KEY_ID=test");
		assertThat(script).contains("AWS_SECRET_ACCESS_KEY=test");
		assertThat(script).contains("AWS_EC2_METADATA_DISABLED=true");
	}

	@Test
	void operatorDocumentationIncludesExactEnvironmentNamesRoleResponsibilitiesAndNoLongLivedAwsKeys()
			throws IOException {
		assertThat(APP_DEV_README).exists();
		assertThat(FLOCI_README).exists();

		String appDevReadme = Files.readString(APP_DEV_README);
		String flociReadme = Files.readString(FLOCI_README);

		assertThat(appDevReadme).contains("terraform-apply-dev");
		assertThat(appDevReadme).contains("terraform-destroy-dev");
		assertThat(appDevReadme).contains("app-deploy-dev");
		assertThat(appDevReadme).contains("dev-destroy");
		assertThat(appDevReadme).contains("APP_DEPLOY_ROLE_ARN");
		assertThat(appDevReadme).contains("do not use long-lived AWS access keys");
		assertThat(flociReadme).contains("./scripts/verify-github-oidc-iam-contract.sh");
		assertThat(flociReadme).contains("AWS_ACCESS_KEY_ID=test");
		assertThat(flociReadme).contains("AWS_SECRET_ACCESS_KEY=test");
		assertThat(flociReadme).contains("AWS_EC2_METADATA_DISABLED=true");
	}

}
