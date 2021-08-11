package com.partinizer.repository;

import com.partinizer.domain.Party;

public interface CustomPartyRepository {

	void createParty(final Party party);

	void acceptInvitation(final String currentId, final String partyId);

	void denyInvitation(final String currentId, final String partyId);

}
