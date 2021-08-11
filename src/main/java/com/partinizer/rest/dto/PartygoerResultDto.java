package com.partinizer.rest.dto;

import java.util.Collection;

import com.partinizer.domain.PartygoerResult;

public class PartygoerResultDto implements Dto {

	private Collection<PartygoerResult> results;

	public PartygoerResultDto(final Collection<PartygoerResult> results) {
		this.results = results;
	}

	public Collection<PartygoerResult> getResults() {
		return results;
	}

}
