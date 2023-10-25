package com.buschmais.backend.adr.dataAccess;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.ADRPath;
import com.buschmais.backend.adr.ADRTag;
import com.buschmais.backend.adr.status.*;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ADRDao {

	private final ADRRepository repo;
	private final MongoOperations mongoOperations;

	ADRDao(@Autowired ADRRepository repo,
		   @Autowired MongoOperations mongoOperations){
		this.repo = repo;
		this.mongoOperations = mongoOperations;
	}

	public List<ADR> findAll() {
		return this.repo.findAll();
	}

	public Optional<ADR> findById(String id) {
		return repo.findById(id);
	}

	public List<ADR> findAllByName(String name) {
		return repo.findAllByName(name);
	}

	public List<ADR> findAllByAuthor(User author) {
		return repo.findAllByAuthor(author);
	}

	public Optional<ADR> findByFullPath(ADRPath path) {
		return repo.findByFullPath(path);
	}

	public List<ADR> findAllByParentPath(ADRPath folderPath) {
		return repo.findAllByParentPath(folderPath);
	}

	public List<ADR> findAllByStatus(ADRStatus status) {
		return repo.findAllByStatus(status);
	}

	public List<ADR> findAllByStatusType(ADRStatusType statusType) {
		return repo.findAllByStatusType(statusType);
	}

	public List<ADR> findAllRelevantByStatusType(ADRStatusType statusType, int number) {
		List<ADR> adrs = repo.findAllByStatusType(statusType);

		if(adrs.isEmpty()) return adrs;

		adrs.sort((a1, a2) -> a2.getTimeStamp().compareTo(a1.getTimeStamp()));
		number = Math.max(0, number);
		int endIndex = Math.min(number, adrs.size());

		return adrs.subList(0, endIndex);
	}

	public List<ADR> findAllByTagsIsContaining(List<ADRTag> tags) {
		return repo.findAllByTagsIsContaining(tags);
	}

	public List<ADR> findAllByTitle(String title) {
		return repo.findAllByTitle(title);
	}

	public void deleteAll() {
		repo.deleteAll();
	}

	public ADR save(@NonNull final ADR adr) {
		adr.setTimeStamp(Instant.now());
		return repo.save(adr);
	}

	public void deleteByFullPath(ADRPath path) {
		repo.deleteByFullPath(path);
	}

	public void deleteAllByParentPath(ADRPath parentPath) {
		repo.deleteAllByParentPath(parentPath);
	}

	public List<ADRTag> findAllTagsMatchRegex(@NonNull String regex){
		if (regex.isEmpty()) {
			return repo.findAll()
					.stream()
					.map(ADR::getTags)
					.flatMap(List::stream)
					.collect(Collectors.toList());
		}

		Query q = new Query();
		q.addCriteria(Criteria.where("tags").elemMatch(Criteria.where("tagText").regex(regex)));
		List<ADR> found = mongoOperations.find(q, ADR.class);

		Pattern p = Pattern.compile(regex);

		return found.stream()
				.map(ADR::getTags)
				.flatMap(List::stream)
				.filter(t -> p.matcher(t.getTagText()).find())
				.collect(Collectors.toList());
	}

	public List<String> findAllPathsAsStrings(){
		return repo.findAll()
				.stream()
				.map(adr -> adr.getFullPath().toString())
				.toList();
	}
}
