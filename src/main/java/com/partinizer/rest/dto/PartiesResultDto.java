package com.partinizer.rest.dto;

import java.util.Collection;
import java.util.stream.Collectors;

import com.partinizer.domain.Party;

public class PartiesResultDto implements Dto {

	private Collection<PartyDto> result;

	public void setResult(Collection<PartyDto> result) {
		this.result = result;
	}

	public PartiesResultDto() {

	}

	public PartiesResultDto(final Collection<Party> parties) {
		result = parties.stream().map(PartyDto::convertToPartyDto).collect(Collectors.toList());
	}

	public Collection<PartyDto> getResult() {
		return result;
	}

}
