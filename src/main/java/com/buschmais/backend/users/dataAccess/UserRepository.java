package com.buschmais.backend.users.dataAccess;

import com.buschmais.backend.users.User;
import com.buschmais.backend.users.UserRights;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByUserName(String userName);
	List<User> findAllByRights(UserRights rights);
}
