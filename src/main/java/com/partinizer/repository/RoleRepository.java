package com.partinizer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.partinizer.domain.Role;
import com.partinizer.domain.RoleName;

@Repository
public interface RoleRepository extends MongoRepository<Role, Long> {
	Optional<Role> findByName(RoleName roleName);
}
