package com.buschmais.backend.adr.status;

public class ADRStatusSuperseded implements ADRStatus{
	@Override
	public ADRStatusType getType() {
		return ADRStatusType.SUPERSEDED;
	}
}
