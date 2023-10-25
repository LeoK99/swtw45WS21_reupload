package com.buschmais.backend.voting;

public class VoteAgainst implements Vote{

	@Override
	public VoteType getType() {
		return VoteType.AGAINST;
	}
}
