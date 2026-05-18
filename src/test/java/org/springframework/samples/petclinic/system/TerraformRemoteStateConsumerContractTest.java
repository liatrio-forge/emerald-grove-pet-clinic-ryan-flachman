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

class TerraformRemoteStateConsumerContractTest {

	private static final Path APP_DEV_DIRECTORY = Path.of("infra/terraform/app/dev");

	private static final Path APP_MAIN = APP_DEV_DIRECTORY.resolve("main.tf");

	private static final Path APP_BACKEND = APP_DEV_DIRECTORY.resolve("backend.hcl.example");

	private static final Path APP_README = APP_DEV_DIRECTORY.resolve("README.md");

	private static final Path FLOCI_DIRECTORY = Path.of("infra/terraform/floci");

	private static final Path FLOCI_BACKEND = FLOCI_DIRECTORY.resolve("backend.hcl.example");

	private static final Path FLOCI_README = FLOCI_DIRECTORY.resolve("README.md");

	private static final Path FLOCI_COMPOSE = FLOCI_DIRECTORY.resolve("docker-compose.yml");

	@Test
	void downstreamDevStackUsesPartialBackendConfigurationAndStableStateKey() throws IOException {
		assertThat(APP_MAIN).exists();
		assertThat(APP_BACKEND).exists();
		assertThat(APP_README).exists();

		String main = Files.readString(APP_MAIN);
		String backend = Files.readString(APP_BACKEND);
		String readme = Files.readString(APP_README);

		assertThat(main).contains("backend \"s3\" {}");
		assertThat(main).doesNotContain("bucket =");
		assertThat(main).doesNotContain("dynamodb_table =");
		assertThat(backend).contains("bucket = \"emerald-grove-pet-clinic-dev-terraform-state\"");
		assertThat(backend).contains("key = \"app/dev/terraform.tfstate\"");
		assertThat(backend).contains("region = \"us-east-1\"");
		assertThat(backend).contains("dynamodb_table = \"emerald-grove-pet-clinic-dev-terraform-locks\"");
		assertThat(backend).contains("skip_credentials_validation = true");
		assertThat(backend).contains("skip_requesting_account_id = true");
		assertThat(backend).contains("skip_metadata_api_check = true");
		assertThat(backend).contains("skip_region_validation = true");
		assertThat(readme).contains("state/dev");
		assertThat(readme).contains("remote state is already managed by the state/dev stack");
		assertThat(readme).contains("app/dev/terraform.tfstate");
		assertThat(readme).contains("GitHub Actions");
	}

	@Test
	void flociGuidanceUsesComposeManagedLocalAwsEndpoints() throws IOException {
		assertThat(FLOCI_BACKEND).exists();
		assertThat(FLOCI_README).exists();
		assertThat(FLOCI_COMPOSE).exists();

		String backend = Files.readString(FLOCI_BACKEND);
		String readme = Files.readString(FLOCI_README);
		String compose = Files.readString(FLOCI_COMPOSE);

		assertThat(backend).contains("bucket = \"emerald-grove-pet-clinic-dev-terraform-state\"");
		assertThat(backend).contains("key = \"app/dev/terraform.tfstate\"");
		assertThat(backend).contains("endpoint");
		assertThat(backend).contains("http://127.0.0.1:4566");
		assertThat(readme).contains("docker compose -f infra/terraform/floci/docker-compose.yml up -d floci");
		assertThat(readme).contains("docker compose -f infra/terraform/floci/docker-compose.yml down");
		assertThat(readme).contains("local AWS-resources environment");
		assertThat(readme)
			.contains("terraform -chdir=infra/terraform/app/dev init -backend-config=backend.hcl.example -reconfigure");
		assertThat(compose).contains("services:");
		assertThat(compose).contains("floci:");
		assertThat(compose).contains("localstack/localstack:3");
		assertThat(compose).contains("\"4566:4566\"");
	}

}
