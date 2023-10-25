package com.buschmais.backend.adr.status;

import com.buschmais.backend.voting.ADRReview;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

@Data
public class ADRStatusApproved implements ADRStatus {

	@Setter(AccessLevel.NONE)
	private ADRReview adrReview;

	@PersistenceConstructor
	private ADRStatusApproved(){}

	public ADRStatusApproved(final ADRReview review){
		this.adrReview = review;
	}

	@EqualsAndHashCode.Include
	@Override
	public ADRStatusType getType() {
		return ADRStatusType.APPROVED;
	}

	@Override
	public boolean isAlwaysAccessible() {
		return true;
	}

}
