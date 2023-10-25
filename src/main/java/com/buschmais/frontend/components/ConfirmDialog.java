package com.buschmais.frontend.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport(value = "./themes/adr-workbench/vaadin-components/confirm-dialog.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
public class ConfirmDialog extends Dialog {

	private Label header;
	private Label message;

	private Button confirmButton;
	private Button cancelButton;

	private final VerticalLayout mainLayout;

	private final VerticalLayout headerContentLayout;
	private final HorizontalLayout buttonLayout;

	public ConfirmDialog() {
		super();
		this.setModal(true);
		this.setResizable(false);
		this.setSizeUndefined();
		this.setCloseOnOutsideClick(false);

		this.mainLayout = new VerticalLayout();
		this.headerContentLayout = new VerticalLayout();
		this.buttonLayout = new HorizontalLayout();

		this.mainLayout.addClassName("main-layout");
		this.headerContentLayout.addClassName("header-content-layout");
		this.buttonLayout.addClassName("button-layout");

		this.add(this.mainLayout);
	}

	public ConfirmDialog(String header) {
		this();
		this.header = new Label(header);
		this.header.addClassName("header");
		this.headerContentLayout.add(this.header);
		this.mainLayout.add(this.headerContentLayout);
	}

	public ConfirmDialog(String header, String message) {
		this(header);
		this.message = new Label(message);
		this.message.addClassName("content-text");
		this.headerContentLayout.add(this.message);
	}

	public ConfirmDialog(String header, String message, String confirmText, ComponentEventListener<ClickEvent<Button>> confirmEvent) {
		this(header, message);
		this.confirmButton = new Button(confirmText);
		this.confirmButton.addClickListener(confirmEvent);
		this.confirmButton.addClassName("confirm-button");
		this.buttonLayout.add(this.confirmButton);
		this.mainLayout.add(this.buttonLayout);
	}

	public ConfirmDialog(String header, String message, String confirmText, ComponentEventListener<ClickEvent<Button>> confirmEvent, String cancelText, ComponentEventListener<ClickEvent<Button>> cancelEvent){
		this(header, message, confirmText, confirmEvent);
		this.cancelButton = new Button(cancelText);
		this.cancelButton.addClickListener(cancelEvent);
		this.cancelButton.addClassName("cancel-button");
		this.buttonLayout.removeAll();
		this.buttonLayout.add(this.confirmButton);
		this.buttonLayout.add(this.cancelButton);
	}


}
