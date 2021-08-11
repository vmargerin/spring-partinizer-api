package com.partinizer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.partinizer.domain.FriendRequest;
import com.partinizer.domain.FriendResult;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.PartygoerResult;
import com.partinizer.domain.RequestReply;
import com.partinizer.domain.Result;
import com.partinizer.repository.PartygoerRepository;
import com.partinizer.security.UserPrincipal;
import com.partinizer.service.impl.PartygoerServiceImpl;

@SpringBootTest
public class PartygoerServiceTest {

	@MockBean
	private PartygoerRepository partygoerRepository;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PartygoerServiceImpl partygoerService;

	private final String CURRENT_USER_ID = "5f87237359e19b2e0c4df359";
	private final String TARGET_USER_ID = "5f87255c59e19b2e0c4df357";

	@Test
	public void registerValid() {
		final String username = "goodUsername";
		final String email = "good@email.com";
		final String password = "P@ssword74";
		final Partygoer partygoer = new Partygoer();
		partygoer.setUsername(username);
		partygoer.setEmail(username);
		partygoer.setPassword(password);
		when(partygoerRepository.existsByUsername(eq(username))).thenReturn(false);
		when(partygoerRepository.existsByUsername(eq(email))).thenReturn(false);
		when(partygoerRepository.save(isA(Partygoer.class))).thenReturn(partygoer);

		final Result<Void> result = partygoerService.register(partygoer);

		assertThat(result.isSuccess()).isTrue();
		verify(passwordEncoder, times(1)).encode(eq(password));
	}

	@Test
	public void registerWithExistingUsername() {
		final String email = "existing@email.com";
		final Partygoer partygoer = new Partygoer();
		partygoer.setUsername(email);
		when(partygoerRepository.existsByUsername(eq(email))).thenReturn(true);

		final Result<Void> result = partygoerService.register(partygoer);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("Username is already taken!");
	}

	@Test
	public void registerWithExistingEmail() {
		final String email = "existing@email.com";
		final Partygoer partygoer = new Partygoer();
		partygoer.setEmail(email);
		when(partygoerRepository.existsByEmail(eq(email))).thenReturn(true);

		final Result<Void> result = partygoerService.register(partygoer);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("Email Address already in use!");
	}

	/*
	 * Check that PartygoerResult has friend boolean to true when the partygoer is
	 * known
	 */
	@Test
	public void searchWithFriendsInResult() {
		final Collection<Partygoer> searchResult = new ArrayList<>();
		final Partygoer currentUser = new Partygoer(CURRENT_USER_ID);
		final Partygoer friend = new Partygoer(TARGET_USER_ID);
		final Partygoer result1 = new Partygoer("5f87237359e19b2e0c4df356");
		searchResult.add(friend);
		searchResult.add(result1);

		currentUser.setFriends(Collections.singletonList(TARGET_USER_ID));
		when(partygoerRepository.getUserFriendsIds(anyString())).thenReturn(currentUser);
		when(partygoerRepository.findPartygoersByUsername(anyString(), anyString()))
				.thenReturn(Optional.of(searchResult));

		final Result<List<PartygoerResult>> result = partygoerService.search("", CURRENT_USER_ID);

		assertThat(result.isSuccess()).isTrue();
		// Check that the friend in result has friend boolean to true
		PartygoerResult partygoerResult = result.getTarget().stream()
				.filter((partygoer) -> partygoer.getId().equals(TARGET_USER_ID)).findFirst().get();
		assertThat(partygoerResult.isFriend()).isTrue();

		// Check that others result has friend boolean to false
		partygoerResult = result.getTarget().stream().filter((partygoer) -> !partygoer.getId().equals(TARGET_USER_ID))
				.findFirst().get();
		assertThat(partygoerResult.isFriend()).isFalse();
	}

