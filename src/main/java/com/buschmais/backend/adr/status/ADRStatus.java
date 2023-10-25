package com.buschmais.backend.adr.status;

import com.buschmais.backend.utils.Opt;
import com.buschmais.backend.voting.ADRReview;

import java.util.Optional;

public interface ADRStatus {
	ADRStatusType getType();
	ADRReview getAdrReview();
	default Opt<ADRReview> adrReviewAsOpt() {return Opt.ofNullable(getAdrReview());}

	boolean isAlwaysAccessible();

	default boolean isWritable(){
		return false;
	}

	default boolean isProposable(){
		return false;
	}

	default boolean isVotable(){
		return false;
	}

	default boolean isVotingStartable(){
		return false;
	}
}
