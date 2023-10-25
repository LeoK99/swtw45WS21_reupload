package com.buschmais.frontend.views.welcome;

import com.buschmais.backend.users.dataAccess.UserDao;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;


@PageTitle("Willkommen")
//@Route(value = StringConstantsFrontend.LANDING_PAGE_PATH, layout = MainLayout.class)
//@RouteAlias(value = "", layout = MainLayout.class)
@CssImport(value = "./themes/adr-workbench/welcome/welcome.css")
@Tag("hello-world-view")
//@WebServlet(value = "/*", asyncSupported = true)
public class WelcomeView extends VerticalLayout {

	private UserDao userDao;

	private Label welcomeLabel;
	private Label motivationLabel;
	private Label joinLabel;
	private Label loginLabel;

	private Html motivationSpan;
	private Html loginSpan;
	private Html registerSpan;
	private Html blankLineP;

	private Div welcomeDiv;
	private Div motivationDiv;
	private Div joinDiv;

	private Button loginButton;

	public WelcomeView(@Autowired UserDao userDao) {
		this.userDao = userDao;

		setupComponents();
		addComponentsToLayouts();
	}

	private void setupComponents() {

		// define
		{
			this.welcomeLabel = new Label("Willkommen bei ADR-Workbench");
			this.motivationLabel = new Label("Motivation");
			this.joinLabel = new Label("Projektbeteiligung");
			this.loginLabel = new Label("Login");

			this.motivationSpan = new Html("<p> Die Dokumentation während der Entwicklung eines Softwareprojekte ist kein besonders beliebtes Thema. " +
					"Dennoch ist Dokumentation das nahezu wichtigste, um den Überblick über ein Projekt zu behalten und um dieses schlussendlich erfolgreich fertigstellen zu können." +
					"Um diese Arbeit zu erleichtern wird das Tool ADR-Workbench entworfen. " +
					"Hiermit soll es möglich sein, sogenannte Architecture Decision Records (kurz ADRs) kollaborativ zu entwerfen. " +
					"Ein ADR stellt dabei eine beliebige Architekturentscheidung (z.B. es wird nur noch Logger X genutzt) dar, die von allen Mitarbeitern akzeptiert, kommentiert und abgelehnt werden kann." +
					"So ist es möglich die Entscheidungen einheitlich zu speichern und immer geordnet abrufen zu können. " +
					"Die grafische Oberfläche (Web-Applikation) soll dabei eine einfache Bedienbarkeit bereitstellen, um ein möglichst effizientes Arbeiten zu ermöglichen.</p>");

			this.loginSpan = new Html("<p>Du bist bereits am Projekt beteiligt? Dann kommst du hier zum Login: </p>");
			this.registerSpan = new Html("<p>Du hast noch keine Zugangsdaten? Kein Problem, frag einfach deinen Projektleiter nach einem Zugang!</p>");
			this.blankLineP = new Html("<p></p>");


			this.welcomeDiv = new Div();
			this.motivationDiv = new Div();
			this.joinDiv = new Div();

			this.loginButton = new Button("Login");
		}

		// set properties
		{

			this.welcomeDiv.addClassName("welcome-div");
			this.motivationDiv.addClassName("motivation-div");
			this.joinDiv.addClassName("join-div");

			this.welcomeLabel.addClassName("heading");
			this.motivationLabel.addClassName("sub-heading");
			this.joinLabel.addClassName("sub-heading");

			this.loginButton.addClassName("login-button");
			this.registerSpan.getElement().getStyle().set("padding-top", "20px");
		}

		// listener
		addButtonListener();

	}

	private void addComponents() {
		this.welcomeDiv.add(this.welcomeLabel);

		this.motivationDiv.add(this.motivationLabel);
		this.motivationDiv.add(this.motivationSpan);

		if(userDao.getCurrentUser() == null) {
			this.joinDiv.add(this.joinLabel);
			this.joinDiv.add(this.loginSpan);
			this.joinDiv.add(this.loginButton);
			this.joinDiv.add(this.blankLineP);
			this.joinDiv.add(this.registerSpan);
		}
	}

	private void addComponentsToLayouts() {
		addComponents();

		add(this.welcomeDiv);
		add(this.motivationDiv);
		add(this.joinDiv);
	}

	private void addButtonListener(){
		this.loginButton.addClickListener(event -> {
			if(this.getUI().isPresent()) this.getUI().get().navigate("login");
		});
	}
}
