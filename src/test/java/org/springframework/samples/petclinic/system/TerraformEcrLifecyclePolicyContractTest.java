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

class TerraformEcrLifecyclePolicyContractTest {

	private static final Path MAIN = Path.of("infra/terraform/app/dev/main.tf");

	private static final Path README = Path.of("infra/terraform/app/dev/README.md");

	@Test
	void devAppStackDefinesReadableCountBasedLifecycleRulesForTaggedAndUntaggedImages() throws IOException {
		assertThat(MAIN).exists();
		assertThat(README).exists();

		String main = Files.readString(MAIN);
		String readme = Files.readString(README);

		assertThat(main).contains("resource \"aws_ecr_lifecycle_policy\" \"app\"");
		assertThat(main).contains("repository = aws_ecr_repository.app.name");
		assertThat(main).contains("tagStatus   = \"untagged\"");
		assertThat(main).contains("countType   = \"imageCountMoreThan\"");
		assertThat(main).contains("countNumber = 5");
		assertThat(main).doesNotContain("countType   = \"sinceImagePushed\"");
		assertThat(readme).contains("lifecycle policy preview");
		assertThat(readme).contains("untagged images");
		assertThat(readme).contains("most recent 5 tagged Git SHA images");
	}

}
