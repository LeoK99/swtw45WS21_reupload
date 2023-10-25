package com.buschmais.backend.adr.status;

import com.buschmais.backend.voting.ADRReview;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class ADRStatusCreated implements ADRStatus {

	@EqualsAndHashCode.Include
	@Override
	public ADRStatusType getType() {
		return ADRStatusType.CREATED;
	}

	@Override
	public ADRReview getAdrReview() {
		return null;
	}

	@Override
	public boolean isAlwaysAccessible() {
		return false;
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	@Override
	public boolean isVotingStartable() {
		return true;
	}

	@Override
	public boolean isProposable() {
		return true;
	}
}
