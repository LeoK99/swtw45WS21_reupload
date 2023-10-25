package com.buschmais.backend.adr;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ADRContainerRepository extends MongoRepository<ADRContainer, String> {

	List<ADRContainer> findAllByName(String name);
	Optional<ADRContainer> findByFullPath(ADRPath path);
	List<ADRContainer> findAllByParentPath(ADRPath folderPath);

	void deleteByFullPath(ADRPath path);
	void deleteAllByParentPath(ADRPath parentPath);
}
