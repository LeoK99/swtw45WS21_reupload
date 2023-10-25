package com.buschmais.frontend.views.logout;

import com.buschmais.backend.users.dataAccess.SecurityService;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;

public class LogoutView extends VerticalLayout {

	private final SecurityService secServ;

	public LogoutView(@Autowired @NotNull SecurityService securityService){
		this.secServ = securityService;
		this.secServ.logOut();
		UI.getCurrent().getPage().setLocation(StringConstantsFrontend.LOGIN_PATH);
	}

}
