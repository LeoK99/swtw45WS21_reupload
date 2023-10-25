package com.buschmais.frontend.vars;

import com.buschmais.backend.users.User;
import com.buschmais.frontend.MainLayout;
import com.buschmais.frontend.views.ADRExplorer.ADRExplorerView;
import com.buschmais.frontend.views.adrCreate.ADRRichCreateView;
import com.buschmais.frontend.views.adrVote.ADRVoteView;
import com.buschmais.frontend.views.kanban.KanbanView;
import com.buschmais.frontend.views.login.LoginView;
import com.buschmais.frontend.views.logout.LogoutView;
import com.buschmais.frontend.views.notifications.NotiView;
import com.buschmais.frontend.views.user.UserView;
import com.buschmais.frontend.views.usercontrol.GroupsInformationView;
import com.buschmais.frontend.views.usercontrol.UserControlView;
import com.buschmais.frontend.views.welcome.WelcomeView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouterLayout;
import lombok.Getter;
import lombok.NonNull;

public class OtherConstantsFrontend {

	public static final User DEFAULT_DELETED_USER = new User(StringConstantsFrontend.DEFAULT_DELETED_USER_NAME, Double.toString(Math.random()*1000000000000d));

	public static class MenuItemInfo {

		private String text;
		private String iconClass;
		private Class<? extends Component> view;

		public MenuItemInfo(String text, String iconClass, Class<? extends Component> view) {
			this.text = text;
			this.iconClass = iconClass;
			this.view = view;
		}

		public String getText() {
			return text;
		}

		public String getIconClass() {
			return iconClass;
		}

		public Class<? extends Component> getView() {
			return view;
		}
	}
	public static final MenuItemInfo[] MENU_ITEM_INFOS = new MenuItemInfo[]{
			new MenuItemInfo(StringConstantsFrontend.MENU_TITLE_LOGIN, "la la-user", LoginView.class),

			new MenuItemInfo(StringConstantsFrontend.MENU_TITLE_OVERVIEW, "la la-clipboard", KanbanView.class),

			new MenuItemInfo(StringConstantsFrontend.MENU_TITLE_EXPLORER, "la la-archive", ADRExplorerView.class),

			new MenuItemInfo(StringConstantsFrontend.MENU_TITLE_CREATE_ADR, "la la-pencil", ADRRichCreateView.class),

			new MenuItemInfo(StringConstantsFrontend.MENU_TITLE_USER, "la la-user", UserView.class),

			new MenuItemInfo(StringConstantsFrontend.MENU_TITLE_USER_CONTROL, "la la-users", UserControlView.class),



			//new MenuItemInfo("ADRDetails", "la la-info-circle", ADRDetailsView.class),



	};


	public static class Route {

		@NonNull
		@Getter
		private String path;

		@NonNull
		@Getter
		private Class<? extends Component> view;

		@NonNull
		@Getter
		private Class<? extends RouterLayout> parentView;

		public Route(@NonNull String path, @NonNull Class<? extends Component> view) {
			this.path = path;
			this.view = view;
			this.parentView = MainLayout.class;
		}

		public Route(@NonNull String path, @NonNull Class<? extends Component> view, @NonNull Class<? extends RouterLayout> parentView) {
			this.path = path;
			this.view = view;
			this.parentView = parentView;
		}
	}
	public static final Route[] ALWAYS_ACCESSIBLE_ROUTES = new Route[] {
			new Route(StringConstantsFrontend.LANDING_PAGE_PATH, WelcomeView.class),
			new Route(StringConstantsFrontend.LANDING_PAGE_PATH_ALIAS, WelcomeView.class)
	};
	public static final Route[] LOGGED_OUT_ACCESSIBLE_ROUTES = new Route[] {
			new Route(StringConstantsFrontend.LOGIN_PATH, LoginView.class)
/*			new Route(StringConstantsFrontend.ADR_CREATE_PATH, ADRCreateView.class),
			new Route(StringConstantsFrontend.ADR_CREATE_PATH, ADRCreateView.class),
			new Route(StringConstantsFrontend.ADR_CREATE_PATH, ADRCreateView.class)
*/	};
	public static final Route[] LOGGED_IN_ACCESSIBLE_ROUTES = new Route[] {
			new Route(StringConstantsFrontend.ADR_CREATE_PATH, ADRRichCreateView.class),
			new Route(StringConstantsFrontend.OVERVIEW_PATH, KanbanView.class),
			new Route(StringConstantsFrontend.ADR_DETAILS_PATH, ADRVoteView.class),
			new Route(StringConstantsFrontend.USER_PATH, UserView.class),
			new Route(StringConstantsFrontend.LOGOUT_PATH, LogoutView.class),
			new Route(StringConstantsFrontend.NOTI_PATH, NotiView.class),
			new Route(StringConstantsFrontend.EXPL_PATH, ADRExplorerView.class),

//			new Route(StringConstantsFrontend.ADR_CREATE_PATH, ADRCreateView.class)
	};
	public static final Route[] ADMIN_ACCESSIBLE_ROUTES = new Route[] {
			new Route(StringConstantsFrontend.USER_CONTROL_PATH, UserControlView.class),
			new Route(StringConstantsFrontend.GROUP_INFORMATION_PATH, GroupsInformationView.class),
//			new Route(StringConstantsFrontend.ADR_CREATE_PATH, ADRCreateView.class)
	};


}
