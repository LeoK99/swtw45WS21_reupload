package com.buschmais.backend.adr.status;

public class ADRStatusRefused implements ADRStatus {
	@Override
	public ADRStatusType getType() {
		return ADRStatusType.REFUSED;
	}
}
