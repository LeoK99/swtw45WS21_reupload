package com.buschmais.backend.voting;

public class VoteFor implements Vote{
	@Override
	public VoteType getType() {
		return VoteType.FOR;
	}
}
