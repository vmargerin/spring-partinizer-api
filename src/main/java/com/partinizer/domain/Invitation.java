package com.partinizer.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class Invitation {

	@DBRef(lazy = true)
	private final Partygoer partygoer;

	private final InvitationStatus status;

	public Invitation(final Partygoer partygoer, final InvitationStatus status) {
		this.partygoer = partygoer;
		this.status = status;
	}

	public Partygoer getPartygoer() {
		return partygoer;
	}

	public InvitationStatus getStatus() {
		return status;
	}

}
