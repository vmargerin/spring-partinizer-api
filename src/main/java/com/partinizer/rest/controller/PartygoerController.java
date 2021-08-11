package com.partinizer.rest.controller;

import java.util.List;

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

import com.partinizer.domain.FriendResult;
import com.partinizer.domain.PartygoerResult;
import com.partinizer.domain.RequestReply;
import com.partinizer.domain.Result;
import com.partinizer.domain.validation.OnCreate;
import com.partinizer.rest.dto.Dto;
import com.partinizer.rest.dto.EmptyDto;
import com.partinizer.rest.dto.ErrorDto;
import com.partinizer.rest.dto.FriendReqDto;
import com.partinizer.rest.dto.FriendResultDto;
import com.partinizer.rest.dto.PartygoerResultDto;
import com.partinizer.security.CurrentUser;
import com.partinizer.security.UserPrincipal;
import com.partinizer.service.PartygoerService;

@RestController
@RequestMapping("/partygoers")
public class PartygoerController {

	@Autowired
	private PartygoerService partygoerService;

	@GetMapping(value = "/search")
	public ResponseEntity<Dto> searchPartygoer(@CurrentUser UserPrincipal currentUser,
			@RequestParam(required = true) @NotBlank String filter) {

		final Result<List<PartygoerResult>> result = partygoerService.search(filter, currentUser.getId());
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(new PartygoerResultDto(result.getTarget()), HttpStatus.OK);
	}

	@GetMapping(value = "/friends")
	public ResponseEntity<Dto> getFriends(@CurrentUser UserPrincipal currentUser) {

		final Result<FriendResult> result = partygoerService.getFriends(currentUser.getId());
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(new FriendResultDto(result.getTarget().getFriends(),
				result.getTarget().getReceived(), result.getTarget().getSent()), HttpStatus.OK);
	}

	@DeleteMapping(value = "/friends/{partygoerId}")
	public ResponseEntity<Dto> deleteFriend(@CurrentUser UserPrincipal currentUser,
			@PathVariable @NotBlank String partygoerId) {
		final Result<Void> result = partygoerService.deleteFriend(currentUser.getId(), partygoerId);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}

	@PostMapping(value = "/friends/requests/{partygoerId}")
	@Validated({ OnCreate.class })
	public ResponseEntity<Dto> createFriendRequest(@Valid @RequestBody final FriendReqDto friendReq,
			@CurrentUser UserPrincipal currentUser, @PathVariable @NotBlank String partygoerId) {
		final Result<Void> result = partygoerService.createFr(currentUser, friendReq.getMessage(), partygoerId);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.CREATED);
	}

	@GetMapping(value = "/friends/requests/{partygoerId}/accept")
	@Validated({ OnCreate.class })
	public ResponseEntity<Dto> acceptFriendRequest(@CurrentUser UserPrincipal currentUser,
			@PathVariable @NotBlank String partygoerId) {
		final Result<Void> result = partygoerService.replyFr(currentUser.getId(), partygoerId, RequestReply.ACCEPT);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}

	@GetMapping(value = "/friends/requests/{partygoerId}/deny")
	@Validated({ OnCreate.class })
	public ResponseEntity<Dto> denyFriendRequest(@CurrentUser UserPrincipal currentUser,
			@PathVariable @NotBlank String partygoerId) {
		final Result<Void> result = partygoerService.replyFr(currentUser.getId(), partygoerId, RequestReply.DENY);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}

	@DeleteMapping(value = "/friends/requests/{partygoerId}")
	public ResponseEntity<Dto> cancelFriendReqSent(@CurrentUser UserPrincipal currentUser,
			@PathVariable @NotBlank String partygoerId) {
		final Result<Void> result = partygoerService.cancelFrSent(currentUser.getId(), partygoerId);
		if (!result.isSuccess()) {
			return new ResponseEntity<>(new ErrorDto(result.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new EmptyDto(), HttpStatus.OK);
	}

}
