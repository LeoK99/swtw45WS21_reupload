package com.buschmais.frontend.components;

import com.buschmais.backend.adrAccess.AccessGroup;
import com.buschmais.backend.adrAccess.AccessRights;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.frontend.broadcasting.BroadcastListener;
import com.buschmais.frontend.broadcasting.Broadcaster;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.buschmais.frontend.vars.StringConstantsFrontend.*;

@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/vaadin-checkbox-group-styles.css", themeFor = "vaadin-checkbox-group")
@CssImport(value = "./themes/adr-workbench/vaadin-components/vaadin-checkbox-styles.css", themeFor = "vaadin-checkbox")
public class CreateNewGroupDialog extends Dialog {
	private final UserDao userDao;
	private final ADRAccessDao adrAccessDao;

	private VerticalLayout mainLayout;
	private HorizontalLayout manageLayout;
	private VerticalLayout boxandrightsLayout;
	private HorizontalLayout buttonsLayout;

	private Div membersDiv;

	private TextField groupname;

	private Button createButton;
	private Button cancelButton;

	private ComboBox<User> users;

	private CheckboxGroup<String> rightsbox;

	private Set<User> choosedUsers, selectUser;
	private Set<String> choosedRights;
	private List<String> rightsList;

	public CreateNewGroupDialog(@NonNull UserDao userDao, @NonNull ADRAccessDao adrAccessDao) {
		super();
		this.userDao = userDao;
		this.adrAccessDao = adrAccessDao;

		this.setModal(true);
		this.setSizeUndefined();
		this.setCloseOnEsc(true);
		this.setCloseOnOutsideClick(true);
		this.setResizable(true);
		this.setDraggable(true);
		this.setMinWidth("min-content");
		this.setMinHeight("min-content");
		this.setWidth("initial");
		this.setMaxWidth("40%");
		this.setHeight("initial");
		this.setMaxHeight("90%");

		mainLayout = new VerticalLayout();
		manageLayout = new HorizontalLayout();
		boxandrightsLayout = new VerticalLayout();
		buttonsLayout = new HorizontalLayout();
		membersDiv = new Div();

		groupname = new TextField();
		groupname.setLabel(StringConstantsFrontend.CREATENEWGROUP_NAME);
		groupname.setClearButtonVisible(true);

		manageLayout.add(membersDiv, boxandrightsLayout);
		mainLayout.add(groupname);
		mainLayout.add(manageLayout, buttonsLayout);
		this.add(mainLayout);

		selectUser = new HashSet<>();
		choosedUsers = new HashSet<>();
		usersboxConfigure();

		rightsbox = new CheckboxGroup<>();
		choosedRights = new HashSet<>();
		rightsboxConfigure();

		createButton = new Button(GENERAL_DIALOG_BUTTON_SAVE);
		createButton.addClassName("confirm-button");
		cancelButton = new Button(GENERAL_DIALOG_BUTTON_CANCEL);
		cancelButton.addClassName("cancel-button");
		buttonsLayout.add(createButton, cancelButton);
		buttonsConfigure();

	}

	private void usersboxConfigure(){
		selectUser.addAll(this.userDao.findAll());
		users = new ComboBox<>(StringConstantsFrontend.CREATENEWGROUP_USERS);
		ComboBox.ItemFilter<User> filter = (user, filterString) -> user.getUserName().toLowerCase().startsWith(filterString.toLowerCase());
		users.setItems(filter, selectUser);
		users.setItemLabelGenerator(User::getUserName);

		users.addValueChangeListener(event -> {
			if (event.getValue() != null) {
				User u = event.getValue();
				Span userSpan = new Span(new Icon(VaadinIcon.CLOSE), new Span(u.getUserName()));
				userSpan.getElement().getThemeList().add("badge primary");
				choosedUsers.add(u);
				membersDiv.add(userSpan);
				userSpan.addClickListener(e -> {
					choosedUsers.remove(u);
					membersDiv.remove(userSpan);
					selectUser.add(u);
					users.setItems(filter, selectUser);
				});
				selectUser.remove(u);
				users.setItems(filter, selectUser);
			}
		});
		boxandrightsLayout.add(users);
	}
	private void rightsboxConfigure(){
		rightsbox.setLabel(StringConstantsFrontend.CREATENEWGROUP_RIGHTS);
		rightsbox.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
		rightsList = new ArrayList<>();
		rightsList.add(GROUP_READABLE);
		rightsList.add(GROUP_WRITABLE);
		rightsList.add(GROUP_VOTABLE);
		rightsbox.setItems(rightsList);
		boxandrightsLayout.add(rightsbox);
	}

	private void buttonsConfigure(){
		createButton.addClickListener(event -> {
			synchronized (adrAccessDao) {
				if(!groupname.isEmpty()){
					AccessRights ar = new AccessRights(false,false,false);
					choosedRights = rightsbox.getValue();
					if(choosedRights.contains(GROUP_READABLE)){
						ar.setReadable(true);
					}
					if(choosedRights.contains(GROUP_WRITABLE)){
						ar.setWritable(true);
					}
					if(choosedRights.contains(GROUP_VOTABLE)){
						ar.setVotable(true);
					}
					AccessGroup ag = new AccessGroup(groupname.getValue(),ar);
					ag.addUsers(choosedUsers);
					ag = adrAccessDao.save(ag);
					this.close();
					Broadcaster.broadcastMessage(BroadcastListener.Event.GROUP_CHANGED, ag.getId(), this);
				}
			}
		});

		cancelButton.addClickListener(event -> this.close());
	}

}
