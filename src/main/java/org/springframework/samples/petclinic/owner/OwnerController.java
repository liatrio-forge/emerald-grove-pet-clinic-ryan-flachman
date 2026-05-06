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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriUtils;

import org.springframework.samples.petclinic.system.ResourceNotFoundException;

import jakarta.validation.Valid;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Wick Dynex
 */
@Controller
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final OwnerRepository owners;

	private final OwnerService ownerService;

	public OwnerController(OwnerRepository owners, OwnerService ownerService) {
		this.owners = owners;
		this.ownerService = ownerService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		return ownerId == null ? new Owner() : this.owners.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
	}

	@GetMapping("/owners/new")
	public String initCreationForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		if (ownerService.isDuplicate(owner.getFirstName(), owner.getLastName(), owner.getTelephone())) {
			result.reject("duplicate", "An owner with this name and telephone already exists.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		return "redirect:/owners/" + owner.getId();
	}

	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
			Model model) {
		int safePage = Math.max(page, 1);
		String lastName = nullIfBlank(owner.getLastName());
		String telephone = nullIfBlank(owner.getTelephone());
		String city = nullIfBlank(owner.getCity());

		if (telephone != null && !telephone.matches("\\d+")) {
			result.rejectValue("telephone", "invalid", "Telephone must contain digits only");
			return "owners/findOwners";
		}

		Page<Owner> ownersResults = findPaginatedForOwners(safePage, lastName, telephone, city);
		if (ownersResults.isEmpty()) {
			result.reject("notFound", "not found");
			return "owners/findOwners";
		}

		if (ownersResults.getTotalElements() == 1) {
			owner = ownersResults.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}

		return addPaginationModel(safePage, model, ownersResults, lastName, telephone, city);
	}

	private static String nullIfBlank(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated, String lastName, String telephone,
			String city) {
		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		if (lastName != null)
			model.addAttribute("filterLastName", lastName);
		if (telephone != null)
			model.addAttribute("filterTelephone", telephone);
		if (city != null)
			model.addAttribute("filterCity", city);
		// Thymeleaf 3.1 includes null URL params as empty strings rather than omitting
		// them, so we pre-build the encoded filter query string for pagination links.
		StringBuilder filterQuery = new StringBuilder();
		if (lastName != null)
			filterQuery.append("&lastName=").append(UriUtils.encodeQueryParam(lastName, StandardCharsets.UTF_8));
		if (telephone != null)
			filterQuery.append("&telephone=").append(UriUtils.encodeQueryParam(telephone, StandardCharsets.UTF_8));
		if (city != null)
			filterQuery.append("&city=").append(UriUtils.encodeQueryParam(city, StandardCharsets.UTF_8));
		model.addAttribute("filterQuery", filterQuery.toString());
		return "owners/ownersList";
	}

	private Page<Owner> findPaginatedForOwners(int page, String lastName, String telephone, String city) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return owners.findBySearchCriteria(lastName, telephone, city, pageable);
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		if (!Objects.equals(owner.getId(), ownerId)) {
			result.rejectValue("id", "mismatch", "The owner ID in the form does not match the URL.");
			redirectAttributes.addFlashAttribute("error", "Owner ID mismatch. Please try again.");
			return "redirect:/owners/{ownerId}/edit";
		}

		owner.setId(ownerId);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		Owner owner = optionalOwner
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		mav.addObject(owner);
		return mav;
	}

}
