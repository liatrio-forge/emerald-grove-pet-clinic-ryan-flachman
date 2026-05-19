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

class TerraformEcrRepositoryContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	private static final Path LOCALS = APP_DEV_DIRECTORY.resolve("locals.tf");

	private static final Path README = APP_DEV_DIRECTORY.resolve("README.md");

	@Test
	void devAppStackDefinesOnePrivateImmutableEcrRepositoryForGitShaImages() throws IOException {
		assertThat(MAIN).exists();
		assertThat(LOCALS).exists();
		assertThat(README).exists();

		String main = Files.readString(MAIN);
		String locals = Files.readString(LOCALS);
		String readme = Files.readString(README);

		assertThat(main).contains("resource \"aws_ecr_repository\" \"app\"");
		assertThat(main).contains("name                 = local.ecr_repository_name");
		assertThat(main).contains("image_tag_mutability = \"IMMUTABLE\"");
		assertThat(main).contains("tags = merge(local.common_tags, {");
		assertThat(main).contains("RepositoryRole = \"application-image\"");
		assertThat(main).doesNotContain("image_tag_mutability = \"MUTABLE\"");
		assertThat(locals).contains("ecr_repository_name");
		assertThat(locals).contains("${var.environment}-petclinic");
		assertThat(readme).contains("immutable Git SHA tags are the only approved");
		assertThat(readme).contains("mutable convenience tags such as `latest` are intentionally excluded");
	}

}
