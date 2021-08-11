package com.partinizer.rest.dto;

import java.util.Collection;

import com.partinizer.domain.Friend;
import com.partinizer.domain.FriendRequest;

public class FriendResultDto implements Dto {

	private final Collection<Friend> friends;

	private final Collection<FriendRequest> received;

	private final Collection<FriendRequest> sent;

	public FriendResultDto(Collection<Friend> friends, Collection<FriendRequest> received,
			Collection<FriendRequest> sent) {
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
