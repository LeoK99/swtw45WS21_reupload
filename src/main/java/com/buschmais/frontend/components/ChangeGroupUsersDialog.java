package com.buschmais.frontend.components;

import com.buschmais.backend.adrAccess.AccessGroup;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.NonNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.buschmais.frontend.vars.StringConstantsFrontend.*;

@CssImport(value = "./themes/adr-workbench/vaadin-components/invite-voter-dialog.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
public class ChangeGroupUsersDialog extends Dialog {

	private final UserDao userService;
	private final ADRAccessDao accessService;
	private final AccessGroup currentGroup;

	/* Layouts */
	private VerticalLayout mainLayout;

	private HorizontalLayout userMainLayout;
	private Div invitedUsersDiv;
	private VerticalLayout userFinderLayout;

	private HorizontalLayout buttonLayout;

	/* Label */
	private Label header;
	private Label message;

	/* Buttons */
	private Button inviteButton;
	private Button cancelButton;

	// User ComboBox
	private ComboBox<User> userComboBox;
	private ComboBox.ItemFilter<User> filter = (user, filterString) -> user.getUserName().toLowerCase().startsWith(filterString.toLowerCase());

	private final Set<User> originalMembers, addedMembers, removedMembers, remainingUsers;

	public ChangeGroupUsersDialog(@NonNull UserDao userService, @NonNull ADRAccessDao accessService, @NonNull String groupId) {
		super();
		this.userService = userService;
		this.accessService = accessService;
		this.currentGroup = accessService.findById(groupId).orElse(null);

		this.originalMembers = new HashSet<>();
		this.addedMembers = new HashSet<>();

		if(this.currentGroup == null) {
			new ErrorNotification("Gruppe nicht verfügbar!").open();
			this.close();
		}
		else {
			this.originalMembers.addAll(this.currentGroup.getUsers());
		}

		this.removedMembers = new HashSet<>(originalMembers);

		this.remainingUsers = new TreeSet<>(Comparator.reverseOrder());
		this.remainingUsers.addAll(this.userService.findAll().stream().filter(user -> !originalMembers.contains(user)).collect(Collectors.toSet()));

		this.setModal(true);
		this.setResizable(false);
		this.setSizeUndefined();

		setupComponents();
	}

	private void setupComponents() {

		{
			this.mainLayout = new VerticalLayout();
			this.userMainLayout = new HorizontalLayout();
			this.userFinderLayout = new VerticalLayout();
			this.buttonLayout = new HorizontalLayout();
			this.invitedUsersDiv = new Div();

			this.header = new Label(CHANGEGROUPUSERSDIALOG_TITLE);
			this.message = new Label(CHANGEGROUPUSERSDIALOG_INFORMATION_TEXT);

			this.userComboBox = new ComboBox<>(CHANGEGROUPUSERSDIALOG_COMBO_BOX_TITLE);
			this.userComboBox.setItems(filter, this.remainingUsers);
			this.userComboBox.setItemLabelGenerator(User::getUserName);

			this.inviteButton = new Button(GENERAL_DIALOG_BUTTON_SAVE);
			this.cancelButton = new Button(GENERAL_DIALOG_BUTTON_CANCEL);
		}

		{
			this.setCloseOnEsc(false);
			this.setCloseOnOutsideClick(false);
			this.setResizable(false);
			this.setDraggable(false);
			this.setMinWidth("min-content");
			this.setMinHeight("min-content");
			this.setWidth("initial");
			this.setMaxWidth("40%");
			this.setHeight("initial");
			this.setMaxHeight("90%");

			this.mainLayout.addClassName("main-layout");
			this.buttonLayout.addClassName("button-layout");
			this.invitedUsersDiv.addClassName("invited-voter-dialog-invited-voters-layout");
			this.userFinderLayout.addClassName("invited-voter-dialog-user-finder-layout");

			this.userFinderLayout.setSizeUndefined();

			this.header.addClassName("header");
			this.message.addClassName("context-text");

			this.inviteButton.addClassName("confirm-button");
			this.cancelButton.addClassName("cancel-button");
		}

		addComponents();
		addUserComboBoxListener();
		addCancelButtonListener();
		addInviteButtonListener();
	}

	private void addComponents() {

		this.userMainLayout.add(this.invitedUsersDiv, this.userFinderLayout);

		this.userFinderLayout.add(this.userComboBox);

		this.buttonLayout.add(this.inviteButton, this.cancelButton);

		this.mainLayout.add(this.header, this.message);
		this.mainLayout.add(this.userMainLayout);
		this.mainLayout.add(this.buttonLayout);

		this.currentGroup.getUsers().forEach(this::addUser);

		this.add(this.mainLayout);
	}

	private void addUser(User user) {
		Span userSpan = new Span(user.getUserName());
		userSpan.add(new Span(" "));
		userSpan.add(new Icon("trash"));
		userSpan.getElement().getThemeList().add("badge success");
		userSpan.addClassName("invite-voter-dialog-span-class");
		if (!addedMembers.contains(user) && (!originalMembers.contains(user) || removedMembers.contains(user))) {
			this.invitedUsersDiv.add(userSpan);

			if(!originalMembers.contains(user)) {
				this.addedMembers.add(user);

				userSpan.addClickListener(clickEvent -> {
					this.addedMembers.remove(user);
					this.invitedUsersDiv.remove(userSpan);

					this.remainingUsers.add(user);
					this.userComboBox.setItems(filter, this.remainingUsers);
				});
			}
			else {
				removedMembers.remove(user);

				userSpan.addClickListener(clickEvent -> {
					this.removedMembers.add(user);
					this.invitedUsersDiv.remove(userSpan);

					this.remainingUsers.add(user);
					this.userComboBox.setItems(filter, this.remainingUsers);
				});
			}

			this.remainingUsers.remove(user);
			this.userComboBox.setItems(filter, this.remainingUsers);
		}
	}

	private void addUserComboBoxListener() {
		this.userComboBox.addValueChangeListener(event ->
		{
			if (event.getValue() != null) {
				this.addUser(event.getValue());
			}
		});
	}

	private void addCancelButtonListener() {
		this.cancelButton.addClickListener(event -> this.close());
	}

	private void addInviteButtonListener() {
		this.inviteButton.addClickListener(event -> {
			synchronized (accessService) {
				AccessGroup group = this.accessService.findById(this.currentGroup.getId()).orElse(null);

				if(group == null) {
					this.close();
					new ErrorNotification(CHANGEGROUPUSERSDIALOG_GROUP_REMOVED_SUCCESSFULLY).open();
					return;
				}

				group.addUsers(addedMembers);

				this.removedMembers.forEach(user -> {
					if(!group.removeUser(user)) {
						new ErrorNotification(CHANGEGROUPUSERSDIALOG_USER_ALREADY_REMOVED.replaceAll("\\[UserName]", user.getUserName())).open();
					}
				});

				this.accessService.save(group);
			}

			this.close();
			new SuccessNotification(GENERAL_DIALOG_SAVED_CHANGES_SUCCESSFULLY).open();
		});
	}

}
