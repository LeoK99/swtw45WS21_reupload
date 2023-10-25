package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.backend.voting.ADRReview;
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
public class InviteVoterDialog extends Dialog {

	private final UserDao userService;
	private final ADRDao adrService;
	private final ADR currentADR;

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
	private ComboBox.ItemFilter<User> filter;
	private ComboBox<User> userComboBox;

	private final Set<User> originalVoters, addedVoters, removedVoters, remainingUsers;

	public InviteVoterDialog(@NonNull UserDao userService, @NonNull ADRDao adrService, @NonNull ADR adr) {
		super();
		this.userService = userService;
		this.adrService = adrService;
		this.currentADR = adr;
		this.originalVoters = new HashSet<>();
		this.originalVoters.addAll(this.currentADR.getStatus().adrReviewAsOpt().get().getInvitedVoters());
		this.addedVoters = new HashSet<>();
		this.removedVoters = new HashSet<>(originalVoters);

		this.remainingUsers = new TreeSet<>(Comparator.reverseOrder());
		this.remainingUsers.addAll(this.userService.findAll().stream().filter(user ->  !originalVoters.contains(user) && (adr.getAuthor().isEmpty() || !adr.getAuthor().get().equals(user))).collect(Collectors.toSet()));

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

			this.header = new Label(INVITEVOTERDIALOG_TITLE);
			this.message = new Label(INVITEVOTERDIALOG_INFORMATION_TEXT);

			this.userComboBox = new ComboBox<>(INVITEVOTERDIALOG_COMBO_BOX_TITLE);
			this.filter = (user, filterString) -> user.getUserName().toLowerCase().startsWith(filterString.toLowerCase());
			this.userComboBox.setItems(this.filter, this.remainingUsers);
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

		this.originalVoters.forEach(this::addUser);

		this.add(this.mainLayout);
	}

	private void addUser(@NonNull User user) {
		if (!addedVoters.contains(user) && (!originalVoters.contains(user) || removedVoters.contains(user))) {
			Span userSpan = createUserSpan(user); // Span

			if (!originalVoters.contains(user)) {
				this.addedVoters.add(user);

				userSpan.addClickListener(clickEvent -> {
					this.addedVoters.remove(user);
					this.invitedUsersDiv.remove(userSpan);

					this.remainingUsers.add(user);
					this.userComboBox.setItems(filter, this.remainingUsers);
				});
			} else {
				this.removedVoters.remove(user);

				userSpan.addClickListener(clickEvent -> {
					this.removedVoters.add(user);
					this.invitedUsersDiv.remove(userSpan);

					this.remainingUsers.add(user);
					this.userComboBox.setItems(filter, this.remainingUsers);
				});
			}

			this.remainingUsers.remove(user);
			this.userComboBox.setItems(this.filter, this.remainingUsers);

			this.invitedUsersDiv.add(userSpan); // add span to div
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
			synchronized (adrService) {
				ADR adr = this.adrService.findById(this.currentADR.getId()).orElse(null);

				if (adr == null) {
					this.close();
					new ErrorNotification(ERROR_NOTIFICATION_ADR_NOT_AVAILABLE).open();
					return;
				}

				this.addedVoters.forEach(adr.getStatus().adrReviewAsOpt().get()::addVoter);

				this.removedVoters.forEach(user -> {
					final ADRReview review = adr.getStatus().adrReviewAsOpt().get();
					if (review.removeVoter(user) != 1) {
						if (review.removeVoter(user) == 0)
							new ErrorNotification(INVITEVOTERDIALOG_VOTER_ALREADY_VOTED.replaceAll("\\[UserName]", user.getUserName())).open();
						else new ErrorNotification(INVITEVOTERDIALOG_VOTER_ALREADY_REMOVED.replaceAll("\\[UserName]", user.getUserName())).open();
					}
				});

				this.adrService.save(adr);
			}

			this.close();
			new SuccessNotification(INVITEVOTERDIALOG_VOTE_INVITE_SUCCESSFUL).open();
		});
	}

	private Span createUserSpan(@NonNull User user) {
		Span userSpan = new Span(user.getUserName());
		userSpan.add(new Span(" "));
		userSpan.add(new Icon("trash"));
		userSpan.getElement().getThemeList().add("badge success");
		userSpan.addClassName("invite-voter-dialog-span-class");
		return userSpan;
	}

}
