package com.buschmais.backend.image;

import com.buschmais.backend.adr.external.ExternalContent;
import com.buschmais.backend.adr.external.ExternalContentType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Image implements ExternalContent {
	@Setter(AccessLevel.NONE)
	@Id
	private String id;

	private String name;

	@PersistenceConstructor
	private Image(){}

	public Image(@NonNull String name) {
		this.name = name;
	}

	@Override
	public ExternalContentType getType() {
		return ExternalContentType.IMAGE;
	}
}
