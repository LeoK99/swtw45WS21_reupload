package com.buschmais.backend.adrAccess;

import com.buschmais.backend.users.User;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
@Data
public class AccessGroup implements Comparable<AccessGroup> {

	@Setter(AccessLevel.PRIVATE)
	@EqualsAndHashCode.Exclude
	@Id
	private String id;

	@Indexed
	@NonNull
	@Unique
	private String name;

	@Setter(AccessLevel.PRIVATE)
	@NonNull
	@DBRef
	private Set<User> users;

	@Setter(AccessLevel.PRIVATE)
	@NonNull
	private AccessRights rights;

	@PersistenceConstructor
	AccessGroup(@NonNull String name) {
		this.name = name;
		this.users = new HashSet<>();
		this.rights = new AccessRights();
	}

	public AccessGroup(@NonNull String name, @NonNull AccessRights rights) {
		this.name = name;
		this.users = new HashSet<>();
		this.rights = rights;
	}

	public AccessGroup(@NonNull String name, @NonNull AccessRights rights, @NonNull Collection<User> users) {
		this.name = name;
		this.users = new HashSet<>();
		this.users.addAll(users);
		this.rights = rights;
	}

	/**
	 * Checks whether the User is contained in the AccessGroup or not
	 * @param user the User for whom the group should be checked
	 * @return true if the user is contained
	 */
	public boolean containsUser(User user) {
		return this.users.contains(user);
	}

	/**
	 * Add User to AccessGroup
	 * @param user the User to be added
	 * @return true if eligible users changed
	 */
	public boolean addUser(User user) {
		return this.users.add(user);
	}

	/**
	 * Add all Users contained in User Collection
	 * @param users Collection of new users
	 * @return true if eligible users changed
	 */
	public boolean addUsers(Collection<User> users) {
		return this.users.addAll(users);
	}

	/**
	 * Remove User from AccessGroup
	 * @param user the User to be removed
	 * @return true if eligible users changed
	 */
	public boolean removeUser(User user) {
		return this.users.remove(user);
	}

	/**
	 * Remove all Users contained in User Collection
	 * @param users Collection of users to be removed
	 * @return true if eligible users changed
	 */
	public boolean removeUsers(Collection<User> users) {
		return this.users.removeAll(users);
	}

	/**
	 * Returns whether the user has at least the given rights
	 * @param user the user which should be examined
	 * @param rights the rights the user should have at least
	 * @return true if the specific user has at least all the rights that are given by the AccessRights argument
	 */
	public boolean hasAccessRights(User user, AccessRights rights) {
		return this.users.contains(user) && this.rights.hasAtLeast(rights);
	}

	@Override
	public int compareTo(@NonNull AccessGroup g) {
		return this.getName().compareTo(g.getName());
	}
}
