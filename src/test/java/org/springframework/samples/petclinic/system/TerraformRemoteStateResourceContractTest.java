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

class TerraformRemoteStateResourceContractTest {

	private static final Path STATE_STACK_DIRECTORY = Path.of("infra/terraform/state/dev");

	private static final Path MAIN = STATE_STACK_DIRECTORY.resolve("main.tf");

	private static final Path VARIABLES = STATE_STACK_DIRECTORY.resolve("variables.tf");

	private static final Path LOCALS = STATE_STACK_DIRECTORY.resolve("locals.tf");

	private static final Path VERSIONS = STATE_STACK_DIRECTORY.resolve("versions.tf");

	private static final Path OUTPUTS = STATE_STACK_DIRECTORY.resolve("outputs.tf");

	@Test
	void stateStackDefinesExactlyOneDevBucketAndOneLockTableWithRecoveryControls() throws IOException {
		assertThat(MAIN).exists();

		String main = Files.readString(MAIN);

		assertThat(main).containsOnlyOnce("resource \"aws_s3_bucket\"");
		assertThat(main).containsOnlyOnce("resource \"aws_dynamodb_table\"");
		assertThat(main).contains("resource \"aws_s3_bucket\" \"terraform_state\"");
		assertThat(main).contains("resource \"aws_dynamodb_table\" \"terraform_lock\"");
		assertThat(main).contains("bucket = local.state_bucket_name");
		assertThat(main).contains("name         = local.lock_table_name");
		assertThat(main).contains("hash_key     = \"LockID\"");
		assertThat(main).contains("name = \"LockID\"");
		assertThat(main).contains("type = \"S\"");
		assertThat(main).contains("resource \"aws_s3_bucket_versioning\" \"terraform_state\"");
		assertThat(main).contains("status = \"Enabled\"");
		assertThat(main)
			.contains("resource \"aws_s3_bucket_server_side_encryption_configuration\" \"terraform_state\"");
		assertThat(main).contains("sse_algorithm = \"AES256\"");
		assertThat(main).contains("billing_mode = \"PAY_PER_REQUEST\"");
		assertThat(main).contains("tags = local.common_tags");
	}

	@Test
	void stateStackDefinesEnvironmentScopedNamingAndSharedTags() throws IOException {
		assertThat(LOCALS).exists();
		assertThat(VARIABLES).exists();

		String locals = Files.readString(LOCALS);
		String variables = Files.readString(VARIABLES);

		assertThat(variables).contains("variable \"environment\"");
		assertThat(variables).contains("default     = \"dev\"");
		assertThat(variables).contains("variable \"aws_region\"");
		assertThat(locals).contains("state_bucket_name");
		assertThat(locals).contains("lock_table_name");
		assertThat(locals).contains("terraform-state");
		assertThat(locals).contains("terraform-locks");
		assertThat(locals).contains("var.environment");
		assertThat(locals).contains("Environment = var.environment");
		assertThat(locals).contains("ManagedBy   = \"terraform\"");
		assertThat(locals).contains("Stack       = \"terraform-state\"");
	}

	@Test
	void stateStackPinsTerraformAndExportsBackendValues() throws IOException {
		assertThat(VERSIONS).exists();
		assertThat(OUTPUTS).exists();

		String versions = Files.readString(VERSIONS);
		String outputs = Files.readString(OUTPUTS);

		assertThat(versions).contains("required_version");
		assertThat(versions).contains("required_providers");
		assertThat(versions).contains("source  = \"hashicorp/aws\"");
		assertThat(outputs).contains("output \"state_bucket_name\"");
		assertThat(outputs).contains("output \"lock_table_name\"");
		assertThat(outputs).contains("output \"aws_region\"");
	}

}
