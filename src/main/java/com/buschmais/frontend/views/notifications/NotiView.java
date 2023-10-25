package com.buschmais.frontend.views.notifications;

import com.buschmais.backend.notifications.Notification;
import com.buschmais.backend.notifications.NotificationType;
import com.buschmais.backend.notifications.VotingPendingNotification;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.backend.voting.ADRReview;
import com.buschmais.frontend.MainLayout;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.MethodReference;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class NotiView extends VerticalLayout {

	private final Grid<Notification> grid;

	private VotingPendingNotification Not;
	private final UserDao userDao;

	public NotiView(@NotNull @Autowired UserDao userDao){
		this.userDao = userDao;
		synchronized (userDao){
			User currentUser = userDao.getCurrentUser();
			grid =new Grid<>(Notification.class,true);

			List<Notification> nots = currentUser.getNotifications();


			Button debug = new Button("Debug add Notificaton");

			grid.setItems(currentUser.getNotifications());

			debug.addClickListener(event -> {
				Not = new VotingPendingNotification(new ADRReview(),LocalDateTime.now());
				currentUser.pushNotification(Not);
				grid.setItems(currentUser.getNotifications());
				UI.getCurrent().getPage().setLocation(StringConstantsFrontend.NOTI_PATH);

			});
			this.add(grid, debug);
		}

	}
}
