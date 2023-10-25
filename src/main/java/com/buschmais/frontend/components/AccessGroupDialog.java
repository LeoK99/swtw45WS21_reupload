package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adrAccess.AccessGroup;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
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

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.buschmais.frontend.vars.StringConstantsFrontend.*;

@CssImport(value = "./themes/adr-workbench/vaadin-components/invite-voter-dialog.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
public class AccessGroupDialog extends Dialog {

	private final ADRAccessDao accesGroupService;
	private final ADRDao adrService;
	private final ADR currentAdr;

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
	private ComboBox<AccessGroup> userComboBox;
	private ComboBox.ItemFilter<AccessGroup> filter = (accessGroup, filterString) -> accessGroup.getName().toLowerCase().startsWith(filterString.toLowerCase());

	private final Set<AccessGroup> originalAccessGroups, addedAccessGroups, removedAccessGroups, remainingAccessGroups;

	public AccessGroupDialog(@NonNull ADRAccessDao groupsService, @NonNull ADRDao adrService, @NonNull String adrId) {
		super();
		this.accesGroupService = groupsService;
		this.adrService = adrService;
		this.currentAdr = this.adrService.findById(adrId).orElse(null);
		this.originalAccessGroups = new HashSet<>();
		this.addedAccessGroups = new HashSet<>();

		if (this.currentAdr == null) {
			new ErrorNotification(ERROR_NOTIFICATION_ADR_NOT_AVAILABLE).open();
			this.close();
		} else {
			this.originalAccessGroups.addAll(this.currentAdr.getAccessGroups());
		}

		this.removedAccessGroups = new HashSet<>(this.originalAccessGroups);

		this.remainingAccessGroups = new TreeSet<>(AccessGroup::compareTo);
		this.remainingAccessGroups.addAll(this.accesGroupService.findAll().stream().filter(accessGroup -> !originalAccessGroups.contains(accessGroup)).collect(Collectors.toSet()));

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

			this.header = new Label(ACCESS_GROUP_DIALOG_TITLE);
			this.message = new Label(ACCESS_GROUP_DIALOG_INFORMATION_TEXT);

			this.userComboBox = new ComboBox<>(ACCESS_GROUP_DIALOG_COMBO_BOX_TITLE);
			this.userComboBox.setItems(this.filter, this.remainingAccessGroups);
			this.userComboBox.setItemLabelGenerator(AccessGroup::getName);

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

		this.originalAccessGroups.forEach(this::addAccessGroup);

		this.add(this.mainLayout);
	}

	private void addAccessGroup(AccessGroup accessGroup) {
		Span userSpan = new Span(accessGroup.getName());
		userSpan.add(new Span(" "));
		userSpan.add(new Icon("trash"));
		userSpan.getElement().getThemeList().add("badge success");
		userSpan.addClassName("invite-voter-dialog-span-class");
		if (!addedAccessGroups.contains(accessGroup) && (!originalAccessGroups.contains(accessGroup) || removedAccessGroups.contains(accessGroup))) {
			this.invitedUsersDiv.add(userSpan);

			if (!originalAccessGroups.contains(accessGroup)) {
				this.addedAccessGroups.add(accessGroup);

				userSpan.addClickListener(clickEvent -> {
					this.addedAccessGroups.remove(accessGroup);
					this.invitedUsersDiv.remove(userSpan);

					this.remainingAccessGroups.add(accessGroup);
					this.userComboBox.setItems(this.filter, this.remainingAccessGroups);
				});
			} else {
				this.removedAccessGroups.remove(accessGroup);

				userSpan.addClickListener(clickEvent -> {
					this.removedAccessGroups.add(accessGroup);
					this.invitedUsersDiv.remove(userSpan);

					this.remainingAccessGroups.add(accessGroup);
					this.userComboBox.setItems(filter, this.remainingAccessGroups);
				});
			}

			this.remainingAccessGroups.remove(accessGroup);
			this.userComboBox.setItems(filter, this.remainingAccessGroups);
		}
	}

	private void addUserComboBoxListener() {
		this.userComboBox.addValueChangeListener(event -> {

			if (event.getValue() != null) {
				this.addAccessGroup(event.getValue());
			}
		});
	}

	private void addCancelButtonListener() {
		this.cancelButton.addClickListener(event -> this.close());
	}

	private void addInviteButtonListener() {
		this.inviteButton.addClickListener(event -> {
			synchronized (adrService) {
				ADR adr = this.adrService.findById(this.currentAdr.getId()).orElse(null);

				if (adr == null) {
					this.close();
					new ErrorNotification(ERROR_NOTIFICATION_ADR_NOT_AVAILABLE).open();
					return;
				}

				this.addedAccessGroups.forEach(adr::addAccessGroup);

				this.removedAccessGroups.forEach(accessGroup -> {
					if (!adr.removeAccessGroup(accessGroup)) {
						new ErrorNotification(ACCESS_GROUP_DIALOG_ACCESS_GROUP_ALREADY_REMOVED.replaceAll("\\[AccessGroupName]", accessGroup.getName())).open();
					}
				});

				this.adrService.save(adr);
			}

			this.close();
			new SuccessNotification(CHANGEGROUPUSERSDIALOG_ACCESS_CHANGE_SUCCESSFUL).open();
		});
	}
}
