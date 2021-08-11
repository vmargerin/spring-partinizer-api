package com.partinizer.rest.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.partinizer.domain.Invitation;
import com.partinizer.domain.InvitationStatus;
import com.partinizer.domain.Party;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.Result;
import com.partinizer.domain.Schedule;
import com.partinizer.repository.PartygoerRepository;
import com.partinizer.rest.dto.InviteeDto;
import com.partinizer.rest.dto.PartiesDto;
import com.partinizer.rest.dto.PartiesResultDto;
import com.partinizer.rest.dto.PartyDto;
import com.partinizer.security.JwtAuthenticationEntryPoint;
import com.partinizer.security.JwtTokenProvider;
import com.partinizer.service.PartyService;

@WebMvcTest(PartyController.class)
@Import(TestConfig.class)
public class PartyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PartyService partyService;

	@MockBean
	private PartygoerRepository userRepository;

	@MockBean
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@MockBean
	private JwtTokenProvider tokenProvider;

	private final static String ERROR_MESSAGE = "An error occured";

	private final static String PARTY_ID = "5f87255c59e19b2e0c4df357";
	private final static String USER_ID = "5f87237359e19b2e0c4df356";
	private final static String FRIEND_ID = "5f87237359e19b2e0c4df356";
	private final static String TITLE = "The Party";
	private final static String DESCRIPTION = "A awesome party";
	private final OffsetDateTime startDate = OffsetDateTime.now().plus(1, ChronoUnit.DAYS);
	private final OffsetDateTime endDate = OffsetDateTime.now().plus(2, ChronoUnit.DAYS);
	private final Party party;
	private final static ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	public PartyControllerTest() {
		var partygoer = new Partygoer(FRIEND_ID);
		party = new Party(PARTY_ID);
		party.setInvitations(Arrays.asList(new Invitation(partygoer, InvitationStatus.ACCEPTED)));
		party.setDescription(DESCRIPTION);
		party.setTitle(TITLE);
		party.setSchedule(new Schedule(startDate, endDate));
		party.setCreator(partygoer);
	}

	@Test
	@WithMockUser
	public void searchWithName() throws Exception {
		final var response = new PartiesResultDto(Arrays.asList(party));
		final var query = "partyname";
		final Collection<Party> parties = Arrays.asList(party);
		when(partyService.searchParties(eq(query))).thenReturn(new Result<>(true, parties, null));
		final MvcResult result = this.mockMvc.perform(get("/parties/search?name=" + query)).andDo(print())
				.andExpect(status().isOk()).andReturn();
		JSONAssert.assertEquals(MAPPER.writeValueAsString(response), result.getResponse().getContentAsString(), false);
	}

	@Test
	@WithMockUser
	public void searchWithError() throws Exception {
		final var query = "partyname";
		when(partyService.searchParties(eq(query))).thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(get("/parties/search?name=" + query)).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	@WithMockUser
	public void getParty() throws Exception {
		final var response = new PartyDto(PARTY_ID, TITLE, new Schedule(startDate, endDate));
		response.setDescription(DESCRIPTION);
		response.setInvitees(Arrays.asList(new InviteeDto(PARTY_ID, "toto", InvitationStatus.ACCEPTED)));
		when(partyService.getParty(eq(PARTY_ID))).thenReturn(new Result<>(true, this.party, null));
		final MvcResult result = this.mockMvc.perform(get("/parties/" + PARTY_ID)).andDo(print())
				.andExpect(status().isOk()).andReturn();
		JSONAssert.assertEquals(MAPPER.writeValueAsString(response), result.getResponse().getContentAsString(), false);
	}

	@Test
	@WithMockUser
	public void getPartyWithError() throws Exception {
		when(partyService.getParty(eq(PARTY_ID))).thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(get("/parties/" + PARTY_ID)).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	@WithUserDetails
	public void getPartiesWithError() throws Exception {
		when(partyService.getUserWithParties(eq(USER_ID))).thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(get("/parties/")).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	@WithUserDetails
	public void getParties() throws Exception {
		var parties = PartiesDto.convertToDto(Arrays.asList(this.party), Arrays.asList(this.party),
				Arrays.asList(this.party));
		final var partygoer = new Partygoer(USER_ID);
		partygoer.setMyParties(Arrays.asList(this.party));
		partygoer.setMyParticipations(Arrays.asList(this.party));
		when(partyService.getUserWithParties(eq(USER_ID))).thenReturn(new Result<>(true, partygoer, null));
		final MvcResult result = this.mockMvc.perform(get("/parties")).andDo(print()).andExpect(status().isOk())
				.andReturn();
		JSONAssert.assertEquals(MAPPER.writeValueAsString(parties), result.getResponse().getContentAsString(), false);
	}

	@Test
	@WithUserDetails
	public void createParty() throws Exception {
		final var partygoer = new Partygoer(USER_ID);
		partygoer.setMyParties(Arrays.asList(this.party));
		final var party = "{\"title\":\"" + TITLE + "\",\"description\":\"" + DESCRIPTION
				+ "\",\"schedule\":{\"startDate\":\"" + startDate + "\",\"endDate\":\"" + endDate
				+ "\"},\"invitations\":[\"" + FRIEND_ID + "\"]}";
		when(partyService.createParty(any(Party.class))).thenReturn(new Result<>(true, null, null));
		this.mockMvc.perform(post("/parties").contentType(MediaType.APPLICATION_JSON).content(party)).andDo(print())
				.andExpect(status().isCreated());
	}

	@Test
	@WithUserDetails
	public void createPartyWithError() throws Exception {
		final var partygoer = new Partygoer(USER_ID);
		partygoer.setMyParties(Arrays.asList(this.party));
		final var party = "{\"title\":\"" + TITLE + "\",\"description\":\"" + DESCRIPTION
				+ "\",\"schedule\":{\"startDate\":\"" + startDate + "\",\"endDate\":\"" + endDate
				+ "\"},\"invitations\":[\"" + FRIEND_ID + "\"]}";
		when(partyService.createParty(any(Party.class))).thenReturn(new Result<>(false, null, ERROR_MESSAGE));
		this.mockMvc.perform(post("/parties").contentType(MediaType.APPLICATION_JSON).content(party))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

}
