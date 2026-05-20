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

class ManualDevEcrPublishWorkflowPushContractTest {

	private static final Path WORKFLOW = Path.of(".github/workflows/manual-dev-ecr-publish.yml");

	@Test
	void workflowBuildsFromRootDockerfileAndPublishesViaRepositoryUriContract() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("REPOSITORY_URI: ${{ vars.REPOSITORY_URI }}");
		assertThat(workflow).contains("docker build --file Dockerfile --tag \"$REPOSITORY_URI:$IMAGE_TAG\" .");
		assertThat(workflow).contains("docker push \"$REPOSITORY_URI:$IMAGE_TAG\"");
		assertThat(workflow).doesNotContain("dev-petclinic");
		assertThat(workflow).contains("Published image:");
		assertThat(workflow).contains("Pushed digest:");
	}

	@Test
	void workflowPublishesOnlyTheFullGitShaTagAndSurfacesTraceabilityOutput() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).contains("IMAGE_TAG: ${{ github.sha }}");
		assertThat(workflow).contains("published_image=$REPOSITORY_URI:$IMAGE_TAG");
		assertThat(workflow).contains("image_digest=$(aws ecr describe-images");
		assertThat(workflow).contains("$GITHUB_STEP_SUMMARY");
		assertThat(workflow).doesNotContain("docker tag \"$REPOSITORY_URI:$IMAGE_TAG\" \"$REPOSITORY_URI:latest\"");
		assertThat(workflow).doesNotContain("docker push \"$REPOSITORY_URI:latest\"");
		assertThat(workflow).doesNotContain("docker push \"$REPOSITORY_URI:main\"");
	}

	@Test
	void workflowStopsAtImagePublicationAndDoesNotTriggerEcsRolloutSteps() throws IOException {
		assertThat(WORKFLOW).exists();

		String workflow = Files.readString(WORKFLOW);

		assertThat(workflow).doesNotContain("ecs register-task-definition");
		assertThat(workflow).doesNotContain("ecs update-service");
		assertThat(workflow).doesNotContain("amazon-ecs-deploy-task-definition");
		assertThat(workflow).doesNotContain("aws ecs update-service");
	}

}
