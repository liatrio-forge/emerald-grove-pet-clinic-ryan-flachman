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

class TerraformApplyWorkflowDocumentationContractTest {

	private static final Path ROOT_README = Path.of("README.md");

	private static final Path APP_DEV_README = Path.of("infra/terraform/app/dev/README.md");

	@Test
	void repositoryDocumentsTheManualApplyWorkflowContractAndSafetyModel() throws IOException {
		assertThat(ROOT_README).exists();
		assertThat(APP_DEV_README).exists();

		String rootReadme = Files.readString(ROOT_README);
		String appDevReadme = Files.readString(APP_DEV_README);

		assertThat(rootReadme).contains("Terraform Apply Dev");
		assertThat(rootReadme).contains("workflow_dispatch");
		assertThat(rootReadme).contains("apply dev");
		assertThat(rootReadme).contains("deploy image reference pinned by digest");
		assertThat(appDevReadme).contains("main branch");
		assertThat(appDevReadme).contains("reviewer approval");
		assertThat(appDevReadme).contains("exact saved Terraform plan");
		assertThat(appDevReadme).contains("dev environment only");
	}

	@Test
	void repositoryDocumentsOidcBackendReuseAndExplicitScopeBoundaries() throws IOException {
		assertThat(ROOT_README).exists();
		assertThat(APP_DEV_README).exists();

		String rootReadme = Files.readString(ROOT_README);
		String appDevReadme = Files.readString(APP_DEV_README);

		assertThat(rootReadme).contains("GitHub OIDC");
		assertThat(appDevReadme).contains("backend.hcl.example");
		assertThat(appDevReadme).contains("deploy_image");
		assertThat(appDevReadme).contains("post-publish ECS deployment step");
		assertThat(appDevReadme).contains("destroy workflow");
		assertThat(appDevReadme).contains("verification commands");
		assertThat(appDevReadme).contains("gh run view");
	}

}
