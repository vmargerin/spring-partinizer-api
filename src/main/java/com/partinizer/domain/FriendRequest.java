package com.partinizer.domain;

public class FriendRequest {

	private String partygoerId;

	private String username;

	private String message;

	public FriendRequest(final String partygoerId, final String username, final String message) {
		this.partygoerId = partygoerId;
		this.username = username;
		this.message = message;
	}

	public String getPartygoerId() {
		return partygoerId;
	}

	public String getUsername() {
		return username;
	}

	public String getMessage() {
		return message;
	}

}
