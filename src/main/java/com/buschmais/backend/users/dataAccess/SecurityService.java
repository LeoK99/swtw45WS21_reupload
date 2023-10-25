package com.buschmais.backend.users.dataAccess;

import com.buschmais.backend.users.User;
import com.buschmais.frontend.vars.OtherConstantsFrontend;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.UIInitListener;
import com.vaadin.flow.server.VaadinServiceInitListener;

import javax.annotation.PostConstruct;
import java.util.List;

public interface SecurityService extends VaadinServiceInitListener, UIInitListener {
	boolean isRouteAccessible(String route);

	User getCurrentUser();

	boolean authenticate(String username, String password);
	boolean logOut();

	List<OtherConstantsFrontend.Route> getDynamicAuthorizedRoutes();
	List<OtherConstantsFrontend.Route> getDynamicUnauthorizedRoutes();
}

