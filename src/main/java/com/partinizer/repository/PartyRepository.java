package com.partinizer.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.partinizer.domain.Party;

@Repository
public interface PartyRepository extends MongoRepository<Party, String>, CustomPartyRepository {

	Optional<Collection<Party>> findByTitleIgnoreCaseStartingWith(final String name);

	Optional<Party> findByIdAndCreator(final String id, final String creatorId);

}
