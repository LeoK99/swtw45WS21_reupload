package com.buschmais.frontend.components;

import com.vaadin.flow.component.notification.Notification;

public class SuccessNotification extends Notification {

	public SuccessNotification(){
		super();
		setProperties();
	}

	public SuccessNotification(String message){
		super(message);
		setProperties();
	}

	private void setProperties(){
		this.addThemeName("success");
		this.setDuration(5000);
		this.setPosition(Position.TOP_CENTER);
	}
}
