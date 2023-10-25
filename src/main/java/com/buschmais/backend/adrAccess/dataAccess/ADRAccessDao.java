package com.buschmais.backend.adrAccess.dataAccess;

import com.buschmais.backend.adrAccess.AccessGroup;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ADRAccessDao {

	private final ADRAccessRepo repo;

	ADRAccessDao(@Autowired ADRAccessRepo repo) {
		this.repo = repo;
	}

	public List<AccessGroup> findAll() { return repo.findAll(); }

	public Optional<AccessGroup> findById(@NonNull String id) {
		return repo.findById(id);
	}

	public Optional<AccessGroup> findByName(@NonNull String name) {
		return repo.findByName(name);
	}

	public AccessGroup save(@NonNull AccessGroup group) {
		return repo.save(group);
	}

	public void deleteAll() {
		repo.deleteAll();
	}

	public void delete(@NonNull AccessGroup group) {
		repo.delete(group);
	}
}
