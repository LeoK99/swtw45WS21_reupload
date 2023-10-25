package com.buschmais.backend.adr;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

@Data
@Document
public class ADRContainer {
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	@EqualsExclude
	@Id
	private String id;

	@lombok.NonNull
	private String name;

	@DBRef
	private ADRContainer parent;

	private ADRContainer(){}

	public ADRContainer(@NonNull final String name){
		this.parent = null;
		this.name = name.trim();
	}
	public ADRContainer(@NonNull final ADRContainer parent,
						@NonNull final String name){
		this.parent = parent;
		this.name = name.trim();
	}

	public ADRPath getParentPath(){
		return parent.getFullPath();
	}

	public ADRPath getFullPath(){
		return getParentPath().add(name);
	}
}
