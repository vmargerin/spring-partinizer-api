package com.partinizer.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.partinizer.domain.Partygoer;

public interface PartygoerRepository extends MongoRepository<Partygoer, String>, CustomPartygoerRepository {

	Optional<Partygoer> findByEmail(String email);

	Partygoer findByUsername(String username);

	@Query(value = "{}", fields = "{friends : 1}")
	Optional<Collection<Partygoer>> findsById(String id);

	@Query(value = "{_id: ?0}", fields = "{username: 1, email: 1, myParties: 1, myParticipations: 1, creator: 1, invitations: 1}")
	Optional<Partygoer> findByIdWithParties(String id);

	@Query(value = "{_id: ?0}", fields = "{ invitations: 1}")
	Partygoer findByIdWithInvitations(String id);

	@Query(value = "{_id: ?0}", fields = "{username: 1}")
	Optional<Partygoer> findUsernameById(String id);

	Optional<Partygoer> findByUsernameOrEmail(String username, String email);

	@Query(value = "{_id: ?0}", fields = "{friendReqReceived: 1,friendReqSent : 1, friends: 1}")
	Partygoer getUserFriendsIds(String id);

	@Query(value = "{_id: {$in:?0}}", fields = "{username: 1}")
	Optional<Collection<Partygoer>> getUserFriends(Collection<String> ids);

	@Query(value = "{_id: ?0}", fields = "{friendReqReceived : 1}")
	Optional<Partygoer> findFriendReqReceivedById(String id);

	@Query(value = "{_id: {$ne: ?0}, username:  { $regex: ?1, $options: 'i' }}", fields = "{username : 1}")
	Optional<Collection<Partygoer>> findPartygoersByUsername(final String id, final String filter);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	boolean existsById(String id);

}
