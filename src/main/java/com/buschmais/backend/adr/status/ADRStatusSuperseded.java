package com.buschmais.backend.adr.status;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.voting.ADRReview;
import lombok.*;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class ADRStatusSuperseded implements ADRStatus{

	@Setter(AccessLevel.NONE)
	private ADRReview adrReview;

	@DBRef
	private ADR supersededById;

	@PersistenceConstructor
	private ADRStatusSuperseded(){}

	public ADRStatusSuperseded(final ADRReview review, @NonNull final ADR supersededBy){
		this.adrReview = review;
		this.supersededById = supersededBy;
	}

	@EqualsAndHashCode.Include
	@Override
	public ADRStatusType getType() {
		return ADRStatusType.SUPERSEDED;
	}

	@Override
	public boolean isAlwaysAccessible() {
		return true;
	}
}
