package com.buschmais.backend.image;

import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ImageRepository extends MongoRepository<Image, String> {

	Optional<Image> findByName(@NonNull String name);
}
