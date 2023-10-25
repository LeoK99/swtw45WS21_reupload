package com.buschmais.backend.adr;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ADRRepository extends MongoRepository<ADR, String> {

	List<ADR> findAllByName(String name);
	Optional<ADR> findByFullPath(ADRPath path);
	List<ADR> findAllByParentPath(ADRPath folderPath);
	List<ADR> findAllByTagsIsContaining(List<ADRTag> tags);
	List<ADR> findAllByTitle(String tags);

	void deleteByFullPath(ADRPath path);
	void deleteAllByParentPath(ADRPath parentPath);
}
