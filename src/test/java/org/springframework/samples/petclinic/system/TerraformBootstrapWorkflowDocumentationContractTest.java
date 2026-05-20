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

class TerraformBootstrapWorkflowDocumentationContractTest {

	private static final Path ROOT_README = Path.of("README.md");

	private static final Path APP_DEV_README = Path.of("infra/terraform/app/dev/README.md");

	@Test
	void repositoryDocumentsTheBootstrapWorkflowContractAndTemporaryCredentialException() throws IOException {
		assertThat(ROOT_README).exists();
		assertThat(APP_DEV_README).exists();

		String rootReadme = Files.readString(ROOT_README);
		String appDevReadme = Files.readString(APP_DEV_README);

		assertThat(rootReadme).contains("Bootstrap Dev Infrastructure");
		assertThat(rootReadme).contains("bootstrap dev");
		assertThat(appDevReadme).contains("dev-bootstrap");
		assertThat(appDevReadme).contains("BOOTSTRAP_AWS_ACCESS_KEY_ID");
		assertThat(appDevReadme).contains("BOOTSTRAP_AWS_SECRET_ACCESS_KEY");
		assertThat(appDevReadme).contains("one-time bootstrap exception");
		assertThat(appDevReadme).contains("remove the bootstrap secrets");
	}

	@Test
	void repositoryDocumentsBootstrapOutputsThatMustBePromotedIntoGitHubVariables() throws IOException {
		assertThat(APP_DEV_README).exists();

		String appDevReadme = Files.readString(APP_DEV_README);

		assertThat(appDevReadme).contains("TERRAFORM_APPLY_ROLE_ARN");
		assertThat(appDevReadme).contains("TERRAFORM_DESTROY_ROLE_ARN");
		assertThat(appDevReadme).contains("APP_PUBLISH_ROLE_ARN");
		assertThat(appDevReadme).contains("APP_DEPLOY_ROLE_ARN");
		assertThat(appDevReadme).contains("REPOSITORY_URI");
		assertThat(appDevReadme).contains("TF_STATE_BUCKET");
		assertThat(appDevReadme).contains("TF_LOCK_TABLE");
	}

}
