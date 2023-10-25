package com.buschmais.backend.adr;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.NonNull;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;

@Data
public class ADRComment {

	@lombok.NonNull
	private String text;

	@lombok.NonNull
	private String author;

	private Instant timeStamp;

	@Setter(AccessLevel.NONE)
	private List<ADRComment> replies;

	public ADRComment(final @lombok.NonNull String author, final @lombok.NonNull String text){
		this.text = text;
		this.author = author;
		replies = new ArrayList<>();
	}

	public boolean addReply(@NonNull ADRComment comment){
		return replies.add(comment);
	}

	public boolean removeReply(@NonNull ADRComment comment){
		return replies.remove(comment);
	}
}