	/*
	 * Check that PartygoerResult has pendingFr boolean to true when a friend
	 * request has been sent
	 */
	@Test
	public void searchWithPendingFrInResult() {
		final Collection<Partygoer> searchResult = new ArrayList<>();
		final Partygoer currentUser = new Partygoer(CURRENT_USER_ID);
		final FriendRequest friendRequest = new FriendRequest(TARGET_USER_ID, "jdoe", "");
		final Partygoer friend = new Partygoer(TARGET_USER_ID);
		final Partygoer result1 = new Partygoer("5f87237359e19b2e0c4df356");
		searchResult.add(friend);
		searchResult.add(result1);

		currentUser.setFriendReqSent(Collections.singletonList(friendRequest));
		when(partygoerRepository.getUserFriendsIds(anyString())).thenReturn(currentUser);
		when(partygoerRepository.findPartygoersByUsername(anyString(), anyString()))
				.thenReturn(Optional.of(searchResult));

		final Result<List<PartygoerResult>> result = partygoerService.search("", CURRENT_USER_ID);

		assertThat(result.isSuccess()).isTrue();
		// Check that the friend in result has pending fr boolean to true
		PartygoerResult partygoerResult = result.getTarget().stream()
				.filter((partygoer) -> partygoer.getId().equals(TARGET_USER_ID)).findFirst().get();
		assertThat(partygoerResult.isPendingFr()).isTrue();

		// Check that others result has pending fr boolean to false
		partygoerResult = result.getTarget().stream().filter((partygoer) -> !partygoer.getId().equals(TARGET_USER_ID))
				.findFirst().get();
		assertThat(partygoerResult.isPendingFr()).isFalse();
	}

