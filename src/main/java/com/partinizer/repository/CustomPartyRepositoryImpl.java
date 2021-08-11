package com.partinizer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.partinizer.domain.Invitation;
import com.partinizer.domain.Party;
import com.partinizer.domain.Partygoer;

public class CustomPartyRepositoryImpl implements CustomPartyRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	public void createParty(Party party) {
		party = mongoTemplate.save(party);
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(party.getCreator().getId());
		query.addCriteria(criteria);
		Update update = new Update();
		update.push("myParties", party.getId());
		mongoTemplate.updateFirst(query, update, Partygoer.class);

		// Avoid state should be: writes is not an empty list
		if (!party.getInvitations().isEmpty()) {
			final BulkOperations ops = mongoTemplate.bulkOps(BulkMode.ORDERED, Partygoer.class);
			for (Invitation invitation : party.getInvitations()) {
				update = new Update();
				update.push("invitations", party.getId());
				criteria = Criteria.where("_id").is(invitation.getPartygoer().getId());
				query = new Query();
				query.addCriteria(criteria);
				ops.updateOne(query, update);
			}
			ops.execute();
		}
	}

	@Override
	public void acceptInvitation(final String currentId, final String partyId) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(currentId);
		query.addCriteria(criteria);
		Update update = new Update();
		update.pull("invitations", partyId);
		update.push("myParticipations", partyId);
		mongoTemplate.findAndModify(query, update, Partygoer.class);

		query = new Query(new Criteria().andOperator(Criteria.where("_id").is(partyId),
				Criteria.where("invitations").elemMatch(Criteria.where("partygoerId").is(currentId))));
		update = new Update().set("invitations.$.status", "ACCEPTED");
		mongoTemplate.findAndModify(query, update, Party.class);
	}

	@Override
	public void denyInvitation(final String currentId, final String partyId) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(currentId);
		query.addCriteria(criteria);
		Update update = new Update();
		update.pull("invitations", partyId);
		mongoTemplate.findAndModify(query, update, Partygoer.class);

		query = new Query(new Criteria().andOperator(Criteria.where("_id").is(partyId),
				Criteria.where("invitations").elemMatch(Criteria.where("partygoerId").is(currentId))));
		update = new Update().set("invitations.$.status", "DENIED");
		mongoTemplate.findAndModify(query, update, Party.class);
	}

}
