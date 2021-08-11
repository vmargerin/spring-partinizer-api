package com.partinizer.rest.controller;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.partinizer.domain.Party;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.RequestReply;
import com.partinizer.domain.Result;
import com.partinizer.domain.validation.OnCreate;
import com.partinizer.rest.dto.Dto;
import com.partinizer.rest.dto.EmptyDto;
import com.partinizer.rest.dto.ErrorDto;
import com.partinizer.rest.dto.PartiesDto;
import com.partinizer.rest.dto.PartiesResultDto;
import com.partinizer.rest.dto.PartyDto;
import com.partinizer.security.CurrentUser;
import com.partinizer.security.UserPrincipal;
import com.partinizer.service.PartyService;

@RestController
@RequestMapping("/parties")
public class PartyController {

	@Autowired
	private PartyService partyService;

	public PartyController() {
		super();
	}

	@GetMapping(value = "/search")
	public ResponseEntity<Dto> searchParty(@RequestParam Map<String, String> requestParams) {
		final String searchQuery = requestParams.get("name").toLowerCase(Locale.getDefault());
		final Result<Collection<Party>> result = partyService.searchParties(searchQuery);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new PartiesResultDto(result.getTarget()), HttpStatus.OK);
	}

	@GetMapping(value = "{partyId}")
	public ResponseEntity<Dto> getParty(@PathVariable String partyId) {
		final Result<Party> result = partyService.getParty(partyId);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(PartyDto.convertToPartyDto(result.getTarget()), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Dto> getUserParties(@CurrentUser UserPrincipal currentUser) {
		final Result<Partygoer> result = partyService.getUserWithParties(currentUser.getId());

		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}

		final PartiesDto parties = PartiesDto.convertToDto(result.getTarget().getMyParties(),
				result.getTarget().getMyParticipations(), result.getTarget().getInvitations());
		return new ResponseEntity<>(parties, HttpStatus.OK);
	}

	@PostMapping
	@Validated({ OnCreate.class })
	public ResponseEntity<Dto> createParty(@Valid @RequestBody final PartyDto partyDto,
			@CurrentUser UserPrincipal currentUser) {
		final Result<Void> result = partyService.createParty(PartyDto.convertToParty(partyDto, currentUser.getId()));

		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.CREATED);
	}

	@DeleteMapping(value = "{partyId}")
	public ResponseEntity<Dto> cancelParty(@PathVariable String partyId, @CurrentUser UserPrincipal currentUser) {
		final Result<Void> result = partyService.cancelParty(partyId, currentUser.getId());

		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.CREATED);
	}

	@GetMapping(value = "/invitations/{partyId}/accept")
	@Validated({ OnCreate.class })
	public ResponseEntity<Dto> acceptFriendRequest(@CurrentUser UserPrincipal currentUser,
			@PathVariable @NotBlank String partyId) {
		final Result<Void> result = partyService.replyInvitation(currentUser.getId(), partyId, RequestReply.ACCEPT);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}

	@GetMapping(value = "/invitations/{partyId}/deny")
	@Validated({ OnCreate.class })
	public ResponseEntity<Dto> denyFriendRequest(@CurrentUser UserPrincipal currentUser,
			@PathVariable @NotBlank String partyId) {
		final Result<Void> result = partyService.replyInvitation(currentUser.getId(), partyId, RequestReply.DENY);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}

	@PostMapping(value = "/{partyId}/invitees/{inviteeId}")
	public ResponseEntity<Dto> addInvitee(@CurrentUser UserPrincipal currentUser,
			@PathVariable @NotBlank String partyId, @PathVariable @NotBlank String inviteeId) {
		final Result<Void> result = partyService.addInvitee(currentUser.getId(), partyId, inviteeId);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}

	@DeleteMapping(value = "/{partyId}/invitees/{inviteeId}")
	public ResponseEntity<Dto> deleteInvitee(@CurrentUser UserPrincipal currentUser,
			@PathVariable @NotBlank String partyId, @PathVariable @NotBlank String inviteeId) {
		final Result<Void> result = partyService.deleteInvitee(currentUser.getId(), partyId, inviteeId);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}

}
