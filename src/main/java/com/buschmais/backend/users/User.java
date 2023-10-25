package com.buschmais.backend.users;

import com.buschmais.backend.image.Image;
import com.buschmais.backend.notifications.Notification;
import lombok.*;
import org.atmosphere.container.TomcatWebSocketUtil;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Document
@Data
public class User implements Comparable<User> {

	@Setter(AccessLevel.NONE)
	@EqualsAndHashCode.Exclude
	@Id
	private String id;

	@NonNull
	@Indexed
	@Unique
	private String userName;

	@NonNull
	@Indexed
	private Password password;

	private Image profilePicture;

	@NonNull
	@Indexed
	private UserRights rights;

	@NonNull
	@Setter(AccessLevel.PRIVATE)
	private List<Notification> notifications;

	@PersistenceConstructor
	private User(){ }

	public User(@NonNull final String username, @NonNull final String password) {
		this.userName = username;
		this.password = new Password(password);
		this.rights = new UserRights();
		this.notifications = new ArrayList<>();
	}

	public void changePassword(@NonNull final String newPassword) {
		this.password.changePassword(newPassword);
	}

	public boolean checkPassword(@NonNull final String password) {
		return this.password.checkPassword(password);
	}

	/**
	 * Gibt zurück, ob der Nutzer mindestens alle gegebenen Rechte besitzt.
	 * Es wird also auch true zurückgegeben, wenn der Nutzer mehr als die angeforderten Rechte besitzt.
	 * @param rights UserRights Element für zu überprüfende Rechte (entsprechendes Element kann über new UserRights(boolean, boolean, boolean) erstellt werden)
	 * @return boolean, ob der Nutzer mindestens die entsprechenden Rechte besitzt
	 */
	public boolean hasRights(@NonNull final UserRights rights){
		return this.rights.hasAtLeast(rights);
	}

	/**
	 * Returns whether the user has access to the UserControl Menu and can create/delete/modify Users
	 * @return true, if user has at least one of the following rights:
	 * <ul>
	 *     <li>can manage Users</li>
	 * </ul>
	 */
	public boolean canManageUsers() {
		return this.rights.hasAtLeast(new UserRights(true, false));
	}

	/**
	 * Returns whether the user can generally start/end all votings
	 * @return true, if user has at least one of the following rights:
	 * <ul>
	 *     <li>can manage Votings</li>
	 * </ul>
	 */
	public boolean canManageVotes() {
		return this.rights.hasAtLeast(new UserRights(false, true, false, false));
	}

	/**
	 * Returns whether the user can generally see all ADRs regardless of his AccessGroup
	 * @return true, if user has at least one of the following rights:
	 * <ul>
	 *     <li>can see all adrs</li>
	 * </ul>
	 */
	public boolean canSeeAllAdrs() {
		return this.rights.hasAtLeast(new UserRights(false, false, true, false));
	}

	/**
	 * Returns whether the user can generally edit the AccessGroups of all ADRs
	 * @return true, if user has at least one of the following rights:
	 * <ul>
	 *     <li>can manage adr access groups</li>
	 * </ul>
	 */
	public boolean canManageAdrAccess() {
		return this.rights.hasAtLeast(new UserRights(false, false, false, true));
	}

	public void pushNotification(@NonNull final Notification notification){
		notifications.add(notification);
	}

	@Override
	public String toString() {
		return "User{" +
				"id='" + id + '\'' +
				", userName='" + userName + '\'' +
				", password=" + password +
				", rights=" + rights +
				'}';
	}

	@Override
	public int compareTo(@NonNull User user) {
		return user.getUserName().compareTo(this.getUserName());
	}
}