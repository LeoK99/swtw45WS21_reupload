package com.buschmais.backend.adr;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class ADRComment {

	@lombok.NonNull
	private String text;

	@Setter(AccessLevel.NONE)
	private List<ADRComment> replies;

	public ADRComment(){
		replies = new ArrayList<>();
	}

	public boolean addReply(@NonNull ADRComment comment){
		return replies.add(comment);
	}

	public boolean removeReply(@NonNull ADRComment comment){
		return replies.remove(comment);
	}
}
