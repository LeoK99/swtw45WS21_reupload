package com.buschmais.backend.adr.status;

import com.buschmais.backend.voting.ADRReview;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

@Data
public class ADRStatusProposed implements ADRStatus {

	@Setter(AccessLevel.NONE)
	private ADRReview adrReview;

	@PersistenceConstructor
	public ADRStatusProposed(){
		adrReview = new ADRReview();
	}

	@EqualsAndHashCode.Include
	@Override
	public ADRStatusType getType() {
		return ADRStatusType.PROPOSED;
	}

	@Override
	public boolean isAlwaysAccessible() {
		return true;
	}

	@Override
	public boolean isVotable() {
		return true;
	}
}
