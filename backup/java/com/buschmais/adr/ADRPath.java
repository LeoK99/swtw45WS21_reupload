package com.buschmais.backend.adr;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Data
public class ADRPath {
	@Setter(AccessLevel.NONE)
	String path;

	ADRPath(@NonNull String path){
		this.path = path;
	}

	public static ADRPath createRoot(){
		return new ADRPath("/");
	}

	public static ADRPath create(@NonNull String path){
		return new ADRPath(path);
	}

	public static ADRPath createFullPath(@NonNull String parent,
										 @NonNull String name){
		return new ADRPath(parent + "/" + name);
	}

	public ADRPath add(@NonNull final String name){
		return createFullPath(path, name);
	}

	public ADRPath up(){
		return create(
				path.equals("/")
				? path
				: path.replaceAll("\\/[\\w]+$", "")); // pops the "/" and everything after
	}

}
