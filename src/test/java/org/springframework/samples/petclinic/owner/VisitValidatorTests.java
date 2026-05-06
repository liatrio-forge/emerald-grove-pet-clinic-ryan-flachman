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

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link VisitValidator}
 */
@ExtendWith(MockitoExtension.class)
class VisitValidatorTests {

	private VisitValidator validator;

	@BeforeEach
	void setUp() {
		validator = new VisitValidator();
	}

	@Test
	void shouldRejectPastDate() {
		Visit visit = new Visit();
		visit.setDate(LocalDate.now().minusDays(1));
		visit.setDescription("checkup");

		Errors errors = new BeanPropertyBindingResult(visit, "visit");
		validator.validate(visit, errors);

		assertThat(errors.hasFieldErrors("date")).isTrue();
		assertThat(errors.getFieldError("date").getCode()).isEqualTo("visit.date.pastNotAllowed");
	}

	@Test
	void shouldRejectNullDate() {
		Visit visit = new Visit();
		visit.setDate(null);
		visit.setDescription("checkup");

		Errors errors = new BeanPropertyBindingResult(visit, "visit");
		validator.validate(visit, errors);

		assertThat(errors.hasFieldErrors("date")).isTrue();
		assertThat(errors.getFieldError("date").getCode()).isEqualTo("required");
	}

	@Test
	void shouldAcceptToday() {
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		visit.setDescription("checkup");

		Errors errors = new BeanPropertyBindingResult(visit, "visit");
		validator.validate(visit, errors);

		assertThat(errors.hasFieldErrors("date")).isFalse();
	}

	@Test
	void shouldAcceptFutureDate() {
		Visit visit = new Visit();
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setDescription("checkup");

		Errors errors = new BeanPropertyBindingResult(visit, "visit");
		validator.validate(visit, errors);

		assertThat(errors.hasFieldErrors("date")).isFalse();
	}

}
