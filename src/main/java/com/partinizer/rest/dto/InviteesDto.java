package com.partinizer.rest.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.partinizer.domain.Invitation;

public class InviteesDto implements Dto {

	private final List<InviteeDto> invitees;

	public InviteesDto(final List<InviteeDto> invitees) {
		this.invitees = invitees;
	}

	public static InviteesDto convertToInviteesDto(final List<Invitation> invitations) {
		return new InviteesDto(invitations.stream().map(invitation -> InviteeDto.convertToInviteeDto(invitation))
				.collect(Collectors.toList()));
	}

	public List<InviteeDto> getInvitees() {
		return invitees;
	}

}
