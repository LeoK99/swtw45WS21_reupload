package com.buschmais.backend.adr;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Data
public class ADRPath {
	@Setter(AccessLevel.NONE)
	String path;

	ADRPath(@NonNull final String path){
		this.path = path;
	}

	public static ADRPath createRoot(){
		return new ADRPath("/");
	}

	public static ADRPath create(@NonNull final String path){
		return new ADRPath(path);
	}

	public static ADRPath createFullPath(@NonNull final String parent,
										 @NonNull final String name){
		return new ADRPath(parent + "/" + name);
	}

	public ADRPath add(@NonNull final String name){
		return createFullPath(path, name);
	}

	public ADRPath up(){
		return create(
				path.equals("/")
				? path
				: path.replaceAll("\\/[\\w]+$", "")); // pops the last "/" and everything after
	}

	@Override
	public String toString(){
		return path;
	}
}
