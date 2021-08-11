package com.partinizer.domain;

public class Friend {

	private final String partygoerId;

	private final String username;

	public Friend(final String partygoerId, final String username) {
		this.partygoerId = partygoerId;
		this.username = username;
	}

	public String getPartygoerId() {
		return partygoerId;
	}

	public String getUsername() {
		return username;
	}

}
