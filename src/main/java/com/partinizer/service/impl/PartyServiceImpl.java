package com.partinizer.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import com.partinizer.domain.Invitation;
import com.partinizer.domain.InvitationStatus;
import com.partinizer.domain.Party;
import com.partinizer.domain.PartyStatus;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.RequestReply;
import com.partinizer.domain.Result;
import com.partinizer.domain.validation.OnCreate;
import com.partinizer.repository.PartyRepository;
import com.partinizer.repository.PartygoerRepository;
import com.partinizer.service.PartyService;

@Service
@Validated
public class PartyServiceImpl implements PartyService {

	private PartyRepository partyRepository;

	private PartygoerRepository partygoerRepository;

	@Autowired
	public PartyServiceImpl(final PartyRepository partyRepository, final PartygoerRepository partygoerRepository) {
		this.partyRepository = partyRepository;
		this.partygoerRepository = partygoerRepository;
	}

	@Override
	public Result<Party> getParty(final String id) {
		final Optional<Party> opt = this.partyRepository.findById(id);
		if (opt.isEmpty()) {
			return new Result<>(false, null, "The party with the id " + id + "doesn't exist");
		}
		return new Result<>(true, opt.get(), null);
	}

	@Override
	public Result<Collection<Party>> searchParties(final String searchQuery) {
		if (StringUtils.isEmpty(searchQuery)) {
			return new Result<>(true, Collections.emptyList(), null);
		}
		final Optional<Collection<Party>> opt = this.partyRepository.findByTitleIgnoreCaseStartingWith(searchQuery);
		if (opt.isEmpty()) {
			return new Result<>(true, Collections.emptyList(), null);
		}
		return new Result<>(true, opt.get(), null);
	}

	@Override
	@Validated(OnCreate.class)
	public Result<Void> createParty(@Valid final Party party) {
		if (party.getSchedule().getStartDate().isAfter(party.getSchedule().getEndDate())) {
			return new Result<>(false, null, "The party start date can be anterior than the end");
		}
		party.setStatus(PartyStatus.OPENED);
		this.partyRepository.createParty(party);
		return new Result<>(true, null, "The party has been created.");
	}

	@Override
	public Result<Void> cancelParty(final String partyId, final String currentId) {
		final Optional<Party> optParty = this.partyRepository.findByIdAndCreator(partyId, currentId);
		// Check if party exists
		// TODO Or is not your party
		if (optParty.isEmpty()) {
			return new Result<>(false, null, "The party " + partyId + " doesn't exist !");
		}
		final Party party = optParty.get();
		party.setStatus(PartyStatus.CANCELLED);
		this.partyRepository.save(party);
		return new Result<>(true, null, "The party has been cancelled.");
	}

	@Override
	public Result<Partygoer> getUserWithParties(final String idUser) {
		return new Result<>(true, this.partygoerRepository.findByIdWithParties(idUser).get(), null);
	}

	@Override
	public Result<Void> replyInvitation(String currentId, String partyId, RequestReply response) {
		// Check if party exists
		if (!this.partyRepository.existsById(partyId)) {
			return new Result<>(false, null, "The party " + partyId + " doesn't exist !");
		}
		// Check if invitations exists
		final Partygoer partygoer = partygoerRepository.findByIdWithInvitations(currentId);
		final Collection<Party> invitations = partygoer.getInvitations();
		if (!invitations.stream().anyMatch((invitation) -> invitation.getId().equals(partyId))) {
			return new Result<>(false, null, "The party doesn't exist");
		}

		if (response == RequestReply.ACCEPT) {
			this.partyRepository.acceptInvitation(currentId, partyId);
		} else {
			this.partyRepository.denyInvitation(currentId, partyId);
		}
		return new Result<>(true, null, "The friend request reply has been sent");
	}

	@Override
	public Result<Void> addInvitee(String currentId, String partyId, String inviteeId) {
		final Optional<Party> optParty = partyRepository.findById(partyId);
		if (optParty.isEmpty()) {
			return new Result<>(false, null, "The party doesn't exist");
		}
		final Party party = optParty.get();
		if (!party.getCreator().getId().equals(currentId)) {
			return new Result<>(false, null, "The user " + currentId + "is not the owner of the party: " + partyId);
		}
		final Optional<Partygoer> optInvitee = partygoerRepository.findById(inviteeId);
		if (optParty.isEmpty()) {
			return new Result<>(false, null, "The partygoer " + inviteeId + " doesn't exist");
		}

		final boolean isExists = party.getInvitations().stream()
				.anyMatch(invitation -> invitation.getPartygoer().getId().equals(inviteeId));
		if (isExists) {
			return new Result<>(false, null, "The partygoer " + inviteeId + " is already invited in party " + partyId);
		}

		party.getInvitations().add(new Invitation(optInvitee.get(), InvitationStatus.PENDING));
		this.partyRepository.save(party);
		return new Result<>(true, null, "The invitee " + inviteeId + " has been added.");
	}

	@Override
	public Result<Void> deleteInvitee(String currentId, String partyId, String inviteeId) {
		final Optional<Party> optParty = partyRepository.findById(partyId);
		if (optParty.isEmpty()) {
			return new Result<>(false, null, "The party doesn't exist");
		}
		final Party party = optParty.get();
		if (!party.getCreator().getId().equals(currentId)) {
			return new Result<>(false, null, "The user " + currentId + "is not the owner of the party: " + partyId);
		}
		// TODO Check if invitee exists
		final List<Invitation> invitations = party.getInvitations().stream()
				.filter(invitation -> !invitation.getPartygoer().getId().equals(inviteeId))
				.collect(Collectors.toList());
		party.setInvitations(invitations);
		this.partyRepository.save(party);
		return new Result<>(true, null, "The invitee " + inviteeId + " has been deleted.");
	}

}
