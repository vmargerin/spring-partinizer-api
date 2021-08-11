package com.partinizer.service;

import java.util.List;

import com.partinizer.domain.FriendResult;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.PartygoerResult;
import com.partinizer.domain.RequestReply;
import com.partinizer.domain.Result;
import com.partinizer.security.UserPrincipal;

public interface PartygoerService {

	Result<Void> register(final Partygoer partygoer);

	Result<List<PartygoerResult>> search(final String filter, final String currentId);

	Result<Void> deleteFriend(final String currentId, final String partygoerId);

	Result<FriendResult> getFriends(final String currentId);

	Result<Void> createFr(final UserPrincipal currentUser, final String message, final String recipientId);

	Result<Void> cancelFrSent(final String currentId, final String recipientId);

	Result<Void> replyFr(final String currentId, final String askerId, RequestReply response);

}
