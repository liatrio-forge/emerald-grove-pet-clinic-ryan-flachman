package org.springframework.samples.petclinic.owner;

/**
 * Raised when Claude's AI note JSON cannot be parsed into a {@link VisitSummary}.
 */
public class VisitSummaryParseException extends RuntimeException {

	public VisitSummaryParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
