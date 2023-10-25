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
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;

@PageTitle("Group Information")
@CssImport(value = "./themes/adr-workbench/UserControl/GroupComponent.css")
public class GroupsInformationView extends VerticalLayout implements HasUrlParameter<String>, BroadcastListener, BeforeLeaveObserver {

	private final ADRAccessDao adrAccessDao;
	private final UserDao userDao;

	private String groupId;

	private HorizontalLayout groupsHeadLayout;
	private HorizontalLayout grouprightsLayout;
	private HorizontalLayout membersmanagementLayout;

	private Span readableYes;
	private Span readableNo;
	private Span writableYes;
	private Span writableNo;
	private Span votableYes;
	private Span votableNo;

	private Button deleteGroupButton;
	private Button manageUsers;
	private Button readable;
	private Button unreadable;
	private Button writable;
	private Button unwritable;
	private Button votable;
	private Button unvotable;

	private Dialog deleteConfirmDialog;

	public GroupsInformationView(@Autowired ADRAccessDao accessDao, @Autowired UserDao userDao){
		this.adrAccessDao = accessDao;
		this.userDao = userDao;

		Broadcaster.registerListener(this);
	}

	@Override
	public void setParameter(BeforeEvent beforeEvent, String groupname) {
		synchronized (adrAccessDao){
			Optional<AccessGroup> group1 = adrAccessDao.findByName(groupname);
			group1.ifPresent((group) -> {
				this.groupId = group.getId();

				Collection<User> members = group.getUsers();

				groupsHeadLayout = new HorizontalLayout();

				Label groupsname = new Label(group.getName());
				groupsname.getElement().getThemeList().add("badge primary");
				Button back = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
				back.addClickListener(event -> {
					String route = RouteConfiguration.forSessionScope().getUrl(UserControlView.class);
					this.getUI().ifPresent(page -> page.navigate(route));
				});
				deleteGroupButton = new Button(StringConstantsFrontend.GROUP_DELETE);
				deleteGroupButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
				deleteGroupButton.addClickListener(event -> {
					deleteConfirmDialog = new ConfirmDialog("Group löschen", "Die Gruppe \"" + group.getName() + "\" wirklich löschen?", "löschen", confirmClickEvent -> {
						synchronized (adrAccessDao){
							adrAccessDao.findByName(group.getName()).ifPresent(adrAccessDao::delete);
						}
						this.deleteConfirmDialog.close();
						new SuccessNotification("Die Gruppe wurde erfolgreich gelöscht!");
						String route = RouteConfiguration.forSessionScope().getUrl(UserControlView.class);
						this.removeAll();
						this.getUI().ifPresent(page -> page.navigate(route));
						Broadcaster.broadcastMessage(Event.GROUP_CHANGED, groupId, this);
					}, StringConstantsFrontend.GENERAL_DIALOG_BUTTON_CANCEL, cancelEvent -> {
						this.deleteConfirmDialog.close();
						new ErrorNotification("Es gab einen Fehler beim Löschen der Gruppe!").open();
					});
					this.deleteConfirmDialog.open();
				});

				manageUsers = new Button("Nutzer verwalten");
				manageUsers.addClickListener(e -> {
					ChangeGroupUsersDialog dialog = new ChangeGroupUsersDialog(this.userDao, this.adrAccessDao, this.groupId);

					dialog.addDetachListener(event -> {
						String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, group.getName());
						this.removeAll();
						this.getUI().ifPresent(page -> page.navigate(route));
					});
					dialog.open();
				});

				groupsHeadLayout.add(back,groupsname,manageUsers, deleteGroupButton);
				membersmanagementLayout = new HorizontalLayout();
				//addUserConfigure(group);
				//removeUserConfigure(group);

				this.setSizeFull();

				this.add(groupsHeadLayout);
				groupRightsConfigure(group);
				buttonsListenerConfigure(group);
				membersListConfigure(members);
				this.add(membersmanagementLayout);
			});
		}
	}

	private void groupRightsConfigure(AccessGroup group){

		readableYes = new Span(new Icon(VaadinIcon.CHECK), new Span(StringConstantsFrontend.GROUP_READABLE));
		readableNo = new Span(new Icon(VaadinIcon.CLOSE), new Span(StringConstantsFrontend.GROUP_READABLE));
		writableYes = new Span(new Icon(VaadinIcon.CHECK), new Span(StringConstantsFrontend.GROUP_WRITABLE));
		writableNo = new Span(new Icon(VaadinIcon.CLOSE), new Span(StringConstantsFrontend.GROUP_WRITABLE));
		votableYes = new Span(new Icon(VaadinIcon.CHECK), new Span(StringConstantsFrontend.GROUP_VOTABLE));
		votableNo = new Span(new Icon(VaadinIcon.CLOSE), new Span(StringConstantsFrontend.GROUP_VOTABLE));

		readable = new Button(StringConstantsFrontend.GROUP_READABLE);
		unreadable = new Button(StringConstantsFrontend.GROUP_UNREADABLE);
		writable = new Button(StringConstantsFrontend.GROUP_WRITABLE);
		unwritable = new Button(StringConstantsFrontend.GROUP_UNWRITABLE);
		votable = new Button(StringConstantsFrontend.GROUP_VOTABLE);
		unvotable = new Button(StringConstantsFrontend.GROUP_UNVOTABLE);

		readableYes.addClassName("readable-yes-span");
		readableNo.addClassName("readable-no-span");
		writableYes.addClassName("writable-yes-span");
		writableNo.addClassName("writable-no-span");
		votableYes.addClassName("votable-yes-span");
		votableNo.addClassName("votable-no-span");
		readable.addClassName("readable-button");
		unreadable.addClassName("unreadable-button");
		writable.addClassName("writable-button");
		unwritable.addClassName("unwritable-button");
		votable.addClassName("votable-button");
		unvotable.addClassName("unvotable-button");

		readableYes.getElement().getThemeList().add("badge");
		readableNo.getElement().getThemeList().add("badge");
		writableYes.getElement().getThemeList().add("badge");
		writableNo.getElement().getThemeList().add("badge");
		votableYes.getElement().getThemeList().add("badge");
		votableNo.getElement().getThemeList().add("badge");

		readable.addThemeVariants(ButtonVariant.LUMO_SMALL);
		unreadable.addThemeVariants(ButtonVariant.LUMO_SMALL);
		writable.addThemeVariants(ButtonVariant.LUMO_SMALL);
		unwritable.addThemeVariants(ButtonVariant.LUMO_SMALL);
		votable.addThemeVariants(ButtonVariant.LUMO_SMALL);
		unvotable.addThemeVariants(ButtonVariant.LUMO_SMALL);

		grouprightsLayout = new HorizontalLayout();

		if(group.getRights().isReadable()){
			grouprightsLayout.add(readableYes);
			grouprightsLayout.add(unreadable);
		}else{
			grouprightsLayout.add(readableNo);
			grouprightsLayout.add(readable);
		}
		if(group.getRights().isWritable()){
			grouprightsLayout.add(writableYes);
			grouprightsLayout.add(unwritable);
		}else{
			grouprightsLayout.add(writableNo);
			grouprightsLayout.add(writable);
		}
		if(group.getRights().isVotable()){
			grouprightsLayout.add(votableYes);
			grouprightsLayout.add(unvotable);
		}else{
			grouprightsLayout.add(votableNo);
			grouprightsLayout.add(votable);
		}

		this.add(grouprightsLayout);

	}

	private void membersListConfigure(Collection<User> members){
		Grid<User> userGrid = new Grid<>(User.class,false);

		userGrid.addColumn(User::getUserName).setHeader(StringConstantsFrontend.GROUP_NAME);
		userGrid.setItems(members);
		userGrid.addClassName("user-grid");
		userGrid.getColumns().forEach(col -> col.setAutoWidth(true));
		this.add(userGrid);
	}

	private void buttonsListenerConfigure(AccessGroup group){
		readable.addClickListener(event -> {
			synchronized (adrAccessDao) {
				group.getRights().setReadable(true);
				adrAccessDao.save(group);
				String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, group.getName());
				this.removeAll();
				this.getUI().ifPresent(page -> page.navigate(route));
			}
		});
		unreadable.addClickListener(event -> {
			synchronized (adrAccessDao) {
				group.getRights().setReadable(false);
				adrAccessDao.save(group);
				String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, group.getName());
				this.removeAll();
				this.getUI().ifPresent(page -> page.navigate(route));
			}
		});
		writable.addClickListener(event -> {
			synchronized (adrAccessDao) {
				group.getRights().setWritable(true);
				adrAccessDao.save(group);
				String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, group.getName());
				this.removeAll();
				this.getUI().ifPresent(page -> page.navigate(route));
			}
		});
		unwritable.addClickListener(event -> {
			synchronized (adrAccessDao) {
				group.getRights().setWritable(false);
				adrAccessDao.save(group);
				String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, group.getName());
				this.removeAll();
				this.getUI().ifPresent(page -> page.navigate(route));
			}
		});
		votable.addClickListener(event -> {
			synchronized (adrAccessDao) {
				group.getRights().setVotable(true);
				adrAccessDao.save(group);
				String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, group.getName());
				this.removeAll();
				this.getUI().ifPresent(page -> page.navigate(route));
			}
		});
		unvotable.addClickListener(event -> {
			synchronized (adrAccessDao) {
				group.getRights().setVotable(false);
				adrAccessDao.save(group);
				String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, group.getName());
				this.removeAll();
				this.getUI().ifPresent(page -> page.navigate(route));
			}
		});
	}

	@Override
	public void receiveBroadcast(BroadcastListener.Event event, String message) {
		if(event == Event.GROUP_CHANGED && groupId.equals(message)) {
			getUI().ifPresent(ui -> ui.access(() -> {
				new ErrorNotification(StringConstantsFrontend.GROUP_GOT_DELETED);
				ui.getPage().setLocation(StringConstantsFrontend.USER_CONTROL_PATH);
			}));
		}
/*			case ADR_CHANGED -> updateDescribingLabels();
			case ADR_REVIEW_STS_CHANGED -> {
				if (adrId != null && adrId.equals(message)) {
					getUI().ifPresent(ui -> ui.access(this::setVariableVotingSectionElements));
				}
			}
*/	}

	/*
	private void addUserConfigure(AccessGroup group){
		ComboBox<User> addUser = new ComboBox<>("add member");
		addUser.setPlaceholder("select user");
		addUser.setAllowCustomValue(false);
		ComboBox.ItemFilter<User> filter = (user, filterString) -> user.getUserName().toLowerCase().startsWith(filterString.toLowerCase());
		addUser.setItems(filter, userDao.findAll());
		addUser.setItemLabelGenerator(user -> {
			return user.getUserName();
		});
		Button add = new Button(StringConstantsFrontend.GROUP_ADD);
		add.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
		add.addClickListener(event -> {
			synchronized (adrAccessDao) {
				if(!addUser.isEmpty()){
					User u= addUser.getValue();
					if(!group.containsUser(u)){
						group.addUser(u);
						adrAccessDao.save(group);
						for (AccessGroup accessGroup : adrAccessDao.findAll()) {
							if (accessGroup.containsUser(u)) {
								if(accessGroup.hashCode() != group.hashCode()){
									accessGroup.removeUser(u);
									adrAccessDao.save(accessGroup);
									break;
								}
							}
						}
						String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, group.getName());
						this.removeAll();
						this.getUI().ifPresent(page -> {
							page.navigate(route);
						});
					}
				}
			}
		});
		membersmanagementLayout.add(addUser,add);
	}
	private void removeUserConfigure(AccessGroup group){
		ComboBox<User> removeUser = new ComboBox<>("remove member");
		removeUser.setPlaceholder("select user");
		removeUser.setAllowCustomValue(false);
		ComboBox.ItemFilter<User> filter = (user, filterString) -> user.getUserName().toLowerCase().startsWith(filterString.toLowerCase());
		removeUser.setItems(filter, group.getUsers());
		removeUser.setItemLabelGenerator(user -> {
			return user.getUserName();
		});
		Button remove = new Button(StringConstantsFrontend.MEMBERCOMPONENT_REMOVE);
		remove.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
		remove.addClickListener(event -> {
			synchronized (adrAccessDao) {
				if(!removeUser.isEmpty()){
					User u= removeUser.getValue();
					group.removeUser(u);
					adrAccessDao.save(group);
					String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, group.getName());
					this.removeAll();
					this.getUI().ifPresent(page -> {
						page.navigate(route);
					});
				}
			}
		});
		membersmanagementLayout.add(removeUser,remove);
	}
	 */

	@Override
	public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
		Broadcaster.unregisterListener(this);
	}
}
