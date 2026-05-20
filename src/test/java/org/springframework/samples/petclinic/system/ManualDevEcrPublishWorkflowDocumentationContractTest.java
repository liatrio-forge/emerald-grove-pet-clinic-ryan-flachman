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

class ManualDevEcrPublishWorkflowDocumentationContractTest {

	private static final Path ROOT_README = Path.of("README.md");

	private static final Path APP_DEV_README = Path.of("infra/terraform/app/dev/README.md");

	@Test
	void repositoryDocumentsTheManualPublishWorkflowContractAndSafetyModel() throws IOException {
		assertThat(ROOT_README).exists();
		assertThat(APP_DEV_README).exists();

		String rootReadme = Files.readString(ROOT_README);
		String appDevReadme = Files.readString(APP_DEV_README);

		assertThat(rootReadme).contains("Manual Dev ECR Publish");
		assertThat(rootReadme).contains("workflow_dispatch");
		assertThat(rootReadme).contains("publish dev image");
		assertThat(appDevReadme).contains("main branch");
		assertThat(appDevReadme).contains("protected `dev` environment");
		assertThat(appDevReadme).contains("immutable Git SHA");
	}

	@Test
	void repositoryDocumentsPublishVariablesVerificationCommandsAndScopeBoundaries() throws IOException {
		assertThat(ROOT_README).exists();
		assertThat(APP_DEV_README).exists();

		String rootReadme = Files.readString(ROOT_README);
		String appDevReadme = Files.readString(APP_DEV_README);

		assertThat(rootReadme).contains("GitHub OIDC");
		assertThat(appDevReadme).contains("APP_PUBLISH_ROLE_ARN");
		assertThat(appDevReadme).contains("REPOSITORY_URI");
		assertThat(appDevReadme).contains("AWS_REGION");
		assertThat(appDevReadme).contains("gh workflow run");
		assertThat(appDevReadme).contains("gh run view --log");
		assertThat(appDevReadme).contains("aws ecr describe-images");
		assertThat(appDevReadme).contains("long-lived AWS access keys");
		assertThat(appDevReadme).contains("automatic publish");
		assertThat(appDevReadme).contains("ECS rollout");
		assertThat(appDevReadme).contains("mutable");
		assertThat(appDevReadme).contains("verification script");
	}

}
