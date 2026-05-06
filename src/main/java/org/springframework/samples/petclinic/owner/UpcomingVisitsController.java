package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/visits")
class UpcomingVisitsController {

	private final VisitRepository visitRepository;

	UpcomingVisitsController(VisitRepository visitRepository) {
		this.visitRepository = visitRepository;
	}

	@GetMapping("/upcoming")
	public String showUpcomingVisits(@RequestParam(name = "days", defaultValue = "7") int days, Model model) {
		if (days < 1 || days > 30) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "days must be between 1 and 30");
		}
		LocalDate start = LocalDate.now();
		LocalDate endExclusive = start.plusDays(days);
		List<UpcomingVisitRow> visits = visitRepository.findUpcomingVisits(start, endExclusive);
		model.addAttribute("visits", visits);
		model.addAttribute("days", days);
		return "visits/upcomingVisits";
	}

}
