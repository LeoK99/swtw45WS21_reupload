package com.buschmais.frontend.views.login;

import com.buschmais.backend.users.dataAccess.SecurityService;
import com.buschmais.frontend.components.ErrorNotification;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;


@PageTitle("Login")
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/vaadin-text-field-styles.css", themeFor = "vaadin-text-field vaadin-password-field")
//@Route(value = StringConstantsFrontend.LOGIN_PATH, layout = MainLayout.class)
public class LoginView extends VerticalLayout {

	private SecurityService secServ;

	private Button loginButton;

	private H2 loginHeading;

	private Label userNameLabel;
	private Label passwordLabel;

	private TextField userNameTextField; // Login with username, email could be provided later
	private PasswordField passwordField;

	private HorizontalLayout userNameLayout;
	private HorizontalLayout passwordLayout;

	public LoginView(@Autowired SecurityService secServ) {
		this.secServ = secServ;

		addComponents();
		this.userNameTextField.setAutofocus(true);
	}

	private void setupComponents() {

		/* Components */

		// Heading
		this.loginHeading = new H2(StringConstantsFrontend.LOGIN_TEXT);

		// Label
		this.userNameLabel = new Label(StringConstantsFrontend.GENERAL_USERNAME_TEXT);
		this.passwordLabel = new Label(StringConstantsFrontend.GENERAL_PASSWORD_TEXT);

		// TextField
		this.userNameTextField = new TextField(StringConstantsFrontend.GENERAL_USERNAME_TEXT);

		this.passwordField = new PasswordField(StringConstantsFrontend.GENERAL_PASSWORD_TEXT);

		// Horizontal Layouts
		this.userNameLayout = new HorizontalLayout();
		this.passwordLayout = new HorizontalLayout();

		// Button
		this.loginButton = new Button(StringConstantsFrontend.LOGIN_TEXT);
		this.loginButton.addClassName("confirm-button"); // change to Buschmais-Color

		/* Methods */

		// setup layouts
		setupLayouts();

		// ClickListener for buttons
		addClickListener();
		addShortcutClickListener();
	}

	private void addComponents() {
		setupComponents();

		// add(this.loginHeading);

		add(this.userNameLayout);
		add(this.passwordLayout);

		add(this.loginButton);
	}

	private void setupLayouts() {

		this.userNameLayout.add(this.userNameTextField);

		this.passwordLayout.add(this.passwordField);
	}

	private void addClickListener() {
		this.loginButton.addClickListener(event -> {
			if(secServ.authenticate(this.userNameTextField.getValue().trim(), this.passwordField.getValue().trim())) {
				UI.getCurrent().getPage().setLocation("overview");
			}else{
				new ErrorNotification(StringConstantsFrontend.LOGIN_ERROR_WRONG_PASSWORD).open(); // password does not fit
			}
		});
	}

	private void addShortcutClickListener() {
		this.loginButton.addClickShortcut(Key.ENTER);
	}
}
