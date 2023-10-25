package com.buschmais.backend.adr.status;

import com.buschmais.backend.voting.ADRReview;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

public class ADRStatusInternallyProposed implements ADRStatus {

	@Setter(AccessLevel.NONE)
	private ADRReview adrReview;

	@PersistenceConstructor
	public ADRStatusInternallyProposed(){
		adrReview = new ADRReview();
	}

	@EqualsAndHashCode.Include
	@Override
	public ADRStatusType getType() {
		return ADRStatusType.INTERNALLY_PROPOSED;
	}

	@Override
	public ADRReview getAdrReview() {
		return adrReview;
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	@Override
	public boolean isProposable() {
		return true;
	}

	@Override
	public boolean isAlwaysAccessible() {
		return false;
	}

	@Override
	public boolean isVotable() {
		return true;
	}
}
