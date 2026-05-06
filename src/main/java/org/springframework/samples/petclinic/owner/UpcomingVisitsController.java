package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/visits")
class UpcomingVisitsController {

	private final VisitRepository visitRepository;

	UpcomingVisitsController(VisitRepository visitRepository) {
		this.visitRepository = visitRepository;
	}

	@GetMapping("/upcoming")
	public String showUpcomingVisits(@RequestParam(name = "days", defaultValue = "7") int days, Model model) {
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(days);
		List<UpcomingVisitRow> visits = visitRepository.findUpcomingVisits(start, end);
		model.addAttribute("visits", visits);
		model.addAttribute("days", days);
		return "visits/upcomingVisits";
	}

}
