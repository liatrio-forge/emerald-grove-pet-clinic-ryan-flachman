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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(OwnerController.class)
@DisabledInNativeImage
@DisabledInAotMode
class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@MockitoBean
	private OwnerService ownerService;

	private Owner george() {
		Owner george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");
		Pet max = new Pet();
		PetType dog = new PetType();
		dog.setName("dog");
		max.setType(dog);
		max.setName("Max");
		max.setBirthDate(LocalDate.now());
		george.addPet(max);
		max.setId(1);
		return george;
	}

	@BeforeEach
	void setup() {
		Owner george = george();

		given(ownerService.isDuplicate(any(), any(), any())).willReturn(false);

		// Safety net: prevents NPE if controller still routes through
		// findByLastNameStartingWith
		given(this.owners.findByLastNameStartingWith(any(), any(Pageable.class))).willReturn(new PageImpl<>(List.of()));

		// Broad stub for all findBySearchCriteria calls (registered first so specific
		// stubs override it)
		given(this.owners.findBySearchCriteria(any(), any(), any(), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(george, new Owner())));

		// Franklin-specific stub (registered last — takes precedence over broad stub for
		// Franklin)
		given(this.owners.findBySearchCriteria(eq("Franklin"), isNull(), isNull(), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(george)));

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		george.getPet("Max").getVisits().add(visit);
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "1316761638"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs").param("city", "London"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/owners/find"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormSuccess() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george(), new Owner()));
		when(this.owners.findBySearchCriteria(isNull(), isNull(), isNull(), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormByLastName() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george()));
		when(this.owners.findBySearchCriteria(eq("Franklin"), isNull(), isNull(), any(Pageable.class)))
			.thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("lastName", "Franklin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormNoOwnersFound() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of());
		when(this.owners.findBySearchCriteria(eq("Unknown Surname"), isNull(), isNull(), any(Pageable.class)))
			.thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("lastName", "Unknown Surname"))
			.andExpect(status().isOk())
			.andExpect(result -> {
				BindingResult bindingResult = (BindingResult) result.getModelAndView()
					.getModel()
					.get(BindingResult.MODEL_KEY_PREFIX + "owner");
				assertThat(bindingResult.getGlobalErrorCount()).isEqualTo(1);
				assertThat(bindingResult.getFieldErrorCount()).isEqualTo(0);
			})
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormByCity() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george(), new Owner()));
		when(this.owners.findBySearchCriteria(isNull(), isNull(), eq("Madison"), any(Pageable.class)))
			.thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("city", "Madison"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormByTelephone() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george(), new Owner()));
		when(this.owners.findBySearchCriteria(isNull(), eq("6085551"), isNull(), any(Pageable.class)))
			.thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("telephone", "6085551"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormByCombinedCriteria() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george(), new Owner()));
		when(this.owners.findBySearchCriteria(eq("Davis"), isNull(), eq("Sun"), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("lastName", "Davis").param("city", "Sun"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormInvalidTelephone() throws Exception {
		mockMvc.perform(get("/owners?page=1").param("telephone", "608-555"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(model().attributeHasFieldErrorCode("owner", "telephone", "invalid"))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormValidPartialTelephone() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george(), new Owner()));
		when(this.owners.findBySearchCriteria(isNull(), eq("608"), isNull(), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("telephone", "608"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attributeHasNoErrors("owner"));
	}

	@Test
	void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
			.andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
			.andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
			.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "1616291589"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormUnchangedSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "")
				.param("telephone", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
			.andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
			.andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
			.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
			.andExpect(model().attribute("owner", hasProperty("pets", not(empty()))))
			.andExpect(model().attribute("owner",
					hasProperty("pets", hasItem(hasProperty("visits", hasSize(greaterThan(0)))))))
			.andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	public void testProcessUpdateOwnerFormWithIdMismatch() throws Exception {
		int pathOwnerId = 1;

		Owner owner = new Owner();
		owner.setId(2);
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("Center Street");
		owner.setCity("New York");
		owner.setTelephone("0123456789");

		when(owners.findById(pathOwnerId)).thenReturn(Optional.of(owner));

		mockMvc.perform(MockMvcRequestBuilders.post("/owners/{ownerId}/edit", pathOwnerId).flashAttr("owner", owner))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/" + pathOwnerId + "/edit"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	void testProcessCreationFormDuplicateRejected() throws Exception {
		given(ownerService.isDuplicate(eq("Joe"), eq("Bloggs"), eq("1316761638"))).willReturn(true);

		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "1316761638"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"))
			.andExpect(result -> {
				BindingResult bindingResult = (BindingResult) result.getModelAndView()
					.getModel()
					.get(BindingResult.MODEL_KEY_PREFIX + "owner");
				assertThat(bindingResult.getGlobalErrors()).hasSize(1);
				assertThat(bindingResult.getGlobalErrors().get(0).getCode()).isEqualTo("duplicate");
			});

		verify(owners, never()).save(any());
	}

}
