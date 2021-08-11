package com.partinizer.rest.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partinizer.domain.Friend;
import com.partinizer.domain.RequestReply;
import com.partinizer.domain.FriendRequest;
import com.partinizer.domain.FriendResult;
import com.partinizer.domain.PartygoerResult;
import com.partinizer.domain.Result;
import com.partinizer.repository.PartyRepository;
import com.partinizer.repository.PartygoerRepository;
import com.partinizer.security.JwtAuthenticationEntryPoint;
import com.partinizer.security.JwtTokenProvider;
import com.partinizer.security.UserPrincipal;
import com.partinizer.service.PartygoerService;

@WebMvcTest(PartygoerController.class)
@Import(TestConfig.class)
@WithUserDetails
public class PartygoerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PartygoerService partygoerService;

	@MockBean
	private PartygoerRepository partygoerRepository;

	@MockBean
	private PartyRepository partyRepository;

	@MockBean
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@MockBean
	private JwtTokenProvider tokenProvider;

	private final static String USER_ID = "5f87237359e19b2e0c4df356";
	private final static String FRIEND_ID = "5f87237359e19b2e0c4df356";
	private final static String USERNAME = "toto";
	private final static String ERROR_MESSAGE = "An error occured";
	private final static ObjectMapper MAPPER = new ObjectMapper();

	@Test
	public void searchPartygoer() throws Exception {
		final var partygoerResult = new PartygoerResult(USER_ID, USERNAME, Boolean.TRUE, Boolean.TRUE);
		when(partygoerService.search(anyString(), anyString()))
				.thenReturn(new Result<>(true, Arrays.asList(partygoerResult), null));
		this.mockMvc.perform(get("/partygoers/search?filter=toto")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"{\"results\":[{\"id\":\"5f87237359e19b2e0c4df356\",\"username\":\"toto\",\"friend\":true,\"pendingFr\":true}]}")));
	}

	@Test
	public void searchPartygoerWithError() throws Exception {
		when(partygoerService.search(anyString(), anyString())).thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(get("/partygoers/search?filter=toto")).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	public void getFriends() throws Exception {
		final var friendResult = new FriendResult(Arrays.asList(new Friend(USER_ID, USERNAME)),
				Arrays.asList(new FriendRequest(USER_ID, USERNAME, "")),
				Arrays.asList(new FriendRequest(USER_ID, USERNAME, "")));
		when(partygoerService.getFriends(anyString())).thenReturn(new Result<>(true, friendResult, null));
		this.mockMvc.perform(get("/partygoers/friends")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"{\"friends\":[{\"partygoerId\":\"5f87237359e19b2e0c4df356\",\"username\":\"toto\"}],\"received\":[{\"partygoerId\":\"5f87237359e19b2e0c4df356\",\"username\":\"toto\",\"message\":\"\"}],\"sent\":[{\"partygoerId\":\"5f87237359e19b2e0c4df356\",\"username\":\"toto\",\"message\":\"\"}]}")));
	}

	@Test
	public void getFriendsWithError() throws Exception {
		when(partygoerService.getFriends(anyString())).thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(get("/partygoers/friends")).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	public void deleteFriend() throws Exception {
		when(partygoerService.deleteFriend(anyString(), anyString())).thenReturn(new Result<>(true, null, null));
		this.mockMvc.perform(delete("/partygoers/friends/" + USER_ID)).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void deleteFriendWithError() throws Exception {
		when(partygoerService.deleteFriend(anyString(), anyString()))
				.thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(delete("/partygoers/friends/" + USER_ID)).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	public void createFriendRequest() throws Exception {
		final var message = "Hey Buddy !";
		final var friendRequest = new FriendRequest(FRIEND_ID, "", message);
		when(partygoerService.createFr(any(UserPrincipal.class), eq(message), eq(FRIEND_ID)))
				.thenReturn(new Result<>(true, null, null));
		this.mockMvc
				.perform(post("/partygoers/friends/requests/" + USER_ID).contentType(MediaType.APPLICATION_JSON)
						.content(MAPPER.writeValueAsString(friendRequest)))
				.andDo(print()).andExpect(status().isCreated());
	}

	@Test
	public void createFriendRequestWithError() throws Exception {
		final var message = "Hey Buddy !";
		final var friendRequest = new FriendRequest(FRIEND_ID, "", message);
		when(partygoerService.createFr(any(UserPrincipal.class), eq(message), eq(FRIEND_ID)))
				.thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc
				.perform(post("/partygoers/friends/requests/" + USER_ID).contentType(MediaType.APPLICATION_JSON)
						.content(MAPPER.writeValueAsString(friendRequest)))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	public void acceptFr() throws Exception {
		when(partygoerService.replyFr(eq(USER_ID), eq(FRIEND_ID), eq(RequestReply.ACCEPT)))
				.thenReturn(new Result<>(true, null, null));
		this.mockMvc.perform(get("/partygoers/friends/requests/" + FRIEND_ID + "/accept")).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void acceptFrWithError() throws Exception {
		when(partygoerService.replyFr(eq(USER_ID), eq(FRIEND_ID), eq(RequestReply.ACCEPT)))
				.thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(get("/partygoers/friends/requests/" + FRIEND_ID + "/accept")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	public void denyFr() throws Exception {
		when(partygoerService.replyFr(eq(USER_ID), eq(FRIEND_ID), eq(RequestReply.DENY)))
				.thenReturn(new Result<>(true, null, null));
		this.mockMvc.perform(get("/partygoers/friends/requests/" + FRIEND_ID + "/deny")).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void denyFrWithError() throws Exception {
		when(partygoerService.replyFr(eq(USER_ID), eq(FRIEND_ID), eq(RequestReply.DENY)))
				.thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(get("/partygoers/friends/requests/" + FRIEND_ID + "/deny")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	public void cancelFr() throws Exception {
		when(partygoerService.cancelFrSent(eq(USER_ID), eq(FRIEND_ID))).thenReturn(new Result<>(true, null, null));
		this.mockMvc.perform(delete("/partygoers/friends/requests/" + FRIEND_ID)).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void cancelFrWithError() throws Exception {
		when(partygoerService.cancelFrSent(eq(USER_ID), eq(FRIEND_ID)))
				.thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(delete("/partygoers/friends/requests/" + FRIEND_ID)).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

}
