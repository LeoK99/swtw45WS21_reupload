package com.buschmais.backend.adrAccess.dataAccess;

import com.buschmais.backend.adrAccess.AccessGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ADRAccessRepo extends MongoRepository<AccessGroup, String> {
	List<AccessGroup> findAll();
	Optional<AccessGroup> findByName(String name);
}
