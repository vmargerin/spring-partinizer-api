package com.partinizer.service;

import java.util.Collection;

import javax.validation.Valid;

import com.partinizer.domain.Party;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.RequestReply;
import com.partinizer.domain.Result;

public interface PartyService {

	Result<Party> getParty(final String id);

	Result<Collection<Party>> searchParties(final String searchQuery);

	Result<Void> createParty(@Valid final Party party);

	Result<Void> cancelParty(final String partyId, final String currentId);

	Result<Partygoer> getUserWithParties(final String idUser);

	Result<Void> replyInvitation(final String currentId, final String partyId, RequestReply response);

	Result<Void> deleteInvitee(final String currentId, final String partyId, final String inviteeId);

	Result<Void> addInvitee(final String currentId, final String partyId, final String inviteeId);

}
