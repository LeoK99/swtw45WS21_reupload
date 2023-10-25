package com.buschmais.backend.users.dataAccess;

import com.buschmais.backend.users.User;
import com.buschmais.backend.users.UserRights;
import com.buschmais.frontend.vars.OtherConstantsFrontend;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("securityService")
public class SecurityServiceImpl implements SecurityService {

	private final UserDao userDao;

	@Autowired
	public SecurityServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public void serviceInit(ServiceInitEvent event) { //Hier nur false als 2ten Parameter mitgeben!!!
		Arrays.asList(OtherConstantsFrontend.ALWAYS_ACCESSIBLE_ROUTES).forEach(route -> addRoute(route, false));
	}

	@Override
	public void uiInit(UIInitEvent uiInitEvent) {
		getDynamicAuthorizedRoutes  ().forEach(route -> addRoute(route, true));
		getDynamicUnauthorizedRoutes().forEach(route -> removeRoute(route, true));
	}

	@Override
	public boolean isRouteAccessible(String route) {
		return getDynamicAuthorizedRoutes().stream().map(OtherConstantsFrontend.Route::getPath).collect(Collectors.toList()).contains(route) || Arrays.stream(OtherConstantsFrontend.ALWAYS_ACCESSIBLE_ROUTES).map(OtherConstantsFrontend.Route::getPath).collect(Collectors.toList()).contains(route);
	}

	@Override
	public User getCurrentUser() {
		return userDao.getCurrentUser();
	}

	@Override
	public boolean authenticate(String username, String password) {
		Optional<User> optUser = userDao.findByUserName(username);

		if (optUser.isEmpty()) {
			return false;
		}

		User user = optUser.get();

		if (user.checkPassword(password)) {
			userDao.setCurrentUser(user);
			//getDynamicAuthorizedRoutes(user).stream().forEach(route -> addRoute(route, true));
			//getDynamicUnauthorizedRoutes(user).stream().forEach(route -> removeRoute(route, true));
			return true;
		}
		return false;
	}

	@Override
	public boolean logOut() {
		UI.getCurrent().getSession().setAttribute(User.class, null);
		getDynamicAuthorizedRoutes  ().forEach(route -> addRoute(route, true));
		getDynamicUnauthorizedRoutes().forEach(route -> removeRoute(route, true));
		return true;
	}

	@Override
	public List<OtherConstantsFrontend.Route> getDynamicAuthorizedRoutes() {

		List<OtherConstantsFrontend.Route> routes = new ArrayList<>();

		if(userDao.getCurrentUser() == null) {
			routes.addAll(Arrays.asList(OtherConstantsFrontend.LOGGED_OUT_ACCESSIBLE_ROUTES));
		}
		else {
			routes.addAll(Arrays.asList(OtherConstantsFrontend.LOGGED_IN_ACCESSIBLE_ROUTES));

			if(userDao.getCurrentUser().canManageUsers()) {
				routes.addAll(Arrays.asList(OtherConstantsFrontend.ADMIN_ACCESSIBLE_ROUTES));
			}
		}

		return routes;
	}

	@Override
	public List<OtherConstantsFrontend.Route> getDynamicUnauthorizedRoutes() {

		List<OtherConstantsFrontend.Route> routes = new ArrayList<>();

		if(userDao.getCurrentUser() == null) {
			routes.addAll(Arrays.asList(OtherConstantsFrontend.LOGGED_IN_ACCESSIBLE_ROUTES));
			routes.addAll(Arrays.asList(OtherConstantsFrontend.ADMIN_ACCESSIBLE_ROUTES));
		}
		else {
			routes.addAll(Arrays.asList(OtherConstantsFrontend.LOGGED_OUT_ACCESSIBLE_ROUTES));
		}

		return routes;
	}

	/**
	 * Adds a route to the accessible routes. If the route is already available it does nothing.
	 * @param route The route
	 * @param forSession If true, the route is only set for the active session. Else it's set for whole application
	 * @return returns true if routes changed, false if not
	 */
	private static boolean addRoute(@NonNull OtherConstantsFrontend.Route route, boolean forSession) {
		//System.out.print("Add " + route.getPath());
		if(forSession) {
			if(RouteConfiguration.forSessionScope().getUrlBase(route.getView()).isEmpty() || !RouteConfiguration.forSessionScope().getUrlBase(route.getView()).get().equals(route.getPath())) {
				//System.out.println(": ForSession ");
				RouteConfiguration.forSessionScope().setRoute(route.getPath(), route.getView(), route.getParentView());
				return true;
			}
		}
		else if(RouteConfiguration.forApplicationScope().getUrlBase(route.getView()).isEmpty() || !RouteConfiguration.forApplicationScope().getUrlBase(route.getView()).get().equals(route.getPath())) {
			//System.out.println(": ForApp ");
			RouteConfiguration.forApplicationScope().setRoute(route.getPath(), route.getView(), route.getParentView());
			return true;
		}
		//System.out.println(" not");
		return false;
	}

	/**
	 * Removes a route from the accessible routes. If the route isn't available it does nothing.
	 * @param route The route
	 * @param forSession If true, the route is only removed from the active session. Else it's removed from the whole application
	 * @return returns true if routes changed, false if not
	 */
	private static boolean removeRoute(OtherConstantsFrontend.Route route, boolean forSession) {
		//System.out.print("Remove " + route.getPath());
		if(forSession && RouteConfiguration.forSessionScope().isRouteRegistered(route.getView())) {
			//System.out.println(": ForSession " + forSession);
			RouteConfiguration.forSessionScope().removeRoute(route.getView());
			return true;
		}
		else if(!forSession && RouteConfiguration.forApplicationScope().isRouteRegistered(route.getView())) {
			//System.out.println(": ForApp " + forSession);
			RouteConfiguration.forApplicationScope().removeRoute(route.getView());
			return true;
		}
		//System.out.println();
		return false;
	}
}
