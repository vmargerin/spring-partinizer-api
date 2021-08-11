package com.partinizer.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.partinizer.domain.Friend;
import com.partinizer.domain.FriendRequest;
import com.partinizer.domain.FriendResult;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.PartygoerResult;
import com.partinizer.domain.RequestReply;
import com.partinizer.domain.Result;
import com.partinizer.domain.Role;
import com.partinizer.domain.RoleName;
import com.partinizer.repository.PartygoerRepository;
import com.partinizer.security.UserPrincipal;
import com.partinizer.service.PartygoerService;

@Service
public class PartygoerServiceImpl implements PartygoerService {

	@Autowired
	private PartygoerRepository partygoerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Result<Void> register(final Partygoer partygoer) {
		if (partygoerRepository.existsByUsername(partygoer.getUsername())) {
			return new Result<>(false, null, "Username is already taken!");
		}

		if (partygoerRepository.existsByEmail(partygoer.getEmail())) {
			return new Result<>(false, null, "Email Address already in use!");
		}

		partygoer.setPassword(passwordEncoder.encode(partygoer.getPassword()));
		partygoer.setRoles(Collections.singleton(new Role(RoleName.ROLE_USER)));

		this.partygoerRepository.save(partygoer);
		return new Result<>(true, null, "Partygoer created.");
	}

	@Override
	public Result<List<PartygoerResult>> search(final String filter, final String currentId) {
		// TODO How check filter input ?
		final Partygoer partygoer = this.partygoerRepository.getUserFriendsIds(currentId);

		final Optional<Collection<Partygoer>> result = this.partygoerRepository.findPartygoersByUsername(currentId,
				"^" + filter.trim() + ".*");
		final List<PartygoerResult> searchResult = new ArrayList<>();
		if (result.isPresent()) {
			result.get().stream().forEach((element) -> {
				if (this.isFriend(element.getId(), partygoer.getFriends())) {
					searchResult.add(new PartygoerResult(element.getId(), element.getUsername(), true, false));
				} else if (this.hasPendingFr(element.getId(), partygoer.getFriendReqSent())) {
					searchResult.add(new PartygoerResult(element.getId(), element.getUsername(), false, true));
				} else {
					searchResult.add(new PartygoerResult(element.getId(), element.getUsername(), false, false));
				}
			});
		}

		return new Result<>(true, searchResult, null);
	}

	@Override
	public Result<FriendResult> getFriends(final String currentId) {
		final Partygoer partygoer = partygoerRepository.getUserFriendsIds(currentId);
		final Collection<String> ids = partygoer.getFriends().stream().map(element -> element)
				.collect(Collectors.toList());
		final Collection<Partygoer> friends = partygoerRepository.getUserFriends(ids).get();
		final Collection<Friend> results = friends.stream()
				.map((friend) -> new Friend(friend.getId(), friend.getUsername())).collect(Collectors.toList());
		return new Result<>(true,
				new FriendResult(results, partygoer.getFriendReqReceived(), partygoer.getFriendReqSent()), null);
	}

	@Override
	public Result<Void> deleteFriend(String currentId, String partygoerId) {
		final Partygoer partygoer = partygoerRepository.getUserFriendsIds(currentId);
		// Check if the recipient is really my friend
		final Collection<String> friends = partygoer.getFriends();
		if (friends.stream().noneMatch((request) -> request.equals(partygoerId))) {
			return new Result<>(false, null, "The partygoer " + partygoerId + " is not in the friends list !");
		}
		this.partygoerRepository.deleteFriend(currentId, partygoerId);
		return new Result<>(true, null, "The friend " + partygoerId + " has been deleted");
	}

	@Override
	public Result<Void> createFr(final UserPrincipal currentUser, final String message, final String recipientId) {
		if (recipientId.equals(currentUser.getId())) {
			return new Result<>(false, null, "A friend request cannot be sent to self !");
		}
		// Check if the recipient user exists
		final Optional<Partygoer> opt = partygoerRepository.findUsernameById(recipientId);
		if (!opt.isPresent()) {
			return new Result<>(false, null, "The user related to the id " + recipientId + "doesn't exist !");
		}
		final Partygoer recipient = opt.get();
		final Partygoer partygoer = partygoerRepository.getUserFriendsIds(currentUser.getId());

		// Check if the friend request doesn't already exist
		final Collection<FriendRequest> friendReqSent = partygoer.getFriendReqSent();
		if (friendReqSent.stream().anyMatch((request) -> request.getPartygoerId().equals(recipientId))) {
			return new Result<>(false, null, "The friend request already exists !");
		}

		// Check if the recipient is not already my friend
		final Collection<String> friends = partygoer.getFriends();
		if (friends.stream().anyMatch((request) -> request.equals(recipientId))) {
			return new Result<>(false, null, "The partygoer is already in the friends list !");
		}

		this.partygoerRepository.addFriendRequest(currentUser.getId(), currentUser.getUsername(),
				new FriendRequest(recipientId, recipient.getUsername(), message));
		return new Result<>(true, null, "The friend request has been created");
	}

	@Override
	public Result<Void> replyFr(final String currentId, final String askerId, RequestReply response) {
		// Check if asker exists
		if (!this.partygoerRepository.existsById(askerId)) {
			return new Result<>(false, null, "The asker " + askerId + " doesn't exist !");
		}
		// Check if friendReq exists
		final Partygoer partygoer = partygoerRepository.getUserFriendsIds(currentId);
		final Collection<FriendRequest> friendReqReceived = partygoer.getFriendReqReceived();
		if (!friendReqReceived.stream().anyMatch((request) -> request.getPartygoerId().equals(askerId))) {
			return new Result<>(false, null, "The friend request doesn't exist");
		}

		if (response == RequestReply.ACCEPT) {
			this.partygoerRepository.acceptFriendRequest(currentId, askerId);
		} else {
			this.partygoerRepository.denyFriendRequest(currentId, askerId);
		}
		return new Result<>(true, null, "The friend request reply has been sent");
	}

	@Override
	public Result<Void> cancelFrSent(String currentId, String recipientId) {
		// Check if friendReq exists
		final Partygoer partygoer = partygoerRepository.getUserFriendsIds(currentId);
		final Collection<FriendRequest> friendReqSent = partygoer.getFriendReqSent();
		if (!friendReqSent.stream().anyMatch((request) -> request.getPartygoerId().equals(recipientId))) {
			return new Result<>(false, null, "The friend request doesn't exist");
		}

		this.partygoerRepository.deleteFriendRequest(currentId, recipientId);
		return new Result<>(true, null, "The friend request has been canceled");
	}

	private boolean isFriend(final String id, final Collection<String> friends) {
		return friends.stream().anyMatch((f) -> f.equals(id));
	}

	private boolean hasPendingFr(final String id, final Collection<FriendRequest> frSent) {
		return frSent.stream().anyMatch((f) -> f.getPartygoerId().equals(id));
	}

}
