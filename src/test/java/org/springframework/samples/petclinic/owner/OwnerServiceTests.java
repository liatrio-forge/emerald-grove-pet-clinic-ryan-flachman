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
package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTests {

	@Mock
	private OwnerRepository owners;

	@InjectMocks
	private OwnerService ownerService;

	@Test
	void testIsDuplicate_returnsTrueWhenMatchExists() {
		given(owners.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone("George", "Franklin", "6085551023"))
			.willReturn(true);

		boolean result = ownerService.isDuplicate("George", "Franklin", "6085551023");

		assertThat(result).isTrue();
	}

	@Test
	void testIsDuplicate_returnsFalseWhenNoMatch() {
		given(owners.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone("George", "Franklin", "6085551023"))
			.willReturn(false);

		boolean result = ownerService.isDuplicate("George", "Franklin", "6085551023");

		assertThat(result).isFalse();
	}

}
