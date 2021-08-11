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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.Result;
import com.partinizer.repository.PartygoerRepository;
import com.partinizer.rest.dto.LoginDto;
import com.partinizer.rest.dto.RegisterDto;
import com.partinizer.security.JwtAuthenticationEntryPoint;
import com.partinizer.security.JwtTokenProvider;
import com.partinizer.service.PartygoerService;

@WebMvcTest(AuthController.class)
@Import(TestConfig.class)
public class AuthControllerTest {

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private PartygoerService partygoerService;

	@MockBean
	private PartygoerRepository partygoerRepository;

	@MockBean
	private JwtTokenProvider tokenProvider;

	@MockBean
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private MockMvc mockMvc;

	private final static ObjectMapper MAPPER = new ObjectMapper();

	private final static String ERROR_MESSAGE = "An error occured";

	@Test
	public void authenticate() throws Exception {
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(new UsernamePasswordAuthenticationToken("toto", "tata"));
		final var loginDto = new LoginDto();
		loginDto.setUsernameOrEmail("toto");
		loginDto.setPassword("tata");
		this.mockMvc
				.perform(post("/auth/signin").contentType(MediaType.APPLICATION_JSON)
						.content(MAPPER.writeValueAsString(loginDto)))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("{\"tokenType\":\"Bearer\"}")));
	}

	@Test
	public void register() throws Exception {
		var registerDto = new RegisterDto();
		registerDto.setUsername("jdoe");
		registerDto.setFirstname("john");
		registerDto.setLastname("doe");
		registerDto.setEmail("jdoe@mail.com");
		registerDto.setPassword("P@ssword45");
		when(partygoerService.register(any(Partygoer.class))).thenReturn(new Result<Void>(true, null, null));
		this.mockMvc
				.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
						.content(MAPPER.writeValueAsString(registerDto)))
				.andDo(print()).andExpect(status().isCreated());
	}

	@Test
	public void registerWithError() throws Exception {
		var registerDto = new RegisterDto();
		registerDto.setUsername("jdoe");
		registerDto.setFirstname("john");
		registerDto.setLastname("doe");
		registerDto.setEmail("jdoe@mail.com");
		registerDto.setPassword("P@ssword45");
		when(partygoerService.register(any(Partygoer.class))).thenReturn(new Result<Void>(false, null, ERROR_MESSAGE));
		this.mockMvc
				.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
						.content(MAPPER.writeValueAsString(registerDto)))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("{\"errorMessage\":\"" + ERROR_MESSAGE + "\"}")));
	}

	@Test
	public void usernameNotExists() throws Exception {
		var username = "jdoe";
		when(partygoerRepository.existsByUsername(eq(username))).thenReturn(Boolean.FALSE);
		this.mockMvc.perform(get("/auth/usernameExists?username=" + username).contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void usernameExists() throws Exception {
		var username = "jdoe";
		when(partygoerRepository.existsByUsername(eq(username))).thenReturn(Boolean.TRUE);
		this.mockMvc.perform(get("/auth/usernameExists?username=" + username).contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isConflict());
	}

	@Test
	public void emailNotExists() throws Exception {
		var email = "jdoe@mail.com";
		when(partygoerRepository.existsByEmail(eq(email))).thenReturn(Boolean.FALSE);
		this.mockMvc.perform(get("/auth/emailExists?email=" + email).contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void emailExists() throws Exception {
		var email = "jdoe@mail.com";
		when(partygoerRepository.existsByEmail(eq(email))).thenReturn(Boolean.TRUE);
		this.mockMvc.perform(get("/auth/emailExists?email=" + email).contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isConflict());
	}

}
