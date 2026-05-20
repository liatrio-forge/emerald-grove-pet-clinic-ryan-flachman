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

class GitHubIdentityStackDocumentationContractTest {

	private static final Path ROOT_README = Path.of("README.md");

	private static final Path STATE_README = Path.of("infra/terraform/state/dev/README.md");

	private static final Path IDENTITY_README = Path.of("infra/terraform/identity/dev/README.md");

	private static final Path APP_README = Path.of("infra/terraform/app/dev/README.md");

	@Test
	void repositoryDocumentsTheThreeLayerLifecycleBoundary() throws IOException {
		assertThat(ROOT_README).exists();
		assertThat(STATE_README).exists();
		assertThat(IDENTITY_README).exists();
		assertThat(APP_README).exists();

		String root = Files.readString(ROOT_README);
		String state = Files.readString(STATE_README);
		String identity = Files.readString(IDENTITY_README);
		String app = Files.readString(APP_README);

		assertThat(root).contains("state/dev");
		assertThat(root).contains("identity/dev");
		assertThat(root).contains("app/dev");
		assertThat(state).contains("state/dev");
		assertThat(state).contains("identity/dev");
		assertThat(state).contains("app/dev");
		assertThat(identity).contains("GitHub OIDC provider");
		assertThat(identity).contains("Terraform apply");
		assertThat(identity).contains("Terraform destroy");
		assertThat(identity).contains("app publish");
		assertThat(identity).contains("app deploy");
		assertThat(app).contains("identity/dev");
		assertThat(app).contains("runtime infrastructure");
		assertThat(app).contains("does not own the GitHub OIDC provider");
	}

}
