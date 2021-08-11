package com.partinizer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.partinizer.domain.FriendRequest;
import com.partinizer.domain.Partygoer;

public class CustomPartygoerRepositoryImpl implements CustomPartygoerRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public void addFriendRequest(final String currentId, final String currentUsername, final FriendRequest req) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(currentId);
		query.addCriteria(criteria);
		Update update = new Update();
		update.push("friendReqSent", req);
		mongoTemplate.updateFirst(query, update, Partygoer.class);

		query = new Query();
		criteria = Criteria.where("_id").is(req.getPartygoerId());
		query.addCriteria(criteria);
		update = new Update();
		update.push("friendReqReceived", new FriendRequest(currentId, currentUsername, req.getMessage()));
		mongoTemplate.updateFirst(query, update, Partygoer.class);

	}

	@Override
	public void acceptFriendRequest(final String currentId, final String askerId) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(currentId);
		query.addCriteria(criteria);
		Update update = new Update();
		update.push("friends", askerId);
		update.pull("friendReqReceived", new BasicDBObject("partygoerId", askerId));
		mongoTemplate.findAndModify(query, update, Partygoer.class);

		query = new Query();
		criteria = Criteria.where("_id").is(askerId);
		query.addCriteria(criteria);
		update.push("friends", currentId);
		update.pull("friendReqSent", new BasicDBObject("partygoerId", currentId));
		mongoTemplate.findAndModify(query, update, Partygoer.class);
	}

	@Override
	public void deleteFriend(final String currentId, final String partygoerId) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(currentId);
		query.addCriteria(criteria);
		Update update = new Update();
		update.pull("friends", partygoerId);
		mongoTemplate.findAndModify(query, update, Partygoer.class);

		query = new Query();
		criteria = Criteria.where("_id").is(partygoerId);
		query.addCriteria(criteria);
		update.pull("friends", currentId);
		mongoTemplate.findAndModify(query, update, Partygoer.class);
	}

	@Override
	public void denyFriendRequest(final String currentId, final String askerId) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(currentId);
		query.addCriteria(criteria);
		Update update = new Update();
		update.pull("friendReqReceived", new BasicDBObject("partygoerId", askerId));
		mongoTemplate.findAndModify(query, update, Partygoer.class);

		query = new Query();
		criteria = Criteria.where("_id").is(askerId);
		query.addCriteria(criteria);
		update.pull("friendReqSent", new BasicDBObject("partygoerId", currentId));
		mongoTemplate.findAndModify(query, update, Partygoer.class);
	}

	@Override
	public void deleteFriendRequest(final String currentId, final String recipientId) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(recipientId);
		query.addCriteria(criteria);
		Update update = new Update();
		update.pull("friendReqReceived", new BasicDBObject("partygoerId", currentId));
		mongoTemplate.findAndModify(query, update, Partygoer.class);

		query = new Query();
		criteria = Criteria.where("_id").is(currentId);
		query.addCriteria(criteria);
		update.pull("friendReqSent", new BasicDBObject("partygoerId", recipientId));
		mongoTemplate.findAndModify(query, update, Partygoer.class);
	}

}
