package com.buschmais.frontend.views.usercontrol;

import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.frontend.components.ErrorNotification;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.NonNull;

import static com.buschmais.frontend.vars.StringConstantsFrontend.USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE;
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
public class UserManageView extends VerticalLayout {

	private final UserDao userDao;

	private User user;

	private Span canmanageuser;
	private Span canmanagevotes;
	private Span canseeall;
	private Span canmanageaccessrights;
	private Span nocanmanageuser;
	private Span nocanmanagevotes;
	private Span nocanseeall;
	private Span nocanmanageaccessrights;

	private Button tocanmanageusers;
	private Button tocanmanagevotes;
	private Button tocanseeall;
	private Button tocanmanageaccessrights;
	private Button tocannotmanageusers;
	private Button tocannotmanagevotes;
	private Button tocannotseeall;
	private Button tocannotmanageaccessrights;

	VerticalLayout rightslayout;
	HorizontalLayout users1;
	HorizontalLayout votes1;
	HorizontalLayout adrs1;
	HorizontalLayout accessrights1;
	HorizontalLayout usernamelayout;

	public UserManageView(@NonNull User userInit, @NonNull UserDao userDao) {

		this.userDao = userDao;

		userDao.findById(userInit.getId()).ifPresentOrElse(userLoc -> this.user = userLoc, () -> new ErrorNotification(USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE).open());

		canmanageuser = new Span(StringConstantsFrontend.GROUP_CANMANAGEUSER);
		canmanagevotes = new Span(StringConstantsFrontend.GROUP_CANMANAGEVOTES);
		canseeall = new Span(StringConstantsFrontend.GROUP_CANSEEALL);
		canmanageaccessrights = new Span(StringConstantsFrontend.GROUP_CANMANAGEACCESSRIGHT);
		nocanmanageuser = new Span(StringConstantsFrontend.GROUP_CANMANAGEUSER);
		nocanmanagevotes = new Span(StringConstantsFrontend.GROUP_CANMANAGEVOTES);
		nocanseeall = new Span(StringConstantsFrontend.GROUP_CANSEEALL);
		nocanmanageaccessrights = new Span(StringConstantsFrontend.GROUP_CANMANAGEACCESSRIGHT);

		canmanageuser.addClassName("confirm-button");
		canmanagevotes.addClassName("confirm-button");
		canseeall.addClassName("confirm-button");
		canmanageaccessrights.addClassName("confirm-button");
		nocanmanageuser.addClassName("cancel-button");
		nocanmanagevotes.addClassName("cancel-button");
		nocanseeall.addClassName("cancel-button");
		nocanmanageaccessrights.addClassName("cancel-button");

		canmanageuser.getElement().getThemeList().add("badge");
		canmanagevotes.getElement().getThemeList().add("badge");
		canseeall.getElement().getThemeList().add("badge");
		canmanageaccessrights.getElement().getThemeList().add("badge");
		nocanmanageuser.getElement().getThemeList().add("badge");
		nocanmanagevotes.getElement().getThemeList().add("badge");
		nocanseeall.getElement().getThemeList().add("badge");
		nocanmanageaccessrights.getElement().getThemeList().add("badge");

		tocanmanageusers = new Button(StringConstantsFrontend.GROUP_CAN);
		tocannotmanageusers = new Button(StringConstantsFrontend.GROUP_CANNOT);
		tocanmanagevotes = new Button(StringConstantsFrontend.GROUP_CAN);
		tocannotmanagevotes = new Button(StringConstantsFrontend.GROUP_CANNOT);
		tocanseeall = new Button(StringConstantsFrontend.GROUP_CAN);
		tocannotseeall = new Button(StringConstantsFrontend.GROUP_CANNOT);
		tocanmanageaccessrights = new Button(StringConstantsFrontend.GROUP_CAN);
		tocannotmanageaccessrights = new Button(StringConstantsFrontend.GROUP_CANNOT);

		tocanmanageusers.addClassName("confirm-button");
		tocanmanagevotes.addClassName("confirm-button");
		tocanseeall.addClassName("confirm-button");
		tocanmanageaccessrights.addClassName("confirm-button");
		tocannotmanageusers.addClassName("cancel-button");
		tocannotmanagevotes.addClassName("cancel-button");
		tocannotseeall.addClassName("cancel-button");
		tocannotmanageaccessrights.addClassName("cancel-button");

		tocanmanageusers.addThemeVariants(ButtonVariant.LUMO_SMALL);
		tocannotmanageusers.addThemeVariants(ButtonVariant.LUMO_SMALL);
		tocanmanagevotes.addThemeVariants(ButtonVariant.LUMO_SMALL);
		tocannotmanagevotes.addThemeVariants(ButtonVariant.LUMO_SMALL);
		tocanseeall.addThemeVariants(ButtonVariant.LUMO_SMALL);
		tocannotseeall.addThemeVariants(ButtonVariant.LUMO_SMALL);
		tocanmanageaccessrights.addThemeVariants(ButtonVariant.LUMO_SMALL);
		tocannotmanageaccessrights.addThemeVariants(ButtonVariant.LUMO_SMALL);

		tocanmanageusers.addClickListener(event -> {
			synchronized (userDao) {
				userDao.findById(user.getId()).ifPresentOrElse(userLoc -> {
					user = userLoc;
					user.getRights().setCanManageUsers(true);
					userDao.save(user);
				}, () -> new ErrorNotification(USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE).open());
			}
			setupComponents();
		});
		tocannotmanageusers.addClickListener(event -> {
			synchronized (userDao) {
				userDao.findById(user.getId()).ifPresentOrElse(userLoc -> {
					this.user = userLoc;
					this.user.getRights().setCanManageUsers(false);
					userDao.save(user);
				}, () -> new ErrorNotification(USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE).open());
			}
			setupComponents();
		});
		tocanmanagevotes.addClickListener(event -> {
			synchronized (userDao) {
				userDao.findById(this.user.getId()).ifPresentOrElse(userLoc -> {
					this.user = userLoc;
					this.user.getRights().setCanManageVoting(true);
					userDao.save(this.user);
				}, () -> new ErrorNotification(USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE).open());
			}
			setupComponents();
		});
		tocannotmanagevotes.addClickListener(event -> {
			synchronized (userDao) {
				userDao.findById(user.getId()).ifPresentOrElse(userLoc -> {
					this.user = userLoc;
					user.getRights().setCanManageVoting(false);
					userDao.save(user);
				}, () -> new ErrorNotification(USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE).open());
			}
			setupComponents();
		});
		tocanseeall.addClickListener(event -> {
			synchronized (userDao) {
				userDao.findById(user.getId()).ifPresentOrElse(userLoc -> {
					this.user = userLoc;
					user.getRights().setCanSeeAllAdrs(true);
					userDao.save(user);
				}, () -> new ErrorNotification(USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE).open());
			}
			setupComponents();
		});
		tocannotseeall.addClickListener(event -> {
			synchronized (userDao) {
				userDao.findById(user.getId()).ifPresentOrElse(userLoc -> {
					this.user = userLoc;
					user.getRights().setCanSeeAllAdrs(false);
					userDao.save(user);
				}, () -> new ErrorNotification(USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE).open());
			}
			setupComponents();
		});
		tocanmanageaccessrights.addClickListener(event -> {
			synchronized (userDao) {
				userDao.findById(user.getId()).ifPresentOrElse(userLoc -> {
					this.user = userLoc;
					user.getRights().setCanManageAdrAccess(true);
					userDao.save(user);
				}, () -> new ErrorNotification(USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE).open());
			}
			setupComponents();
		});
		tocannotmanageaccessrights.addClickListener(event -> {
			synchronized (userDao) {
				userDao.findById(user.getId()).ifPresentOrElse(userLoc -> {
					this.user = userLoc;
					user.getRights().setCanManageAdrAccess(false);
					userDao.save(user);
				}, () -> new ErrorNotification(USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE).open());
			}
			setupComponents();
		});

		setupComponents();
	}

