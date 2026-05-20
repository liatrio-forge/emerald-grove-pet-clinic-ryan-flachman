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

class TerraformFinalCleanupDocumentationContractTest {

	private static final Path ROOT_README = Path.of("README.md");

	private static final Path APP_README = Path.of("infra/terraform/app/dev/README.md");

	@Test
	void repositoryDocumentsFinalCleanupVariableResetSequenceAndPersistentBootstrapException() throws IOException {
		assertThat(ROOT_README).exists();
		assertThat(APP_README).exists();

		String rootReadme = Files.readString(ROOT_README);
		String appReadme = Files.readString(APP_README);

		assertThat(rootReadme).contains("Bootstrap Destroy Dev State Backend");
		assertThat(rootReadme).contains("blank the AWS-derived GitHub variable values");
		assertThat(rootReadme).contains("preserving the variable names");
		assertThat(rootReadme).contains("dev-bootstrap secrets remain stored by design");
		assertThat(appReadme).contains("Run `Bootstrap Destroy Dev State Backend` after the tracked teardown");
		assertThat(appReadme).contains("set the AWS-derived GitHub variable values to empty strings");
		assertThat(appReadme).contains("preserve the variable names for future reuse");
		assertThat(appReadme).contains("standing POC bootstrap exception");
	}

}
