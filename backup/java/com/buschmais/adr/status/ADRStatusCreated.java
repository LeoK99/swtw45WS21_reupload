package com.buschmais.backend.adr.status;

public class ADRStatusCreated implements ADRStatus {
	@Override
	public ADRStatusType getType() {
		return ADRStatusType.CREATED;
	}
}
