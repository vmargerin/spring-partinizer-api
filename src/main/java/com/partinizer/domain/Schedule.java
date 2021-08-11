package com.partinizer.domain;

import java.time.OffsetDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

public class Schedule {

	@NotNull
	@FutureOrPresent
	protected final OffsetDateTime startDate;

	@NotNull
	@Future
	protected final OffsetDateTime endDate;

	public Schedule(final OffsetDateTime startDate, final OffsetDateTime endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public OffsetDateTime getStartDate() {
		return startDate;
	}

	public OffsetDateTime getEndDate() {
		return endDate;
	}

}