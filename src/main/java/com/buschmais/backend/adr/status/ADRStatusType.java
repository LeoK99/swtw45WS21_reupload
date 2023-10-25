package com.buschmais.backend.adr.status;

import com.buschmais.backend.constants.strings;

public enum ADRStatusType {
	CREATED(strings.adrStatusCreated()),
	INTERNALLY_PROPOSED(strings.adrStatusInternallyProposed()),
	PROPOSED(strings.adrStatusProposed()),
	APPROVED(strings.adrStatusApproved()),
	REFUSED(strings.adrStatusRefused()),
	SUPERSEDED(strings.adrStatusSuperseded());

	final private String name;
	ADRStatusType(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
}
