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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 * @author Dave Syer
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	@Column(name = "visit_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@Column(length = 2000)
	@NotBlank
	private String description;

	@Column(name = "ai_status", length = 20)
	@Enumerated(EnumType.STRING)
	private AiStatus aiStatus;

	@Column(name = "ai_summary", length = 4000)
	private String aiSummary;

	@Column(name = "ai_tags", length = 500)
	private String aiTags;

	@Column(name = "ai_urgency", length = 20)
	private String aiUrgency;

	@Column(name = "ai_follow_up", length = 2000)
	private String aiFollowUp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id", insertable = false, updatable = false)
	private Pet pet;

	/**
	 * Creates a new instance of Visit for the current date
	 */
	public Visit() {
		this.date = LocalDate.now();
		this.aiStatus = AiStatus.PENDING;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AiStatus getAiStatus() {
		return this.aiStatus;
	}

	public void setAiStatus(AiStatus aiStatus) {
		this.aiStatus = aiStatus;
	}

	public String getAiSummary() {
		return this.aiSummary;
	}

	public void setAiSummary(String aiSummary) {
		this.aiSummary = aiSummary;
	}

	public String getAiTags() {
		return this.aiTags;
	}

	public void setAiTags(String aiTags) {
		this.aiTags = aiTags;
	}

	public String getAiUrgency() {
		return this.aiUrgency;
	}

	public void setAiUrgency(String aiUrgency) {
		this.aiUrgency = aiUrgency;
	}

	public String getAiFollowUp() {
		return this.aiFollowUp;
	}

	public void setAiFollowUp(String aiFollowUp) {
		this.aiFollowUp = aiFollowUp;
	}

	public Pet getPet() {
		return this.pet;
	}

}
