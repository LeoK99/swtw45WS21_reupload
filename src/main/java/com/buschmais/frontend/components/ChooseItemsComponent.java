package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.vaadin.flow.component.dialog.Dialog;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

public class ChooseItemsComponent <T> extends Dialog {

	private final UserDao userService;
	private final ADRDao adrService;

	private final ADR currentAdr;

	private final Set<T> originalSet, addedSet, removedSet, remainingSet;

	public ChooseItemsComponent(@NonNull UserDao userService, @NonNull ADRDao adrService, @NonNull ADR adr){
		super();

		this.userService = userService;
		this.adrService = adrService;
		this.currentAdr = adr;

		this.originalSet = new HashSet<>();
		this.addedSet = new HashSet<>();
		this.removedSet = new HashSet<>();
		this.remainingSet = new HashSet<>();
	}

	private void init(){

	}

	private void setupComponents(){

	}

	private void addComponents(){

	}



}
