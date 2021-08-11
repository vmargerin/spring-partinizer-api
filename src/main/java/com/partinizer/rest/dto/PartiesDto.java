package com.partinizer.rest.dto;

import java.util.Collection;
import java.util.List;

import com.partinizer.domain.Party;

public class PartiesDto implements Dto {

	private final List<PartyDto> myParties;

	private final List<PartyDto> myParticipations;

	private final List<InvitationDto> invitations;

	public PartiesDto(final List<PartyDto> myParties, final List<PartyDto> myParticipations,
			final List<InvitationDto> invitations) {
		this.myParticipations = myParticipations;
		this.myParties = myParties;
		this.invitations = invitations;
	}

	public Collection<PartyDto> getMyParties() {
		return myParties;
	}

	public Collection<PartyDto> getMyParticipations() {
		return myParticipations;
	}

	public Collection<InvitationDto> getInvitations() {
		return invitations;
	}

	public static PartiesDto convertToDto(final List<Party> myParties, final List<Party> myParticipations,
			final List<Party> invitations) {
		return new PartiesDto(PartyDto.convertToPartyDtos(myParties), PartyDto.convertToPartyDtos(myParticipations),
				InvitationDto.convertToInvitationsDtos(invitations));

	}

}
