package com.buschmais.frontend.components;

import com.buschmais.backend.adrAccess.AccessGroup;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.users.User;
import com.buschmais.frontend.broadcasting.BroadcastListener;
import com.buschmais.frontend.broadcasting.Broadcaster;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.buschmais.frontend.views.usercontrol.GroupsInformationView;
import com.buschmais.frontend.views.usercontrol.UserControlView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouteConfiguration;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
@CssImport(value = "./themes/adr-workbench/UserControl/GroupComponent.css")
public class GroupComponent extends VerticalLayout {
	private final ADRAccessDao adrAccessDao;

	private AccessGroup group;
	private Collection<User> groupMembers;

	private HorizontalLayout headlineLayout;
	private HorizontalLayout rightsLayout;

	private Button groupNameButton;
	private Button deleteGroupButton;

	private Dialog deleteConfirmDialog;

	private Span readableYes;
	private Span readableNo;
	private Span writableYes;
	private Span writableNo;
	private Span votableYes;
	private Span votableNo;
	private Span commentableYes;
	private Span commentableNo;

	public GroupComponent(@NonNull AccessGroup accessGroup, @Autowired ADRAccessDao adrAccessDao){

		this.group = accessGroup;
		this.adrAccessDao = adrAccessDao;
		this.groupMembers = group.getUsers();

		groupNameButton = new Button(group.getName());
		groupNameButton.addClassName("group-name-button");
		deleteGroupButton = new Button(StringConstantsFrontend.GROUP_DELETE);
		deleteGroupButton.addClassName("delete-group-button");

		nameButtonConfigure();
		deleteGroupButtonConfigure();

		readableYes = new Span(new Icon(VaadinIcon.CHECK), new Span(StringConstantsFrontend.GROUP_READABLE));
		readableNo = new Span(new Icon(VaadinIcon.CLOSE), new Span(StringConstantsFrontend.GROUP_READABLE));
		writableYes = new Span(new Icon(VaadinIcon.CHECK), new Span(StringConstantsFrontend.GROUP_WRITABLE));
		writableNo = new Span(new Icon(VaadinIcon.CLOSE), new Span(StringConstantsFrontend.GROUP_WRITABLE));
		votableYes = new Span(new Icon(VaadinIcon.CHECK), new Span(StringConstantsFrontend.GROUP_VOTABLE));
		votableNo = new Span(new Icon(VaadinIcon.CLOSE), new Span(StringConstantsFrontend.GROUP_VOTABLE));
		readableYes.addClassName("readable-yes-span");
		readableNo.addClassName("readable-no-span");
		writableYes.addClassName("writable-yes-span");
		writableNo.addClassName("writable-no-span");
		votableYes.addClassName("votable-yes-span");
		votableNo.addClassName("votable-no-span");
		readableYes.getElement().getThemeList().add("badge");
		readableNo.getElement().getThemeList().add("badge");
		writableYes.getElement().getThemeList().add("badge");
		writableNo.getElement().getThemeList().add("badge");
		votableYes.getElement().getThemeList().add("badge");
		votableNo.getElement().getThemeList().add("badge");

		rightsLayout = new HorizontalLayout();
		if(group.getRights().isReadable()){
			rightsLayout.add(readableYes);
		}else{
			rightsLayout.add(readableNo);
		}
		if(group.getRights().isWritable()){
			rightsLayout.add(writableYes);
		}else{
			rightsLayout.add(writableNo);
		}
		if(group.getRights().isVotable()){
			rightsLayout.add(votableYes);
		}else{
			rightsLayout.add(votableNo);
		}

		headlineLayout = new HorizontalLayout();
		headlineLayout.add(groupNameButton,deleteGroupButton);
		this.add(headlineLayout, rightsLayout);

		HorizontalLayout members = new HorizontalLayout();
		if(groupMembers.size() <= 8){
			for (User member : groupMembers) {
				Label name = new Label(member.getUserName());
				name.getElement().getThemeList().add("badge normal");
				members.add(name);
			}
		}else{
			int i = 0;
			for (User member : groupMembers) {
				if(i < 8) {
					Label name = new Label(member.getUserName());
					name.getElement().getThemeList().add("badge normal");
					members.add(name);
					i++;
				}else{
					break;
				}
			}
			Label more = new Label(StringConstantsFrontend.GROUP_MORE);
			members.add(more);
		}
		this.add(members);

		this.addClassName("group-component");
	}

	private void nameButtonConfigure(){
		this.groupNameButton.addClickListener(event -> {
			String route = RouteConfiguration.forSessionScope().getUrl(GroupsInformationView.class, this.group.getName());
			this.getUI().ifPresent(page -> page.navigate(route));
		});
	}

	private void deleteGroupButtonConfigure(){
		this.deleteGroupButton.addClickListener(event -> {
			deleteConfirmDialog = new ConfirmDialog("Group löschen", "Die Gruppe \"" + group.getName() + "\" wirklich löschen?", "löschen", confirmClickEvent -> {
				synchronized (adrAccessDao){
					adrAccessDao.findByName(group.getName()).ifPresent(adrAccessDao::delete);
				}
				this.deleteConfirmDialog.close();
				new SuccessNotification("Die Gruppe wurde erfolgreich gelöscht!");
				Broadcaster.broadcastMessage(BroadcastListener.Event.GROUP_CHANGED, this.group.getId(), this);
			}, StringConstantsFrontend.GENERAL_DIALOG_BUTTON_CANCEL, cancelEvent -> {
				this.deleteConfirmDialog.close();
				new ErrorNotification("Es gab einen Fehler beim Löschen der Gruppe!").open();
			});
			deleteConfirmDialog.addDetachListener(e -> {
				String route = RouteConfiguration.forSessionScope().getUrl(UserControlView.class);
				//this.removeAll();
				this.getUI().ifPresent(page -> page.navigate(route));
			});
			this.deleteConfirmDialog.open();
		});
	}
}
