package com.buschmais.backend.adr;

import com.buschmais.backend.adr.external.ExternalContent;
import com.buschmais.backend.adr.status.ADRStatus;
import lombok.AccessLevel;
import lombok.Data; // marks the class as a dataclass for automatic getters and setters
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed; // makes the object easily filterable via the marked field
import org.springframework.data.mongodb.core.mapping.DBRef;	// marks a field as a ref (won't embed it into the document)
import org.springframework.data.mongodb.core.mapping.Document; // marks the class as a document/entity with an ID
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
public final class ADR {

	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	@EqualsExclude
	@Id
	private String id;

	@Setter(AccessLevel.PRIVATE)
	@DBRef
	private ADRContainer parent;

	@lombok.NonNull
	@Indexed
	private String name;

	@lombok.NonNull
	@Indexed
	private String title;

	@lombok.NonNull
	private String content;

	@lombok.NonNull
	private String decision;

	@lombok.NonNull
	private String consequences;

	@lombok.NonNull
	@Indexed
	private ADRStatus sts;

	@Setter(AccessLevel.PRIVATE)
	@Indexed
	private List<ADRTag> tags;

	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	@DBRef
	private List<ExternalContent> externalContents;

	@Setter(AccessLevel.PRIVATE)
	private List<ADRComment> comments;

	private ADR(){}

	public ADR(@NonNull final ADRContainer parent,
			   @NonNull final String name,
			   @NonNull final ADRStatus sts){

		this.parent = parent;
		this.name = name.trim();
		this.sts = sts;
		this.tags = new ArrayList<>();
		this.externalContents = new ArrayList<>();
		this.comments = new ArrayList<>();
	}

	public boolean addTag(@NonNull final ADRTag tag){
		return tags.add(tag);
	}

	public boolean removeTag(@NonNull final ADRTag tag){
		return tags.remove(tag);
	}

	public boolean addExternalContent(@NonNull final ExternalContent content){
		return externalContents.add(content);
	}

	public boolean removeExternalContent(@NonNull final ExternalContent content){
		return externalContents.remove(content);
	}

	public boolean addADRComment(@NonNull final ADRComment comment){
		return comments.remove(comment);
	}

	public boolean removeADRComment(@NonNull final ADRComment comment){
		return comments.remove(comment);
	}

	public ADRPath getParentPath(){
		return parent.getFullPath();
	}

	public ADRPath getFullPath(){
		return getParentPath().add(name);
	}
}
