package com.buschmais.frontend.components;

import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.NonNull;

@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/create-user-dialog.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/vaadin-text-field-styles.css")
public class CreateUserDialogComponent extends Dialog {

	private final UserDao userService;

	private User createdUser;

	private VerticalLayout mainLayout;

	private VerticalLayout titleLayout;
	private VerticalLayout inputUserLayout;
	private HorizontalLayout buttonLayout;

	private Label titleLabel;
	private Label descriptionLabel;

	private TextField userNameTextField;
	private TextField passwordField;

	private Button createUserButton;
	private Button cancelButton;

	public CreateUserDialogComponent(@NonNull UserDao userService){
		super();

		this.userService = userService;

		init();

		setupComponents();
		addComponents();
	}

	private void init(){
		this.setModal(true);
		this.setResizable(false);
		this.setSizeUndefined();
		this.setCloseOnOutsideClick(false);
	}

	private void setupComponents(){

		{
			this.mainLayout = new VerticalLayout();
			this.titleLayout = new VerticalLayout();
			this.inputUserLayout = new VerticalLayout();
			this.buttonLayout = new HorizontalLayout();

			this.titleLabel = new Label(StringConstantsFrontend.CREATEUSERDIALOG_LABEL_TITLE);
			this.descriptionLabel = new Label(StringConstantsFrontend.CREATEUSERDIALOG_LABEL_DESCRIPTION);

			this.userNameTextField = new TextField(StringConstantsFrontend.GENERAL_USERNAME_TEXT);
			this.passwordField = new TextField(StringConstantsFrontend.GENERAL_PASSWORD_TEXT);

			this.createUserButton = new Button(StringConstantsFrontend.CREATEUSERDIALOG_BUTTON_CONFIRM);
			this.cancelButton = new Button(StringConstantsFrontend.GENERAL_DIALOG_BUTTON_CANCEL);
		}

		{
			this.mainLayout.addClassName("create-user-dialog-main-layout");

			this.titleLayout.addClassName("create-user-dialog-title-layout");
			this.inputUserLayout.addClassName("create-user-dialog-input-user-dialog");
			this.buttonLayout.addClassName("create-user-dialog-button-layout");

			this.titleLabel.addClassName("create-user-dialog-label-title");

			this.createUserButton.addClassName("confirm-button");
			this.cancelButton.addClassName("cancel-button");
		}

		{
			addCreateUserButtonListener();
			addCancelButtonListener();
		}

	}

	private void addComponents(){

		this.mainLayout.add(this.titleLabel);

		this.titleLayout.add(this.descriptionLabel);

		this.inputUserLayout.add(this.userNameTextField, this.passwordField);

		this.buttonLayout.add(this.createUserButton, this.cancelButton);

		this.mainLayout.add(this.titleLayout, this.inputUserLayout, this.buttonLayout);

		this.add(this.mainLayout);
	}

	private void addCancelButtonListener(){
			this.cancelButton.addClickListener(event -> {
				this.close();
			});
	}

	private void addCreateUserButtonListener(){
		this.createUserButton.addClickListener(event -> {
			synchronized (this.userService){
				this.createdUser = new User(this.userNameTextField.getValue(), this.passwordField.getValue());
				if(this.userService.save(this.createdUser) != null){
					this.close();
					new SuccessNotification(StringConstantsFrontend.CREATEUSERDIALOG_NOTIFICATION_SUCCESS.replaceAll("\\[UserName]", this.createdUser.getUserName())).open();
				}else{
					new ErrorNotification(StringConstantsFrontend.CREATEUSERDIALOG_NOTIFICATION_ERROR.replaceAll("\\[UserName]", this.createdUser.getUserName())).open();
				}
			}
		});
	}
}
