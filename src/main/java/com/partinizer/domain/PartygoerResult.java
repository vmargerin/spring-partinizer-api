package com.partinizer.domain;

public class PartygoerResult {

	private final String id;
	private final String username;

	private final boolean friend;
	private final boolean pendingFr;

	public PartygoerResult(final String id, final String username, final boolean friend, final boolean pendingFr) {
		this.id = id;
		this.username = username;
		this.friend = friend;
		this.pendingFr = pendingFr;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public boolean isFriend() {
		return friend;
	}

	public boolean isPendingFr() {
		return pendingFr;
	}

}
