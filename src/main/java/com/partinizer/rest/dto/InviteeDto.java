package com.partinizer.rest.dto;

import com.partinizer.domain.Invitation;
import com.partinizer.domain.InvitationStatus;

public class InviteeDto implements Dto {

	private final String id;

	private final String username;

	private final InvitationStatus status;

	public InviteeDto(final String id, final String username, final InvitationStatus status) {
		this.id = id;
		this.username = username;
		this.status = status;
	}

	public static InviteeDto convertToInviteeDto(final Invitation invitation) {
		return new InviteeDto(invitation.getPartygoer().getId(), invitation.getPartygoer().getUsername(),
				invitation.getStatus());
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public InvitationStatus getStatus() {
		return status;
	}

}
