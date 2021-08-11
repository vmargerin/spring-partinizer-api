package com.partinizer.domain;

import java.util.Collection;

public class FriendResult {

	private final Collection<Friend> friends;

	private final Collection<FriendRequest> received;

	private final Collection<FriendRequest> sent;

	public FriendResult(Collection<Friend> friends, Collection<FriendRequest> received, Collection<FriendRequest> sent) {
		this.friends = friends;
		this.received = received;
		this.sent = sent;

	}

	public Collection<Friend> getFriends() {
		return friends;
	}

	public Collection<FriendRequest> getReceived() {
		return received;
	}

	public Collection<FriendRequest> getSent() {
		return sent;
	}

}
