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

class ProductionContainerBuildContractTest {

	private static final Path DOCKERFILE = Path.of("Dockerfile");

	private static final Path DOCKERIGNORE = Path.of(".dockerignore");

	@Test
	void dockerfileDefinesAMultiStageMavenWrapperBuildAndLeanRuntimeImage() throws IOException {
		assertThat(DOCKERFILE).as("The repository-owned Dockerfile should exist at the project root").exists();

		String dockerfile = Files.readString(DOCKERFILE);

		assertThat(dockerfile).contains("FROM");
		assertThat(dockerfile).containsPattern("(?s)FROM\\s+.+\\s+AS\\s+build");
		assertThat(dockerfile).contains("./mvnw");
		assertThat(dockerfile).containsPattern("(?s)FROM\\s+.+\\s+AS\\s+runtime");
		assertThat(dockerfile).contains("COPY --from=build");
		assertThat(dockerfile).containsPattern("(?s)(ENTRYPOINT|CMD)\\s*\\[.*java.*-jar.*\\]");

		String runtimeStage = dockerfile.substring(dockerfile.indexOf("FROM eclipse-temurin:17-jre AS runtime"));
		assertThat(runtimeStage).doesNotContain("COPY src");
	}

	@Test
	void dockerignoreExcludesLocalBuildOutputsAndNonRuntimeInputs() throws IOException {
		assertThat(DOCKERIGNORE).as("The repository-owned .dockerignore should exist at the project root").exists();

		String dockerignore = Files.readString(DOCKERIGNORE);

		assertThat(dockerignore).contains(".git");
		assertThat(dockerignore).contains(".agents");
		assertThat(dockerignore).contains("docs/specs");
		assertThat(dockerignore).contains("target");
		assertThat(dockerignore).contains("test-results");
	}

}
