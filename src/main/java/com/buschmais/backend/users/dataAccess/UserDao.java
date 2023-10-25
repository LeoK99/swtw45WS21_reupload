package com.buschmais.backend.users.dataAccess;

import com.buschmais.backend.image.ImageDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.UserRights;
import com.vaadin.flow.server.VaadinSession;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDao {

	private final UserRepository repo;
	private final MongoOperations mongoOperations;
	private final ImageDao imageDao;

	UserDao(@Autowired UserRepository repo,
			@Autowired MongoOperations mongoOperations,
			@Autowired ImageDao imageDao){
		this.repo = repo;
		this.mongoOperations = mongoOperations;
		this.imageDao = imageDao;
	}

	/**
	 * Gibt alle Nutzer zurück
	 * @return entsprechender Nutzer
	 */
	public List<User> findAll() {
		return repo.findAll();
	}

	public Optional<User> findById(String id) {
		if(id == null) {
			return Optional.empty();
		}

		return repo.findById(id);
	}

	/**
	 * Gibt einen Nutzer mit dem entsprechenden Nutzernamen zurück
	 * @param userName der Nutzername des Nutzers
	 * @return entsprechender Nutzer
	 */
	public Optional<User> findByUserName(@NonNull final String userName) throws UsernameNotFoundException {
		return repo.findByUserName(userName);
	}

	/**
	 * Gibt alle Nutzer zurück, die genau alle gegebenen Rechte haben.
	 * Es werden auf true und false gesetzte Werte beachtet.
	 * Es werden also keine Nutzer zurückgegeben, die mehr (oder weniger) als die angeforderten Rechte besitzen.
	 * @param rights UserRights Element zum Abgleich der Rechte
	 * @return entsprechende Nutzer
	 */
	public List<User> findAllByRights(@NonNull final UserRights rights) {
		return repo.findAllByRights(rights);
	}

	/**
	 * Gibt alle Nutzer zurück, die mindestens alle gegebenen Rechte haben.
	 * Es werden nur auf true gesetzte Werte beachtet.
	 * Es werden also auch alle Nutzer zurückgegeben, die mehr als die angeforderten Rechte besitzen.
	 * @param rights UserRights Element zum Abgleich der Rechte
	 * @return entsprechende Nutzer
	 */
	public List<User> findAllByHasRights(@NonNull final UserRights rights) {
		if(rights.getFlags() == 0)
			return repo.findAll();

		Query q = new Query();
		q.addCriteria(Criteria.where("rights.flags").bits().allSet(rights.getFlags()));
		return mongoOperations.find(q, User.class);
	}

	/**
	 * Speichert den angegebenen Nutzer in der Datenbank ab
	 * @param user zu speichernder Nutzer
	 * @return user which got saved; null, if a user with the username already exists
	 */
	public User save(@NonNull final User user) {
		if(this.findByUserName(user.getUserName()).isPresent() && this.findById(user.getId()).isEmpty()) {
			return null;
		}
		return repo.save(user);
	}

	/**
	 *
	 * @return currently logged in user; null, if user not logged in or user got deleted
	 */
	public User getCurrentUser(){
		try {
			return this.findById(VaadinSession.getCurrent().getAttribute(User.class).getId()).orElse(null);
		}
		catch(Exception e) {
			return null;
		}
	}

	public void setCurrentUser(@NonNull final User user){
		VaadinSession.getCurrent().setAttribute(User.class, user);
	}

	public void deleteAll() {
		repo.findAll().forEach(u -> {if(u.getProfilePicture() != null) imageDao.delete(u.getProfilePicture());});
		repo.deleteAll();
	}

	public void delete(@NonNull User user) {
		if(user.getProfilePicture() != null){
			imageDao.delete(user.getProfilePicture());
		}
		repo.delete(user);
	}
}