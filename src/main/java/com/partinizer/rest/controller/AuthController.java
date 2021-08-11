package com.partinizer.rest.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.partinizer.domain.Result;
import com.partinizer.repository.PartygoerRepository;
import com.partinizer.rest.dto.Dto;
import com.partinizer.rest.dto.EmptyDto;
import com.partinizer.rest.dto.ErrorDto;
import com.partinizer.rest.dto.LoginDto;
import com.partinizer.rest.dto.RegisterDto;
import com.partinizer.rest.payload.response.JwtAuthenticationResp;
import com.partinizer.security.JwtTokenProvider;
import com.partinizer.service.PartygoerService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PartygoerService partygoerService;

	@Autowired
	private PartygoerRepository partygoerRepository;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResp> authenticate(@Valid @RequestBody final LoginDto loginDto) {

		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		final String jwt = tokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResp(jwt));
	}

	@PostMapping("/signup")
	public ResponseEntity<Dto> register(@Valid @RequestBody final RegisterDto registerDto) {

		final Result<Void> result = this.partygoerService.register(RegisterDto.convertToPartygoer(registerDto));

		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.CREATED);
	}

	@GetMapping("/usernameExists")
	public ResponseEntity<Dto> usernameExists(@NotBlank @RequestParam("username") String username) {
		boolean isExist = true;
		isExist = partygoerRepository.existsByUsername(username);
		return isExist ? new ResponseEntity<>(new EmptyDto(), HttpStatus.CONFLICT)
				: new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}

	@GetMapping("/emailExists")
	public ResponseEntity<Dto> emailExists(@NotBlank @RequestParam("email") String email) {
		boolean isExist = true;
		isExist = partygoerRepository.existsByEmail(email);

		return isExist ? new ResponseEntity<>(new EmptyDto(), HttpStatus.CONFLICT)
				: new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}
}
