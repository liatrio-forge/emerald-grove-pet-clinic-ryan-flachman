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

class ManualDevEcrPublishWorkflowBuildAndAuthContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/manual-dev-ecr-publish.yml");

	@Test
	void workflowPackagesTheApplicationBeforeDockerBuildAndPublishSteps() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("./mvnw package -DskipTests");
		assertThat(workflow).contains("docker build");
		assertThat(workflow).contains("docker push");
		assertThat(workflow.indexOf("./mvnw package -DskipTests")).isLessThan(workflow.indexOf("docker build"));
		assertThat(workflow.indexOf("docker build")).isLessThan(workflow.indexOf("docker push"));
	}

	@Test
	void workflowRequestsMinimalPermissionsAndUsesOidcPublishRoleConfiguration() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("permissions:");
		assertThat(workflow).contains("contents: read");
		assertThat(workflow).contains("id-token: write");
		assertThat(workflow).doesNotContain("packages: write");
		assertThat(workflow).doesNotContain("security-events: write");
		assertThat(workflow).contains("aws-actions/configure-aws-credentials@v4");
		assertThat(workflow).contains("role-to-assume: ${{ vars.APP_PUBLISH_ROLE_ARN }}");
		assertThat(workflow).contains("aws-region: ${{ env.AWS_REGION }}");
		assertThat(workflow).doesNotContain("aws-access-key-id:");
		assertThat(workflow).doesNotContain("aws-secret-access-key:");
	}

}
