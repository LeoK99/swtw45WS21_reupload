package com.buschmais.frontend;

import com.buschmais.backend.users.dataAccess.SecurityService;
import com.buschmais.frontend.broadcasting.BroadcastListener;
import com.buschmais.frontend.broadcasting.Broadcaster;
import com.buschmais.frontend.components.ErrorNotification;
import com.buschmais.frontend.components.UserMenu;
import com.buschmais.frontend.vars.OtherConstantsFrontend;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport("./themes/adr-workbench/main-layout.css")
@PageTitle("Main")

public class MainLayout extends AppLayout implements VaadinServiceInitListener, BroadcastListener, BeforeLeaveObserver {

	SecurityService securityService;

	UserMenu usrMenu;
	private H1 viewTitle;

	public MainLayout(@Autowired SecurityService securityService,  @Autowired UserMenu usrMenu) {
		this.usrMenu = usrMenu.update();
		this.securityService = securityService;
		setPrimarySection(Section.DRAWER);
		addToNavbar(true, createHeaderContent());
		addToDrawer(createDrawerContent());

		Broadcaster.registerListener(this);
	}

	private Component createHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.addClassName("text-secondary");
		toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		toggle.getElement().setAttribute("aria-label", "Menu toggle");

		viewTitle = new H1();
		viewTitle.addClassNames("m-0", "text-l");

		Header header = new Header(toggle, viewTitle);
		header.addComponentAsFirst(usrMenu);
		header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
				"w-full");
		return header;
	}

	private Component createDrawerContent() {
		H2 appName = new H2("ADR-Workbench"); // Maybe read from pom.xml, plugin necessary
		appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

		com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
				createNavigation(), createFooter());
		section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
		return section;
	}

	private Nav createNavigation() {
		Nav nav = new Nav();
		nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
		nav.getElement().setAttribute("aria-labelledby", "views");

		// Wrap the links in a list; improves accessibility
		UnorderedList list = new UnorderedList();
		list.addClassNames("list-none", "m-0", "p-0");
		nav.add(list);

		for (RouterLink link : createLinks()) {
			link.addClassName("main-layout-router-link");
			ListItem item = new ListItem(link);
			item.addClassName("main-layout-list-item");
			list.add(item);
		}
		return nav;
	}

	private List<RouterLink> createLinks() {

		List<? extends Class<? extends Component>> menuComponentsAllowed = securityService.getDynamicAuthorizedRoutes().stream().map(OtherConstantsFrontend.Route::getView).toList();

		List<RouterLink> links = new ArrayList<>();
		for (OtherConstantsFrontend.MenuItemInfo menuItemInfo : OtherConstantsFrontend.MENU_ITEM_INFOS) {
			if (menuComponentsAllowed.contains(menuItemInfo.getView())) {
				links.add(createLink(menuItemInfo));
			}
		}

		return links;
	}

	private static RouterLink createLink(OtherConstantsFrontend.MenuItemInfo menuItemInfo) {
		RouterLink link = new RouterLink();
		link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
		link.setRoute(menuItemInfo.getView());

		Span icon = new Span();
		icon.addClassNames("me-s", "text-l");
		if (!menuItemInfo.getIconClass().isEmpty()) {
			icon.addClassNames(menuItemInfo.getIconClass());
		}

		Span text = new Span(menuItemInfo.getText());
		text.addClassNames("font-medium", "text-s");
		text.addClassName("main-layout-link-text");

		link.add(icon, text);
		return link;
	}

	private Footer createFooter() {
		Footer layout = new Footer();
		layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

		return layout;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		viewTitle.setText(getCurrentPageTitle());
	}

	private String getCurrentPageTitle() {
		PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
		return title == null ? "" : title.value();
	}

	@Override
	public void serviceInit(ServiceInitEvent serviceInitEvent) {
		serviceInitEvent.getSource().addUIInitListener(uiInitEvent -> {
			LoadingIndicatorConfiguration loadingConf = uiInitEvent.getUI().getLoadingIndicatorConfiguration();
			loadingConf.setApplyDefaultTheme(false);
		});
	}

	public void receiveBroadcast(Event event, String message) {
		if (event == Event.USER_DELETED) {
			getUI().ifPresent(ui -> ui.access(() -> {
				if(securityService.getCurrentUser() == null) {
					new ErrorNotification(StringConstantsFrontend.USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE);
					securityService.logOut();
					ui.getPage().setLocation(StringConstantsFrontend.LANDING_PAGE_PATH_ALIAS);
				}
			}));
		}
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
		Broadcaster.unregisterListener(this);
	}
}
