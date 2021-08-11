package com.partinizer.repository;

import com.partinizer.domain.FriendRequest;

public interface CustomPartygoerRepository {

	void addFriendRequest(final String currentId, final String currentUsername, final FriendRequest invitation);

	void acceptFriendRequest(final String currentId, final String askerId);

	void denyFriendRequest(final String currentId, final String askerId);

	void deleteFriendRequest(final String currentId, final String recipientId);

	void deleteFriend(final String currentId, final String partygoerId);

}
