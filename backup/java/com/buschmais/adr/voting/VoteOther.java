package com.buschmais.backend.voting;

public class VoteOther implements Vote{
	@Override
	public VoteType getType() {
		return VoteType.OTHER;
	}
}