	@Test
	public void searchWithNoResult() {
		final Partygoer partygoer = new Partygoer();
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(partygoer);
		when(partygoerRepository.findPartygoersByUsername(CURRENT_USER_ID, ""))
				.thenReturn(Optional.of(Collections.emptyList()));

		final Result<List<PartygoerResult>> result = partygoerService.search("", CURRENT_USER_ID);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getTarget().isEmpty()).isTrue();
	}

	@Test
	public void createFrForCurrentUser() {
		final UserPrincipal principal = UserPrincipal.create(new Partygoer(CURRENT_USER_ID));
		final Result<Void> result = partygoerService.createFr(principal, "", CURRENT_USER_ID);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("A friend request cannot be sent to self !");
	}

	@Test
	public void createFrUserNoExist() {
		final UserPrincipal principal = UserPrincipal.create(new Partygoer(CURRENT_USER_ID));
		when(partygoerRepository.findUsernameById(anyString())).thenReturn(Optional.empty());

		final Result<Void> result = partygoerService.createFr(principal, "", TARGET_USER_ID);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("The user related to the id " + TARGET_USER_ID + "doesn't exist !");
	}

	@Test
	public void createFrAlreadyExist() {
		final UserPrincipal principal = UserPrincipal.create(new Partygoer(CURRENT_USER_ID));
		final List<FriendRequest> frSent = Arrays.asList(new FriendRequest(TARGET_USER_ID, "", ""));
		final Partygoer currentUser = new Partygoer();
		currentUser.setFriendReqSent(frSent);
		when(partygoerRepository.findUsernameById(anyString())).thenReturn(Optional.of(new Partygoer()));
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);

		final Result<Void> result = partygoerService.createFr(principal, "", TARGET_USER_ID);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("The friend request already exists !");
	}

	@Test
	public void createFrFriendAlreadyExist() {
		final UserPrincipal principal = UserPrincipal.create(new Partygoer(CURRENT_USER_ID));
		final Partygoer currentUser = new Partygoer();
		currentUser.setFriends(Arrays.asList(TARGET_USER_ID));
		when(partygoerRepository.findUsernameById(anyString())).thenReturn(Optional.of(new Partygoer()));
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);

		final Result<Void> result = partygoerService.createFr(principal, "", TARGET_USER_ID);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("The partygoer is already in the friends list !");
	}

	@Test
	public void createFrValid() {
		final UserPrincipal principal = UserPrincipal.create(new Partygoer(CURRENT_USER_ID));
		final Partygoer currentUser = new Partygoer();
		when(partygoerRepository.findUsernameById(anyString())).thenReturn(Optional.of(new Partygoer()));
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);

		final Result<Void> result = partygoerService.createFr(principal, "", TARGET_USER_ID);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getMessage()).isEqualTo("The friend request has been created");
	}

	@Test
	public void getFriends() {
		final String friendUsername = "jdoe";
		final Partygoer currentUser = new Partygoer();
		final Partygoer friend1 = new Partygoer(TARGET_USER_ID);
		friend1.setUsername(friendUsername);
		final Collection<String> friendIds = Arrays.asList(TARGET_USER_ID);
		final Collection<Partygoer> friends = Arrays.asList(friend1);
		currentUser.setFriends(Arrays.asList(TARGET_USER_ID));
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);
		when(partygoerRepository.getUserFriends(friendIds)).thenReturn(Optional.of(friends));

		final Result<FriendResult> result = partygoerService.getFriends(CURRENT_USER_ID);
		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getTarget().getFriends().iterator().next().getUsername()).isEqualTo(friendUsername);
	}

	@Test
	public void deleteFriendNotExists() {
		final Partygoer currentUser = new Partygoer();
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);

		final Result<Void> result = partygoerService.deleteFriend(CURRENT_USER_ID, TARGET_USER_ID);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("The partygoer " + TARGET_USER_ID + " is not in the friends list !");
	}

	@Test
	public void deleteFriendValid() {
		final Partygoer currentUser = new Partygoer();
		currentUser.setFriends(Arrays.asList(TARGET_USER_ID));
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);

		final Result<Void> result = partygoerService.deleteFriend(CURRENT_USER_ID, TARGET_USER_ID);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getMessage()).isEqualTo("The friend " + TARGET_USER_ID + " has been deleted");
	}

	@Test
	public void replyFrFromUnexistingUser() {
		final Partygoer currentUser = new Partygoer();
		currentUser.setFriends(Arrays.asList(TARGET_USER_ID));
		when(partygoerRepository.existsById(TARGET_USER_ID)).thenReturn(false);

		final Result<Void> result = partygoerService.replyFr(CURRENT_USER_ID, TARGET_USER_ID, RequestReply.ACCEPT);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("The asker " + TARGET_USER_ID + " doesn't exist !");
	}

	@Test
	public void replyFrNotExists() {
		final Partygoer currentUser = new Partygoer();
		when(partygoerRepository.existsById(TARGET_USER_ID)).thenReturn(true);
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);

		final Result<Void> result = partygoerService.replyFr(CURRENT_USER_ID, TARGET_USER_ID, RequestReply.ACCEPT);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("The friend request doesn't exist");
	}

	@Test
	public void replyFrValid() {
		final Partygoer currentUser = new Partygoer();
		currentUser.setFriendReqReceived(Arrays.asList(new FriendRequest(TARGET_USER_ID, "jdoe", "Hey !")));
		when(partygoerRepository.existsById(TARGET_USER_ID)).thenReturn(true);
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);

		final Result<Void> result = partygoerService.replyFr(CURRENT_USER_ID, TARGET_USER_ID, RequestReply.ACCEPT);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getMessage()).isEqualTo("The friend request reply has been sent");
	}

	@Test
	public void cancelFrNotExists() {
		final Partygoer currentUser = new Partygoer();
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);

		final Result<Void> result = partygoerService.cancelFrSent(CURRENT_USER_ID, TARGET_USER_ID);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("The friend request doesn't exist");
	}

	@Test
	public void cancelFrValid() {
		final Partygoer currentUser = new Partygoer();
		currentUser.setFriendReqSent(Arrays.asList(new FriendRequest(TARGET_USER_ID, "jdoe", "Hey !")));
		when(partygoerRepository.getUserFriendsIds(CURRENT_USER_ID)).thenReturn(currentUser);

		final Result<Void> result = partygoerService.cancelFrSent(CURRENT_USER_ID, TARGET_USER_ID);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getMessage()).isEqualTo("The friend request has been canceled");
	}

}