	private void setupComponents() {

		this.removeAll();

		rightslayout = new VerticalLayout();
		users1 = new HorizontalLayout();
		votes1 = new HorizontalLayout();
		adrs1 = new HorizontalLayout();
		accessrights1 = new HorizontalLayout();

		if (user.canManageUsers()) {
			users1.add(canmanageuser);
			users1.add(tocannotmanageusers);
			rightslayout.add(users1);
		} else {
			users1.add(nocanmanageuser);
			users1.add(tocanmanageusers);
			rightslayout.add(users1);
		}
		if (user.canManageVotes()) {
			votes1.add(canmanagevotes);
			votes1.add(tocannotmanagevotes);
			rightslayout.add(votes1);
		} else {
			votes1.add(nocanmanagevotes);
			votes1.add(tocanmanagevotes);
			rightslayout.add(votes1);
		}
		if (user.canSeeAllAdrs()) {
			adrs1.add(canseeall);
			adrs1.add(tocannotseeall);
			rightslayout.add(adrs1);
		} else {
			adrs1.add(nocanseeall);
			adrs1.add(tocanseeall);
			rightslayout.add(adrs1);
		}
		if (user.canManageAdrAccess()) {
			accessrights1.add(canmanageaccessrights);
			accessrights1.add(tocannotmanageaccessrights);
			rightslayout.add(accessrights1);
		} else {
			accessrights1.add(nocanmanageaccessrights);
			accessrights1.add(tocanmanageaccessrights);
			rightslayout.add(accessrights1);
		}
		this.add(rightslayout);
	}

/*	@Override
	public void receiveBroadcast(BroadcastListener.Event event, String message) {
		switch (event){
			case GROUP_DELETED -> {
				groupsLayoutConfigure();
			}
			case ADR_CHANGED -> updateDescribingLabels();
			case ADR_REVIEW_STS_CHANGED -> {
				if (adrId != null && adrId.equals(message)) {
					getUI().ifPresent(ui -> ui.access(this::setVariableVotingSectionElements));
				}
			}
		}
	}*/
}
