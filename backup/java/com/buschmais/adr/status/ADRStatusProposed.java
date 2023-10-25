package com.buschmais.backend.adr.status;

public class ADRStatusProposed implements ADRStatus {

	@Override
	public ADRStatusType getType() {
		return ADRStatusType.PROPOSED;
	}
}
