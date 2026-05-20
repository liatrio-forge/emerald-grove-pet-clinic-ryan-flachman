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

class GitHubLifecycleConfigurationContractTest {

	private static final Path ROOT_README = Path.of("README.md");

	private static final Path APP_README = Path.of("infra/terraform/app/dev/README.md");

	private static final Path SCREENSHOT = Path
		.of("docs/specs/34-spec-full-dev-infra-lifecycle-cleanup/34-proofs/34-task-04-github-config-sanitized.png");

	@Test
	void repositoryListsTheExactLifecycleVariablesAndProtectedEnvironments() throws IOException {
		assertThat(ROOT_README).exists();
		assertThat(APP_README).exists();

		String rootReadme = Files.readString(ROOT_README);
		String appReadme = Files.readString(APP_README);

		assertThat(rootReadme).contains("dev");
		assertThat(rootReadme).contains("dev-destroy");
		assertThat(rootReadme).contains("dev-bootstrap");
		assertThat(appReadme).contains("AWS_REGION");
		assertThat(appReadme).contains("TERRAFORM_APPLY_ROLE_ARN");
		assertThat(appReadme).contains("TERRAFORM_DESTROY_ROLE_ARN");
		assertThat(appReadme).contains("APP_PUBLISH_ROLE_ARN");
		assertThat(appReadme).contains("APP_DEPLOY_ROLE_ARN");
		assertThat(appReadme).contains("REPOSITORY_URI");
		assertThat(appReadme).contains("TF_STATE_BUCKET");
		assertThat(appReadme).contains("TF_LOCK_TABLE");
	}

	@Test
	void repositoryIncludesSanitizedReviewerFacingGithubConfigurationEvidence() {
		assertThat(SCREENSHOT).exists();
	}

}
