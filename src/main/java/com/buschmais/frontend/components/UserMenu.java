package com.buschmais.frontend.components;

import com.buschmais.backend.image.Image;
import com.buschmais.backend.image.ImageDao;
import com.buschmais.backend.notifications.Notification;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.frontend.broadcasting.BroadcastListener;
import com.buschmais.frontend.broadcasting.Broadcaster;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UIScope
@Component
public class UserMenu extends MenuBar  implements BroadcastListener {

	//Components
	private Avatar avatar;
	private Icon icon;
	private MenuItem avatarItem;
	private SubMenu subMenu;

	//Strings
	private String profilePictureSource;
	private String profilePictureAlt;
	private String name;

	//Bool
	private Notification newNot;
	private Notification oldNot;


	//Interfaces


	// other class instances
	private final UserDao userDao;
	private final ImageDao imgDao;

	private User currentUser;

	public UserMenu(@Autowired UserDao userDao, @Autowired ImageDao imgDao){

		this.imgDao = imgDao;
		this.userDao = userDao;
		if(userDao.getCurrentUser() != null) {
			this.currentUser = userDao.getCurrentUser();
			loadVariables();
			addComponents();
			this.setId("UserMenu");
			Broadcaster.registerListener(this);
			addDetachListener((DetachEvent e) -> Broadcaster.unregisterListener(this));

		}
	}

	private void addComponents() {

		this.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

		avatar = new Avatar(name);
		Image tempImg = this.currentUser.getProfilePicture();
		if(tempImg != null) {
			this.imgDao.getInputStream(tempImg).ifPresent(inputStream -> {
				avatar.setImageResource(new StreamResource("db-stream", () -> inputStream));
			});
		}


		icon = new Icon(VaadinIcon.BELL);
		icon.setColor("#65b32e");
		this.addItem(icon);
		icon.setVisible(false);

		avatarItem = this.addItem(avatar);
		avatarItem.addClickListener(event -> update());

		subMenu = avatarItem.getSubMenu();
		MenuItem noti = subMenu.addItem(StringConstantsFrontend.USERMENU_NOTI);
		noti.addClickListener(event -> UI.getCurrent().getPage().setLocation(StringConstantsFrontend.NOTI_PATH));
		MenuItem logout = subMenu.addItem(StringConstantsFrontend.USERMENU_LOGOUT);
		logout.addClickListener((event -> UI.getCurrent().getPage().setLocation(StringConstantsFrontend.LOGOUT_PATH)));

		/*Image profPic = this.currentUser.getProfilePicture();
		Optional<InputStream> imstream = imgDao.getInputStream(profPic);

		StreamResource streamResource = new StreamResource(imstream);
		avatar.setImageResource(imstream.orElse(null));

		avatar.setImage("BACKEND GIV PIC PLS");*/


	}

	private void loadVariables() {
		this.name = currentUser.getUserName();

	}

	public UserMenu update(){
		if(currentUser != null) {
			List<Notification> NotList = new ArrayList<>(currentUser.getNotifications());
			Notification Notification = null;
			if(NotList.size()>0){
				Notification = NotList.get(NotList.size()-1);
			}
			oldNot = newNot;
			newNot = Notification;
			//System.out.println("oldNot:");
			//System.out.println(oldNot);
			//System.out.println("newNot:");
			//System.out.println(newNot);
			getUI().ifPresent(ui -> ui.access(()->{
				icon.setVisible(oldNot != newNot);
			}));


		}
		return this;

	}

	public void setIndicator(boolean state){
		if(state)
			this.addItem(icon);
		else
			icon.getElement().removeFromParent();
	}


	@Override
	public void receiveBroadcast(Event event, String message) {
		//System.out.println("Broadcast Recived");
		//setIndicator(true);
		update();
	}
}
