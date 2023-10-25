package com.buschmais.frontend.views.usercontrol;

import com.buschmais.backend.adrAccess.AccessGroup;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.frontend.broadcasting.BroadcastListener;
import com.buschmais.frontend.broadcasting.Broadcaster;
import com.buschmais.frontend.components.*;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

//@Route(value = "groupcontrol", layout = MainLayout.class)
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
@PageTitle("Group Management")
public class UserControlView extends HorizontalLayout implements BroadcastListener, BeforeLeaveObserver {
	private final ADRAccessDao adrAccessDao;
	private final UserDao userDao;

	private VerticalLayout groupsLayout;
	private VerticalLayout userFinderLayout;

	private List<AccessGroup> groupList;

	private ComboBox<User> userFinder;

	private Set<User> allUserSet;

	private Button createNewGroup;
	private Button createUserButton, deleteUserButton;

	private ConfirmDialog confirmDialog;

	public UserControlView(@Autowired ADRAccessDao adrAccessDao, @Autowired UserDao userDao) {
		this.adrAccessDao = adrAccessDao;
		this.userDao = userDao;

		this.allUserSet = new TreeSet<>(Comparator.reverseOrder());

		Broadcaster.registerListener(this);

		//groupsLayoutConfigure();
		userFinderConfigure();

		this.groupsLayout = groupsLayoutConfigure();

		this.add(groupsLayout, userFinderLayout);
	}

	private void userFinderConfigure() {
		userFinderLayout = new VerticalLayout();
		userFinder = new ComboBox<>(StringConstantsFrontend.USER_FINDER);
		ComboBox.ItemFilter<User> filter = (user, filterString) -> user.getUserName().toLowerCase().startsWith(filterString.toLowerCase());
		this.allUserSet.addAll(this.userDao.findAll().stream().filter(user -> !user.equals(this.userDao.getCurrentUser())).collect(Collectors.toSet()));
		userFinder.setItems(filter, this.allUserSet);
		userFinder.setItemLabelGenerator(User::getUserName);
		userFinder.setWidth(null);

		// create user button
		this.createUserButton = new Button(StringConstantsFrontend.USERMANAGEVIEW_CREATE_USER);
		this.createUserButton.addClassName("confirm-button");
		this.deleteUserButton = new Button(StringConstantsFrontend.DELETE_USER_BUTTON);

		this.createUserButton.addClickListener(event -> {
			CreateUserDialogComponent dialog = new CreateUserDialogComponent(userDao);
			dialog.addDetachListener(detachEvent -> {
				this.allUserSet = new TreeSet<>();
				this.allUserSet.addAll(this.userDao.findAll().stream().filter(user -> !user.equals(this.userDao.getCurrentUser())).collect(Collectors.toSet()));
				userFinder.setItems(filter, this.allUserSet);
				userFinderLayout.removeAll();
				userFinderLayout.add(userFinder, createUserButton, deleteUserButton);
			});
			dialog.open();
		});

		deleteUserButton.addClassName("cancel-button");
		userFinder.addValueChangeListener(event -> {
			if (!userFinder.isEmpty()) {
				User u = userFinder.getValue();
				userFinderLayout.removeAll();
				userFinderLayout.add(userFinder, createUserButton, deleteUserButton);
				userFinderLayout.add(new UserManageView(u, userDao));
			}
		});
		deleteUserButton.addClickListener(event -> {
			if (!userFinder.isEmpty()) {
				this.confirmDialog = new ConfirmDialog(StringConstantsFrontend.USERMANAGEVIEW_DELETE_USER, StringConstantsFrontend.USERMANAGEVIEW_DELETE_USER_CONFIRM.replaceAll("\\[UserName]", userFinder.getValue().getUserName()), StringConstantsFrontend.USERMANAGEVIEW_DELETE_USER, confirmClickEvent -> {
					if (!userFinder.isEmpty()) {
						User u = userFinder.getValue();
						synchronized (userDao) {
							userDao.findById(u.getId()).ifPresent(userDao::delete);
						}

						this.confirmDialog.close();
						new SuccessNotification(StringConstantsFrontend.USERNAMANAGEVIEW_USER_DELETED_SUCCESSFULLY.replaceAll("\\[UserName]", userFinder.getValue().getUserName()));

						this.allUserSet.remove(u);
						userFinder.setItems(filter, this.allUserSet);
						userFinderLayout.removeAll();
						userFinderLayout.add(userFinder, createUserButton, deleteUserButton);

						Broadcaster.broadcastMessage(Event.USER_DELETED, u.getId(), this);
					}
				}, StringConstantsFrontend.GENERAL_DIALOG_BUTTON_CANCEL, cancelEvent -> {
					this.confirmDialog.close();
					new ErrorNotification(StringConstantsFrontend.USERMANAGEVIEW_ERROR_DELETING_USER).open();
				});

				this.confirmDialog.open();
			}else{
				new ErrorNotification(StringConstantsFrontend.USERMANAGEVIEW_SPECIFY_USER).open();
			}
		});

		userFinderLayout.add(userFinder, this.createUserButton, deleteUserButton);
	}

	private VerticalLayout groupsLayoutConfigure() {
		VerticalLayout groupsLayout = new VerticalLayout();
		groupList = adrAccessDao.findAll();
		for (AccessGroup group : groupList) {
			groupsLayout.add(new GroupComponent(group, this.adrAccessDao));
		}
		groupsLayout.setSpacing(false);
		groupsLayout.getThemeList().add("spacing-l");
		groupsLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		groupsLayout.add(buttonConfigure());
		return groupsLayout;
	}

	private Button buttonConfigure() {
		createNewGroup = new Button(StringConstantsFrontend.USERMANAGEVIEW_CREATENEWGROUP);
		createNewGroup.addClassName("confirm-button");
		createNewGroup.getStyle().set("width", "100%");
		createNewGroup.addClickListener(event -> {
			CreateNewGroupDialog cngd = new CreateNewGroupDialog(userDao, adrAccessDao);
			cngd.open();
		});
		return createNewGroup;
	}

	@Override
	public void receiveBroadcast(Event event, String message) {
		switch (event){
			case GROUP_CHANGED -> getUI().ifPresent(ui -> ui.access(() -> {
				this.remove(groupsLayout);
				this.groupsLayout = groupsLayoutConfigure();
				this.addComponentAsFirst(groupsLayout);
			}));
			case USER_DELETED -> getUI().ifPresent(ui -> ui.access(() -> {
				this.userFinderLayout.remove(userFinder);
				this.userFinder.setItems(userDao.findAll());
				this.userFinderLayout.addComponentAsFirst(userFinder);
			}));
/*			case ADR_REVIEW_STS_CHANGED -> {
				if (adrId != null && adrId.equals(message)) {
					getUI().ifPresent(ui -> ui.access(this::setVariableVotingSectionElements));
				}
			}
*/		}
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
		Broadcaster.unregisterListener(this);
	}
}

