package com.buschmais.frontend.components;

import com.vaadin.flow.component.notification.Notification;

public class ErrorNotification extends Notification {

	public ErrorNotification(){
		super();
		setProperties();
	}

	public ErrorNotification(String message){
		super(message);
		setProperties();
	}

	private void setProperties(){
		this.addThemeName("error");
		this.setDuration(5000);
		this.setPosition(Position.TOP_CENTER);
	}
}
