package com.buschmais.backend.adr.status;

public class ADRStatusApproved implements ADRStatus {
	@Override
	public ADRStatusType getType() {
		return ADRStatusType.APPROVED;
	}
}
