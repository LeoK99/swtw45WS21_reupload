package com.buschmais.backend.adr.dataAccess;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.ADRPath;
import com.buschmais.backend.adr.ADRTag;
import com.buschmais.backend.adr.status.ADRStatus;
import com.buschmais.backend.adr.status.ADRStatusType;
import com.buschmais.backend.users.User;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ADRRepository extends MongoRepository<ADR, String> {

	@NonNull Optional<ADR> findById(@NonNull String id);

	List<ADR> findAllByName(String name);
	List<ADR> findAllByAuthor(User author);
	Optional<ADR> findByFullPath(ADRPath path);
	List<ADR> findAllByParentPath(ADRPath folderPath);
	List<ADR> findAllByStatus(ADRStatus status);
	List<ADR> findAllByStatusType(ADRStatusType statusType);

	List<ADR> findAllByTagsIsContaining(List<ADRTag> tags);
	List<ADR> findAllByTitle(String title);



	void deleteByFullPath(ADRPath path);
	void deleteAllByParentPath(ADRPath parentPath);
}
