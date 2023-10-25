package com.buschmais.backend.adr;

import com.buschmais.backend.config.RecursiveSaving;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
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

	@RecursiveSaving
	@DBRef
	private ADRContainer parent;

	@PersistenceConstructor
	private ADRContainer(ADRPath fullPath, ADRPath parentPath){}

	public ADRContainer(@NonNull final String name){
		this.parent = null;
		this.name = name.trim();
	}
	public ADRContainer(@NonNull final ADRContainer parent,
						@NonNull final String name){
		this.parent = parent;
		this.name = name.trim();
	}

	@AccessType(AccessType.Type.PROPERTY)
	public ADRPath getParentPath(){
		if(parent == null)
			return ADRPath.createRoot();
		return parent.getFullPath();
	}

	private void setParentPath(ADRPath parentPath){
	}

	@AccessType(AccessType.Type.PROPERTY)
	public ADRPath getFullPath(){
		if(parent == null)
			return ADRPath.createRoot().add(name);
		return getParentPath().add(name);
	}


	public void setFullPath(ADRPath parentPath){
	}
}
